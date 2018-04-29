package com.mmall.util;

import com.mmall.common.RedisShardedPool;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.ShardedJedis;

@Slf4j
public class RedisShardedPoolUtil {

    public static Long expire(String key,Integer exTime){
        ShardedJedis jedis = null;
        Long result = null;

        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.expire(key,exTime);
        } catch (Exception e) {
            log.error("set new expire of key:{}  error",key);
            RedisShardedPool.returnBrokenResource(jedis);
            return result;
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }

    public static String set(String key,String value){
        ShardedJedis jedis = null;
        String result = null;

        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.set(key,value);
        } catch (Exception e) {
            log.error("set key:{} value:{} error",key,value);
            RedisShardedPool.returnBrokenResource(jedis);
            return result;
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }

    public static String get(String key){
        ShardedJedis jedis = null;
        String result = null;

        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.get(key);
        } catch (Exception e) {
            log.error("get key:{} value error",key);
            RedisShardedPool.returnBrokenResource(jedis);
            return result;
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }

    //extime s
    public static String setEx(String key,String value,int exTime){
        ShardedJedis jedis = null;
        String result = null;

        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.setex(key,exTime,value);
        } catch (Exception e) {
            log.error("setex key:{} value:{} error",key,value);
            RedisShardedPool.returnBrokenResource(jedis);
            return result;
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }

    public static Long del(String key){
        ShardedJedis jedis = null;
        Long result = null;
        log.info("need to delete key {}",key);
        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.del(key);
        } catch (Exception e) {
            log.error("delete key:{} value error",key);
            RedisShardedPool.returnBrokenResource(jedis);
            return result;
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }

    public static void main(String[] args) {


        RedisShardedPoolUtil.set("key","value");
        String value = RedisShardedPoolUtil.get("key");

        RedisShardedPoolUtil.setEx("key1","value1",60*10);

        RedisShardedPoolUtil.expire("key",10*60);

        RedisShardedPoolUtil.del("key");

        System.out.println("end");
    }
}
