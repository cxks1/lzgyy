package com.lzgyy.plugins.redis.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.alibaba.dubbo.config.annotation.Service;
import com.lzgyy.plugins.redis.service.RedisService;

/**
 * redis工具接口实现类
 */
@Service(timeout=5000, version="1.0.0")
public class RedisServiceImpl implements RedisService{
	
	private static final Logger logger = LogManager.getLogger(RedisServiceImpl.class);

	@Resource
    private RedisTemplate<String, Object> redisTemplate;
	
	public RedisTemplate<String, Object> getRedisTemplate(){
		return redisTemplate;
	}
	
	// ================================================ 过期 ================================================
	@Override
    public void expire(String key, long timeout) {
		logger.info("redis expire key="+key+", timeout="+timeout);
    	redisTemplate.expire(key, timeout, TimeUnit.MILLISECONDS);
    }
	
	@Override
	public long getExpire(String key) {
		logger.info("redis getExpire key="+key);
		return redisTemplate.getExpire(key);
	}
	
	// ================================================ 删除 ================================================
    @Override
    public void delete(String key) {
    	logger.info("redis delete key="+key);
    	redisTemplate.delete(key);
    }
	
    // ================================================ Object ================================================
    @Override
    public void setObject(String key, Object value) {
    	logger.info("redis setObject key="+key+", value="+value);
        ValueOperations<String, Object> vo = redisTemplate.opsForValue();
        vo.set(key, value);
    }
    
    @Override
    public void setObject(String key, Object value, long timeout) {
    	logger.info("redis setObject key="+key+", value="+value+", timeout="+timeout);
        ValueOperations<String, Object> vo = redisTemplate.opsForValue();
        vo.set(key, value, timeout, TimeUnit.MILLISECONDS);
    }

    @Override
    public Object getObject(String key) {
    	logger.info("redis getObject key="+key);
        ValueOperations<String, Object> vo = redisTemplate.opsForValue();
        return vo.get(key);
    }
    
    // ================================================ Hash ================================================
    @Override
    public Boolean hashCheckHxists(String hKey, String hashKey) {
    	logger.info("redis hashCheckHxists hKey="+hKey+", hashKey="+hashKey);
    	return redisTemplate.opsForHash().hasKey(hKey, hashKey);
    }

    @Override
    public Object hashGet(String hKey, String hashKey) {
    	logger.info("redis hashGet hKey="+hKey+", hashKey="+hashKey);
    	return redisTemplate.opsForHash().get(hKey, hashKey);
    }

    @Override
    public Map<Object, Object> hashGetAll(String key) {
    	logger.info("redis hashGetAll key="+key);
    	return redisTemplate.opsForHash().entries(key);
    }

    @Override
    public Long hashIncrementLongOfHashMap(String hKey, String hashKey, Long delta) {
    	logger.info("redis hashIncrementLongOfHashMap hKey="+hKey+", hashKey="+hashKey+", delta="+delta);
    	return redisTemplate.opsForHash().increment(hKey, hashKey, delta);
    }

    @Override
    public Double hashIncrementDoubleOfHashMap(String hKey, String hashKey, Double delta) {
    	logger.info("redis hashIncrementDoubleOfHashMap hKey="+hKey+", hashKey="+hashKey+", delta="+delta);
    	return redisTemplate.opsForHash().increment(hKey, hashKey, delta);
    }

    @Override
    public void hashPushHashMap(String key, Object hashKey, Object value) {
    	logger.info("redis hashPushHashMap key="+key+", hashKey="+hashKey+", value="+value);
    	redisTemplate.opsForHash().put(key, hashKey, value);
    }

    @Override
    public Set<Object> hashGetAllHashKey(String key) {
    	logger.info("redis hashGetAllHashKey key="+key);
    	return redisTemplate.opsForHash().keys(key);
    }

    @Override
    public Long hashGetHashMapSize(String key) {
    	logger.info("redis hashGetHashMapSize key="+key);
    	return redisTemplate.opsForHash().size(key);
    }

    @Override
    public List<Object> hashGetHashAllValues(String key) {
    	logger.info("redis hashGetHashAllValues key="+key);
    	return redisTemplate.opsForHash().values(key);
    }

    @Override
    public Long hashDeleteHashKey(String key, Object... hashKeys) {
    	logger.info("redis hashDeleteHashKey key="+key+", hashKeys="+hashKeys);
    	return redisTemplate.opsForHash().delete(key, hashKeys);
    }
    
