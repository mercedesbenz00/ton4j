package org.ton.java.tl.types;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.ton.java.cell.CellSlice;
import org.ton.java.utils.Utils;

import java.math.BigInteger;

@Builder
@Getter
@Setter
@ToString
/**
 * ton_api.tl
 * db.block.info#4ac6e727
 *  id:tonNode.blockIdExt
 *  flags:# prev_left:flags.1?tonNode.blockIdExt
 *  prev_right:flags.2?tonNode.blockIdExt
 *  next_left:flags.3?tonNode.blockIdExt
 *  next_right:flags.4?tonNode.blockIdExt
 *  lt:flags.13?long
 *  ts:flags.14?int
 *  state:flags.17?int256
 *  masterchain_ref_seqno:flags.23?int = db.block.Info;
 */
public class DbBlockInfo {

    public static final long DB_BLOCK_INFO_MAGIC = 0x4ac6e727;

    long magic;
    BlockIdExt id;
    BigInteger flags;
    BlockIdExt prevLeft;
    BlockIdExt prevRight;
    BlockIdExt nextLeft;
    BlockIdExt nextRight;
    BigInteger lt;
    BigInteger ts;
    int[] state;
    BigInteger masterChainRefSeqNo;

    private String getMagic() {
        return Long.toHexString(magic);
    }

    private String getState() {
        return Utils.bytesToHex(state);
    }

    public static DbBlockInfo deserialize(CellSlice cs) {
        int magic = Integer.reverseBytes(cs.loadUint(32).intValue());
        assert (magic == DB_BLOCK_INFO_MAGIC) : "DbBlockInfo: magic not equal to 0x4ac6e727, found " + Long.toHexString(magic);
        DbBlockInfo dbBlockInfo = DbBlockInfo.builder()
                .magic(DB_BLOCK_INFO_MAGIC)
                .id(BlockIdExt.deserialize(cs))
                .build();
        int f = Integer.reverseBytes(cs.loadUint(32).intValue());
        BigInteger flags = BigInteger.valueOf(f);

        // todo improve little endian reading
        dbBlockInfo.setFlags(flags);
        dbBlockInfo.setPrevLeft(flags.testBit(1) ? BlockIdExt.deserialize(cs) : null);
        dbBlockInfo.setPrevRight(flags.testBit(2) ? BlockIdExt.deserialize(cs) : null);
        dbBlockInfo.setNextLeft(flags.testBit(3) ? BlockIdExt.deserialize(cs) : null);
        dbBlockInfo.setNextRight(flags.testBit(4) ? BlockIdExt.deserialize(cs) : null);
        dbBlockInfo.setLt(flags.testBit(13) ? BigInteger.valueOf(Long.reverseBytes(cs.loadUint(64).longValue())) : null);
        dbBlockInfo.setTs(flags.testBit(14) ? BigInteger.valueOf(Integer.reverseBytes(cs.loadUint(32).intValue())) : null);
        dbBlockInfo.setState(flags.testBit(17) ? Utils.reverseIntArray(cs.loadBytes(256)) : null);
        dbBlockInfo.setMasterChainRefSeqNo(flags.testBit(23) ? BigInteger.valueOf(Integer.reverseBytes(cs.loadUint(32).intValue())) : null);
        return dbBlockInfo;
    }
}
