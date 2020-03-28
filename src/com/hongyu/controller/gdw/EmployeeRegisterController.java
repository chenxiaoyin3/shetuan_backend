package com.hongyu.controller.gdw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.Article;
import com.hongyu.entity.Department;
import com.hongyu.entity.EmployeeRegister;
import com.hongyu.entity.EmployeeRegisterEducation;
import com.hongyu.entity.EmployeeRegisterWork;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.Position;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.EmployeeRegisterService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.PositionService;
import com.hongyu.util.AuthorityUtils;

@Controller
@RequestMapping("/admin/employeeRegister/")
public class EmployeeRegisterController {

	@Resource(name = "employeeRegisterServiceImpl")
	EmployeeRegisterService employeeRegisterService;

	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;
	
	@Resource(name="departmentServiceImpl")
	DepartmentService departmentService;

	@Resource(name="positionServiceImpl")
	PositionService positionService;
	/**
	 * @param employeeRegister
	 * @param session
	 * @return
	 */
	@RequestMapping("add")
	@ResponseBody
	public Json add(@RequestBody EmployeeRegister employeeRegister,Long departmentId,Long positionId, HttpSession session) {
		Json json = new Json();
		try {
			String username1 = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin = hyAdminService.find(username1);
		
			if(hyAdmin.getPosition().equals("行政中心经理")){
				
			}
			if (employeeRegister != null) {
				if (employeeRegister.getEmployeeRegisterWorks() != null
						&& employeeRegister.getEmployeeRegisterWorks().size() > 0) {
					for (EmployeeRegisterWork employeeRegisterWork : employeeRegister.getEmployeeRegisterWorks()) {
						employeeRegisterWork.setEmployeeRegister(employeeRegister);
					}
				}
				if (employeeRegister.getEmployeeRegisterEducations() != null
						&& employeeRegister.getEmployeeRegisterEducations().size() > 0) {
					for (EmployeeRegisterEducation employeeRegisterEducation : employeeRegister
							.getEmployeeRegisterEducations()) {
						employeeRegisterEducation.setEmployeeRegister(employeeRegister);
					}
				}
				Department department=departmentService.find(departmentId);
				Position position=positionService.find(positionId);
				employeeRegister.setPosition(position);
				employeeRegister.setDepartment(department);
				employeeRegister.setOperator(hyAdmin);
				employeeRegisterService.save(employeeRegister);
				json.setSuccess(true);
				json.setMsg("添加成功");
			} else {
				json.setSuccess(false);
				json.setMsg("添加失败，内容为空");
			}

		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("添加失败");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}

	@RequestMapping("modify")
	@ResponseBody
	public Json modify(EmployeeRegister employeeRegister,Long departmentId,Long positionId) {
		Json json = new Json();
		try {
			EmployeeRegister oldEmployeeRegister=employeeRegisterService.find(employeeRegister.getId());
			if(oldEmployeeRegister!=null){
				oldEmployeeRegister.setImage(employeeRegister.getImage());
				oldEmployeeRegister.setName(employeeRegister.getName());
				oldEmployeeRegister.setIsMale(employeeRegister.getIsMale());
				oldEmployeeRegister.setBirthday(employeeRegister.getBirthday());
				oldEmployeeRegister.setIdNumber(employeeRegister.getIdNumber());
				oldEmployeeRegister.setAddress(employeeRegister.getAddress());
				oldEmployeeRegister.setNation(employeeRegister.getNation());
				oldEmployeeRegister.setHeight(employeeRegister.getHeight());
				oldEmployeeRegister.setWeight(employeeRegister.getWeight());
				oldEmployeeRegister.setIsMarried(employeeRegister.getIsMarried());
				oldEmployeeRegister.setPolitics(employeeRegister.getPolitics());
				oldEmployeeRegister.setHealth(employeeRegister.getHealth());
				oldEmployeeRegister.setWorkingSeniority(employeeRegister.getWorkingSeniority());
				oldEmployeeRegister.setSalary(employeeRegister.getSalary());
				oldEmployeeRegister.setDegree(employeeRegister.getDegree());
				oldEmployeeRegister.setProfession(employeeRegister.getProfession());
				oldEmployeeRegister.setGraduatedSchool(employeeRegister.getGraduatedSchool());
				oldEmployeeRegister.setIsOnBusiness(employeeRegister.getIsOnBusiness());
				oldEmployeeRegister.setIsOvertime(employeeRegister.getIsOvertime());
				oldEmployeeRegister.setIsRegulated(employeeRegister.getIsRegulated());
				oldEmployeeRegister.setMobile(employeeRegister.getMobile());
				oldEmployeeRegister.setComputerSkill(employeeRegister.getComputerSkill());
				oldEmployeeRegister.setOtherSkill(employeeRegister.getOtherSkill());
				oldEmployeeRegister.setHobby(employeeRegister.getHobby());
				oldEmployeeRegister.setFirstMobile(employeeRegister.getFirstMobile());
				oldEmployeeRegister.setFirstPhone(employeeRegister.getFirstPhone());
				oldEmployeeRegister.setAddress1(employeeRegister.getAddress1());
				oldEmployeeRegister.setUrgentPhone(employeeRegister.getUrgentPhone());
				oldEmployeeRegister.setUrgentMobile(employeeRegister.getUrgentMobile());
				oldEmployeeRegister.setAddress2(employeeRegister.getAddress2());
				oldEmployeeRegister.setRemark(employeeRegister.getRemark());
				oldEmployeeRegister.setEntrytime(employeeRegister.getEntrytime());
				oldEmployeeRegister.setJobStatus(employeeRegister.getJobStatus());
				oldEmployeeRegister.setRegisterStatus(employeeRegister.getRegisterStatus());
				oldEmployeeRegister.setUsername(employeeRegister.getUsername());
				oldEmployeeRegister.setImageDiploma(employeeRegister.getImageDiploma());
				oldEmployeeRegister.setImageIdcard(employeeRegister.getImageIdcard());
				oldEmployeeRegister.getEmployeeRegisterWorks().clear();
				if (employeeRegister.getEmployeeRegisterWorks() != null
						&& employeeRegister.getEmployeeRegisterWorks().size() > 0) {
					for (EmployeeRegisterWork employeeRegisterWork : employeeRegister.getEmployeeRegisterWorks()) {
						employeeRegisterWork.setEmployeeRegister(oldEmployeeRegister);
					}
					oldEmployeeRegister.getEmployeeRegisterWorks().addAll(employeeRegister.getEmployeeRegisterWorks());
				}
				oldEmployeeRegister.getEmployeeRegisterEducations().clear();
				if (employeeRegister.getEmployeeRegisterEducations() != null
						&& employeeRegister.getEmployeeRegisterEducations().size() > 0) {
					for (EmployeeRegisterEducation employeeRegisterEducation : employeeRegister
							.getEmployeeRegisterEducations()) {
						employeeRegisterEducation.setEmployeeRegister(oldEmployeeRegister);
					}
					oldEmployeeRegister.getEmployeeRegisterEducations().addAll(employeeRegister.getEmployeeRegisterEducations());
				}
				Department department=departmentService.find(departmentId);
				Position position=positionService.find(positionId);
				oldEmployeeRegister.setPosition(position);
				oldEmployeeRegister.setDepartment(department);
				employeeRegisterService.update(oldEmployeeRegister);
				json.setSuccess(true);
				json.setMsg("添加成功");
			} else {
				json.setSuccess(false);
				json.setMsg("添加失败，内容为空");
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("添加失败");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	
	@RequestMapping("/list/view")
	@ResponseBody
	public Json list(Pageable pageable,EmployeeRegister employeeRegister,HttpSession session,HttpServletRequest request){
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
			Order order = Order.desc("createtime");
			orders.add(order);

			/** 数据按照创建人筛选 */
			List<Filter> filters = new ArrayList<Filter>();
			Filter filter = Filter.in("operator", hyAdmins);
			filters.add(filter);
			
			pageable.setFilters(filters);
			pageable.setOrders(orders);
			
			Page<EmployeeRegister> page=employeeRegisterService.findPage(pageable,employeeRegister);
			for (EmployeeRegister tmp : page.getRows()) {
				HyAdmin creator = tmp.getOperator();
				HashMap<String, Object> m = new HashMap<String, Object>();
				m.put("id", tmp.getId());
				m.put("createDate", tmp.getCreatetime());
				m.put("modifyDate", tmp.getModifytime());
				m.put("operator", tmp.getOperator());
				m.put("name", tmp.getName());
				m.put("mobile", tmp.getMobile());
				m.put("jobStatus", tmp.getJobStatus());
				m.put("registerStatus", tmp.getRegisterStatus());
				m.put("username", tmp.getUsername());
				m.put("isMale", tmp.getIsMale());
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
	@RequestMapping("/detail/view")
	@ResponseBody
	public Json detail(Long id){
		Json json=new Json();
		try {
			EmployeeRegister employeeRegister=employeeRegisterService.find(id);
			if(employeeRegister!=null){
				json.setSuccess(true);
				json.setMsg("获取成功");
				json.setObj(employeeRegister);
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
}
