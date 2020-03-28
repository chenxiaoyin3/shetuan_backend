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
import com.hongyu.entity.GNYLabel;
import com.hongyu.entity.GNYLabel;
import com.hongyu.entity.HyAdmin;
import com.hongyu.service.GNYLabelService;
import com.hongyu.service.HyAdminService;

/**
 * 国内游产品标签管理
 * @author liyang
 * @version 2018年12月28日 下午2:07:09
 */
@Controller
@RequestMapping("admin/gnyLabel")
public class GNYLabelController {
	
	@Resource(name = "gnyLabelServiceImpl")
	GNYLabelService gnyLabelService;
	
	@Resource(name="hyAdminServiceImpl")
	HyAdminService hyAdminService;
	
	private List<HashMap<String, Object>> fieldFilter(GNYLabel parent){
	    List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
	    if (parent.getChildGNYLabels().size() > 0) {
	      for (GNYLabel child: parent.getChildGNYLabels()){	
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
	
	private boolean checkIfParentLabelValid(Long pid, GNYLabel gnyLabel) {
		  if (gnyLabel.getId() == pid) {
			  return false;
		  } else {
			  if (gnyLabel.getChildGNYLabels() == null || gnyLabel.getChildGNYLabels().size() == 0) {
				  return true;
			  } else {
				  boolean valid = true;
				  for (GNYLabel c : gnyLabel.getChildGNYLabels()) {
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
	public Json gnyLabelTreeList(){
	    Json json = new Json();
	    try {
			List<Filter> filters = new ArrayList<Filter>();
			Filter filter = new Filter("parent", Filter.Operator.isNull, null);
			filters.add(filter);
			List<Order> orders = new ArrayList<Order>();
			orders.add(Order.asc("id"));
			List<GNYLabel> list = this.gnyLabelService.findList(null, filters, orders);
			List<HashMap<String, Object>> obj = new ArrayList<HashMap<String, Object>>();
			for (GNYLabel parent : list){
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
	public Json gnyLabelList(Pageable pageable, GNYLabel gnyLabel){
		Json json = new Json();
		try {
			if (gnyLabel.getName() != null && !gnyLabel.getName().equals("")) {
				List<Filter> filters = new ArrayList<Filter>();
				filters.add(new Filter("name", Operator.like, gnyLabel.getName()));
				pageable.setFilters(filters);
			}
			List<Order> orders = new ArrayList<Order>();
			orders.add(Order.desc("id"));
			pageable.setOrders(orders);
			gnyLabel.setName(null);
			Page<GNYLabel> page = this.gnyLabelService.findPage(pageable, gnyLabel);
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
	public Json gnyLabelAdd(GNYLabel gnyLabel, Long pid, HttpSession session){
		Json json = new Json();
		
		try{
		    /**
			 * 获取当前用户
			 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			  
			List<Filter> filters = new ArrayList<>();
			if (pid != null) {
				GNYLabel parent = this.gnyLabelService.find(pid);
				if (parent == null){
				json.setSuccess(false);
				json.setMsg("父标签不存在");
				json.setObj(null);
				return json;
				} else {
				    gnyLabel.setParent(parent);
				}
			}
			filters.add(Filter.eq("name", gnyLabel.getName()));
			List<GNYLabel> result = gnyLabelService.findList(null, filters, null);
			if (result.size() > 0) {
				json.setSuccess(false);
				json.setMsg("设置失败,标签已存在");
				json.setObj(null);
				return json;
			}
			String fullName = "";
			if(gnyLabel.getParent()==null){
				fullName = gnyLabel.getName();
			}else{
				fullName = gnyLabel.getParent().getName()+"/"+gnyLabel.getName();
			}
			gnyLabel.setFullName(fullName);
			gnyLabel.setOperator(admin.getName());
			this.gnyLabelService.save(gnyLabel);
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
	public Json gnyLabelModify(GNYLabel gnyLabel, Long pid){
		Json json = new Json();
		try{	
			GNYLabel old_label = gnyLabelService.find(gnyLabel.getId());
	
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("name", gnyLabel.getName()));
			List<GNYLabel> result = gnyLabelService.findList(null, filters, null);
			if (result.size() > 0 && !result.get(0).getId().equals(gnyLabel.getId())) {
				json.setSuccess(false);
				json.setMsg("设置失败,标签已存在");
				json.setObj(null);
				return json;
			}
	    	
			if (pid != null) {
				GNYLabel parent = gnyLabelService.find(pid);
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
				if(gnyLabel.getIsActive()!=null && old_label.getIsActive()!=gnyLabel.getIsActive()){
					boolean ishow = gnyLabel.getIsActive();
					for(GNYLabel label:old_label.getChildGNYLabels()){
						label.setIshow(ishow);
					}
				}
			}
			old_label.setOrders(gnyLabel.getOrders());
			old_label.setIsActive(gnyLabel.getIsActive());
			old_label.setName(gnyLabel.getName());
			old_label.setIconUrl(gnyLabel.getIconUrl());
			String fullName = "";
			if(old_label.getParent()==null){
				fullName = old_label.getName();
			}else{
				fullName = old_label.getParent().getName()+"/"+old_label.getName();
			}
			old_label.setFullName(fullName);
			gnyLabelService.update(old_label);
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
	public Json gnyLabelDetail(Long id){
		Json json = new Json();
    
		try {
			if (id != null) {
				GNYLabel gnyLabel = gnyLabelService.find(id);
				if (gnyLabel == null){
					json.setSuccess(false);
					json.setMsg("标签不存在");
					json.setObj(null);
					return json;
				} else {
					json.setSuccess(true);
					json.setMsg("查询成功");
					json.setObj(gnyLabel);
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
	public Json gnyLabelCancel(Long id){
		Json json = new Json();
    
		try {
			if (id != null) {
				GNYLabel gnyLabel = gnyLabelService.find(id);
				if (gnyLabel == null){
					json.setSuccess(false);
					json.setMsg("标签不存在");
					json.setObj(null);
					return json;
				} else {
					gnyLabel.setIsActive(false);
					gnyLabelService.update(gnyLabel);
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
	public Json gnyLabelRestore(Long id){
    Json json = new Json();
    
    	try {
			if (id != null) {
				GNYLabel gnyLabel = gnyLabelService.find(id);
				if (gnyLabel == null){
					json.setSuccess(false);
					json.setMsg("标签不存在");
					json.setObj(null);
					return json;
				} else {
					gnyLabel.setIsActive(true);
					gnyLabelService.update(gnyLabel, "createTime");
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
	public Json gnyLabelSelectlist(Long id, String name, Boolean isActive){
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
			List<GNYLabel> list = gnyLabelService.findList(null, filters, new ArrayList<Order>());
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
