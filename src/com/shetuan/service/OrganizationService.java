package com.shetuan.service;

import java.util.Date;

import com.grain.service.BaseService;
import com.hongyu.Json;
import com.hongyu.Pageable;
import com.shetuan.entity.Organization;

public interface OrganizationService extends BaseService<Organization,Long>{
	Json listView(Pageable pageable,String name, Boolean state, String creator, String place, Date startTime, Date endTime);

	Json getDetail(Long organizationId);

	Json detailById(Long organizationId);

	void addClickNumber(Long organizationId);
}
