package com.astrobookings.client.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EncodingFilterTest {

    @Mock
    private ServletRequest request;

    @Mock
    private ServletResponse response;

    @Mock
    private FilterChain chain;

    @Test
    void forcesUtf8BeforeContinuingTheChain() throws Exception {
        when(request.getCharacterEncoding()).thenReturn(null);

        new EncodingFilter().doFilter(request, response, chain);

        verify(request).setCharacterEncoding("UTF-8");
        verify(response).setCharacterEncoding("UTF-8");
        verify(chain).doFilter(request, response);
    }

    @Test
    void keepsAnExplicitRequestEncoding() throws Exception {
        when(request.getCharacterEncoding()).thenReturn("ISO-8859-1");

        new EncodingFilter().doFilter(request, response, chain);

        verify(request, org.mockito.Mockito.never()).setCharacterEncoding(org.mockito.ArgumentMatchers.anyString());
        verify(response).setCharacterEncoding("UTF-8");
        verify(chain).doFilter(request, response);
    }
}
