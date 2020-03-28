package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.FddContractTemplateDao;
import com.hongyu.entity.FddContractTemplate;
@Repository("fddContractTemplateDaoImpl")
public class FddContractTemplateDaoImpl extends BaseDaoImpl<FddContractTemplate, Long> implements FddContractTemplateDao{

}
