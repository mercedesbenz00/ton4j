package org.ton.java.tlb.types;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.ton.java.cell.Cell;
import org.ton.java.cell.CellBuilder;
import org.ton.java.cell.CellSlice;

/**
 * vm_stk_cell#03 cell:^Cell = VmStackValue;
 */
@Builder
@Getter
@Setter
@ToString
public class VmStackValueCell implements VmStackValue {
    int magic;
    Cell cell;

    @Override
    public Cell toCell() {
        return CellBuilder.beginCell()
                .storeUint(0x03, 8)
                .storeRef(cell)
                .endCell();
    }

    public static VmStackValueCell deserialize(CellSlice cs) {
        return VmStackValueCell.builder()
                .magic(cs.loadUint(8).intValue())
                .cell(cs.loadRef())
                .build();
    }
}
