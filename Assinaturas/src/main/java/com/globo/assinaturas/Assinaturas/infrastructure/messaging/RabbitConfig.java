package com.globo.assinaturas.Assinaturas.infrastructure.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;

@Configuration
public class RabbitConfig {

    public static final String NOTIFICACAO_QUEUE = "notificacaoQueue";
    public static final String NOTIFICACAO_EXCHANGE = "notificacaoExchange";
    public static final String ROUTING_KEY = "notificacaoKey";

    @Bean
    public Queue notificacaoQueue() {
        return new Queue(NOTIFICACAO_QUEUE, true);
    }

    @Bean
    public DirectExchange notificacaoExchange() {
        return new DirectExchange(NOTIFICACAO_EXCHANGE);
    }

    @Bean
    public Binding binding(Queue notificacaoQueue, DirectExchange notificacaoExchange) {
        return BindingBuilder.bind(notificacaoQueue).to(notificacaoExchange).with(ROUTING_KEY);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setExchange(NOTIFICACAO_EXCHANGE);
        rabbitTemplate.setRoutingKey(ROUTING_KEY);
        return rabbitTemplate;
    }

    @Bean
    public SimpleMessageListenerContainer messageListenerContainer(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueues(notificacaoQueue());
        return container;
    }
}

