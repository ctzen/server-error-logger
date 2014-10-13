package com.ctzen.servlet.errorlogger;

import java.io.IOException;

/**
 * Error logger interface.  Implementor is responsible to log the error somewhere.
 *
 * @author cchang
 */
public interface ErrorLogger {

    void log(final String errorId, final String error) throws IOException;

}
