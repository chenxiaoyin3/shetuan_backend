package com.hongyu.service;

import org.springframework.transaction.annotation.Transactional;

import com.grain.service.BaseService;
import com.grain.util.StringUtil;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyUser;

public interface HyUserService extends BaseService<HyUser, Long>{
	
	void updatePassword(HyUser hyUser);
}
