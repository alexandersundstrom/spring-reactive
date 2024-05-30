package com.sundstrom.reactive_demo;

import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.ProxyFactoryBean;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.String.format;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Slf4j
public class SchedulerExecutorServiceDecoratorsTest {

    private final AtomicInteger methodInvocationsCount = new AtomicInteger();
    private String rsb = "rsb";

    @BeforeEach
    void before() {
        Schedulers.resetFactory();
        Schedulers.addExecutorServiceDecorator(
                this.rsb, (scheduler, scheduledExecutorService) -> this.decorate(scheduledExecutorService));
    }

    @AfterEach
    void after() {
        Schedulers.resetFactory();
        Schedulers.removeExecutorServiceDecorator(this.rsb);
    }

    @Test
    void changeDefaultDecorator() {
        var integerFlux = Flux
                .just(1)
                .delayElements(Duration.ofMillis(1));

        StepVerifier
                .create(integerFlux)
                .thenAwait(Duration.ofMillis(10))
                .expectNextCount(1)
                .verifyComplete();

        assertThat(methodInvocationsCount.get()).isEqualTo(1);
    }

    private ScheduledExecutorService decorate(ScheduledExecutorService executorService) {
        try {
            var pfb = new ProxyFactoryBean();
            pfb.setProxyInterfaces(new Class[]{ScheduledExecutorService.class});
            pfb.addAdvice((MethodInterceptor) methodInvocation -> {
                var methodName = methodInvocation.getMethod().getName().toLowerCase();
                this.methodInvocationsCount.incrementAndGet();
                log.info(format("methodName: (%s) incrementing...", methodName));
                return methodInvocation.proceed();
            });
            pfb.setSingleton(true);
            pfb.setTarget(executorService);
            return (ScheduledExecutorService) pfb.getObject();
        } catch (Exception e) {
            log.error("Something went wront", e);
        }
        return null;
    }
}
