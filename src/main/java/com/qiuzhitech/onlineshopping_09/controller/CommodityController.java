package com.qiuzhitech.onlineshopping_09.controller;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.qiuzhitech.onlineshopping_09.db.dao.OnlineShoppingCommodityDao;
import com.qiuzhitech.onlineshopping_09.db.po.OnlineShoppingCommodity;
import com.qiuzhitech.onlineshopping_09.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@Slf4j
@Controller
public class CommodityController {
    @Resource
    private OnlineShoppingCommodityDao  onlineShoppingCommodityDao;

    @RequestMapping("/addItem")
    public String AddCommodity() {
        return "add_commodity";
    }

    @Resource
    SearchService searchService;

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

    @GetMapping("/item/{itemID}")
    String itemDetail(@PathVariable("itemID") long itemID, Map<String, Object> resultMap) {
        OnlineShoppingCommodity onlineShoppingCommodity = onlineShoppingCommodityDao.selectByCommodityId(itemID);
        resultMap.put("commodity", onlineShoppingCommodity);
        return "item_detail";
    }

    @GetMapping("/")
    String itemList(Map<String, Object> resultMap) {
        List<OnlineShoppingCommodity> commodities = onlineShoppingCommodityDao.listItems();
        resultMap.put("itemList", commodities);
        return "list_items";
    }

    @GetMapping("/commodities/{sellerID}")
    String itemListBySeller(@PathVariable("sellerID") long sellerID, Map<String, Object> resultMap) {
        try (Entry entry = SphU.entry("listItemsRule", EntryType.IN, 1,
                sellerID)) {
            List<OnlineShoppingCommodity> commodities = onlineShoppingCommodityDao.selectByUserId(sellerID);
            resultMap.put("itemList", commodities);
        } catch (BlockException e) {
            log.error("ListItems got throttled" + e.toString());
            return "wait";
        }
        return "list_items";
    }

    @PostConstruct
    public void init() {
        List<FlowRule> rules = new ArrayList<>();
        FlowRule rule = new FlowRule();
        // Define resource
        rule.setResource("listItemsRule");
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        //Define QPS count
        rule.setCount(1);
        // FlowRule rule2 = new FlowRule();
        // rule2.setGrade(RuleConstant.FLOW_GRADE_QPS);
        // rule2.setCount(2);
        // rule2.setResource("HelloResource");
        rules.add(rule);
        // rules.add(rule2);
        FlowRuleManager.loadRules(rules);
    }


    @GetMapping("searchAction")
    public String searchCommodity(@RequestParam("keyWord") String keyword, Map<String, Object> resultMap) {
        List<OnlineShoppingCommodity> commodities = searchService.searchCommodityByES(keyword);
        resultMap.put("itemList", commodities);
        return "list_items";
    }
}
