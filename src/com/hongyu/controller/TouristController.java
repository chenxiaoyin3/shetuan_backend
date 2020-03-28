package com.hongyu.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.Json;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.TouristType;
import com.hongyu.service.TouristTypeService;

@Controller
@RequestMapping("/settings/touristtype/")
public class TouristController {
	@Resource(name = "touristTypeServiceImpl")
	TouristTypeService  touristTypeService;
	
	@RequestMapping(value="add", method = RequestMethod.POST)
	@ResponseBody
	public Json add(@RequestBody TouristType touristType) {
		Json j = new Json();
		try{
			touristType.setStatus(true);
			touristTypeService.save(touristType);
			j.setSuccess(true);
			j.setMsg("添加成功！");
		}catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	@RequestMapping(value="cancel")
	@ResponseBody
	public Json cancel(Long id) {
		Json j = new Json();
		try{
			TouristType touristType = touristTypeService.find(id);
			touristType.setStatus(false);
			touristTypeService.update(touristType);
			j.setSuccess(true);
			j.setMsg("取消成功！");
		}catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	@RequestMapping(value="update")
	@ResponseBody
	public Json update(@RequestBody TouristType touristType) {
		Json j = new Json();
		try{
			touristTypeService.update(touristType);
			j.setSuccess(true);
			j.setMsg("更新成功！");
		}catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	@RequestMapping(value="list")
	@ResponseBody
	public Json list(Pageable pageable, TouristType touristType){
		Json j = new Json();
		Page<TouristType> page=touristTypeService.findPage(pageable,touristType);
		j.setSuccess(true);
		j.setMsg("查询成功");
		j.setObj(page);
		return j;
	}

	@RequestMapping(value="restore")
	@ResponseBody
	public Json restore(Long id) {
		Json j = new Json();
		try{
			TouristType touristType = touristTypeService.find(id);
			touristType.setStatus(true);
			touristTypeService.update(touristType);
			j.setSuccess(true);
			j.setMsg("恢复成功！");
		}catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
}
