package com.gymcrm.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
public class TransactionLoggingFilter implements Filter {

    private static final String TRANSACTION_ID = "transactionId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Generate or retrieve transaction ID
        String transactionId = httpRequest.getHeader("X-Transaction-Id");
        if (transactionId == null || transactionId.isBlank()) {
            transactionId = UUID.randomUUID().toString();
        }

        // Put transaction ID in MDC for logging
        MDC.put(TRANSACTION_ID, transactionId);

        // Add transaction ID to response header
        httpResponse.setHeader("X-Transaction-Id", transactionId);

        long startTime = System.currentTimeMillis();

        try {
            // Log incoming request
            log.info("Incoming request: {} {} | Transaction: {}",
                    httpRequest.getMethod(),
                    httpRequest.getRequestURI(),
                    transactionId);

            chain.doFilter(request, response);

        } finally {
            long duration = System.currentTimeMillis() - startTime;

            // Log response
            log.info("Response: {} {} | Status: {} | Duration: {}ms | Transaction: {}",
                    httpRequest.getMethod(),
                    httpRequest.getRequestURI(),
                    httpResponse.getStatus(),
                    duration,
                    transactionId);

            // Clear MDC
            MDC.remove(TRANSACTION_ID);
        }
    }
}

