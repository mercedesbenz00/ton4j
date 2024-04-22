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
public class ConfigParams31 {
    TonHashMapE fundamentalSmcAddr;

    public Cell toCell() {
        return CellBuilder.beginCell()
                .storeDict(fundamentalSmcAddr.serialize(
                        k -> CellBuilder.beginCell().storeUint((Long) k, 256).endCell().bits,
                        v -> CellBuilder.beginCell().storeBit((Boolean) v).endCell()
                ))
                .endCell();
    }

    public static ConfigParams31 deserialize(CellSlice cs) {
        return ConfigParams31.builder()
                .fundamentalSmcAddr((cs.loadDictE(256,
                        k -> k.readInt(256),
                        v -> CellSlice.beginParse(v).loadBit())))
                .build();
    }
}
