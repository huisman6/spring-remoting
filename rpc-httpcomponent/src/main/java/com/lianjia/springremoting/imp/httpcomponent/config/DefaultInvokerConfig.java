package com.lianjia.springremoting.imp.httpcomponent.config;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.lianjia.springremoting.imp.httpcomponent.factory.HttpClientConfig;
import com.lianjia.springremoting.imp.httpcomponent.factory.RPCHttpClientHolder;
import com.lianjia.springremoting.imp.httpcomponent.interceptor.HessianServiceInvokerInterceptor;
import com.lianjia.springremoting.imp.httpcomponent.interceptor.HttpServiceInvokerInterceptor;
import com.lianjia.springremoting.interceptor.AutowiredAnnotedTypeInterceptor;
import com.lianjia.springremoting.invoker.processor.AutowiredRPCServiceBeanPostProcessor;

/**
 * <p>
 * HttpClientFactory& interceptors &bean post processor
 * </p>
 * 
 * @author huisman
 * @since 1.0.0
 * @createAt 2015年9月21日 下午9:05:13
 * @Copyright (c) 2015,Youzhixu.com Rights Reserved.
 */
@Configuration
public class DefaultInvokerConfig {
	@Autowired
	Environment env;

	@Bean
	public HttpClientConfig rpcInternalHttpClientConfig() {
		HttpClientConfig httpClientConfig = new HttpClientConfig();
		// 连接远程主机超时
		httpClientConfig.setConnectionTimeout(
				Integer.parseInt(env.getProperty("rpc.httpclient.connectionTimeout", "15000")));
		// socket读取超时
		httpClientConfig.setReadTimeout(
				Integer.parseInt(env.getProperty("rpc.httpclient.readTimeout", "20000")));
		// 请求获取连接池中空闲连接的超时时间
		httpClientConfig.setRequestConnectionTimeout(Integer
				.parseInt(env.getProperty("rpc.httpclient.requestConnectionTimeout", "8000")));
		// 最大连接数
		httpClientConfig.setMaxConnection(
				Integer.parseInt(env.getProperty("rpc.httpclient.maxConnection", "600")));
		// 每个主机最大并发100
		httpClientConfig.setMaxConnectionPerRoute(
				Integer.parseInt(env.getProperty("rpc.httpclient.maxConnectionPerRoute", "100")));
		return httpClientConfig;
	}

	@Bean
	public PoolingHttpClientConnectionManager rpcInternalHttpPoolManager() {
		// 本机证书
		SSLContext sslcontext;
		try {
			sslcontext = SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy())
					.build();
		} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
			throw new IllegalStateException(e);
		}
		// trust all host
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext,
				SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		// 注册https/http
		Registry<ConnectionSocketFactory> socketFactoryRegistry =
				RegistryBuilder.<ConnectionSocketFactory>create()
						.register("http", PlainConnectionSocketFactory.getSocketFactory())
						.register("https", sslsf).build();

		// SocketConfig socketConfig = SocketConfig.custom()
		// .setTcpNoDelay(true)
		// .build();
		HttpClientConfig config = rpcInternalHttpClientConfig();
		PoolingHttpClientConnectionManager manager =
				new PoolingHttpClientConnectionManager(socketFactoryRegistry);
		manager.setMaxTotal(config.getMaxConnection());// 连接池最大并发连接数
		manager.setDefaultMaxPerRoute(config.getMaxConnectionPerRoute());// 单路由(url)最大并发数
		return manager;
	}

	/**
	 * <p>
	 * RPC httpClient实例
	 * </p>
	 * 
	 * @return
	 * @since: 1.0.0
	 */
	@Bean(name = RPCHttpClientHolder.RPC_INTERNAL_HTTP_CLIENT)
	public CloseableHttpClient rpcInternalHttpClient() {
		HttpClientConfig config = rpcInternalHttpClientConfig();
		RequestConfig requestConfig = RequestConfig.custom()
				// 连接池请求连接的时间
				.setConnectionRequestTimeout(config.getRequestConnectionTimeout())
				.setSocketTimeout(config.getReadTimeout())
				.setConnectTimeout(config.getConnectionTimeout()).setCircularRedirectsAllowed(false)
				.setRedirectsEnabled(true).build();
		// SocketConfig socketConfig = SocketConfig.custom()
		// .setTcpNoDelay(true)
		// .build();
		return HttpClients.custom().setConnectionManager(rpcInternalHttpPoolManager())
				.setDefaultRequestConfig(requestConfig).build();
	}



	/**
	 * <p>
	 * 负责释放资源
	 * </p>
	 * 
	 * @return
	 * @since: 1.0.0
	 */
	@Bean
	public RPCHttpClientHolder rpcHttpClientHolder() {
		return new RPCHttpClientHolder(rpcInternalHttpClient(), rpcInternalHttpPoolManager());
	}

	@Bean
	public static AutowiredRPCServiceBeanPostProcessor autowiredRPCServiceBeanPostProcessor() {
		return new AutowiredRPCServiceBeanPostProcessor();
	}

	/**
	 * <p>
	 * 处理@HttpService
	 * </p>
	 * 
	 * @since: 1.0.0
	 * @return
	 */
	@Bean
	public AutowiredAnnotedTypeInterceptor httpServiceInvokerInterceptor() {
		return new HttpServiceInvokerInterceptor();
	}

	/**
	 * <p>
	 * 处理@HessianService
	 * </p>
	 * 
	 * @since: 1.0.0
	 * @return
	 */
	@Bean
	public AutowiredAnnotedTypeInterceptor hessianServiceInvokerInterceptor() {
		return new HessianServiceInvokerInterceptor();
	}
}
