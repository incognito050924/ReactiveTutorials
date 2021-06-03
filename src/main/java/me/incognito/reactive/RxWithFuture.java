package me.incognito.reactive;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.function.Supplier;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/**
 * Create by incognito on 2021/6/3
 */
@Slf4j
public class RxWithFuture {
    private static final ExecutorService ES = Executors.newFixedThreadPool(10);

    private static Supplier<Map<String, Object>> mapSupplier = () -> Collections.singletonMap("result", "success");

    public static void main(String[] args) throws Exception {
//        final Future<Map<String, Object>> future = ES.submit(() -> RxWithFuture.mapSupplier.get());
//        Flowable.fromFuture(future)
//                .map(item -> item.toString())
//                .map(item -> "[MAP]" + item)
//                .subscribe(map -> log.info("Result: {}", map));
//
//        Mono.fromSupplier(mapSupplier)
//                .log()
//                .map(item -> item.toString())
//                .log()
//                .map(item -> "[MAP]" + item)
//                .log()
//                .subscribe(map -> log.info("Result: {}", map));

//        final List<Map<String, Object>> res = new RestTemplate().getForObject("http://101.101.217.170:8088/engine-rest/process-definition", List.class);
//        log.info("Response: {}", res);

//        final ListenableFuture<ResponseEntity<List>> resFuture = new AsyncRestTemplate().getForEntity("http://101.101.217.170:8088/engine-rest/process-definition", List.class);
//        Flowable.fromFuture(resFuture)
//                .subscribeOn(Schedulers.io())
//                .map(ResponseEntity::getBody)
//                .flatMap(Flux::fromIterable)
//                .map(obj -> ((Map<String, Object>) obj).get("id"))
//                .subscribe(body -> log.info("Response: {}", body));

//        AsyncRestTemplate
        final Flux<String> flux = WebClient.create()
                .get()
                .uri("http://101.101.217.170:8088/engine-rest/process-definition")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                .flux()
                .flatMap(Flux::fromIterable)
                .map(def -> def.get("id").toString());

        Flowable.fromPublisher(flux)
                .doOnNext(id -> log.info("ID: {}", id))
                .subscribe();

        log.info("Finished");

        ForkJoinWorkerThread.currentThread().join();
    }
}
