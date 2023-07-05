package com.santiago.posada.config;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.rabbitmq.*;

@Configuration
public class RabbitMqConfig {

    public static final String TOPIC_EXCHANGE = "tasks-topic-exchange";
    public static final String QUEUE_EVENTS_GENERAL = "tasks.events.general";
    public static final String QUEUE_EVENTS_TASK_CREATED = "tasks.events.task.created";
    public static final String QUEUE_EVENTS_AUTHOR_CREATED = "tasks.events.author.created";
    public static final String ROUTING_KEY_EVENTS_GENERAL = "routingkey.tasks.events.#";
    public static final String ROUTING_KEY_EVENTS_TASK_CREATED = "routingkey.tasks.events.task.created";
    public static final String ROUTING_KEY_EVENTS_AUTHOR_CREATED = "routingkey.tasks.events.author.created";

    @Bean
    public Mono<Connection> createConnectionToRabbit(){
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setVirtualHost("/");
        connectionFactory.setPort(5672);
        return Mono.fromCallable(()->connectionFactory.newConnection("events-handler"));
    }

    @Bean
    public SenderOptions senderOptions(Mono<Connection> connectionMono) {
        return new SenderOptions()
                .connectionMono(connectionMono)
                .resourceManagementScheduler(Schedulers.boundedElastic());
    }
    @Bean
    public Sender createSender(SenderOptions options){
        return RabbitFlux.createSender(options);
    }

    @Bean
    public ReceiverOptions receiverOptions(Mono<Connection> connectionMono) {
        return new ReceiverOptions()
                .connectionMono(connectionMono);
    }

    @Bean
    public Receiver createReceiver(ReceiverOptions options){
        return RabbitFlux.createReceiver(options);
    }


    //To create the instance of the necessary resources in RabbitMq such as the
    //exchange, queue and binding
    /*@Bean
    public Mono<AMQP.Queue.BindOk> createQueue(Sender sender){
        return sender.declareExchange(
                ExchangeSpecification.exchange("my-topic-exchange").type("topic"))
                .then(sender.declareQueue(
                        QueueSpecification.queue("my-queue")))
                .then(sender.bind(
                        BindingSpecification.binding().exchange("my-topic-exchange").queue("my-queue").routingKey("topic.key1")));
    }*/
}
