package com.sundstrom.reactive_demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.test.StepVerifier;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class AsyncEmitterTest {

    private final ExecutorService executorService = Executors.newFixedThreadPool(1);

    @Test
    void async() {
        Flux<Integer> integers = Flux.create(emitter -> this.launch(emitter, 5));
        StepVerifier
                .create(integers
                        .doFinally(signalType -> this.executorService.shutdown()))
                .expectNextCount(5)
                .verifyComplete();
    }

    private void launch(FluxSink<Integer> integerFluxSink, int count) {
        this.executorService.submit(() -> {
            var integer = new AtomicInteger();
            Assertions.assertNotNull(integerFluxSink);
            while (integer.get() < count) {
                var random = Math.random();
                integerFluxSink.next(integer.incrementAndGet());
                this.sleep((long) (random * 1000));
            }
            integerFluxSink.complete();
        });
    }

    private void sleep(long s) {
        try {
            Thread.sleep(s);
        } catch (Exception e) {
            log.error("Error", e);
        }
    }
}
