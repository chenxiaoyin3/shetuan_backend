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
import com.hongyu.entity.BankAccountType;
import com.hongyu.service.BankAccountTypeService;

@Controller
@RequestMapping("/settings/bankaccounttype/")
public class BankAccountTypeController {
	@Resource(name = "bankAccountTypeServiceImpl")
	BankAccountTypeService  bankAccountTypeService;
	
	@RequestMapping(value="add", method = RequestMethod.POST)
	@ResponseBody
	public Json add(@RequestBody BankAccountType bankAccountType) {
		Json j = new Json();
		try{
			bankAccountType.setStatus(true);
			bankAccountTypeService.save(bankAccountType);
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
			BankAccountType bankAccountType = bankAccountTypeService.find(id);
			bankAccountType.setStatus(false);
			bankAccountTypeService.update(bankAccountType);
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
	public Json update(@RequestBody BankAccountType bankAccountType) {
		Json j = new Json();
		try{
			bankAccountTypeService.update(bankAccountType);
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
	public Json list(Pageable pageable, BankAccountType bankAccountType){
		Json j = new Json();
		Page<BankAccountType> page=bankAccountTypeService.findPage(pageable,bankAccountType);
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
			BankAccountType bankAccountType = bankAccountTypeService.find(id);
			bankAccountType.setStatus(true);
			bankAccountTypeService.update(bankAccountType);
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
