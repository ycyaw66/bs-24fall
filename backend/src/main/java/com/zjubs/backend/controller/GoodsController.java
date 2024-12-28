package com.zjubs.backend.controller;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zjubs.backend.controller.dto.SearchBody;
import com.zjubs.backend.model.Goods;
import com.zjubs.backend.service.GoodsService;
import com.zjubs.backend.utils.RespResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @PostMapping("/search")
    public RespResult searchGoods(@Validated @RequestBody SearchBody body) {
        List<Goods> goodsList = null;
        String platform = body.getPlatform();
        String keyword = body.getKeyword();
        if (platform.equals("jd")) {
            goodsList = goodsService.getJdGoods(keyword);
        } else if ("suning".equals(platform)) {
            // goodsList = goodsService.getSuningGoods(keyword);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("goods", goodsList);

        return RespResult.success(data);
    }
}
