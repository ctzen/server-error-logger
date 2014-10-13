package com.ctzen.servlet.errorlogger;

import javax.servlet.http.HttpServletRequest;

/**
 * Implementors know how to get the exception thrown during request processing.
 *
 * @author cchang
 */
public interface ExceptionGetter {

    /**
     * @param req
     * @return Exception thrown or null if there isn't ny
     */
    Exception getException(final HttpServletRequest req);

}
