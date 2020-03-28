package com.shetuan.controller;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.hongyu.Json;
import com.hongyu.Pageable;
import com.shetuan.service.PeopleTopicService;

@RestController
@RequestMapping("/shetuan_officalWebsite/peopleTopic/")
public class PeopleTopicController {
	@Resource(name = "PeopleTopicServiceImpl")
	PeopleTopicService peopleTopicService;

	@RequestMapping("list")
	@ResponseBody
	public Json list(Pageable pageable, String name, String organizationName) {
		Json j = new Json();
		j = peopleTopicService.list(pageable, name, organizationName);
		return j;
	}
	
	@RequestMapping("peopleDetailById")
	@ResponseBody
	public Json detailById(Long id) {
		Json j=new Json();
		j = peopleTopicService.detailById(id);
		return j;
	}
}
