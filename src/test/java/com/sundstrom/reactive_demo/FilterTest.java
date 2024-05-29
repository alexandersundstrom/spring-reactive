package com.sundstrom.reactive_demo;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FilterTest {

    @Test
    void filterTest() {
        Flux<Integer> evenNumbers = Flux
                .range(0, 1000)
                .take(5)
                .filter(integer -> integer % 2 == 0);

        StepVerifier
                .create(evenNumbers)
                .expectNext(0,2,4)
                .verifyComplete();
    }
}
