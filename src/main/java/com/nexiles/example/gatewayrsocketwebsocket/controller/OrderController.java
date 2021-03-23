package com.nexiles.example.gatewayrsocketwebsocket.controller;

import com.nexiles.example.gatewayrsocketwebsocket.OrderCreator;
import com.nexiles.example.gatewayrsocketwebsocket.config.RSocketConfig;
import com.nexiles.example.gatewayrsocketwebsocket.config.SecurityConstants;
import com.nexiles.example.gatewayrsocketwebsocket.events.NewOrderEvent;
import com.nexiles.example.gatewayrsocketwebsocket.pojo.CustomMetadata;
import com.nexiles.example.gatewayrsocketwebsocket.pojo.Order;
import com.nexiles.example.gatewayrsocketwebsocket.pojo.RSocketUser;
import com.nexiles.example.gatewayrsocketwebsocket.states.OrderKind;
import com.nexiles.example.gatewayrsocketwebsocket.utility.RSocketAuthUser;
import com.nexiles.example.gatewayrsocketwebsocket.utility.SecurityUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.security.Principal;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/server/orders")
public class OrderController {

    private final OrderCreator orderCreator;

    private final Sinks.Many<Order> orderSink;

    private final SecurityUtility securityUtility;

    public OrderController(OrderCreator orderCreator, SecurityUtility securityUtility) {
        this.orderCreator = orderCreator;
        this.orderSink = Sinks.many().multicast().directBestEffort(); // Publish new order to new subscribers and do not delay
        this.securityUtility = securityUtility;
    }

    @EventListener
    public void newOrderEvent(NewOrderEvent e) {
        final Order order = e.getOrder();
        log.info("New order event: {}", order.toString());
        orderSink.tryEmitNext(order);
    }

    @GetMapping("/new")
    public Mono<Order> createNewOrder(@RequestParam(value = "kind", required = false) String identifier, Principal principal) {

        final SecurityUtility.Provider provider = securityUtility.resolveSecurityProvider(principal);

        final Order order;
        if (identifier != null) {
            final OrderKind orderKind = OrderKind.byIdentifier(identifier);
            order = orderCreator.createOrderByKind(orderKind);
        } else {
            order = orderCreator.createRandomOrder();
        }

        log.info("New order request from '{}' and provider '{}': {}", securityUtility.resolveUserName(principal), provider, order);
        orderSink.tryEmitNext(order);
        return Mono.just(order);
    }

    @GetMapping(produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Order> getOrders() {
        return Flux.from(orderSink.asFlux());
    }

    private static final String DESTINATION_KEY = "lookupDestination";
    private static final String RSOCKET_FRAME_TYPE_KEY = "rsocketFrameType";
    private static final String CONTENT_TYPE_KEY = "contentType";

    @MessageMapping(value = "orders.{kind}")
    public Flux<Order> subscribeToOrders(@RSocketAuthUser RSocketUser rSocketUser,
                                         @Headers Map<String, Object> metadata,
                                         @Payload(required = false) Map<String, String> payload,
                                         @DestinationVariable("kind") String identifier) {

        logUserHeadersAndPayload(rSocketUser, metadata, payload, String.format("New RSocket connection to route: '%s' - kind: '%s'", "orders", identifier));
        final OrderKind requestedOrderKind = OrderKind.byIdentifier(identifier);

        final Set<GrantedAuthority> grantedAuthorities = rSocketUser.getAuthorities(); // There should/must be only one
        if (grantedAuthorities.isEmpty()) // but handle...
            throw new AccessDeniedException("User has no authorities!");

        final Set<String> authorities = grantedAuthorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());

        return Flux.from(orderSink.asFlux().filter(order ->
                // 'admin' gets requested no matter what
                (authorities.contains(SecurityConstants.ADMIN_ROLE) && order.getKind().equals(requestedOrderKind)) ||
                        // 'frodo' gets only LOTR orders
                        (requestedOrderKind.equals(OrderKind.LOTR) && authorities.contains(SecurityConstants.LOTR_ROLE) && order.getKind().equals(requestedOrderKind)) ||
                        // 'john' gets only GOT order
                        (requestedOrderKind.equals(OrderKind.GOT) && authorities.contains(SecurityConstants.GOT_ROLE) && order.getKind().equals(requestedOrderKind))
        ));
    }

    @SuppressWarnings("unused")
    @ConnectMapping
    public void rSocketConnect(@RSocketAuthUser RSocketUser rsocketUser,
                               @Headers Map<String, Object> metadata,
                               @Payload(required = false) Map<String, String> payload) {

        logUserHeadersAndPayload(rsocketUser, metadata, payload, "New RSocket connection");
    }

    private void logUserHeadersAndPayload(RSocketUser user, Map<String, Object> metadata,
                                          Map<String, String> payload, String reason) {

        final Object destination = metadata.getOrDefault(DESTINATION_KEY, null);
        final Object frameType = metadata.getOrDefault(RSOCKET_FRAME_TYPE_KEY, null);
        final Object contentType = metadata.getOrDefault(CONTENT_TYPE_KEY, null);

        log.debug("");
        log.debug(" - " + reason);
        log.debug("User: '{}' Role/s: '{}'", user.getUserName(), user.getAuthorities());
        log.debug("IdentityProvider: '{}'", user.getProvider());
        log.debug("Destination: '{}'", destination != null && !destination.toString().equals("") ? destination : "unknown");
        log.debug("FrameType: '{}'", frameType != null ? frameType : "unknown");
        log.debug("ContentType: '{}'", contentType != null ? contentType : "unknown");

        if (payload != null) {
            log.debug("Payload: '{}'", payload);
        }

        final Object customMeta = metadata.getOrDefault(RSocketConfig.CUSTOM_META_KEY, null);
        if (customMeta != null) {
            log.debug("CustomMetadata: '{}'", ((CustomMetadata) customMeta).getData());
        }

        log.debug("");
    }

}
