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
import com.hongyu.entity.ZBYLabel;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.ZBYLabelService;

/**
 * 周边游产品标签管理
 * @author liyang
 * @version 2018年12月28日 下午2:15:02
 */
@Controller
@RequestMapping("admin/zbyLabel")
public class ZBYLabelController {

	@Resource(name = "zbyLabelServiceImpl")
	ZBYLabelService zbyLabelService;
	
	@Resource(name="hyAdminServiceImpl")
	HyAdminService hyAdminService;
	
	private List<HashMap<String, Object>> fieldFilter(ZBYLabel parent){
	    List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
	    if (parent.getChildZBYLabels().size() > 0) {
	      for (ZBYLabel child: parent.getChildZBYLabels()){	
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
	
	private boolean checkIfParentLabelValid(Long pid, ZBYLabel zbyLabel) {
		  if (zbyLabel.getId() == pid) {
			  return false;
		  } else {
			  if (zbyLabel.getChildZBYLabels() == null || zbyLabel.getChildZBYLabels().size() == 0) {
				  return true;
			  } else {
				  boolean valid = true;
				  for (ZBYLabel c : zbyLabel.getChildZBYLabels()) {
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
	public Json zbyLabelTreeList(){
	    Json json = new Json();
	    try {
			List<Filter> filters = new ArrayList<Filter>();
			Filter filter = new Filter("parent", Filter.Operator.isNull, null);
			filters.add(filter);
			List<Order> orders = new ArrayList<Order>();
			orders.add(Order.asc("id"));
			List<ZBYLabel> list = this.zbyLabelService.findList(null, filters, orders);
			List<HashMap<String, Object>> obj = new ArrayList<HashMap<String, Object>>();
			for (ZBYLabel parent : list){
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
	public Json zbyLabelList(Pageable pageable, ZBYLabel zbyLabel){
		Json json = new Json();
		try {
			if (zbyLabel.getName() != null && !zbyLabel.getName().equals("")) {
				List<Filter> filters = new ArrayList<Filter>();
				filters.add(new Filter("name", Operator.like, zbyLabel.getName()));
				pageable.setFilters(filters);
			}
			List<Order> orders = new ArrayList<Order>();
			orders.add(Order.desc("id"));
			pageable.setOrders(orders);
			zbyLabel.setName(null);
			Page<ZBYLabel> page = this.zbyLabelService.findPage(pageable, zbyLabel);
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
	public Json zbyLabelAdd(ZBYLabel zbyLabel, Long pid, HttpSession session){
		Json json = new Json();
		
		try{
		    /**
			 * 获取当前用户
			 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			  
			List<Filter> filters = new ArrayList<>();
			if (pid != null) {
				ZBYLabel parent = this.zbyLabelService.find(pid);
				if (parent == null){
				json.setSuccess(false);
				json.setMsg("父标签不存在");
				json.setObj(null);
				return json;
				} else {
				    zbyLabel.setParent(parent);
				}
			}
			filters.add(Filter.eq("name", zbyLabel.getName()));
			List<ZBYLabel> result = zbyLabelService.findList(null, filters, null);
			if (result.size() > 0) {
				json.setSuccess(false);
				json.setMsg("设置失败,标签已存在");
				json.setObj(null);
				return json;
			}
			String fullName = "";
			if(zbyLabel.getParent()==null){
				fullName = zbyLabel.getName();
			}else{
				fullName = zbyLabel.getParent().getName()+"/"+zbyLabel.getName();
			}
			zbyLabel.setFullName(fullName);
			zbyLabel.setOperator(admin.getName());
			this.zbyLabelService.save(zbyLabel);
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
	public Json zbyLabelModify(ZBYLabel zbyLabel, Long pid){
		Json json = new Json();
		try{	
			ZBYLabel old_label = zbyLabelService.find(zbyLabel.getId());
	
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("name", zbyLabel.getName()));
			List<ZBYLabel> result = zbyLabelService.findList(null, filters, null);
			if (result.size() > 0 && !result.get(0).getId().equals(zbyLabel.getId())) {
				json.setSuccess(false);
				json.setMsg("设置失败,标签已存在");
				json.setObj(null);
				return json;
			}
	    	
			if (pid != null) {
				ZBYLabel parent = zbyLabelService.find(pid);
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
				if(zbyLabel.getIsActive()!=null && old_label.getIsActive()!=zbyLabel.getIsActive()){
					boolean ishow = zbyLabel.getIsActive();
					for(ZBYLabel label:old_label.getChildZBYLabels()){
						label.setIshow(ishow);
					}
				}
			}
			old_label.setOrders(zbyLabel.getOrders());
			old_label.setIsActive(zbyLabel.getIsActive());
			old_label.setName(zbyLabel.getName());
			old_label.setIconUrl(zbyLabel.getIconUrl());
			String fullName = "";
			if(old_label.getParent()==null){
				fullName = old_label.getName();
			}else{
				fullName = old_label.getParent().getName()+"/"+old_label.getName();
			}
			old_label.setFullName(fullName);
			zbyLabelService.update(old_label);
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
	public Json zbyLabelDetail(Long id){
		Json json = new Json();
    
		try {
			if (id != null) {
				ZBYLabel zbyLabel = zbyLabelService.find(id);
				if (zbyLabel == null){
					json.setSuccess(false);
					json.setMsg("标签不存在");
					json.setObj(null);
					return json;
				} else {
					json.setSuccess(true);
					json.setMsg("查询成功");
					json.setObj(zbyLabel);
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
	public Json zbyLabelCancel(Long id){
		Json json = new Json();
    
		try {
			if (id != null) {
				ZBYLabel zbyLabel = zbyLabelService.find(id);
				if (zbyLabel == null){
					json.setSuccess(false);
					json.setMsg("标签不存在");
					json.setObj(null);
					return json;
				} else {
					zbyLabel.setIsActive(false);
					zbyLabelService.update(zbyLabel);
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
	public Json zbyLabelRestore(Long id){
    Json json = new Json();
    
    	try {
			if (id != null) {
				ZBYLabel zbyLabel = zbyLabelService.find(id);
				if (zbyLabel == null){
					json.setSuccess(false);
					json.setMsg("标签不存在");
					json.setObj(null);
					return json;
				} else {
					zbyLabel.setIsActive(true);
					zbyLabelService.update(zbyLabel, "createTime");
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
	public Json zbyLabelSelectlist(Long id, String name, Boolean isActive){
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
			List<ZBYLabel> list = zbyLabelService.findList(null, filters, new ArrayList<Order>());
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
