package com.hongyu.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.Department;
import com.hongyu.entity.QuestionnaireScore;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.QuestionnaireScoreService;

@Controller
@RequestMapping("/admin/scoreManagement")
public class QuestionnaireScoreManagementController {
	@Resource(name="questionanireScoreServiceImpl")
	QuestionnaireScoreService questionnaireScoreService;
	
	@Resource(name="departmentServiceImpl")
	DepartmentService departmentService;
	
	@RequestMapping("list")
	@ResponseBody
	public Json list(Pageable pageable,Long departmentId){
		Json json=new Json();
		try {
			Department department=departmentService.find(departmentId);
			List<Filter> filters=new ArrayList<>();
			filters.add(Filter.eq("department",department));
			pageable.setFilters(filters);
			Page<QuestionnaireScore> page=questionnaireScoreService.findPage(pageable);
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(page);
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("查询错误： "+e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
	@RequestMapping("detail")
	@ResponseBody
	public Json detial(Long id){
		Json json=new Json();
		try {
			QuestionnaireScore questionnaireScore=questionnaireScoreService.find(id);
			if(questionnaireScore==null){
				json.setSuccess(false);
				json.setMsg("获取失败");
			}else {
				json.setSuccess(true);
				json.setMsg("获取成功");
				json.setObj(questionnaireScore);
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取错误： "+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	@RequestMapping("getDepartments")
	@ResponseBody
	public Json getDepartments(){
		Json json=new Json();
		try {
			List<Department> lists=departmentService.findAll();
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(lists);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取错误： "+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}

}
