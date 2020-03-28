package com.hongyu.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyCompany;
import com.hongyu.entity.HyRole;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyCompanyService;
import com.hongyu.service.HyDepartmentModelService;
import com.hongyu.service.HyRoleService;
import com.hongyu.util.AuthorityUtils;
import com.hongyu.util.liyang.EmployeeUtil;

@Controller
@RequestMapping("/admin/subCompanyAdmin/employeeManagement")
public class SubCompanyEmployeeManagementController {
	@Resource(name="hyCompanyServiceImpl")
	HyCompanyService hyCompanyService;
	
	@Resource(name="departmentServiceImpl")
	DepartmentService departmentService;
	
	@Resource(name="hyAdminServiceImpl")
	private HyAdminService hyAdminService;
	@Resource(name="hyRoleServiceImpl")
	HyRoleService hyRoleService;
	@Resource(name="hyDepartmentModelServiceImpl")
	HyDepartmentModelService hyDepartmentModelService;
	
	@RequestMapping(value="list/view", method = RequestMethod.POST)
	@ResponseBody
	public Json listview(Pageable pageable,String departmentName,String name,HttpSession session,HttpServletRequest request)
	{
		Json json=new Json();
		try{
			HyAdmin hyAdminee=new HyAdmin();
			Map<String,Object> map=new HashMap<String,Object>();
			List<HashMap<String, Object>> list = new ArrayList<>();
			/**
			 * 获取当前用户
			 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			
			/** 
			 * 获取用户权限范围
			 */
			CheckedOperation co=(CheckedOperation) request.getAttribute("co");
			
			/** 所有符合条件的账号 ,默认可以看到自己创建的数据 */
			Set<HyAdmin> hyAdmins = AuthorityUtils.getAdmins(session, request);
			List<Filter> adminFilter=new ArrayList<Filter>();
			adminFilter.add(Filter.in("creator", hyAdmins));
			if(departmentName==null)
			{
				if(name!=null){
					adminFilter.add(Filter.like("name", name));
				}
				pageable.setFilters(adminFilter);
				Page<HyAdmin> page=this.hyAdminService.findPage(pageable,hyAdminee);
				if(page.getTotal()>0){
				    for(HyAdmin hyAdmin:page.getRows())
				    {
					    HashMap<String,Object> adMap=new HashMap<String,Object>();
					    HyAdmin creator=hyAdmin.getCreator();
					    adMap.put("name", hyAdmin.getName());
					    adMap.put("mobilephone", hyAdmin.getMobile());
					    adMap.put("usename",hyAdmin.getUsername());
					    adMap.put("role", hyAdmin.getRole().getName());
					    adMap.put("company", EmployeeUtil.getCompany(hyAdmin).getHyCompany().getCompanyName());
					    adMap.put("department", hyAdmin.getDepartment().getName());
					    adMap.put("isEnabled", hyAdmin.getIsEnabled());
					    if(hyAdmin.getCreator()!=null){
						    adMap.put("creator", creator.getName());
				        }
					    /** 当前用户对本条数据的操作权限 */
					    if(creator.equals(admin)){
				    	    if(co==CheckedOperation.view){
				    		    adMap.put("privilege", "view");
				    	    }
				    	    else{
				    		    adMap.put("privilege", "edit");
				    	    }
				        }
				        else{
				    	    if(co==CheckedOperation.edit){
				    		    adMap.put("privilege", "edit");
				    	    }
				    	    else{
				    		    adMap.put("privilege", "view");
				    	    }	    	
				        }
					    list.add(adMap);
				    }
				}
				map.put("rows", list);
			    map.put("pageNumber", Integer.valueOf(pageable.getPage()));
			    map.put("pageSize", Integer.valueOf(pageable.getRows()));
			    map.put("total",Long.valueOf(page.getTotal()));
				json.setMsg("查询成功");
			    json.setSuccess(true);
			    json.setObj(map);
			}
			else{
				if(name != null){
				    adminFilter.add(Filter.like("name", name));
			    }
				List<Filter> departmentFilter=new ArrayList<Filter>();
				departmentFilter.add(Filter.like("name", departmentName));
				List<Department> departmentList=departmentService.findList(null,departmentFilter,null);
				if(departmentList.size()==0){
					json.setMsg("查询成功");
				    json.setSuccess(true);
				    json.setObj(new Page<HyAdmin>());
				}
				else{
					adminFilter.add(Filter.in("department", departmentList));    
				    pageable.setFilters(adminFilter);
				    Page<HyAdmin> page=this.hyAdminService.findPage(pageable,hyAdminee);
					if(page.getTotal()>0){	
					    for(HyAdmin hyAdmin:page.getRows())
					    {
						    HashMap<String,Object> adMap=new HashMap<String,Object>();
						    HyAdmin creator=hyAdmin.getCreator();
						    adMap.put("name", hyAdmin.getName());
						    adMap.put("mobilephone", hyAdmin.getMobile());
						    adMap.put("usename",hyAdmin.getUsername());
						    adMap.put("role", hyAdmin.getRole().getName());
						    adMap.put("company", EmployeeUtil.getCompany(hyAdmin).getHyCompany().getCompanyName());
						    adMap.put("department", hyAdmin.getDepartment().getName());
						    adMap.put("isEnabled", hyAdmin.getIsEnabled());
						    if(hyAdmin.getCreator()!=null){
							    adMap.put("creator", creator.getName());
					        }
						    /** 当前用户对本条数据的操作权限 */
						    if(creator.equals(admin)){
					    	    if(co==CheckedOperation.view){
					    		    adMap.put("privilege", "view");
					    	    }
					    	    else{
					    		    adMap.put("privilege", "edit");
					    	    }
					        }
					        else{
					    	    if(co==CheckedOperation.edit){
					    		    adMap.put("privilege", "edit");
					    	    }
					    	    else{
					    		    adMap.put("privilege", "view");
					    	    }	    	
					        }
						    list.add(adMap);
					    }
					}
					map.put("rows", list);
				    map.put("pageNumber", Integer.valueOf(pageable.getPage()));
				    map.put("pageSize", Integer.valueOf(pageable.getRows()));
				    map.put("total",Long.valueOf(page.getTotal()));
					json.setMsg("查询成功");
				    json.setSuccess(true);
				    json.setObj(map);
				}		
			}		
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping(value="detail/view")
	@ResponseBody
	public Json detailview(String usename)
	{
		Json json=new Json();		
		try{
			HyAdmin hyAdmin=hyAdminService.find(usename);
			HashMap<String, Object> map = new HashMap<>();
			map.put("role", hyAdmin.getRole().getName());
			map.put("roleId", hyAdmin.getRole().getId());
			map.put("usename", hyAdmin.getUsername());
			map.put("name", hyAdmin.getName());
			map.put("mobilephone", hyAdmin.getMobile());
			map.put("company", EmployeeUtil.getCompany(hyAdmin).getHyCompany().getCompanyName());
			map.put("department",hyAdmin.getDepartment().getName());
			map.put("departmentId",hyAdmin.getDepartment().getId());
			json.setMsg("查询成功");
		    json.setSuccess(true);
		    json.setObj(map);
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping(value="add", method = RequestMethod.POST)
	@ResponseBody
	public Json add(Long roleId,Long departmentId,String usename,String name,String mobilephone,HttpSession httpSession)
	{
		Json json=new Json();
		try{
			//判断新建员工的用户名是否已存在
			List<HyAdmin> adminList=hyAdminService.findAll();
			for(HyAdmin tmp:adminList) {
				if(tmp.getUsername().equals(usename)) {
					json.setMsg("该账号已存在");
					json.setSuccess(false);
					return json;
				}
			}
			HyAdmin hyAdmin=new HyAdmin();
			String username = (String) httpSession.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			HyRole role=hyRoleService.find(roleId);	
			hyAdmin.setRole(role);
			hyAdmin.setCreator(admin);
			hyAdmin.setUsername(usename);
			hyAdmin.setName(name);
			hyAdmin.setMobile(mobilephone);
			hyAdmin.setIsEnabled(true);
			Department department=departmentService.find(departmentId);
			hyAdmin.setDepartment(department);
			hyAdminService.save(hyAdmin);
			json.setMsg("添加成功");
		    json.setSuccess(true);
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping("edit")
	@ResponseBody
	public Json modify(String usename,String name,String mobilephone,Long roleId,Long departmentId)
	{
		Json json=new Json();
		try{
			HyAdmin hyAdmin=new HyAdmin();
			hyAdmin.setUsername(usename);
			HyRole hyRole=hyRoleService.find(roleId);
			hyAdmin.setRole(hyRole);
			Department department=departmentService.find(departmentId);
			hyAdmin.setDepartment(department);
			hyAdmin.setName(name);
			hyAdmin.setMobile(mobilephone);
			hyAdmin.setModifyDate(new Date());;
			hyAdminService.update(hyAdmin,"creator","password","isEnabled","position","isOnjob","wechat",
					"wechatUrl","qq","address","isManager","isLocked","loginFailureCount","lockedDate",
					"loginDate","loginIp");
			json.setMsg("编辑成功");
		    json.setSuccess(true);
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping(value="roleList")
	@ResponseBody
	public Json roleList(HttpSession httpSession)
	{
		Json json=new Json();
		try{
			String username = (String) httpSession.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			HyRole hyRole=admin.getRole();
			Set<HyRole> hyRoleSet=hyRole.getHyRolesForSubroles();
			List<HyRole> roleList=new ArrayList<HyRole>(hyRoleSet);
			List<HashMap<String,Object>> obj=new ArrayList<HashMap<String,Object>>();
			for(HyRole role:roleList)
			{
				 HashMap<String, Object> map=new HashMap<String, Object>();
				 map.put("roleId", role.getId());
				 map.put("roleName", role.getName());
				 obj.add(map);
			}
			json.setMsg("列表成功");
		    json.setSuccess(true);
		    json.setObj(obj);
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	
//	@RequestMapping(value="departmentList")
//	@ResponseBody
//	public Json departmentList(HttpSession httpSession)
//	{
//		Json json=new Json();
//		try{
//			String username = (String) httpSession.getAttribute(CommonAttributes.Principal);
//			HyAdmin hyAdmin = hyAdminService.find(username);
//			HyCompany hyCompany=EmployeeUtil.getCompany(hyAdmin).getHyCompany();
//			List<Map<String, Object>> list = new LinkedList<>();
//			Map<String, Object> map = new HashMap<String, Object>();
//			map.put("company", hyCompany.getCompanyName());
//			Department hyDepartment=hyCompany.getHyDepartment();
//			List<Department> hyDepartments=new ArrayList<>(hyDepartment.getHyDepartments());
//			for(Department department:hyDepartments) {
//				Map<String, Object> obj = new HashMap<String, Object>();
//				obj.put("departmentId", department.getId());
//				obj.put("departmentName", department.getName());
//				list.add(obj);
//			}
//			map.put("departmentList", list);
//			json.setMsg("列表成功");
//		    json.setSuccess(true);
//		    json.setObj(map);
//		}
//		catch(Exception e){
//			json.setSuccess(false);
//			json.setMsg(e.getMessage());
//		}
//		return json;
//	}
	
	/**
	 * 获取部门树
	 * @return
	 */
	@RequestMapping(value="tree/view",method = RequestMethod.GET)
	@ResponseBody
	public Json tree(HttpSession httpSession) {
		Json j = new Json();
		try{
			String username = (String) httpSession.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin = hyAdminService.find(username);
			HyCompany hyCompany=EmployeeUtil.getCompany(hyAdmin).getHyCompany();
			List<HashMap<String, Object>> lhm = new ArrayList<>();
			Department depart = hyCompany.getHyDepartment();
			HashMap<String, Object> hm = new HashMap<String, Object>();
			hm.put("value", depart.getId());
			hm.put("label", depart.getFullName());
			hm.put("children", addChildren(depart));
			lhm.add(hm);
			j.setMsg("查询成功");
			j.setSuccess(true);
			j.setObj(lhm);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	public List<HashMap<String, Object>> addChildren(Department parent) {
		List<HashMap<String, Object>> list = new ArrayList<>();
		if(parent.getHyDepartments().size() > 0){
			for(Department child : parent.getHyDepartments()) {
				HashMap<String, Object> hm = new HashMap<String, Object>();
				hm.put("value", child.getId());
				hm.put("label", child.getFullName());
				hm.put("children", addChildren(child));
				list.add(hm);
			}
		}
		return list;
	}
	
	@RequestMapping(value="cancel")
	@ResponseBody
	public Json cancel(String usename)
	{
		Json json=new Json();
		try{
			HyAdmin hyAdmin=hyAdminService.find(usename);
			hyAdmin.setIsEnabled(false);
			hyAdminService.update(hyAdmin);
			json.setMsg("取消成功");
		    json.setSuccess(true);
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping(value="restore")
	@ResponseBody
	public Json restore(String usename)
	{
		Json json=new Json();
		try{
			HyAdmin hyAdmin=hyAdminService.find(usename);
			hyAdmin.setIsEnabled(true);
			hyAdminService.update(hyAdmin);
			json.setMsg("恢复成功");
		    json.setSuccess(true);
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
}

