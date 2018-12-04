package com.lzgyy.plugins.redis.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;

/**
 * redis工具接口
 */
public interface RedisService {
	
	/**
	 * 获得redis模板
	 * @return
	 */
	RedisTemplate<String, Object> getRedisTemplate();
	
	// ================================================ 过期 ================================================
	
	/**
     * 设置redis过期时间
     * @param key	    哈希表名称
     * @param timeout 失效时间(毫秒)
     */
    void expire(String key, long timeout);
	
	/**
     * 从redis获得过期时间
     * @param key 缓存的键
     */
    long getExpire(String key);
	
	// ================================================ 删除 ================================================
    /**
     * 从redis删除
     * @param key 缓存的键
     */
    void delete(String key);
    
    // ================================================ Object ================================================
	/**
     * 设置redis值
     * @param key	缓存的键
     * @param value 缓存的值
     */
    void setObject(String key, Object value);
    
    /**
     * 设置redis值
     * @param key	    缓存的键	
     * @param value	    缓存的键
     * @param timeout 失效时间(毫秒)
     */
    void setObject(String key,Object value, long timeout);
    
    /**
     * 从redis获取值
     * @param key 缓存的键
     * @return
     */
    Object getObject(String key);
    
    // ================================================ Hash ================================================
    /**
     * 查看哈希表 hKey 中，给定域 hashKey 是否存在。
     * @param hKey 	    哈希表名称
     * @param hashKey 域的hashKey
     * @return  如果哈希表含有给定域，返回 1，如果哈希表不含有给定域，或 hashKey 不存在，返回 0
     */
    Boolean hashCheckHxists(String hKey, String hashKey);

    /**
     * 查询哈希表 hKey 中给定域 hashKey 的值。
     * @param  hKey		哈希表名称
     * @param  hashKey	域的hashKey
     * @return 给定域的值。当给定域不存在或是给定 key 不存在时，返回 nil 。
     */
    Object hashGet(String hKey, String hashKey);

    /**
     * 获取所有的散列值
     * @param key	哈希表名称
     * @return
     */
    Map<Object, Object> hashGetAll(String key);

    /**
     *哈希表 hKey 中的域 hashKey 的值加上增量 delta <BR>
     *增量也可以为负数，相当于对给定域进行减法操作。  如果 key 不存在，一个新的哈希表被创建并执行 HINCRBY 命令。<BR>
     *如果域 field 不存在，那么在执行命令前，域的值被初始化为 0 。  对一个储存字符串值的域 field 执行 HINCRBY 命令将造成一个错误。
     * @param hKey		哈希表名称
     * @param hashKey	域的hashKey
     * @param delta		增量
     * @return 执行 HINCRBY 命令之后，哈希表 hKey 中域 hashKey 的值。
     */
    Long hashIncrementLongOfHashMap(String hKey, String hashKey, Long delta);

    /**
     * 哈希表 hKey 中的域 hashKey 的值加上浮点值 增量 delta 。
     * @param hKey		哈希表名称
     * @param hashKey	域的hashKey
     * @param delta		增量
     * @return 执行 HINCRBY 命令之后，哈希表 hKey 中域 hashKey 的值。
     */
    Double hashIncrementDoubleOfHashMap(String hKey, String hashKey, Double delta);

    /**
     * 添加键值对到哈希表key中
     * @param key		哈希表名称
     * @param hashKey	域的hashKey
     * @param value		值
     */
    void hashPushHashMap(String key, Object hashKey, Object value);

    /**
     * 获取哈希表key中的所有域
     * @param key	哈希表名称
     * @return
     */
    Set<Object> hashGetAllHashKey(String key);

    /**
     * 获取散列中的字段数量
     * @param key	哈希表名称
     * @return
     */
    Long hashGetHashMapSize(String key);

    /**
     * 获取哈希中的所有值
     * @param key	哈希表名称
     * @return
     */
    List<Object> hashGetHashAllValues(String key);

