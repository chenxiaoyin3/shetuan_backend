package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.ContractTemplate;
import com.hongyu.entity.FddContractTemplate;
import com.hongyu.service.FddContractTemplateService;
@Service("fddContractTemplateServiceImpl")
public class FddContractTemplateServiceImpl extends BaseServiceImpl<FddContractTemplate, Long> implements FddContractTemplateService{
	@Override
	@Resource(name="fddContractTemplateDaoImpl")
	public void setBaseDao(BaseDao<FddContractTemplate, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}
}
