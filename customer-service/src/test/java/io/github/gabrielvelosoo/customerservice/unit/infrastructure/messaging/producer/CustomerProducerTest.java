package io.github.gabrielvelosoo.customerservice.unit.infrastructure.messaging.producer;

import io.github.gabrielvelosoo.customerservice.application.dto.event.CustomerCreatedEvent;
import io.github.gabrielvelosoo.customerservice.application.dto.event.CustomerDeletedEvent;
import io.github.gabrielvelosoo.customerservice.application.dto.event.CustomerUpdatedEvent;
import io.github.gabrielvelosoo.customerservice.infrastructure.messaging.configuration.RabbitConfiguration;
import io.github.gabrielvelosoo.customerservice.infrastructure.messaging.producer.CustomerProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CustomerProducerTest {

    @Mock
    RabbitTemplate rabbitTemplate;

    @InjectMocks
    CustomerProducer customerProducer;

    @Test
    void shouldPublishCustomerCreatedEvent() {
        CustomerCreatedEvent event = new CustomerCreatedEvent(1L, "Name", "Last", "e@mail", "pwd");
        customerProducer.publishCustomerCreated(event);
        verify(rabbitTemplate).convertAndSend(RabbitConfiguration.EXCHANGE, RabbitConfiguration.CREATE_KEY, event);
    }

    @Test
    void shouldPublishCustomerUpdatedEvent() {
        CustomerUpdatedEvent event = new CustomerUpdatedEvent(2L, "New", "Name");
        customerProducer.publishCustomerUpdated(event);
        verify(rabbitTemplate).convertAndSend(RabbitConfiguration.EXCHANGE, RabbitConfiguration.UPDATE_KEY, event);
    }

    @Test
    void shouldPublishCustomerDeletedEvent() {
        CustomerDeletedEvent event = new CustomerDeletedEvent(3L);
        customerProducer.publishCustomerDeleted(event);
        verify(rabbitTemplate).convertAndSend(RabbitConfiguration.EXCHANGE, RabbitConfiguration.DELETE_KEY, event);
    }
}