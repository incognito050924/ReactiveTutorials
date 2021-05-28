package me.incognito.reactive;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

import lombok.extern.slf4j.Slf4j;

/**
 * Create by incognito on 2021/5/28
 */
@Slf4j
public class RawBuild {
    private static final Random RND = new Random();

    private static Iterable<Integer> iter() {
        return Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
    }

    private static Publisher<Integer> intPub() {
        return new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> s) {
                try {
                    s.onSubscribe(subscription(s, iter().iterator()));
                } catch (final Exception e) {
                    s.onError(e);
                }
            }
        };
    }

    private static Subscriber<Integer> sub(final String name) {
        return new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription s) {
                log.info("{} onSubscribe: {}", name, s);
                s.request(3);
                s.request(3);
                s.cancel();
            }

            @Override
            public void onNext(Integer integer) {
                log.info("{} onNext: {}", name, integer);
            }

            @Override
            public void onError(Throwable t) {
                log.warn("{} onError: {}", name, t.getMessage(), t);
            }

            @Override
            public void onComplete() {
                log.info("{} onComplete", name);
            }
        };
    }

    public static Subscription subscription(
            Subscriber<? super Integer> subscriber,
            Iterator<Integer> ints
    ) {
        return new Subscription() {
            @Override
            public void request(final long n) {
                try {
                    long count = 0;
                    while (true) {
                        if (ints.hasNext() && count++ < n) {
                            subscriber.onNext(ints.next());
                        } else {
                            subscriber.onComplete();
                            return;
                        }
                    }
                } catch (final Exception e) {
                    subscriber.onError(e);
                }
            }

            @Override
            public void cancel() {
                try {
                    subscriber.onComplete();
                } catch (final Exception e) {
                    subscriber.onError(e);
                }
            }
        };
    }

    public static void main(String[] args) {
        intPub().subscribe(sub("B"));
        intPub().subscribe(sub("A"));
    }
}
