package com.youzhixu.springremoting.imp.httpcomponent.interceptor;

import java.lang.annotation.Annotation;

import org.apache.http.client.HttpClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.remoting.caucho.HessianProxyFactoryBean;

import com.youzhixu.springremoting.exporter.annotation.HessianService;
import com.youzhixu.springremoting.imp.httpcomponent.executor.HttpComponentHessianConnectionFactory;
import com.youzhixu.springremoting.imp.httpcomponent.factory.RPCHttpClientFactoryBean;
import com.youzhixu.springremoting.interceptor.adapter.AbstractRemotingAutowiredInterceptor;
import com.youzhixu.springremoting.serialize.Serializer;
import com.youzhixu.springremoting.url.UrlResolver;

/**
 * @author huisman
 * @since 1.0.0
 * @createAt 2015年9月20日 下午12:16:08
 * @Copyright (c) 2015,Youzhixu.com Rights Reserved.
 */
public class HessianServiceInvokerInterceptor extends AbstractRemotingAutowiredInterceptor
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

	public HessianServiceInvokerInterceptor() {
		super();
	}

	@Override
	public int getOrder() {
		return 0;
	}

	/**
	 * 负责HessianService 的注入
	 */
	@Override
	public boolean accept(Annotation[] memberAnnotations, Class<?> autowiredType) {
		return autowiredType.getAnnotation(HessianService.class) != null;
	}

	@Override
	public String getAppName(Class<?> autowiredType) {
		HessianService hessianService = autowiredType.getAnnotation(HessianService.class);
		return hessianService.value();
	}

	@Override
	public Object doResolveValue(Class<?> autowiredType, String serviceUrl) {
		HessianProxyFactoryBean hp = new HessianProxyFactoryBean();
		HttpComponentHessianConnectionFactory hessianConnectionFactory =
				new HttpComponentHessianConnectionFactory(urlResolver, this.rpcHttpClient);
		hp.setConnectionFactory(hessianConnectionFactory);
		hp.setServiceInterface(autowiredType);
		hp.setServiceUrl(serviceUrl);
		//use hessian2
		hp.setHessian2(true);
		hp.afterPropertiesSet();
		return hp.getObject();
	}

	@Override
	public Serializer getSerializer(Class<?> autowiredType) {
		return autowiredType.getAnnotation(HessianService.class).serializer().provider();
	}



}
