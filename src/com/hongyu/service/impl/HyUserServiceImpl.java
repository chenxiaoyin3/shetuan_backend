package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grain.service.impl.BaseServiceImpl;
import com.grain.util.StringUtil;
import com.hongyu.dao.HyUserDao;
import com.hongyu.entity.HyUser;
import com.hongyu.service.HyUserService;

@Service(value = "hyUserServiceImpl")
public class HyUserServiceImpl extends BaseServiceImpl<HyUser, Long> implements HyUserService{
	@Resource(name = "hyUserDaoImpl")
	HyUserDao dao;
	@Resource(name = "hyUserDaoImpl")
	public void setBaseDao(HyUserDao dao) {
		super.setBaseDao(dao);
	}
	
	
	@Override
	@Transactional
	public void updatePassword(HyUser hyUser) {
		if (hyUser.getPassWord()!=null){
			String encryptpw = StringUtil.encodePassword(hyUser.getPassWord(),
					"MD5");
			hyUser.setPassWord(encryptpw);
		}
		update(hyUser);
	}
}
