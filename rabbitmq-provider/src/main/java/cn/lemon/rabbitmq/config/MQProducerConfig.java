package cn.lemon.rabbitmq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MQProducerConfig {
	@Value("${spring.rabbitmq.queuename}")
	private String queueName;
	@Value("${spring.rabbitmq.exchange}")
	private String queueExchange;
	@Value("${spring.rabbitmq.routingkey}")
	private String routingkey;
	 
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    @Bean
    @ConfigurationProperties(prefix="spring.rabbitmq")
    public ConnectionFactory connectionFactory(){
        //publisherConfirms 消息确认回调
    	return new CachingConnectionFactory ();
    }
    
    @Bean
    public RabbitTemplate template(ConnectionFactory connectionFactory, MessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        //template.setChannelTransacted(false);
        template.setMandatory(true);
        template.setExchange(queueExchange);
        template.setRoutingKey(routingkey);
        template.setMessageConverter(converter);
        return template;
    }

    @Bean
    public Queue queue() {
        return new Queue(queueName, true);
    }

    @Bean
    public TopicExchange exchange(){
        return new TopicExchange(queueExchange);
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(routingkey);
    }
}
