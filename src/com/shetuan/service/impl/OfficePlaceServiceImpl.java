package com.shetuan.service.impl;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.Filter;
import com.shetuan.dao.OfficePlaceDao;
import com.shetuan.entity.OfficePlace;
import com.shetuan.service.OfficePlaceService;
@Service("OfficePlaceServiceImpl")
public class OfficePlaceServiceImpl extends BaseServiceImpl<OfficePlace,Long> implements OfficePlaceService{
	@Override
	@Resource(name="OfficePlaceDaoImpl")
	public void setBaseDao(BaseDao<OfficePlace,Long> baseDao) {
		super.setBaseDao(baseDao);
	}
}
