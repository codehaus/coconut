/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.core;

import java.io.IOException;

public class GeneratedTransformerMock implements
        GeneratedTransformerMockInterface, Transformer<String, Long> {
    public String method() {
        return "m";
    }

    public void voidReturn() {

    }

    public int ireturn() {
        return 1;
    }

    public long lreturn() {
        return 2l;
    }

    public short sreturn() {
        return 3;
    }

    public double dreturn() {
        return 4;
    }

    public float freturn() {
        return 5;
    }

    public byte byreturn() {
        return 6;
    }

    public char creturn() {
        return 7;
    }
    
    public int iarg(int i) {
        return i;
    }

    public int iarg(Integer i) {
        return i.intValue();
    }
    
    public String declaresException() throws Exception {
        return "declares";
    }

    public String throwsException() throws Exception {
        throw new IOException();
    }

    public boolean breturn() {
        return true;
    }

    
    public String method2() {
        return "m2";
    }

    protected String protectedMethod() {
        return "shouldFail";
    }

    String packagePrivateMethod() {
        return "shouldFail";
    }

    @SuppressWarnings("unused")
    private String privateMethod() {
        return "shouldFail";
    }

    public String interfaceMethod() {
        return "im";
    }
    public Long string0Arg() {
        return Long.parseLong("4");
    }
    public Long string1Arg(String from) {
        return Long.parseLong(from);
    }

    public Long string2Arg(String from1, String from2) {
        return Long.parseLong(from1) + Long.parseLong(from2);
    }

    public Long string3Arg(String from1, String from2, String from3) {
        return Long.parseLong(from1) + Long.parseLong(from2)
                + Long.parseLong(from3);
    }

    public Long string4Arg(String from1, String from2, String from3,
            String from4) {
        return Long.parseLong(from1) + Long.parseLong(from2)
                + Long.parseLong(from3) + Long.parseLong(from4);
    }

    public Long mixed(String from, long[] i1, int i2, Boolean[] i3) {
        return 4l;
    }
    
    public GeneratedTransformerMockInterface self() {
        return this;
    }

    public Long transform(String from, Object i1, Long i2) {
        return Long.parseLong(from) + ((Number) i1).longValue() + i2;
    }

    public Long transform(String from, Number i2) {
        return Long.parseLong(from) + i2.longValue();
    }

    public Long transform(String from) {
        return Long.parseLong(from);
    }

    public Long transform(Object from) {
        return 5l;
    }
}
