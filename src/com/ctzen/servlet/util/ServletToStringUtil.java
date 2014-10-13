package com.ctzen.servlet.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.http.HttpStatus;

import com.ctzen.common.tostring.ToStringUtil;
import com.ctzen.common.tostring.style.MultiLineIndentToStringStyle;

/**
 * toString() helpers for various HTTP parts.
 *
 * @author cchang
 */
public class ServletToStringUtil {

    /**
     * Constructor.
     *
     * @param requestAttributesFilter   exclude request attributes starting with these
     * @param sessionAttributesFilter   exclude session attributes starting with these
     */
    public ServletToStringUtil(final Set<String> requestAttributesFilter, final Set<String> sessionAttributesFilter) {
        if (requestAttributesFilter == null) {
            this.requestAttributesFilter = Collections.emptySet();
        }
        else {
            this.requestAttributesFilter = requestAttributesFilter;
        }
        if (sessionAttributesFilter == null) {
            this.sessionAttributesFilter = Collections.emptySet();
        }
        else {
            this.sessionAttributesFilter = requestAttributesFilter;
        }
    }

    private final Set<String> requestAttributesFilter;

    private final Set<String> sessionAttributesFilter;

    private boolean isFiltered(final String name, final Set<String> filters) {
        if (name == null) {
            return false;
        }
        for (String filter: filters) {
            if (name.startsWith(filter)) {
                return true;
            }
        }
        return false;
    }

    public String toString(final HttpServletRequest req) {
        final ToStringBuilder tsb = new ToStringBuilder(req, MultiLineIndentToStringStyle.STYLE);
        tsb.append("requestURL", req.getRequestURL())
           .append("authType", req.getAuthType())
           .append("characterEncoding", req.getCharacterEncoding())
           .append("contentLength", req.getContentLengthLong())
           .append("contentType", req.getContentType())
           .append("contextPath", req.getContextPath())
           .append("dispatcherType", req.getDispatcherType())
           .append("localAddr", req.getLocalAddr())
           .append("localName", req.getLocalName())
           .append("localPort", req.getLocalPort())
           .append("locale", req.getLocale())
           .append("locales", ToStringUtil.toString(req.getLocales()))
           .append("method", req.getMethod())
           .append("pathInfo", req.getPathInfo())
           .append("pathTranslated", req.getPathTranslated())
           .append("protocol", req.getProtocol())
           .append("queryString", req.getQueryString())
           .append("remoteAddr", req.getRemoteAddr())
           .append("remoteHost", req.getRemoteHost())
           .append("remotePort", req.getRemotePort())
           .append("remoteUser", req.getRemoteUser())
           .append("requestURI", req.getRequestURI())
           .append("requestedSessionId", req.getRequestedSessionId())
           .append("scheme", req.getScheme())
           .append("serverName", req.getServerName())
           .append("serverPort", req.getServerPort())
           .append("servletPath", req.getServletPath())
           .append("userPrincipal", req.getUserPrincipal())
           .append("isAsyncStarted", req.isAsyncStarted())
           .append("isAsyncSupported", req.isAsyncSupported())
           .append("isRequestedSessionIdFromCookie", req.isRequestedSessionIdFromCookie())
           .append("isRequestedSessionIdFromURL", req.isRequestedSessionIdFromURL())
           .append("isRequestedSessionIdValid", req.isRequestedSessionIdValid())
           .append("isSecure", req.isSecure())
//         .append("parameterMap", ToStringUtil.toString(req.getParameterMap(), MultiLineIndentToStringStyle.STYLE_2))
           .append("parameters", parametersToString(req, MultiLineIndentToStringStyle.STYLE_2))
           .append("headers", headersToString(req, MultiLineIndentToStringStyle.STYLE_2))
           .append("cookies", toString(req.getCookies(), MultiLineIndentToStringStyle.STYLE_2))
           .append("asyncContext", req.getAsyncContext())
           .append("attributes", attributesToString(req, MultiLineIndentToStringStyle.STYLE_2));
        return tsb.toString();
    }

    public String toString(final HttpServletResponse resp) {
        final ToStringBuilder tsb = new ToStringBuilder(resp, MultiLineIndentToStringStyle.STYLE);
        tsb.append("bufferSize", resp.getBufferSize())
           .append("characterEncoding", resp.getCharacterEncoding())
           .append("contentType", resp.getContentType())
           .append("isCommitted", resp.isCommitted())
           .append("locale", resp.getLocale())
           .append("status", responseStatusToString(resp.getStatus()))
           .append("headers", headersToString(resp, MultiLineIndentToStringStyle.STYLE_2));
        return tsb.toString();
    }