    /**
     * 删除一个或多个哈希字段
     * @param key		哈希表名称
     * @param hashKeys	域的hashKey
     * @return 返回值为被成功删除的数量
     */
    Long hashDeleteHashKey(String key, Object... hashKeys);
    
    // ================================================ List ================================================
    /**
     * 从右向左存压栈
     * @param key
     * @param value
     */
    void listRightPushList(String key, String value);

    /**
     * 从右出栈
     * @param key
     * @return
     */
    Object listRightPopList(String key);

    /**
     * 从左向右存压栈
     * @param key
     * @param value
     */
    void listLeftPushList(String key, String value);

    /**
     * 从左出栈
     * @param key
     * @return
     */
    Object listLeftPopList(String key);

    /**
     * 集合list的长度
     * @param key
     * @return
     */
    Long listSize(String key);

    /**
     * 查询列表 key 中指定区间内的元素，区间以偏移量 start 和 stop 指定。
     * @param key
     * @param start
     * @param end
     * @return
     */
    List<Object> listRangeList(String key, Long start, Long end);

    /**
     * 移除key中值为value的i个,返回删除的个数；如果没有这个元素则返回0 
     * @param key
     * @param i
     * @param value
     * @return
     */
    Long listRemoveFromList(String key, long i, Object value);

    /**
     * 根据下标查询list中某个值
     * @param key
     * @param index
     * @return
     */
    Object listIndexFromList(String key, long index);

    /**
     * 根据下标设置value
     * @param key
     * @param index
     * @param value
     */
    void listSetValueToList(String key, long index, String value);

    /**
     * 裁剪(删除), 删除 除了[start,end]以外的所有元素 
     * @param key
     * @param start
     * @param end
     */
    void listTrimByRange(String key, Long start, Long end);

    // ================================================ Set ================================================
    /**
     * 将一个或多个 value 元素加入到集合 key 当中，已经存在于集合的 value 元素将被忽略。
     * @param key
     * @param values
     * @return 被添加到集合中的新元素的数量，不包括被忽略的元素。
     */
    Long setAddSetMap(String key, String...values);

    /**
     * 获取set集合的大小
     * @param key
     * @return
     */
    Long setGetSizeForSetMap(String key);

    /**
     * 获取set集合中的元素
     * @param key
     * @return
     */
    Set<Object> setGetMemberOfSetMap(String key);

    /**
     * 检查元素是不是set集合中的
     * @param key
     * @param o
     * @return
     */
    Boolean setCheckIsMemberOfSet(String key, Object o);

    // ================================================ String ================================================
    /**
     * 如果 key 已经存在并且是一个字符串， APPEND 命令将 value 追加到 key 原来的值的末尾。<BR>
     * 如果 key 不存在， APPEND 就简单地将给定 key 设为 value ，就像执行 SET key value 一样。
     * @param key
     * @param value
     * @return 追加 value 之后， key 中字符串的长度
     */
    Integer stringAppendString(String key, String value);

    /**
     * 获取指定键的值
     * @param key
     * @return
     */
    Object stringGetStringByKey(String key);

    /**
     * 获取存储在键上的字符串的子字符串
     * @param key
     * @param start
     * @param end
     * @return 截取后的子字符串
     */
    String stringGetSubStringFromString(String key, long start, long end);

    /**
     * 将键的整数值按给定的长整型数值增加
     * @param key
     * @param delta
     * @return 返回增长后的结果值
     */
    Long stringIncrementLongString(String key, Long delta);

    /**
     * 将键的整数值按给定的浮点型数值增加
     * @param key
     * @param delta
     * @return 返回增长后的结果值
     */
    Double stringIncrementDoubleString(String key, Double delta);

    /**
     * 设置指定键的值
     * @param key
     * @param value
     */
    void stringSetString(String key, String value);

    /**
     * 设置键的字符串值并返回其旧值
     * @param key
     * @param value
     * @return
     */
    Object stringGetAndSet(String key, String value);

    /**
     * 使用键和到期时间来设置值,时间单位默认为毫秒
     * @param key
     * @param value
     * @param timeout
     * @return
     */
    void stringSetValueAndExpireTime(String key, String value, long timeout);
	
}