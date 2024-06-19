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
 * cp:(Maybe int16) = VmControlData;
 */
@Builder
@Getter
@Setter
@ToString
public class VmControlData {
    BigInteger nargs;
    VmStack stack;
    VmSaveList save;
    BigInteger cp;

    public Cell toCell() {
        return CellBuilder.beginCell()
                .storeUintMaybe(nargs, 13)
                .storeCellMaybe(stack.toCell())
                .storeCell(save.toCell())
                .storeIntMaybe(cp, 16)
                .endCell();
    }

    public static VmControlData deserialize(CellSlice cs) {
        return VmControlData.builder()
                .nargs(cs.loadUintMaybe(13))
                .stack(VmStack.deserialize(cs)) // maybe
                .save(VmSaveList.deserialize(cs))
                .cp(cs.loadIntMaybe(16))
                .build();
    }
}
