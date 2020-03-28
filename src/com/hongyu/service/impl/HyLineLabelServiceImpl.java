package com.hongyu.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.Filter;
import com.hongyu.dao.HyLineLabelDao;
import com.hongyu.entity.HyLine;
import com.hongyu.entity.HyLineLabel;
import com.hongyu.entity.HySpecialtyLineLabel;
import com.hongyu.service.HyLineLabelService;
import com.hongyu.service.HySpecialtyLineLabelService;

@Service("hyLineLabelServiceImpl")
public class HyLineLabelServiceImpl extends BaseServiceImpl<HyLineLabel, Long> implements HyLineLabelService{

	@Resource(name = "hyLineLabelDaoImpl")
	HyLineLabelDao dao;
	
	@Resource(name = "hySpecialtyLineLabelServiceImpl")
	HySpecialtyLineLabelService hySpecialtyLineLabelService;
	
	@Resource(name = "hyLineLabelDaoImpl")
	public void setBaseDao(HyLineLabelDao dao){
		super.setBaseDao(dao);		
	}

	@Override
	public List<HyLineLabel> getLabelsByLine(HyLine hyLine) {
		// TODO Auto-generated method stub
		List<Filter> lineLabelFilters = new ArrayList<>();
		lineLabelFilters.add(Filter.eq("hyLine", hyLine));
		lineLabelFilters.add(Filter.eq("isMarked", true));
		List<HySpecialtyLineLabel> specialtyLineLabels = hySpecialtyLineLabelService.findList(null,lineLabelFilters,null);
		List<HyLineLabel> res = new ArrayList<>();
		for(HySpecialtyLineLabel specialtyLineLabel:specialtyLineLabels) {
			if(specialtyLineLabel.getHyLabel()!=null) {
				res.add(specialtyLineLabel.getHyLabel());
			}
		}
		return res;
	}
}
