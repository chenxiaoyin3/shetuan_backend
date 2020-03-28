package com.grain.controller.admin;

import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.grain.entity.user.OpRegister;
import com.grain.entity.user.Role;
import com.grain.service.user.RoleService;
import com.hongyu.CommonAttributes;
import com.hongyu.EasyUItreeNode;
import com.hongyu.Json;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.Principal;

/**
 * Controller - 角色
 * 
 */
@Controller
@RequestMapping("/admin/role")
public class RoleController extends BaseController {

	@Resource(name = "roleServiceImpl")
	private RoleService roleService;

	/**
	 * 跳转到角色管理页面
	 * 
	 * @return
	 */
	@RequestMapping("/manager")
	public String manager() {
		return "/admin/role";
	}


	/**
	 * 添加角色
	 * 
	 * @return
	 */
	@RequestMapping("/add")
	@ResponseBody
	public Json add(Role role, HttpSession session) {
		Json j = new Json();
		try{
			roleService.save(role);
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
	 * 修改角色
	 * 
	 * @param role
	 * @return
	 */
	@RequestMapping("/edit")
	@ResponseBody
	public Json edit(Role role) {
		Json j = new Json();
		try {
			roleService.update(role);
			j.setSuccess(true);
			j.setMsg("编辑成功！");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}

		return j;
	}

	/**
	 * 获得角色列表
	 * 
	 * @return
	 */
	@RequestMapping("/dataGrid")
	@ResponseBody
	public Page<Role> getDataGrid(Pageable pageable,Role queryParm) {
		Page<Role> page=roleService.findPage(pageable);
		return page;
	}

	/**
	 * 角色树(只能看到自己拥有的角色)
	 * 
	 * @return
	 */
	@RequestMapping("/tree")
	@ResponseBody
	public List<EasyUItreeNode> tree(HttpSession session,HttpServletRequest req) {
		Principal p = (Principal) session.getAttribute(CommonAttributes.Principal);
		String id = req.getParameter("id");
		return roleService.tree(p,id);
	}

	/**
	 * 角色树
	 * 
	 * @return
	 */
	@RequestMapping("/allTree")
	@ResponseBody
	public List<EasyUItreeNode> allTree(HttpServletRequest req) {
		String id = req.getParameter("id");
		return roleService.tree(null,id);
	}

	/**
	 * 删除角色
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public Json delete(Long id) {
		Json j = new Json();
		roleService.delete(id);
		j.setMsg("删除成功！");
		j.setSuccess(true);
		return j;
	}

	/**
	 * 跳转到角色授权页面
	 * 
	 * @return
	 */
	@RequestMapping("/grantPage")
	public String grantPage(HttpServletRequest request, Long id) {
		Role r = roleService.find(id);
		Set<OpRegister> l = r.getOps();
		String ids="";
		for (OpRegister k:l){
			ids +=k.getId() + ",";
		}
		if (ids.length()>0)
		ids=ids.substring(0,ids.length()-1);
		r.setResourceIds(ids);
		request.setAttribute("role", r);
		return "/admin/roleGrant";
	}

	/**
	 * 授权
	 * 
	 * @param role
	 * @return
	 */
	@RequestMapping(value="/grant" , method=RequestMethod.POST)
	@ResponseBody
	public Json grant(Role role,@RequestParam("resourceIds") Long... ids ) {
		Json j = new Json();
		roleService.grant(role,ids);
		j.setMsg("授权成功！");
		j.setSuccess(true);
		return j;
	}

}
