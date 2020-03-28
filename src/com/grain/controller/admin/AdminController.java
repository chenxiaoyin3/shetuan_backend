package com.grain.controller.admin;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.grain.entity.user.Admin;
import com.grain.entity.user.Role;
import com.grain.service.user.AdminService;
import com.grain.service.user.RoleService;
import com.hongyu.Page;
import com.hongyu.Pageable;


@Controller
@RequestMapping("/admin")
public class AdminController extends BaseController {
/*	private transient final Log log = LogFactory
			.getLog(SysUserController.class);*/
	@Resource(name="adminServiceImpl")
	private AdminService admin;
	@Resource(name="roleServiceImpl")
	private RoleService role;

	/**
	 * 进入用户列表页面，将角色信息添加到model中，供下拉框选择
	 * 
	 * @param model
	 * @return 页面名称
	 */
	@RequestMapping("/listentrance")
	public String ListEntrance(Model model) {
		model.addAttribute("roles", role.findAll());
		return "admin/list";
	}

	/**
	 * 获取用户列表
	 * 
	 * @param datagrid
	 *            插件发送的分页信息的封装
	 * @param name queryParm 
	 *            查询条件的封转
	 *            
	 * @return 由@ResponseBody转化的json
	 */
	@RequestMapping("/list")
	@ResponseBody
	public Page<Admin> getList(Pageable pageable,Admin queryParm) {
		if (queryParm!=null &&queryParm.getRole()!=null && 
				queryParm.getRole().getId()<0)	//没有选中角色
			queryParm.setRole(null);
		Page<Admin> map=admin.findPage(pageable,queryParm);
		return map;
	}
	/**
	 * 删除
	 * @param id
	 * @return
	 */
	@RequestMapping("/remove")
	@ResponseBody
	public String delete(@RequestParam(value="username") String username){
		
		Admin su = admin.findByUsername(username);
		admin.delete(su);
		return "success";
		
	}
	/**
	 * 进入新增用户界面
	 * @param model
	 * @return
	 */
	@RequestMapping("/addEntrance")
	public String addEntrance(Model model){
		model.addAttribute("roles", role.findAll());
		return "admin/add";
	}
	/**
	 *  查询用户名是否可被注册
	 * @param name
	 * @return
	 */
	@RequestMapping("/checkName")
	@ResponseBody
	public String checkName(String username){
		if (admin.existUserName(username))
			return "error";
		else
			return "ok";
	}
	/**
	 * 
	 * @param newsu
	 * @param roleID
	 * @return
	 */
	@RequestMapping("/add")
	@ResponseBody
	public 	String add(Admin newsu,@RequestParam(value="roleID") Long roleID){
		//取得对应角色
		Role r = role.find(roleID);
		newsu.setRole(r);
		admin.save(newsu);
		return "ok";
		
	}
	
	@RequestMapping(value="/edit", method = RequestMethod.GET)
	public String Entrance(@RequestParam("username") String username,Model model) {
		model.addAttribute("user",admin.find(username));
		model.addAttribute("roles", role.findAll());
		return "admin/edit";
	}

	@RequestMapping(value="/edit",method = RequestMethod.POST)
	@ResponseBody
	public String doEdit( Admin su,
			/*@ModelAttribute("roles") List<Role> roles,*/
			@RequestParam(required = false, value = "roleID") Long roleID) {
		// roleID为空，则是修改密码
		if (roleID == null) {
			Admin r = admin.find(su.getUsername());
			r.setPassword(su.getPassword());
			admin.updatePassword(r);
			// roleID不为空则代表修改用户角色
		} else {
			Role r = null;
			// 取得对应用户角色
			List<Role> roles = role.findAll();
			for (Role temp : roles) {
				if (temp.getId().equals(roleID)) {
					r = temp;
					break;
				}

			}
			su.setRole(r);
			admin.update(su);
		}
		return "success";
	}
	
}
