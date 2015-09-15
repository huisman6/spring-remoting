package com.youzhixu.springremoting.service;

import com.youzhixu.springremoting.annotation.RPCService;
import com.youzhixu.springremoting.model.User;

/**
 * <p>
 *
 * </p>
 * 
 * @author liuhui
 * @createAt 2015年9月15日 下午3:05:22
 * @since 1.0.0
 * @Copyright (c) 2015, Lianjia Group All Rights Reserved.
 */
@RPCService
public interface UserService {
	User findById(int userId);
}
