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
@Configuration
public class HttpClientConfig implements Serializable {
	private static final long serialVersionUID = 1L;
  	/**
	  * 连接超时
	 */
	@Value("${rpc.httpclient.connectionTimeout:20000")
	private int connectionTimeout;
	  /**
	 * 读取超时时间
	 */
	@Value("${rpc.httpclient.readTimeout:20000")
	private int readTimeout;
	
	/**
	 *从连接池中获取可用连接的超时时间
	 */
	@Value("${rpc.httpclient.requestTimeout:5000")
	private int requestTimeout;
	
	/**
	 * 总共最大连接数
	 */
	@Value("${rpc.httpclient.maxConnection:400")
	private int maxConnection;
	
	/**
	 *每个不同的host是一个route，每个route上最多可以打开http连接数
	 */
	@Value("${rpc.httpclient.maxConnectionPerRoute:80")
	private int maxConnectionPerRoute;

	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public int getReadTimeout() {
		return readTimeout;
	}
	public int getRequestTimeout() {
		return requestTimeout;
	}

	public int getMaxConnection() {
		return maxConnection;
	}
	public int getMaxConnectionPerRoute() {
		return maxConnectionPerRoute;
	}

	@Override
	public String toString() {
		return "HttpClientConfig [connectionTimeout=" + connectionTimeout + ", readTimeout="
				+ readTimeout + ", requestTimeout=" + requestTimeout + ", maxConnection="
				+ maxConnection + ", maxConnectionPerRoute=" + maxConnectionPerRoute + "]";
	}

	
	
}


