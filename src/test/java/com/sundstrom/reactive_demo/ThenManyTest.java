package com.sundstrom.reactive_demo;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ThenManyTest {

    @Test
    void thenMany() {
        var letterCount = new AtomicInteger();
        var numberCount = new AtomicInteger();
        Flux<String> letterPublisher = Flux
                .just("a", "b", "c")
                .doOnNext(value -> letterCount.incrementAndGet());

       Flux<Integer> numberPublisher = Flux
               .just(1,2,3)
               .doOnNext(number -> numberCount.incrementAndGet());

        Flux<Integer> thisBeforeThat = letterPublisher.thenMany(numberPublisher);

        StepVerifier
                .create(thisBeforeThat)
                .expectNext(1,2,3)
                .verifyComplete();

        assertThat(letterCount.get()).isEqualTo(3);
        assertThat(numberCount.get()).isEqualTo(3);
    }
}
