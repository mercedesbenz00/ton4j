package org.ton.java.tlb.types;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.ton.java.cell.Cell;
import org.ton.java.cell.CellBuilder;
import org.ton.java.cell.CellSlice;

import java.math.BigInteger;

/**
 * vmc_envelope$01 cdata:VmControlData next:^VmCont = VmCont;
 */
@Builder
@Getter
@Setter
@ToString
public class VmcPushInt implements VmCont {
    long magic;
    BigInteger value;
    VmCont next;

    @Override
    public Cell toCell() {
        return CellBuilder.beginCell()
                .storeUint(0b1111, 4)
                .storeInt(value, 32)
                .storeRef(next.toCell())
                .endCell();
    }

    public static VmcPushInt deserialize(CellSlice cs) {
        return VmcPushInt.builder()
                .magic(cs.loadUint(2).intValue())
                .next(VmCont.deserialize(CellSlice.beginParse(cs.loadRef())))
                .build();
    }
}
