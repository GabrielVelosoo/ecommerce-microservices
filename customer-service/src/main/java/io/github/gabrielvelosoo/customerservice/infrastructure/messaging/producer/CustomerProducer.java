package io.github.gabrielvelosoo.customerservice.infrastructure.messaging.producer;

import io.github.gabrielvelosoo.customerservice.application.dto.event.CustomerCreatedEvent;
import io.github.gabrielvelosoo.customerservice.application.dto.event.CustomerDeletedEvent;
import io.github.gabrielvelosoo.customerservice.application.dto.event.CustomerUpdatedEvent;
import io.github.gabrielvelosoo.customerservice.infrastructure.messaging.configuration.RabbitConfiguration;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerProducer {

    private static final Logger logger = LogManager.getLogger(CustomerProducer.class);

    private final RabbitTemplate rabbitTemplate;

    public void publishCustomerCreated(CustomerCreatedEvent event) {
        logger.info("[RabbitMQ] Publishing CustomerCreatedEvent. customerId='{}'", event.customerId());
        rabbitTemplate.convertAndSend(
                RabbitConfiguration.EXCHANGE,
                RabbitConfiguration.CREATE_KEY,
                event
        );
    }

    public void publishCustomerUpdated(CustomerUpdatedEvent event) {
        logger.info("[RabbitMQ] Publishing CustomerUpdatedEvent. customerId='{}'", event.customerId());
        rabbitTemplate.convertAndSend(
                RabbitConfiguration.EXCHANGE,
                RabbitConfiguration.UPDATE_KEY,
                event
        );
    }

    public void publishCustomerDeleted(CustomerDeletedEvent event) {
        logger.info("[RabbitMQ] Publishing CustomerDeletedEvent. customerId='{}'", event.customerId());
        rabbitTemplate.convertAndSend(
                RabbitConfiguration.EXCHANGE,
                RabbitConfiguration.DELETE_KEY,
                event
        );
    }
}
