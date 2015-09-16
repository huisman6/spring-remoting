package com.youzhixu.springremoting.constant;

import org.springframework.remoting.caucho.HessianServiceExporter;
import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;

/**
 * <p>
 * rpc service 序列化的协议 <br>
 * 对象转换为字节流时的默认实现
 * </p>
 * 
 * @author huisman
 * @createAt 2015年9月16日 下午1:16:32
 * @since 1.0.0
 * @Copyright (c) 2015, Youzhixu.com All Rights Reserved.
 */
public enum Protocol {
	/**
	 * use spring
	 */
	HTTP_INVOKER(HttpInvokerServiceExporter.class), HESSIAN(HessianServiceExporter.class);
	private Class<?> provider;

	private Protocol(Class<?> provider) {
		this.provider = provider;
	}

	/**
	 * <p>
	 * 对象转换为字节流时的默认实现
	 * </p>
	 * 
	 * @since: 1.0.0
	 * @return
	 */
	public Class<?> getProvider() {
		return provider;
	}



}
