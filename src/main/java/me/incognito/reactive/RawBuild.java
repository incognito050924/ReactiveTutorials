package me.incognito.reactive;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Iterator;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;

/**
 * Create by incognito on 2021/5/28
 */
@Slf4j
public class RawBuild {
    private static final Random RND = new Random();

    private static Iterable<Integer> iter() {
        return Stream.iterate(0, i -> i + 1).limit(10).collect(Collectors.toList());
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

    private static <T> Subscriber<T> sub(final String name) {
        return new Subscriber<T>() {
            private Subscription subscription;
            @Override
            public void onSubscribe(final Subscription s) {
                subscription = s;
                log.info("{} onSubscribe: {}", name, s);
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(T item) {
                log.info("{} onNext: {}", name, item);
//                subscription.request(1);
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
            public void request(long n) {
                try {
                    while (n-- > 0) {
                        if (ints.hasNext()) {
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
//    private static Publisher<Integer> addPub(Publisher<Integer> source) {
//        return new Publisher<Integer>() {
//            @Override
//            public void subscribe(Subscriber<? super Integer> subscriber) {
//                source.subscribe(new Subscriber<Integer>() {
//                    @Override
//                    public void onSubscribe(Subscription subscription) {
//                        subscriber.onSubscribe(subscription);
//                    }
//
//                    @Override
//                    public void onNext(Integer integer) {
//                        subscriber.onNext(integer + 10);
//                    }
//
//                    @Override
//                    public void onError(Throwable t) {
//                        subscriber.onError(t);
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        subscriber.onComplete();
//                    }
//                });
//            }
//        };
//    }

    private static <T, R> Publisher<R> mapPub(final Publisher<T> source, final Function<T, R> mapper) {
        return new Publisher<R>() {
            @Override
            public void subscribe(Subscriber<? super R> subscriber) {
                source.subscribe(new Subscriber<T>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        subscriber.onSubscribe(s);
                    }

                    @Override
                    public void onNext(T item) {
                        subscriber.onNext(mapper.apply(item));
                    }

                    @Override
                    public void onError(Throwable t) {
                        subscriber.onError(t);
                    }

                    @Override
                    public void onComplete() {
                        subscriber.onComplete();
                    }
                });
            }
        };
    }

    public static <T> Publisher<T> filterPub(final Publisher<T> source, final Predicate<T> predicate) {
        return new Publisher<T>() {
            @Override
            public void subscribe(Subscriber<? super T> sub) {
                source.subscribe(new DelegateSub<T>(sub) {
                    @Override
                    public void onNext(T t) {
                        if (predicate.test(t)) {
                            this.subscriber.onNext(t);
                        }
                    }
                });
            }
        };
    }

    private static <T> Publisher<T> reducePub(final Publisher<T> source, final BiFunction<T, T, T> accumulator, final T init) {
        return new Publisher<T>() {
            @Override
            public void subscribe(Subscriber<? super T> sub) {
                source.subscribe(new DelegateSub<T>(sub) {
                    private T acc = init;

                    @Override
                    public void onNext(T t) {
                        acc = accumulator.apply(acc, t);
                    }

                    @Override
                    public void onComplete() {
                        subscriber.onNext(acc);
                        subscriber.onComplete();
                    }
                });
            }
        };
    }

    public static void main(String[] args) throws InterruptedException {
        final Publisher<Integer> intPub = intPub();
//        final Publisher<Integer> addPub = mapPub(intPub, i -> i + 1);
        final Publisher<Integer> evenPub = filterPub(intPub, i -> i % 2 == 0);
        final Publisher<Integer> sumPub = reducePub(evenPub, (acc, i) -> acc + i, 0);
        sumPub.subscribe(sub("A"));
    }

    abstract static class DelegateSub<T> implements Subscriber<T> {
        protected final Subscriber<? super T> subscriber;
        public DelegateSub(final Subscriber<? super T> subscriber) {
            this.subscriber = subscriber;
        }

        @Override
        public void onSubscribe(final Subscription s) {
            subscriber.onSubscribe(s);
        }

        @Override
        public void onNext(T t) {
            subscriber.onNext(t);
        }

        @Override
        public void onError(Throwable t) {
            subscriber.onError(t);
        }

        @Override
        public void onComplete() {
            subscriber.onComplete();
        }
    }
}
