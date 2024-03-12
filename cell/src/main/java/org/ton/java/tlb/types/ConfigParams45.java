package org.ton.java.tlb.types;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.ton.java.cell.Cell;
import org.ton.java.cell.CellBuilder;
import org.ton.java.cell.CellSlice;
import org.ton.java.cell.TonHashMapE;

@Builder
@Getter
@Setter
@ToString
public class ConfigParams45 {
    int magic;
    TonHashMapE precompiledContractsList;
    long suspendedUntil;

    public Cell toCell() {

        return CellBuilder.beginCell()
                .storeUint(0xc0, 8)
                .storeDict(precompiledContractsList.serialize(
                        k -> CellBuilder.beginCell().storeUint((Long) k, 256).bits,
                        v -> CellBuilder.beginCell().storeCell(((PrecompiledSmc) v).toCell())))
                .storeUint(suspendedUntil, 32)
                .endCell();
    }

    public static ConfigParams45 deserialize(CellSlice cs) {
        return ConfigParams45.builder()
                .magic(cs.loadUint(8).intValue())
                .precompiledContractsList(cs.loadDictE(256,
                        k -> k.readUint(256),
                        v -> PrecompiledSmc.deserialize(CellSlice.beginParse(v))))
                .build();
    }
}
