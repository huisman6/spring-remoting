package com.lianjia.springremoting.imp.httpcomponent.interceptor;

import java.lang.annotation.Annotation;

import org.apache.http.client.HttpClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;

import com.lianjia.springremoting.exporter.annotation.HttpService;
import com.lianjia.springremoting.imp.httpcomponent.executor.HttpComponentCustomizeHttpInvokerExecutor;
import com.lianjia.springremoting.imp.httpcomponent.factory.RPCHttpClientFactoryBean;
import com.lianjia.springremoting.interceptor.adapter.AbstractRemotingAutowiredInterceptor;
import com.lianjia.springremoting.serialize.Serializer;
import com.lianjia.springremoting.url.UrlResolver;

/**
 * @author huisman
 * @since 1.0.0
 * @createAt 2015年9月20日 下午12:16:08
 * @Copyright (c) 2015,Youzhixu.com Rights Reserved.
 */
public class HttpServiceInvokerInterceptor extends AbstractRemotingAutowiredInterceptor
		implements
			BeanFactoryAware {
	private UrlResolver urlResolver;
	private HttpClient rpcHttpClient;

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.urlResolver = beanFactory.getBean(UrlResolver.class);
		this.rpcHttpClient =
				(HttpClient) beanFactory.getBean(RPCHttpClientFactoryBean.RPC_HTTP_CLIENT);

	}

	public HttpServiceInvokerInterceptor() {
		super();
	}

	@Override
	public int getOrder() {
		return 0;
	}

	/**
	 * 负责HttpService的注入
	 */
	@Override
	public boolean accept(Annotation[] memberAnnotations, Class<?> autowiredType) {
		return autowiredType.getAnnotation(HttpService.class) != null;
	}



	@Override
	public Object doResolveValue(Class<?> autowiredType, String serviceUrl) {
		HttpInvokerProxyFactoryBean hp = new HttpInvokerProxyFactoryBean();
		HttpComponentCustomizeHttpInvokerExecutor httpExecutor;
		httpExecutor =
				new HttpComponentCustomizeHttpInvokerExecutor(urlResolver, rpcHttpClient,
						getSerializer(autowiredType));
		hp.setHttpInvokerRequestExecutor(httpExecutor);
		hp.setServiceInterface(autowiredType);
		hp.setServiceUrl(serviceUrl);
		hp.afterPropertiesSet();
		return hp.getObject();
	}

	@Override
	public Serializer getSerializer(Class<?> autowiredType) {
		return autowiredType.getAnnotation(HttpService.class).serializer().provider();
	}

	@Override
	public String getAppName(Class<?> autowiredType) {
		HttpService httpService = autowiredType.getAnnotation(HttpService.class);
		return httpService.value();
	}

}
