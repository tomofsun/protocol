package com.wakzz.common.exception;

public class UnknownProtoSerializerException extends RuntimeException {

    private byte serializer;

    public UnknownProtoSerializerException(byte serializer) {
        this.serializer = serializer;
    }

    @Override
    public String toString() {
        String s = getClass().getName();
        return s + ": " + serializer;
    }
}
