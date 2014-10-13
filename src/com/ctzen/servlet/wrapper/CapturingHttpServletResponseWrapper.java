package com.ctzen.servlet.wrapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * Wraps a {@link HttpServletResponse} and captures response body.
 *
 * @author cchang
 */
public class CapturingHttpServletResponseWrapper extends HttpServletResponseWrapper {

    public CapturingHttpServletResponseWrapper(final HttpServletResponse response) {
        super(response);
    }

    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    /**
     * @return buffer containing bytes written
     */
    public ByteArrayOutputStream getBuffer() {
        return buffer;
    }

    private CapturingServletOutputStream stream;

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (stream == null) {
            stream = new CapturingServletOutputStream(super.getOutputStream(), buffer);
        }
        return stream;
    }

    private PrintWriter writer;

    @Override
    public PrintWriter getWriter() throws IOException {
        if (writer == null) {
            final String enc = getCharacterEncoding();
            writer = new PrintWriter(new OutputStreamWriter(getOutputStream(), enc == null ? "UTF-8" : enc));
        }
        return writer;
    }

}
