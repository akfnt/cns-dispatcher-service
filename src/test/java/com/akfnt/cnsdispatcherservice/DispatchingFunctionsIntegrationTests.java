package com.akfnt.cnsdispatcherservice;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.function.context.FunctionCatalog;
import org.springframework.cloud.function.context.test.FunctionalSpringBootTest;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

@FunctionalSpringBootTest
// 스프링 클라우드 스트림에서 함수 프로그래밍 모델을 사용하면 입력 데이터를 받는 함수에 대해서는 입력 바인딩이, 풀력 데이터를 반환하는 함수에 대해서는 출력 바인딩이 자동으로 생성됨
// 스프링 클라우드 스트림 적용후 packAndLabelOrder() 에서 AssertionError 발생: onNext에서 GenericMessage 가 전송됨 (ClassCastException(GenericMessage <-> OrderDispatchedMessage))
@Disabled("These tests are only necessary when using the functions alone (no bindings)")
public class DispatchingFunctionsIntegrationTests {
    @Autowired
    private FunctionCatalog catalog;

    @Test
    void packOrder() {
        Function<OrderAcceptedMessage, Long> pack = catalog.lookup(Function.class, "pack");
        long orderId = 121;
        assertThat(pack.apply(new OrderAcceptedMessage(orderId))).isEqualTo(orderId);
    }
    @Test
    void labelOrder() {
        Function<Flux<Long>, Flux<OrderDispatchedMessage>> label = catalog.lookup(Function.class, "label");
        Flux<Long> orderId = Flux.just(121L);

        StepVerifier.create(label.apply(orderId))
                .expectNextMatches(dispatchedOrder ->
                        dispatchedOrder.equals(new OrderDispatchedMessage(121L)))
                .verifyComplete();
    }

    // 스프링 클라우드 함수를 통해 합성된 함수 pack() + label() 의 작동을 테스트
    @Test
    void packAndLabelOrder() {
        // FunctionCatalog 로부터 합성 함수를 가져온다
        Function<OrderAcceptedMessage, Flux<OrderDispatchedMessage>> packAndLabel = catalog.lookup(Function.class, "pack|label");
        long orderId = 121;

        StepVerifier.create(packAndLabel.apply(new OrderAcceptedMessage(orderId)))  // 함수에 대한 입력인 OrderAcceptedMessage 를 정의한다
                .expectNextMatches(dispatchedOrder ->
                        dispatchedOrder.equals(new OrderDispatchedMessage(orderId))) // 함수의 출력이 OrderDispatchedMessage 객체인지 확인한다
                .verifyComplete();
    }
}