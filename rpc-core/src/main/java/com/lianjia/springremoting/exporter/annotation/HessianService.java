package com.lianjia.springremoting.exporter.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.lianjia.springremoting.serialize.SerializeProvider;

/**
 * 必须指定服务名称（app）, 如何获取服务提供方的url取决于相关实现。<br>
 * 我们既可以使用Spring Enviroment配置rpc服务提供者的http访问地址：rpc.{app}.url=http://youdomain.com<br>
 * 比如，app=user，消费方需要配置参数： rpc.user.url=http://youdomain.com<br>
 * 也可以使用当作netflix eureka client查找服务提供方的地址，app值用作服务提供方的vipAddress。<br>
 * <br>
 * <br>
 * 使用 HessianServiceExporter 传输对象<br>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@RPCService("")
public @interface HessianService {
	/**
	 * @return 服务名称
	 * @since: 1.0.0
	 */
	String value();

	/**
	 * <p>
	 * 序列化实现类
	 * </p>
	 * 
	 * @return
	 * @since: 1.0.0
	 */
	SerializeProvider serializer() default SerializeProvider.NONE;
}
