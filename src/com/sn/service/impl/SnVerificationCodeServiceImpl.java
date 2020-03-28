package com.sn.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.sn.entity.SnVerificationCode;
import com.sn.service.SnVerificationCodeService;

@Service("SnVerificationCodeServiceImpl")
public class SnVerificationCodeServiceImpl extends BaseServiceImpl<SnVerificationCode, Long> implements SnVerificationCodeService {
	@Override
	@Resource(name="SnVerificationCodeDaoImpl")
	public void setBaseDao(BaseDao<SnVerificationCode,Long> baseDao) {
		super.setBaseDao(baseDao);
	}
}
