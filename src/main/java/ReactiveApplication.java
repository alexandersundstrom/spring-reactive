import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ReactiveApplication {

    public static void main(String[] args) {
        Flux<Integer> flux = Flux.just(1,2,3,4);
        Mono<Integer> mono = Mono.just(1);
    }
}
