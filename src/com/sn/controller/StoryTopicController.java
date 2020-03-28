package com.sn.controller;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.hongyu.Json;
import com.hongyu.Pageable;
import com.sn.service.StoryManagementService;

@RestController
@RequestMapping("/sn_officalWebsite/storyTopic/")
public class StoryTopicController {
	@Resource(name = "StoryManagementServiceImpl")
	private StoryManagementService storyManagementService;

	@RequestMapping("list")
	@ResponseBody
	public Json list(Pageable pageable) {
		Json j = new Json();
		j = storyManagementService.list(pageable,null);
		return j;
	}
	
	@RequestMapping("detail")
	@ResponseBody
	public Json detail(Long id) {
		Json j = new Json();
		j = storyManagementService.detail(id);
		storyManagementService.addClickNumber(id);
		return j;
	}
}
