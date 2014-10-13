package com.ctzen.servlet

import groovy.transform.CompileStatic

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.filter.DelegatingFilterProxy
import org.springframework.web.servlet.DispatcherServlet
import org.springframework.web.servlet.config.annotation.EnableWebMvc

import com.ctzen.servlet.errorlogger.ErrorLogger;
import com.ctzen.servlet.errorlogger.ErrorLoggingFilter;
import com.ctzen.servlet.errorlogger.FileErrorLogger;
import com.ctzen.servlet.errorlogger.RequestAttributeExceptionGetter;
import com.ctzen.servlet.util.ServletToStringUtil

/**
 * @author cchang
 */
@CompileStatic
@EnableWebMvc
@Configuration
@ComponentScan(basePackageClasses = TestController)
class WebAppConfig {

    @Bean
    MockMvc mockMvc(final WebApplicationContext webApplicationContext, final ErrorLoggingFilter errorLoggingFilter) {
        MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .addFilters(new DelegatingFilterProxy(errorLoggingFilter))
            .build()
    }

    @Bean
    ErrorLoggingFilter errorLoggingFilter(final ServletToStringUtil servletToStringUtil, final ErrorLogger errorLogger) {
        new ErrorLoggingFilter(
            [HttpStatus.INTERNAL_SERVER_ERROR.value()],
            new RequestAttributeExceptionGetter(DispatcherServlet.EXCEPTION_ATTRIBUTE),
            servletToStringUtil,
            errorLogger);
    }

    @Bean
    ServletToStringUtil servletToStringUtil() {
        new ServletToStringUtil(
            [
                'org.springframework.core.convert.ConversionService',
                'org.springframework.web.servlet.resource.ResourceUrlProvider',
                'org.springframework.test.web.servlet.MockMvc.MVC_RESULT_ATTRIBUTE',
                'org.springframework.web.context.request.async.WebAsyncManager.WEB_ASYNC_MANAGER',
                'org.springframework.web.servlet.DispatcherServlet.CONTEXT',
                'org.springframework.web.servlet.DispatcherServlet.EXCEPTION',
                'org.springframework.web.servlet.DispatcherServlet.FLASH_MAP_MANAGER',
                'org.springframework.web.servlet.DispatcherServlet.LOCALE_RESOLVER',
                'org.springframework.web.servlet.DispatcherServlet.THEME_RESOLVER',
                'org.springframework.web.servlet.DispatcherServlet.THEME_SOURCE',
                'org.springframework.web.servlet.resource.ResourceUrlProvider'
            ] as Set,
            [
            ] as Set
        );
    }

    @Bean
    FileErrorLogger errorLogger() {
        new FileErrorLogger(new File('build/reports/webapp-errors'))
    }

}
