package com.nexiles.example.gatewayrsocketwebsocket;

import com.github.javafaker.Faker;
import com.nexiles.example.gatewayrsocketwebsocket.pojo.Order;
import com.nexiles.example.gatewayrsocketwebsocket.states.OrderKind;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class OrderCreator {

    private final Faker faker;

    private final AtomicLong entry = new AtomicLong(0);

    private Order.OrderBuilder baseOrder() {
        return Order.builder()
                .dateTime(OffsetDateTime.now())
                .entry(entry.getAndIncrement())
                .number(faker.number().randomDigit())
                .name(faker.funnyName().name())
                .address(faker.address().fullAddress());
    }

    public Order createOrderByKind(OrderKind orderKind) {
        switch (orderKind) {
            case ALL: return createRandomOrder();
            case LOTR: return createLOTROrder();
            case GOT: return createGOTOrder();
            default: return null;
        }
    }

    /**
     * Either create a LOTR (Lord of the Rings) order or a GOT (Game of Thrones) order.
     * @return LOTR or GOT order.
     */
    public Order createRandomOrder() {
        int random = new Random().nextInt(10);

        if (random < 5)
            return createLOTROrder();
        else
            return createGOTOrder();
    }

    public Order createLOTROrder() {
        return lotrOrder(baseOrder());
    }

    public Order createGOTOrder() {
        return gotOrder(baseOrder());
    }

    private Order lotrOrder(Order.OrderBuilder orderBuilder) {
        return orderBuilder.kind(OrderKind.LOTR).item(faker.lordOfTheRings().character()).build();
    }

    private Order gotOrder(Order.OrderBuilder orderBuilder) {
        return orderBuilder.kind(OrderKind.GOT).item(faker.gameOfThrones().character()).build();
    }

}
