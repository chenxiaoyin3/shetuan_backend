package com.hongyu.controller;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Filter.Operator;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Order.Direction;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.SpecialtyCategory;
import com.hongyu.entity.SpecialtyCategoryModifyHistory;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.SpecialtyCategoryModifyHistoryService;
import com.hongyu.service.SpecialtyCategoryService;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping({"admin/business/product/category"})
public class SpecialtyCategoryController
{
  @Resource(name="specialtyCategoryServiceImpl")
  SpecialtyCategoryService specialtyCategoryServiceImpl;
  
  @Resource(name = "specialtyCategoryModifyHistoryServiceImpl")
  SpecialtyCategoryModifyHistoryService categoryHistorySrv;
  
  @Resource(name="hyAdminServiceImpl")
  private HyAdminService hyAdminService;
  
  private List<HashMap<String, Object>> fieldFilter(SpecialtyCategory parent)
  {
    List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
    if (parent.getChildSpecialtyCategory().size() > 0) {
      for (SpecialtyCategory child : parent.getChildSpecialtyCategory())
      {	
    	  if(child.getIsActive()) {
    		  HashMap<String, Object> hm = new HashMap<String, Object>();
    	      hm.put("value", child.getId());
    	      hm.put("label", child.getName());
    	      hm.put("children", fieldFilter(child));
    	      list.add(hm);
    	  }
      }
    }
    return list;
  }
  
  private boolean checkIfParentCategoryValid(Long pid, SpecialtyCategory category) {
	  if (category.getId() == pid) {
		  return false;
	  } else {
		  if (category.getChildSpecialtyCategory() == null || category.getChildSpecialtyCategory().size() == 0) {
			  return true;
		  } else {
			  boolean valid = true;
			  for (SpecialtyCategory c : category.getChildSpecialtyCategory()) {
				  valid = valid && checkIfParentCategoryValid(pid, c);
				  if (!valid)
					  break;
			  }
			  return valid;
		  }
	  }
  }
  
  @RequestMapping({"/treelist/view"})
  @ResponseBody
  public Json specialtyCategoryTreeList()
  {
    Json json = new Json();
    List<Filter> filters = new ArrayList<Filter>();
    Filter filter = new Filter("parent", Filter.Operator.isNull, null);
    filters.add(filter);
    List<Order> orders = new ArrayList<Order>();
    orders.add(Order.asc("id"));
    List<SpecialtyCategory> list = this.specialtyCategoryServiceImpl.findList(null, filters, orders);
    List<HashMap<String, Object>> obj = new ArrayList<HashMap<String, Object>>();
    for (SpecialtyCategory parent : list)
    {
      HashMap<String, Object> hm = new HashMap<String, Object>();
      hm.put("value", parent.getId());
      hm.put("label", parent.getName());
      hm.put("children", fieldFilter(parent));
      obj.add(hm);
    }
    json.setSuccess(true);
    json.setMsg("查询成功");
    json.setObj(obj);
    return json;
  }
  
  @RequestMapping({"/page/view"})
  @ResponseBody
  public Json specialtyCategoryList(Pageable pageable, SpecialtyCategory category)
  {
    Json json = new Json();
    if (category.getName() != null && !category.getName().equals("")) {
    	List<Filter> filters = new ArrayList<Filter>();
    	filters.add(new Filter("name", Operator.like, category.getName()));
    	pageable.setFilters(filters);
    }
    List<Order> orders = new ArrayList<Order>();
	orders.add(Order.desc("id"));
	pageable.setOrders(orders);
    category.setName(null);
    Page<SpecialtyCategory> page = this.specialtyCategoryServiceImpl.findPage(pageable, category);
    json.setSuccess(true);
    json.setMsg("查询成功");
    json.setObj(page);
    return json;
  }
  
  @RequestMapping({"/add"})
  @ResponseBody
  public Json specialtyCategoryAdd(SpecialtyCategory category, Long pid, HttpSession session)
  {
    Json json = new Json();
    /**
	   * 获取当前用户
	   */
	  String username = (String) session.getAttribute(CommonAttributes.Principal);
	  HyAdmin admin = hyAdminService.find(username);
	  
	  List<Filter> filters = new ArrayList<>();
    
    if (pid != null) {
    	SpecialtyCategory parent = this.specialtyCategoryServiceImpl.find(pid);
        if (parent == null)
        {
          json.setSuccess(false);
          json.setMsg("父分区不存在");
          json.setObj(null);
          return json;
        } else {
        	category.setParent(parent);
        }
    }
    
    try
    {	
    	filters.add(Filter.eq("name", category.getName()));
    	List<SpecialtyCategory> result = specialtyCategoryServiceImpl.findList(null, filters, null);
    	if (result.size() > 0) {
    		json.setSuccess(false);
        	json.setMsg("设置失败,分区已存在");
        	json.setObj(null);
        	return json;
    	}
    	
    	category.setOperator(admin.getName());
    	this.specialtyCategoryServiceImpl.save(category);
    	json.setSuccess(true);
    	json.setMsg("添加成功");
    	json.setObj(null);
    }
    catch (Exception e)
    {
    	json.setSuccess(false);
    	json.setMsg("添加失败");
    	json.setObj(null);
    }
    return json;
  }
  
