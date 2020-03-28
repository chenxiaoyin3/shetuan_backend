package com.shetuan.controller;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.hongyu.Json;
import com.hongyu.Pageable;
import com.shetuan.service.HistoricalDataIndexService;

@RestController
@RequestMapping("/shetuan_officalWebsite/historicalIndexTopic/")
public class HistoricalDataIndexTopicController {
	
	@Resource(name = "HistoricalDataIndexServiceImpl")
	HistoricalDataIndexService historicalDataIndexService;

	@RequestMapping("list")
	@ResponseBody
	public Json list(Pageable pageable,String name, String organizationName, @DateTimeFormat(pattern = "yyyy-MM-dd")Date startTime,
			@DateTimeFormat(pattern = "yyyy-MM-dd")Date endTime) {
		Json j=new Json();
		j = historicalDataIndexService.listView(pageable, name, organizationName, startTime, endTime);
		return j;
	}

	@RequestMapping("historicalIndexDetailById")
	@ResponseBody
	public Json getDetail(Long id) {
		Json j=new Json();
		j = historicalDataIndexService.getDetail(id);
		return j;
	}
}