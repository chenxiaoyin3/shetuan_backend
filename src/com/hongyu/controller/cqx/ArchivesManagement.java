package com.hongyu.controller.cqx;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Date;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.Department;
import com.hongyu.entity.EmployeeInduction;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyEmployeeInductionEducation;
import com.hongyu.entity.HyEmployeeInductionJob;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.service.EmployeeInductionService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyEmployeeInductionEducationService;
import com.hongyu.service.HyEmployeeInductionJobService;
import com.hongyu.service.DepartmentService;
import com.hongyu.util.AuthorityUtils;
@RestController
@RequestMapping("/admin/business/archivesManagement/")
public class ArchivesManagement {
	@Resource(name = "employeeInductionServiceImpl")
	EmployeeInductionService employeeInductionService;
	
	@Resource(name = "hyEmployeeInductionEducationServiceImpl")
	HyEmployeeInductionEducationService hyEmployeeInductionEducationService;

	@Resource(name = "hyEmployeeInductionJobServiceImpl")
	HyEmployeeInductionJobService hyEmployeeInductionJobService;
	
	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;
	
	@Resource(name="departmentServiceImpl")
	DepartmentService departmentService;
	
	@RequestMapping("list/view")
	@ResponseBody
	public Json list(Pageable pageable,String name,EmployeeInduction employeeInduction,HttpSession session,HttpServletRequest request) {
		Json json=new Json();
		try {
			HashMap<String,Object> hm=new HashMap<String,Object>();
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
			/** 筛选 */
			List<Filter> filters = new ArrayList<Filter>();
			if(name!=null) {
				filters.add(Filter.like("name", name));
			}
			pageable.setFilters(filters);
			/** 将数据按照时间排序 */
			List<Order> orders =new ArrayList<Order>();
			Order order=Order.desc("createtime");
			orders.add(order);
			
			
			pageable.setOrders(orders);
			Page<EmployeeInduction> page=employeeInductionService.findPage(pageable,employeeInduction);
			for(EmployeeInduction tmp:page.getRows()) {
				HashMap<String,Object> m=new HashMap<String,Object>();
				m.put("id", tmp.getId());
				m.put("name",tmp.getName());
				m.put("sex",tmp.getSex());
				m.put("nationality",tmp.getNationality());
				m.put("department",tmp.getDepartment());
				m.put("identificationCardId",tmp.getIdentificationCardId());
				m.put("phone",tmp.getPhone());
//				m.put("hyEmployeeInductionEducation",tmp.getHyEmployeeInductionEducation());
//				m.put("hyEmployeeInductionJob",tmp.getHyEmployeeInductionJob());
				result.add(m);
			}
			hm.put("total",page.getTotal());
			hm.put("pageNumber",page.getPageNumber());
			hm.put("pageSize",page.getPageSize());
			hm.put("rows",result);
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(hm);
		} 
		catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取失败: "+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}

	@RequestMapping("/detail/view")
	@ResponseBody
	public Json detail(Long id){
		Json json=new Json();
		try {
			EmployeeInduction employeeInduction=employeeInductionService.find(id);
			if(employeeInduction!=null){
				json.setSuccess(true);
				json.setMsg("获取成功");
				json.setObj(employeeInduction);
			}else{
				json.setSuccess(false);
				json.setMsg("获取失败");
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取失败");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	
	@RequestMapping("/getDepartmentById")
	@ResponseBody
	public Json getDepartmentById(Long id){
		Json json=new Json();
		try {
			List<Long> treePaths = new ArrayList<Long>();
			treePaths=departmentService.find(id).getTreePaths();
			treePaths.toArray();
			String[] string=new String[treePaths.size()];
			List<HashMap<String, Object>> result = new ArrayList<>();			
			for(int i=0;i<treePaths.size();i++) {
				HashMap<String, Object> hm = new HashMap<String, Object>();
				Department depart =departmentService.find(treePaths.get(i));
				string[i]=(Long.toString(depart.getId()));
//				hm.put("value", depart.getId());
//				hm.put("label", depart.getFullName());
//				hm.put();
//				result.add(hm);
			}
			
			if(string.length!=0){
				json.setSuccess(true);
				json.setMsg("获取成功");
				json.setObj(string);
			}else{
				json.setSuccess(false);
				json.setMsg("获取失败");
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取失败");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	
	/**
	 * 新建员工入职信息
	 * @param 
	 * @author cxy
 	 * @date 2019-04-19
	 */
	@RequestMapping("add")
	@ResponseBody
	@Transactional(propagation = Propagation.REQUIRED)
	public Json add(@RequestBody EmployeeInduction employeeInduction, HttpSession session) 
	{
		Json json = new Json();
		try {
			/**
			 * 获取当前用户
			 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin user = hyAdminService.find(username);
			
			employeeInduction.setCreatetime(new Date());
			employeeInduction.setModifytime(null);
			
			for(HyEmployeeInductionEducation hyEmployeeInductionEducation:employeeInduction.getHyEmployeeInductionEducation()){
				hyEmployeeInductionEducation.setEmployeeInduction(employeeInduction);
			}
			for(HyEmployeeInductionJob hyEmployeeInductionJob:employeeInduction.getHyEmployeeInductionJob()){
				hyEmployeeInductionJob.setEmployeeInduction(employeeInduction);
			}
			
			employeeInductionService.save(employeeInduction);
			
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
	 * 编辑员工入职信息
	 * @param 
	 * @param 
	 * @author cxy
 	 * @date 2019-04-19
	 */
	@RequestMapping("edit")
	@ResponseBody
	@Transactional(propagation = Propagation.REQUIRED)
	public Json edit(@RequestBody EmployeeInduction employeeInduction, HttpSession session) 
	{
		Json json = new Json();
		try {
			/**
			 * 获取当前用户
			 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			employeeInduction.setModifytime(new Date());

			for(HyEmployeeInductionEducation hyEmployeeInductionEducation:employeeInduction.getHyEmployeeInductionEducation()){
				hyEmployeeInductionEducation.setEmployeeInduction(employeeInduction);
				hyEmployeeInductionEducationService.update(hyEmployeeInductionEducation);
			}
			for(HyEmployeeInductionJob hyEmployeeInductionJob:employeeInduction.getHyEmployeeInductionJob()){
				hyEmployeeInductionJob.setEmployeeInduction(employeeInduction);
				hyEmployeeInductionJobService.update(hyEmployeeInductionJob);
			}
			employeeInductionService.update(employeeInduction);
			json.setSuccess(true);
			json.setMsg("修改成功");
			json.setObj(null);
		}catch (Exception e){
			json.setSuccess(false);
			json.setMsg("修改失败："+e.getMessage());
			json.setObj(null);
		}
    
		return json;
	}
	
	/**
	 * 删除员工入职信息
	 * @param id
	 * @author cxy
 	 * @date 2019-04-21
	 */
	@RequestMapping("delete")
	@ResponseBody
	@Transactional(propagation = Propagation.REQUIRED)
	public Json delete(Long id) 
	{

		Json json = new Json();
		try {
			EmployeeInduction employeeInduction = employeeInductionService.find(id);
			employeeInductionService.delete(employeeInduction);
			
			json.setSuccess(true);
			json.setMsg("删除成功");
			json.setObj(null);
		}catch (Exception e){
			json.setSuccess(false);
			json.setMsg("删除失败："+e.getMessage());
			json.setObj(null);
		}
	
		return json;
	}
	
	/**
	 * 获取部门树
	 * @return
	 */
	@RequestMapping(value="department/view",method = RequestMethod.GET)
	@ResponseBody
	public Json tree() {
		Json j = new Json();
		try{
			List<HashMap<String, Object>> lhm = new ArrayList<>();
			Department depart = departmentService.find(Long.valueOf(1));
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
}
