package com.youzhixu.springremoting.exporter.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.remoting.caucho.HessianServiceExporter;

/**
 * 使用 HessianServiceExporter 传输对象
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface HessianService {
	Class<?> provider() default HessianServiceExporter.class;

	/**
	 * <p>
	 * 服务名称，我们约定使用Spring Enviroment查找rpc.{appName}.url查找rpc提供者http访问地址<br>
	 * 比如，app=user，服务消费方需要配置参数： rpc.user.url=http://youdomain.com，以便通过http访问服务提供者的服务
	 * </p>
	 * 
	 * @since: 1.0.0
	 * @return
	 */
	String app();
}
