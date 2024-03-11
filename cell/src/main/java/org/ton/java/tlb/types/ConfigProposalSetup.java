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
public class ConfigProposalSetup {
    int cfgVoteCfg;
    int minTotRounds;
    int maxTotRounds;
    int minWins;
    int maxLosses;
    int minStoreSec;
    int maxStoreSec;
    int bitPrice;
    int cellPrice;

    public Cell toCell() {
        return CellBuilder.beginCell()
                .storeUint(0x36, 1)
                .storeUint(minTotRounds, 8)
                .storeUint(maxTotRounds, 8)
                .storeUint(minWins, 8)
                .storeUint(maxLosses, 8)
                .storeUint(minStoreSec, 32)
                .storeUint(maxStoreSec, 32)
                .storeUint(bitPrice, 32)
                .storeUint(cellPrice, 32)
                .endCell();
    }

    public static ConfigProposalSetup deserialize(CellSlice cs) {
        return ConfigProposalSetup.builder()
                .cfgVoteCfg(cs.loadUint(8).intValue())
                .minTotRounds(cs.loadUint(8).intValue())
                .maxTotRounds(cs.loadUint(8).intValue())
                .minWins(cs.loadUint(8).intValue())
                .maxLosses(cs.loadUint(8).intValue())
                .minStoreSec(cs.loadUint(32).intValue())
                .maxStoreSec(cs.loadUint(32).intValue())
                .bitPrice(cs.loadUint(32).intValue())
                .cellPrice(cs.loadUint(32).intValue())
                .build();
    }
}
