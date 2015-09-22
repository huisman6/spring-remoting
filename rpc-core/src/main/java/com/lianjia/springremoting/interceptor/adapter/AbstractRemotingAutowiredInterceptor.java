package com.lianjia.springremoting.interceptor.adapter;

import com.lianjia.springremoting.constant.ServicePath;
import com.lianjia.springremoting.interceptor.AutowiredAnnotedTypeInterceptor;
import com.lianjia.springremoting.serialize.Serializer;
import com.lianjia.springremoting.url.UrlResolver;

/**
 * <p>
 * 专门负责处理注入@Remoting时求值
 * </p>
 * 
 * @author huisman
 * @createAt 2015年9月21日 上午9:26:22
 * @since 1.0.0
 * @Copyright (c) 2015, Lianjia Group All Rights Reserved.
 */

public abstract class AbstractRemotingAutowiredInterceptor
		implements
			AutowiredAnnotedTypeInterceptor {


	@Override
	public Object resolveAutowiredValue(Class<?> autowiredType) {
		return doResolveValue(autowiredType, getServiceUrl(autowiredType));
	}

	/**
	 * <p>
	 * 使用虚拟host,交给UrlResolver解析
	 * </p>
	 * 
	 * @See {@link UrlResolver}
	 * @param appName
	 * @param serviceInterface
	 * @return
	 * @since: 1.0.0
	 */
	private String getServiceUrl(Class<?> autowiredType) {
		return "http://" + getAppName(autowiredType) + ServicePath.PREFIX + "/"
				+ autowiredType.getName();
	}

	/**
	 * <p>
	 * 注入的值
	 * </p>
	 * 
	 * @since: 1.0.0
	 * @param serviceInterface
	 * @return
	 */
	public abstract Object doResolveValue(Class<?> autowiredType, String serviceUrl);

	/**
	 * <p>
	 * 获取序列化的机制
	 * </p>
	 * 
	 * @since: 1.0.0
	 * @param serviceInterface
	 * @return
	 */
	public abstract Serializer getSerializer(Class<?> autowiredType);

	/**
	 * <p>
	 * appName
	 * </p>
	 * 
	 * @since: 1.0.0
	 * @param serviceInterface
	 * @return
	 */
	public abstract String getAppName(Class<?> autowiredType);

}
