package com.sundstrom.reactive_demo;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class TakeTest {

    @Test
    void take() {
        var count = 10;
        var take = range().take(count);

        StepVerifier
                .create(take)
                .expectNextCount(10)
                .verifyComplete();

        StepVerifier
                .create(take)
                .expectNext(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .verifyComplete();

    }

    @Test
    void takeUntil() {
        var count = 50;
        Flux<Integer> take = range().takeUntil(integer -> integer == count);

        StepVerifier.
                create(take)
                .expectNextCount(count)
                .verifyComplete();

    }

    private Flux<Integer> range() {
        return Flux.range(1, 1000);
    }
}
