package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.VerificationCodeDao;
import com.hongyu.entity.VerificationCode;
import com.hongyu.service.VerificationCodeService;

@Service("verificationCodeServiceImpl")
public class VerificationCodeServiceImpl extends BaseServiceImpl<VerificationCode, Long>
		implements VerificationCodeService {

	@Resource(name="verificationCodeDaoImpl")
	VerificationCodeDao verificationCodeServiceImpl;
	
	@Resource(name="verificationCodeDaoImpl")
	public void setBaseDao(VerificationCodeDao dao){
		super.setBaseDao(dao);
	}
}