    public String toString(final HttpSession sess) {
        final ToStringBuilder tsb = new ToStringBuilder(sess, MultiLineIndentToStringStyle.STYLE);
        tsb.append("id", sess.getId())
           .append("creationTime", sess.getCreationTime() + " (" + new Date(sess.getCreationTime()) + ")")
           .append("lastAccessedTime", sess.getLastAccessedTime() + " (" + new Date(sess.getLastAccessedTime()) + ")")
           .append("maxInactiveInterval", sess.getMaxInactiveInterval())
           .append("attributes", attributesToString(sess, MultiLineIndentToStringStyle.STYLE_2));
        return tsb.toString();
    }

    public String headersToString(final HttpServletRequest req, final ToStringStyle style) {
        final Enumeration<String> names = req.getHeaderNames();
        if (names == null) {
            return ToStringUtil.NULL_STRING;
        }
        final ToStringBuilder tsb = new ToStringBuilder(names, style);
        final Set<String> sortedNames = new TreeSet<>(Collections.list(names));
        for (final String name: sortedNames) {
            tsb.append(name, ToStringUtil.toString(req.getHeaders(name), true));
        }
        return tsb.toString();
    }

    public String headersToString(final HttpServletResponse resp, final ToStringStyle style) {
        final Collection<String> names = resp.getHeaderNames();
        if (names == null) {
            return ToStringUtil.NULL_STRING;
        }
        final ToStringBuilder tsb = new ToStringBuilder(names, style);
        final Set<String> sortedNames = new TreeSet<>(names);
        for (final String name: sortedNames) {
            tsb.append(name, ToStringUtil.toString(resp.getHeaders(name), true));
        }
        return tsb.toString();
    }

    public String parametersToString(final HttpServletRequest req, final ToStringStyle style) {
        final Enumeration<String> names = req.getParameterNames();
        if (names == null) {
            return ToStringUtil.NULL_STRING;
        }
        final ToStringBuilder tsb = new ToStringBuilder(names, style);
        final Set<String> sortedNames = new TreeSet<>(Collections.list(names));
        for (final String name: sortedNames) {
            tsb.append(name, ToStringUtil.toString(req.getParameterValues(name), true));
        }
        return tsb.toString();
    }

    public String attributesToString(final HttpServletRequest req, final ToStringStyle style) {
        final Enumeration<String> names = req.getAttributeNames();
        if (names == null) {
            return ToStringUtil.NULL_STRING;
        }
        final ToStringBuilder tsb = new ToStringBuilder(names, style);
        final Set<String> sortedNames = new TreeSet<>(Collections.list(names));
        for (final String name: sortedNames) {
            if (!isFiltered(name, requestAttributesFilter)) {
                tsb.append(name, req.getAttribute(name));
            }
        }
        return tsb.toString();
    }

    public String attributesToString(final HttpSession sess, final ToStringStyle style) {
        final Enumeration<String> names = sess.getAttributeNames();
        if (names == null) {
            return ToStringUtil.NULL_STRING;
        }
        final ToStringBuilder tsb = new ToStringBuilder(names, style);
        final Set<String> sortedNames = new TreeSet<>(Collections.list(names));
        for (final String name: sortedNames) {
            if (!isFiltered(name, sessionAttributesFilter)) {
                tsb.append(name, sess.getAttribute(name));
            }
        }
        return tsb.toString();
    }

    public String toString(final Cookie[] cookies, final ToStringStyle style) {
        if (cookies == null) {
            return ToStringUtil.NULL_STRING;
        }
        final ToStringBuilder tsb = new ToStringBuilder(cookies, style);
        final ToStringStyle style2;
        if (style instanceof MultiLineIndentToStringStyle) {
            style2 = MultiLineIndentToStringStyle.get(((MultiLineIndentToStringStyle)style).getLevel() + 1);
        }
        else {
            style2 = style;
        }
        final Map<String, Cookie> sortedCookies = new TreeMap<>();
        for (final Cookie cookie: cookies) {
            sortedCookies.put(cookie.getName(), cookie);
        }
        for (final Cookie cookie: sortedCookies.values()) {
            tsb.append(toString(cookie, style2));
        }
        return tsb.toString();
    }

    public String toString(final Cookie cookie, final ToStringStyle style) {
        if (cookie == null) {
            return ToStringUtil.NULL_STRING;
        }
        final ToStringBuilder tsb = new ToStringBuilder(cookie, style);
        tsb.append("name", cookie.getName())
           .append("value", cookie.getValue())
           .append("comment", cookie.getComment())
           .append("domain", cookie.getDomain())
           .append("maxAge", cookie.getMaxAge())
           .append("path", cookie.getPath())
           .append("secure", cookie.getSecure())
           .append("version", cookie.getVersion())
           .append("isHttpOnly", cookie.isHttpOnly());
        return tsb.toString();
    }

    public String responseStatusToString(final int status) {
        String ret = Integer.toString(status);
        try {
            final HttpStatus hs = HttpStatus.valueOf(status);
            ret += " - " + hs.name();
        }
        catch (final IllegalArgumentException e) {}
        return ret;
    }

}
