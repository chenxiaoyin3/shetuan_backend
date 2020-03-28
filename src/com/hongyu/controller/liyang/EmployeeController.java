package com.hongyu.controller.liyang;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.http.HttpResponse;
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
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyCompanyService;
import com.hongyu.service.HyRoleService;
import com.hongyu.util.AuthorityUtils;
/**
 * 员工列表
 * @author liyang
 *
 */
@Controller
@RequestMapping("/admin/employee")
public class EmployeeController {
	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;

	@Resource(name = "hyRoleServiceImpl")
	HyRoleService hyRoleService;

	@Resource(name = "departmentServiceImpl")
	DepartmentService departmentService;
	
	@Resource(name = "hyCompanyServiceImpl")
	HyCompanyService hyCompanyService;
	
	/**
	 * 返回员工列表
	 * @param pageable	分页信息
	 * @param departmentName	所属部门名称
	 * @param String	员工姓名  主要是name参数
	 * @param session
	 * @param request
	 * @return
	 */
	@RequestMapping("list/view")
	@ResponseBody
	public Json list(Pageable pageable,String departmentName, String name, HttpSession session, HttpServletRequest request) {
		Json json = new Json();
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
			/** 将数据按照名字排序 */
			List<Order> orders = new ArrayList<Order>();
			Order order = Order.desc("createDate");
			orders.add(order);

			/** 数据按照创建人筛选 */
			List<Filter> filters = new ArrayList<Filter>();
			Filter filter = Filter.in("hyAdmin", hyAdmins);
			if(departmentName != null){
				List<Filter> filters2 = new ArrayList<>();
				filters2.add(Filter.like("name", departmentName));
				List<Department> departments = departmentService.findList(null,filters2,null);
				if(!departments.isEmpty()){
					filters.add(Filter.in("department", departments));
				}		
			}
			if(name != null){
				filters.add(Filter.like("name", name));
			}
			filters.add(filter);

			pageable.setFilters(filters);
			pageable.setOrders(orders);

			/** 找到分页的员工数据 */
			Page<HyAdmin> page = hyAdminService.findPage(pageable);

			/** 遍历当前页员工数据，返回前端需要的数据格式 */
			for (HyAdmin employee : page.getRows()) {
				HyAdmin creater = employee.getHyAdmin();

				HashMap<String, Object> m = new HashMap<String, Object>();
				m.put("username", employee.getUsername());
				//真实姓名
				m.put("name", employee.getName());
				//联系电话
				m.put("mobile", employee.getMobile());
				//角色
				m.put("role", employee.getRole().getName());
				m.put("roleId", employee.getRole().getId());
				//所在部门
				m.put("department", employee.getDepartment().getName());
				//职位
				m.put("position",employee.getPosition());
				//获取公司名称的时候，就一直找他的父公司。找到是公司的那个
				Department department = employee.getDepartment();
				while(!department.getIsCompany()){
					department = department.getHyDepartment();
				}
				m.put("company", department.getName());
				m.put("wechat", employee.getWechat());
				m.put("address", employee.getAddress());
				//是否启用
				m.put("isEnabled", employee.getIsEnabled());
				if (creater.equals(admin)) {
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
			json.setMsg("查找成功！");
			json.setObj(hm);

		} catch (Exception e) {
			json.setSuccess(true);
			json.setMsg("获取失败: "+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
}
