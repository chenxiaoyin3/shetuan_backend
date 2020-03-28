package com.shetuan.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.hongyu.Json;
import com.shetuan.service.HomePageService;

@RestController
@RequestMapping("/shetuan_officalWebsite/homePage/")
public class HomePageController {
	@Resource(name = "HomePageServiceImpl")
	HomePageService homePageService;
	
	@RequestMapping(value="totalNumber")
	@ResponseBody
	public Json getTotalOrganizationNumber() {
		Json j=new Json();
		j=homePageService.getTotalOrganizationNumber();
		return j;
	}
	
	@RequestMapping(value="hotlist")
	@ResponseBody
	public Json getHotList() {
		Json j=new Json();
		j=homePageService.getHotList();
		return j;
	}
}
