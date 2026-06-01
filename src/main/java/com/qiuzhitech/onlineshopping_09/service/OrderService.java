package com.qiuzhitech.onlineshopping_09.service;

import com.alibaba.fastjson.JSON;
import com.qiuzhitech.onlineshopping_09.db.dao.OnlineShoppingCommodityDao;
import com.qiuzhitech.onlineshopping_09.db.dao.OnlineShoppingOrderDao;
import com.qiuzhitech.onlineshopping_09.db.po.OnlineShoppingCommodity;
import com.qiuzhitech.onlineshopping_09.db.po.OnlineShoppingOrder;
import com.qiuzhitech.onlineshopping_09.service.mq.MQService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.UUID;
@Slf4j
@Service
public class OrderService {
    @Resource
    private OnlineShoppingOrderDao onlineShoppingOrderDao;
    @Resource
    private OnlineShoppingCommodityDao onlineShoppingCommodityDao;
    @Resource
    private RedisService redisService;
    @Resource
    MQService mqService;

    public OnlineShoppingOrder createOrderSQL(Long userID, Long itemID) {
        int res = onlineShoppingCommodityDao.deductStock(itemID);
        if (res > 0) {
            return createOrder(userID, itemID, true);
        } else {
            log.error("Order Is Not Available");
            return null;
        }
    }

    public OnlineShoppingOrder placeOrderByDistributedLock(long userID, Long commodityID) {
        String requestID = UUID.randomUUID().toString();
        boolean res = redisService.tryToGetDistributedLock(commodityID, requestID, 30);
        if (res) {
            OnlineShoppingOrder order = placeOrderOriginal(userID, commodityID);
            redisService.releaseDistributedLock(commodityID, requestID, 30);
            return order;
        } else {
            log.error("Order Is Not Available");
            return null;
        }
    }

    public OnlineShoppingOrder placeOrderByRedis (Long userID, long commodityID) {

        String key = "OnlineShoppingCommodity_" + commodityID;
        long v = redisService.deductStock(key);
        if (v < 0) {
            log.error("Order Is Not Available :" + commodityID);
            return null;
        } else {
            return placeOrderOriginal(userID, commodityID);
        }
    }


    public OnlineShoppingOrder placeOrderOriginal(Long userID, Long itemID) {
        OnlineShoppingCommodity onlineShoppingCommodity = onlineShoppingCommodityDao.selectByCommodityId(itemID);
        if (onlineShoppingCommodity.getAvailableStock() > 0) {
            log.info("Order is placed");
            onlineShoppingCommodity.setAvailableStock(onlineShoppingCommodity.getAvailableStock() - 1);
            onlineShoppingCommodityDao.updateCommodity(onlineShoppingCommodity);
            return createOrder(userID, itemID, true);
        } else {
            log.error("Order Not Available");
            return null;
        }
    }

    public OnlineShoppingOrder createOrder(Long userID, Long itemID, boolean shouldInsertDB) {
        OnlineShoppingOrder order = OnlineShoppingOrder.builder()
                .commodityId(itemID)
                .userId(userID)
                .orderNo(UUID.randomUUID().toString())
                .orderStatus(1) //1: pending 2: finish 99: invalid
                .orderAmount(1L)
                .createTime(new Date())
                .build();
        if (shouldInsertDB) {
            onlineShoppingOrderDao.insertOrder(order);
        }
        return order;
    }
    public OnlineShoppingOrder placeOrderFinal (Long userID, long commodityID) throws MQBrokerException, RemotingException, InterruptedException, MQClientException {

        String key = "OnlineShoppingCommodity_" + commodityID;
        long v = redisService.deductStock(key);
        if (v < 0) {
            log.error("Order Is Not Available :" + commodityID);
            return null;
        } else {
            OnlineShoppingOrder order = createOrder(userID, commodityID,false);
            mqService.sendMessageFIFO("CreateOrder", JSON.toJSONString(order));
            return order;
        }
    }
}

