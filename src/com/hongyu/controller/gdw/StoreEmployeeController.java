package com.hongyu.controller.gdw;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
import com.hongyu.entity.HyRole;
import com.hongyu.entity.HySupplierContract;
import com.hongyu.entity.Store;
import com.hongyu.entity.WeBusiness;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyRoleService;
import com.hongyu.service.StoreService;
import com.hongyu.service.WeBusinessService;
import com.hongyu.util.AuthorityUtils;
import com.hongyu.util.Constants;
import com.hongyu.util.QrcodeUtil;

@Controller
@RequestMapping("/admin/storeEmployee/")
@Transactional(propagation = Propagation.REQUIRED)
public class StoreEmployeeController {

	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;

	@Resource(name = "hyRoleServiceImpl")
	HyRoleService hyRoleService;

	@Resource(name = "departmentServiceImpl")
	DepartmentService departmentService;

	@Resource(name = "storeServiceImpl")
	StoreService storeService;

	@Resource(name = "weBusinessServiceImpl")
	WeBusinessService weBusinessService;

	@Value("${system.ymymWebSite}")
	private String ymymWebSite;

	@RequestMapping("add")
	@ResponseBody
	@Transactional(propagation = Propagation.REQUIRED)
	public Json add(HyAdmin hyAdmin, Long roleId,BigDecimal lineDivideProportion, HttpSession session) {
		Json json = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin user = hyAdminService.find(username);
			System.out.println(username);
			HyRole hyRole = hyRoleService.find(roleId);
			// zjl write at 01/24, add Wechat business.
			List<Filter> filters = new ArrayList<Filter>();
			Department department = user.getDepartment();
			System.out.println(department.getName());
			filters.add(Filter.eq("department", department));
			List<Store> list = storeService.findList(null, filters, null);
			if (list == null || list.size() == 0) {
				json.setSuccess(false);
				json.setMsg("本人不属于门店员工，无法创建");
			} else {
				Store store = list.get(0);
				if(hyAdminService.find(hyAdmin.getUsername()) != null) {
					json.setSuccess(false);
					json.setMsg("员工用户名已存在，无法创建");
					return json;
				}
				hyAdmin.setDepartment(department);
				hyAdmin.setRole(hyRole);
				hyAdmin.setHyAdmin(user);
				hyAdminService.save(hyAdmin);
				WeBusiness business = new WeBusiness();
				business.setName(hyAdmin.getName());
				business.setType(0);
				business.setAddress(hyAdmin.getAddress());
				business.setStoreId(store.getId());
				business.setNameOfStore(store.getStoreName());
				business.setMobile(hyAdmin.getMobile());
				business.setWechatAccount(hyAdmin.getWechat());
				business.setIsLineWechatBusiness(true);
				business.setLineDivideProportion(lineDivideProportion);
				business.setOperator(user);
				business.setAccount(hyAdmin);
				weBusinessService.save(business);
				String url = ymymWebSite + "?uid=" + business.getId();
				business.setUrl(url);
				StoragePlugin filePlugin = new FilePlugin();
				String uuid = UUID.randomUUID() + "";
				String location = "/"+Constants.resourcesPath+"/qrcode/" + uuid + ".jpg";
				String tmp = System.getProperty("java.io.tmpdir") + "/upload_" + uuid + ".tmp";
				File file = QrcodeUtil.getQrcode(url, 200, tmp);
				filePlugin.upload(location, file, null);
				business.setQrcodeUrl(location);
				weBusinessService.update(business);
				json.setSuccess(true);
				json.setMsg("创建成功");
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("创建失败: "+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}

	@RequestMapping("list/view")
	@ResponseBody
	public Json list(Pageable pageable, HyAdmin hyAdmin, HttpSession session, HttpServletRequest request) {
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
				m.put("name", employee.getName());
				m.put("mobile", employee.getMobile());
				m.put("role", employee.getRole().getName());
				m.put("roleId", employee.getRole().getId());
				m.put("wechat", employee.getWechat());
				m.put("address", employee.getAddress());
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
