package com.gymcrm.bdd.integration.config;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@Configuration
public class WireMockConfig {

    private WireMockServer wireMockServer;

    @PostConstruct
    public void startWireMock() {
        wireMockServer = new WireMockServer(
                WireMockConfiguration.wireMockConfig().dynamicPort()
        );
        wireMockServer.start();

        System.setProperty(
                "training-workload.service.url",
                "http://localhost:" + wireMockServer.port()
        );

        configureDefaultStubs();
    }

    private void configureDefaultStubs() {
        wireMockServer.stubFor(post(urlPathMatching("/api/workload.*"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\":\"success\"}")));

        wireMockServer.stubFor(delete(urlPathMatching("/api/workload.*"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\":\"deleted\"}")));
    }

    @Bean
    public WireMockServer wireMockServer() {
        return wireMockServer;
    }

    @PreDestroy
    public void stopWireMock() {
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.stop();
        }
        System.clearProperty("training-workload.service.url");
    }
}