package com.hongyu.service;


import java.util.Date;

import javax.servlet.http.HttpSession;

import com.grain.service.BaseService;
import com.hongyu.Json;
import com.hongyu.entity.Guide;
import com.hongyu.entity.HyLine.LineType;
import com.hongyu.entity.HyOrder;

public interface GuideService extends BaseService<Guide, Long> {
	public Json caculate(LineType lineType,Integer serviceType,Boolean groupType,Integer star,Integer days) throws Exception;
	public boolean isAvailable(Long guide,Date startDate,Date endDate)throws Exception;
}
