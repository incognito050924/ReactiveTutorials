package me.incognito.reactive;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

/**
 * Create by incognito on 2021/5/28
 */
@Slf4j
public class TestReactor {

    private static void infinite() {
        final Disposable disposable = Flux.fromStream(Stream.iterate(0, prev -> prev + 1))
                .subscribeOn(Schedulers.single())
                .publishOn(Schedulers.single())
                .subscribe(
                        i -> log.info("#{}", i)
                        , err -> log.error("Error: {}", err.getMessage())
                        , () -> log.info("completed")
                );

        try {
            TimeUnit.SECONDS.sleep(2);
            System.out.println("Dispose!!!");
            disposable.dispose();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Flux.fromStream(Stream.iterate(0, i -> i+1).limit(10))
                .map(i -> i + 1)
                .flatMap(i -> Flux.just(i, i, i))
//                .filter(i -> i % 2 == 0)
//                .reduce(0, (acc, i) -> acc + i)
                .subscribe(item -> log.info("Item: {}", item), throwable -> log.error("msg: {}", throwable.getMessage()));
    }
}
