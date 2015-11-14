package com.lianjia.springremoting.consumer.test;

import java.util.Arrays;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.lianjia.springremoting.example.spi.service.UserService;
import com.lianjia.springremoting.invoker.annotation.Remoting;

/**
 * test作为服务消费者注入需要的service<br>
 * 模拟作为客户端请求服务提供方
 * 
 * @author huisman
 * @createAt 2015年9月15日 下午4:47:20
 * @since 1.0.0
 * @Copyright (c) 2015, Youzhixu.com All Rights Reserved.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:consumer-applicationContext.xml")
@Component
public class ConsumerTest {

	@Remoting
	private UserService userService;

	@Test
	public void testUserSearch() {
		System.out.println(userService.findById(256));
		System.out.println(Arrays.toString(userService.findAll().toArray()));
	}


	// 客户端
	private static Server server;

	@AfterClass
	public static void stopWebapp() throws Exception {
		server.stop();
	}

	@BeforeClass
	public static void startWebapp() throws Exception {
		server = new Server();
		Connector connector = new SelectChannelConnector();
		connector.setPort(8080);
		server.addConnector(connector);

		WebAppContext webAppContext = new WebAppContext();
		webAppContext.setContextPath("/");
		webAppContext.setWar("src/main/webapp");

		server.setHandler(webAppContext);
		server.start();
	}


}
