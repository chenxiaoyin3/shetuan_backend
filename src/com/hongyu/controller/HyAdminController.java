package com.hongyu.controller;

import static com.hongyu.util.Constants.zonggongsi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyDepartmentModel;
import com.hongyu.entity.HyRole;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyRoleService;

@Controller
@RequestMapping("/admin/user/")
public class HyAdminController {
	@Resource(name="hyAdminServiceImpl")
	private HyAdminService hyAdminService;
	
	@Resource(name="hyRoleServiceImpl")
	private HyRoleService hyRoleService;
	
	@Resource(name="departmentServiceImpl")
	private DepartmentService departmentService;
	
	/**
	 * 获取可分配的角色
	 * @param session
	 * @return 可分配的角色数组
	 */
	@RequestMapping(value="addEntrance", method = RequestMethod.GET)
	@ResponseBody
	public Json addEntrance() {
		/**
		 * 根据用户找到角色
		 */
//		Principal principal=(Principal) session.getAttribute(CommonAttributes.Principal);
//		String username = principal.getUsername();
//		HyAdmin admin = hyAdminService.find("zhongjunlin");
//		HyRole r = admin.getRole();
		List<HyRole> roles = new ArrayList();
		Json j = new Json();
		/**
		 * 系统管理员能看到的页面
		 * 角色处于被取消状态不可以分配给新用户
		 */
		List<Filter> filters = new ArrayList();
		Filter filter = Filter.eq("status", true);
		filters.add(filter);
		roles = hyRoleService.findList(null, filters, null);

		j.setMsg("查询成功！");
		j.setSuccess(true);
		j.setObj(roles);
		return j;
	}
	
	/**
	 *  查询用户名是否可被注册
	 * @param name
	 * @return
	 */
	@RequestMapping(value = "checkName/view", method = RequestMethod.GET)
	@ResponseBody
	public Json checkName(String username){
		Json j = new Json();
		if (hyAdminService.find(username) != null){
			j.setMsg("用户名重复");
			j.setSuccess(false);
		}else {
			j.setMsg("用户名可用");
			j.setSuccess(true);
		}
		return j;
	}
	
	/**
	 * 新增用户
	 * @param admin
	 * @param roleId
	 * @return
	 */
	@RequestMapping(value="add", method = RequestMethod.POST)
	@ResponseBody
	public Json add(HyAdmin admin, Long roleId, Long departmentId) {
		Json j = new Json();
		try{
			Department hyDepartment = departmentService.find(departmentId);
			HyRole role = hyRoleService.find(roleId);
			admin.setRole(role);
			admin.setDepartment(hyDepartment);
			hyAdminService.save(admin);
			j.setSuccess(true);
			j.setMsg("添加成功！");
		}catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	/**
	 * 取消用户-状态变为禁用
	 * @param id
	 * @return
	 */
	@RequestMapping(value="cancel")
	@ResponseBody
	public Json cancel(String username) {
		Json j = new Json();
		try{
			HyAdmin admin = hyAdminService.find(username);
			admin.setIsEnabled(false);
			hyAdminService.update(admin);
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
	 * 恢复账号
	 * @param id
	 * @return
	 */
	@RequestMapping(value="restore")
	@ResponseBody
	public Json restore(String username) {
		Json j = new Json();
		try{
			HyAdmin admin = hyAdminService.find(username);
			admin.setIsEnabled(true);
			hyAdminService.update(admin);
			j.setSuccess(true);
			j.setMsg("恢复成功！");
		}catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	/**
	 * 用户详情页面
	 * @param username
	 * @return 用户信息
	 */
	@RequestMapping(value="detail/view", method = RequestMethod.GET)
	@ResponseBody
	public Json detail(String username) {
		Json j = new Json();
		HyAdmin admin = hyAdminService.find(username);
		j.setMsg("查看详情成功");
		j.setSuccess(true);
		j.setObj(admin);
		return j;
	}
	
	/**
	 * 获取用户列表
	 * 
	 * @param pageable:分页信息
	 * @param hyAdmin:用户信息     
	 * @param role:角色信息
	 * 
	 * @return 用户信息数组
	 */
	@RequestMapping(value="list/view", method = RequestMethod.POST)
	@ResponseBody
	public Json getList(Pageable pageable,HyAdmin hyAdmin, Long roleId) {
		Json j = new Json();
		try {
			if(roleId != null) {
				HyRole role = hyRoleService.find(roleId);
				hyAdmin.setRole(role);
			}
			Page<HyAdmin> page = hyAdminService.findPage(pageable, hyAdmin);
			j.setSuccess(true);
			j.setMsg("查找成功！");
			j.setObj(page);
		} catch(Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		
		return j;
	}
	
	/**
	 * 编辑用户信息(不能编辑用户名密码)
	 * @param 
	 * @param roleID 角色ID
	 * @param admin 用户信息
	 * @return
	 */
	@RequestMapping(value="edit",method = RequestMethod.POST)
	@ResponseBody
	public Json doEdit(HyAdmin admin, Long roleId) {
		Json j = new Json();
		try{
			HyRole role = hyRoleService.find(roleId);
			admin.setRole(role);
			hyAdminService.update(admin,
						"username","password",
						"department","isEnabled",
						"createDate","isOnjob", "contract",
						"areaQiche", "areaGuonei", "areaChujing",
						"isLocked","loginFailureCount",
						"lockedDate","loginDate",
						"loginIp","hyAdmin","hyAdmins","hySupplierContract");
			j.setSuccess(true);
			j.setMsg("编辑成功！");
		}catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	/**
	 * 重置用户密码为 12345
	 * @param username
	 * @return
	 */
	@RequestMapping(value="resetpw",method = RequestMethod.POST)
	@ResponseBody
	public Json resetpw(String username) {
		Json j = new Json();
		HyAdmin hyAdmin = hyAdminService.find(username);
		hyAdmin.setPassword("12345");
		hyAdminService.updatePassword(hyAdmin);
		j.setSuccess(true);
		j.setMsg("重置成功！");
		return j;
	}
	
	/**
	 * 获取部门树
	 * @return
	 */
	@RequestMapping(value="tree/view",method = RequestMethod.GET)
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
