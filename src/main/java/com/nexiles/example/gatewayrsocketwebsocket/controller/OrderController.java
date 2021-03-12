package com.nexiles.example.gatewayrsocketwebsocket.controller;

import com.nexiles.example.gatewayrsocketwebsocket.OrderCreator;
import com.nexiles.example.gatewayrsocketwebsocket.config.RSocketConfig;
import com.nexiles.example.gatewayrsocketwebsocket.events.NewOrderEvent;
import com.nexiles.example.gatewayrsocketwebsocket.pojo.CustomMetadata;
import com.nexiles.example.gatewayrsocketwebsocket.pojo.Order;
import com.nexiles.example.gatewayrsocketwebsocket.states.OrderKind;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/server/orders")
public class OrderController {

    private final OrderCreator orderCreator;

    private final Sinks.Many<Order> orderSink;

    public OrderController(OrderCreator orderCreator) {
        this.orderCreator = orderCreator;
        this.orderSink = Sinks.many().multicast().directBestEffort(); // Publish new order to new subscribers and do not delay
    }

    @EventListener
    public void newOrderEvent(NewOrderEvent e) {
        final Order order = e.getOrder();
        log.info("New order event: {}", order.toString());
        orderSink.tryEmitNext(order);
    }

    @GetMapping("/new")
    public Mono<Order> createNewOrder(@RequestParam(value = "kind", required = false) String identifier) {

        final Order order;
        if (identifier != null) {
            final OrderKind orderKind = OrderKind.byIdentifier(identifier);
            order = orderCreator.createOrderByKind(orderKind);
        } else {
            order = orderCreator.createRandomOrder();
        }

        log.info("New order request: {}", order.toString());
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
    public Flux<Order> getOrderEvents(@Headers Map<String, Object> metadata,
                                      @Payload(required = false) Map<String, String> payload,
                                      @DestinationVariable("kind") String identifier) {

        logHeadersAndPayload(metadata, payload, String.format("New RSocket connection to route: '%s' - kind: '%s'", "orders", identifier));
        final OrderKind orderKind = OrderKind.byIdentifier(identifier);
        return Flux.from(orderSink.asFlux().filter(order -> orderKind.equals(OrderKind.ALL) || order.getKind().equals(orderKind)));
    }

    @SuppressWarnings("unused")
    @ConnectMapping
    public void rSocketConnect(@Headers Map<String, Object> metadata, @Payload(required = false) Map<String, String> payload) {
        logHeadersAndPayload(metadata, payload, "New RSocket connection");
    }

    private void logHeadersAndPayload(Map<String, Object> metadata, Map<String, String> payload, String reason) {

        final Object destination = metadata.getOrDefault(DESTINATION_KEY, null);
        final Object frameType = metadata.getOrDefault(RSOCKET_FRAME_TYPE_KEY, null);
        final Object contentType = metadata.getOrDefault(CONTENT_TYPE_KEY, null);

        log.debug("");
        log.debug(" - " + reason);
        log.debug("Destination: {}", destination != null && !destination.toString().equals("") ? destination.toString() : "unknown");
        log.debug("FrameType: {}", frameType != null ? frameType.toString() : "unknown");
        log.debug("ContentType: {}", contentType != null ? contentType.toString() : "unknown");

        if (payload != null) {
            log.debug("Payload: {}", payload.toString());
        }

        final Object customMeta = metadata.getOrDefault(RSocketConfig.CUSTOM_META_KEY, null);
        if (customMeta != null) {
            log.debug("CustomMetadata: {}", ((CustomMetadata) customMeta).getData());
        }

        log.debug("");
    }

}
