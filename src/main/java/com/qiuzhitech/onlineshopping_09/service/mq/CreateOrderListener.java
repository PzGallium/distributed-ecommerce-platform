package com.qiuzhitech.onlineshopping_09.service.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qiuzhitech.onlineshopping_09.db.dao.OnlineShoppingCommodityDao;
import com.qiuzhitech.onlineshopping_09.db.dao.OnlineShoppingOrderDao;
import com.qiuzhitech.onlineshopping_09.db.po.OnlineShoppingOrder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.locks.LockSupport;

@Component
@Slf4j
@RocketMQMessageListener(topic = "CreateOrder", consumerGroup = "CreateOrderGroup1")
public class CreateOrderListener implements RocketMQListener<MessageExt>, RocketMQPushConsumerLifecycleListener {

    @Resource
    private OnlineShoppingOrderDao onlineShoppingOrderDao;
    @Resource
    private OnlineShoppingCommodityDao onlineShoppingCommodityDao;
    @Resource
    private MQService mqService;

    @SneakyThrows
    @Override
    public void onMessage(MessageExt messageExt) {
        String s = new String(messageExt.getBody());
        OnlineShoppingOrder order = JSON.parseObject(s, OnlineShoppingOrder.class);
        int res = onlineShoppingCommodityDao.deductStock(order.getCommodityId());
        if (res > 0) {
            order.setCreateTime(new Date());
            onlineShoppingOrderDao.insertOrder(order);
            log.info("Message process is successful: {}", order);
            mqService.sendDelayMessageFIFO("PaymentCheck", JSON.toJSONString(order), 3);
        } else {
            throw new RuntimeException("Create Order Failed");
        }

        long nanos = 1_000_000_000L; // seconds in nanoseconds
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
