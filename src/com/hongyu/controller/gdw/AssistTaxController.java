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
import com.hongyu.entity.Store;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.service.AssistService;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.StoreService;
import com.hongyu.util.AuthorityUtils;

@Controller
@RequestMapping("/admin/assistTax/")
public class AssistTaxController {
	@Resource(name="assistServiceImpl")
	AssistService assistService;
	
	@Resource(name="departmentServiceImpl")
	DepartmentService departmentService;
	
	@Resource(name="hyAdminServiceImpl")
	HyAdminService hyAdminService;
	
	@Resource(name="storeServiceImpl")
	StoreService storeService;
	
	@RequestMapping(value="add")
	@ResponseBody
	public Json add(Assist assist,HttpSession session){
		Json json=new Json();
		try {
			String username=(String)session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin=hyAdminService.find(username);
			assist.setProposer(hyAdmin);
			Department department=hyAdmin.getDepartment();
			List<Filter> filters=new ArrayList<>();
			filters.add(Filter.eq("department", department));
			List<Store> stores=storeService.findList(null,filters,null);
			if(stores!=null&&stores.size()!=0){
				Store store=stores.get(0);
				assist.setStore(store);
			}
			assist.setAssistType(Assist.taxType);
			assistService.save(assist);
			json.setSuccess(true);
			json.setMsg("添加成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("添加失败: "+e.getMessage());
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
	
	@RequestMapping(value="delete")
	@ResponseBody
	public Json delete(Long id){
		Json json=new Json();
		try {
			assistService.delete(id);
			json.setSuccess(true);
			json.setMsg("删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			json.setSuccess(false);
			json.setMsg("删除失败: "+e.getMessage());
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
			Set<HyAdmin> hyAdmins=AuthorityUtils.getAdmins(session, request);
			/** 将数据按照时间排序 */
			List<Order> orders = new ArrayList<Order>();
			Order order = Order.desc("createDate");
			orders.add(order);

			/** 数据按照创建人筛选 */
			List<Filter> filters = new ArrayList<Filter>();
			Filter filter = Filter.in("proposer", hyAdmins);
			filters.add(filter);
			filters.add(Filter.eq("assistType", Assist.taxType));
			pageable.setFilters(filters);
			pageable.setOrders(orders);
			
			Page<Assist> page=assistService.findPage(pageable,assist);
			for(Assist tmp:page.getRows()){
				HyAdmin creator=tmp.getProposer();
				HashMap<String, Object> m = new HashMap<String, Object>();
				m.put("id", tmp.getId());
				m.put("requiredSupport", tmp.getRequiredSupport());
				m.put("application", tmp.getApplication());
				m.put("proposer", tmp.getProposer());
				m.put("operator", tmp.getOperator());
				m.put("status", tmp.getStatus());
				m.put("phone", tmp.getPhone());
				m.put("starttime", tmp.getStarttime());
				m.put("createDate", tmp.getCreateDate());
				m.put("modifyDate", tmp.getModifyDate());
				if (creator.equals(admin)) {
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
}
