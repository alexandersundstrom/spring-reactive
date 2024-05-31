package com.sundstrom.reactive_demo;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class HotStream3Test {
    private List<Integer> one = new ArrayList<Integer>();
    private List<Integer> two = new ArrayList<Integer>();
    private List<Integer> three = new ArrayList<Integer>();

    private Consumer<Integer> subscribe(List<Integer> list) {
        return list::add;
    }

    /**
     * This shows how to use the publish operator to create a Publisher<T> that lets you "pile on"
     * subscribers until a limit is reached. Then, all subscribers may observe the results
     */
    @Test
    void publish() {
        Flux<Integer> pileOn = Flux
                .just(1,2,3)
                .publish()
                .autoConnect(3)
                .subscribeOn(Schedulers.immediate());

        pileOn.subscribe(subscribe(one));
        assertThat(one.size()).isEqualTo(0); //Haven't reach min subscribers

        pileOn.subscribe(subscribe(two));
        assertThat(two.size()).isEqualTo(0); //Haven't reach min subscribers

        pileOn.subscribe(subscribe(three));
        assertThat(one.size()).isEqualTo(3); //Haven't reach min subscribers
        assertThat(two.size()).isEqualTo(3); //Haven't reach min subscribers
        assertThat(three.size()).isEqualTo(3); //Haven't reach min subscribers

    }
}
