package com.akfnt.cnsdispatcherservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.util.function.Function;

// 함수는 설정 클래스에서 정의된다
@Slf4j
@Configuration
public class DispatchingFunctions {

    // 주문을 포장하는 비즈니스 로직을 구현하는 함수
    // 빈으로 정의된 함수는 스프링 클라우드 함수가 찾아서 관리할 수 있다
    @Bean
    public Function<OrderAcceptedMessage, Long> pack() {
        // OrderAcceptedMessage 객체를 입력으로 받는다
        return orderAcceptedMessage -> {
            log.info("The order with id {} is packed.", orderAcceptedMessage.orderId());
            // 주문 아이디를 출력으로 반환한다
            return orderAcceptedMessage.orderId();
        };
    }

    // 주문 레이블링 비즈니스 로직을 구현하는 리액티브 함수
    @Bean
    public Function<Flux<Long>, Flux<OrderDispatchedMessage>> label() {
        return orderFLux -> orderFLux.map(orderId -> {
            log.info("The order with id {} is labeled.", orderId);
            return new OrderDispatchedMessage(orderId);
        });
    }
}
