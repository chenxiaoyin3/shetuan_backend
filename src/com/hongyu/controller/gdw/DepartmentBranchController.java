package com.hongyu.controller.gdw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.mail.Session;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
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
import com.hongyu.entity.HyDepartmentModel;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyDepartmentModelService;
import com.hongyu.util.Constants;

@Controller
@RequestMapping("/admin/departmentBranch/")
public class DepartmentBranchController {
	@Resource(name = "departmentServiceImpl")
	DepartmentService departmentService;

	@Resource(name = "hyDepartmentModelServiceImpl")
	HyDepartmentModelService hyDepartmentModelService;
	
	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;

	@RequestMapping("add")
	@ResponseBody
	@Transactional(propagation = Propagation.REQUIRED)
	public Json add(Department department, Long parentId, String modelName,HttpSession session) {
		Json json = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin creator = hyAdminService.find(username);
			HyDepartmentModel hyDepartmentModel = hyDepartmentModelService.find(modelName);
			Department parent = departmentService.find(parentId);
			department.setHyDepartment(parent);
			department.setHyDepartmentModel(hyDepartmentModel);
			department.setIsCompany(false);
			department.setCreator(creator);
			departmentService.save(department);
			json.setSuccess(true);
			json.setMsg("添加成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("添加失败: " + e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}

	@RequestMapping("edit")
	@ResponseBody
	@Transactional(propagation = Propagation.REQUIRED)
	public Json edit(Department department, Long parentId, String modelName) {
		Json json = new Json();
		try {
			HyDepartmentModel hyDepartmentModel = hyDepartmentModelService.find(modelName);
			Department parent = departmentService.find(parentId);
			department.setHyDepartment(parent);
			department.setHyDepartmentModel(hyDepartmentModel);
			departmentService.update(department, "status","treePath", "isCompany",
					"hyCompany", "createDate", "store", "hyRoleAuthority"
					, "hyDepartments", "hyAdmins", "creator");
			
			//新增，更新子部门的全称 20190322
			Department de = departmentService.find(department.getId());
			getAllSubDepartments(de);
						
			json.setSuccess(true);
			json.setMsg("编辑成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("编辑失败: " + e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}

	// @RequestMapping("delete")
	// @ResponseBody
	// public Json delete(Long id){
	// Json json=new Json();
	// try {
	// departmentService.delete(id);
	// json.setSuccess(true);
	// json.setMsg("删除成功");
	// } catch (Exception e) {
	// json.setSuccess(false);
	// json.setMsg("删除失败");
	// e.printStackTrace();
	// // TODO: handle exception
	// }
	// return json;
	// }
	@RequestMapping("getParentDepartment/view")
	@ResponseBody
	public Json getParentDepartment() {
		Json json = new Json();
		try {
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("name", Constants.zonggongsi));
			filters.add(Filter.eq("status", 1));
			List<Department> lists = departmentService.findList(null, filters, null);
			List<HashMap<String, Object>> result = new ArrayList<>();
			for (Department tmp : lists) {
				if(tmp.getHyDepartmentModel().getName().indexOf(Constants.fengongsimendian)>=0){
					continue;
				}
				HashMap<String, Object> m = new HashMap<String, Object>();
				m.put("value", tmp.getId().toString());
				m.put("label", tmp.getName());
				m.put("key", tmp.getId().toString());
				m.put("children", getChildren(tmp));
				result.add(m);
			}
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(result);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败: " + e.getMessage());
			// TODO: handle exception
		}
		return json;
	}

	@RequestMapping("list/view")
	@ResponseBody
	public Json list(Pageable pageable, Department department) {
		Json json = new Json();
		try {
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.like("name", Constants.fengongsi));
			List<HyDepartmentModel> hyDepartmentModels = hyDepartmentModelService.findList(null, filters, null);
			List<Filter> filters2 = new ArrayList<>();
			filters2.add(Filter.in("hyDepartmentModel", hyDepartmentModels));
			if (department.getFullName() == null || department.getFullName().equals("")) {
				List<Filter> filters3 = new ArrayList<>();
				filters3.add(Filter.eq("name", Constants.zonggongsi));
				List<Department> lists = departmentService.findList(null, filters3, null);
				if (lists != null && lists.size() > 0) {
					filters2.add(Filter.eq("hyDepartment", lists.get(0)));
				}
			}
			List<Order> orders = new ArrayList<Order>();
			Order order = Order.desc("createDate");
			orders.add(order);
			pageable.setOrders(orders);
			pageable.setFilters(filters2);

			Page<Department> page = departmentService.findPage(pageable, department);
			HashMap<String, Object> hm = new HashMap<String, Object>();
			List<HashMap<String, Object>> result = new ArrayList<>();
			for (Department tmp : page.getRows()) {
				HashMap<String, Object> m = new HashMap<String, Object>();
				m.put("id", tmp.getId());
				m.put("name", tmp.getName());
				m.put("fullName", tmp.getFullName());
				m.put("treePath", tmp.getTreePath());
				m.put("children", getChildren(tmp,department.getStatus()));
				m.put("status", tmp.getStatus());
				m.put("parent", tmp.getHyDepartment());
				m.put("orders", tmp.getOrder());
				result.add(m);
			}
			hm.put("total", result.size());
			hm.put("pageNumber", page.getPageNumber());
			hm.put("pageSize", page.getPageSize());
			hm.put("rows", result);
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(hm);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败: " + e.getMessage());
			// TODO: handle exception
		}
		return json;
	}
	private static List<Map<String, Object>> getChildren(Department department) {
		Set<Department> hyDepartments = department.getHyDepartments();
		if(hyDepartments.size()<1)return null;
		List<Map<String, Object>> ans = new ArrayList<>();
		for (Department tmp : hyDepartments) {
			if(tmp.getStatus()!=1){
				continue;
			}
			if(tmp.getHyDepartmentModel().getName().indexOf(Constants.fengongsimendian)>=0){
				continue;
			}
			HashMap<String, Object> m = new HashMap<String, Object>();
			m.put("value", tmp.getId().toString());
			m.put("label", tmp.getName());
			m.put("key", tmp.getId().toString());
			m.put("children", getChildren(tmp));
			ans.add(m);
		}
		return ans;
	}
	private static List<Map<String, Object>> getChildren(Department department,Integer status) {
		Set<Department> hyDepartments = department.getHyDepartments();
		if(hyDepartments.size()<1)return null;
		List<Map<String, Object>> ans = new ArrayList<>();
		for (Department tmp : hyDepartments) {
			HashMap<String, Object> m = new HashMap<String, Object>();
			if(status!=null&&status!=tmp.getStatus()){
				continue;
			}
			m.put("id", tmp.getId());
			m.put("name", tmp.getName());
			m.put("fullName", tmp.getFullName());
			m.put("treePath", tmp.getTreePath());
			m.put("children", getChildren(tmp,status));
			m.put("status", tmp.getStatus());
			m.put("parent", tmp.getHyDepartment());
			m.put("orders", tmp.getOrder());
			ans.add(m);
		}
		return ans;
	}

	@RequestMapping("get/view")
	@ResponseBody
	public Json get(Long id) {
		Json json = new Json();
		try {
			Department department = departmentService.find(id);
			Map<String, Object> ans=new HashMap<>();
			ans.put("id", department.getId());
			ans.put("hyDepartment", department.getHyDepartment());
			ans.put("hyDepartmentModel", department.getHyDepartmentModel());
			ans.put("name", department.getName());
			ans.put("status", department.getStatus());
			ans.put("fullName", department.getFullName());
			ans.put("order", department.getOrder());
	
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(ans);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取失败: " + e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}

	@RequestMapping("getModels/view")
	@ResponseBody
	public Json getModels() {
		Json json = new Json();
		try {
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.like("name", "分公司"));
			List<HyDepartmentModel> hyDepartmentModels = hyDepartmentModelService.findList(null, filters, null);
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(hyDepartmentModels);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取失败: " + e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}

	@RequestMapping("changeStatus")
	@ResponseBody
	@Transactional(propagation = Propagation.REQUIRED)
	public Json changeStatus(Long id) {
		Json json = new Json();
		try {
			Department department = departmentService.find(id);
			int status = department.getStatus();
			if (status == 1) {
				Set<HyAdmin> sets = department.getHyAdmins();
				if (sets.size() > 0) {
					json.setSuccess(false);
					json.setMsg("存在员工，无法取消");
				} else {
					department.setStatus(0);// 锁定
					departmentService.update(department);
					json.setSuccess(true);
					json.setMsg("取消成功");
				}
			} else {
				department.setStatus(1);// 解锁
				departmentService.update(department);
				json.setSuccess(true);
				json.setMsg("解锁成功");
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("解锁失败： " + e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	
	private void getAllSubDepartments(Department depart) {
		if(depart.getHyDepartments().size() > 0) {
			for(Department temp : depart.getHyDepartments()) {
				temp.setFullName(depart.getFullName() + temp.getName());
				departmentService.update(temp);
				getAllSubDepartments(temp);
			}
		}
	}
}
