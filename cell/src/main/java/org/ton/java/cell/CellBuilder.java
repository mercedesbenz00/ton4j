package org.ton.java.cell;

import org.ton.java.address.Address;
import org.ton.java.bitstring.BitString;
import org.ton.java.utils.Utils;

import java.math.BigInteger;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class CellBuilder {

    Cell cell;

    private CellBuilder() {
        cell = new Cell();
//        super();
    }

    private CellBuilder(int bitSize) {
        cell = new Cell(bitSize);
    }

    public static CellBuilder beginCell() {
        return new CellBuilder();
    }

    /**
     * Create a cell with custom length of bit.
     */
    public static CellBuilder beginCell(int bitSize) {
        return new CellBuilder(bitSize);
    }

    /**
     * Converts a builder into an ordinary cell.
     */
    public Cell endCell() {
        cell.calculateHashes();
        cell.hash = Utils.bytesToHex(cell.getHash());
        return cell;
    }

    public CellBuilder storeBit(Boolean bit) {
        checkBitsOverflow(1);
        cell.bits.writeBit(bit);
        return this;
    }

    public CellBuilder storeBits(List<Boolean> arrayBits) {
        checkBitsOverflow(arrayBits.size());
        for (Boolean bit : arrayBits) {
            cell.bits.writeBit(bit);
        }
        return this;
    }

    public CellBuilder storeBits(Boolean[] arrayBits) {
        checkBitsOverflow(arrayBits.length);
        cell.bits.writeBitArray(arrayBits);
        return this;
    }

    public CellBuilder storeUint(long number, int bitLength) {
        return storeUint(BigInteger.valueOf(number), bitLength);
    }

    public CellBuilder storeUintMaybe(long number, int bitLength) {
        return storeUintMaybe(BigInteger.valueOf(number), bitLength);
    }

    public CellBuilder storeUint(int number, int bitLength) {
        return storeUint(BigInteger.valueOf(number), bitLength);
    }

    public CellBuilder storeUintMaybe(int number, int bitLength) {
        return storeUintMaybe(BigInteger.valueOf(number), bitLength);
    }

    public CellBuilder storeUint(short number, int bitLength) {
        return storeUint(BigInteger.valueOf(number), bitLength);
    }

    public CellBuilder storeUintMaybe(short number, int bitLength) {
        return storeUintMaybe(BigInteger.valueOf(number), bitLength);
    }

    public CellBuilder storeUint(Byte number, int bitLength) {
        return storeUint(BigInteger.valueOf(number), bitLength);
    }

    public CellBuilder storeUintMaybe(Byte number, int bitLength) {
        return storeUintMaybe(BigInteger.valueOf(number), bitLength);
    }

    public CellBuilder storeUint(String number, int bitLength) {
        return storeUint(new BigInteger(number), bitLength);
    }

    public CellBuilder storeUintMaybe(String number, int bitLength) {
        return storeUintMaybe(new BigInteger(number), bitLength);
    }

    public CellBuilder storeUint(BigInteger number, int bitLength) {
        checkBitsOverflow(bitLength);
        checkSign(number);
        cell.bits.writeUint(isNull(number) ? BigInteger.ZERO : number, bitLength);
        return this;
    }

    public CellBuilder storeUintMaybe(BigInteger number, int bitLength) {
        if (isNull(number)) {
            cell.bits.writeBit(false);
        } else {
            cell.bits.writeBit(true);
            checkBitsOverflow(bitLength);
            checkSign(number);
            cell.bits.writeUint(number, bitLength);
        }
        return this;
    }

    public CellBuilder storeVarUint(BigInteger number, int bitLength) {
        checkSign(number);
        cell.bits.writeVarUint(number, bitLength);
        return this;
    }

    public CellBuilder storeVarUint(Byte number, int bitLength) {
        checkSign(BigInteger.valueOf(number));
        cell.bits.writeVarUint(BigInteger.valueOf(number), bitLength);
        return this;
    }

    public CellBuilder storeVarUintMaybe(BigInteger number, int bitLength) {
        if (isNull(number)) {
            cell.bits.writeBit(false);
        } else {
            cell.bits.writeBit(true);
            checkSign(number);
            cell.bits.writeVarUint(number, bitLength);
        }
        return this;
    }

    public CellBuilder storeInt(long number, int bitLength) {
        return storeInt(BigInteger.valueOf(number), bitLength);
    }

    public CellBuilder storeIntMaybe(long number, int bitLength) {
        return storeIntMaybe(BigInteger.valueOf(number), bitLength);
    }

    public CellBuilder storeInt(int number, int bitLength) {
        return storeInt(BigInteger.valueOf(number), bitLength);
    }

    public CellBuilder storeIntMaybe(int number, int bitLength) {
        return storeIntMaybe(BigInteger.valueOf(number), bitLength);
    }

    public CellBuilder storeInt(short number, int bitLength) {
        return storeInt(BigInteger.valueOf(number), bitLength);
    }

    public CellBuilder storeIntMaybe(short number, int bitLength) {
        return storeIntMaybe(BigInteger.valueOf(number), bitLength);
    }

    public CellBuilder storeInt(byte number, int bitLength) {
        return storeInt(BigInteger.valueOf(number), bitLength);
    }

    public CellBuilder storeIntMaybe(byte number, int bitLength) {
        return storeIntMaybe(BigInteger.valueOf(number), bitLength);
    }

    public CellBuilder storeInt(BigInteger number, int bitLength) {
        BigInteger sint = BigInteger.ONE.shiftLeft(bitLength - 1);
        if ((number.compareTo(sint.negate()) >= 0) && (number.compareTo(sint) < 0)) {
            cell.bits.writeInt(number, bitLength);
            return this;
        } else {
            throw new Error("Can't store an Int, because its value allocates more space than provided.");
        }
    }

    public CellBuilder storeIntMaybe(BigInteger number, int bitLength) {
        if (isNull(number)) {
            cell.bits.writeBit(false);
        } else {
            cell.bits.writeBit(true);
            cell.bits.writeInt(number, bitLength);
        }
        return this;
    }

    public CellBuilder storeBitString(BitString bitString) {
        checkBitsOverflow(bitString.getUsedBits());
        cell.bits.writeBitString(bitString.clone());
        return this;
    }

    public CellBuilder storeBitStringUnsafe(BitString bitString) {
        cell.bits.writeBitString(bitString.clone());
        return this;
    }

    public CellBuilder storeString(String str) {
        checkBitsOverflow(str.length() * 8);
        cell.bits.writeString(str);
        return this;
    }

    public CellBuilder storeSnakeString(String str) {
        byte[] strBytes = str.getBytes();
        Cell c = f(127 - 4, strBytes);
        return this.storeSlice(CellSlice.beginParse(c));
    }

    private Cell f(int space, byte[] data) {
        if (data.length < space) {
            space = data.length;
        }
        BitString bs = new BitString(data, space * 8);
        CellBuilder c = CellBuilder.beginCell().storeBitString(bs);

        byte[] tmp = new byte[data.length - space];
        System.arraycopy(data, space, tmp, 0, data.length - space);

        if (tmp.length > 0) {
            Cell ref = f(127, tmp);
            c.storeRef(ref);
        }
        return c.endCell();
    }

    public CellBuilder storeAddress(Address address) {
        checkBitsOverflow(267);
        cell.bits.writeAddress(address);
        return this;
    }

    public CellBuilder storeBytes(byte[] number) {
        checkBitsOverflow(number.length * 8);
        cell.bits.writeBytes(number);
        return this;
    }

    public CellBuilder storeBytes(int[] number) {
        checkBitsOverflow(number.length * 8);
        cell.bits.writeBytes(number);
        return this;
    }

    public CellBuilder storeBytes(List<Byte> bytes) {
        checkBitsOverflow(bytes.size() * 8);
        for (Byte b : bytes) {
            cell.bits.writeUint8(b);
        }
        return this;
    }

    public CellBuilder storeList(List<BigInteger> bytes, int bitLength) {
        checkBitsOverflow(bitLength);
        for (BigInteger b : bytes) {
            cell.bits.writeUint(b, bitLength);
        }
        return this;
    }

    public CellBuilder storeBytes(byte[] number, int bitLength) {
        checkBitsOverflow(bitLength);
        cell.bits.writeBytes(number);
        return this;
    }

    public CellBuilder storeBytes(int[] number, int bitLength) {
        checkBitsOverflow(bitLength);
        cell.bits.writeBytes(number);
        return this;
    }

    public CellBuilder storeRef(Cell c) {
        checkRefsOverflow(1);
        cell.refs.add(c.clone());
        return this;
    }

    public CellBuilder storeRefMaybe(Cell c) {
        if (isNull(c)) {
            cell.bits.writeBit(false);
        } else {
            cell.bits.writeBit(true);
            checkRefsOverflow(1);
            cell.refs.add(c.clone());
        }
        return this;
    }

    public CellBuilder storeRefs(List<Cell> cells) {
        checkRefsOverflow(cells.size());
        for (Cell c : cells) {
            cell.refs.add(c.clone());
        }
        return this;
    }

    public CellBuilder storeRefs(Cell... cells) {
        checkRefsOverflow(cells.length);
        for (Cell c : cells) {
            cell.refs.add(c.clone());
        }
        return this;
    }

    public CellBuilder storeSlice(CellSlice cellSlice) {
        checkBitsOverflow(cellSlice.bits.getUsedBits());
        checkRefsOverflow(cellSlice.refs.size());

        storeBitString(cellSlice.bits);
        for (Cell c : cellSlice.refs) {
            cell.refs.add(c.clone());
        }
        return this;
    }
//    Cell cc = c.clone();
//    checkBitsOverflow(cc.bits.getUsedBits());
//    checkRefsOverflow(cc.refs.size());
//    storeBitString(cc.bits);
//    cell.refs.addAll(cc.refs);

    public CellBuilder storeCell(Cell c) {
        checkBitsOverflow(c.bits.getUsedBits());
        checkRefsOverflow(c.refs.size());

        storeBitString(c.bits);
        for (Cell cc : c.refs) {
            cell.refs.add(cc.clone());
        }
        //cell.depths.addAll(c.depths);
        //cell.hashes.addAll(c.hashes);
//        cell.calculateHashes();
        return this;
    }

    public CellBuilder storeCellMaybe(Cell c) {
        if (isNull(c)) {
            cell.bits.writeBit(false);
        } else {
            cell.bits.writeBit(true);
            storeCell(c.clone());
        }
        return this;
    }

    public CellBuilder storeDict(Cell dict) {
        storeSlice(CellSlice.beginParse(dict));
        return this;
    }

    /**
     * Stores up to 2^120-1 nano-coins in Cell
     *
     * @param coins amount in nano-coins
     * @return CellBuilder
     */
    public CellBuilder storeCoins(BigInteger coins) {
        cell.bits.writeCoins(isNull(coins) ? BigInteger.ZERO : coins);
        return this;
    }

    /**
     * Stores up to 2^120-1 nano-coins in Cell
     *
     * @param coins amount in nano-coins
     * @return CellBuilder
     */
    public CellBuilder storeCoinsMaybe(BigInteger coins) {
        if (isNull(coins)) {
            cell.bits.writeBit(false);
        } else {
            cell.bits.writeBit(true);
            cell.bits.writeCoins(coins);
        }
        return this;
    }

    public int getUsedBits() {
        return cell.bits.getUsedBits();
    }

    public int getFreeBits() {
        return cell.bits.getFreeBits();
    }

    public int getFreeRefs() {
        return cell.getFreeRefs();
    }

    void checkBitsOverflow(int length) {
        if (length > cell.bits.getFreeBits()) {
            throw new Error("Bits overflow. Can't add " + length + " cell.bits. " + cell.bits.getFreeBits() + " bits left.");
        }
    }

    void checkSign(BigInteger i) {
        if (nonNull(i) && (i.signum() < 0)) {
            throw new Error("Integer " + i + " must be unsigned");
        }
    }

    void checkRefsOverflow(int count) {
        if (count > (4 - cell.refs.size())) {
            throw new Error("Refs overflow. Can't add " + count + " cell.refs. " + (4 - cell.refs.size()) + " refs left.");
        }
    }

    public int[] toUnsignedByteArray() {
        return cell.bits.toUnsignedByteArray();
    }

    public byte[] toSignedByteArray() {
        return cell.bits.toSignedByteArray();
    }

    public CellBuilder fromBoc(String data) {
        cell = Cell.fromBocMultiRoot(Utils.hexToSignedBytes(data)).get(0);
        return this;
    }

    public CellBuilder fromBoc(byte[] data) {
        cell = Cell.fromBocMultiRoot(data).get(0);
        return this;
    }

    public CellBuilder fromBoc(int[] data) {
        cell = Cell.fromBocMultiRoot(Utils.unsignedBytesToSigned(data)).get(0);
        return this;
    }
}
