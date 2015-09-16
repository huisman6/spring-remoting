package com.youzhixu.springremoting.test;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.youzhixu.springremoting.service.UserService;

/**
 * @author huisman
 * @createAt 2015年9月15日 下午4:47:20
 * @since 1.0.0
 * @Copyright (c) 2015, Youzhixu.com All Rights Reserved.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test.xml")
public class TestServer {

	@Autowired
	private UserService userService;

	@Test
	public void testUserSearch() {
		System.out.println(userService.findById(256));
	}

	private static Server server;

	@BeforeClass
	public static void startWebapp() throws Exception {
		server = new Server();

		Connector connector = new SelectChannelConnector();
		connector.setPort(8080);

		server.addConnector(connector);

		WebAppContext webAppContext = new WebAppContext();
		webAppContext.setContextPath("/remoting");

		webAppContext.setWar("src/main/webapp");

		server.setHandler(webAppContext);

		server.start();
	}

	@AfterClass
	public static void stopWebapp() throws Exception {
		server.stop();
	}
}
