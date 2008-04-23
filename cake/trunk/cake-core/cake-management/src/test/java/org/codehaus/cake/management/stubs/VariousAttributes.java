/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org>
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.management.stubs;

import java.io.IOException;

import org.codehaus.cake.management.annotation.ManagedAttribute;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: AttributedStub3.java 415 2007-11-09 08:25:23Z kasper $
 */
public class VariousAttributes {
    private String none;

    private boolean readOnly;

    private Integer readWrite;

    private String writeOnly;

    public String getError() {
        throw new LinkageError();
    }

    @ManagedAttribute()
    public String getException2() throws Exception {
        throw new IOException();
    }

    public String getNone() {
        return none;
    }

    public Integer getReadWrite() {
        return readWrite;
    }

    public String getRuntimeException() {
        throw new IllegalMonitorStateException();
    }

    public String getWriteOnly() {
        return writeOnly;
    }

    @ManagedAttribute
    public boolean isReadOnly() {
        return readOnly;
    }

    @ManagedAttribute(defaultValue = "throwError", description = "desc")
    public void setError(String ignore) {
        throw new LinkageError();
    }

    @ManagedAttribute()
    public void setException1(String re) throws Exception {
        throw new IOException();
    }

    public void setNone(String none) {
        this.none = none;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    @ManagedAttribute()
    public void setReadWrite(Integer readWrite) {
        this.readWrite = readWrite;
    }

    @ManagedAttribute(defaultValue = "throwRuntimeException", description = "desc")
    public void setRuntimeException(String re) {
        throw new IllegalMonitorStateException();
    }

    @ManagedAttribute(isWriteOnly = true)
    public void setWriteOnly(String writeOnly) {
        this.writeOnly = writeOnly;
    }
}
