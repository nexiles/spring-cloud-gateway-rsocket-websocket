package com.nexiles.example.gatewayrsocketwebsocket.states;

import java.util.Arrays;

public enum OrderKind {

    LOTR("lotr"),
    GOT("got"),
    ALL("all");

    private final String identifier;

    OrderKind(String kind) {
        this.identifier = kind;
    }

    public static OrderKind byIdentifier(String identifier) {
        return Arrays.stream(OrderKind.values())
                .filter(orderKind -> orderKind.identifier.equals(identifier)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Unknown identifier: %s", identifier)));
    }

}
