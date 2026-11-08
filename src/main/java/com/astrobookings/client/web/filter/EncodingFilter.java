package com.astrobookings.client.web.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Forces UTF-8 on every request and response, so launch names with accents
 * (for example {@code Cuisine}) render correctly.
 */
@WebFilter(filterName = "EncodingFilter", urlPatterns = "/*")
public class EncodingFilter implements Filter {

    private static final String ENCODING = StandardCharsets.UTF_8.name();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request.getCharacterEncoding() == null) {
            request.setCharacterEncoding(ENCODING);
        }
        response.setCharacterEncoding(ENCODING);
        chain.doFilter(request, response);
    }
}
