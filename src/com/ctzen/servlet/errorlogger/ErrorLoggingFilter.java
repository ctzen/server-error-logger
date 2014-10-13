package com.ctzen.servlet.errorlogger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctzen.common.tostring.ToStringUtil;
import com.ctzen.servlet.util.ServletToStringUtil;
import com.ctzen.servlet.wrapper.CapturingHttpServletRequestWrapper;
import com.ctzen.servlet.wrapper.CapturingHttpServletResponseWrapper;

/**
 * Request and response body capturing, error logging servlet filter.
 *
 * @author cchang
 */
public class ErrorLoggingFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(ErrorLoggingFilter.class);

    /**
     * Request attribute name for the request body {@link ByteArrayOutputStream} buffer.
     */
    public static final String REQUEST_BODY_ATTRIBUTE = ErrorLoggingFilter.class.getName() + ".REQUEST_BODY";

    /**
     * Request attribute name for the response body {@link ByteArrayOutputStream} buffer.
     */
    public static final String RESPONSE_BODY_ATTRIBUTE = ErrorLoggingFilter.class.getName() + ".RESPONSE_BODY";

    /**
     * Set this in request attribute (to any value except {@code null}) to skip logging.
     */
    public static final String DONT_LOG_ATTRIBUTE = ErrorLoggingFilter.class.getName() + ".DONT_LOG";

    /**
     * Response header name for the error id.
     */
    public static final String ERROR_ID_HEADER_NAME = "X-Error-Id";

    /**
     * Constructor.
     *
     * @param statusCodes           trigger logging when response http status code is one of these, typically [500]
     * @param exceptionGetter       responsible for retrieving any exception of the current request processing
     * @param servletToStringUtil   toString() helpers for HTTP parts
     * @param errorLogger           responsible for the actuall logging
     */
    public ErrorLoggingFilter(final Collection<Integer> statusCodes,
            final ExceptionGetter exceptionGetter,
            final ServletToStringUtil servletToStringUtil,
            final ErrorLogger errorLogger) {
        this.statusCodes = statusCodes;
        LOG.info("log upon status codes: {}", statusCodes);
        this.exceptionGetter = exceptionGetter;
        this.servletToStringUtil = servletToStringUtil;
        this.errorLogger = errorLogger;
    }

    private final Collection<Integer> statusCodes;

    /**
     * @return response status codes that triggers logging
     */
    public Collection<Integer> getStatusCodes() {
        return statusCodes;
    }

    private final ExceptionGetter exceptionGetter;

    public ExceptionGetter getExceptionGetter() {
        return exceptionGetter;
    }

    private final ServletToStringUtil servletToStringUtil;

    public ServletToStringUtil getServletToStringUtil() {
        return servletToStringUtil;
    }

    private final ErrorLogger errorLogger;

    public ErrorLogger getErrorLogger() {
        return errorLogger;
    }

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        final CapturingHttpServletRequestWrapper req = new CapturingHttpServletRequestWrapper((HttpServletRequest)request);
        req.setAttribute(REQUEST_BODY_ATTRIBUTE, req.getBuffer());
        final CapturingHttpServletResponseWrapper resp = new CapturingHttpServletResponseWrapper((HttpServletResponse)response);
        req.setAttribute(RESPONSE_BODY_ATTRIBUTE, resp.getBuffer());
        try {
            chain.doFilter(req, resp);
        }
        catch (final Exception e) {
            doLog(req, resp, e);
            throw e;
        }
        if (statusCodes.contains(resp.getStatus()) && req.getAttribute(DONT_LOG_ATTRIBUTE) == null) {
            final Exception ex = exceptionGetter.getException(req);
            doLog(req, resp, ex);
        }
    }

    private void doLog(final CapturingHttpServletRequestWrapper req, final CapturingHttpServletResponseWrapper resp, final Exception ex) {
        try {
            final String errorId = getErrorId(resp);
            final String logText = buildLogString(errorId, req, req.getBuffer().toByteArray(),
                    resp, resp.getBuffer().toByteArray(), ex);
            log(errorId, logText);
        }
        catch (final Exception e) {
            LOG.error("Exception trying to log error!!", e);
        }
    }

    /**
     * @return unique error id
     */
    public String getErrorId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Note. If {@value #ERROR_ID_HEADER_NAME} is already set in the supplied response, it will be used.
     * Otherwise, a new error id is generated.
     *
     * @param response  set {@value #ERROR_ID_HEADER_NAME} header to response, if supplied.
     * @return error id
     */
    public String getErrorId(final HttpServletResponse response) {
        final String errorId;
        if (response == null) {
            errorId = getErrorId();
        }
        else {
            final String headerErrorId = response.getHeader(ERROR_ID_HEADER_NAME);
            if (headerErrorId == null) {
                errorId = getErrorId();
                response.setHeader(ERROR_ID_HEADER_NAME, errorId);
            }
            else {
                errorId = headerErrorId;
            }
        }
        return errorId;
    }

    public String buildLogString(final String errorId, final HttpServletRequest req, final byte[] reqBody,
            final HttpServletResponse resp, final byte[] respBody,
            final Exception ex) {
        final StringBuilder ret = new StringBuilder();
        ret.append("ERROR ID: ").append(errorId)
           .append("\n\nTHREAD: ").append(Thread.currentThread())
           .append("\n\nREQUEST:\n")
           .append(servletToStringUtil.toString(req))
           .append("\n\nREQUEST BODY:\n")
           .append(reqBody == null ? ToStringUtil.NULL_STRING : new String(reqBody));
        ret.append("\n\nRESPONSE:\n")
           .append(servletToStringUtil.toString(resp))
           .append("\n\nRESPONSE BODY:\n")
           .append(respBody == null ? ToStringUtil.NULL_STRING : new String(respBody));
        final HttpSession sess = req.getSession(false);
        ret.append("\n\nSESSION:\n")
           .append(sess == null ? ToStringUtil.NULL_STRING : servletToStringUtil.toString(sess));
        ret.append("\n\nEXCEPTION:\n")
           .append(ex == null ? ToStringUtil.NULL_STRING : ExceptionUtils.getStackTrace(ex));
        ret.append("\n");
        return ret.toString();
    }

    public void log(final String errorId, final String logText) {
        try {
            errorLogger.log(errorId, logText);
        }
        catch (Exception e) {
            LOG.error("Exception trying to log error!!", e);
            LOG.error(logText);
        }
    }

}
