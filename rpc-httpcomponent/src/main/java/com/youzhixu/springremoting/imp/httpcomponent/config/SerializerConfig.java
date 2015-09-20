package com.youzhixu.springremoting.imp.httpcomponent.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.youzhixu.springremoting.serialize.JavaSerializer;
import com.youzhixu.springremoting.serialize.Serializer;

/**
 * <p>
 * rpc 序列化实现
 * </p> 
 * @author huisman 
 * @since 1.0.0
 * @createAt 2015年9月20日 下午9:09:27
 * @Copyright (c) 2015,Youzhixu.com Rights Reserved. 
 */
@Configuration
public class SerializerConfig {
  @Bean(name="serializer")
  public Serializer serializer(){
	  return new JavaSerializer();
  }
}


