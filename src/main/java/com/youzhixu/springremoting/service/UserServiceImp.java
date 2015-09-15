package com.youzhixu.springremoting.service;

import org.springframework.stereotype.Service;

import com.youzhixu.springremoting.model.User;

/**
 * <p>
 *
 * </p>
 * 
 * @author liuhui
 * @createAt 2015年9月15日 下午3:11:30
 * @since 1.0.0
 * @Copyright (c) 2015, Lianjia Group All Rights Reserved.
 */
@Service
public class UserServiceImp implements UserService {

	@Override
	public User findById(int userId) {
		User usr = new User();
		HttpRequestHandlerServlet 
		usr.setId(userId);
		usr.setDesc("测试啊");
		usr.setUserName("东兴");
		return usr;
	}

}
