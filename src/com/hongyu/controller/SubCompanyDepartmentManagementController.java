package com.hongyu.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import com.hongyu.entity.HyDepartmentModel;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyCompanyService;
import com.hongyu.service.HyDepartmentModelService;
import com.hongyu.util.AuthorityUtils;

@Controller
@RequestMapping("/admin/subCompanyAdmin/departmentManagement")
public class SubCompanyDepartmentManagementController {
	@Resource(name="hyCompanyServiceImpl")
	HyCompanyService hyCompanyService;
	
	@Resource(name="departmentServiceImpl")
	DepartmentService departmentService;
	
	@Resource(name="hyAdminServiceImpl")
	private HyAdminService hyAdminService;
	
	@Resource(name="hyDepartmentModelServiceImpl")
	HyDepartmentModelService hyDepartmentModelService;
	
	@RequestMapping(value="list/view", method = RequestMethod.POST)
	@ResponseBody
	public Json listview(Pageable pageable,HttpSession session,HttpServletRequest request)
	{
		Json json=new Json();
		try{
			Department department=new Department();
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
			List<Filter> departmentFilter=new ArrayList<Filter>();
			departmentFilter.add(Filter.in("creator", hyAdmins));
			departmentFilter.add(Filter.eq("isCompany",false));
			pageable.setFilters(departmentFilter);
			Page<Department> page=departmentService.findPage(pageable,department);
			if(page.getTotal()>0){
				for(Department hyDepartment:page.getRows()){
					HashMap<String,Object> depMap=new HashMap<String,Object>();
				    HyAdmin creator=hyDepartment.getCreator();
				    depMap.put("id", hyDepartment.getId());
				    depMap.put("name", hyDepartment.getName());
				    depMap.put("parentName", hyDepartment.getHyDepartment().getName());
				    depMap.put("remark", hyDepartment.getRemark());
				    if(hyDepartment.getCreator()!=null){
				    	depMap.put("creator", creator.getName());
			        }
				    /** 当前用户对本条数据的操作权限 */
				    if(creator.equals(admin)){
			    	    if(co==CheckedOperation.view){
			    	    	depMap.put("privilege", "view");
			    	    }
			    	    else{
			    	    	depMap.put("privilege", "edit");
			    	    }
			        }
			        else{
			    	    if(co==CheckedOperation.edit){
			    	    	depMap.put("privilege", "edit");
			    	    }
			    	    else{
			    	    	depMap.put("privilege", "view");
			    	    }	    	
			        }
				    list.add(depMap);
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
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping(value="detail/view", method = RequestMethod.GET)
	@ResponseBody
	public Json detailview(Long id)
	{
		Json json=new Json();
		try{
			Department department=departmentService.find(id);
			Map<String,Object> map=new HashMap<String,Object>();
			map.put("name", department.getName());
			map.put("parentName", department.getHyDepartment().getName());
			map.put("remark", department.getRemark());
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
	public Json add(Department department,Long parentId,String modelName,HttpSession httpSession)
	{
		Json json=new Json();
		try{
			Department parentDepartment=departmentService.find(parentId);
			List<Department> departmentList=new ArrayList<Department>(parentDepartment.getHyDepartments());
			List<String> list=new ArrayList<String>();
			for(Department depart:departmentList){
				list.add(depart.getName());
			}
			if(list.contains(department.getName())){
				json.setMsg("该部门名称已存在");
			    json.setSuccess(true);
			}
			else{
				String username = (String) httpSession.getAttribute(CommonAttributes.Principal);
				HyAdmin admin = hyAdminService.find(username);
				department.setCreator(admin);			
				department.setHyDepartment(parentDepartment);
				HyDepartmentModel hyDepartmentModel=hyDepartmentModelService.find(modelName);
				department.setHyDepartmentModel(hyDepartmentModel);
				department.setIsCompany(false);
				department.setCreateDate(new Date());
				departmentService.save(department);
				json.setMsg("添加成功");
			    json.setSuccess(true);
			}
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping("edit")
	@ResponseBody
	public Json modify(Department department,Long parentId,String modelName)
	{
		Json json=new Json();
		try{
			Department parentDepartment=departmentService.find(parentId);
			List<Department> departmentList=new ArrayList<Department>(parentDepartment.getHyDepartments());
			List<String> list=new ArrayList<String>();
			for(Department depart:departmentList){
				list.add(depart.getName());
			}
			if(list.contains(department.getName())){
				json.setMsg("该部门名称已存在");
			    json.setSuccess(true);
			}
			else{
				department.setHyDepartment(parentDepartment);
				HyDepartmentModel hyDepartmentModel=hyDepartmentModelService.find(modelName);
				department.setHyDepartmentModel(hyDepartmentModel);
				department.setModifyDate(new Date());
				departmentService.update(department,"isCompany","creator");
				json.setMsg("编辑成功");
			    json.setSuccess(true);
			}		
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping("delete")
	@ResponseBody
	public Json delete(Long id)
	{
		Json json=new Json();
		try{
			departmentService.delete(id);
			json.setMsg("删除成功");
		    json.setSuccess(true);
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping("parentList")
	@ResponseBody
	public Json parentList(HttpSession session)
	{
	    Json json=new Json();
	    try{
	    	/**
			 * 获取当前用户
			 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			Department adDepartment=admin.getDepartment();
			Department hyDepartment=new Department();
			if(adDepartment.getIsCompany()==true){
				hyDepartment=adDepartment;
			}
			else{
				if(adDepartment.getHyDepartment().getIsCompany()==true){
					hyDepartment=adDepartment.getHyDepartment();
				}
				else{
					if(adDepartment.getHyDepartment().getHyDepartment().getIsCompany()==true){
						hyDepartment=adDepartment.getHyDepartment().getHyDepartment();
					}
					else{
						hyDepartment=adDepartment.getHyDepartment().getHyDepartment().getHyDepartment();
					}
				}
			}
			HashMap<String,Object> map=new HashMap<String,Object>();
			List<HashMap<String,Object>> list=new ArrayList<HashMap<String,Object>>();
			map.put("id", hyDepartment.getId());
			map.put("name", hyDepartment.getName());
			list.add(map);
	    	List<Department> departmentList=new ArrayList<Department>(hyDepartment.getHyDepartments());
	    	for(Department department:departmentList){
	    		HashMap<String,Object> demap=new HashMap<String,Object>();	    		
	    		demap.put("id",department.getId());
	    		demap.put("name", department.getName());
	    		list.add(demap);
	    	}
	    	json.setMsg("列表成功");
		    json.setSuccess(true);
		    json.setObj(list);
	    }
	    catch(Exception e){
	    	json.setSuccess(false);
			json.setMsg(e.getMessage());
	    }
	    return json;
	}
	
	@RequestMapping("modelList")
	@ResponseBody
	public Json modelList()
	{
		Json json=new Json();
		try{
			List<HyDepartmentModel> modelList=hyDepartmentModelService.findAll();
			json.setMsg("列表成功");
		    json.setSuccess(true);
		    json.setObj(modelList);
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
}
