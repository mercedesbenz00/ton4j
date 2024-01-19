package org.ton.java.tlb.types;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.ton.java.cell.Cell;
import org.ton.java.cell.CellBuilder;
import org.ton.java.cell.CellSlice;

import java.math.BigInteger;

@Builder
@Getter
@Setter
@ToString
/**
 * import_fees$_
 * fees_collected:Grams
 * value_imported:CurrencyCollection = ImportFees;
 */

public class ImportFees {
    BigInteger feesCollected;
    CurrencyCollection valueImported;

    public Cell toCell() {
        return CellBuilder.beginCell()
                .storeCoins(feesCollected)
                .storeSlice(CellSlice.beginParse(valueImported.toCell()))
                .endCell();
    }
}