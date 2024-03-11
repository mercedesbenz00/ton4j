package org.ton.java.tlb.types;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.ton.java.cell.Cell;
import org.ton.java.cell.CellBuilder;
import org.ton.java.cell.CellSlice;

@Builder
@Getter
@Setter
@ToString
public class BlockLimits {
    int magic;
    ParamLimits bytes;
    ParamLimits gas;
    ParamLimits ltDelta;

    public Cell toCell() {
        return CellBuilder.beginCell()
                .storeUint(0x5d, 8)
                .storeCell(bytes.toCell())
                .storeCell(gas.toCell())
                .storeCell(ltDelta.toCell())
                .endCell();
    }

    public static BlockLimits deserialize(CellSlice cs) {
        return BlockLimits.builder()
                .magic(cs.loadUint(8).intValue())
                .bytes(ParamLimits.deserialize(cs))
                .gas(ParamLimits.deserialize(cs))
                .ltDelta(ParamLimits.deserialize(cs))
                .build();
    }
}
