# servlet-error-logger

A [Spring MVC](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/mvc.html) request and response body capturing, error logging servlet filter.

## Motivation

Helps diagnose why that 500 internal server error happened.

## Usage

### Gradle Dependencies

```groovy
    compile 'com.ctzen:servlet-error-logger:1.0'
```

which in turn requires [ctzen-common](https://github.com/ctzen/ctzen-common), and [ctzen-test](https://github.com/ctzen/ctzen-test). The latter is required to unit-test this project. 

### Spring Context Setup

Install a [DelegatingFilterProxy](http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/filter/DelegatingFilterProxy.html) which points to a `ErrorLoggingFilter`.

```java
@Bean
public ErrorLoggingFilter errorLoggingFilter(
    ServletToStringUtil servletToStringUtil, 
    ErrorLogger errorLogger) {

    Set<Integer> responseStatusCodesToLog = new HashSet<Integer>();
    responseStatusCodesToLog.add(HttpStatus.INTERNAL_SERVER_ERROR.value());

    ExceptionGetter exceptionGetter = 
        new RequestAttributeExceptionGetter(DispatcherServlet.EXCEPTION_ATTRIBUTE);

    return new ErrorLoggingFilter(
        responseStatusCodesToLog,
        exceptionGetter,
        servletToStringUtil,
        errorLogger);
}

@Bean
public ServletToStringUtil servletToStringUtil() {
    Set<String> requestAttributeFilter = new HashSet<String>();
    Set<String> sessionAttributeFilter = new HashSet<String>();
    return new ServletToStringUtil(requestAttributeFilter, sessionAttributeFilter);
}

@Bean
FileErrorLogger errorLogger() {
    new FileErrorLogger(new File('/var/log/webapp-errors'))
}
```

## Operation

The `ErrorLoggingFilter` wraps the request and response with a version capbable of capturing the bodies, chain, and if the response status code is one of `responseStatusCodesToLog` (typically 500), builds and log the request, response, session, and any exception thrown.  Any uncaught exception during chaining also triggers the logging action.

See the unit test sources for usage patterns.

