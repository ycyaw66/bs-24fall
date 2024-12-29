package com.zjubs.backend.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.zjubs.backend.mapper.GoodsMapper;
import com.zjubs.backend.model.Goods;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GoodsService {
    @Autowired GoodsMapper goodsMapper;

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
            insertGoodsList(goodsList, "jd", keyword);
            return goodsList;
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
            insertGoodsList(goodsList, "suning", keyword);
            return goodsList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Goods> getGoodsFromDB(String platform, String keyword) {
        return goodsMapper.selectByPlatformAndKeyword(platform, keyword);
    }

    public void insertGoodsList(List<Goods> goodsList, String platform, String keyword) {
        for (Goods goods : goodsList) {
            goods.setPlatform(platform);
            goods.setKeyword(keyword);
            goodsMapper.insert(goods);
        }
    }
}
