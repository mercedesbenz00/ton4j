package org.ton.java.tlb.types;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.ton.java.cell.Cell;
import org.ton.java.cell.CellBuilder;
import org.ton.java.cell.CellSlice;
import org.ton.java.cell.TonHashMapE;

/**
 * _ cregs:(HashmapE 4 VmStackValue) = VmSaveList;
 */
@Builder
@Getter
@Setter
@ToString
public class VmSaveList {
    TonHashMapE cregs;

    public Cell toCell() {
        return CellBuilder.beginCell()
                .storeDict(cregs.serialize(
                        k -> CellBuilder.beginCell().storeUint((Long) k, 4).endCell().getBits(),
                        v -> CellBuilder.beginCell().storeCell(((VmStackValue) v).toCell()).endCell()
                ))
                .endCell();
    }

    public static VmSaveList deserialize(CellSlice cs) {
        return VmSaveList.builder()
                .cregs(cs.loadDictE(
                        4,
                        k -> k.readUint(4),
                        v -> VmStackValue.deserialize(CellSlice.beginParse(v))))
                .build();
    }
}
