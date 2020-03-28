package com.shetuan.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Pageable;
import com.shetuan.service.ActivityService;

@RestController
@RequestMapping("/shetuan_officalWebsite/activityTopic/")
public class ActivityTopicController {
	
	@Resource(name = "ActivityServiceImpl")
	ActivityService activityService;

	@RequestMapping("list")
	@ResponseBody
	public Json list(Pageable pageable,String name, String organizationName, Integer type, @DateTimeFormat(pattern = "yyyy-MM-dd")Date startTime,
			@DateTimeFormat(pattern = "yyyy-MM-dd")Date endTime, String place) {
		Json j=new Json();
		j = activityService.listView(pageable, name, organizationName, type, startTime, endTime, place);
		return j;
	}

	@RequestMapping("activityDetailById")
	@ResponseBody
	public Json getDetail(Long id) {
		Json j=new Json();
		j = activityService.getDetail(id);
		return j;
	}
}
