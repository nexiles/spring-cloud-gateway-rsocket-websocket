package com.nexiles.example.gatewayrsocketwebsocket.controller;

import com.nexiles.example.gatewayrsocketwebsocket.OrderCreator;
import com.nexiles.example.gatewayrsocketwebsocket.events.NewOrderEvent;
import com.nexiles.example.gatewayrsocketwebsocket.pojo.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/orders")
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
    public Mono<Order> createNewOrder() {
        final Order order = orderCreator.createOrder();
        log.info("New order request: {}", order.toString());
        orderSink.tryEmitNext(order);
        return Mono.just(order);
    }

    // http :8070/orders --stream
    @GetMapping(produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Order> getOrders() {
        return Flux.from(orderSink.asFlux());
    }

    private static final String DESTINATION_KEY = "lookupDestination";
    private static final String RSOCKET_FRAME_TYPE_KEY = "rsocketFrameType";
    private static final String CONTENT_TYPE_KEY = "contentType";

    // rsc --stream --route=orders --debug ws://localhost:8070/rsocket - 8070 since ws transport instead tcp
    @MessageMapping(value = "orders")
    public Flux<Order> getOrderEvents(@Headers Map<String, Object> metadata) {
        logHeaders(metadata, "New RSocket connection to route: 'orders'");
        return Flux.from(orderSink.asFlux());
    }

    @ConnectMapping
    public void rsocketConnect(@Headers Map<String, Object> metadata) {

        final Object destination = metadata.getOrDefault(DESTINATION_KEY, null);
        final Object frameType = metadata.getOrDefault(RSOCKET_FRAME_TYPE_KEY, null);
        final Object contentType = metadata.getOrDefault(CONTENT_TYPE_KEY, null);
        logHeaders(metadata, "New RSocket connection");
    }

    private void logHeaders(Map<String, Object> metadata, String reason) {

        final Object destination = metadata.getOrDefault(DESTINATION_KEY, null);
        final Object frameType = metadata.getOrDefault(RSOCKET_FRAME_TYPE_KEY, null);
        final Object contentType = metadata.getOrDefault(CONTENT_TYPE_KEY, null);

        log.debug("");
        log.debug(" - " + reason + " - ");
        log.debug("Destination: {}", destination != null && destination.equals("") ? destination.toString() : "unknown");
        log.debug("FrameType: {}", frameType != null ? frameType.toString() : "unknown");
        log.debug("ContentType: {}", contentType != null ? contentType.toString() : "unknown");
        log.debug("");
    }

}
