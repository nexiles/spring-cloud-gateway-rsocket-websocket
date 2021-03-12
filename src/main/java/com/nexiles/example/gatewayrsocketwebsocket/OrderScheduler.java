package com.nexiles.example.gatewayrsocketwebsocket;

import com.nexiles.example.gatewayrsocketwebsocket.events.NewOrderEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderScheduler {

    private final OrderCreator orderCreator;

    private final ApplicationEventPublisher eventPublisher;

    @Scheduled(fixedDelay = 5000)
    public void scheduledOrder() {
        eventPublisher.publishEvent(NewOrderEvent.forOrder(orderCreator.createRandomOrder()));
    }

}
