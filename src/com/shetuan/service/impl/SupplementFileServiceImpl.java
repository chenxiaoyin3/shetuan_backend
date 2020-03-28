package com.shetuan.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.shetuan.entity.SupplementFile;
import com.shetuan.service.SupplementFileService;
@Service("SupplementFileServiceImpl")
public class SupplementFileServiceImpl extends BaseServiceImpl<SupplementFile,Long> implements SupplementFileService{
	@Override
	@Resource(name="SupplementFileDaoImpl")
	public void setBaseDao(BaseDao<SupplementFile,Long> baseDao) {
		super.setBaseDao(baseDao);
	}
}
