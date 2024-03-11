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
public class ConfigParams29 {
    ConsensusConfig consensusConfig;

    public Cell toCell() {
        return CellBuilder.beginCell()
                .storeCell(consensusConfig.toCell())
                .endCell();
    }

    public static ConfigParams29 deserialize(CellSlice cs) {
        return ConfigParams29.builder()
                .consensusConfig(ConsensusConfig.deserialize(cs))
                .build();
    }
}
