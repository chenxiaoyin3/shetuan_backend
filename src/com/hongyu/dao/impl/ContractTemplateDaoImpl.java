package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.ContractTemplateDao;
import com.hongyu.entity.ContractTemplate;

@Repository("contractTemplateDaoImpl")
public class ContractTemplateDaoImpl extends BaseDaoImpl<ContractTemplate,Long> implements ContractTemplateDao {

}
