/*
 * Copyright 2019 lambdaprime
 * 
 * Website: https://github.com/lambdaprime/xfunction
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package id.xfunction;

import java.nio.ByteBuffer;
import java.util.regex.Pattern;

/**
 * @author lambdaprime intid@protonmail.com
 */
public class XByte {

    private static final String[] HEX_CODES = {
        "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "0a", "0b", "0c", "0d", "0e",
        "0f", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "1a", "1b", "1c", "1d",
        "1e", "1f", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "2a", "2b", "2c",
        "2d", "2e", "2f", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "3a", "3b",
        "3c", "3d", "3e", "3f", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "4a",
        "4b", "4c", "4d", "4e", "4f", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59",
        "5a", "5b", "5c", "5d", "5e", "5f", "60", "61", "62", "63", "64", "65", "66", "67", "68",
        "69", "6a", "6b", "6c", "6d", "6e", "6f", "70", "71", "72", "73", "74", "75", "76", "77",
        "78", "79", "7a", "7b", "7c", "7d", "7e", "7f", "80", "81", "82", "83", "84", "85", "86",
        "87", "88", "89", "8a", "8b", "8c", "8d", "8e", "8f", "90", "91", "92", "93", "94", "95",
        "96", "97", "98", "99", "9a", "9b", "9c", "9d", "9e", "9f", "a0", "a1", "a2", "a3", "a4",
        "a5", "a6", "a7", "a8", "a9", "aa", "ab", "ac", "ad", "ae", "af", "b0", "b1", "b2", "b3",
        "b4", "b5", "b6", "b7", "b8", "b9", "ba", "bb", "bc", "bd", "be", "bf", "c0", "c1", "c2",
        "c3", "c4", "c5", "c6", "c7", "c8", "c9", "ca", "cb", "cc", "cd", "ce", "cf", "d0", "d1",
        "d2", "d3", "d4", "d5", "d6", "d7", "d8", "d9", "da", "db", "dc", "dd", "de", "df", "e0",
        "e1", "e2", "e3", "e4", "e5", "e6", "e7", "e8", "e9", "ea", "eb", "ec", "ed", "ee", "ef",
        "f0", "f1", "f2", "f3", "f4", "f5", "f6", "f7", "f8", "f9", "fa", "fb", "fc", "fd", "fe",
        "ff"
    };

    /**
     * Returns a string representation of the byte array as a hexadecimal string.
     *
     * <p>Each byte of the array is converted to the code base 16. The code is padded with 0 at the
     * front if its length is less than 2. It means that length of the code is always 2 symbols: a -
     * 0a
     *
     * <p>Example:
     *
     * <pre>{@code
     * XByte.toHex("hello world".getBytes())
     * }</pre>
     *
     * <p>Will return string "68656c6c6f20776f726c64".
     *
     * <p>Java 17 analog is:
     *
     * <pre>{@code
     * HexFormat.of().formatHex("hello world".getBytes())
     * }</pre>
     */
    public static String toHex(byte... a) {
        StringBuilder buf = new StringBuilder();
        for (byte b : a) {
            buf.append(toHexPair(b));
        }
        String hex = buf.toString();
        Preconditions.isTrue(hex.length() % 2 == 0);
        return hex;
    }

    public static String toHex(int... a) {
        StringBuilder buf = new StringBuilder();
        for (int i : a) {
            var bytes = ByteBuffer.allocate(4).putInt(i).array();
            for (byte b : bytes) {
                buf.append(toHexPair(b));
            }
        }
        String hex = buf.toString();
        Preconditions.isTrue(hex.length() % 2 == 0);
        return hex;
    }

    /**
     * Java 17 analog:
     *
     * <pre>{@code
     * HexFormat.of().parseHex("68656c6c6f20776f726c64")
     * }</pre>
     */
    public static byte[] fromHex(String hex) {
        var out = new byte[hex.length() / 2];
        for (int i = 0; i < hex.length(); i += 2) {
            out[i / 2] = Integer.valueOf("" + hex.charAt(i) + hex.charAt(i + 1), 16).byteValue();
        }
        return out;
    }