    // ================================================ List ================================================
    @Override
    public void listRightPushList(String key, String value) {
    	logger.info("redis listRightPushList key="+key+", value="+value);
    	redisTemplate.opsForList().rightPush(key, value);
    }

    @Override
    public Object listRightPopList(String key) {
    	logger.info("redis listRightPopList key="+key);
    	return redisTemplate.opsForList().rightPop(key);
    }

    @Override
    public void listLeftPushList(String key, String value) {
    	logger.info("redis listLeftPushList key="+key+", value="+value);
    	redisTemplate.opsForList().leftPush(key, value);
    }

    @Override
    public Object listLeftPopList(String key) {
    	logger.info("redis listLeftPopList key="+key);
    	return redisTemplate.opsForList().leftPop(key);
    }

    @Override
    public Long listSize(String key) {
    	logger.info("redis listSize key="+key);
    	return redisTemplate.opsForList().size(key);
    }

    @Override
    public List<Object> listRangeList(String key, Long start, Long end) {
    	logger.info("redis listRangeList key="+key+", start="+start+", end="+end);
    	return redisTemplate.opsForList().range(key, start, end);
    }

    @Override
    public Long listRemoveFromList(String key, long i, Object value) {
    	logger.info("redis listRemoveFromList key="+key+", i="+i+", value="+value);
    	return redisTemplate.opsForList().remove(key, i, value);
    }

    @Override
    public Object listIndexFromList(String key, long index) {
    	logger.info("redis listIndexFromList key="+key+", index="+index);
    	return redisTemplate.opsForList().index(key, index);
    }

    @Override
    public void listSetValueToList(String key, long index, String value) {
    	logger.info("redis listSetValueToList key="+key+", index="+index+", value="+value);
    	redisTemplate.opsForList().set(key, index, value);
    }

    @Override
    public void listTrimByRange(String key, Long start, Long end) {
    	logger.info("redis listTrimByRange key="+key+", start="+start+", end="+end);
    	redisTemplate.opsForList().trim(key, start, end);
    }

    // ================================================ Set ================================================
    @Override
    public Long setAddSetMap(String key, String...values) {
    	logger.info("redis setAddSetMap key="+key+", values="+values);
    	return redisTemplate.opsForSet().add(key, values);
    }

    @Override
    public Long setGetSizeForSetMap(String key) {
    	logger.info("redis setGetSizeForSetMap key="+key);
    	return redisTemplate.opsForSet().size(key);
    }

    @Override
    public Set<Object> setGetMemberOfSetMap(String key) {
    	logger.info("redis setGetMemberOfSetMap key="+key);
    	return redisTemplate.opsForSet().members(key);
    }

    @Override
    public Boolean setCheckIsMemberOfSet(String key, Object o) {
    	logger.info("redis setCheckIsMemberOfSet key="+key+", o="+o);
    	return redisTemplate.opsForSet().isMember(key, o);
    }

    // ================================================ String ================================================
    @Override
    public Integer stringAppendString(String key, String value){
    	logger.info("redis stringAppendString key="+key+", value="+value);
    	return redisTemplate.opsForValue().append(key, value);
    }

    @Override
    public Object stringGetStringByKey(String key) {
    	logger.info("redis stringGetStringByKey key="+key);
    	return redisTemplate.opsForValue().get(key);
    }

    @Override
    public String stringGetSubStringFromString(String key, long start, long end) {
    	logger.info("redis stringGetSubStringFromString key="+key+", start="+start+", end="+end);
    	return redisTemplate.opsForValue().get(key, start, end);
    }

    @Override
    public Long stringIncrementLongString(String key, Long delta) {
    	logger.info("redis stringIncrementLongString key="+key+", delta="+delta);
    	return redisTemplate.opsForValue().increment(key, delta);
    }

    @Override
    public Double stringIncrementDoubleString(String key, Double delta) {
    	logger.info("redis stringIncrementDoubleString key="+key+", delta="+delta);
    	return redisTemplate.opsForValue().increment(key, delta);
    }

    @Override
    public void stringSetString(String key, String value) {
    	logger.info("redis stringSetString key="+key+", value="+value);
    	redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public Object stringGetAndSet(String key, String value){
    	logger.info("redis stringGetAndSet key="+key+", value="+value);
    	return redisTemplate.opsForValue().getAndSet(key, value);
    }

    @Override
    public void stringSetValueAndExpireTime(String key, String value, long timeout) {
    	logger.info("redis stringSetValueAndExpireTime key="+key+", value="+value+", timeout="+timeout);
    	redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.MILLISECONDS);
    }
}