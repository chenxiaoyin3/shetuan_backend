package com.sn.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.hongyu.Json;
import com.hongyu.Pageable;
import com.sn.entity.User;
import com.sn.service.UserService;

@RestController
@RequestMapping("/admin/userManagement/")
public class UserManagementController {
	@Resource(name = "UserServiceImpl")
	private UserService userService;

	@RequestMapping("list")
	@ResponseBody
	public Json add(Pageable pageable,String username) {
		Json j = new Json();
		j = userService.list(pageable, username);
		return j;
	}
}
