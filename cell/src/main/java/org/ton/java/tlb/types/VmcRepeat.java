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
 vmc_repeat$10100 count:uint63 body:^VmCont after:^VmCont = VmCont;
 */
@Builder
@Getter
@Setter
@ToString
public class VmcRepeat implements VmCont {
    long magic;
    BigInteger count;
    VmCont body;
    VmCont after;


    @Override
    public Cell toCell() {
        return CellBuilder.beginCell()
                .storeUint(0b10100, 5)
                .storeUint(count, 63)
                .storeRef(body.toCell())
                .storeRef(after.toCell())
                .endCell();
    }

    public static VmcRepeat deserialize(CellSlice cs) {
        return VmcRepeat.builder()
                .magic(cs.loadUint(5).intValue())
                .count(cs.loadUint(63))
                .body(VmCont.deserialize(CellSlice.beginParse(cs.loadRef())))
                .after(VmCont.deserialize(CellSlice.beginParse(cs.loadRef())))
                .build();
    }
}
