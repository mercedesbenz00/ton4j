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
public class ConfigParams25 {
    MsgForwardPrices configFwdPrices;

    public Cell toCell() {
        return CellBuilder.beginCell()
                .storeCell(configFwdPrices.toCell())
                .endCell();
    }

    public static ConfigParams25 deserialize(CellSlice cs) {
        return ConfigParams25.builder()
                .configFwdPrices(MsgForwardPrices.deserialize(cs))
                .build();
    }
}
