package com.qiuzhitech.onlineshopping_09.controller;

import com.qiuzhitech.onlineshopping_09.db.dao.OnlineShoppingCommodityDao;
import com.qiuzhitech.onlineshopping_09.db.dao.OnlineShoppingOrderDao;
import com.qiuzhitech.onlineshopping_09.db.po.OnlineShoppingCommodity;
import com.qiuzhitech.onlineshopping_09.db.po.OnlineShoppingOrder;
import com.qiuzhitech.onlineshopping_09.service.OrderService;
import com.qiuzhitech.onlineshopping_09.service.RedisService;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Controller
public class OrderController {

    @Resource
    private OnlineShoppingCommodityDao onlineShoppingCommodityDao;
    @Resource
    private OnlineShoppingOrderDao  onlineShoppingOrderDao;
    @Resource
    private OrderService orderService;
    @Autowired
    private RedisService redisService;

    @GetMapping("/commodity/buy/{userID}/{itemID}")
    public String buyCommodity(@PathVariable Long userID, @PathVariable Long itemID, Map<String, Object> resultMap) throws MQBrokerException, RemotingException, InterruptedException, MQClientException {
        OnlineShoppingOrder order = orderService.placeOrderFinal(userID, itemID);

        if (order != null) {
//            redisService.addToDenyList(String.valueOf(userID), String.valueOf(itemID));
            resultMap.put("resultInfo", "success");
            resultMap.put("orderNo", order.getOrderNo());
        } else {
            resultMap.put("resultInfo", "Out of Stock");
            resultMap.put("orderNo", "");
        }
        return "order_result";
    }

    @GetMapping("/commodity/orderQuery/{orderNum}")
    public String orderQuery(@PathVariable String orderNum, Map<String, Object> resultMap) {
        OnlineShoppingOrder order = onlineShoppingOrderDao.queryOrderByOrderNum(orderNum);
        resultMap.put("order", order);
        OnlineShoppingCommodity commodity = onlineShoppingCommodityDao.selectByCommodityId(order.getCommodityId());
        resultMap.put("commodity", commodity);
        return "order_check";
    }

    @GetMapping("/commodity/payOrder/{orderNum}")
    public String payOrder(@PathVariable String orderNum, Map<String, Object> resultMap) {
        OnlineShoppingOrder onlineShoppingOrder = onlineShoppingOrderDao.queryOrderByOrderNum(orderNum);
        onlineShoppingOrder.setOrderStatus(2);
        onlineShoppingOrder.setCreateTime(new Date());
        onlineShoppingOrderDao.updateOrder(onlineShoppingOrder);
        return orderQuery(orderNum, resultMap);
    }
}
