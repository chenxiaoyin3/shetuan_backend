package com.shetuan.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.hongyu.Json;
import com.shetuan.service.RealObjectSourceService;

@RestController
@RequestMapping("admin/realObject")
public class RealObjectSourceController {

	@Resource(name = "realObjectSourceServiceImpl")
	private RealObjectSourceService realObjectSourceService;
	
	@RequestMapping(value="add")
	@ResponseBody
	public Json addLetterSource(String type, Long realObjectID, String name, String url){
		Json json = new Json();
		try {
			json = realObjectSourceService.addRealObjectSource(type, realObjectID,  name,  url);	
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("添加失败: "+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;		
	}
	
	@RequestMapping(value="edit")
	@ResponseBody
	public Json editRealObjectSource(String type, Long realObjectSourceID, String name, String url) {
		Json json = new Json();
		try {
			json = realObjectSourceService.editRealObjectSource(type, realObjectSourceID,  name,  url);	
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("添加失败: "+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;		
	}
}
