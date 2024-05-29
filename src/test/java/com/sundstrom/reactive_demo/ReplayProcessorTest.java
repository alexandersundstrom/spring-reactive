package com.sundstrom.reactive_demo;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;

public class ReplayProcessorTest {

    @Test
    void replayProcessor() {
        int historySize = 2;
        Sinks.Many<String> processor = Sinks.many()
                .replay()
                .limit(historySize);
        produce(processor);
        consume(processor.asFlux());
    }

    private void produce(Sinks.Many<String> processor) {
        processor.tryEmitNext("1");
        processor.tryEmitNext("2");
        processor.tryEmitNext("3");
        processor.tryEmitComplete();
    }

    private void consume(Flux<String> publisher) {

        for (int i = 0; i < 5; i++) {
            StepVerifier
                    .create(publisher)
                    .expectNext("2")
                    .expectNext("3")
                    .verifyComplete();
        }
    }
}
