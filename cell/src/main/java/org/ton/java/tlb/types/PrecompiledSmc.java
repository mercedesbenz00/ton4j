package org.ton.java.tlb.types;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.ton.java.cell.Cell;
import org.ton.java.cell.CellBuilder;
import org.ton.java.cell.CellSlice;

import java.math.BigInteger;

@Builder
@Getter
@Setter
@ToString
public class PrecompiledSmc {
    int magic;
    BigInteger gasUsage;

    public Cell toCell() {
        return CellBuilder.beginCell()
                .storeUint(0xb0, 8)
                .storeUint(gasUsage, 64)
                .endCell();
    }

    public static PrecompiledSmc deserialize(CellSlice cs) {
        return PrecompiledSmc.builder()
                .magic(cs.loadUint(8).intValue())
                .gasUsage(cs.loadUint(64))
                .build();
    }
}
