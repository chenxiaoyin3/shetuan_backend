package com.hongyu.controller.gdw;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.Json;
import com.hongyu.entity.ContactGroup;
import com.hongyu.service.ContactGroupService;

@Controller
@RequestMapping("/admin/contactGroup")
public class ContactGroupController {
	@Resource(name="contactGroupServiceImpl")
	ContactGroupService contactGroupService;
	
	@RequestMapping(value="/addContactGroup")
	@ResponseBody
	public Json addContactGroup(ContactGroup contactGroup){
		Json json =new Json();
		try {
			contactGroup.setCreatetime(new Date());
			contactGroupService.save(contactGroup);
			json.setSuccess(true);
			json.setMsg("添加成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("添加失败: "+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	
	@RequestMapping(value="/editContactGroup")
	@ResponseBody
	public Json editContactGroup(ContactGroup contactGroup){
		Json json=new Json();
		try {
			contactGroupService.update(contactGroup);
			json.setSuccess(true);
			json.setMsg("编辑成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("编辑失败: "+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	
	@RequestMapping(value="/deleteContactGroup")
	@ResponseBody
	public Json deleteContactGroup(Long id){
		Json json=new Json();
		try {
			contactGroupService.delete(id);
			json.setSuccess(true);
			json.setMsg("删除成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("删除失败: "+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	} 

	
}
