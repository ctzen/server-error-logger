package com.ctzen.servlet

import static org.fest.assertions.api.Assertions.assertThat
import groovy.transform.CompileStatic

import javax.servlet.ServletInputStream
import javax.servlet.ServletOutputStream
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

import com.ctzen.servlet.errorlogger.ErrorLoggingFilter

/**
 * @author cchang
 */
@CompileStatic
@Controller
class TestController {

    @Autowired
    private ErrorLoggingFilter errorLoggingFilter

    @RequestMapping('/read-by-stream')
    ResponseEntity<String> readByStream(final HttpServletRequest req) {
        final byte[] buffer = new byte[1024]
        final ServletInputStream stream = req.inputStream
        final int read
        try {
            read = stream.read(buffer)
        }
        finally {
            stream.close()
        }
        new ResponseEntity<>(new String(buffer, 0, read), HttpStatus.OK)
    }

    @RequestMapping('/read-by-reader')
    ResponseEntity<String> readByReader(final HttpServletRequest req) {
        final BufferedReader reader = req.reader
        final String s
        try {
            s = reader.readLine()
        }
        finally {
            reader.close()
        }
        new ResponseEntity<>(s, HttpStatus.OK)
    }

    @RequestMapping('/write-by-stream')
    void writeByStream(@RequestBody final String text, final HttpServletResponse resp) {
        final ServletOutputStream stream = resp.outputStream
        try {
            stream.print(text)
            stream.write(text.bytes)
        }
        finally {
            stream.close()
        }
    }

    @RequestMapping('/write-by-writer')
    void writeByWriter(@RequestBody final String text, final HttpServletResponse resp) {
        final PrintWriter writer = resp.writer
        try {
            writer.print(text)
            writer.write(text)
        }
        finally {
            writer.close()
        }
    }

    @RequestMapping('/unhandled-exception')
    ResponseEntity<String> unhandledException(@RequestBody final String message) {
        throw new RuntimeException(message)
    }


    @RequestMapping('/illegal-monitor-state-exception')
    ResponseEntity<String> runtimeException(@RequestBody final String message) {
        throw new IllegalMonitorStateException(message)
    }

    @ExceptionHandler(IllegalMonitorStateException)
    public ResponseEntity<String> exceptionHandler(final Exception e, final HttpServletResponse resp) {
        resp.setHeader('X-Handler', 'CATCH_ALL')
        return new ResponseEntity<>(e.localizedMessage, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @RequestMapping('/illegal-argument-exception')
    ResponseEntity<String> illegalArgumentException(@RequestBody final String message, final HttpServletResponse resp) {
        resp.outputStream.write('HELLO'.bytes)
        throw new IllegalArgumentException(message)
    }

    @ExceptionHandler([IllegalArgumentException])
    public ResponseEntity<String> illegalArgumentExceptionHandler(final HttpServletRequest req, final HttpServletResponse resp,
        final Exception e) {
        resp.setHeader('X-Handler', 'ILLEGAL_ARGUMENT')
        final String errorId = errorLoggingFilter.getErrorId(resp)
        final ByteArrayOutputStream reqBody = (ByteArrayOutputStream)req.getAttribute(ErrorLoggingFilter.REQUEST_BODY_ATTRIBUTE)
        assert e.localizedMessage.bytes == reqBody.toByteArray()
        final ByteArrayOutputStream respBody = (ByteArrayOutputStream)req.getAttribute(ErrorLoggingFilter.RESPONSE_BODY_ATTRIBUTE)
        assert 'HELLO'.bytes == respBody.toByteArray()
        return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @RequestMapping('/illegal-state-exception')
    ResponseEntity<String> illegalStateException(@RequestBody final String message, final HttpServletResponse resp) {
        resp.writer.write('HELLO')
        throw new IllegalStateException(message)
    }

    @ExceptionHandler([IllegalStateException])
    public ResponseEntity<String> illegalStateExceptionHandler(final HttpServletRequest req, final HttpServletResponse resp,
        final Exception e) {
        resp.setHeader('X-Handler', 'ILLEGAL_STATE')
        req.setAttribute(ErrorLoggingFilter.DONT_LOG_ATTRIBUTE, true)
        return new ResponseEntity<>(e.localizedMessage, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @RequestMapping('/illegal-access-exception')
    ResponseEntity<String> illegalAccessException(@RequestBody final String message, final HttpServletResponse resp) {
        throw new IllegalAccessException(message)
    }

    @ExceptionHandler([IllegalAccessException])
    public ResponseEntity<String> illegalAccessExceptionHandler(final HttpServletRequest req, final HttpServletResponse resp,
        final Exception e) {
        resp.setHeader('X-Handler', 'ILLEGAL_ACCESS')
        req.setAttribute(ErrorLoggingFilter.DONT_LOG_ATTRIBUTE, true)
        final String errorId = errorLoggingFilter.getErrorId(resp)
        final ByteArrayOutputStream reqBody = (ByteArrayOutputStream)req.getAttribute(ErrorLoggingFilter.REQUEST_BODY_ATTRIBUTE)
        assert e.localizedMessage.bytes == reqBody.toByteArray()
        final ByteArrayOutputStream respBody = (ByteArrayOutputStream)req.getAttribute(ErrorLoggingFilter.RESPONSE_BODY_ATTRIBUTE)
        final String logText = errorLoggingFilter.buildLogString(errorId, req, reqBody.toByteArray(), resp, respBody.toByteArray(), e)
        errorLoggingFilter.log(errorId, logText)
        return new ResponseEntity<>(e.localizedMessage, HttpStatus.INTERNAL_SERVER_ERROR)
    }

}
