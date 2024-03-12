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
public class ValidatorSignedTempKey {
    int magic;
    ValidatorTempKey key;
    CryptoSignature signature;

    public Cell toCell() {
        return CellBuilder.beginCell()
                .storeUint(0x4, 4)
                .storeRef(key.toCell())
                .storeCell(signature.toCell())
                .endCell();
    }

    public static ValidatorSignedTempKey deserialize(CellSlice cs) {
        return ValidatorSignedTempKey.builder()
                .magic(cs.loadUint(4).intValue())
                .key(ValidatorTempKey.deserialize(CellSlice.beginParse(cs.loadRef())))
                .signature(CryptoSignature.deserialize(cs))
                .build();
    }
}
