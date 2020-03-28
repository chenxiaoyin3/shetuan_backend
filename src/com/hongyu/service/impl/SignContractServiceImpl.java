package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.SignContract;
import com.hongyu.service.SignContractService;

@Service("signContractServiceImpl")
public class SignContractServiceImpl extends BaseServiceImpl<SignContract,Long >implements SignContractService {

	@Resource(name="signContractDaoImpl")
	@Override
	public void setBaseDao(BaseDao<SignContract, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}

}
