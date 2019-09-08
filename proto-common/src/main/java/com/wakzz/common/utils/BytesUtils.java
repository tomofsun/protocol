package com.wakzz.common.utils;

public class BytesUtils {

    private static final char[] BASE16 = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private static final int MASK4 = 0x0f;

    private BytesUtils() {
    }

    /**
     * byte array copy.
     *
     * @param src    src.
     * @param length new length.
     * @return new byte array.
     */
    public static byte[] copyOf(byte[] src, int length) {
        byte[] dest = new byte[length];
        System.arraycopy(src, 0, dest, 0, Math.min(src.length, length));
        return dest;
    }

    /**
     * to byte array.
     *
     * @param v value.
     * @return byte[].
     */
    public static byte[] short2bytes(short v) {
        byte[] ret = {0, 0};
        short2bytes(v, ret);
        return ret;
    }

    /**
     * to byte array.
     *
     * @param v value.
     * @param b byte array.
     */
    public static void short2bytes(short v, byte[] b) {
        short2bytes(v, b, 0);
    }

    /**
     * to byte array.
     *
     * @param v value.
     * @param b byte array.
     */
    public static void short2bytes(short v, byte[] b, int off) {
        b[off + 1] = (byte) v;
        b[off + 0] = (byte) (v >>> 8);
    }

    /**
     * to byte array.
     *
     * @param v value.
     * @return byte[].
     */
    public static byte[] int2bytes(int v) {
        byte[] ret = {0, 0, 0, 0};
        int2bytes(v, ret);
        return ret;
    }

    /**
     * to byte array.
     *
     * @param v value.
     * @param b byte array.
     */
    public static void int2bytes(int v, byte[] b) {
        int2bytes(v, b, 0);
    }

    /**
     * to byte array.
     *
     * @param v   value.
     * @param b   byte array.
     * @param off array offset.
     */
    public static void int2bytes(int v, byte[] b, int off) {
        b[off + 3] = (byte) v;
        b[off + 2] = (byte) (v >>> 8);
        b[off + 1] = (byte) (v >>> 16);
        b[off + 0] = (byte) (v >>> 24);
    }

    /**
     * to byte array.
     *
     * @param v value.
     * @return byte[].
     */
    public static byte[] float2bytes(float v) {
        byte[] ret = {0, 0, 0, 0};
        float2bytes(v, ret);
        return ret;
    }

    /**
     * to byte array.
     *
     * @param v value.
     * @param b byte array.
     */
    public static void float2bytes(float v, byte[] b) {
        float2bytes(v, b, 0);
    }

    /**
     * to byte array.
     *
     * @param v   value.
     * @param b   byte array.
     * @param off array offset.
     */
    public static void float2bytes(float v, byte[] b, int off) {
        int i = Float.floatToIntBits(v);
        b[off + 3] = (byte) i;
        b[off + 2] = (byte) (i >>> 8);
        b[off + 1] = (byte) (i >>> 16);
        b[off + 0] = (byte) (i >>> 24);
    }

    /**
     * to byte array.
     *
     * @param v value.
     * @return byte[].
     */
    public static byte[] long2bytes(long v) {
        byte[] ret = {0, 0, 0, 0, 0, 0, 0, 0};
        long2bytes(v, ret);
        return ret;
    }

    /**
     * to byte array.
     *
     * @param v value.
     * @param b byte array.
     */
    public static void long2bytes(long v, byte[] b) {
        long2bytes(v, b, 0);
    }

    /**
     * to byte array.
     *
     * @param v   value.
     * @param b   byte array.
     * @param off array offset.
     */
    public static void long2bytes(long v, byte[] b, int off) {
        b[off + 7] = (byte) v;
        b[off + 6] = (byte) (v >>> 8);
        b[off + 5] = (byte) (v >>> 16);
        b[off + 4] = (byte) (v >>> 24);
        b[off + 3] = (byte) (v >>> 32);
        b[off + 2] = (byte) (v >>> 40);
        b[off + 1] = (byte) (v >>> 48);
        b[off + 0] = (byte) (v >>> 56);
    }

    /**
     * to byte array.
     *
     * @param v value.
     * @return byte[].
     */
    public static byte[] double2bytes(double v) {
        byte[] ret = {0, 0, 0, 0, 0, 0, 0, 0};
        double2bytes(v, ret);
        return ret;
    }

    /**
     * to byte array.
     *
     * @param v value.
     * @param b byte array.
     */
    public static void double2bytes(double v, byte[] b) {
        double2bytes(v, b, 0);
    }

    /**
     * to byte array.
     *
     * @param v   value.
     * @param b   byte array.
     * @param off array offset.
     */
    public static void double2bytes(double v, byte[] b, int off) {
        long j = Double.doubleToLongBits(v);
        b[off + 7] = (byte) j;
        b[off + 6] = (byte) (j >>> 8);
        b[off + 5] = (byte) (j >>> 16);
        b[off + 4] = (byte) (j >>> 24);
        b[off + 3] = (byte) (j >>> 32);
        b[off + 2] = (byte) (j >>> 40);
        b[off + 1] = (byte) (j >>> 48);
        b[off + 0] = (byte) (j >>> 56);
    }

    /**
     * to short.
     *
     * @param b byte array.
     * @return short.
     */
    public static short bytes2short(byte[] b) {
        return bytes2short(b, 0);
    }

    /**
     * to short.
     *
     * @param b   byte array.
     * @param off offset.
     * @return short.
     */
    public static short bytes2short(byte[] b, int off) {
        return (short) (((b[off + 1] & 0xFF) << 0) +
                ((b[off + 0]) << 8));
    }

