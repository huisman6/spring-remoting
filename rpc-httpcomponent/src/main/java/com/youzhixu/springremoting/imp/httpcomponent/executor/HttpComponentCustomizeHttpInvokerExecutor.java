package com.youzhixu.springremoting.imp.httpcomponent.executor;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.springframework.remoting.httpinvoker.AbstractHttpInvokerRequestExecutor;
import org.springframework.remoting.httpinvoker.HttpComponentsHttpInvokerRequestExecutor;
import org.springframework.remoting.httpinvoker.HttpInvokerClientConfiguration;
import org.springframework.remoting.httpinvoker.SimpleHttpInvokerRequestExecutor;
import org.springframework.remoting.support.RemoteInvocationResult;

import com.youzhixu.springremoting.serialization.Serializer;

/**
 * @author huisman 
 * @since 1.0.0
 * @see HttpComponentsHttpInvokerRequestExecutor
 * @see AbstractHttpInvokerRequestExecutor
 * @see SimpleHttpInvokerRequestExecutor
 * @createAt 2015年9月20日 下午2:03:01
 * @Copyright (c) 2015,Youzhixu.com Rights Reserved. 
 */
public class HttpComponentCustomizeHttpInvokerExecutor extends AbstractHttpInvokerRequestExecutor{
    /**
     *负责对象序列化和反序列化
     */
    private Serializer serializer;
    private HttpClient httpClient
    
	public HttpComponentInvokerExecutor(Serializer serializer,HttpClient httpclient) {
		this.serializer=serializer;
	}



	@Override
	protected RemoteInvocationResult doExecuteRequest(HttpInvokerClientConfiguration config,
			ByteArrayOutputStream baos) throws Exception {
		HttpPost postMethod = createHttpPost(config);
		setRequestBody(config, postMethod, baos);
		try {
			HttpResponse response = executeHttpPost(config, getHttpClient(), postMethod);
			validateResponse(config, response);
			InputStream responseBody = getResponseBody(config, response);
			return readRemoteInvocationResult(responseBody, config.getCodebaseUrl());
		}
		finally {
			postMethod.releaseConnection();
		}
	}

}


