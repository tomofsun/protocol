package com.wakzz.common.exception;

import java.util.Arrays;

public class UnknownMagicException extends RuntimeException {

    private byte[] magic;

    public UnknownMagicException(byte[] magic) {
        this.magic = magic;
    }

    @Override
    public String toString() {
        String s = getClass().getName();
        return s + ": " + Arrays.toString(magic);
    }
}
