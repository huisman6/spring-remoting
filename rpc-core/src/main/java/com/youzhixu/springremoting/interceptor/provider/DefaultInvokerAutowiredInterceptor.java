package com.youzhixu.springremoting.interceptor.provider;

import java.lang.annotation.Annotation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.remoting.caucho.HessianProxyFactoryBean;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;

import com.youzhixu.springremoting.constant.ServicePath;
import com.youzhixu.springremoting.exporter.annotation.HessianService;
import com.youzhixu.springremoting.exporter.annotation.HttpService;
import com.youzhixu.springremoting.interceptor.AutowiredAnnotedTypeInterceptor;
import com.youzhixu.springremoting.invoker.annotation.Remoting;
import com.youzhixu.springremoting.invoker.executor.HttpComponentCustomizeHttpInvokerExecutor;
import com.youzhixu.springremoting.invoker.executor.HttpComponentHessianConnectionFactory;
import com.youzhixu.springremoting.url.UrlResolver;

/**
 * @author huisman 
 * @since 1.0.0
 * @createAt 2015年9月20日 下午12:16:08
 * @Copyright (c) 2015,Youzhixu.com Rights Reserved. 
 */
public class DefaultInvokerAutowiredInterceptor implements AutowiredAnnotedTypeInterceptor,BeanFactoryAware{
	private final Log logger=LogFactory.getLog(this.getClass());
	private ConfigurableListableBeanFactory beanFactory;
	private HttpComponentCustomizeHttpInvokerExecutor httpInvokerExecutor;
	private HttpComponentHessianConnectionFactory hessianConnectonFactory;
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory=(ConfigurableListableBeanFactory)beanFactory;
		this.httpInvokerExecutor=  this.beanFactory.getBean(HttpComponentCustomizeHttpInvokerExecutor.class);
		if (httpInvokerExecutor == null) {
			throw new IllegalArgumentException("@RPCService找不到Invoker Executor实现"+HttpComponentCustomizeHttpInvokerExecutor.class);
		}
		if(logger.isInfoEnabled()){
			logger.info("找到httpInvoker Executor:"+this.httpInvokerExecutor.getClass().getName());
		}
		this.hessianConnectonFactory=this.beanFactory.getBean(HttpComponentHessianConnectionFactory.class);
		if (hessianConnectonFactory==null) {
			throw new IllegalArgumentException("@RPCService Invoker Executor实现"+HttpComponentHessianConnectionFactory.class);
		}
		if (logger.isInfoEnabled()) {
			logger.info("找到hessian connectionFactory:"+this.hessianConnectonFactory.getClass().getName());
		}
	}

	@Override
	public int getOrder() {
		return 0;
	}

	@Override
	public Object resolveAutowiredValue(Class<?> autowiredType) {
		HttpService httpService = autowiredType.getAnnotation(HttpService.class);
		if (httpService != null) {
			return createHttpInvokerBean(httpService.value(), autowiredType);
		} 
		HessianService hessianService = autowiredType.getAnnotation(HessianService.class);
		if (hessianService != null) {
			return createHessianInvokerBean(hessianService.value(), autowiredType);
		}
		return null;
	}

	/**
	 * <p>
	 * 创建@HttpService标准的rpc invoker 代理
	 * </p>
	 * 
	 * @param appName
	 * @param serviceInterface
	 * @return
	 * @since: 1.0.0
	 */
	protected Object createHttpInvokerBean(String appName, Class<?> serviceInterface) {
		HttpInvokerProxyFactoryBean hp = new HttpInvokerProxyFactoryBean();
		hp.setHttpInvokerRequestExecutor(this.httpInvokerExecutor);
		hp.setServiceInterface(serviceInterface);
		hp.setServiceUrl(getServiceUrl(appName, serviceInterface));
		hp.afterPropertiesSet();
		return hp.getObject();
	}

	/**
	 * <p>
	 * 创建@HessianService标准的rpc invoker 代理
	 * </p>
	 * 
	 * @param appName
	 * @param serviceInterface
	 * @return
	 * @since: 1.0.0
	 */
	protected Object createHessianInvokerBean(String appName, Class<?> serviceInterface) {
		HessianProxyFactoryBean hp = new HessianProxyFactoryBean();
		hp.setConnectionFactory(this.hessianConnectonFactory);
		hp.setServiceInterface(serviceInterface);
		hp.setServiceUrl(getServiceUrl(appName, serviceInterface));
		hp.afterPropertiesSet();
		return hp.getObject();
	}

	/**
	  * <p>
	  *  使用虚拟host,交给UrlResolver解析
	  * </p> 
	  * @See {@link UrlResolver}
	  * @param appName
	  * @param serviceInterface
	  * @return
	  * @since: 1.0.0
	 */
	private String getServiceUrl(String appName, Class<?> serviceInterface) {
		return "rpc://"+appName+ServicePath.PREFIX + "/" + serviceInterface.getName();
	}


	/**
	 * 负责Remoting.class的值解析
	 */
	@Override
	public boolean accept(Annotation[] memberAnnotations, Class<?> autowiredType) {
		if (memberAnnotations == null || memberAnnotations.length == 0) {
			return false;
		}
		for (Annotation annotation : memberAnnotations) {
			return annotation.annotationType() == Remoting.class;
		}
		return false;
	}

}


