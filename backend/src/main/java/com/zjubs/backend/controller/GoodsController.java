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

import com.zjubs.backend.controller.dto.AuthorizationBody;
import com.zjubs.backend.controller.dto.HistoryBody;
import com.zjubs.backend.controller.dto.IsLikedBody;
import com.zjubs.backend.controller.dto.LikeBody;
import com.zjubs.backend.controller.dto.SearchBody;
import com.zjubs.backend.model.Goods;
import com.zjubs.backend.model.History;
import com.zjubs.backend.service.GoodsService;
import com.zjubs.backend.service.UserService;
import com.zjubs.backend.utils.RespResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private UserService userService;

    @PostMapping("/search")
    public RespResult searchGoods(@Validated @RequestBody SearchBody body) {
        String username = userService.getUsernameByToken(body.getAuthorization());
        String token = body.getAuthorization();
        if (!userService.checkToken(username, token)) {
            return RespResult.fail("用户未登录");
        }
        String platform = body.getPlatform();
        String keyword = body.getKeyword();
        List<Goods> goodsList = goodsService.getGoodsFromDB(platform, keyword);
        if (goodsList.isEmpty()) {
            if (platform.equals("jd")) {
                goodsList = goodsService.getJdGoods(keyword);
            } else if (platform.equals("suning")) {
                goodsList = goodsService.getSuningGoods(keyword);
            }
        }
        Map<String, Object> data = new HashMap<>();
        data.put("goods", goodsList);

        return RespResult.success(data);
    }

    @PostMapping("/isliked")
    public RespResult isGoodsLiked(@Validated @RequestBody IsLikedBody body) {
        String username = userService.getUsernameByToken(body.getAuthorization());
        String token = body.getAuthorization();
        if (!userService.checkToken(username, token)) {
            return RespResult.fail("用户未登录");
        }
        String isLiked = goodsService.isGoodsLiked(body.getUsername(), body.getGoods());

        Map<String, Object> data = new HashMap<String, Object>(1) {{
            put("isliked", isLiked);
        }};
        return RespResult.success(data);
    }
    
    @PostMapping("/like")
    public RespResult likeGoods(@Validated @RequestBody LikeBody body) {
        String username = userService.getUsernameByToken(body.getAuthorization());
        String token = body.getAuthorization();
        if (!userService.checkToken(username, token)) {
            return RespResult.fail("用户未登录");
        }
        goodsService.likeGoods(body.getUsername(), body.getGoods(), body.getOperation());
        return RespResult.success();
    }

    @PostMapping("/userlike")
    public RespResult getUserlike(@Validated @RequestBody AuthorizationBody body) {
        String username = userService.getUsernameByToken(body.getAuthorization());
        String token = body.getAuthorization();
        if (!userService.checkToken(username, token)) {
            return RespResult.fail("用户未登录");
        }
        List<Goods> goodsList = goodsService.getUserlike(username);
        Map<String, Object> data = new HashMap<String, Object>(1) {{
            put("goods", goodsList);
        }};
        return RespResult.success(data);
    }
    
    @PostMapping("/history")
    public RespResult getHistory(@Validated @RequestBody HistoryBody body) {
        List<History> historyList = goodsService.getHistory(body.getGoods());
        Map<String, Object> data = new HashMap<String, Object>(1) {{
            put("history", historyList);
        }};
        return RespResult.success(data);
    }
}
