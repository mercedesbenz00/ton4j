package org.ton.java.bitstring;

import org.apache.commons.lang3.StringUtils;
import org.ton.java.address.Address;
import org.ton.java.utils.Utils;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static java.util.Objects.isNull;

public class BitString {

    Deque<Boolean> array;

    private static int MAX_LENGTH = 1023;

    private int initialLength;

    public BitString(BitString bs) {
        array = new ArrayDeque<>(bs.array.size());
        for (Boolean b : bs.array) {
            writeBit(b);
        }
        initialLength = bs.array.size() == 0 ? MAX_LENGTH : bs.array.size();
    }

    public BitString(byte[] bytes) {
        this(Utils.signedBytesToUnsigned(bytes));
    }

    public BitString(int[] bytes) {
        if (bytes.length == 0) {
            array = new ArrayDeque<>(0);
            initialLength = 0;
        } else {
            String bits = StringUtils.leftPad(Utils.bytesToBitString(bytes), bytes.length * 8, '0');

            array = new ArrayDeque<>(bits.length());
            for (int i = 0; i < bits.length(); i++) { // whole length
                if (bits.charAt(i) == '1') {
                    array.addLast(true);
                } else if (bits.charAt(i) == '0') {
                    array.addLast(false);
                } else {
                    // else '-' sign - do nothing
                }
            }
            initialLength = bits.length();
        }
    }

    public BitString(int[] bytes, int size) {
        if (bytes.length == 0) {
            array = new ArrayDeque<>(0);
            initialLength = 0;
        } else {
            String bits = StringUtils.leftPad(Utils.bytesToBitString(bytes), bytes.length * 8, '0');

            array = new ArrayDeque<>(bits.length());
            for (int i = 0; i < size; i++) { // specified length
                if (bits.charAt(i) == '1') {
                    array.addLast(true);
                } else if (bits.charAt(i) == '0') {
                    array.addLast(false);
                } else {
                    // else '-' sign - do nothing
                }
            }
            initialLength = bits.length();
        }
    }

    /**
     * Create BitString limited by length
     *
     * @param length int    length of BitString in bits
     */
    public BitString(int length) {
        array = new ArrayDeque<>(length);
        initialLength = length;
    }

    public BitString() {
        array = new ArrayDeque<>(MAX_LENGTH);
        initialLength = MAX_LENGTH;
    }

    /**
     * Return free bits, that derives from total length minus bits written
     *
     * @return int
     */
    public int getFreeBits() {
        return initialLength - array.size();
    }

    /**
     * Returns used bits, i.e. last position of writeCursor
     *
     * @return int
     */
    public int getUsedBits() {
        return array.size();
    }

    /**
     * @return int
     */
    public int getUsedBytes() {
        return (int) Math.ceil(array.size() / (double) 8);
    }

    /**
     * Gets current bit without removing it
     *
     * @return boolean    bit value at position `n`
     */
    public boolean get() {
        return array.peekFirst();
    }

    /**
     * Check if bit at position n is reachable
     *
     * @param n int
     */
    private void checkRange(int n) {
        if (n > getLength()) {
            throw new Error("BitString overflow");
        }
    }

    /**
     * Write bit and increase cursor
     *
     * @param b boolean
     */
    public void writeBit(boolean b) {
        array.addLast(b);
    }

    /**
     * Write bit and increase cursor
     *
     * @param b byte
     */
    void writeBit(byte b) {
        if ((b) > 0) {
            array.addLast(true);
        } else {
            array.addLast(false);
        }
    }

    /**
     * @param ba boolean[]
     */
    public void writeBitArray(boolean[] ba) {
        for (boolean b : ba) {
            writeBit(b);
        }
    }

    /**
     * @param ba byte[]
     */
    public void writeBitArray(byte[] ba) {
        for (byte b : ba) {
            writeBit(b);
        }
    }

