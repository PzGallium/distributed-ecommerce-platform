package com.qiuzhitech.onlineshopping_09.controller;

import com.qiuzhitech.onlineshopping_09.db.dao.OnlineShoppingCommodityDao;
import com.qiuzhitech.onlineshopping_09.db.po.OnlineShoppingCommodity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Controller
public class CommodityController {
    @Resource
    private OnlineShoppingCommodityDao  onlineShoppingCommodityDao;

    @RequestMapping("/addItem")
    public String AddCommodity() {
        return "add_commodity";
    }


    @PostMapping("/commodities")
    public String HandleAddCommodity(@RequestParam("commodityId") long commodityId,
                               @RequestParam("commodityName") String commodityName,
                               @RequestParam("commodityDesc") String commodityDesc,
                               @RequestParam("price") int price,
                               @RequestParam("creatorUserId") long creatorUserId,
                               @RequestParam("availableStock") int availableStock,
                               Map<String, Object> resultMap
                               ) {
        OnlineShoppingCommodity commodity = OnlineShoppingCommodity.builder()
                .commodityId(commodityId)
                .commodityName(commodityName)
                .commodityDesc(commodityDesc)
                .price(price)
                .creatorUserId(creatorUserId)
                .availableStock(availableStock)
                .totalStock(availableStock)
                .lockStock(0)
                .build();
        int ret = onlineShoppingCommodityDao.insertCommodity(commodity);
        resultMap.put("abc", commodity);
        return "add_commodity_success";
    }


    @GetMapping("/")
    public String GetAllCommodities(Map<String, Object> resultMap) {
        List<OnlineShoppingCommodity> onlineShoppingCommodities = onlineShoppingCommodityDao.listItems();
        resultMap.put("itemList", onlineShoppingCommodities);
        return "list_items";
    }

    @GetMapping("/commodities/{sellerId}")
    public String GetCommoditiesForSeller(@PathVariable long sellerId, Map<String, Object> resultMap) {
        List<OnlineShoppingCommodity> onlineShoppingCommodities = onlineShoppingCommodityDao.selectByUserId(sellerId);
        resultMap.put("itemList", onlineShoppingCommodities);
        return "list_items";
    }

    @GetMapping("/item/{commodityId}")
    public String GetCommodityDetail(@PathVariable long commodityId, Map<String, Object> resultMap) {
        OnlineShoppingCommodity onlineShoppingCommodity = onlineShoppingCommodityDao.selectByCommodityId(commodityId);
        resultMap.put("commodity", onlineShoppingCommodity);
        return "item_detail";
    }

}
