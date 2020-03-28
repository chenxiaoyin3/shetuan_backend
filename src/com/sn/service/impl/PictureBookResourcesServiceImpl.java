package com.sn.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.sn.entity.PictureBookResources;
import com.sn.service.PictureBookResourcesService;

@Service("PictureBookResourcesServiceImpl")
public class PictureBookResourcesServiceImpl extends BaseServiceImpl<PictureBookResources,Long> implements PictureBookResourcesService {
	@Override
	@Resource(name="PictureBookResourcesDaoImpl")
	public void setBaseDao(BaseDao<PictureBookResources,Long> baseDao) {
		super.setBaseDao(baseDao);
	}
}