  @RequestMapping({"/modify"})
  @ResponseBody
  public Json specialtyCategoryMoodify(SpecialtyCategory category, Long pid)
  {
    Json json = new Json();
    
    
      try
      {	
    	SpecialtyCategory old_category = specialtyCategoryServiceImpl.find(category.getId());
    	
    	List<Filter> filters = new ArrayList<>();
    	filters.add(Filter.eq("name", category.getName()));
    	List<SpecialtyCategory> result = specialtyCategoryServiceImpl.findList(null, filters, null);
    	if (result.size() > 0 && !result.get(0).getId().equals(category.getId())) {
    		json.setSuccess(false);
        	json.setMsg("设置失败,分区已存在");
        	json.setObj(null);
        	return json;
    	}
    	    	
    	if (pid != null) {
        	SpecialtyCategory parent = specialtyCategoryServiceImpl.find(pid);
            if (parent == null)
            {
              json.setSuccess(false);
              json.setMsg("父分区不存在");
              json.setObj(null);
              return json;
            } else {
            	//判断传入的pid是否能作为合法的父分区
            	if (checkIfParentCategoryValid(pid, old_category)) {
            		old_category.setParent(parent);
            	} else {
            		json.setSuccess(false);
                    json.setMsg("父分区选择不合法");
                    json.setObj(null);
                    return json;
            	}
            }
        }

    	Date hisotryDeadTime = new Date();
    	SpecialtyCategoryModifyHistory his = new SpecialtyCategoryModifyHistory(old_category, hisotryDeadTime);
    	old_category.setIsActive(category.getIsActive());
    	old_category.setName(category.getName());
    	old_category.setIconUrl(category.getIconUrl());
    	old_category.setCreateTime(hisotryDeadTime);
    	old_category.setOrders(category.getOrders());//设置分区的排序
        specialtyCategoryServiceImpl.update(old_category);
        categoryHistorySrv.save(his);
        json.setSuccess(true);
        json.setMsg("修改成功");
        json.setObj(null);
      }
      catch (Exception e)
      {
        json.setSuccess(false);
        json.setMsg("修改失败");
        json.setObj(null);
      }
    
    return json;
  }
  
  @RequestMapping({"/detail/view"})
  @ResponseBody
  public Json specialtyCategoryDetail(Long id)
  {
    Json json = new Json();
    
    if (id != null) {
    	SpecialtyCategory category = specialtyCategoryServiceImpl.find(id);
        if (category == null)
        {
          json.setSuccess(false);
          json.setMsg("分区不存在");
          json.setObj(null);
          return json;
        } else {
          json.setSuccess(true);
          json.setMsg("查询成功");
          json.setObj(category);
          return json;
        }
    } else {
    	json.setSuccess(false);
        json.setMsg("缺失参数");
        json.setObj(null);
    }
    return json;
  }
  
  @RequestMapping({"/cancel"})
  @ResponseBody
  public Json specialtyCategoryCancel(Long id)
  {
    Json json = new Json();
    
    if (id != null) {
    	SpecialtyCategory category = specialtyCategoryServiceImpl.find(id);
        if (category == null)
        {
          json.setSuccess(false);
          json.setMsg("分区不存在");
          json.setObj(null);
          return json;
        } else {
          category.setIsActive(false);
          specialtyCategoryServiceImpl.update(category);
          json.setSuccess(true);
          json.setMsg("设置成功");
          json.setObj(null);
          return json;
        }
    } else {
    	json.setSuccess(false);
        json.setMsg("缺失参数");
        json.setObj(null);
    }
    return json;
  }
  
  @RequestMapping({"/restore"})
  @ResponseBody
  public Json specialtyCategoryRestore(Long id)
  {
    Json json = new Json();
    
    if (id != null) {
    	SpecialtyCategory category = specialtyCategoryServiceImpl.find(id);
        if (category == null)
        {
          json.setSuccess(false);
          json.setMsg("分区不存在");
          json.setObj(null);
          return json;
        } else {
          category.setIsActive(true);
          specialtyCategoryServiceImpl.update(category, "createTime");
          json.setSuccess(true);
          json.setMsg("设置成功");
          json.setObj(null);
          return json;
        }
    } else {
    	json.setSuccess(false);
        json.setMsg("缺失参数");
        json.setObj(null);
    }
    return json;
  }
  
  @RequestMapping({"/historylist/view"})
  @ResponseBody
  public Json specialtyCategoryHistoryList(Long id)
  {
    Json json = new Json();
    
    try {
//		Page<SpecialtyCategoryModifyHistory> page = categoryHistorySrv.findPage(new Pageable());
    	List<Filter> filters = new ArrayList<>();
        Filter filter = new Filter("categoryid", Operator.eq, id);
        filters.add(filter);
        List<Order> orders = new ArrayList<Order>();
        orders.add(new Order("id", Direction.asc));
        List<SpecialtyCategoryModifyHistory> list = categoryHistorySrv.findList(null, filters, orders);
		json.setSuccess(true);
		json.setMsg("查询成功");
		json.setObj(list);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		json.setSuccess(false);
		json.setMsg("查询失败");
		json.setObj(e);
	}
    return json;
  }
  
  @RequestMapping({"/selectlist/view"})
  @ResponseBody
  public Json specialtyCategorySelectlist(Long id, String name, Boolean isActive)
  {
    Json json = new Json();
    List<Filter> filters = new ArrayList<>();
    if (id != null) {
    	Filter filter = new Filter("id", Operator.ne, id);
        filters.add(filter);
    }
    if (name != null && !name.equals("")) {
    	Filter filter = new Filter("name", Operator.like, name);
        filters.add(filter);
    }
    if (isActive != null) {
    	Filter filter = new Filter("isActive", Operator.eq, isActive);
        filters.add(filter);
    }
    List<SpecialtyCategory> list = specialtyCategoryServiceImpl.findList(null, filters, new ArrayList<Order>());
//    SpecialtyCategory ca = specialtyCategoryServiceImpl.find(id);
    
//    Page<SpecialtyCategory> page1 = specialtyCategoryServiceImpl.findPage(new Pageable(1, 100000), new SpecialtyCategory(id));
    json.setSuccess(true);
    json.setMsg("查询成功");
    json.setObj(list);
    return json;
  }
  
}
