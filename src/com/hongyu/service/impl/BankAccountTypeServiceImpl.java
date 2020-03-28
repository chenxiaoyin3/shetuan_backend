package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.BankAccountTypeDao;
import com.hongyu.entity.BankAccountType;
import com.hongyu.service.BankAccountTypeService;
@Service(value = "bankAccountTypeServiceImpl")
public class BankAccountTypeServiceImpl extends BaseServiceImpl<BankAccountType, Long> 
implements BankAccountTypeService {
	
	@Resource(name = "bankAccountTypeDaoImpl")
	BankAccountTypeDao dao;
	
	@Resource(name = "bankAccountTypeDaoImpl")
	public void setBaseDao(BankAccountTypeDao dao){
		super.setBaseDao(dao);		
	}	
}