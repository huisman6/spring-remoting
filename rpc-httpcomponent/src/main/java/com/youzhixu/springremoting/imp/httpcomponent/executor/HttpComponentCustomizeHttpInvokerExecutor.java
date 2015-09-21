package com.youzhixu.springremoting.imp.httpcomponent.executor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.util.Locale;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NoHttpResponseException;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.remoting.httpinvoker.AbstractHttpInvokerRequestExecutor;
import org.springframework.remoting.httpinvoker.HttpComponentsHttpInvokerRequestExecutor;
import org.springframework.remoting.httpinvoker.HttpInvokerClientConfiguration;
import org.springframework.remoting.httpinvoker.SimpleHttpInvokerRequestExecutor;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationResult;
import org.springframework.util.StringUtils;

import com.youzhixu.springremoting.serialize.Serializer;
import com.youzhixu.springremoting.url.UrlResolver;

/**
 * @author huisman
 * @since 1.0.0
 * @see HttpComponentsHttpInvokerRequestExecutor
 * @see AbstractHttpInvokerRequestExecutor
 * @see SimpleHttpInvokerRequestExecutor
 * @createAt 2015年9月20日 下午2:03:01
 * @Copyright (c) 2015,Youzhixu.com Rights Reserved.
 */
public class HttpComponentCustomizeHttpInvokerExecutor extends AbstractHttpInvokerRequestExecutor {
	private Serializer serializer;
	/**
	 * 负责发送http请求
	 */
	private HttpClient httpClient;
	/**
	 * 负责根据serviceUrl解析真正请求的host
	 */
	private UrlResolver urlResolver;

	public HttpComponentCustomizeHttpInvokerExecutor(UrlResolver urlResolver,
			HttpClient httpClient, Serializer serializer) {
		this.httpClient = httpClient;
		this.urlResolver = urlResolver;
		this.serializer = serializer;
	}


	/**
	 * 默认使用ObjectOutputStream写到ByteArrayOutputStream，<br>
	 * 使用Serializer来实现
	 */
	@Override
	protected void writeRemoteInvocation(RemoteInvocation invocation, OutputStream os)
			throws IOException {
		OutputStream dos = decorateOutputStream(os);
		try {
			serializer.writeObject(dos, invocation);
		} finally {
			dos.close();
		}
	}

	@Override
	protected RemoteInvocationResult readRemoteInvocationResult(InputStream is, String codebaseUrl)
			throws IOException, ClassNotFoundException {
		InputStream dis = decorateInputStream(is);
		try {
			Object obj = serializer.readObject(dis);
			if (!(obj instanceof RemoteInvocationResult)) {
				throw new RemoteException("Deserialized object needs to be assignable to type ["
						+ RemoteInvocationResult.class.getName() + "]: " + obj);
			}
			return (RemoteInvocationResult) obj;
		} finally {
			dis.close();
		}
	}


	@Override
	protected RemoteInvocationResult doExecuteRequest(HttpInvokerClientConfiguration config,
			ByteArrayOutputStream baos) throws Exception {

		HttpPost postMethod = createHttpPost(config);
		setRequestBody(config, postMethod, baos);
		try {
			HttpResponse response = executeHttpPost(config, this.httpClient, postMethod);
			validateResponse(config, response);
			InputStream responseBody = getResponseBody(config, response);
			return readRemoteInvocationResult(responseBody, config.getCodebaseUrl());
		} finally {
			postMethod.releaseConnection();
		}
	}


	/**
	 * Create a HttpPost for the given configuration.
	 * <p>
	 * The default implementation creates a standard HttpPost with
	 * "application/x-java-serialized-object" as "Content-Type" header.
	 * 
	 * @param config the HTTP invoker configuration that specifies the target service
	 * @return the HttpPost instance
	 * @throws java.io.IOException if thrown by I/O methods
	 */
	protected HttpPost createHttpPost(HttpInvokerClientConfiguration config) throws IOException {
		HttpPost httpPost = new HttpPost(this.urlResolver.resolveUrl(config.getServiceUrl()));
		LocaleContext localeContext = LocaleContextHolder.getLocaleContext();
		if (localeContext != null) {
			Locale locale = localeContext.getLocale();
			if (locale != null) {
				httpPost.addHeader(HTTP_HEADER_ACCEPT_LANGUAGE, StringUtils.toLanguageTag(locale));
			}
		}
		if (isAcceptGzipEncoding()) {
			httpPost.addHeader(HTTP_HEADER_ACCEPT_ENCODING, ENCODING_GZIP);
		}
		return httpPost;
	}

