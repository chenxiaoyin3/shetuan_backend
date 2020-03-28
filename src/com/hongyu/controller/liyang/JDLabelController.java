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
import com.hongyu.entity.JDLabel;
import com.hongyu.entity.JDLabel;
import com.hongyu.entity.HyAdmin;
import com.hongyu.service.JDLabelService;
import com.hongyu.service.HyAdminService;

/**
 * 景点产品标签管理
 * @author liyang
 * @version 2018年12月28日 下午2:12:26
 */
@Controller
@RequestMapping("admin/jdLabel")
public class JDLabelController {
	
	@Resource(name = "jdLabelServiceImpl")
	JDLabelService jdLabelService;
	
	@Resource(name="hyAdminServiceImpl")
	HyAdminService hyAdminService;
	
	private List<HashMap<String, Object>> fieldFilter(JDLabel parent){
	    List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
	    if (parent.getChildJDLabels().size() > 0) {
	      for (JDLabel child: parent.getChildJDLabels()){	
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
	
	private boolean checkIfParentLabelValid(Long pid, JDLabel jdLabel) {
		  if (jdLabel.getId() == pid) {
			  return false;
		  } else {
			  if (jdLabel.getChildJDLabels() == null || jdLabel.getChildJDLabels().size() == 0) {
				  return true;
			  } else {
				  boolean valid = true;
				  for (JDLabel c : jdLabel.getChildJDLabels()) {
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
	public Json jdLabelTreeList(){
	    Json json = new Json();
	    try {
			List<Filter> filters = new ArrayList<Filter>();
			Filter filter = new Filter("parent", Filter.Operator.isNull, null);
			filters.add(filter);
			List<Order> orders = new ArrayList<Order>();
			orders.add(Order.asc("id"));
			List<JDLabel> list = this.jdLabelService.findList(null, filters, orders);
			List<HashMap<String, Object>> obj = new ArrayList<HashMap<String, Object>>();
			for (JDLabel parent : list){
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
	public Json jdLabelList(Pageable pageable, JDLabel jdLabel){
		Json json = new Json();
		try {
			if (jdLabel.getName() != null && !jdLabel.getName().equals("")) {
				List<Filter> filters = new ArrayList<Filter>();
				filters.add(new Filter("name", Operator.like, jdLabel.getName()));
				pageable.setFilters(filters);
			}
			List<Order> orders = new ArrayList<Order>();
			orders.add(Order.desc("id"));
			pageable.setOrders(orders);
			jdLabel.setName(null);
			Page<JDLabel> page = this.jdLabelService.findPage(pageable, jdLabel);
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
	public Json jdLabelAdd(JDLabel jdLabel, Long pid, HttpSession session){
		Json json = new Json();
		
		try{
		    /**
			 * 获取当前用户
			 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			  
			List<Filter> filters = new ArrayList<>();
			if (pid != null) {
				JDLabel parent = this.jdLabelService.find(pid);
				if (parent == null){
				json.setSuccess(false);
				json.setMsg("父标签不存在");
				json.setObj(null);
				return json;
				} else {
				    jdLabel.setParent(parent);
				}
			}
			filters.add(Filter.eq("name", jdLabel.getName()));
			List<JDLabel> result = jdLabelService.findList(null, filters, null);
			if (result.size() > 0) {
				json.setSuccess(false);
				json.setMsg("设置失败,标签已存在");
				json.setObj(null);
				return json;
			}
			String fullName = "";
			if(jdLabel.getParent()==null){
				fullName = jdLabel.getName();
			}else{
				fullName = jdLabel.getParent().getName()+"/"+jdLabel.getName();
			}
			jdLabel.setFullName(fullName);
			jdLabel.setOperator(admin.getName());
			this.jdLabelService.save(jdLabel);
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
	public Json jdLabelModify(JDLabel jdLabel, Long pid){
		Json json = new Json();
		try{	
			JDLabel old_label = jdLabelService.find(jdLabel.getId());
	
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("name", jdLabel.getName()));
			List<JDLabel> result = jdLabelService.findList(null, filters, null);
			if (result.size() > 0 && !result.get(0).getId().equals(jdLabel.getId())) {
				json.setSuccess(false);
				json.setMsg("设置失败,标签已存在");
				json.setObj(null);
				return json;
			}
	    	
			if (pid != null) {
				JDLabel parent = jdLabelService.find(pid);
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
				if(jdLabel.getIsActive()!=null && old_label.getIsActive()!=jdLabel.getIsActive()){
					boolean ishow = jdLabel.getIsActive();
					for(JDLabel label:old_label.getChildJDLabels()){
						label.setIshow(ishow);
					}
				}
			}
			old_label.setOrders(jdLabel.getOrders());
			old_label.setIsActive(jdLabel.getIsActive());
			old_label.setName(jdLabel.getName());
			old_label.setIconUrl(jdLabel.getIconUrl());
			String fullName = "";
			if(old_label.getParent()==null){
				fullName = old_label.getName();
			}else{
				fullName = old_label.getParent().getName()+"/"+old_label.getName();
			}
			old_label.setFullName(fullName);
			jdLabelService.update(old_label);
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
	public Json jdLabelDetail(Long id){
		Json json = new Json();
    
		try {
			if (id != null) {
				JDLabel jdLabel = jdLabelService.find(id);
				if (jdLabel == null){
					json.setSuccess(false);
					json.setMsg("标签不存在");
					json.setObj(null);
					return json;
				} else {
					json.setSuccess(true);
					json.setMsg("查询成功");
					json.setObj(jdLabel);
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
	public Json jdLabelCancel(Long id){
		Json json = new Json();
    
		try {
			if (id != null) {
				JDLabel jdLabel = jdLabelService.find(id);
				if (jdLabel == null){
					json.setSuccess(false);
					json.setMsg("标签不存在");
					json.setObj(null);
					return json;
				} else {
					jdLabel.setIsActive(false);
					jdLabelService.update(jdLabel);
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
	public Json jdLabelRestore(Long id){
    Json json = new Json();
    
    	try {
			if (id != null) {
				JDLabel jdLabel = jdLabelService.find(id);
				if (jdLabel == null){
					json.setSuccess(false);
					json.setMsg("标签不存在");
					json.setObj(null);
					return json;
				} else {
					jdLabel.setIsActive(true);
					jdLabelService.update(jdLabel, "createTime");
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
//	/**
//	 * 返回可以作为当前标签的父标签的所有标签	 	  
//	 * @param id
//	 * @param name
//	 * @param isActive
//	 * @return
//	 */
//	@RequestMapping({"/selectlist/view"})
//	@ResponseBody
//	public Json jdLabelSelectlist(Long id, String name, Boolean isActive){
//		Json json = new Json();
//		List<Filter> filters = new ArrayList<>();
//		if (id != null) {
//			Filter filter = new Filter("id", Operator.ne, id);
//			filters.add(filter);
//		}
//		if (name != null && !name.equals("")) {
//			Filter filter = new Filter("name", Operator.like, name);
//			filters.add(filter);
//		}
//		if (isActive != null) {
//			Filter filter = new Filter("isActive", Operator.eq, isActive);
//			filters.add(filter);
//		}
//		try {
//			List<JDLabel> list = jdLabelService.findList(null, filters, new ArrayList<Order>());
//			json.setSuccess(true);
//			json.setMsg("查询成功");
//			json.setObj(list);
//		} catch (Exception e) {
//			json.setSuccess(false);
//			json.setMsg("查询失败:"+e.getMessage());
//			json.setObj(null);
//		}
//		return json;
//	}	
	/**
	 * 返回可以作为当前标签的父标签的所有标签
	 * 为了不建三级标签，返回的时候自动过滤子标签	 	  
	 * @param id
	 * @param name
	 * @param isActive
	 * @return
	 */
	@RequestMapping({"/selectlist/view"})
	@ResponseBody
	public Json jdLabelSelectlist(Long id, String name, Boolean isActive){
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
			List<JDLabel> list = jdLabelService.findList(null, filters, new ArrayList<Order>());
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
