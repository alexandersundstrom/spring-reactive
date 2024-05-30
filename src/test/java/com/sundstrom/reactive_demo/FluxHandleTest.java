package com.sundstrom.reactive_demo;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.stream.Stream;

import static java.lang.String.format;

public class FluxHandleTest {

    @Test
    void handle() {
        StepVerifier
                .create(handle(5, 4))
                .expectNext(0, 1, 2, 3)
                .expectError(IllegalArgumentException.class)
                .verify();

        StepVerifier
                .create(handle(3,3))
                .expectNext(0,1,2)
                .verifyComplete();
    }

    Flux<Integer> handle(int max, int numberToError) {
        return Flux.range(0, max)
                .handle((value, sink) -> {  // handle the flux, using a sink
                    List<Integer> upTo = Stream
                            .iterate(0, i -> i < numberToError, i -> i + 1)
                            .toList();

                    if (upTo.contains(value)) {
                        sink.next(value);
                        return;
                    }

                    if (value == numberToError) {
                        sink.error(new IllegalArgumentException(format("No %d for you!", numberToError)));
                        return;
                    }
                    sink.complete();
                });
    }
}
