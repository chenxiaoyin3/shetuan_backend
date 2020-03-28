package com.hongyu.util.redis;

import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by xyy on 2019/5/20.
 *
 * @author xyy
 *
 * 以Redis存储权限相关的数据 用于鉴权缓存
 */
@Service("redisUtils")
public class RedisUtils {
    public final static String REDIS_PING_TEST_RESULT = "PONG";

    @Autowired
    @Qualifier("redisTemplate")
    public RedisTemplate redisTemplate;

    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }

    public void deleteBypPrex(String prex) {
    	Set<String> keys = redisTemplate.keys(prex);
    	if(CollectionUtils.isNotEmpty(keys)) {
    		redisTemplate.delete(keys);
    	}
    }
    public boolean ping() {
        boolean flag = false;
        try {
            String result = redisTemplate.getConnectionFactory().getConnection().ping();
            if (REDIS_PING_TEST_RESULT.equals(result)) {
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 基本的对象，Integer、String、实体类等
     *
     * @param key   缓存的键值
     * @param value 缓存的值
     * @return 缓存的对象
     */
    public <T> ValueOperations<String, T> setObject(String key, T value) {
        ValueOperations<String, T> operation = redisTemplate.opsForValue();
        operation.set(key, value);
        return operation;
    }

    /**
     * 获得基本对象。
     *
     * @param key 缓存键值
     * @return 缓存键值对应的数据
     */
    public <T> T getObject(String key) {
        ValueOperations<String, T> operation = redisTemplate.opsForValue();
        return operation.get(key);
    }

    /**
     * 缓存List数据
     *
     * @param key      缓存的键值
     * @param dataList 待缓存的List数据
     * @return 缓存的对象
     */
    public <T> ListOperations<String, T> setList(String key, List<T> dataList) {
        ListOperations listOperation = redisTemplate.opsForList();
        if (null != dataList) {
            int size = dataList.size();
            for (int i = 0; i < size; i++) {
                listOperation.rightPush(key, dataList.get(i));
            }
        }

        return listOperation;
    }

    /**
     * 获得缓存的list对象
     *
     * @param key 缓存的键值
     * @return 缓存键值对应的数据
     */
    public <T> List<T> getList(String key) {
        List<T> dataList = new ArrayList<T>();
        ListOperations<String, T> listOperation = redisTemplate.opsForList();
        Long size = listOperation.size(key);
        for (int i = 0; i < size; i++) {
            dataList.add((T) listOperation.leftPop(key));
        }
        return dataList;
    }

    /**
     * 缓存Set
     *
     * @param key     缓存键值
     * @param dataSet 缓存的数据
     * @return 缓存数据的对象
     */
    public <T> BoundSetOperations<String, T> setSet(String key, Set<T> dataSet) {
        BoundSetOperations<String, T> setOperation = redisTemplate.boundSetOps(key);
        Iterator<T> it = dataSet.iterator();
        while (it.hasNext()) {
            setOperation.add(it.next());
        }
        return setOperation;
    }

    /**
     * 获得缓存的set
     *
     * @param key
     * @return
     */
    public Set<T> getSet(String key) {
        Set<T> dataSet = new HashSet<>();
        BoundSetOperations<String, T> operation = redisTemplate.boundSetOps(key);
        Long size = operation.size();
        for (int i = 0; i < size; i++) {
            dataSet.add(operation.pop());
        }
        return dataSet;
    }


    /*-----------------------------------------------------------------------------------------------------------------
     opsForHash可用于操作嵌套的Map结构,形如<H, HK, HV>

                      "name":"admin"
                      1:"URL1"
     hyRole:1   ->    2:"URL2"
                      3:"URL2"
                      4:"URL4"

     (key,    <String/Integer, Value>)
    ----------------------------------------------------------------------------------------------------------------- */
    /**
     * 保存Map,其中HK为String
     * TODO 使用批量保存代替for循环
     * @param key
     * @param dataMap
     * @return
     */
    public <T> HashOperations<String, String, T> setMap(String key, Map<String, T> dataMap) {
        HashOperations hashOperations = redisTemplate.opsForHash();
        if (null != dataMap) {
            for (Map.Entry<String, T> entry : dataMap.entrySet()) {
                hashOperations.put(key, entry.getKey(), entry.getValue());
            }
        }
        return hashOperations;
    }

    /**
     * 获取所有的Map,其中HK为String
     * @param key
     * @return
     */
    public <T> Map<String, T> getMap(String key) {
        Map<String, T> map = redisTemplate.opsForHash().entries(key);
        return map;
    }


    /**
     * 保存Map,其中HK为Integer
     * TODO 使用批量保存代替for循环
     * @param key
     * @param dataMap
     * @return
     */
    public <T> HashOperations<String, Integer, T> setIntegerMap(String key, Map<Integer, T> dataMap) {
        HashOperations hashOperations = redisTemplate.opsForHash();
        if (null != dataMap) {
            for (Map.Entry<Integer, T> entry : dataMap.entrySet()) {
                hashOperations.put(key, entry.getKey(), entry.getValue());
            }
        }
        return hashOperations;
    }

    /**
     * 获取所有的Map,其中HK为Integer
     * @param key
     * @return
     */
    public <T> Map<Integer, T> getIntegerMap(String key) {
        Map<Integer, T> map = redisTemplate.opsForHash().entries(key);
        return map;
    }

    /**
     * 通过key和HK,直接获取HV
     * 其中HK为String
     * */
    public Object getHV(String key, String HK){
        return redisTemplate.opsForHash().get(key, HK);
    }

    /**
     * 通过key和HK,直接获取HV
     * 其中HK为Integer
     * */
    public Object getHV(String key, Integer HK){
        return redisTemplate.opsForHash().get(key, HK);
    }

    /**
     * 通过key获取HK的集合
     * */
    public <T> Set<T> getHKs(String key){
        return redisTemplate.opsForHash().keys(key);
    }

    /**
     * 通过key获取HV的集合
     * */
    public <T> List<T> getHVs(String key){
        return redisTemplate.opsForHash().values(key);
    }
}
