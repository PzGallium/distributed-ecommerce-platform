package com.qiuzhitech.onlineshopping_09.service.mq;

import com.alibaba.fastjson.JSON;
import com.qiuzhitech.onlineshopping_09.db.dao.OnlineShoppingCommodityDao;
import com.qiuzhitech.onlineshopping_09.db.dao.OnlineShoppingOrderDao;
import com.qiuzhitech.onlineshopping_09.db.po.OnlineShoppingOrder;
import com.qiuzhitech.onlineshopping_09.service.RedisService;
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
@RocketMQMessageListener(topic = "PaymentCheck", consumerGroup = "PaymentCheckGroup1")
public class PaymentCheckListener implements RocketMQListener<MessageExt>, RocketMQPushConsumerLifecycleListener {

    @Resource
    private OnlineShoppingOrderDao onlineShoppingOrderDao;
    @Resource
    private OnlineShoppingCommodityDao onlineShoppingCommodityDao;
    @Resource
    private MQService mqService;
    @Resource
    private RedisService redisService;

    @SneakyThrows
    @Override
    public void onMessage(MessageExt messageExt) {
        String s = new String(messageExt.getBody());
        OnlineShoppingOrder historyOrder = JSON.parseObject(s, OnlineShoppingOrder.class);
        OnlineShoppingOrder order = onlineShoppingOrderDao.queryOrderByOrderNum(historyOrder.getOrderNo());
        int status = order.getOrderStatus();
        if (status == 1) {
            order.setOrderStatus(99);
            onlineShoppingOrderDao.updateOrder(order);
            log.info("order status is set to 99");
            onlineShoppingCommodityDao.revertStock(order.getCommodityId());
            log.info("DB is reverted");
            String key = "OnlineShoppingCommodity_" + order.getCommodityId();
            redisService.reverStock(key);
            log.info("Redis is reverted");
        } else if (status == 2) {
            log.info("Payment check has been successful");
        }
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
