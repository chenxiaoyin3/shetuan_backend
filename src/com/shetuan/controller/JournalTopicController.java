package com.shetuan.controller;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.hongyu.Json;
import com.hongyu.Pageable;
import com.shetuan.service.JournalService;
import com.shetuan.service.JournalTopicService;

@RestController
@RequestMapping("/shetuan_officalWebsite/journalTopic/")
public class JournalTopicController {
	@Resource(name = "JournalTopicServiceImpl")
	JournalTopicService journalTopicService;

	@RequestMapping(value = "list")
	@ResponseBody
	public Json list(Pageable pageable, String name, String organizationName, @DateTimeFormat(pattern = "yyyy-MM-dd")Date startTime, @DateTimeFormat(pattern = "yyyy-MM-dd")Date endTime,
			boolean state) {
		Json j = new Json();
		j = journalTopicService.list(pageable, name, organizationName, startTime, endTime, state);
		return j;
	}

	@Resource(name = "JournalServiceImpl")
	JournalService journalService;

	@RequestMapping("journalDetailById")
	@ResponseBody
	public Json getDetail(Long id) {
		Json j=new Json();
		j = journalService.getDetail(id);
		return j;
	}
}