    /**
     * Write unsigned int
     *
     * @param number    BigInteger
     * @param bitLength int size of uint in bits
     */
    public void writeUint(BigInteger number, int bitLength) {
        if (number.compareTo(BigInteger.ZERO) < 0) {
            throw new Error("Unsigned number cannot be less than 0");
        }
        if (bitLength == 0 || (number.bitLength() > bitLength)) {
            if (number.compareTo(BigInteger.ZERO) == 0) {
                return;
            }
            throw new Error("bitLength is too small for number, got number=" + number + ", bitLength=" + bitLength);
        }

        String s = number.toString(2);

        if (s.length() != bitLength) {
            s = "0".repeat(bitLength - s.length()) + s;
        }

        for (int i = 0; i < bitLength; i++) {
            writeBit(s.charAt(i) == '1');
        }
    }

    /**
     * Write unsigned int
     *
     * @param number    value
     * @param bitLength size of uint in bits
     */
    public void writeUint(long number, int bitLength) {
        writeUint(BigInteger.valueOf(number), bitLength);
    }

    /**
     * Write signed int
     *
     * @param number    BigInteger
     * @param bitLength int size of int in bits
     */
    public void writeInt(BigInteger number, int bitLength) {
        if (bitLength == 1) {
            if (number.compareTo(BigInteger.valueOf(-1)) == 0) {
                writeBit(true);
                return;
            }
            if (number.compareTo(BigInteger.ZERO) == 0) {
                writeBit(false);
                return;
            }
            throw new Error("bitLength is too small for number");
        } else {
            if (number.signum() == -1) {
                writeBit(true);
                BigInteger b = BigInteger.TWO;
                BigInteger nb = b.pow(bitLength - 1);
                writeUint(nb.add(number), bitLength - 1);
            } else {
                writeBit(false);
                writeUint(number, bitLength - 1);
            }
        }
    }

    /**
     * Write unsigned 8-bit int
     *
     * @param ui8 int
     */
    public void writeUint8(int ui8) {
        writeUint(BigInteger.valueOf(ui8), 8);
    }

    /**
     * Write array of unsigned 8-bit ints
     *
     * @param ui8 byte[]
     */
    public void writeBytes(byte[] ui8) {
        for (byte b : ui8) {
            writeUint8(b & 0xff);
        }
    }

    /**
     * Write array of signed 8-bit ints
     *
     * @param ui8 byte[]
     */
    public void writeBytes(int[] ui8) {
        for (int b : ui8) {
            writeUint8(b);
        }
    }

    /**
     * Write UTF-8 string
     *
     * @param value String
     */
    public void writeString(String value) {
        writeBytes(value.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * @param amount positive BigInteger in nano-coins
     */
    public void writeCoins(BigInteger amount) {
        if (amount.signum() == -1) {
            throw new Error("Coins value must be positive.");
        }

        if (amount.compareTo(BigInteger.ZERO) == 0) {
            writeUint(BigInteger.ZERO, 4);
        } else {
            int bytesSize = (int) Math.ceil((amount.bitLength() / (double) 8));
            if (bytesSize >= 16) {
                throw new Error("Amount is too big. Maximum amount 2^120-1");
            }
            writeUint(BigInteger.valueOf(bytesSize), 4);
            writeUint(amount, bytesSize * 8);
        }
    }

    public void writeVarUint(BigInteger i, int bitLength) {

        if (i.compareTo(BigInteger.ZERO) == 0) {
            writeUint(BigInteger.ZERO, 4);
        } else {
            int bytesSize = (int) Math.ceil((i.bitLength() / (double) 8));
            if (bytesSize >= bitLength) {
                throw new Error("Amount is too big. Should fit in " + bitLength + " bits");
            }
            writeUint(BigInteger.valueOf(bytesSize), 4);
            writeUint(i, bytesSize * 8);
        }
    }

    /**
     * Appends BitString with Address
     * addr_none$00 = MsgAddressExt;
     * addr_std$10
     * anycast:(Maybe Anycast)
     * workchain_id:int8
     * address:uint256 = MsgAddressInt;
     *
     * @param address Address
     */
    public void writeAddress(Address address) {
        if (isNull(address)) {
            writeUint(BigInteger.ZERO, 2);
        } else {
            writeUint(BigInteger.TWO, 2);
            writeUint(BigInteger.ZERO, 1);
            writeInt(BigInteger.valueOf(address.wc), 8);
            writeBytes(address.hashPart);
        }
    }

    /**
     * Write another BitString to this BitString
     *
     * @param anotherBitString BitString
     */
    public void writeBitString(BitString anotherBitString) {
        for (Boolean b : anotherBitString.array) {
            writeBit(anotherBitString.readBit());
        }
    }

    /**
     * Read one bit without removing it
     *
     * @return true or false
     */
    public boolean prereadBit() {
        return get();
    }

    /**
     * Read and removes one bit from start
     *
     * @return true or false
     */
    public boolean readBit() {
//        return array.getFirst();
        return array.pollFirst();
    }

    /**
     * Read n bits from the BitString
     *
     * @param n integer
     * @return BitString with length n read from original Bitstring
     */
    public BitString readBits(int n) {
        BitString result = new BitString(n);
        for (int i = 0; i < n; i++) {
            result.writeBit(readBit());
        }
        return result;
    }

    /**
     * Read rest of bits from the BitString
     *
     * @return BitString with length of read bits from original Bitstring
     */
    public BitString readBits() {
        BitString result = new BitString(array.size());
        for (int i = 0; i < array.size(); i++) {
            result.writeBit(readBit());
        }
        return result;
    }

    /**
     * Read bits of bitLength without moving readCursor, i.e. modifying BitString
     *
     * @param bitLength length in bits
     * @return BigInteger
     */
    public BigInteger preReadUint(int bitLength) {
        BitString cloned = new BitString(this);

        if (bitLength < 1) {
            throw new Error("Incorrect bitLength");
        }
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < bitLength; i++) {
            boolean b = cloned.readBit();
            if (b) {
                s.append("1");
            } else {
                s.append("0");
            }
        }
        return new BigInteger(s.toString(), 2);
    }

    /**
     * Read unsigned int of bitLength
     *
     * @param bitLength int bitLength Size of uint in bits
     * @return BigInteger
     */
    public BigInteger readUint(int bitLength) {
        if (bitLength < 1) {
            throw new Error("Incorrect bitLength");
        }
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < bitLength; i++) {
            boolean b = readBit();
            if (b) {
                s.append("1");
            } else {
                s.append("0");
            }
        }
        return new BigInteger(s.toString(), 2);
    }

