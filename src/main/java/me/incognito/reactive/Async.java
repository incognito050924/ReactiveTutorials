package me.incognito.reactive;

import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureTask;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import lombok.extern.slf4j.Slf4j;

/**
 * Create by incognito on 2021/6/2
 */
@Slf4j
public class Async {
    private static ExecutorService ES = Executors.newFixedThreadPool(10);

    private static Supplier<String> stringSupplier = () -> {
        final String value = "ABC";
        log.info("value is {}", value);
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return value;
    };

    private static Callable<String> stringCallable = stringSupplier::get;

    private static void futureTest() throws Exception {
        final Future<String> future = ES.submit(stringCallable);
        log.info("Future: {}", future);
        log.info("Future Result: {}", future.get());
    }

    private static void listenableFutureTest() {
        final ListenableFuture<String> future = new ListenableFutureTask<>(stringCallable);
        future.addCallback(
                result -> {
                    log.info("Future Result: {}", result);
                    // Callback Hell
                    final ListenableFutureTask<String> f2 = new ListenableFutureTask<>(() -> "Nested " + result);
                    f2.addCallback(
                            result2 -> {
                                log.info("Future2 Result: {}", result2);
                            }, err2 -> {
                                log.error(err2.getMessage());
                            });
                    ES.submit(f2);
                },
                err -> {
                    log.error(err.getMessage());
                });
        ES.submit((Runnable) future);
    }

    private static void completableFutureTest() {
        final CompletableFuture<String> completableFuture = new CompletableFuture<>();
        completableFuture
                .thenComposeAsync(result -> {
                    log.info("Future Result: {}", result);
                    return CompletableFuture.completedFuture("Nested " + result);
                })
                .thenComposeAsync(result -> {
                    log.info("Nested Future Result: {}", result);
                    return CompletableFuture.completedFuture("Nested " + result);
                })
                .thenAcceptAsync(result -> log.info("Result: {}", result));

        completableFuture.complete("ABC");

//        final CompletableFuture<?> future = CompletableFuture.supplyAsync(stringSupplier)
//            .thenAcceptAsync(result -> log.info("Future Result: {}", result));
    }

    public static void main(String[] args) throws Exception {
//        futureTest();
//        listenableFutureTest();
        completableFutureTest();

        ES.shutdown();
        log.info("Finished");
    }
}
