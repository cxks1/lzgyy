package com.lzgyy.plugins.redis.config;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class RedisSerializerConfig<T> implements RedisSerializer<T>{
	

	@Override
	public byte[] serialize(T t) throws SerializationException {
		if(null == t) {
			return new byte[0];
		}
		return JSONArray.toJSONBytes(t,SerializerFeature.PrettyFormat,
                SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullStringAsEmpty,
                SerializerFeature.DisableCircularReferenceDetect,
                SerializerFeature.WriteNullListAsEmpty);
	}

	@Override
	public T deserialize(byte[] bytes) throws SerializationException {
		
		if(null == bytes || bytes.length == 0) {
			return null;
		}
		Object obj = JSONArray.parse(bytes, Feature.AllowUnQuotedFieldNames, Feature.AllowISO8601DateFormat, Feature.CustomMapDeserializer);
		
		return (T) obj ;
	}
}