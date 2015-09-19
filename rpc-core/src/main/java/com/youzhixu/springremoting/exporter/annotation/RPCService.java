package com.youzhixu.springremoting.exporter.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 必须指定服务名称（value），我们约定使用Spring Enviroment查找rpc.{value}.url查找rpc提供者的http访问地址<br>
 * 比如，value=user，服务消费方需要配置参数： rpc.user.url=http://youdomain.com，以便通过http访问服务提供者的服务<br>
 * value也可以用来当作netflix eureka client 查找服务提供方的vipAddress。
 * @author huisman 
 * @since 1.0.0
 * @createAt 2015年9月19日 下午4:09:46
 * @Copyright (c) 2015,Youzhixu.com Rights Reserved. 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface RPCService {
	/**
	 * <p>
	 * 服务名称，我们约定使用Spring Enviroment查找rpc.{value}.url查找rpc提供者http访问地址<br>
	 * 比如，value=user，服务消费方需要配置参数： rpc.user.url=http://youdomain.com，以便通过http访问服务提供者的服务<br>
	 * value也可以用来当作netflix eureka client 查找服务提供方的vipAddress。
	 * </p>
	 * 
	 * @since: 1.0.0
	 * @return
	 */
	String value();
}


