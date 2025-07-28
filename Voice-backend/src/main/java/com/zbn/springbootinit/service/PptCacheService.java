package com.zbn.springbootinit.service;

import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

public class PptCacheService {

    private final RedissonClient redissonClient;
    private static final String REDIS_KEY_PREFIX = "ppt_text_cache:";

    public PptCacheService(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    /**
     * 获取缓存中的文本
     */
    public String getCachedText(String fileHash) {
        RMap<String, String> cacheMap = redissonClient.getMap(REDIS_KEY_PREFIX + "texts");
        return cacheMap.get(fileHash);
    }

    /**
     * 缓存文本
     */
    public void cacheText(String fileHash, String textContent) {
        RMap<String, String> cacheMap = redissonClient.getMap(REDIS_KEY_PREFIX + "texts");
        cacheMap.put(fileHash, textContent);
    }
}