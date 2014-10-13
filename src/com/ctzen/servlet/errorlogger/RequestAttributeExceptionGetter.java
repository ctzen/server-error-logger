package com.ctzen.servlet.errorlogger;

import javax.servlet.http.HttpServletRequest;

/**
 * {@link ExceptionGetter} where Exception is stored as request attribute, such as the spring DispatcherServlet.
 *
 * @author cchang
 */
public class RequestAttributeExceptionGetter implements ExceptionGetter {

    /**
     * @param attribute request attribute name where Exception is kept,
     * e.g. org.springframework.web.servlet.DispatcherServlet.EXCEPTION_ATTRIBUTE
     */
    public RequestAttributeExceptionGetter(final String attribute) {
        this.attribute = attribute;
    }

    private final String attribute;

    @Override
    public Exception getException(final HttpServletRequest req) {
        return (Exception)req.getAttribute(attribute);
    }

}
