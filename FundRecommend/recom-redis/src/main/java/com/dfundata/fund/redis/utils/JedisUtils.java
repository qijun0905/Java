package com.dfundata.fund.redis.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dfundata.fund.redis.JedisCache;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Set;

/**
 * 封装一些常用的 java 和 redis 之间的操作
 *
 */
public class JedisUtils {

    /**
     * 将 实体类转化成的 JSON对象 存到 redis hash 中
     * 如果 field 存在，则更新，如果 field 不在，则新增
     * @param key
     * @param obj
     */
    public static void addAndUpdateJSONToRedisHash(JedisCache jedisCache,String key , JSONObject obj){
        Set<String> keySet = obj.keySet();
        for(String field : keySet){
            if(StringUtils.isNotBlank(obj.getString(field))){
                    jedisCache.hset(key,field,obj.getString(field));
            }
        }
    }

    /**
     * 将实体类转换的JSON对象保存到redis hash中
     * 如果 field 存在，则跳过，如果 field 不在，则新增
     * @param key
     * @param obj
     */
    public static void addJSONToRedisHash(JedisCache jedisCache,String key , JSONObject obj){
        Set<String> keySet = obj.keySet();
        for(String field : keySet){
            Boolean hexists = jedisCache.hexists(key, field);
            if(!hexists){
                jedisCache.hset(key,field,obj.getString(field));
            }
        }
    }

    /**
     *  根据 key 从 redis hash 中取出 JSONObject 对象
     * @param key
     * @return
     */
    public static JSONObject getEntryFromRedisHash(JedisCache jedisCache,String key){
        JSONObject object = null;
        Map<String, String> entry = jedisCache.hgetAll(key);
        if(entry != null){
            String jsonString = JSON.toJSONString(entry);
            object = JSONObject.parseObject(jsonString);
        }
        return object;
    }

    /**
     * 从 JSONObject 里面删除 Redis hash 字段中有的 key
     * @param obj {"key1":"key111","key2":"key222","key3":"key333"}
     * @param key xxxxxxx
     * @param filed  key2
     * @return {"key1":"key111","key3":"key333"}
     */
    public JSONObject delJSONRedisHashHasField(JedisCache jedisCache,JSONObject obj , String key , String filed){
        Boolean hexists = jedisCache.hexists(key, filed);
        if(hexists){
            obj.remove(filed);
        }
        return  obj;
    }


    /**
     * 清除 有序集合中 sortSetKey 里面的从 min 到 max 的members
     * @param sortSetKey
     * @param min
     * @param max
     * @return 去除的 members
     */
    public static Set<String> sortSetSizeFormatByTime(JedisCache jedisCache,String sortSetKey, double min, double max ){
        Set<String> keys = jedisCache.zrevrangeByScore(sortSetKey, max, min);
        for(String key:keys){
            jedisCache.zrem(sortSetKey,key);
        }
        return  keys;
    }

    public static Boolean sortSetHasMember(JedisCache jedisCache,String sortSetKey,String member){
        Long zrank = jedisCache.zrank(sortSetKey, member);
        if(zrank != null){
            return  true;
        }else{
            return false;
        }
    }

}
