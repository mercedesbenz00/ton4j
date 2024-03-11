package org.ton.java.tlb.types;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.ton.java.cell.Cell;
import org.ton.java.cell.CellSlice;

@Builder
@Getter
@Setter
@ToString
public class ConfigParams11 {
    ConfigVotingSetup configVotingSetup;

    public Cell toCell() {

        return configVotingSetup.toCell();
    }

    public static ConfigParams11 deserialize(CellSlice cs) {
        return ConfigParams11.builder()
                .configVotingSetup(ConfigVotingSetup.deserialize(cs))
                .build();
    }
}
