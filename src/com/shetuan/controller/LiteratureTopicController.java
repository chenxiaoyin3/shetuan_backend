package com.shetuan.controller;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.hongyu.Json;
import com.hongyu.Pageable;
import com.shetuan.service.LiteratureService;

@RestController
@RequestMapping("/shetuan_officalWebsite/literatureTopic/")
public class LiteratureTopicController {

	@Resource(name = "LiteratureServiceImpl")
	LiteratureService literatureService;

	@RequestMapping("list")
	@ResponseBody
	public Json list(Pageable pageable, String name, String organizationName, Integer type, @DateTimeFormat(pattern = "yyyy-MM-dd")Date startTime,
			@DateTimeFormat(pattern = "yyyy-MM-dd")Date endTime) {
		Json j=new Json();
		j = literatureService.listView(pageable, name, organizationName, type, startTime, endTime);
		return j;
	}

	@RequestMapping("literatureDetailById")
	@ResponseBody
	public Json getDetail(Long id) {
		Json j=new Json();
		j = literatureService.getDetail(id);
		return j;
	}
}
