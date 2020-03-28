package com.sn.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.hongyu.Json;
import com.hongyu.Pageable;
import com.sn.service.ChildSongManagementService;

@RestController
@RequestMapping("/sn_officalWebsite/childSongTopic/")
public class ChildSongTopicController {
	@Resource(name = "ChildSongManagementServiceImpl")
	private ChildSongManagementService childSongManagementService;

	@RequestMapping("list")
	@ResponseBody
	public Json list(Pageable pageable) {
		Json j = new Json();
		j = childSongManagementService.list(pageable,null);
		return j;
	}
	
	@RequestMapping("detail")
	@ResponseBody
	public Json detail(Long id) {
		Json j = new Json();
		j = childSongManagementService.detail(id);
		childSongManagementService.addClickNumber(id);
		return j;
	}
}
