package com.hongyu.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.grain.util.StringUtil;
import com.hongyu.CommonAttributes;
import com.hongyu.Json;
import com.hongyu.entity.HyAdmin;
import com.hongyu.service.HyAdminService;
/**
 * Controller - 个人中心
 * @author guoxinze
 *
 */
@RestController
@RequestMapping("/admin/personalCenter")
public class PersonalCenterController {
	
	
	@Resource(name = "hyAdminServiceImpl")
	private HyAdminService hyAdminService;
	
	/**
	 * 修改个人密码
	 * @param username
	 * @return
	 */
	@RequestMapping(value="/editpw",method = RequestMethod.POST)
	public Json resetpw(HttpSession session, String oldPassword, String password ) {
		Json j = new Json();
		try{
			/**
			 * 由session找到当前用户
			 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			/**
			 * 加密比对，若密码正确则修改个人信息
			 */
			String encryptpw = StringUtil.encodePassword(oldPassword, "MD5");
			
			if(admin.getPassword().equalsIgnoreCase(encryptpw)){
				admin.setPassword(password);
				hyAdminService.updatePassword(admin);
			} else {
				j.setSuccess(false);
				j.setMsg("密码错误!");
				return j;
			}
			j.setSuccess(true);
			j.setMsg("修改成功！");
		}catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	/**
	 * 编辑个人信息(不能编辑用户名密码)
	 * @param admin 用户信息
	 * @return
	 */
	@RequestMapping(value="edit",method = RequestMethod.POST)
	public Json doEdit(HyAdmin admin) {
		Json j = new Json();
		try{
			hyAdminService.update(admin,
						"username","password","role",
						"department","isEnabled","position",
						"createDate","isOnjob","isManager",
						"isLocked","loginFailureCount",
						"lockedDate","loginDate",
						"loginIp","hyAdmin","hyAdmins", 
						"areaQiche", "areaGuonei", "areaChujing",
						"liableContracts");
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
	 * 获取个人信息
	 * @param session 会话信息
	 * @return
	 */
	@RequestMapping(value="getInfo/view",method = RequestMethod.GET)
	public Json getInfo(HttpSession session) {
		Json j = new Json();
		/**
		 * 由session找到当前用户
		 */
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin admin = hyAdminService.find(username);
		
		j.setMsg("查看详情成功");
		j.setSuccess(true);
		j.setObj(admin);
		return j;
	}
}
