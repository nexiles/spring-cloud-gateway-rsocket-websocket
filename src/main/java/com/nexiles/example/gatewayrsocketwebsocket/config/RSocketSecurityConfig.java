package com.nexiles.example.gatewayrsocketwebsocket.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.rsocket.EnableRSocketSecurity;
import org.springframework.security.config.annotation.rsocket.RSocketSecurity;
import org.springframework.security.messaging.handler.invocation.reactive.AuthenticationPrincipalArgumentResolver;
import org.springframework.security.rsocket.api.PayloadExchange;
import org.springframework.security.rsocket.api.PayloadExchangeType;
import org.springframework.security.rsocket.api.PayloadInterceptor;
import org.springframework.security.rsocket.api.PayloadInterceptorChain;
import org.springframework.security.rsocket.core.PayloadSocketAcceptorInterceptor;
import org.springframework.util.MimeType;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
@EnableRSocketSecurity
@EnableReactiveMethodSecurity
public class RSocketSecurityConfig {

    @Bean
    public RSocketMessageHandler messageHandler(RSocketStrategies strategies) {
        RSocketMessageHandler handler = new RSocketMessageHandler();
        handler.getArgumentResolverConfigurer().addCustomResolver(new AuthenticationPrincipalArgumentResolver());
        handler.setRSocketStrategies(strategies);
        return handler;
    }

    @Bean
    public PayloadSocketAcceptorInterceptor authorization(RSocketSecurity security) {
        security.authorizePayload(authorize -> authorize.anyExchange().authenticated())
                .addPayloadInterceptor(this.payLoadInterceptor())
                .simpleAuthentication(Customizer.withDefaults())
//                .jwt(Customizer.withDefaults())
        ;
        return security.build();
    }

    private PayloadInterceptor payLoadInterceptor() {
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

            return Mono.empty();
        };
    }

}
