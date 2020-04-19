package com.wakzz.common.exception;

public class UnknownProtoVersionException extends RuntimeException {

    private byte version;

    public UnknownProtoVersionException(byte version) {
        this.version = version;
    }

    @Override
    public String toString() {
        String s = getClass().getName();
        return s + ": " + version;
    }
}
