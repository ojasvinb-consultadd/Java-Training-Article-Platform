package com.ojasvinC.article_platform.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class RequestIdFilter extends OncePerRequestFilter {

    private static final String MDC_KEY = "requestId";
    private static final String HEADER_NAME = "X-Request-ID";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // 1. Extract existing ID from headers, or mint a shiny new UUID
            String requestId = request.getHeader(HEADER_NAME);
            if (requestId == null || requestId.isBlank()) {
                requestId = UUID.randomUUID().toString().substring(0, 8); // Shortened for cleaner logs
            }

            // 2. Stuff it into the MDC thread-local context
            MDC.put(MDC_KEY, requestId);

            // 3. Optional: Pass it back in the response headers so clients/postman can see it
            response.setHeader(HEADER_NAME, requestId);

            // 4. Continue down the filter chain (Security, Controllers, etc.)
            filterChain.doFilter(request, response);

        } finally {
            // 5. CRITICAL: Always clear the MDC when the thread finishes to prevent leakage!
            MDC.clear();
        }
    }
}