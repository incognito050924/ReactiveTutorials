package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class DemoApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Start");
        WebClient.create()
                .get()
                .uri("http://localhost:8080/rx/get/all")
                .retrieve()
                .bodyToFlux(new ParameterizedTypeReference<Map<String, String>>() {})
                .doOnNext(map -> log.info("onNext: {}", map))
                .subscribe(map -> log.info("Response Body: {}", map));
    }
}
