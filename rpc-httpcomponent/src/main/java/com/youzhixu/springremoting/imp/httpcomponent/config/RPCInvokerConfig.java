package com.youzhixu.springremoting.imp.httpcomponent.config;

import org.apache.http.client.HttpClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;

import com.youzhixu.springremoting.imp.httpcomponent.factory.RPCHttpClientFactoryBean;
import com.youzhixu.springremoting.invoker.config.DefaultInvokerConfig;
import com.youzhixu.springremoting.invoker.executor.HttpComponentCustomizeHttpInvokerExecutor;
import com.youzhixu.springremoting.invoker.executor.HttpComponentHessianConnectionFactory;
import com.youzhixu.springremoting.url.SimpleUrlResolver;
import com.youzhixu.springremoting.url.UrlResolver;

/**
 * <p>
 * invoker executor&UrlResolver&HttpClientFactory
 * </p>
 * 
 * @author huisman
 * @createAt 2015年9月16日 上午11:39:02
 * @since 1.0.0
 * @Copyright (c) 2015, Youzhixu.com All Rights Reserved.
 */
@Configuration
@Import(DefaultInvokerConfig.class)
public class RPCInvokerConfig implements BeanFactoryAware {
	private UrlResolver urlResolver;
	private HttpClient rpcHttpClient;
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		ConfigurableListableBeanFactory bf=(ConfigurableListableBeanFactory)beanFactory;
		this.urlResolver=bf.getBean(UrlResolver.class);
		this.rpcHttpClient=bf.getBean("rpcHttpClient",HttpClient.class);
	}

	/**
	  * <p>
	  *  客户端如何解析服务提供者的url
	  * </p> 
	  * @return
	  * @since: 1.0.0
	 */
	@Bean(name="urlResolver")
	public UrlResolver urlResolver(){
		return new SimpleUrlResolver();
	}
	
	/**
	  * <p>
	  *  创建httpclient
	  * </p> 
	  * @return
	  * @since: 1.0.0
	 */
	@Bean(name="rpcHttpClient",autowire=Autowire.BY_NAME)
	public Object rpcHttpClientFactoryBean(){
		return new RPCHttpClientFactoryBean();
	}
	
	/**
	  * <p>
	  *  默认httpinvoker使用httpcomponent发送请求
	  * </p> 
	  * @return
	  * @since: 1.0.0
	 */
	@Bean(name="httpComponentCustomizeHttpInvokerExecutor")
	public HttpComponentCustomizeHttpInvokerExecutor httpComponentCustomizeHttpInvokerExecutor(){
		return new HttpComponentCustomizeHttpInvokerExecutor(urlResolver,rpcHttpClient);
	}
	
	/**
	  * <p>
	  *  hessian自定义的connectionFactory
	  * </p> 
	  * @return
	  * @since: 1.0.0
	 */
	@Bean(name="httpComponentHessianConnectionFactory")
	public HttpComponentHessianConnectionFactory httpComponentHessianConnectionFactory(){
		return new HttpComponentHessianConnectionFactory(urlResolver,rpcHttpClient);
	}
}