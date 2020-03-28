package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.ContractTemplate;
import com.hongyu.service.ContractTemplateService;

@Service("contractTemplateServiceImpl")
public class ContractTemplateServiceImpl extends BaseServiceImpl<ContractTemplate,Long> implements ContractTemplateService {

	@Override
	@Resource(name="contractTemplateDaoImpl")
	public void setBaseDao(BaseDao<ContractTemplate, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}

}
