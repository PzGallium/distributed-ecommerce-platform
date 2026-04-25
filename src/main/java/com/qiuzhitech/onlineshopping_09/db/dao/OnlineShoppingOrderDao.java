package com.qiuzhitech.onlineshopping_09.db.dao;

import com.qiuzhitech.onlineshopping_09.db.po.OnlineShoppingOrder;

import java.util.List;

public interface OnlineShoppingOrderDao {
    int insertOrder(OnlineShoppingOrder onlineShoppingOrder);
    OnlineShoppingOrder selectByOrderId(long orderId);
    List<OnlineShoppingOrder> listOrders();
    int updateOrder(OnlineShoppingOrder onlineShoppingOrder);

    OnlineShoppingOrder queryOrderByOrderNum(String orderNum);
}
