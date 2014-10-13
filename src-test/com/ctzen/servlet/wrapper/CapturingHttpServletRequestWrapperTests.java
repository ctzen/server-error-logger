package com.ctzen.servlet.wrapper;

import static com.ctzen.test.spring.web.servlet.result.MockMvcResultHandlers.reporter;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.ByteArrayOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testng.annotations.Test;

import com.ctzen.servlet.WebAppConfig;
import com.ctzen.servlet.errorlogger.ErrorLoggingFilter;

/**
 * @author cchang
 */
@WebAppConfiguration
@ContextConfiguration(classes = WebAppConfig.class)
@Test
public class CapturingHttpServletRequestWrapperTests extends AbstractTestNGSpringContextTests {

    @Autowired
    private MockMvc mockMvc;

    public void captureByStream() throws Exception {
        final String content = "READ BY STREAM.";
        final MvcResult r = mockMvc.perform(get("/read-by-stream").content(content))
                .andDo(reporter())
                .andExpect(status().isOk())
                .andExpect(content().string(content))
                .andReturn();
        final ByteArrayOutputStream captured = (ByteArrayOutputStream)r.getRequest().getAttribute(ErrorLoggingFilter.REQUEST_BODY_ATTRIBUTE);
        assertThat(captured.toByteArray()).isEqualTo(content.getBytes());
    }

    public void captureByReader() throws Exception {
        final String content = "READ BY READER.";
        final MvcResult r = mockMvc.perform(get("/read-by-reader").content(content))
                .andDo(reporter())
                .andExpect(status().isOk())
                .andExpect(content().string(content))
                .andReturn();
        final ByteArrayOutputStream captured = (ByteArrayOutputStream)r.getRequest().getAttribute(ErrorLoggingFilter.REQUEST_BODY_ATTRIBUTE);
        assertThat(captured.toByteArray()).isEqualTo(content.getBytes());
    }

}
