package com.zjubs.backend.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.zjubs.backend.mapper.GoodsMapper;
import com.zjubs.backend.mapper.HistoryMapper;
import com.zjubs.backend.mapper.UserlikeMapper;
import com.zjubs.backend.model.Goods;
import com.zjubs.backend.model.History;
import com.zjubs.backend.model.User;
import com.zjubs.backend.model.Userlike;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GoodsService {
    
    @Autowired
    GoodsMapper goodsMapper;

    @Autowired
    UserlikeMapper userlikeMapper;

    @Autowired
    HistoryMapper historyMapper;

    @Autowired
    UserService userService;

    @Autowired
    EmailValidService emailValidService;

    public List<Goods> getJdGoods(String keyword) {
        try {
            log.info("--python script start--");
            InputStream inputStream = new ClassPathResource("script/jd.py").getInputStream();
            File tempScriptFile = File.createTempFile("jingdong", ".py");
            tempScriptFile.deleteOnExit();
            Files.copy(inputStream, tempScriptFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            
            String scriptPath = tempScriptFile.getAbsolutePath();
            ProcessBuilder processBuilder = new ProcessBuilder("python", scriptPath, keyword);
            processBuilder.redirectErrorStream(true);
            
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                log.info("Python script executed successfully");
            } else {
                log.error("Python script execution failed with exit code " + exitCode);
            }
            
            ObjectMapper objectMapper = new ObjectMapper();
            List<Goods> goodsList = objectMapper.readValue(output.toString(), new TypeReference<List<Goods>>() {});
            return insertGoodsList(goodsList, "jd", keyword);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Goods> getSuningGoods(String keyword) {
        try {
            log.info("--python script start--");
            InputStream inputStream = new ClassPathResource("script/suning.py").getInputStream();
            File tempScriptFile = File.createTempFile("suning", ".py");
            tempScriptFile.deleteOnExit();
            Files.copy(inputStream, tempScriptFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            
            String scriptPath = tempScriptFile.getAbsolutePath();
            ProcessBuilder processBuilder = new ProcessBuilder("python", scriptPath, keyword);
            processBuilder.redirectErrorStream(true);
            
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                log.info("Python script executed successfully");
            } else {
                log.error("Python script execution failed with exit code " + exitCode);
            }
            
            ObjectMapper objectMapper = new ObjectMapper();
            List<Goods> goodsList = objectMapper.readValue(output.toString(), new TypeReference<List<Goods>>() {});
            return insertGoodsList(goodsList, "suning", keyword);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Goods> getGoodsFromDB(String platform, String keyword) {
        // 缓存10分钟，超时则重新爬取
        List<Goods> goodsList = goodsMapper.selectByPlatformAndKeyword(platform, keyword);
        for (Goods goods : goodsList) {
            Timestamp timestamp = goods.getTimeStamp();
            Timestamp now = Timestamp.valueOf(LocalDateTime.now());
            if (now.getTime() - timestamp.getTime() > 600000) {
                goodsMapper.deleteGoodsByTitle(goods.getProductTitle());
            }
        }
        return goodsMapper.selectByPlatformAndKeyword(platform, keyword);
    }

    public List<Goods> insertGoodsList(List<Goods> goodsList, String platform, String keyword) {
        List<Goods> newGoodsList = new ArrayList<>();
        for (Goods goods : goodsList) {
            goods.setPlatform(platform);
            goods.setKeyword(keyword);
            Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
            goods.setTimeStamp(timestamp);
            Goods oldGoods = goodsMapper.getGoodsByTitle(goods.getProductTitle());
            if (oldGoods == null) {
                newGoodsList.add(goods);
                goodsMapper.insert(goods);
                insertHistory(goods);
            }
        }
        return newGoodsList;
    }

    public String isGoodsLiked(String username, String goods) {
        Userlike userlike = userlikeMapper.selectUserlike(username, goods);
        return userlike == null ? "0" : "1";
    }

    public void likeGoods(String username, String goods, String operation) {
        if (operation.equals("1")) {
            userlikeMapper.insert(new Userlike(username, goods));
        } else {
            userlikeMapper.deleteUserlike(username, goods);
        }
    }

    public List<Goods> getUserlike(String username) {
        List<Userlike> goodsList = userlikeMapper.selectUserlikeByUsername(username);
        List<Goods> goods = new ArrayList<>();
        for (Userlike userlike : goodsList) {
            Goods good = goodsMapper.getGoodsByTitle(userlike.getGoodsTitle());
            goods.add(good);
        }
        return goods;
    }

    public List<History> getHistory(String productTitle) {
        return historyMapper.getHistoryByTitle(productTitle);
    }

    public void insertHistory(Goods goods) {
        // 价格有变动时才插入历史记录
        History oldHistory = historyMapper.getLatestHistoryByTitle(goods.getProductTitle());
        if (oldHistory == null || !oldHistory.getProductPrice().equals(goods.getProductPrice())) {
            History history = new History();
            history.setProductTitle(goods.getProductTitle());
            history.setProductPrice(goods.getProductPrice());
            history.setTimeStamp(goods.getTimeStamp());
            // 如果价格降低，则提醒用户，价格字符串格式为 "￥123.45"
            if (oldHistory != null) {
                try {
                    double oldPrice = Double.parseDouble(oldHistory.getProductPrice().substring(1));
                    double newPrice = Double.parseDouble(goods.getProductPrice().substring(1));
                    if (newPrice < oldPrice) {
                        remindUser(goods, history);
                    }
                } catch (NumberFormatException e) {
                    log.error("Price format error: " + goods.getProductPrice());
                }
            }
            historyMapper.insert(history);
        }
    }

    public void remindUser(Goods goods, History history) {
        // 如果用户收藏了该商品，且价格降低，则发送邮件提醒
        List<Userlike> userlikeList = userlikeMapper.selectUserlikeByGoodsTitle(goods.getProductTitle());
        for (Userlike userlike : userlikeList) {
            String username = userlike.getUsername();
            String goodsTitle = userlike.getGoodsTitle();
            if (goodsTitle.equals(goods.getProductTitle())) {
                User user = userService.getUserByUsername(username);
                String email = user.getEmail();
                String content = "您收藏的商品 \"" + goods.getProductTitle() + "\" 价格降低啦，当前价格为 " + history.getProductPrice() + "，快去看看吧！";
                emailValidService.sendMailWithContent(email, content);
            }
        }
    }
}
