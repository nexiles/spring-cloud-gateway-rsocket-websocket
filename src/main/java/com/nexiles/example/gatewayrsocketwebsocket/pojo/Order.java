package com.nexiles.example.gatewayrsocketwebsocket.pojo;

import com.nexiles.example.gatewayrsocketwebsocket.states.OrderKind;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.OffsetDateTime;

@Data
@Builder
@ToString
@AllArgsConstructor
public class Order {

    private final OffsetDateTime dateTime;

    private final Long entry;

    private final Integer number;

    private final String name;

    private final String address;

    private final OrderKind kind;

    private final String item;

}
