package com.hongyu.controller.liyang;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.grain.plugin.StoragePlugin;
import com.grain.plugin.file.FilePlugin;
import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyCompany;
import com.hongyu.entity.HyRole;
import com.hongyu.entity.Store;
import com.hongyu.entity.WeBusiness;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyCompanyService;
import com.hongyu.service.HyRoleService;
import com.hongyu.util.AuthorityUtils;
import com.hongyu.util.Constants;
import com.hongyu.util.QrcodeUtil;
/**
 * 行政部分-员工帐号管理（去掉该功能，暂时作废）
 * @author li_yang
 *
 */
@Controller
@RequestMapping("/admin/employeeAccountManagement")
public class EmployeeAccountManagementController {
	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;

	@Resource(name = "hyRoleServiceImpl")
	HyRoleService hyRoleService;

	@Resource(name = "departmentServiceImpl")
	DepartmentService departmentService;
	
	@Resource(name = "hyCompanyServiceImpl")
	HyCompanyService hyCompanyService;
	
	@RequestMapping("list/view")
	@ResponseBody
	public Json list(Pageable pageable,String departmentName,String name, HyAdmin hyAdmin, HttpSession session, HttpServletRequest request) {
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
			Page<HyAdmin> page = hyAdminService.findPage(pageable, hyAdmin);

			/** 遍历当前页员工数据，返回前端需要的数据格式 */
			for (HyAdmin employee : page.getRows()) {
				HyAdmin creater = employee.getHyAdmin();

				HashMap<String, Object> m = new HashMap<String, Object>();
				m.put("username", employee.getUsername());
				//真是姓名
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
				m.put("company", employee.getDepartment().getHyCompany().getCompanyName());
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
	@RequestMapping("get/view")
	@ResponseBody
	public Json get(String username) {
		Json json = new Json();
		try {
			HyAdmin hyAdmin = hyAdminService.find(username);
			json.setSuccess(true);
			json.setMsg("查找成功");
			json.setObj(hyAdmin);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败: "+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	/**
	 * 新建员工
	 * @param hyAdmin	用来接收员工信息
	 * @param roleId	角色id
	 * @param companyName	公司名称 String
	 * @param departmentName	部门名称 String
	 * @param session
	 * @return
	 */
	@RequestMapping("add")
	@ResponseBody
	@Transactional(propagation = Propagation.REQUIRED)
	public Json add(HyAdmin hyAdmin, Long roleId,String companyName,String departmentName, HttpSession session) {
		Json json = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin user = hyAdminService.find(username);
			
			HyRole hyRole = hyRoleService.find(roleId);
			hyAdmin.setRole(hyRole);
			//设置部门和公司
			if(companyName != null && departmentName != null){
				List<Filter> filters1 = new ArrayList<>();
				filters1.add(Filter.eq("companyName",companyName));
				List<HyCompany> hyCompanys = hyCompanyService.findList(null,filters1,null);
				List<Filter> filters2 = new ArrayList<>();
				filters2.add(Filter.eq("name",departmentName));
				if(!hyCompanys.isEmpty())
				filters2.add(Filter.in("hyCompany",hyCompanys ));
				List<Department> departments = departmentService.findList(null,filters2,null);
				if(!departments.isEmpty()){
					hyAdmin.setDepartment(departments.get(0));
				}
			}
			hyAdminService.save(hyAdmin);
			json.setSuccess(true);
			json.setMsg("新建成功");
		
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("创建失败: "+e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
	/**
	 * 编辑信息的时候，那些实体属性必须手动设置不能直接接收
	 * @param hyAdmin
	 * @param roleId
	 * @return
	 */
	@RequestMapping("edit")
	@ResponseBody
	@Transactional(propagation = Propagation.REQUIRED)
	public Json edit(HyAdmin hyAdmin,Long roleId) {
		Json json = new Json();
		try {
			HyRole role=hyRoleService.find(roleId);
			if(role!=null){
				hyAdmin.setRole(role);
			}
			/*
			 * 更新员工的这些信息
			 */
			hyAdminService.update(hyAdmin, "username", "password", "department", "createDate", "modifyDate",
					"position", "isOnjob", "contract", "areaQiche", "areaGuonei", "areaChujing", "isManager",
					"isLocked", "loginFailureCount", "lockedDate", "loginDate", "loginIp", "hyAdmin", "hyAdmins",
					"hySupplierContract","isEnabled");
			json.setSuccess(true);
			json.setMsg("编辑成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("编辑失败:"+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	@RequestMapping("changeStatus")
	@ResponseBody
	public Json changeStatus(String username) {
		Json json = new Json();
		try {
			HyAdmin hyAdmin = hyAdminService.find(username);
			hyAdmin.setIsEnabled(hyAdmin.getIsEnabled() == true ? false : true);
			hyAdminService.update(hyAdmin);
			json.setSuccess(true);
			json.setMsg("更改成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("更改失败: "+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}

	@RequestMapping(value = "getRoles/view")
	@ResponseBody
	public Json getRoles(HttpSession session) {
		Json j = new Json();

		try {
			/**
			 * 获取当前用户角色
			 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyRole role = hyAdminService.find(username).getRole();

			/** 获取子角色 */
			Set<HyRole> subRoles = role.getHyRolesForSubroles();
			if (subRoles.size() > 0) {
				Iterator<HyRole> iterator = subRoles.iterator();
				while (iterator.hasNext()) {
					HyRole subRole = iterator.next();
					if (!subRole.getStatus())
						iterator.remove();
				}
			}
			j.setSuccess(true);
			j.setMsg("查询成功");
			j.setObj(subRoles);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg("查询失败："+e.getMessage());
		}
		return j;
	}
	
}
