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
public class ConfigParams36 {
    ValidatorSet nextValidatorSet;

    public Cell toCell() {
        return CellBuilder.beginCell()
                .storeCell(nextValidatorSet.toCell())
                .endCell();
    }

    public static ConfigParams36 deserialize(CellSlice cs) {
        return ConfigParams36.builder()
                .nextValidatorSet(ValidatorSet.deserialize(cs))
                .build();
    }
}
