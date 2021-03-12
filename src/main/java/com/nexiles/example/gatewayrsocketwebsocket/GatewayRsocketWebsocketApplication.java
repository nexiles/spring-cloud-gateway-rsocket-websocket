package com.nexiles.example.gatewayrsocketwebsocket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class GatewayRsocketWebsocketApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayRsocketWebsocketApplication.class, args);
	}

}
