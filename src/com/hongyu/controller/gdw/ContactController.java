package com.hongyu.controller.gdw;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.Json;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.Contact;
import com.hongyu.service.ContactService;

@Controller
@RequestMapping("/admin/contact")
public class ContactController {

	@Resource(name="contactServiceImpl")
	ContactService contactService;

	@RequestMapping(value="/addContact")
	@ResponseBody
	public Json addContact(Contact contact){
		Json json=new Json();
		try {
			contactService.save(contact);
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
	@RequestMapping("/getContacts/view")
	@ResponseBody
	public Json getContacts(Pageable pageable,Contact contact){
		Json json=new Json();
		try {
			Page<Contact> page=contactService.findPage(pageable, contact);
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(page);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取失败: "+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	
	@RequestMapping(value="/deleteContact")
	@ResponseBody
	public Json deleteContact(Long id){
		Json json=new Json();
		try {
			contactService.delete(id);
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
	@RequestMapping(value="/editContact")
	@ResponseBody
	public Json editContact(Contact contact){
		Json json=new Json();
		try {
			contactService.update(contact);
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
}
