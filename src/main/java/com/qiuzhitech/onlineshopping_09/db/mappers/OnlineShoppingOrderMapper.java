package com.qiuzhitech.onlineshopping_09.db.mappers;

import com.qiuzhitech.onlineshopping_09.db.po.OnlineShoppingOrder;

import java.util.List;

public interface OnlineShoppingOrderMapper {
    int deleteByPrimaryKey(Long orderId);

    int insert(OnlineShoppingOrder record);

    int insertSelective(OnlineShoppingOrder record);

    OnlineShoppingOrder selectByPrimaryKey(Long orderId);

    int updateByPrimaryKeySelective(OnlineShoppingOrder record);

    int updateByPrimaryKey(OnlineShoppingOrder record);

    List<OnlineShoppingOrder> listOrders();

    OnlineShoppingOrder selectByOrderNum(String orderNum);
}