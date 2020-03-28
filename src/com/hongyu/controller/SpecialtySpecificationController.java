package com.hongyu.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.Json;
import com.hongyu.Pageable;
import com.hongyu.entity.Specialty;
import com.hongyu.entity.SpecialtySpecification;
import com.hongyu.service.SpecialtyService;
import com.hongyu.service.SpecialtySpecificationService;

@Controller
@RequestMapping("business/product/specification")
public class SpecialtySpecificationController {
	
	@Resource(name="specialtySpecificationServiceImpl")
	SpecialtySpecificationService specialtySpecificationSrv;
	
	@Resource(name="specialtyServiceImpl")
	SpecialtyService specialtyServiceImpl;
	
//	static class WrapSpecification {
//		public Pageable pageable;
//		public SpecialtySpecification specification;
//		public Long pid;
//		public Pageable getPageable() {
//			return pageable;
//		}
//		public void setPageable(Pageable pageable) {
//			this.pageable = pageable;
//		}
//		public SpecialtySpecification getSpecification() {
//			return specification;
//		}
//		public void setSpecification(SpecialtySpecification specification) {
//			this.specification = specification;
//		}
//		public Long getPid() {
//			return pid;
//		}
//		public void setPid(Long pid) {
//			this.pid = pid;
//		}
//		
//	}
//	
//	@RequestMapping("/add")
//	@ResponseBody
//	public Json specialtySpecificationAdd(@RequestBody WrapSpecification wrap) {
//		Json json = new Json();
//		if (wrap.getPid() == null) {
//			json.setSuccess(false);
//			json.setMsg("没有指定特产");
//			json.setObj(null);
//			return json;
//		}
//		Specialty specialty = specialtyServiceImpl.find(wrap.getPid());
//		
//		if (specialty == null) {
//			json.setSuccess(false);
//			json.setMsg("指定特产不存在");
//			json.setObj(null);
//			return json;
//		}
//		
//		try {
//			wrap.getSpecification().setSpecialty(specialty);
//			wrap.getSpecification().setIsActive(true);
//			specialtySpecificationSrv.save(wrap.getSpecification());
//			json.setSuccess(true);
//			json.setMsg("添加成功");
//			json.setObj(null);
//			return json;
//		} catch (Exception e) {
//			json.setSuccess(false);
//			json.setMsg("添加失败");
//			json.setObj(null);
//		}
//		
//		return json;
//	}
	
//	@RequestMapping("/delete")
//	@ResponseBody
//	public Json specialtySpecificationDelete(Long specificationid) {
//		Json json = new Json();
//		if (specificationid == null) {
//			json.setSuccess(false);
//			json.setMsg("没有指定特产规格");
//			json.setObj(null);
//			return json;
//		}
//		
//		try {
//			SpecialtySpecification specification = specialtySpecificationSrv.find(specificationid);
//			specification.setIsActive(false);
//			specialtySpecificationSrv.update(specification);
//			json.setSuccess(true);
//			json.setMsg("删除成功");
//			json.setObj(null);
//			return json;
//		} catch (Exception e) {
//			json.setSuccess(false);
//			json.setMsg("删除啊失败");
//			json.setObj(null);
//		}
//		
//		return json;
//	}
//	
//	@RequestMapping("/modify")
//	@ResponseBody
//	public Json specialtySpecificationModify(@RequestBody WrapSpecification wrap) {
//		Json json = new Json();
//		if (wrap.getPid() == null) {
//			json.setSuccess(false);
//			json.setMsg("没有指定特产");
//			json.setObj(null);
//			return json;
//		}
//		Specialty specialty = specialtyServiceImpl.find(wrap.getPid());
//		
//		if (specialty == null) {
//			json.setSuccess(false);
//			json.setMsg("指定特产不存在");
//			json.setObj(null);
//			return json;
//		}
//		
//		try {
//			wrap.getSpecification().setSpecialty(specialty);
//			specialtySpecificationSrv.update(wrap.getSpecification());
//			json.setSuccess(true);
//			json.setMsg("更新成功");
//			json.setObj(null);
//			return json;
//		} catch (Exception e) {
//			json.setSuccess(false);
//			json.setMsg("更新失败");
//			json.setObj(null);
//		}
//		
//		return json;
//	}
	
	@RequestMapping("/list")
	@ResponseBody
	public Json specialtySpecificationInSelectedCategory(Long specialtyid) {
		Json json = new Json();
		if (specialtyid == null) {
			json.setSuccess(false);
			json.setMsg("没有指定特产");
			json.setObj(null);
			return json;
		}
		
		try {
			Specialty specialty = specialtyServiceImpl.find(specialtyid);
			if (specialty != null) {
				json.setSuccess(true);
				json.setMsg("查询成功");
				json.setObj(specialty.getSpecifications());
			} else {
				json.setSuccess(false);
				json.setMsg("找不到指定特产");
				json.setObj(null);
			}
			
			return json;
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败失败");
			json.setObj(null);
		}
		
		return json;
	}
	
}
