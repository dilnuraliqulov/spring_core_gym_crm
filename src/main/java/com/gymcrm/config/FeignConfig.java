package com.gymcrm.config;

import feign.Logger;
import feign.RequestInterceptor;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    private static final String TRANSACTION_ID = "transactionId";

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public RequestInterceptor transactionIdInterceptor() {
        return requestTemplate -> {
            String transactionId = MDC.get(TRANSACTION_ID);
            if (transactionId != null) {
                requestTemplate.header("X-Transaction-ID", transactionId);
            }
        };
    }
}

