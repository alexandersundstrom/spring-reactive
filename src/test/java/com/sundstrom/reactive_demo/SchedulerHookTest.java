package com.sundstrom.reactive_demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.String.format;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Slf4j
public class SchedulerHookTest {

    @Test
    void onScheduleHookTest() {
        var counter = new AtomicInteger();
        Schedulers.onScheduleHook("my hook", runnable -> () -> {
            var threadName = Thread.currentThread().getName();
            counter.incrementAndGet();
            log.info(format("Before execution: %s", threadName));
            runnable.run();
            log.info(format("After execution: %s", threadName));
        });

        var integerFlux = Flux
                .just(1,2,3)
                .delayElements(Duration.ofMillis(1))
                .subscribeOn(Schedulers.immediate());

        StepVerifier
                .create(integerFlux)
                .expectNext(1,2,3)
                .verifyComplete();

        assertThat(counter.get()).isEqualTo(3);
    }
}
