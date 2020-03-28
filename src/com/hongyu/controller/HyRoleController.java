package com.hongyu.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.hongyu.util.Constants.fengongsi;
import static com.hongyu.util.Constants.zonggongsi;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Order.Direction;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAuthority;
import com.hongyu.entity.HyDepartmentModel;
import com.hongyu.entity.HyRole;
import com.hongyu.entity.HyRoleAuthority;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.entity.HyRoleAuthority.CheckedRange;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAuthorityService;
import com.hongyu.service.HyDepartmentModelService;
import com.hongyu.service.HyRoleService;
import com.hongyu.service.RoleAuthorityService;
import com.hongyu.util.BeanUtils;
/**
 * Controller - 角色管理
 * @author guoxinze
 *
 */
@Controller
@RequestMapping("/admin/settings/role/")
public class HyRoleController {
	
	public static class Auth {
		public Long id;
		public CheckedOperation co;
		public CheckedRange cr;
		public Set<Long> departments = new HashSet<Long>(0);
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public CheckedOperation getCo() {
			return co;
		}
		public void setCo(CheckedOperation co) {
			this.co = co;
		}
		public CheckedRange getCr() {
			return cr;
		}
		public void setCr(CheckedRange cr) {
			this.cr = cr;
		}
		public Set<Long> getDepartments() {
			return departments;
		}
		public void setDepartments(Set<Long> departments) {
			this.departments = departments;
		}
		
	}
	/**
	 * 包装类，用于封装授权信息
	 * @author guoxinze
	 *
	 */
	static class Wrap {
		public Long roleId;
		public Set<Auth> authorities;
		public Long getRoleId() {
			return roleId;
		}
		public void setRoleId(Long roleId) {
			this.roleId = roleId;
		}
		public Set<Auth> getAuthorities() {
			return authorities;
		}
		public void setAuthorities(Set<Auth> authorities) {
			this.authorities = authorities;
		}
	}
	@Resource(name = "departmentServiceImpl")
	DepartmentService  hyDepartmentService;
	
	@Resource(name = "hyDepartmentModelServiceImpl")
	HyDepartmentModelService  hyDepartmentModelService;
	
	@Resource(name = "hyRoleServiceImpl")
	private HyRoleService hyRoleService;
	
	@Resource(name = "hyRoleAuthorityServiceImpl")
	private RoleAuthorityService roleAuthorityService;
	
