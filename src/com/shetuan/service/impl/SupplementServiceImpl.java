package com.shetuan.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.shetuan.entity.Supplement;
import com.shetuan.service.SupplementService;
@Service("SupplementServiceImpl")
public class SupplementServiceImpl extends BaseServiceImpl<Supplement,Long> implements SupplementService{
	@Override
	@Resource(name="SupplementDaoImpl")
	public void setBaseDao(BaseDao<Supplement,Long> baseDao) {
		super.setBaseDao(baseDao);
	}
}
