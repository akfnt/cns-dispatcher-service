package com.akfnt.cnsdispatcherservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestChannelBinderConfiguration.class)   // 테스트 바인더 설정 임포트
public class FunctionStreamIntegrationTests {
    @Autowired
    private InputDestination input;     // 입력바인딩은 packlabel-in-0 밖에 없으므로 기본 설정상 이 바인딩을 나타낸다

    @Autowired
    private OutputDestination output;   // 출력바인딩은 packlabel-out-0 밖에 없으므로 기본 설정상 이 바인딩을 나타낸다

    @Autowired
    private ObjectMapper objectMapper;  // JSON 메시지 페이로드(이진 데이터)를 자바 객체로 역직렬화하기 위해 잭슨을 사용한다

    @Test
    void whenOrderAcceptedThenDispatched() throws IOException {
        long orderId = 121;
        Message<OrderAcceptedMessage> inputMessage = MessageBuilder
                .withPayload(new OrderAcceptedMessage(orderId)).build();
        Message<OrderDispatchedMessage> expectedOutputMessage = MessageBuilder
                .withPayload(new OrderDispatchedMessage(orderId)).build();

        this.input.send(inputMessage);
        assertThat(objectMapper.readValue(output.receive().getPayload(), OrderDispatchedMessage.class))
                .isEqualTo(expectedOutputMessage.getPayload());
    }

}
