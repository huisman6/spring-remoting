package com.youzhixu.springremoting.factory;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

/**
 * <p>
 *  创建httpclient
 * </p> 
 * @author huisman 
 * @since 1.0.0
 * @createAt 2015年9月20日 下午3:58:06
 * @Copyright (c) 2015,Youzhixu.com Rights Reserved. 
 */
public class RPCHttpClientFactoryBean implements EnvironmentAware,FactoryBean<HttpClient>,InitializingBean,DisposableBean{
	private final Log logger=LogFactory.getLog(this.getClass());
    private CloseableHttpClient httpclient;
    private HttpClientConfig httpClientConfig;
    private Environment env;
    private PoolingHttpClientConnectionManager clientManager;
	@Override
	public HttpClient getObject() throws Exception {
		return this.httpclient;
	}

	@Override
	public Class<?> getObjectType() {
		return HttpClient.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		prepare();
	}
	private void loadConfig(){
		httpClientConfig=new HttpClientConfig();
		//连接远程主机超时
		httpClientConfig.setConnectionTimeout(Integer.parseInt(env.getProperty("rpc.httpclient.connectionTimeout","15000")));
		//socket读取超时
		httpClientConfig.setReadTimeout(Integer.parseInt(env.getProperty("rpc.httpclient.readTimeout","20000")));
		//请求获取连接池中空闲连接的超时时间
		httpClientConfig.setRequestConnectionTimeout(Integer.parseInt(env.getProperty("rpc.httpclient.requestConnectionTimeout","8000")));
		//最大连接数
		httpClientConfig.setMaxConnection(Integer.parseInt(env.getProperty("rpc.httpclient.maxConnection","600")));
		//每个主机最大并发100
		httpClientConfig.setMaxConnectionPerRoute(Integer.parseInt(env.getProperty("rpc.httpclient.maxConnectionPerRoute","100")));
		if (logger.isInfoEnabled()) {
			logger.info("init httpclient配置====================》"+this.httpClientConfig);
		}
	}
	private void prepare(){
		try {
		loadConfig();
        SSLContextBuilder builder = new SSLContextBuilder();
        builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build());
        RequestConfig requestConfig = RequestConfig.custom()
        		//连接池请求连接的时间
        		.setConnectionRequestTimeout(this.httpClientConfig.getRequestConnectionTimeout())
                .setSocketTimeout(this.httpClientConfig.getReadTimeout())
                .setConnectTimeout(this.httpClientConfig.getConnectionTimeout())
                .setCircularRedirectsAllowed(false)
                .setCookieSpec("easy").setRedirectsEnabled(true)
                .build();
//	        SocketConfig socketConfig = SocketConfig.custom()
//	                .setTcpNoDelay(true)
//	                .build();
	        this.clientManager = new PoolingHttpClientConnectionManager();
	        this.clientManager.setMaxTotal(this.httpClientConfig.getMaxConnection());// 连接池最大并发连接数
	        this.clientManager.setDefaultMaxPerRoute(this.httpClientConfig.getMaxConnectionPerRoute());// 单路由(url)最大并发数

	        this.httpclient = HttpClients.custom().setConnectionManager(this.clientManager)
	                .setSSLSocketFactory(sslsf).setDefaultRequestConfig(requestConfig).build();
	    } catch (Exception e) {
	        throw new IllegalStateException("httpclient init error:"+this.httpClientConfig);
	    }
	}

	@Override
	public void destroy() throws Exception {
		if (this.httpclient !=null) {
			this.httpclient.close();
		}
		if (this.clientManager !=null) {
			this.clientManager.shutdown();
		}
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.env=environment;
	}

}


