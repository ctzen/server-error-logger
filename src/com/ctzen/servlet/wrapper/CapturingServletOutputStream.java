package com.ctzen.servlet.wrapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ResourceBundle;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;

/**
 *
 * Wraps a {@link ServletOutputStream} and capture written bytes.
 *
 * @author cchang
 */
public class CapturingServletOutputStream extends ServletOutputStream {

    private static final String LSTRING_FILE = "javax.servlet.LocalStrings";

    private static ResourceBundle lStrings = ResourceBundle.getBundle(LSTRING_FILE);

    /**
     * @param delegate  the real {@link ServletOutputStream}
     */
    public CapturingServletOutputStream(final ServletOutputStream delegate, final ByteArrayOutputStream buffer) {
        this.delegate = delegate;
        this.buffer = buffer;
    }

    private final ServletOutputStream delegate;

    private final ByteArrayOutputStream buffer;

    private static final byte[] LN_BYTES = "\r\n".getBytes();

    @Override
    public void print(final String s) throws IOException {
        delegate.print(s);
        buffer.write((s == null ? "null" : s).getBytes());
    }

    @Override
    public void print(final boolean b) throws IOException {
        delegate.print(b);
        buffer.write(lStrings.getString(b ? "value.true" : "value.false").getBytes());
    }

    @Override
    public void print(final char c) throws IOException {
        delegate.print(c);
        buffer.write(String.valueOf(c).getBytes());
    }

    @Override
    public void print(final int i) throws IOException {
        delegate.print(i);
        buffer.write(String.valueOf(i).getBytes());
    }

    @Override
    public void print(final long l) throws IOException {
        delegate.print(l);
        buffer.write(String.valueOf(l).getBytes());
    }

    @Override
    public void print(final float f) throws IOException {
        delegate.print(f);
        buffer.write(String.valueOf(f).getBytes());
    }

    @Override
    public void print(final double d) throws IOException {
        delegate.print(d);
        buffer.write(String.valueOf(d).getBytes());
    }

    @Override
    public void println() throws IOException {
        delegate.println();
        buffer.write(LN_BYTES);
    }

    @Override
    public void println(final String s) throws IOException {
        delegate.println(s);
        buffer.write((s == null ? "null" : s).getBytes());
        buffer.write(LN_BYTES);
    }

    @Override
    public void println(final boolean b) throws IOException {
        delegate.println(b);
        buffer.write(lStrings.getString(b ? "value.true" : "value.false").getBytes());
        buffer.write(LN_BYTES);
    }

    @Override
    public void println(final char c) throws IOException {
        delegate.println(c);
        buffer.write(String.valueOf(c).getBytes());
        buffer.write(LN_BYTES);
    }

    @Override
    public void println(final int i) throws IOException {
        delegate.println(i);
        buffer.write(String.valueOf(i).getBytes());
        buffer.write(LN_BYTES);
    }

    @Override
    public void println(final long l) throws IOException {
        delegate.println(l);
        buffer.write(String.valueOf(l).getBytes());
        buffer.write(LN_BYTES);
    }

    @Override
    public void println(final float f) throws IOException {
        delegate.println(f);
        buffer.write(String.valueOf(f).getBytes());
        buffer.write(LN_BYTES);
    }

    @Override
    public void println(final double d) throws IOException {
        delegate.println(d);
        buffer.write(String.valueOf(d).getBytes());
        buffer.write(LN_BYTES);
    }

    @Override
    public boolean isReady() {
        return delegate.isReady();
    }

    @Override
    public void setWriteListener(final WriteListener writeListener) {
        delegate.setWriteListener(writeListener);
    }

    @Override
    public void write(final int b) throws IOException {
        delegate.write(b);
        buffer.write(b);
    }

    @Override
    public void write(final byte[] b) throws IOException {
        delegate.write(b);
        buffer.write(b);
    }

    @Override
    public void write(final byte[] b, int off, int len) throws IOException {
        delegate.write(b, off, len);
        buffer.write(b, off, len);
    }

    @Override
    public void flush() throws IOException {
        delegate.flush();
    }

    @Override
    public void close() throws IOException {
        delegate.close();
    }

}
