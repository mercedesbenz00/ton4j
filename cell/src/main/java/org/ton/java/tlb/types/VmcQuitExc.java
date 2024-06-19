package org.ton.java.tlb.types;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.ton.java.cell.Cell;
import org.ton.java.cell.CellBuilder;
import org.ton.java.cell.CellSlice;

/**
 * vmc_quit_exc$1001 = VmCont;
 */
@Builder
@Getter
@Setter
@ToString
public class VmcQuitExc implements VmCont {
    long magic;
    long exitCode;

    @Override
    public Cell toCell() {
        return CellBuilder.beginCell()
                .storeUint(0b1001, 4)
                .endCell();
    }

    public static VmcQuitExc deserialize(CellSlice cs) {
        return VmcQuitExc.builder()
                .magic(cs.loadUint(4).intValue())
                .build();
    }
}
