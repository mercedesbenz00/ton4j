package org.ton.java.tlb.types;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.ton.java.cell.*;

@Builder
@Getter
@Setter
@ToString
/**
 * masterchain_block_extra#cca5
 *   key_block:(## 1)
 *   shard_hashes:ShardHashes // _ (HashmapE 32 ^(BinTree ShardDescr)) = ShardHashes;
 *   shard_fees:ShardFees //     _ (HashmapAugE 96 ShardFeeCreated ShardFeeCreated) = ShardFees;
 *   ^[
 *     prev_blk_signatures:(HashmapE 16 CryptoSignaturePair)
 *     recover_create_msg:(Maybe ^InMsg)
 *     mint_msg:(Maybe ^InMsg)
 *    ]
 *   config:key_block?ConfigParams
 * = McBlockExtra;
 */
public class McBlockExtra {
    long magic;
    boolean keyBlock;
    //    ShardHashes shardHashes;
    TonHashMapE shardHashes;
    //    ShardFees shardFees;
    TonHashMapAugE shardFees;
    McBlockExtraInfo info;
    ConfigParams config;

    private String getMagic() {
        return Long.toHexString(magic);
    }

    public Cell toCell() {
        return CellBuilder.beginCell()
                .storeUint(0xcca5, 32)
                .storeBit(keyBlock)
//                .storeCell(shardHashes.toCell())
//                .storeCell(shardFees.toCell())
                .storeDict(shardHashes.serialize(
                        k -> CellBuilder.beginCell().storeUint((Long) k, 32).endCell().bits,
                        v -> CellBuilder.beginCell().storeRef((Cell) v).endCell() // todo ShardDescr
                ))
                .storeDict(shardFees.serialize(
                        k -> CellBuilder.beginCell().storeUint((Long) k, 96).endCell().bits,
                        v -> CellBuilder.beginCell().storeCell((Cell) v), // todo ShardFeeCreated
                        e -> CellBuilder.beginCell().storeCell((Cell) e), // todo ShardFeeCreated
                        (fk, fv) -> CellBuilder.beginCell().storeUint(0, 1) // todo
                ))
                .storeRef(info.toCell())
                .storeCell(keyBlock ? config.toCell() : CellBuilder.beginCell().endCell())
                .endCell();
    }

    public static McBlockExtra deserialize(CellSlice cs) {
        long magic = cs.loadUint(16).longValue();
        assert (magic == 0xcca5L) : "McBlockExtra: magic not equal to 0xcca5, found 0x" + Long.toHexString(magic);

        boolean keyBlock = cs.loadBit();
        McBlockExtra mcBlockExtra = McBlockExtra.builder()
                .magic(0xcca5L)
                .keyBlock(keyBlock)
//                .shardHashes(ShardHashes.deserialize(cs))
//                .shardFees(ShardFees.deserialize(cs))
                .shardHashes(cs.loadDictE(32,
                        k -> k.readInt(32),
                        v -> CellSlice.beginParse(v).loadRef())) // ref
                .shardFees(cs.loadDictAugE(92,
                        k -> k.readInt(92),
                        v -> v,
                        e -> e))
                .build();
        mcBlockExtra.setInfo(McBlockExtraInfo.deserialize(CellSlice.beginParse(cs.loadRef())));
        mcBlockExtra.setConfig(keyBlock ? ConfigParams.deserialize(cs) : null);
        return mcBlockExtra;
    }
}
