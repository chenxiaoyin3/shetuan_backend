package com.shetuan.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.hongyu.Json;
import com.hongyu.Pageable;
import com.shetuan.service.VideoService;

@RestController
@RequestMapping(value = "/shetuan_officalWebsite/videoTopic/")
public class VideoTopicController {

	@Resource(name = "VideoServiceImpl")
	VideoService videoService;
	
	@RequestMapping("list")
	Json listView(Pageable pageable, String name, String organizationName, String peopleName) {
		Json json = videoService.listView(pageable, name, organizationName, peopleName);
		return json;
	}

	@RequestMapping("videoDetailById")
	@ResponseBody
	public Json getDetail(Long id) {
		Json j=new Json();
		j = videoService.getDetail(id);
		return j;
	}
}
