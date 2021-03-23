package com.nexiles.example.gatewayrsocketwebsocket.config;

import com.nexiles.example.gatewayrsocketwebsocket.pojo.CustomMetadata;
import com.nexiles.example.gatewayrsocketwebsocket.utility.RSocketAuthUserArgumentResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.rsocket.messaging.RSocketStrategiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.security.rsocket.api.PayloadExchange;
import org.springframework.security.rsocket.api.PayloadExchangeType;
import org.springframework.security.rsocket.api.PayloadInterceptor;
import org.springframework.security.rsocket.api.PayloadInterceptorChain;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

@Slf4j
@Configuration(proxyBeanMethods = false)
public class RSocketConfig {

    public static final String CUSTOM_META_KEY = "custom-meta";

    @Bean
    public RSocketMessageHandler messageHandler(RSocketStrategies strategies) {
        RSocketMessageHandler handler = new RSocketMessageHandler();
        handler.getArgumentResolverConfigurer().addCustomResolver(new RSocketAuthUserArgumentResolver());
        handler.setRSocketStrategies(strategies);
        return handler;
    }

    /**
     * Just for demonstration purpose.
     *
     * @return {@code Mono<Void>} to indicate when processing is complete
     */
    public static PayloadInterceptor payLoadInterceptor() {
        return (PayloadExchange exchange, PayloadInterceptorChain chain) -> {
            final PayloadExchangeType exchangeType = exchange.getType();
            final MimeType metadataMimeType = exchange.getMetadataMimeType();
            final MimeType dataMimeType = exchange.getDataMimeType();

            log.debug("");
            log.debug("- RSocketRequest");
            log.debug("Exchange: {}", exchangeType);
            log.debug("MedataMimeType: {}", metadataMimeType);
            log.debug("DataMimeType: {}", dataMimeType);
            log.debug("");

            return chain.next(exchange);
        };
    }

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
