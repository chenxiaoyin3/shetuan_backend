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
import com.hongyu.entity.GroupType;
import com.hongyu.service.GroupTypeService;

@Controller
@RequestMapping("/settings/grouptype/")
public class GroupTypeController {
	@Resource(name = "groupTypeServiceImpl")
	GroupTypeService  groupTypeService;
	
	@RequestMapping(value="add", method = RequestMethod.POST)
	@ResponseBody
	public Json add(@RequestBody GroupType groupType) {
		Json j = new Json();
		try{
			groupType.setStatus(true);
			groupTypeService.save(groupType);
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
			GroupType groupType = groupTypeService.find(id);
			groupType.setStatus(false);
			groupTypeService.update(groupType);
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
	public Json update(@RequestBody GroupType groupType) {
		Json j = new Json();
		try{
			groupTypeService.update(groupType);
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
	public Json list(Pageable pageable, GroupType groupType){
		Json j = new Json();
		Page<GroupType> page=groupTypeService.findPage(pageable,groupType);
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
			GroupType groupType = groupTypeService.find(id);
			groupType.setStatus(true);
			groupTypeService.update(groupType);
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
