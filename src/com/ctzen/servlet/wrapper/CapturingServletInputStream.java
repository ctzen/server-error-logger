package com.ctzen.servlet.wrapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;

/**
 * Wraps a {@link ServletInputStream} and capture read bytes.<br>
 * <br>
 * CAVEAT: Captures bytes as they are read, may not be the actual entire
 * input stream content if {@link #skip(long)}, {@link #mark(int)},
 * and/or {@link #reset()}, or simply did not exhaust the stream.
 *
 * @author cchang
 */
public class CapturingServletInputStream extends ServletInputStream {

    /**
     * @param delegate  the real {@link ServletInputStream}
     */
    public CapturingServletInputStream(final ServletInputStream delegate, final ByteArrayOutputStream buffer) {
        this.delegate = delegate;
        this.buffer = buffer;
    }

    private final ServletInputStream delegate;

    private final ByteArrayOutputStream buffer;

    @Override
    public int readLine(final byte[] b, final int off, final int len) throws IOException {
        final int ret = delegate.readLine(b, off, len);
        if (ret > 0) {
            buffer.write(b, off, ret);
        }
        return ret;
    }

    @Override
    public boolean isFinished() {
        return delegate.isFinished();
    }

    @Override
    public boolean isReady() {
        return delegate.isReady();
    }

    @Override
    public void setReadListener(final ReadListener readListener) {
        delegate.setReadListener(readListener);
    }

    @Override
    public int read() throws IOException {
        final int ret = delegate.read();
        if (ret != -1) {
            buffer.write(ret);
        }
        return ret;
    }

    @Override
    public int read(final byte[] b) throws IOException {
        final int ret = delegate.read(b);
        if (ret > 0) {
            buffer.write(b, 0, ret);
        }
        return ret;
    }

    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        final int ret = delegate.read(b, off, len);
        if (ret > 0) {
            buffer.write(b, off, ret);
        }
        return ret;
    }

    @Override
    public long skip(final long n) throws IOException {
        return delegate.skip(n);
    }

    @Override
    public int available() throws IOException {
        return delegate.available();
    }

    @Override
    public void close() throws IOException {
        delegate.close();
    }

    @Override
    public synchronized void mark(final int readlimit) {
        delegate.mark(readlimit);
    }

    @Override
    public synchronized void reset() throws IOException {
        delegate.reset();
    }

    @Override
    public boolean markSupported() {
        return delegate.markSupported();
    }

}