    /**
     * Read signed int of bitLength
     *
     * @param bitLength int bitLength Size of signed int in bits
     * @return BigInteger
     */
    public BigInteger readInt(int bitLength) {
        if (bitLength < 1) {
            throw new Error("Incorrect bitLength");
        }

        boolean sign = readBit();
        if (bitLength == 1) {
            return sign ? new BigInteger("-1") : BigInteger.ZERO;
        }

        BigInteger number = readUint(bitLength - 1);
        if (sign) {
            BigInteger b = BigInteger.TWO;
            BigInteger nb = b.pow(bitLength - 1);
            number = number.subtract(nb);
        }
        return number;
    }

    public BigInteger readUint8() {
        return readUint(8);
    }

    public BigInteger readUint16() {
        return readUint(16);
    }

    public BigInteger readUint32() {
        return readUint(32);
    }

    public BigInteger readUint64() {
        return readUint(64);
    }

    public BigInteger readInt8() {
        return readInt(8);
    }

    public BigInteger readInt16() {
        return readInt(16);
    }

    public BigInteger readInt32() {
        return readInt(32);
    }

    public BigInteger readInt64() {
        return readInt(64);
    }

    public Address readAddress() {
        BigInteger i = preReadUint(2);
        if (i.intValue() == 0) {
            readBits(2);
            return null;
        }
        readBits(2);
        readBits(1);
        int workchain = readInt(8).intValue();
        BigInteger hashPart = readUint(256);

        String address = workchain + ":" + String.format("%64s", hashPart.toString(16)).replace(' ', '0');
        return Address.of(address);
    }

    public String readString(int length) {
        BitString bitString = readBits(length);
        return new String(bitString.toByteArray());
    }

    /**
     * @param length in bits
     * @return byte array
     */
    public byte[] readBytes(int length) {
        BitString bitString = readBits(length);
        return bitString.toByteArray();
    }

    /**
     * @return hex string
     */
    public String toString() {
        return toBitString();
    }

    /**
     * @return BitString from 0 to writeCursor
     */
    public String toBitString() {
        BitString cloned = new BitString(this);
        StringBuilder s = new StringBuilder();
        for (Boolean b : cloned.array) {
            s.append(b ? '1' : '0');
        }
        return s.toString();
    }

