package me.incognito.reactive;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import lombok.extern.slf4j.Slf4j;

/**
 * Create by incognito on 2021/5/28
 */
@Slf4j
public class TestRxJava {
    private static void infinite() {
        final Disposable disposable = Flowable.interval(500, TimeUnit.MILLISECONDS)
                .subscribe(i -> log.info("#{}", i)
                        , err -> log.error("Error: {}", err.getMessage())
                        , () -> log.info("completed"));
    }

    private static void single() {
        Single.error(new Exception("abc"))
                .subscribe(
                        i -> log.info("#{}", i)
                        , err -> log.error("Error: {}", err.getMessage()));
    }

    public static void main(String[] args) throws InterruptedException {
        single();
        TimeUnit.MINUTES.sleep(1);
    }
}
