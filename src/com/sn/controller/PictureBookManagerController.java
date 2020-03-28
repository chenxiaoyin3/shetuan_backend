package com.sn.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.hongyu.Json;
import com.sn.entity.PictureBook;
import com.sn.entity.PictureBookResources;
import com.sn.service.PictureBookService;

@RestController
@RequestMapping("/admin/pictureBook/")
public class PictureBookManagerController {
	@Resource(name = "PictureBookServiceImpl")
	PictureBookService pictureBookService;

	@RequestMapping("add")
	@ResponseBody
	public Json add(@RequestBody PictureBook pictureBook, HttpSession session) {
		Json j=new Json();
		j = pictureBookService.add(pictureBook, session);
		return j;
	}

	@RequestMapping("edit")
	@ResponseBody
	public Json edit(@RequestBody PictureBook pictureBook, HttpSession session) {
		Json j=new Json();
		j = pictureBookService.edit(pictureBook, session);
		return j;
	}

	@RequestMapping("delete")
	@ResponseBody
	public Json Delete(Long id) {
		Json j=new Json();
		j = pictureBookService.Delete(id);
		return j;
	}
}
