package com.akfnt.cnsdispatcherservice;

// 주문 아이디를 가지고 있는 DTO
public record OrderAcceptedMessage(
        Long orderId
) {
}