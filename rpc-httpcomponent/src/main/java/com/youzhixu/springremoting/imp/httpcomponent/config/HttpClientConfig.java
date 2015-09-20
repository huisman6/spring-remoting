package com.youzhixu.springremoting.imp.httpcomponent.config;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 *  httpclient默认参数配置
 * </p> 
 * @author huisman 
 * @since 1.0.0
 * @createAt 2015年9月20日 下午2:05:51
 * @Copyright (c) 2015,Youzhixu.com Rights Reserved. 
 */
public class HttpClientConfig implements Serializable {
	private static final long serialVersionUID = 1L;
  	/**
	  * 连接超时
	 */
	private int connectionTimeout=20*1000;
	  /**
	 * 读取超时时间
	 */
	private int readTimeout=20*1000;
	
	/**
	 *从连接池中获取可用连接的超时时间
	 */
	private int requestTimeout=15*1000;
	
	/**
	 * 总共最大连接数
	 */
	private int maxConnection=500;
	
	/**
	 *每个不同的host是一个route，每个route上最多可以打开http连接数
	 */
	private int maxConnectionPerRoute;

	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public int getReadTimeout() {
		return readTimeout;
	}

	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	public int getRequestTimeout() {
		return requestTimeout;
	}

	public void setRequestTimeout(int requestTimeout) {
		this.requestTimeout = requestTimeout;
	}

	public int getMaxConnection() {
		return maxConnection;
	}

	public void setMaxConnection(int maxConnection) {
		this.maxConnection = maxConnection;
	}

	public int getMaxConnectionPerRoute() {
		return maxConnectionPerRoute;
	}

	public void setMaxConnectionPerRoute(int maxConnectionPerRoute) {
		this.maxConnectionPerRoute = maxConnectionPerRoute;
	}
	
	
}


