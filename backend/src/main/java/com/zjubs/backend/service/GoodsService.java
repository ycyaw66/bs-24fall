package com.zjubs.backend.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
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

    public List<Goods> getJdGoods(String keyword) {
        try {
            log.info("--python script start--");
            String scriptPath = new ClassPathResource("script/jd.py").getFile().getAbsolutePath();
            File scriptDir = new File(scriptPath).getParentFile();
            File outputFile = new File(scriptDir, "jd_search_result.json");
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
                log.info("Python script executed successfully" + output.toString());
            } else {
                log.error("Python script execution failed with exit code " + exitCode);
            }
            
            ObjectMapper objectMapper = new ObjectMapper();
            List<Goods> goodsList = objectMapper.readValue(outputFile, new TypeReference<List<Goods>>(){});
            return insertGoodsList(goodsList, "jd", keyword);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Goods> getSuningGoods(String keyword) {
        try {
            log.info("--python script start--");
            String scriptPath = new ClassPathResource("script/suning.py").getFile().getAbsolutePath();
            File scriptDir = new File(scriptPath).getParentFile();
            File outputFile = new File(scriptDir, "suning_search_result.json");
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
                log.info("Python script executed successfully" + output.toString());
            } else {
                log.error("Python script execution failed with exit code " + exitCode);
            }
            
            ObjectMapper objectMapper = new ObjectMapper();
            List<Goods> goodsList = objectMapper.readValue(outputFile, new TypeReference<List<Goods>>(){});
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
            if (now.getTime() - timestamp.getTime() > 6000) {
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
                History history = new History();
                history.setProductTitle(goods.getProductTitle());
                history.setProductPrice(goods.getProductPrice());
                history.setTimeStamp(timestamp);
                historyMapper.insert(history);
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
}
