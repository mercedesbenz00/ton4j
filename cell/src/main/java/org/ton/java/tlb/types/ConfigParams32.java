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
public class ConfigParams32 {
    ValidatorSet prevValidatorSet;

    public Cell toCell() {
        return CellBuilder.beginCell()
                .storeCell(prevValidatorSet.toCell())
                .endCell();
    }

    public static ConfigParams32 deserialize(CellSlice cs) {
        return ConfigParams32.builder()
                .prevValidatorSet(ValidatorSet.deserialize(cs))
                .build();
    }
}
