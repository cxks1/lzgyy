package com.lzgyy.common.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;

import java.io.IOException;
import java.util.Locale;
  
/** 
 * JSON转换工具类 
 */  
public class JsonUtil {  
  
	private static ObjectMapper mapper;

    public static synchronized ObjectMapper getMapperInstance(boolean createNew) {
        if (createNew) {
            return new ObjectMapper();
        } else if (mapper == null) {
            mapper = new ObjectMapper();
            //对象属性按字母顺序排列
            mapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
            //在反序列化时，如果类中没有对应的属性，不抛出JsonMappingException异常
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            //序列化时，如果对象的某属性为null,生成Json字符串时不包含这个null属性
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            mapper.setLocale(Locale.CHINA);
        }
        return mapper;
    }

    /**
     * 将java对象转换为字符串
     * @param param java 对象
     * @return json字符串
     */
    public static String toJson(Object param){
        try {
            ObjectMapper objectMapper = JsonUtil.getMapperInstance(false);
            objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
            String dataJson =  objectMapper.writeValueAsString(param);
            return dataJson;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将java对象为字符串 类名作为json的顶级属性
     * @param param java 对象
     * @returnjson字符串
     */
    public static String toJsonWithRoot(Object param){
        try {
            ObjectMapper objectMapper = JsonUtil.getMapperInstance(false);
            objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, true);
            String dataJson =  objectMapper.writeValueAsString(param);
            return dataJson;
        }catch (Exception e){
           e.printStackTrace();
        }
        return null;
    }

    /**
     * 将json字符串转换为java对象
     * @param json json字符串
     * @param cls java对象类型
     * @param <T>
     * @return
     */
    public static <T> T jsonToBean(String json, Class<T> cls){
        try {
            ObjectMapper objectMapper = JsonUtil.getMapperInstance(false);
            objectMapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE,false);
            return objectMapper.readValue(json, cls);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将json字符串转换为java对象  ，解析字符串时，将类的名字或者别名作为顶级属性来解析
     * @param json json字符串
     * @param cls java对象类型
     * @param <T>
     * @return
     */
    public static <T> T jsonToBeanWithRoot(String json, Class<T> cls){
        try {
            ObjectMapper objectMapper = JsonUtil.getMapperInstance(false);
            objectMapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
            return objectMapper.readValue(json, cls);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将json字符串转换为java对象
     * @param json json字符串
     * @param typeReference 复杂的java对象类型
     * @param <T>
     * @return
     */
    public static <T> T jsonToBeanByTypeReference(String json, TypeReference typeReference){
        try {
            ObjectMapper objectMapper = JsonUtil.getMapperInstance(false);
            objectMapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE,false);
            return objectMapper.readValue(json, typeReference);
        } catch (Exception e) {
           e.printStackTrace();
            return null;
        }
    }
    /**
     * 将json字符串转换为java对象，解析字符串时，将类的名字或者别名作为顶级属性来解析
     * @param json json字符串
     * @param typeReference 复杂的java对象类型
     * @param <T>
     * @return
     */
    public static <T> T jsonToBeanByTypeReferenceWithRoot(String json, TypeReference typeReference){
        try {
            ObjectMapper objectMapper = JsonUtil.getMapperInstance(false);
            objectMapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE,true);
            return objectMapper.readValue(json, typeReference);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 从json字符串中读取出指定的节点
     *
     * @param json
     * @param key
     * @return
     * @throws JsonProcessingException
     * @throws IOException
     */
    public static JsonNode getValueFromJson(String json, String key) {
        ObjectMapper objectMapper = JsonUtil.getMapperInstance(false);
        objectMapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE,false);
        JsonNode node = null;
        try {
            node = objectMapper.readTree(json);
            return node.get(key);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}