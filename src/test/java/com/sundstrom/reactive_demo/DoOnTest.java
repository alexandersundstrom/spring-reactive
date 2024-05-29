package com.sundstrom.reactive_demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Signal;
import reactor.core.publisher.SignalType;
import reactor.test.StepVerifier;

import java.util.ArrayList;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class DoOnTest {

    @Test
    void doOn() {
        var signals = new ArrayList<Signal<Integer>>();
        var subscriptions = new ArrayList<Subscription>();
        var nextValues = new ArrayList<Integer>();
        var exceptions = new ArrayList<Throwable>();
        var finallySignals = new ArrayList<SignalType>();

        Flux<Integer> on = Flux.<Integer>create(sink -> {
                    sink.next(1);
                    sink.next(2);
                    sink.next(3);
                    sink.error(new IllegalArgumentException("oops"));
                    sink.complete();
                })
                .doOnNext(nextValues::add)
                .doOnEach(signals::add)
                .doOnSubscribe(subscriptions::add)
                .doOnError(IllegalArgumentException.class, exceptions::add)
                .doFinally(finallySignals::add);

        StepVerifier
                .create(on)
                .expectNext(1, 2, 3)
                .expectError(IllegalArgumentException.class)
                .verify();

        signals.forEach(this::info);
        assertThat(signals.size()).isEqualTo(4);

        finallySignals.forEach(this::info);
        assertThat(finallySignals.size()).isEqualTo(1);

        subscriptions.forEach(this::info);
        assertThat(subscriptions.size()).isEqualTo(1);

        exceptions.forEach(this::info);
        assertThat(exceptions.size()).isEqualTo(1);
        assertTrue(exceptions.get(0) instanceof IllegalArgumentException);

    }

    private void info(Object object) {
        log.info(object.toString());
    }
}