	@Resource(name = "hyAuthorityServiceImpl")
	private HyAuthorityService hyAuthorityService;
	
	
	/**
	 * 新增角色
	 * @param hyRole
	 * @return Json
	 */
	@RequestMapping(value="add", method = RequestMethod.POST)
	@ResponseBody
	public Json add(HyRole hyRole) {
		Json j = new Json();
		
		if(BeanUtils.isBlank(hyRole)){
			j.setSuccess(false);
			j.setMsg("角色不能为空");
			return j;
		}
		
		try{
			hyRoleService.save(hyRole);
			j.setSuccess(true);
			j.setMsg("添加成功！");
		}catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg("角色名不能重复");
		}
		return j;
	}
	
	
	/**
	 * 取消角色，以后不能再新建该角色的账号
	 * @param id
	 * @return
	 */
	@RequestMapping(value="cancel")
	@ResponseBody
	public Json cancel(Long id) {
		Json j = new Json();
		try{
			HyRole hyRole = hyRoleService.find(id);
			hyRole.setStatus(false);
			hyRoleService.update(hyRole);
			j.setSuccess(true);
			j.setMsg("取消成功！");
		}catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	/**
	 * 恢复角色，以后不能再新建该角色的账号
	 * @param id
	 * @return
	 */
	@RequestMapping(value="restore")
	@ResponseBody
	public Json restore(Long id) {
		Json j = new Json();
		try{
			HyRole hyRole = hyRoleService.find(id);
			hyRole.setStatus(true);
			hyRoleService.update(hyRole);
			j.setSuccess(true);
			j.setMsg("恢复成功！");
		}catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	@RequestMapping(value="update")
	@ResponseBody
	public Json update(HyRole hyRole) {
		Json j = new Json();
		try{	
			hyRoleService.update(hyRole, "status", "hyRolesForRoles","hyRolesForSubroles");
			j.setSuccess(true);
			j.setMsg("更新成功！");
		}catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	/**
	 * 角色分页列表信息
	 * @param pageable 分页信息
	 * @param queryParam 查询信息
	 * @return
	 */
	@RequestMapping(value="list/view", method = RequestMethod.POST)
	@ResponseBody
	public Json list(Pageable pageable, HyRole queryParam){	
		Json j = new Json();
		List<HashMap<String, Object>> lhm = new ArrayList<>();
		Page<HyRole> page = hyRoleService.findPage(pageable, queryParam);
		for(HyRole role : page.getRows()) {
			HashMap<String, Object> hm = new HashMap<String, Object>();
			hm.put("id", role.getId());
			hm.put("name", role.getName());
			hm.put("description", role.getDescription());
			hm.put("status", role.getStatus());
			hm.put("subRoles", role.getHyRolesForSubroles());
			lhm.add(hm);
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("pageSize", page.getPageSize());
		result.put("pageNumber", page.getPageNumber());
		result.put("total", page.getTotal());
		result.put("rows", lhm);
		j.setSuccess(true);
		j.setMsg("查询成功");
		j.setObj(result);
		return j;	
	}
	
	/**
	 * 获取所有权限
	 * 
	 * @return
	 */
	@RequestMapping(value="authoritylist/view")
	@ResponseBody
	public Json authorities(Long id){	
		Json j = new Json();
		
		try{	
			List<HashMap<String, Object>> lhm = new ArrayList<HashMap<String, Object>>();
			/** 根据ID找到角色 */
			HyRole role = hyRoleService.find(id);
			
			/** 根据角色找到角色权限对应实体集合 */
			Set<HyRoleAuthority> roleAuthorities = role.getHyRoleAuthorities();
			
			/** 找到所有权限信息 */
			List<Filter> filters = new ArrayList();
			Filter filter = Filter.isNull("hyAuthority");
			filters.add(filter);
			List<Order> orders = new ArrayList();
			Order order = new Order("id",Direction.asc);
			orders.add(order);
			List<HyAuthority> authorities = hyAuthorityService.findList(null,filters,orders);
			for(HyAuthority authority : authorities) {
				
				if(authority.getIsDisplay()) {
					HashMap<String, Object> hm = new HashMap<>();
					hm.put("id", authority.getId());
					hm.put("name", authority.getName());
					hm.put("range", authority.getRange());
					hm.put("operation", authority.getOperation());
					for(HyRoleAuthority hra : roleAuthorities) {
						if(hra.getAuthoritys().getId() == authority.getId()) {
							HashMap<String, Object> innerHm = new HashMap<>();
							innerHm.put("cr", hra.getRangeCheckedNumber());
							innerHm.put("co", hra.getOperationCheckedNumber());
							innerHm.put("range_checked_list_name", hra.getRangeCheckedListName());
							innerHm.put("departments", hra.getDepartmentIds());
							hm.put("checked", innerHm);
							break;
						}
					}
					hm.put("children", authorityChildren(authority, roleAuthorities));
					lhm.add(hm);
				}
				
			}
			j.setSuccess(true);
			j.setMsg("查询成功！");
			j.setObj(lhm);
		}catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;	
	}
	
	/**
	 * 给角色授权资源,以JSON提交
	 * @param roleId 角色ID
	 * @param resourceIds 资源ID
	 * @return
	 */
	@RequestMapping(value="/grant/authorities" , method=RequestMethod.POST)
	@ResponseBody
	public Json grantAuthorities(@RequestBody Wrap wrap) {
		Json j = new Json();
		hyRoleService.grantResources(wrap.roleId, wrap.authorities);
		j.setMsg("授权成功！");
		j.setSuccess(true);
		return j;
	}
	
	/**
	 * 获取角色
	 * @param session
	 * @return 可分配的角色数组
	 */
	@RequestMapping(value="getRoles/view", method = RequestMethod.GET)
	@ResponseBody
	public Json getRoles() {
		Json j = new Json();
			
		List<Filter> filters = new ArrayList<Filter>();
		Filter filter = Filter.eq("status", true);
		filters.add(filter);
		List<HyRole> roles = hyRoleService.findList(null, filters, null);
		
		j.setMsg("查询成功！");
		j.setSuccess(true);
		j.setObj(roles);
		return j;
	}
	
	/**
	 * 给角色分配能分配的子角色
	 * 
	 * @param roleId:父角色的ID
	 * @param subRoleIds:子角色的ID字符串
	 * @return  JSON
	 */
	@RequestMapping(value="/grant/subroles" , method=RequestMethod.POST)
	@ResponseBody
	public Json grantSubRoles(Long roleId, Long... subRoleIds ) {
		Json j = new Json();
		hyRoleService.grant(roleId, subRoleIds);
		j.setMsg("授权成功！");
		j.setSuccess(true);
		return j;
	}
	
	
	/**
	 * 获取分公司列表
	 * @return
	 */
	@RequestMapping(value="subcompany/view", method = RequestMethod.GET)
	@ResponseBody
	public Json subcompany(){
		Json j = new Json();
		try{	
			HyDepartmentModel model = hyDepartmentModelService.find(fengongsi);
			/** 找到所有子公司信息 */
			List<Filter> filters = new ArrayList<Filter>();
			Filter filter = Filter.eq("hyDepartmentModel", model);
			Filter filter1 = Filter.eq("status", 1);
			filters.add(filter);
			filters.add(filter1);
			List<Order> orders = new ArrayList<Order>();
			Order order = new Order("id",Direction.asc);
			orders.add(order);
			List<Department> departments = hyDepartmentService.findList(null,filters,orders);
			j.setSuccess(true);
			j.setMsg("查询成功！");
			j.setObj(departments);
		}catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;	
	}
	
	/**
	 * 获取部门列表
	 * @return
	 */
	@RequestMapping(value="department/view", method = RequestMethod.GET)
	@ResponseBody
	public Json department(){
		Json j = new Json();
		try{
			List<HashMap<String, Object>> lhm = new ArrayList<HashMap<String, Object>>();
			List<Filter> filters = new ArrayList<Filter>();
			Filter f = Filter.eq("status", 1);
			Filter f1 = Filter.eq("isCompany", false);

			filters.add(f);
			filters.add(f1);
			List<Order> orders = new ArrayList<>();
			Order order = Order.asc("id");
			orders.add(order);
			List<Department> departs = hyDepartmentService.findList(null, filters, orders);
		
			for(Department depart : departs){
				if(depart.getHyDepartment() != null && depart.getHyDepartment().getIsCompany() == true) {
					HashMap<String, Object> hm = new HashMap<String, Object>();
					hm.put("id", depart.getId());
					hm.put("fullName", depart.getFullName());
					hm.put("children", addChildren(depart));
					lhm.add(hm);
				}			
			}
			j.setSuccess(true);
			j.setMsg("查询成功！");
			j.setObj(lhm);
		}catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;	
	}
	
	public List<HashMap<String, Object>> addChildren(Department parent) {
		List<HashMap<String, Object>> list = new ArrayList();
		if(parent.getHyDepartments().size() > 0){
			for(Department child : parent.getHyDepartments()) {
				HashMap<String, Object> hm = new HashMap<String, Object>();
				hm.put("id", child.getId());
				hm.put("fullName", child.getFullName());
				hm.put("children", addChildren(child));
				list.add(hm);
			}
		}
		return list;
	}
	
	public List<HashMap<String, Object>> authorityChildren(HyAuthority parent,Set<HyRoleAuthority> roleAuthorities) {
		List<HashMap<String, Object>> list = new ArrayList();
		if(parent.getHyAuthorities().size() > 0){
			for(HyAuthority child : parent.getHyAuthorities()) {
				HashMap<String, Object> hm = new HashMap<String, Object>();
				hm.put("id", child.getId());
				hm.put("name", child.getName());
				hm.put("range", child.getRange());
				hm.put("operation", child.getOperation());
				for(HyRoleAuthority hra : roleAuthorities) {
					if(hra.getAuthoritys().getId() == child.getId()) {
						HashMap<String, Object> innerHm = new HashMap<>();
						innerHm.put("cr", hra.getRangeCheckedNumber());
						innerHm.put("co", hra.getOperationCheckedNumber());
						innerHm.put("range_checked_list_name", hra.getRangeCheckedListName());
						innerHm.put("departments", hra.getDepartmentIds());
						hm.put("checked", innerHm);
						break;
					}
				}
				hm.put("children", authorityChildren(child, roleAuthorities));
				list.add(hm);
			}
		}
		return list;
	}
	
	/**
	 * 权限复制
	 * @param fromId 原来角色
	 * @param toId 后来角色
	 * @return
	 */
	@RequestMapping(value="copy")
	@ResponseBody
	public Json copyAuthority(Long fromId, Long toId){
		Json j = new Json();
		try{	
			HyRole fromRole = hyRoleService.find(fromId);
			HyRole toRole = hyRoleService.find(toId);
			
			if(null == fromRole || null == toRole) {
				j.setMsg("角色不存在");
				j.setSuccess(false);
				return j;
			}
			Set<Auth> auths = new HashSet<>();
			Set<HyRoleAuthority> fromAuth = fromRole.getHyRoleAuthorities();
			for(HyRoleAuthority temp : fromAuth) {
				Auth auth = new Auth();
				auth.setId(temp.getAuthoritys().getId());
				auth.setCo(temp.getOperationCheckedNumber());
				auth.setCr(temp.getRangeCheckedNumber());
				Set<Long> departs = new HashSet<>();
				Set<Department> depart = temp.getDepartments();
				for(Department t : depart) {
					departs.add(t.getId());
				}
				auth.setDepartments(departs);
				auths.add(auth);
			}
			
			hyRoleService.grantResources(toId, auths);
			
			j.setSuccess(true);
			j.setMsg("复制权限成功！");
		}catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
}
