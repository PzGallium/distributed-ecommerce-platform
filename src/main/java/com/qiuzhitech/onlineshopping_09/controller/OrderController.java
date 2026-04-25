package com.qiuzhitech.onlineshopping_09.controller;

import com.qiuzhitech.onlineshopping_09.db.dao.OnlineShoppingCommodityDao;
import com.qiuzhitech.onlineshopping_09.db.dao.OnlineShoppingOrderDao;
import com.qiuzhitech.onlineshopping_09.db.po.OnlineShoppingCommodity;
import com.qiuzhitech.onlineshopping_09.db.po.OnlineShoppingOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Controller
@Slf4j
public class OrderController {

    @Resource
    OnlineShoppingCommodityDao commodityDao;

    @Resource
    OnlineShoppingOrderDao orderDao;

    @RequestMapping("/commodity/buy/{userId}/{commodityId}")
    public String BuyCommodity(@PathVariable long userId,
                               @PathVariable long commodityId,
                               Map<String, Object> resultMap) {
        OnlineShoppingOrder order = createOnlineShoppingOrder(userId, commodityId);
        if (order != null) {
            resultMap.put("orderNo", order.getOrderNo());
            resultMap.put("resultInfo", "Place order success! Order number:" + order.getOrderNo());
        } else {
            resultMap.put("orderNo", "");
            resultMap.put("resultInfo", "Order create failed, check log for detail");
        }
        return "order_result";
    }


    @GetMapping("/commodity/orderQuery/{orderNum}")
    public String OrderCheck(@PathVariable String orderNum,
                               Map<String, Object> resultMap) {
        OnlineShoppingOrder order = orderDao.queryOrderByOrderNum(orderNum);
        resultMap.put("order", order);
        OnlineShoppingCommodity commodity = commodityDao.selectByCommodityId(order.getCommodityId());
        resultMap.put("commodity", commodity);
        return "order_check";
    }


    @GetMapping("/commodity/payOrder/{orderNum}")
    public String PayOrder(@PathVariable String orderNum,
                               Map<String, Object> resultMap) {
        OnlineShoppingOrder order = orderDao.queryOrderByOrderNum(orderNum);
        order.setPayTime(new Date());
        order.setOrderStatus(2);
        orderDao.updateOrder(order);
        return OrderCheck(orderNum, resultMap);
    }

    private OnlineShoppingOrder createOnlineShoppingOrder(long userId,
                                                          long commodityId) {
        OnlineShoppingCommodity onlineShoppingCommodity = commodityDao.selectByCommodityId(commodityId);
        Integer availableStock = onlineShoppingCommodity.getAvailableStock();
        if (availableStock > 0) {
            availableStock--;
            onlineShoppingCommodity.setAvailableStock(availableStock);
            int result = commodityDao.updateCommodity(onlineShoppingCommodity);
            if (result == 1) {
                OnlineShoppingOrder order = OnlineShoppingOrder.builder()
                        .userId(userId)
                        .commodityId(commodityId)
                        .orderNo(UUID.randomUUID().toString())
                        .orderAmount(1L)
                        .createTime(new Date())
                        .orderStatus(1)
                        // 1. pending payment
                        // 2. finish payment
                        // 99. overtime order
                        .build();
                orderDao.insertOrder(order);
                return order;
            }
        }
        log.warn("commodity out of stock, commodityId:" + onlineShoppingCommodity.getCommodityId());
        return null;

    }
}
