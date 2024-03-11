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
public class ConfigParams24 {
    MsgForwardPrices configMcFwdPrices;

    public Cell toCell() {
        return CellBuilder.beginCell()
                .storeCell(configMcFwdPrices.toCell())
                .endCell();
    }

    public static ConfigParams24 deserialize(CellSlice cs) {
        return ConfigParams24.builder()
                .configMcFwdPrices(MsgForwardPrices.deserialize(cs))
                .build();
    }
}
