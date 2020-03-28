package com.sn.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.hongyu.Json;
import com.hongyu.Pageable;
import com.sn.service.PictureBookService;

@RestController
@RequestMapping("/sn_officalWebsite/pictureBookTopic/")
public class PictureBookController {

	@Resource(name = "PictureBookServiceImpl")
	PictureBookService pictureBookService;	
	
	@RequestMapping("list")
	@ResponseBody
	public Json list(Pageable pageable,String name, Integer type) {
		Json j=new Json();
		j = pictureBookService.listView(pageable, name, type);
		return j;
	}

	@RequestMapping("detail")
	@ResponseBody
	public Json getDetail(Long id) {
		Json j=new Json();
		j = pictureBookService.getDetail(id);
		return j;
	}
}
