package com.hongyu.service;

import java.util.List;

import javax.servlet.http.HttpSession;

import com.grain.service.BaseService;
import com.hongyu.Json;
import com.hongyu.entity.AddedService;

public interface AddedServiceService extends BaseService<AddedService, Long> {
	 Json addValueAddedService(List<AddedService> adddedServices) throws Exception;
	 Json insertApplySubmit(List<Long> ids, HttpSession session) throws Exception;
	 void insertApplySubmitAuto() throws Exception;
}