    /**
     * to int.
     *
     * @param b byte array.
     * @return int.
     */
    public static int bytes2int(byte[] b) {
        return bytes2int(b, 0);
    }

    /**
     * to int.
     *
     * @param b   byte array.
     * @param off offset.
     * @return int.
     */
    public static int bytes2int(byte[] b, int off) {
        return ((b[off + 3] & 0xFF) << 0) +
                ((b[off + 2] & 0xFF) << 8) +
                ((b[off + 1] & 0xFF) << 16) +
                ((b[off + 0]) << 24);
    }

    /**
     * to int.
     *
     * @param b byte array.
     * @return int.
     */
    public static float bytes2float(byte[] b) {
        return bytes2float(b, 0);
    }

    /**
     * to int.
     *
     * @param b   byte array.
     * @param off offset.
     * @return int.
     */
    public static float bytes2float(byte[] b, int off) {
        int i = ((b[off + 3] & 0xFF) << 0) +
                ((b[off + 2] & 0xFF) << 8) +
                ((b[off + 1] & 0xFF) << 16) +
                ((b[off + 0]) << 24);
        return Float.intBitsToFloat(i);
    }

    /**
     * to long.
     *
     * @param b byte array.
     * @return long.
     */
    public static long bytes2long(byte[] b) {
        return bytes2long(b, 0);
    }

    /**
     * to long.
     *
     * @param b   byte array.
     * @param off offset.
     * @return long.
     */
    public static long bytes2long(byte[] b, int off) {
        return ((b[off + 7] & 0xFFL) << 0) +
                ((b[off + 6] & 0xFFL) << 8) +
                ((b[off + 5] & 0xFFL) << 16) +
                ((b[off + 4] & 0xFFL) << 24) +
                ((b[off + 3] & 0xFFL) << 32) +
                ((b[off + 2] & 0xFFL) << 40) +
                ((b[off + 1] & 0xFFL) << 48) +
                (((long) b[off + 0]) << 56);
    }

    /**
     * to long.
     *
     * @param b byte array.
     * @return double.
     */
    public static double bytes2double(byte[] b) {
        return bytes2double(b, 0);
    }

    /**
     * to long.
     *
     * @param b   byte array.
     * @param off offset.
     * @return double.
     */
    public static double bytes2double(byte[] b, int off) {
        long j = ((b[off + 7] & 0xFFL) << 0) +
                ((b[off + 6] & 0xFFL) << 8) +
                ((b[off + 5] & 0xFFL) << 16) +
                ((b[off + 4] & 0xFFL) << 24) +
                ((b[off + 3] & 0xFFL) << 32) +
                ((b[off + 2] & 0xFFL) << 40) +
                ((b[off + 1] & 0xFFL) << 48) +
                (((long) b[off + 0]) << 56);
        return Double.longBitsToDouble(j);
    }

    /**
     * to hex string.
     *
     * @param bs byte array.
     * @return hex string.
     */
    public static String bytes2hex(byte[] bs) {
        return bytes2hex(bs, 0, bs.length);
    }

    /**
     * to hex string.
     *
     * @param bs  byte array.
     * @param off offset.
     * @param len length.
     * @return hex string.
     */
    public static String bytes2hex(byte[] bs, int off, int len) {
        if (off < 0) {
            throw new IndexOutOfBoundsException("bytes2hex: offset < 0, offset is " + off);
        }
        if (len < 0) {
            throw new IndexOutOfBoundsException("bytes2hex: length < 0, length is " + len);
        }
        if (off + len > bs.length) {
            throw new IndexOutOfBoundsException("bytes2hex: offset + length > array length.");
        }

        byte b;
        int r = off, w = 0;
        char[] cs = new char[len * 2];
        for (int i = 0; i < len; i++) {
            b = bs[r++];
            cs[w++] = BASE16[b >> 4 & MASK4];
            cs[w++] = BASE16[b & MASK4];
        }
        return new String(cs);
    }

    /**
     * from hex string.
     *
     * @param str hex string.
     * @return byte array.
     */
    public static byte[] hex2bytes(String str) {
        return hex2bytes(str, 0, str.length());
    }

    /**
     * from hex string.
     *
     * @param str hex string.
     * @param off offset.
     * @param len length.
     * @return byte array.
     */
    public static byte[] hex2bytes(final String str, final int off, int len) {
        if ((len & 1) == 1) {
            throw new IllegalArgumentException("hex2bytes: ( len & 1 ) == 1.");
        }

        if (off < 0) {
            throw new IndexOutOfBoundsException("hex2bytes: offset < 0, offset is " + off);
        }
        if (len < 0) {
            throw new IndexOutOfBoundsException("hex2bytes: length < 0, length is " + len);
        }
        if (off + len > str.length()) {
            throw new IndexOutOfBoundsException("hex2bytes: offset + length > array length.");
        }

        int num = len / 2, r = off, w = 0;
        byte[] b = new byte[num];
        for (int i = 0; i < num; i++) {
            b[w++] = (byte) (hex(str.charAt(r++)) << 4 | hex(str.charAt(r++)));
        }
        return b;
    }

    private static byte hex(char c) {
        if (c <= '9') {
            return (byte) (c - '0');
        }
        if (c >= 'a' && c <= 'f') {
            return (byte) (c - 'a' + 10);
        }
        if (c >= 'A' && c <= 'F') {
            return (byte) (c - 'A' + 10);
        }
        throw new IllegalArgumentException("hex string format error [" + c + "].");
    }

}