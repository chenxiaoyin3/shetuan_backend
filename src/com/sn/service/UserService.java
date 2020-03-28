package com.sn.service;

import com.grain.service.BaseService;
import com.hongyu.Json;
import com.hongyu.Pageable;
import com.sn.entity.User;

public interface UserService extends BaseService<User, Long> {

	public Json add(User user);
	
	public Json list(Pageable pageable,String username);

	Json send(String phone);

	Json testoid(String openid);
}
