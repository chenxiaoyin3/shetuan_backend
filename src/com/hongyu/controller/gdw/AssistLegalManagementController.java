package com.hongyu.controller.gdw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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
import com.hongyu.entity.Assist;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.service.AssistService;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;

@Controller
@RequestMapping("/admin/assistLegalManagement/")
public class AssistLegalManagementController {
	@Resource(name="assistServiceImpl")
	AssistService assistService;
	
	@Resource(name="departmentServiceImpl")
	DepartmentService departmentService;
	
	@Resource(name="hyAdminServiceImpl")
	HyAdminService hyAdminService;
	
	@RequestMapping("deal")
	@ResponseBody
	public Json deal(Long id,HttpSession session){
		Json json=new Json();
		try {
			String username=(String)session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin=hyAdminService.find(username);
			Assist assist=assistService.find(id);
			assist.setOperator(hyAdmin);
			assist.setStatus(1);
			assistService.update(assist);
			json.setSuccess(true);
			json.setMsg("处理成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("处理失败: "+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	@RequestMapping("list/view")
	@ResponseBody
	public Json list(Pageable pageable,Assist assist,HttpSession session,HttpServletRequest request){
		Json json=new Json();
		try {
			HashMap<String, Object> hm = new HashMap<String, Object>();
			List<HashMap<String, Object>> result = new ArrayList<>();
			/**
			 * 获取当前用户
			 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);

			/**
			 * 获取用户权限范围
			 */
			CheckedOperation co = (CheckedOperation) request.getAttribute("co");

			
			/** 将数据按照时间排序 */
			List<Order> orders = new ArrayList<Order>();
			Order order = Order.desc("createDate");
			orders.add(order);

			
			List<Filter> filters = new ArrayList<Filter>();
			Filter filter = Filter.eq("assistType", Assist.legalType);//1法律帮扶
			filters.add(filter);
			
			pageable.setFilters(filters);
			pageable.setOrders(orders);
			
			Page<Assist> page=assistService.findPage(pageable,assist);
			for(Assist tmp:page.getRows()){
				HyAdmin creator=tmp.getProposer();
				HashMap<String, Object> m = new HashMap<String, Object>();
				m.put("id", tmp.getId());
				m.put("storeName", tmp.getStore()==null?"":tmp.getStore().getStoreName());
				m.put("requiredSupport", tmp.getRequiredSupport());
				m.put("application", tmp.getApplication());
				m.put("proposer", tmp.getProposer());
				m.put("operator", tmp.getOperator());
				m.put("createDate", tmp.getCreateDate());
				m.put("modifyDate", tmp.getModifyDate());
				m.put("status", tmp.getStatus());
				m.put("phone", tmp.getPhone());
				m.put("starttime", tmp.getStarttime());
				if (admin.equals(creator)) {
					if (co == CheckedOperation.view) {
						m.put("privilege", "view");
					} else {
						m.put("privilege", "edit");
					}
				} else {
					if (co == CheckedOperation.edit) {
						m.put("privilege", "edit");
					} else {
						m.put("privilege", "view");
					}
				}
				result.add(m);
			}
			hm.put("total", page.getTotal());
			hm.put("pageNumber", page.getPageNumber());
			hm.put("pageSize", page.getPageSize());
			hm.put("rows", result);
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(hm);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取失败: "+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	@RequestMapping(value="get/view")
	@ResponseBody
	public Json get(Long id){
		Json json =new Json();
		try {
			Assist assist=assistService.find(id);
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(assist);
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("获取失败: "+e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
}