	/**
	 * Set the given serialized remote invocation as request body.
	 * <p>
	 * The default implementation simply sets the serialized invocation as the HttpPost's request
	 * body. This can be overridden, for example, to write a specific encoding and to potentially
	 * set appropriate HTTP request headers.
	 * 
	 * @param config the HTTP invoker configuration that specifies the target service
	 * @param httpPost the HttpPost to set the request body on
	 * @param baos the ByteArrayOutputStream that contains the serialized RemoteInvocation object
	 * @throws java.io.IOException if thrown by I/O methods
	 */
	protected void setRequestBody(HttpInvokerClientConfiguration config, HttpPost httpPost,
			ByteArrayOutputStream baos) throws IOException {
		ByteArrayEntity entity = new ByteArrayEntity(baos.toByteArray());
		entity.setContentType(getContentType());
		httpPost.setEntity(entity);
	}

	/**
	 * Execute the given HttpPost instance.
	 * 
	 * @param config the HTTP invoker configuration that specifies the target service
	 * @param httpClient the HttpClient to execute on
	 * @param httpPost the HttpPost to execute
	 * @return the resulting HttpResponse
	 * @throws java.io.IOException if thrown by I/O methods
	 */
	protected HttpResponse executeHttpPost(HttpInvokerClientConfiguration config,
			HttpClient httpClient, HttpPost httpPost) throws IOException {
		return httpClient.execute(httpPost);
	}

	/**
	 * Validate the given response as contained in the HttpPost object, throwing an exception if it
	 * does not correspond to a successful HTTP response.
	 * <p>
	 * Default implementation rejects any HTTP status code beyond 2xx, to avoid parsing the response
	 * body and trying to deserialize from a corrupted stream.
	 * 
	 * @param config the HTTP invoker configuration that specifies the target service
	 * @param response the resulting HttpResponse to validate
	 * @throws java.io.IOException if validation failed
	 */
	protected void validateResponse(HttpInvokerClientConfiguration config, HttpResponse response)
			throws IOException {
		StatusLine status = response.getStatusLine();
		if (status.getStatusCode() >= 300) {
			throw new NoHttpResponseException(
					"Did not receive successful HTTP response: status code = "
							+ status.getStatusCode() + ", status message = ["
							+ status.getReasonPhrase() + "]");
		}
	}

	/**
	 * Extract the response body from the given executed remote invocation request.
	 * <p>
	 * The default implementation simply fetches the HttpPost's response body stream. If the
	 * response is recognized as GZIP response, the InputStream will get wrapped in a
	 * GZIPInputStream.
	 * 
	 * @param config the HTTP invoker configuration that specifies the target service
	 * @param httpResponse the resulting HttpResponse to read the response body from
	 * @return an InputStream for the response body
	 * @throws java.io.IOException if thrown by I/O methods
	 * @see #isGzipResponse
	 * @see java.util.zip.GZIPInputStream
	 */
	protected InputStream getResponseBody(HttpInvokerClientConfiguration config,
			HttpResponse httpResponse) throws IOException {
		if (isGzipResponse(httpResponse)) {
			return new GZIPInputStream(httpResponse.getEntity().getContent());
		} else {
			return httpResponse.getEntity().getContent();
		}
	}

	/**
	 * Determine whether the given response indicates a GZIP response.
	 * <p>
	 * The default implementation checks whether the HTTP "Content-Encoding" header contains "gzip"
	 * (in any casing).
	 * 
	 * @param httpResponse the resulting HttpResponse to check
	 * @return whether the given response indicates a GZIP response
	 */
	protected boolean isGzipResponse(HttpResponse httpResponse) {
		Header encodingHeader = httpResponse.getFirstHeader(HTTP_HEADER_CONTENT_ENCODING);
		return (encodingHeader != null && encodingHeader.getValue() != null && encodingHeader
				.getValue().toLowerCase().contains(ENCODING_GZIP));
	}


}
