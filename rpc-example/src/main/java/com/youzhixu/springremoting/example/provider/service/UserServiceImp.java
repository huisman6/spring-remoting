package com.youzhixu.springremoting.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;

import com.youzhixu.springremoting.example.provider.model.User;
import com.youzhixu.springremoting.example.spi.UserService;

/**
 * @author huisman
 * @createAt 2015年9月15日 下午3:11:30
 * @since 1.0.0
 * @Copyright (c) 2015, Lianjia Group All Rights Reserved.
 */
@Service
public class UserServiceImp implements UserService {

	@Override
	public User findById(int userId) {
		User usr = new User();
		usr.setId(userId);
		usr.setDesc("测试啊");
		usr.setUserName("东兴");
		return usr;
	}

	@Override
	public List<User> findAll() {
		// just do it
		ThreadLocalRandom tlr = ThreadLocalRandom.current();
		int count = tlr.nextInt(3, 120);
		List<User> userList = new ArrayList<>(count);
		for (int i = 0; i < count; i++) {
			int id = tlr.nextInt(1, 999999);
			userList.add(new User(id, "名字啊：" + id, id + "--我是描述啊啊啊啊啊"));
		}
		return userList;
	}
}
