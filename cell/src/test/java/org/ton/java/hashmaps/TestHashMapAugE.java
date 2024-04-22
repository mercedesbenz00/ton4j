package org.ton.java.hashmaps;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.ton.java.cell.Cell;
import org.ton.java.cell.CellBuilder;
import org.ton.java.cell.CellSlice;
import org.ton.java.cell.TonHashMapAugE;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@RunWith(JUnit4.class)
public class TestHashMapAugE {

    @Test
    public void testHashMapAugeSerialization() {
        TonHashMapAugE hashmapAugE = new TonHashMapAugE(32);
        long forkExtra = 0;
        for (long i = 1; i <= 10; i++) {
            long value = i * 3;
            long extra = value * value;
            forkExtra += extra;
            hashmapAugE.elements.put(i, Pair.of(value, extra));
            log.info("forkextra {}", forkExtra);
        }
        log.info("HashmapAugE {}", hashmapAugE);


        Cell cell = hashmapAugE.serialize(
                k -> CellBuilder.beginCell().storeUint((Long) k, 32).endCell().bits,
                v -> CellBuilder.beginCell().storeUint((Long) v, 32).endCell(),
                e -> CellBuilder.beginCell().storeUint((Long) e, 32).endCell(),
                (fv, fe) -> CellBuilder.beginCell().storeUint(
                        CellSlice.beginParse((Cell) fv).loadUint(32).longValue() +
                                CellSlice.beginParse((Cell) fe).loadUint(32).longValue(), 32)
        );

        log.info("serialized cell {}", cell.print());

        CellSlice cs = CellSlice.beginParse(cell);
        TonHashMapAugE loadedDict = cs.loadDictAugE(32,
                k -> k.readUint(32),
                v -> CellSlice.beginParse(v).loadUint(32),
                e -> CellSlice.beginParse(e).loadUint(32)
        );

        log.info("Deserialized hashmap from cell {}", loadedDict);

        assertThat(loadedDict.elements.size()).isEqualTo(10);
    }

    // all hashmaps have structure: HashMapAugE 32 uint32 uint32
    // original BoCs located under resources folder, generated with cpp code
    @Test
    public void testHashMapAugeEmptyDeserialization() {

        Cell cell = CellBuilder.beginCell().fromBoc("b5ee9c720101010100070000090000000040").endCell();
        log.info("cell {}", cell.print());

        CellSlice cs = CellSlice.beginParse(cell);

        TonHashMapAugE loadedDict = cs.loadDictAugE(32,
                k -> k.readUint(32),
                v -> CellSlice.beginParse(v).loadUint(32),
                e -> CellSlice.beginParse(e).loadUint(32)
        );

        log.info("Deserialized hashmap from cell {}", loadedDict);
    }

    @Test
    public void testHashMapAugeOneEntriesDeserialization() {
        Cell cell = CellBuilder.beginCell().fromBoc("b5ee9c7201010201001700010980000004c001001aa0000000010000000900000003").endCell();
        log.info("cell {}", cell.print());

        CellSlice cs = CellSlice.beginParse(cell);

        TonHashMapAugE loadedDict = cs.loadDictAugE(32,
                k -> k.readUint(32),
                v -> CellSlice.beginParse(v).loadUint(32),
                e -> CellSlice.beginParse(e).loadUint(32)
        );

        log.info("Deserialized hashmap from cell {}", loadedDict);
    }

    @Test
    public void testHashMapAugeTwoEntriesDeserialization() {
        Cell cell = CellBuilder.beginCell().fromBoc("b5ee9c7201010401002800010980000016c001020bcf00000016c0020300115000000090000000380011400000024000000068").endCell();
        log.info("cell {}", cell.print());

        CellSlice cs = CellSlice.beginParse(cell);

        TonHashMapAugE loadedDict = cs.loadDictAugE(32,
                k -> k.readUint(32),
                v -> CellSlice.beginParse(v).loadUint(32),
                e -> CellSlice.beginParse(e).loadUint(32)
        );

        log.info("Deserialized hashmap from cell {}", loadedDict);
    }

    @Test
    public void testHashMapAugeTenEntriesDeserialization() {
        Cell cell = CellBuilder.beginCell().fromBoc("b5ee9c720101140100c8000109800006c4c001020bce000006c4c0020302090000013b200405020940000089d8101102090000001fa0060702090000011ba00a0b001150000000900000003802090000001d60080900110000000900000001a0001100000014400000026002090000005c600c0d0209000000bf600e0f001100000024000000032000110000003840000003e000110000005100000004a000110000006e400000056002090000014660121300114000003840000001e800110000009000000006200011000000b640000006e0").endCell();
        log.info("cell {}", cell.print());

        CellSlice cs = CellSlice.beginParse(cell);

        TonHashMapAugE loadedDict = cs.loadDictAugE(32,
                k -> k.readUint(32),
                v -> CellSlice.beginParse(v).loadUint(32),
                e -> CellSlice.beginParse(e).loadUint(32)
        );

        log.info("Deserialized hashmap from cell {}", loadedDict);
    }
}