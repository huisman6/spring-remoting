package com.youzhixu.springremoting.exporter.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
  * 必须指定服务名称（app）, 如何获取服务提供方的url取决于相关实现。<br>
 * 我们既可以使用Spring Enviroment配置rpc服务提供者的http访问地址：rpc.{app}.url=http://youdomain.com<br>
 * 比如，app=user，消费方需要配置参数： rpc.user.url=http://youdomain.com<br>
 * 也可以使用当作netflix eureka client查找服务提供方的地址，app值用作服务提供方的vipAddress。<br>
 * <br><br>
 * 默认使用spring http invoker实现对象传输<br>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@RPCService(value = "")
@Documented
public @interface HttpService {
	/**
	  * @return 服务名称
	  * @since: 1.0.0
	 */
	String value();
}
