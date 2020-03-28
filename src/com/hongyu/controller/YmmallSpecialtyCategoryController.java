package com.hongyu.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.validation.constraints.Null;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.Filter;
import com.hongyu.Filter.Operator;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.entity.SpecialtyCategory;
import com.hongyu.service.SpecialtyCategoryService;

@Controller
@RequestMapping({"ymmall/product/category"})
public class YmmallSpecialtyCategoryController {
	
	@Resource(name="specialtyCategoryServiceImpl")
	SpecialtyCategoryService specialtyCategoryServiceImpl;
	
	@RequestMapping(value={"/super_categories"},method=RequestMethod.GET)
	@ResponseBody
	public Json superCategories(){
		Json json = new Json();
		List<Filter> filters = new ArrayList<Filter>();
		Filter filter = new Filter("parent", Filter.Operator.isNull, null);
		filters.add(filter);
		List<Order> orders = new ArrayList<Order>();
		orders.add(Order.desc("orders"));//按照“排序orders”排序”
	    orders.add(Order.asc("id"));
	    try {
	    	List<SpecialtyCategory> list = this.specialtyCategoryServiceImpl.findList(null, filters, orders);
		    List<HashMap<String, Object>> obj = new ArrayList<HashMap<String, Object>>();
		    for(SpecialtyCategory parent:list){
		    	HashMap<String, Object> hm = new HashMap<String, Object>();
		    	hm.put("id", parent.getId());
		    	hm.put("name", parent.getName());
		    	hm.put("iconUrl", parent.getIconUrl());
		    	hm.put("orders", parent.getOrders());
		    	hm.put("isActive", parent.getIsActive());
		    	if(parent.getParent()!=null)
		    		hm.put("pid", parent.getParent().getId());
		    	else
		    		hm.put("pid", null);
		    	obj.add(hm);
		    }
		    json.setSuccess(true);
		    json.setMsg("查询成功");
		    json.setObj(obj);
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
		    json.setMsg("查询失败");
		    json.setObj(null);
		}
	    
	    return json;
	}
	
	@RequestMapping(value={"/sub_categories"},method=RequestMethod.GET)
	@ResponseBody
	public Json subCategories(@RequestParam("category_id")Long categoryId){
		Json json=new Json();
		try {
			if(categoryId==null){
				json.setSuccess(false);
				json.setMsg("父分区id未指定");
				json.setObj(null);
			}else{
				SpecialtyCategory category=specialtyCategoryServiceImpl.find(categoryId);
				List<SpecialtyCategory> list=category.getChildSpecialtyCategory();
				List<Map<String, Object>> obj=new ArrayList<Map<String,Object>>();
				for(SpecialtyCategory c:list){
					if(c.getIsActive()==true){
						HashMap<String, Object> hm = new HashMap<String, Object>();
						hm.put("id", c.getId());
				    	hm.put("name", c.getName());
				    	hm.put("iconUrl", c.getIconUrl());
				    	hm.put("orders", c.getOrders());
				    	hm.put("isActive", c.getIsActive());
				    	hm.put("pid", categoryId);
				    	obj.add(hm);
					}
				}
				json.setSuccess(true);
				json.setMsg("查询成功");
				json.setObj(obj);
				
			}
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(e);
		}
		
		return json;
	}

}
