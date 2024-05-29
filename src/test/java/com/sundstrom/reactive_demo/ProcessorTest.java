package com.sundstrom.reactive_demo;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;

public class ProcessorTest {

    @Test
    void emitterProcessor() {
        Sinks.Many<String> processor = Sinks.many().unicast().onBackpressureBuffer();
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
        StepVerifier
                .create(publisher)
                .expectNext("1")
                .expectNext("2")
                .expectNext("3")
                .verifyComplete();
    }
}
