package org.demo.embedded;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

public class LoggingFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
        System.out.println("LoggingFilter initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        System.out.println("Incoming request: " + httpRequest.getMethod() + " " + httpRequest.getRequestURI());
        chain.doFilter(request, response); // 继续后续处理
    }

    @Override
    public void destroy() {
        System.out.println("LoggingFilter destroyed");
    }
}
