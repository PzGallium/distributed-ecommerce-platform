package com.qiuzhitech.onlineshopping_09.db.mappers;

import com.qiuzhitech.onlineshopping_09.db.po.OnlineShoppingCommodity;

import java.util.List;

public interface OnlineShoppingCommodityMapper {
    int deleteByPrimaryKey(Long commodityId);

    int insert(OnlineShoppingCommodity record);

    int insertSelective(OnlineShoppingCommodity record);

    OnlineShoppingCommodity selectByPrimaryKey(Long commodityId);

    int updateByPrimaryKeySelective(OnlineShoppingCommodity record);

    int updateByPrimaryKey(OnlineShoppingCommodity record);

    int deductStock(Long itemID);

    List<OnlineShoppingCommodity> listItems();

    List<OnlineShoppingCommodity> selectByUserId(long userId);

    int revertStock(long itemID);

}