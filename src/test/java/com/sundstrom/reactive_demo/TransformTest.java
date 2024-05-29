package com.sundstrom.reactive_demo;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class TransformTest {

    @Test
    void transform() {
        var finished = new AtomicBoolean();
        Flux<String> letters = Flux
                .just("A", "B", "C")
                .transform(stringFlux -> stringFlux.doFinally(signal -> finished.set(true)));

        StepVerifier
                .create(letters)
                .expectNextCount(3)
                .verifyComplete();
        assertTrue(finished.get());
    }

    @Test
    void transformToString() {
        Flux<Integer> numbers = Flux.range(1, 3);
        Flux<String> map = numbers.map(integer -> "Value " + integer);

        StepVerifier
                .create(map)
                .expectNext("Value 1", "Value 2", "Value 3")
                .verifyComplete();
    }

    @Test
    void reduce() {
        Mono<Integer> numbers = Flux.range(0, 3)
                .reduce((integer, integer2) -> integer + integer2);

        StepVerifier
                .create(numbers)
                .expectNext(3)
                .verifyComplete();
    }
}
