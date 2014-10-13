package com.ctzen.servlet.wrapper;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Wraps a {@link HttpServletRequest} and captures request body.<br>
 * <br>
 * Does not support multi-parts... yet.
 *
 * @author cchang
 */
public class CapturingHttpServletRequestWrapper extends HttpServletRequestWrapper {

    public CapturingHttpServletRequestWrapper(final HttpServletRequest request) {
        super(request);
    }

    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    /**
     * @return buffer containing bytes read
     */
    public ByteArrayOutputStream getBuffer() {
        return buffer;
    }

    private CapturingServletInputStream stream;

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (stream == null) {
            stream = new CapturingServletInputStream(super.getInputStream(), buffer);
        }
        return stream;
    }

    private BufferedReader reader;

    @Override
    public BufferedReader getReader() throws IOException {
        if (reader == null) {
            final String enc = getCharacterEncoding();
            reader = new BufferedReader(new InputStreamReader(getInputStream(), enc == null ? "UTF-8" : enc));
        }
        return reader;
     }

}
