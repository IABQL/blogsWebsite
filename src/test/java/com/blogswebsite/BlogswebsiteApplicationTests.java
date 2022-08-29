package com.blogswebsite;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = BlogswebsiteApplication.class)
class BlogswebsiteApplicationTests {


    @Test
    void contextLoads() {

    }
    @Autowired
    private Producer producer;

    @Test
    public void test() throws InterruptedException {
        producer.send("test","hello");
        Thread.sleep(10000);
    }

    @Test
    public void testXss(){
        String parameter = "<script></script>";//模拟获取到的表单参数
        String s = StringEscapeUtils.escapeHtml3(parameter);//将参数转成这样表达：&lt;script&gt;&lt;/script&gt;
    }
}


@Component
class Producer{
    @Autowired
    private KafkaTemplate kafkaTemplate;
    public void send(String topic,String content){
        kafkaTemplate.send(topic,content);
        System.out.println(kafkaTemplate);
    }
}

@Component
class Consumer{
    @KafkaListener(topics = {"test"})
    public void write(ConsumerRecord record){
        System.out.println(record.value());
    }
}