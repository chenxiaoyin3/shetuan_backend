package com.hongyu.service.impl;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.Filter;
import com.hongyu.dao.HySpecialtyLabelDao;
import com.hongyu.entity.HyLabel;
import com.hongyu.entity.HySpecialtyLabel;
import com.hongyu.entity.Specialty;
import com.hongyu.service.HySpecialtyLabelService;

@Service("hySpecialtyLabelServiceImpl")
public class HySpecialtyLabelServiceImpl extends BaseServiceImpl<HySpecialtyLabel, Long> implements HySpecialtyLabelService{

	@Resource(name = "hySpecialtyDaoImpl")
	HySpecialtyLabelDao dao;
	
	@Resource(name = "hySpecialtyDaoImpl")
	public void setBaseDao(HySpecialtyLabelDao dao){
		super.setBaseDao(dao);		
	}

	@Override
	public Boolean isMarked(HyLabel hyLabel, Specialty specialty) {
		// TODO Auto-generated method stub
		List<Filter> filters = new ArrayList<>();
		filters.add(Filter.eq("hyLabel", hyLabel));
		filters.add(Filter.eq("specialty", specialty));
		filters.add(Filter.eq("isMarked", true));
		List<HySpecialtyLabel> hySpecialtyLabels = this.findList(null,filters,null);
		if(hySpecialtyLabels==null || hySpecialtyLabels.isEmpty()) {
			return false;
		}else {
			return true;
		}

	}

}
