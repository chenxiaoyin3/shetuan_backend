package com.shetuan.controller;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.hongyu.Json;
import com.hongyu.Pageable;
import com.shetuan.service.OrganizationService;
import com.shetuan.service.OrganizationTopicService;

@RestController
@RequestMapping("/shetuan_officalWebsite/organizationTopic/")
public class OrganizationTopicController {
	
	@Resource(name = "OrganizationTopicServiceImpl")
	OrganizationTopicService organizationTopicService;
	
	@RequestMapping(value="list")
	@ResponseBody
	public Json list(Pageable pageable,String name,boolean state,String creator,String place,@DateTimeFormat(pattern = "yyyy-MM-dd")Date startTime,@DateTimeFormat(pattern = "yyyy-MM-dd")Date endTime) {
		Json j=new Json();
		j=organizationService.listView(pageable, name, state, creator, place, startTime, endTime);
		return j;
	}
	
	@RequestMapping(value = "realObjectDetailById")
	@ResponseBody
	public Json realObjectDetailById(Long id) {
		Json j = new Json();
		j = organizationTopicService.realObjectDetailById(id);
		return j;
	}
	
	@Resource(name = "OrganizationServiceImpl")
	private OrganizationService organizationService;
	
	@RequestMapping("detailById")
	@ResponseBody
	Json getDetail(Long organizationId) {
		Json j = organizationService.detailById(organizationId);
		if(j.isSuccess()) {
			//社团点击量
			organizationService.addClickNumber(organizationId);
		}
		return j;
	}
}
