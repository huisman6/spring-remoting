package com.lianjia.springremoting.imp.httpcomponent.interceptor;

import java.lang.annotation.Annotation;

import org.apache.http.client.HttpClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.remoting.caucho.HessianProxyFactoryBean;

import com.lianjia.springremoting.exporter.annotation.HessianService;
import com.lianjia.springremoting.imp.httpcomponent.executor.HttpComponentHessianConnectionFactory;
import com.lianjia.springremoting.imp.httpcomponent.factory.RPCHttpClientHolder;
import com.lianjia.springremoting.interceptor.adapter.AbstractRemotingAutowiredInterceptor;
import com.lianjia.springremoting.serialize.Serializer;
import com.lianjia.springremoting.url.UrlResolver;

/**
 * @author huisman
 * @since 1.0.0
 * @createAt 2015年9月20日 下午12:16:08
 * @Copyright (c) 2015,Youzhixu.com Rights Reserved.
 */
public class HessianServiceInvokerInterceptor extends AbstractRemotingAutowiredInterceptor
		implements BeanFactoryAware {
	private UrlResolver urlResolver;
	private HttpClient rpcHttpClient;

	/**
	 * 因为HessianServiceInvokerInterceptor会优先实例化，导致@autowired没有被BeanPostProcessor处理（not getting
	 * processed by all BeanPostProcessors）
	 */
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.urlResolver = BeanFactoryUtils.beanOfType((ListableBeanFactory) beanFactory,
				UrlResolver.class, false, true);
		this.rpcHttpClient =
				(HttpClient) beanFactory.getBean(RPCHttpClientHolder.RPC_INTERNAL_HTTP_CLIENT);
		// FactoryBean，提前实例化
		// this.rpcHttpClient =
		// (HttpClient) BeanFactoryUtils.bean((ListableBeanFactory) beanFactory,
		// RPCHttpClientFactoryBean.class, false, true);
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
		// use hessian2
		hp.setHessian2(true);
		hp.afterPropertiesSet();
		return hp.getObject();
	}

	@Override
	public Serializer getSerializer(Class<?> autowiredType) {
		return autowiredType.getAnnotation(HessianService.class).serializer().provider();
	}

}
