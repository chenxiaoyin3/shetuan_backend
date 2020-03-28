package com.hongyu.service;

import javax.servlet.http.HttpSession;

import com.grain.service.BaseService;
import com.hongyu.Json;
import com.hongyu.entity.Design;

public interface DesignService extends BaseService<Design, Long> {
	public Json pay(Long id, HttpSession session)throws Exception;
}
