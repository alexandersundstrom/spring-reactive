package com.sundstrom.reactive_demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.String.format;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Slf4j
public class SchedulerSubscribeOnTest {

    @Test
    void subscribeOn() {
        var rsbThreadName = SchedulerSubscribeOnTest.class.getName();
        var map = new ConcurrentHashMap<String, AtomicInteger>();
        var executor = Executors.newFixedThreadPool(5, runnable -> {
            Runnable wrapper = () -> {
                var key = Thread.currentThread().getName();
                var result = map.computeIfAbsent(key, s -> new AtomicInteger());
                result.incrementAndGet();
                runnable.run();
            };
            return new Thread(wrapper, rsbThreadName);
        });

        var scheduler = Schedulers.fromExecutor(executor);
        var integerMono = Mono
                .just(1)
                .subscribeOn(scheduler)
                .doFinally(signalType -> {
                    map.forEach((key, value) -> log.info(format("Key: %s: %d", key, value.get())));
                });

        StepVerifier
                .create(integerMono)
                .expectNextCount(1)
                .verifyComplete();

        var atomicInteger = map.get(rsbThreadName);
        assertThat(atomicInteger.get()).isEqualTo(1);
    }
}
