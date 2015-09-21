package com.youzhixu.springremoting.imp.httpcomponent.executor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;

import com.caucho.hessian.client.HessianConnection;
import com.caucho.hessian.client.HessianConnectionFactory;
import com.caucho.hessian.client.HessianProxy;
import com.caucho.hessian.client.HessianProxyFactory;
import com.youzhixu.springremoting.url.UrlResolver;

/**
 * @author huisman
 * @since 1.0.0
 * @createAt 2015年9月20日 下午5:51:50
 * @Copyright (c) 2015,Youzhixu.com Rights Reserved.
 */
public class HttpComponentHessianConnectionFactory implements HessianConnectionFactory {
	/**
	 * 负责发送http请求
	 */
	private HttpClient httpClient;
	/**
	 * 负责根据serviceUrl解析真正请求的host
	 */
	private UrlResolver urlResolver;

	/**
	 * <p>
	 * 
	 * </p>
	 * 
	 * @param proxyFactory
	 * @param httpClient
	 * @param urlResolver
	 */
	public HttpComponentHessianConnectionFactory(UrlResolver urlResolver, HttpClient httpClient) {
		this.httpClient = httpClient;
		this.urlResolver = urlResolver;
	}

	@Override
	public void setHessianProxyFactory(HessianProxyFactory factory) {
		// ignored 不需要读取proxy配置的超时时间。。我们统一使用httpClient
	}

	@Override
	public HessianConnection open(URL url) throws IOException {
		return new HttpClientConnection(this.urlResolver.resolveUrl(url.toString()),
				this.httpClient);
	}
}


/**
 * <p>
 * hessian发送请求的流程如下：<br>
 * 1,获取HessianConnection proxy.getConnectionFactory().open(url);<br>
 * 2,{@link #addHeader(String, String)}HessianConnection.addHeader();<br>
 * 3, 获取OutputStream，用来写入hessian协议 {@link #getOutputStream()}HessianConnection.getOutputStream();<br>
 * 4, 发送请求 {@link #sendRequest()} HessianConnection.sendRequest();<br>
 * 5, 如果发送请求异常，则调用{@link #destroy()} HessianConnection.destroy();<br>
 * 6, 获取服务端响应流 {@link #getInputStream()} HessianConnection.getInputStream();<br>
 * 7, 如果content-encoding为"deflate"，则调用InflaterInputStream解压缩;<br>
 * 8, 包装InputStream为HessianInput，并解析数据。;<br>
 * </p>
 * 
 * @see HessianProxy#sendRequest(String methodName, Object []args)
 * @author huisman
 * @since 1.0.0
 * @createAt 2015年9月20日 下午5:45:52
 * @Copyright (c) 2015,Youzhixu.com Rights Reserved.
 */
class HttpClientConnection implements HessianConnection {
	private static final int DEFAULT_BUFFER_SIZE = 1024;
	private HttpPost httpPost;
	/**
	 * 用于hessian写入自己的协议
	 */
	private OutputStream out;
	private HttpClient httpClient;
	private HttpResponse httpResponse;

	/**
	 * <p>
	 * 实际service请求地址
	 * </p>
	 * 
	 * @param httpPost
	 */
	public HttpClientConnection(String actualServiceUrl, HttpClient httpClient) {
		super();
		httpPost = new HttpPost(actualServiceUrl);
		this.out = new ByteArrayOutputStream(DEFAULT_BUFFER_SIZE);
		this.httpClient = httpClient;
	}

	@Override
	public void addHeader(String key, String value) {
		this.httpPost.addHeader(key, value);
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return this.out;
	}

	/**
	 * 发送请求
	 */
	@Override
	public void sendRequest() throws IOException {
		this.httpResponse = this.httpClient.execute(httpPost);
	}

	@Override
	public int getStatusCode() {
		return this.httpResponse.getStatusLine().getStatusCode();
	}

	@Override
	public String getStatusMessage() {
		StatusLine statusLine = this.httpResponse.getStatusLine();
		return statusLine.toString();
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return httpResponse.getEntity().getContent();
	}

	@Override
	public void close() throws IOException {
		this.httpPost.releaseConnection();
	}

	@Override
	public void destroy() throws IOException {
		this.httpPost.releaseConnection();
	}

	@Override
	public String getContentEncoding() {
		Header header = httpResponse.getEntity().getContentEncoding();
		if (header != null) {
			return header.getValue();
		}
		return "";
	}

}
