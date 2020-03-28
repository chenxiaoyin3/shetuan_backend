package com.hongyu.controller.liyang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.Filter.Operator;
import com.hongyu.entity.CJYLabel;
import com.hongyu.entity.HyAdmin;
import com.hongyu.service.CJYLabelService;
import com.hongyu.service.HyAdminService;

import oracle.net.aso.f;

/**
 * 出境游产品标签管理
 * @author liyang
 * @version 2018年12月28日 下午12:13:01
 */
@Controller
@RequestMapping("admin/cjyLabel")
public class CJYLabelController {
	
	@Resource(name = "cjyLabelServiceImpl")
	CJYLabelService cjyLabelService;
	
	@Resource(name="hyAdminServiceImpl")
	HyAdminService hyAdminService;
	
	private List<HashMap<String, Object>> fieldFilter(CJYLabel parent){
	    List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
	    if (parent.getChildCJYLabels().size() > 0) {
	      for (CJYLabel child: parent.getChildCJYLabels()){	
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
	
	private boolean checkIfParentLabelValid(Long pid, CJYLabel cjyLabel) {
		  if (cjyLabel.getId() == pid) {
			  return false;
		  } else {
			  if (cjyLabel.getChildCJYLabels() == null || cjyLabel.getChildCJYLabels().size() == 0) {
				  return true;
			  } else {
				  boolean valid = true;
				  for (CJYLabel c : cjyLabel.getChildCJYLabels()) {
					  valid = valid && checkIfParentLabelValid(pid, c);
					  if (!valid)
						  break;
				  }
				  return valid;
			  }
		  }
	}
	
	@RequestMapping({"/treelist/view"})
	@ResponseBody
	public Json cjyLabelTreeList(){
	    Json json = new Json();
	    try {
			List<Filter> filters = new ArrayList<Filter>();
			Filter filter = new Filter("parent", Filter.Operator.isNull, null);
			filters.add(filter);
			List<Order> orders = new ArrayList<Order>();
			orders.add(Order.asc("id"));
			List<CJYLabel> list = this.cjyLabelService.findList(null, filters, orders);
			List<HashMap<String, Object>> obj = new ArrayList<HashMap<String, Object>>();
			for (CJYLabel parent : list){
			  HashMap<String, Object> hm = new HashMap<String, Object>();
			  hm.put("value", parent.getId());
			  hm.put("label", parent.getName());
			  hm.put("children", fieldFilter(parent));
			  obj.add(hm);
			}
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(obj);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败："+e.getMessage());
			json.setObj(null);
		}
	    return json;
	}

	@RequestMapping({"/page/view"})
	@ResponseBody
	public Json cjyLabelList(Pageable pageable, CJYLabel cjyLabel){
		Json json = new Json();
		try {
			if (cjyLabel.getName() != null && !cjyLabel.getName().equals("")) {
				List<Filter> filters = new ArrayList<Filter>();
				filters.add(new Filter("name", Operator.like, cjyLabel.getName()));
				pageable.setFilters(filters);
			}
			List<Order> orders = new ArrayList<Order>();
			orders.add(Order.desc("id"));
			pageable.setOrders(orders);
			cjyLabel.setName(null);
			Page<CJYLabel> page = this.cjyLabelService.findPage(pageable, cjyLabel);
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(page);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败："+e.getMessage());
			json.setObj(null);
		}
		return json;
	}
	@RequestMapping({"/add"})
	@ResponseBody
	public Json cjyLabelAdd(CJYLabel cjyLabel, Long pid, HttpSession session){
		Json json = new Json();
		
		try{
		    /**
			 * 获取当前用户
			 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			  
			List<Filter> filters = new ArrayList<>();
			if (pid != null) {
				CJYLabel parent = this.cjyLabelService.find(pid);
				if (parent == null){
					json.setSuccess(false);
					json.setMsg("父标签不存在");
					json.setObj(null);
					return json;
				} else {
				    cjyLabel.setParent(parent);
				}
			}
			filters.add(Filter.eq("name", cjyLabel.getName()));
			List<CJYLabel> result = cjyLabelService.findList(null, filters, null);
			if (result.size() > 0) {
				json.setSuccess(false);
				json.setMsg("设置失败,标签已存在");
				json.setObj(null);
				return json;
			}
			
			cjyLabel.setOperator(admin.getName());
			String fullName = "";
			if(cjyLabel.getParent()==null){
				fullName = cjyLabel.getName();
			}else{
				fullName = cjyLabel.getParent().getName()+"/"+cjyLabel.getName();
			}
			cjyLabel.setFullName(fullName);
			this.cjyLabelService.save(cjyLabel);
			json.setSuccess(true);
			json.setMsg("添加成功");
			json.setObj(null);
		}catch (Exception e){
			json.setSuccess(false);
			json.setMsg("添加失败："+e.getMessage());
			json.setObj(null);
		}
		return json;
	}
	
	@RequestMapping({"/modify"})
	@ResponseBody
	public Json cjyLabelModify(CJYLabel cjyLabel, Long pid){
		Json json = new Json();
		try{	
			CJYLabel old_label = cjyLabelService.find(cjyLabel.getId());
	
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("name", cjyLabel.getName()));
			List<CJYLabel> result = cjyLabelService.findList(null, filters, null);
			if (result.size() > 0 && !result.get(0).getId().equals(cjyLabel.getId())) {
				json.setSuccess(false);
				json.setMsg("设置失败,标签已存在");
				json.setObj(null);
				return json;
			}
	    	
			if (pid != null) {
				CJYLabel parent = cjyLabelService.find(pid);
				if (parent == null){
					json.setSuccess(false);
					json.setMsg("父标签不存在");
					json.setObj(null);
					return json;
				} else {
					//判断传入的pid是否能作为合法的父标签
					if (checkIfParentLabelValid(pid, old_label)) {
						old_label.setParent(parent);
					} else {
						json.setSuccess(false);
						json.setMsg("父标签选择不合法");
						json.setObj(null);
						return json;
					}
				}
			}
			if(old_label.getParent()==null){
				//如果它本身是父标签，就更新其子标签的显示状态。
				if(cjyLabel.getIsActive()!=null && old_label.getIsActive()!=cjyLabel.getIsActive()){
					boolean ishow = cjyLabel.getIsActive();
					for(CJYLabel label:old_label.getChildCJYLabels()){
						label.setIshow(ishow);
					}
				}
			}
			old_label.setOrders(cjyLabel.getOrders());
			old_label.setIsActive(cjyLabel.getIsActive());
			old_label.setName(cjyLabel.getName());
			old_label.setIconUrl(cjyLabel.getIconUrl());
			String fullName = "";
			if(old_label.getParent()==null){
				fullName = old_label.getName();
			}else{
				fullName = old_label.getParent().getName()+"/"+old_label.getName();
			}
			old_label.setFullName(fullName);
			cjyLabelService.update(old_label);
			json.setSuccess(true);
			json.setMsg("修改成功");
			json.setObj(null);
		}catch (Exception e){
			json.setSuccess(false);
			json.setMsg("修改失败");
			json.setObj(null);
		}
    
		return json;
	}
	  
	@RequestMapping({"/detail/view"})
	@ResponseBody
	public Json cjyLabelDetail(Long id){
		Json json = new Json();
    
		try {
			if (id != null) {
				CJYLabel cjyLabel = cjyLabelService.find(id);
				if (cjyLabel == null){
					json.setSuccess(false);
					json.setMsg("标签不存在");
					json.setObj(null);
					return json;
				} else {
					json.setSuccess(true);
					json.setMsg("查询成功");
					json.setObj(cjyLabel);
					return json;
				}
			} else {
				json.setSuccess(false);
				json.setMsg("缺失参数");
				json.setObj(null);
			}
		} catch (Exception e) {
			
			json.setSuccess(false);
			json.setMsg("查询失败："+e.getMessage());
			json.setObj(null);
		}
		return json;
	}
  
	@RequestMapping({"/cancel"})
	@ResponseBody
	public Json cjyLabelCancel(Long id){
		Json json = new Json();
    
		try {
			if (id != null) {
				CJYLabel cjyLabel = cjyLabelService.find(id);
				if (cjyLabel == null){
					json.setSuccess(false);
					json.setMsg("标签不存在");
					json.setObj(null);
					return json;
				} else {
					cjyLabel.setIsActive(false);
					cjyLabelService.update(cjyLabel);
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
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败："+e.getMessage());
			json.setObj(null);
		}
		return json;
	}
	  
	@RequestMapping({"/restore"})
	@ResponseBody
	public Json cjyLabelRestore(Long id){
    Json json = new Json();
    
    	try {
			if (id != null) {
				CJYLabel cjyLabel = cjyLabelService.find(id);
				if (cjyLabel == null){
					json.setSuccess(false);
					json.setMsg("标签不存在");
					json.setObj(null);
					return json;
				} else {
					cjyLabel.setIsActive(true);
					cjyLabelService.update(cjyLabel, "createTime");
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
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败："+e.getMessage());
			json.setObj(null);
		}
    	return json;
	}
	  	 	  
	@RequestMapping({"/selectlist/view"})
	@ResponseBody
	public Json cjyLabelSelectlist(Long id, String name, Boolean isActive){
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
		filters.add(Filter.isNull("parent"));
		try {
			List<CJYLabel> list = cjyLabelService.findList(null, filters, new ArrayList<Order>());
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(list);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败:"+e.getMessage());
			json.setObj(null);
		}
		return json;
	}	
}
