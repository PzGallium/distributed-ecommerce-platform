package com.qiuzhitech.onlineshopping_09.service.mq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.LockSupport;

@Component
@Slf4j
@RocketMQMessageListener(topic = "topic1", consumerGroup = "consumerGroup1")
public class TestConsumerListener implements RocketMQListener<MessageExt>, RocketMQPushConsumerLifecycleListener {

    @Override
    public void onMessage(MessageExt messageExt) {
        String s = new String(messageExt.getBody());
        long nanos = 3_000_000_000L; // seconds in nanoseconds
        LockSupport.parkNanos(nanos);
    }

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMax(1);
        defaultMQPushConsumer.setConsumeThreadMin(1);
        defaultMQPushConsumer.setConsumeTimeout(1);
        defaultMQPushConsumer.setMaxReconsumeTimes(2); //retry 2 times + 1
        //to Dead Letter Queue if fail
    }
}
