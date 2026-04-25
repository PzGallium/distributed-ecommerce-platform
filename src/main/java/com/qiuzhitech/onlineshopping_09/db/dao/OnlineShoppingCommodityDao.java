package com.qiuzhitech.onlineshopping_09.db.dao;

import com.qiuzhitech.onlineshopping_09.db.po.OnlineShoppingCommodity;

import java.util.List;

public interface OnlineShoppingCommodityDao {
    int insertCommodity(OnlineShoppingCommodity onlineShoppingCommodity);
    OnlineShoppingCommodity selectByCommodityId(long commodityId);
    List<OnlineShoppingCommodity> selectByUserId(long userId);
    List<OnlineShoppingCommodity> listItems();
    int updateCommodity(OnlineShoppingCommodity onlineShoppingCommodity);

}
