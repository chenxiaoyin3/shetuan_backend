package com.shetuan.controller;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.hongyu.Json;
import com.hongyu.Pageable;
import com.shetuan.entity.People;
import com.shetuan.service.PeopleManagementService;

@RestController
@RequestMapping("/admin/peopleManagement/")
public class PeopleManagementController {
	@Resource(name = "PeopleManagementServiceImpl")
	PeopleManagementService peopleManagementService;
	
	@RequestMapping(value="list")
	@ResponseBody
	public Json list(Pageable pageable,String peopleName,boolean state) {
		Json j=new Json();
		j=peopleManagementService.list(pageable, peopleName,state);
		return j;
	}
	
	@RequestMapping(value="detailById")
	@ResponseBody
	public Json detailById(Long id) {
		Json j=new Json();
		j=peopleManagementService.detailById(id);
		return j;
	}
	
	@RequestMapping(value="add")
	@ResponseBody
	@Transactional(propagation = Propagation.REQUIRED)
	public Json addById(@RequestBody People people) {
		Json j=new Json();
		j=peopleManagementService.add(people);
		return j;
	}
	
	@RequestMapping(value="editById")
	@ResponseBody
	@Transactional(propagation = Propagation.REQUIRED)
	public Json editById(@RequestBody People people) {
		Json j=new Json();
		j=peopleManagementService.editById(people);
		return j;
	}
	
	@RequestMapping(value="invalidById")
	@ResponseBody
	@Transactional(propagation = Propagation.REQUIRED)
	public Json invalidById(Long id) {
		Json j=new Json();
		j=peopleManagementService.invalidById(id);
		return j;
	}
	
	@RequestMapping(value="validById")
	@ResponseBody
	@Transactional(propagation = Propagation.REQUIRED)
	public Json validById(Long id) {
		Json j=new Json();
		j=peopleManagementService.validById(id);
		return j;
	}
	
	@RequestMapping(value="getAllPeople")
	@ResponseBody
	public Json getAllPeople() {
		Json j=new Json();
		j=peopleManagementService.getAllPeople();
		return j;
	}
}
