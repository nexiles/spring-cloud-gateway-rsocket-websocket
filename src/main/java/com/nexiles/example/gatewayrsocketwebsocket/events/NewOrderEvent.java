package com.nexiles.example.gatewayrsocketwebsocket.events;

import com.nexiles.example.gatewayrsocketwebsocket.pojo.Order;
import lombok.Value;

@Value(staticConstructor = "forOrder")
public class NewOrderEvent {

    Order order;

}
