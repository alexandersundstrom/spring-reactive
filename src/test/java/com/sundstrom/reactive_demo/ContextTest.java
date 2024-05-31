package com.sundstrom.reactive_demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SignalType;
import reactor.util.context.Context;
import reactor.util.context.ContextView;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Slf4j
public class ContextTest {


    /**
     * Here’s an example of a simple reactive Publisher that has access to a Context.
     * Each time there’s a new value emitted, we poke at the current Context and ask it
     * for the value that should be in the context, the string value1
     *
     * It is a map, as well, supporting any number of keys and values that are tied to the current Publisher.
     * The values in the context are unique to the current Publisher, not the current thread.
     */
    @Test
    void context() throws Exception {
        var valueOne = "value1";
        var observedContextValues = new ConcurrentHashMap<String, AtomicInteger>();
        var max = 3;
        var key = "key1";
        var cdl = new CountDownLatch(max);

        var just = Flux
                .range(0, max)
                .delayElements(Duration.ofMillis(1))
                .doOnEach(integerSignal -> {
                    ContextView currentContextView = integerSignal.getContextView();
                    if (integerSignal.getType().equals(SignalType.ON_NEXT)) {
                        String key1 = currentContextView.get(key);
                        assertThat(key1).isNotNull();
                        assertThat(key1).isEqualTo(valueOne);
                        observedContextValues.computeIfAbsent(key, k -> new AtomicInteger(0)).incrementAndGet();

                    }
                })
                .contextWrite(Context.of(key, valueOne));

        just.subscribe(integer -> {
            log.info("integer: " + integer);
            cdl.countDown();
        });
        cdl.await();
        assertThat(observedContextValues.get(key).get()).isEqualTo(3);
    }

}
