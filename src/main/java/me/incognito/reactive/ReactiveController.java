package me.incognito.reactive;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Create by incognito on 2021/6/1
 */
@RestController
@RequestMapping("/rx")
@Slf4j
public class ReactiveController {

    @GetMapping("/get/map")
    public Mono<Map<String, Object>> getMap() {
        return Mono.fromSupplier(() -> {
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return Collections.singletonMap("result", "success");
        });
    }

    @GetMapping("/get/all")
    public Flux<Map<String, Object>> getAll() {
        return Flux.interval(Duration.ofSeconds(1))
                .take(10)
                .map(l -> Collections.singletonMap("count", l));
    }
}
