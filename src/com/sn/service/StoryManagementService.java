package com.sn.service;

import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.RequestBody;

import com.hongyu.Json;
import com.hongyu.Pageable;
import com.sn.controller.StoryManagementController.WrapStory;

public interface StoryManagementService {
	public Json add(@RequestBody WrapStory wrapStory, HttpSession session);

	public Json delete(Long id);

	public Json edit(@RequestBody WrapStory wrapStory, HttpSession session);

	public Json list(Pageable pageable, String name);
	
	public Json detail(Long id);
	
	public void addClickNumber(Long id);
}
