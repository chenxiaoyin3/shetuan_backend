package com.hongyu.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.Filter;
import com.hongyu.dao.HyLabelDao;
import com.hongyu.entity.HyLabel;
import com.hongyu.entity.Specialty;
import com.hongyu.entity.SpecialtySpecification;
import com.hongyu.service.HyLabelService;
import com.hongyu.service.HySpecialtyLabelService;
import com.hongyu.service.SpecialtySpecificationService;

@Service("hyLabelServiceImpl")
public class HyLabelServiceImpl extends BaseServiceImpl<HyLabel, Long> implements HyLabelService{
	
	@Resource(name = "hyLabelDaoImpl")
	HyLabelDao dao;
	
	@Resource(name = "hyLabelDaoImpl")
	public void setBaseDao(HyLabelDao dao){
		super.setBaseDao(dao);		
	}
	
	@Resource(name="specialtySpecificationServiceImpl")
	SpecialtySpecificationService specialtySpecificationService;
	
	@Resource(name = "hySpecialtyLabelServiceImpl")
	HySpecialtyLabelService hySpecialtyLabelService;

	@Override
	public List<SpecialtySpecification> getSpecificationsByLabelId(long id) {
		// TODO Auto-generated method stub
		try {
			HyLabel hyLabel = this.find(id);
			if(hyLabel==null || !hyLabel.getIsActive()) {
				throw new Exception("没有有效标签");
			}
			if(hyLabel.getSpecialtys()==null || hyLabel.getSpecialtys().isEmpty()) {
				throw new Exception("没有有效特产");
			}
			List<Specialty> tmps = hyLabel.getSpecialtys();
			List<Specialty> specialties = new ArrayList<>();
			for(Specialty specialty:tmps) {
				if(specialty.getIsActive() && specialty.getSaleState().equals(1) &&
						hySpecialtyLabelService.isMarked(hyLabel, specialty)) {
					specialties.add(specialty);
				}
			}
			
			if(specialties==null || specialties.isEmpty()){
				return null;
			}
			List<Filter> filters2=new ArrayList<>();
			filters2.add(Filter.in("specialty", specialties));
			filters2.add(Filter.eq("isActive", true));
			
			List<SpecialtySpecification> specifications=specialtySpecificationService.findList(null,filters2,null);

			return specifications;
			
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}
}
