package com.youzhixu.springremoting.imp.factory;

import org.springframework.beans.factory.FactoryBean;

/**
 * <p>
 *  创建httpclient
 * </p> 
 * @author huisman 
 * @since 1.0.0
 * @createAt 2015年9月20日 下午3:58:06
 * @Copyright (c) 2015,Youzhixu.com Rights Reserved. 
 */
public class RPCHttpClientFactoryBean implements FactoryBean<HttpClient>{

	@Override
	public HttpClient getObject() throws Exception {
		return null;
	}

	@Override
	public Class<?> getObjectType() {
		return HttpClient.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}


