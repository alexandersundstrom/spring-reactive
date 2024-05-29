package com.sundstrom.reactive_demo;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
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
}
