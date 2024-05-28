package com.sundstrom.reactive_demo;

import org.junit.jupiter.api.Test;
import org.reactivestreams.FlowAdapters;
import org.reactivestreams.Publisher;
import reactor.adapter.JdkFlowAdapter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Flow;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class SimpleFluxFactoriesTest {

    @Test
    void simple() {
        Flux<Integer> rangeOfIntegers = Flux.range(0, 10);
        StepVerifier.create(rangeOfIntegers)
                .expectNextCount(10)
                .verifyComplete();

        Flux<String> letters = Flux.just("A", "B", "C");
        StepVerifier.create(letters)
                .expectNext("A", "B", "C")
                .verifyComplete();

        var now = System.currentTimeMillis();
        Mono<Date> date = Mono.just(new Date(now));
        StepVerifier.create(date)
                .expectNext(new Date(now))
                .verifyComplete();

        var empty = Mono.empty();
        StepVerifier.create(empty)
                .verifyComplete();

        var fromArray = Flux.fromArray(new Integer[] {1,2,3,});
        StepVerifier.create(fromArray)
                .expectNext(1,2,3)
                .verifyComplete();

        var fromIterable = Flux.fromIterable(List.of(1,2,3));
        StepVerifier.create(fromIterable)
                .expectNext(1,2,3)
                .verifyComplete();

        var integer = new AtomicInteger();
        var fromStream = Flux.fromStream(Stream.generate(integer::incrementAndGet));
        StepVerifier.create(fromStream.take(3))
                .expectNext(1)
                .expectNext(2)
                .expectNext(3)
                .verifyComplete();
    }

    @Test
    void flowConverter() {
        Flux<Integer> original = Flux.range(0,10);
        Flow.Publisher<Integer> jdk9Flow = FlowAdapters.toFlowPublisher(original);
        Publisher<Integer> mappedBackAsReactiveStream = FlowAdapters.toPublisher(jdk9Flow);

        StepVerifier.create(original)
                .expectNextCount(10)
                .verifyComplete();

        StepVerifier.create(mappedBackAsReactiveStream)
                .expectNextCount(10)
                .verifyComplete();

        Flux<Integer> mappedBackToFlux = JdkFlowAdapter.flowPublisherToFlux(jdk9Flow);
        StepVerifier.create(mappedBackToFlux)
                .expectNextCount(10)
                .verifyComplete();
    }
}
