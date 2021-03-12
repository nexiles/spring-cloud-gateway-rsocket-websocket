package com.nexiles.example.gatewayrsocketwebsocket.config;

import com.nexiles.example.gatewayrsocketwebsocket.pojo.CustomMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.rsocket.messaging.RSocketStrategiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

@Slf4j
@Configuration(proxyBeanMethods = false)
public class RSocketConfig {

    public static final String CUSTOM_META_KEY = "custom-meta";

    @SuppressWarnings("CodeBlock2Expr")
    @Bean
    @Order(2) // After AutoConfiguration
    public RSocketStrategiesCustomizer configure() {
        return (strategy) -> {
            strategy.metadataExtractorRegistry(registry -> {
                registry.metadataToExtract(
                        MimeType.valueOf(MimeTypeUtils.APPLICATION_JSON_VALUE),
                        CustomMetadata.class, CUSTOM_META_KEY);
            });
        };
    }

}
