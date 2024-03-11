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
public class ConfigVotingSetup {
    int cfgVoteSetup;
    ConfigProposalSetup normalParams;
    ConfigProposalSetup criticalParams;

    public Cell toCell() {
        return CellBuilder.beginCell()
                .storeUint(0x91, 1)
                .storeRef(normalParams.toCell())
                .storeRef(criticalParams.toCell())
                .endCell();
    }

    public static ConfigVotingSetup deserialize(CellSlice cs) {
        return ConfigVotingSetup.builder()
                .cfgVoteSetup(cs.loadUint(8).intValue())
                .normalParams(ConfigProposalSetup.deserialize(CellSlice.beginParse(cs.loadRef())))
                .criticalParams(ConfigProposalSetup.deserialize(CellSlice.beginParse(cs.loadRef())))
                .build();
    }
}
