package com.hongyu.service;

import javax.servlet.http.HttpSession;

import com.grain.service.BaseService;
import com.hongyu.Json;
import com.hongyu.entity.GroupPlaceholder;

public interface GroupPlaceholderService extends BaseService<GroupPlaceholder, Long> {
	public Json addPlaceHolder(Long groupId,Integer number,HttpSession session) throws Exception;
	public Json deletePlaceHolder(Long id)throws Exception;
}
