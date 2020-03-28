package com.shetuan.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.hongyu.Json;
import com.hongyu.Pageable;
import com.shetuan.service.ImageService;

@RestController
@RequestMapping(value = "/shetuan_officalWebsite/imageTopic/")
public class ImageTopicController {

	@Resource(name = "ImageServiceImpl")
	ImageService imageService;
	
	@RequestMapping("list")
	Json listView(Pageable pageable, String name, String organizationName,  String peopleName) {
		Json j = imageService.listView(pageable, name, organizationName, peopleName);
		return j;
	}
	
	@RequestMapping("imageDetailById")
	@ResponseBody
	public Json getDetail(Long id) {
		Json j=new Json();
		j = imageService.getDetail(id);
		return j;
	}
}
