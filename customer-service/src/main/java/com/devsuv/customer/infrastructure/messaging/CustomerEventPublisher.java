package com.devsuv.customer.infrastructure.messaging;

import com.devsuv.customer.application.dto.CustomerEventDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomerEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publish(CustomerEventDTO event) {
        log.info("Publishing customer event: operation={}, customerId={}", event.getOperation(), event.getCustomerId());
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY, event);
        log.info("Customer event published successfully for customerId: {}", event.getCustomerId());
    }
}
