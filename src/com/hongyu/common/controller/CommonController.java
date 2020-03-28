package com.hongyu.common.controller;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.hongyu.CommonAttributes;
import com.hongyu.Json;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyRole;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyRoleService;

@RestController
@RequestMapping("/common/privilege")
public class CommonController {
	
	@Resource(name = "hyAdminServiceImpl")
	private HyAdminService hyAdminService;
	
	@Resource(name = "hyRoleServiceImpl")
	private HyRoleService hyRoleService;
	
	/**
	 * 返回当前登录用户可以分配的子角色
	 * @param session
	 * @return
	 */
	@RequestMapping(value="/getSubroles", method = RequestMethod.GET)
	public Json getSubRoles(HttpSession session) {
		Json j = new Json();
		
		try {
			/**
			 * 获取当前用户角色
			 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyRole role = hyAdminService.find(username).getRole();
			
			/** 获取子角色 */
			Set<HyRole> subRoles = role.getHyRolesForSubroles();
			if(subRoles.size() > 0) {
				Iterator<HyRole> iterator = subRoles.iterator();
				while(iterator.hasNext()){
					HyRole subRole = iterator.next();
					if(!subRole.getStatus())
						iterator.remove();
				}
			}
			j.setSuccess(true);
			j.setMsg("查询成功");
			j.setObj(subRoles);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	/**
	 * 登录检验
	 */
	@RequestMapping(value="/logincheck")
	public Json loginCheck(HttpSession session) {
		Json j = new Json();
		try {
			if(session.getAttribute(CommonAttributes.Principal) == null) {
				j.setMsg("需要登录");
				j.setSuccess(false);
				return j;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		j.setSuccess(true);
		j.setMsg("已登录");
		return j;
	}
	@RequestMapping("/checkUsername")
	@ResponseBody
	public Json checkUsername(String username){
		Json json=new Json();
		try {
			HyAdmin hyAdmin=hyAdminService.find(username);
			if(hyAdmin!=null){
				json.setSuccess(false);
				json.setMsg("账户已存在");
			}else{
				json.setSuccess(true);
				json.setMsg("允许创建账户");
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查找账户错误，请重试： "+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
//	/**
//	 * 权限检验
//	 * @return
//	 */
//	@RequestMapping(value="/authoritycheck")
//	public Json authorityCheck() {
//		Json j = new Json();
//		j.setSuccess(false);
//		j.setMsg("没有权限");
//		return j;
//	}
}
