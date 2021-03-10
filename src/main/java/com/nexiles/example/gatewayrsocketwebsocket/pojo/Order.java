package com.nexiles.example.gatewayrsocketwebsocket.pojo;

import lombok.Value;

import java.time.OffsetDateTime;

@Value
public class Order {

    OffsetDateTime dateTime;

    Long entry;

    Integer number;

    String name;

    String address;

    String item;

}
