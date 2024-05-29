package com.sundstrom.reactive_demo;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FlatConcatAndSwitchMapTest {

    /**
     * Whichever answers first, order not preserved
     */
    @Test
    void flatMap() {
        Flux<Integer> numbers = Flux.just(
                        new Pair(1, 300),
                        new Pair(2, 200),
                        new Pair(3, 100))
                .flatMap(pair -> this.delayReplyFor(pair.id, pair.delay));

        StepVerifier
                .create(numbers)
                .expectNext(3, 2, 1)
                .verifyComplete();
    }

    /**
     * Order preserved, has to wait and looses asynchronity
     */
    @Test
    void concatMap() {
        Flux<Integer> numbers = Flux.just(
                        new Pair(1, 300),
                        new Pair(2, 200),
                        new Pair(3, 100))
                .concatMap(pair -> this.delayReplyFor(pair.id, pair.delay));

        StepVerifier
                .create(numbers)
                .expectNext(1, 2, 3)
                .verifyComplete();
    }

    /**
     * Cancels any outstanding inner publishers as soon as a new value arrives
     * Good for triggering in searchboxes on word complete
     */
    @Test
    void switchMap() {
        Flux<String> source = Flux.just("re", "rea", "react", "reactive")
                .delayElements(Duration.ofMillis(100))
                .switchMap(this::switchMapLookup); //each lookup takes 500 millis, so only the last one is not canceled

        StepVerifier
                .create(source)
                .expectNext("reactive -> reactive")
                .verifyComplete();

    }

    private Flux<String> switchMapLookup(String word) {
        return Flux.just(word + " -> reactive").delayElements(Duration.ofMillis(500));
    }

    private Flux<Integer> delayReplyFor(Integer i, long delay) {
        return Flux.just(i)
                .delayElements(Duration.ofMillis(delay));
    }

    private record Pair(int id, long delay) {
    }
}
