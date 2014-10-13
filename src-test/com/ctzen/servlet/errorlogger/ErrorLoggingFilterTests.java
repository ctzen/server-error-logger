package com.ctzen.servlet.errorlogger;

import static com.ctzen.test.spring.web.servlet.result.MockMvcResultHandlers.reporter;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.hamcrest.Matchers.anything;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.NestedServletException;
import org.testng.annotations.Test;

import com.ctzen.servlet.WebAppConfig;

/**
 * @author cchang
 */
@WebAppConfiguration
@ContextConfiguration(classes = WebAppConfig.class)
@Test
public class ErrorLoggingFilterTests extends AbstractTestNGSpringContextTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ErrorLoggingFilter errorLoggingFilter;

    public void filterLogged() throws Exception {
        final String content = "THROW ME!";
        final MvcResult r = mockMvc.perform(get("/illegal-monitor-state-exception").content(content))
                .andDo(reporter())
                .andExpect(status().isInternalServerError())
                .andExpect(header().string("X-Handler", "CATCH_ALL"))
                .andExpect(header().string(ErrorLoggingFilter.ERROR_ID_HEADER_NAME, anything()))
                .andExpect(content().string(content))
                .andReturn();
        assertThat(r.getRequest().getAttribute(ErrorLoggingFilter.DONT_LOG_ATTRIBUTE)).isNull();
        final String errorId = r.getResponse().getHeader(ErrorLoggingFilter.ERROR_ID_HEADER_NAME);
        final File logFile = new File(((FileErrorLogger)errorLoggingFilter.getErrorLogger()).getFolder(), errorId);
        assertThat(logFile).exists();
    }

    public void handledAndFilterLogged() throws Exception {
        final String content = "THROW ME!";
        final MvcResult r = mockMvc.perform(get("/illegal-argument-exception").content(content))
                .andDo(reporter())
                .andExpect(status().isInternalServerError())
                .andExpect(header().string("X-Handler", "ILLEGAL_ARGUMENT"))
                .andExpect(header().string(ErrorLoggingFilter.ERROR_ID_HEADER_NAME, anything()))
                .andExpect(content().string("HELLO" + content))
                .andReturn();
        assertThat(r.getRequest().getAttribute(ErrorLoggingFilter.DONT_LOG_ATTRIBUTE)).isNull();
        final String errorId = r.getResponse().getHeader(ErrorLoggingFilter.ERROR_ID_HEADER_NAME);
        final File logFile = new File(((FileErrorLogger)errorLoggingFilter.getErrorLogger()).getFolder(), errorId);
        assertThat(logFile).exists();
    }

    public void notFilterLogged() throws Exception {
        final String content = "THROW ME!";
        final MvcResult r = mockMvc.perform(get("/illegal-state-exception").content(content))
                .andDo(reporter())
                .andExpect(status().isInternalServerError())
                .andExpect(header().string("X-Handler", "ILLEGAL_STATE"))
                .andExpect(header().string(ErrorLoggingFilter.ERROR_ID_HEADER_NAME, anything()))
                .andExpect(content().string(content))
                .andReturn();
        assertThat(r.getRequest().getAttribute(ErrorLoggingFilter.DONT_LOG_ATTRIBUTE)).isNotNull();
    }

    public void handledAndLogged() throws Exception {
        final String content = "THROW ME!";
        final MvcResult r = mockMvc.perform(get("/illegal-access-exception").content(content))
                .andDo(reporter())
                .andExpect(status().isInternalServerError())
                .andExpect(header().string("X-Handler", "ILLEGAL_ACCESS"))
                .andExpect(header().string(ErrorLoggingFilter.ERROR_ID_HEADER_NAME, anything()))
                .andExpect(content().string(content))
                .andReturn();
        assertThat(r.getRequest().getAttribute(ErrorLoggingFilter.DONT_LOG_ATTRIBUTE)).isNotNull();
        final String errorId = r.getResponse().getHeader(ErrorLoggingFilter.ERROR_ID_HEADER_NAME);
        final File logFile = new File(((FileErrorLogger)errorLoggingFilter.getErrorLogger()).getFolder(), errorId);
        assertThat(logFile).exists();
    }

    public void unhandled() throws Exception {
        final String content = "THROW ME!";
        NestedServletException caught = null;
        try {
            mockMvc.perform(get("/unhandled-exception").content(content));
        }
        catch (NestedServletException e) {
            caught = e;
        }
        assertThat(caught).isNotNull();
        @SuppressWarnings("null")
        final Throwable cause = caught.getCause();
        assertThat(cause).isInstanceOf(RuntimeException.class);
        assertThat(cause.getLocalizedMessage()).isEqualTo(content);
        // can't check log exists unless knows how to get to the response object
    }

}
