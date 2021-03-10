package com.nexiles.example.gatewayrsocketwebsocket;

import com.github.javafaker.Faker;
import com.nexiles.example.gatewayrsocketwebsocket.pojo.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class OrderCreator {

    private final Faker faker;

    private final AtomicLong entry = new AtomicLong(0);

    public Order createOrder() {
        return new Order(
                OffsetDateTime.now(),
                entry.getAndIncrement(),
                faker.number().randomDigit(),
                faker.funnyName().name(),
                faker.address().fullAddress(),
                faker.lordOfTheRings().character());
    }

}
