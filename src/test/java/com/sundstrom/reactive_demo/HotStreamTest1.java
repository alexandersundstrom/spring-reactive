package com.sundstrom.reactive_demo;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Sinks;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class HotStreamTest1 {

    @Test
    void hot() {
        var first = new ArrayList<Integer>();
        var second = new ArrayList<Integer>();
        Sinks.ManyWithUpstream<Integer> emitter = Sinks.unsafe().manyWithUpstream().multicastOnBackpressureBuffer();

        emitter.asFlux().subscribe(collect(first));
        emitter.tryEmitNext(1);
        emitter.tryEmitNext(2);

        emitter.asFlux().subscribe(collect(second));

        emitter.tryEmitNext(3);
        emitter.tryEmitComplete();

        assertThat(first.size()).isEqualTo(3);
        assertThat(second.size()).isEqualTo(1);

    }

    Consumer<Integer> collect(List<Integer> collection) {
        return collection::add;
    }

}