    /**
     * Works same way as {@link #toHex(byte...)} except it formats the resulting string as pairs of
     * bytes separated with white spaces
     *
     * <p>Example:
     *
     * <pre>{@code
     * XByte.toHexPairs("hello world".getBytes())
     * }</pre>
     *
     * <p>Will return string "68 65 6c 6c 6f 20 77 6f 72 6c 64".
     */
    public static String toHexPairs(byte... a) {
        String hex = toHex(a);
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < hex.length(); i += 2) {
            buf.append(hex.substring(i, i + 2));
            buf.append(" ");
        }
        if (buf.length() > 0) buf.setLength(buf.length() - 1);
        return buf.toString();
    }

    /** Opposite to {@link XByte#toHexPairs(byte...)} */
    public static byte[] fromHexPairs(String hex) {
        var array =
                Pattern.compile("\\s|,|\\n")
                        .splitAsStream(hex)
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .mapToInt(s -> Integer.valueOf(s, 16))
                        .toArray();
        return copyAsByteLiterals(array);
    }

    /**
     * Converts int to hexadecimal pairs string.
     *
     * <p>Example:
     *
     * <pre>{@code
     * XByte.toHexPairs(123)
     * }</pre>
     *
     * <p>Will return string "00 00 00 7b".
     */
    public static String toHexPairs(int i) {
        return toHexPairs(ByteBuffer.allocate(4).putInt(i).array());
    }

    /**
     * Returns a hexadecimal pair string representation of the given byte.
     *
     * <p>The code is padded with 0 at the front if its length is less than 2. It means that length
     * of the code is always 2 symbols: a - 0a
     */
    public static String toHexPair(byte b) {
        return HEX_CODES[Byte.toUnsignedInt(b)];
    }

    /**
     * Java does not allow you to specify byte literals in form of 0xff (which is int literal in
     * Java). This methods accepts integer literals and cast them to bytes.
     *
     * <p>It allows you to create byte[] from int literals like:
     *
     * <pre>{@code
     * var cafe = XByte.copyToByteArray(0xca, 0xfe);
     * // instead of
     * var cafe = new byte[]{(byte)0xca, (byte)0xfe};
     * }</pre>
     */
    public static byte[] copyAsByteLiterals(int... byteLiterals) {
        byte[] res = new byte[byteLiterals.length];
        for (int i = 0; i < res.length; i++) {
            Preconditions.isLess(byteLiterals[i], 0xff, "Not a byte literal");
            res[i] = (byte) byteLiterals[i];
        }
        return res;
    }

    /** Resulting byte array size will be ints.length * Integer.BYTES */
    public static byte[] copyToByteArray(int... ints) {
        var buf = ByteBuffer.allocate(ints.length * Integer.BYTES);
        buf.asIntBuffer().put(ints);
        return buf.array();
    }

    /** Resulting byte array size will be longs.length * Long.BYTES */
    public static byte[] copyToByteArray(long... longs) {
        var buf = ByteBuffer.allocate(longs.length * Long.BYTES);
        buf.asLongBuffer().put(longs);
        return buf.array();
    }

    /** Reverse bits in the byte */
    public static byte reverseBits(byte b) {
        b = (byte) (((b & 0xaa) >> 1) | ((b & 0x55) << 1));
        b = (byte) (((b & 0xcc) >> 2) | ((b & 0x33) << 2));
        b = (byte) (((b & 0xf0) >> 4) | ((b & 0x0f) << 4));
        return b;
    }

    /**
     * Reverse bits in the bytes of the int.
     *
     * <p>Example: 0b00000011_00001010 into 0b11000000_01010000
     */
    public static int reverseBitsInBytes(int i) {
        i = ((i & 0xaaaaaaaa) >> 1) | ((i & 0x55555555) << 1);
        i = ((i & 0xcccccccc) >> 2) | ((i & 0x33333333) << 2);
        i = ((i & 0xf0f0f0f0) >> 4) | ((i & 0x0f0f0f0f) << 4);
        return i;
    }

    public static int toInt(int byte1st) {
        return byte1st << 24;
    }

    public static int toInt(int byte1st, int byte2nd) {
        return toInt(byte1st) | (0xff & byte2nd) << 16;
    }

    public static int toInt(int byte1st, int byte2nd, int byte3rd) {
        return toInt(byte1st, byte2nd) | (0xff & byte3rd) << 8;
    }

    public static int toInt(int byte1st, int byte2nd, int byte3rd, int byte4st) {
        return toInt(byte1st, byte2nd, byte3rd) | (0xff & byte4st);
    }

    /**
     * Equivalent to:
     *
     * <pre>{@code
     * return toInt(arrayOf4Bytes[0], arrayOf4Bytes[1], arrayOf4Bytes[2], arrayOf4Bytes[3]);
     * }</pre>
     */
    public static int toInt(byte[] arrayOf4Bytes) {
        Preconditions.equals(4, arrayOf4Bytes.length, "integer size exceeded");
        return toInt(arrayOf4Bytes[0], arrayOf4Bytes[1], arrayOf4Bytes[2], arrayOf4Bytes[3]);
    }

    public static short toShort(int byte1st) {
        return (short) (byte1st << 8);
    }

    public static short toShort(int byte1st, int byte2nd) {
        return (short) (toShort(byte1st) | byte2nd);
    }
}
