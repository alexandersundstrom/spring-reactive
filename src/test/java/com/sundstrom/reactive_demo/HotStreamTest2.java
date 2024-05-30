package com.sundstrom.reactive_demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SignalType;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Slf4j
public class HotStreamTest2 {

    @Test
    void hot() throws Exception {
        var ten = 10;
        log.info("Start of test");
        var cdl = new CountDownLatch(2);
        Flux<Integer> live = Flux
                .range(0, ten)
                .delayElements(Duration.ofMillis(ten))
                .share();

        var one = new ArrayList<Integer>();
        var two = new ArrayList<Integer>();
        live
                .doFinally(signalTypeConsumer(cdl))
                .subscribe(collect(one));
        Thread.sleep(ten * 2);
        live
                .doFinally(signalTypeConsumer(cdl))
                .subscribe(collect(two));

        cdl.await(5, TimeUnit.SECONDS);
        log.info("Stopped");

        assertThat(one.size()).isGreaterThan(two.size());
    }

    private Consumer<SignalType> signalTypeConsumer(CountDownLatch cdl) {
        return signalType -> {
            if (signalType.equals(SignalType.ON_COMPLETE)) {
                try {
                    cdl.countDown();
                    log.info("await()...");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    private Consumer<Integer> collect(List<Integer> ints) {
        return ints::add;
    }
}
