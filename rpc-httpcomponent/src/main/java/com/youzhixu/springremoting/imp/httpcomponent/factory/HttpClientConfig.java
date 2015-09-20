package com.youzhixu.springremoting.imp.httpcomponent.factory;

import java.io.Serializable;

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
//	@Value("${rpc.httpclient.connectionTimeout:15000}")
	private int connectionTimeout=15000;
	  /**
	 * 读取超时时间
	 */
//	@Value("${rpc.httpclient.readTimeout:20000}")
	private int readTimeout=20000;
	
	/**
	 *从连接池中获取可用连接的超时时间
	 */
//	@Value("${rpc.httpclient.requestConnectionTimeout:5000}")
	private int requestConnectionTimeout=5000;
	
	/**
	 * 总共最大连接数
	 */
//	@Value("${rpc.httpclient.maxConnection:500}")
	private int maxConnection=500;
	
	/**
	 *每个不同的host是一个route，每个route上最多可以打开http连接数(并发)
	 */
//	@Value("${rpc.httpclient.maxConnectionPerRoute:100}")
	private int maxConnectionPerRoute=100;

	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public int getReadTimeout() {
		return readTimeout;
	}
	

	public int getRequestConnectionTimeout() {
		return requestConnectionTimeout;
	}

	public void setRequestConnectionTimeout(int requestConnectionTimeout) {
		this.requestConnectionTimeout = requestConnectionTimeout;
	}

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	public void setMaxConnection(int maxConnection) {
		this.maxConnection = maxConnection;
	}

	public void setMaxConnectionPerRoute(int maxConnectionPerRoute) {
		this.maxConnectionPerRoute = maxConnectionPerRoute;
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
				+ readTimeout + ", requestTimeout=" + requestConnectionTimeout + ", maxConnection="
				+ maxConnection + ", maxConnectionPerRoute=" + maxConnectionPerRoute + "]";
	}

	
	
}


