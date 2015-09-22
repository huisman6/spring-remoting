package com.lianjia.springremoting.interceptor.adapter;

import com.lianjia.springremoting.interceptor.ServiceExporterRegistryInterceptor;
import com.lianjia.springremoting.serialize.Serializer;

/**
 * <p>
 *
 * </p>
 * 
 * @author huisman
 * @createAt 2015年9月21日 上午9:37:46
 * @since 1.0.0
 * @Copyright (c) 2015, Lianjia Group All Rights Reserved.
 */

public abstract class AbstractRPCServiceExporterInterceptor
		implements
			ServiceExporterRegistryInterceptor {

	@Override
	public Object resolveServcieExporter(Class<?> serviceInterface, Object serviceProvider) {
		return doResolveExporter(getAppName(serviceInterface), serviceProvider, serviceInterface,
				getSerializer(serviceInterface));
	}

	public abstract Object doResolveExporter(String appName, Object serviceBean,
			Class<?> serviceInterface, Serializer serializer);

	/**
	 * <p>
	 * 获取序列化的机制
	 * </p>
	 * 
	 * @since: 1.0.0
	 * @param serviceInterface
	 * @return
	 */
	public abstract Serializer getSerializer(Class<?> serviceInterface);


	/**
	 * <p>
	 * appName
	 * </p>
	 * 
	 * @since: 1.0.0
	 * @param serviceInterface
	 * @return
	 */
	public abstract String getAppName(Class<?> serviceInterface);


}
