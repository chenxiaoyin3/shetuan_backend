package com.hongyu.task.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.hongyu.Filter;
import com.hongyu.entity.Specialty;
import com.hongyu.service.SpecialtyService;
import com.hongyu.task.Processor;

@Component("productPutOnAndPutOffProcessor")
public class ProductPutOnAndPutOffProcessor implements Processor {
	
	@Resource(name="specialtyServiceImpl")
	SpecialtyService specialtyServiceImpl;

	@Override
	public void process() {
		Calendar now = Calendar.getInstance();
		now.setTime(new Date());
		//产品上架设置
		List<Filter> filters = new ArrayList<Filter>();
		filters.add(Filter.eq("saleState", 0));
		filters.add(Filter.isNotNull("putonTime"));
		List<Specialty> specialties = specialtyServiceImpl.findList(null, filters, null);
		for (Specialty specialty : specialties) {
			Date putOnTime = specialty.getPutonTime();
			Calendar compareDate = Calendar.getInstance();
			compareDate.setTime(putOnTime);
			if (now.get(Calendar.YEAR) == compareDate.get(Calendar.YEAR) && 
				now.get(Calendar.MONTH) == compareDate.get(Calendar.MONTH) &&
				now.get(Calendar.DAY_OF_MONTH) == compareDate.get(Calendar.DAY_OF_MONTH)) {
				specialty.setSaleState(1);
				specialtyServiceImpl.update(specialty);
			}
		}
		
		//产品下架设置
		filters.clear();
		filters.add(Filter.eq("saleState", 1));
		filters.add(Filter.isNotNull("putoffTime"));
		List<Specialty> toPutOffspecialties = specialtyServiceImpl.findList(null, filters, null);
		for (Specialty specialty : toPutOffspecialties) {
			Date putOffTime = specialty.getPutoffTime();
			Calendar compareDate = Calendar.getInstance();
			compareDate.setTime(putOffTime);
			if (now.get(Calendar.YEAR) == compareDate.get(Calendar.YEAR) && 
				now.get(Calendar.MONTH) == compareDate.get(Calendar.MONTH) &&
				now.get(Calendar.DAY_OF_MONTH) == compareDate.get(Calendar.DAY_OF_MONTH)) {
				specialty.setSaleState(0);
				specialtyServiceImpl.update(specialty);
			}
		}
		
	}

}
