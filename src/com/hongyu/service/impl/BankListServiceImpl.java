package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.BankListDao;
import com.hongyu.entity.BankList;
import com.hongyu.service.BankListService;
@Service(value = "bankListServiceImpl")
public class BankListServiceImpl extends BaseServiceImpl<BankList, Long> 
implements BankListService {
	
	@Resource(name = "bankListDaoImpl")
	BankListDao dao;
	
	@Resource(name = "bankListDaoImpl")
	public void setBaseDao(BankListDao dao){
		super.setBaseDao(dao);		
	}	
}
