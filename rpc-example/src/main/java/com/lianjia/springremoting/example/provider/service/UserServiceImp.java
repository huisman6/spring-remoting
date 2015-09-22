package com.lianjia.springremoting.example.provider.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;

import com.lianjia.springremoting.example.spi.model.User;
import com.lianjia.springremoting.example.spi.service.UserService;

/**
 * 服务实现
 * 
 * @author huisman
 * @createAt 2015年9月15日 下午3:11:30
 * @since 1.0.0
 * @Copyright (c) 2015, Youzhixu.com All Rights Reserved.
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
		int count = tlr.nextInt(3, 200);
		List<User> userList = new ArrayList<>(count);
		userList.add(new User(count, "总共生成user：" + count, "统计信息"));
		for (int i = 0; i < count; i++) {
			int id = tlr.nextInt(1, 999999);
			userList.add(new User(id, "名字啊：" + id, id + "--我是描述啊啊啊啊啊"));
		}
		return userList;
	}
}
