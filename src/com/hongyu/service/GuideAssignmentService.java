package com.hongyu.service;

import com.grain.service.BaseService;
import com.hongyu.Json;
import com.hongyu.entity.GuideAssignment;

public interface GuideAssignmentService extends BaseService<GuideAssignment, Long> {
	public Json changeStatus(Long id,String comment,String username)throws Exception;
}
