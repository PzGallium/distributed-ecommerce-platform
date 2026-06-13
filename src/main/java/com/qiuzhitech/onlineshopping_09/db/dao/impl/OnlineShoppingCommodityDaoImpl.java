package com.qiuzhitech.onlineshopping_09.db.dao.impl;

import com.qiuzhitech.onlineshopping_09.db.dao.OnlineShoppingCommodityDao;
import com.qiuzhitech.onlineshopping_09.db.mappers.OnlineShoppingCommodityMapper;
import com.qiuzhitech.onlineshopping_09.db.po.OnlineShoppingCommodity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
@Slf4j
@Repository
public class OnlineShoppingCommodityDaoImpl implements OnlineShoppingCommodityDao {
    /**
     * @param onlineShoppingCommodity
     * @return
     */

    @Resource
    private OnlineShoppingCommodityMapper onlineShoppingCommodityMapper;

    @Override
    public int insertCommodity(OnlineShoppingCommodity onlineShoppingCommodity) {
        return onlineShoppingCommodityMapper.insert(onlineShoppingCommodity);
    }

    /**
     * @param commodityId
     * @return
     */
    @Override
    public OnlineShoppingCommodity selectByCommodityId(long commodityId) {
        return  onlineShoppingCommodityMapper.selectByPrimaryKey(commodityId);
    }

    @Override
    public List<OnlineShoppingCommodity> selectByUserId(long userId) {
        return onlineShoppingCommodityMapper.selectByUserId(userId);
    }

    /**
     * @return
     */
    @Override
    public List<OnlineShoppingCommodity> listItems() {
        return onlineShoppingCommodityMapper.listItems();
    }

    /**
     * @param onlineShoppingCommodity
     * @return
     */
    @Override
    public int updateCommodity(OnlineShoppingCommodity onlineShoppingCommodity) {
        return onlineShoppingCommodityMapper.updateByPrimaryKey(onlineShoppingCommodity);
    }

    @Override
    public int deductStock(Long itemID) {
        return onlineShoppingCommodityMapper.deductStock(itemID);
    }

    @Override
    public int revertStock(long itemID) {
        return onlineShoppingCommodityMapper.revertStock(itemID);
    }

    @Override
    public List<OnlineShoppingCommodity> searchCommodityByKeyword(String keyword) {
        log.info("keyword:{}", keyword);
        return onlineShoppingCommodityMapper.searchCommodityByKeyword("%" + keyword + "%");
    }
}