    public int getLength() {
        return toBitString().length();
    }

    /**
     * @return BitString from current position to writeCursor
     */
    public String getBitString() {
        BitString cloned = clone();
        StringBuilder s = new StringBuilder();
        for (Boolean b : cloned.array) {
            s.append(b ? '1' : '0');
        }
        return s.toString();
    }

    public int[] toUnsignedByteArray() {
        if (array.size() == 0) {
            return new int[0];
        }
        String bin = getBitString();
        int[] result = new int[(int) Math.ceil(bin.length() / (double) 8)];
        int j = 0;
        for (String str : bin.split("(?<=\\G.{8})")) {
            result[j++] = Integer.parseInt(str, 2);
        }
        return result;
    }

    public List<BigInteger> toByteList() {
        if (array.size() == 0) {
            return new ArrayList<>();
        }
        String bin = getBitString();
        List<BigInteger> result = new ArrayList<>((int) Math.ceil(bin.length() / (double) 8));
        int j = 0;
        for (String str : bin.split("(?<=\\G.{8})")) {
            result.add(new BigInteger(str, 2));
        }
        return result;
    }

    public byte[] toByteArray() {
        if (array.size() == 0) {
            return new byte[0];
        }
        String bin = getBitString();
        byte[] result = new byte[(int) Math.ceil(bin.length() / (double) 8)];

        for (int i = 0; i < bin.length(); i++) {
            if (bin.charAt(i) == '1') {
                result[(i / 8)] |= 1 << (7 - (i % 8));
            } else {
                result[(i / 8)] &= ~(1 << (7 - (i % 8)));
            }
        }

        return result;
    }

    public int[] toUintArray() {
        if (array.size() == 0) {
            return new int[0];
        }
        String bin = getBitString();
        int[] result = new int[(int) Math.ceil(bin.length() / (double) 8)];

        for (int i = 0; i < bin.length(); i++) {
            if (bin.charAt(i) == '1') {
                result[(i / 8)] |= 1 << (7 - (i % 8));
            } else {
                result[(i / 8)] &= ~(1 << (7 - (i % 8)));
            }
        }

        return result;
    }

    public boolean[] toBooleanArray() {
        String bin = getBitString();
        boolean[] result = new boolean[bin.length()];
        int i = 0;
        for (Boolean b : array) {
            result[i] = b;
            i++;
        }
        return result;
    }

    public BitString clone() {
        return new BitString(this);
    }

    public BitString cloneFrom(int from) {
        BitString cloned = clone();
        for (int i = 0; i < from; i++) {
            cloned.readBit();
        }
        return cloned;
    }

    /**
     * like Fift
     *
     * @return String
     */
    public String toHex() {

        if (array.size() % 4 == 0) {
            byte[] arr = toByteArray();
            String s = Utils.bytesToHex(arr).toUpperCase();
            if (array.size() % 8 == 0) {
                return s;
            } else {
                return s.substring(0, s.length() - 1);
            }
        } else {
            BitString temp = clone();
            temp.writeBit(true);
            while (temp.array.size() % 4 != 0) {
                temp.writeBit(false);
            }
            return temp.toHex().toUpperCase() + '_';
        }
    }

    public void setTopUppedArray(int[] arr, boolean fulfilledBytes) {
        int length = arr.length * 8;
        array = new BitString(arr).array;

        if (!(fulfilledBytes || (length == 0))) {
            boolean foundEndBit = false;
            for (byte c = 0; c < 7; c++) {
                if (array.pollLast()) {
                    foundEndBit = true;
                    break;
                }
            }
            if (!foundEndBit) {
                System.err.println(Arrays.toString(arr) + ", " + fulfilledBytes);
                throw new Error("Incorrect TopUppedArray");
            }
        }
    }

    public int[] getTopUppedArray() {
        BitString ret = clone();
        int tu = (int) Math.ceil(ret.array.size() / (double) 8) * 8 - ret.array.size();
        if (tu > 0) {
            tu = tu - 1;
            ret.writeBit(true);
            while (tu > 0) {
                tu = tu - 1;
                ret.writeBit(false);
            }
        }
        int[] b = Arrays.copyOfRange(ret.toUnsignedByteArray(), 0, (int) Math.ceil(ret.array.size() / (double) 8));
        return b;
    }
}
