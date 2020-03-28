package com.hongyu.controller;

import java.util.ArrayList;
import java.util.HashMap;
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
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyEmployeeInduction;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.entitycustom.AddressBook;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyEmployeeInductionService;
import com.hongyu.util.AuthorityUtils;
/**
 * 员工通讯录列表
 * @author cxy
 * @date 2019-04-18
 */
@Controller
@RequestMapping("/admin/addressBook/") //待定URL
public class HyAddressBookController {

	@Resource(name = "hyEmployeeInductionServiceImpl")
	HyEmployeeInductionService hyEmployeeInductionService;

	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;
	
	@RequestMapping("view")
	@ResponseBody
	public Json getEmployeeInduction(Pageable pageable, String name,HttpSession session,HttpServletRequest request){
		Json j = new Json();
		
		try{
			HashMap<String, Object> obj = new HashMap<>();
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
			/** 筛选 */
			List<Filter> filters = new ArrayList<Filter>();
			if(name!=null) {
				filters.add(Filter.like("name", name));
			}
			pageable.setFilters(filters);
			List<Order> orders = new ArrayList<>();
			orders.add(Order.desc("id")); // 按时间倒序 这里直接使用id
			pageable.setOrders(orders);
		
			HyEmployeeInduction employeeInduction = new HyEmployeeInduction();
			

			Page<HyEmployeeInduction> page=hyEmployeeInductionService.findPage(pageable,employeeInduction);
		
			List<HashMap<String, Object>> addressBook = new ArrayList<HashMap<String, Object>>();
			for (HyEmployeeInduction r : page.getRows()) {
				HashMap<String,Object> map=new HashMap<String,Object>();
				map.put("name", r.getName());
				map.put("department", r.getDepartment().getName());
				map.put("phone", r.getPhone());
				addressBook.add(map);
			}	

			obj.put("rows", addressBook);
			obj.put("total", page.getTotal());
			obj.put("pageNumber", page.getPageNumber());
			obj.put("totalPage", page.getTotalPages());
			obj.put("pageSize", page.getPageSize());

		
			j.setSuccess(true);
			j.setMsg("查询成功");
			j.setObj(obj);
		
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("查询失败");
			j.setObj(null);
			e.printStackTrace();
		}
		return j;
	}
}
