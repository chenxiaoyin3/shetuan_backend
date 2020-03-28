package com.sn.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.hongyu.Json;
import com.sn.entity.User;
import com.sn.service.UserService;

@RestController
@RequestMapping("/sn_officalWebsite/userTopic/")
public class UserController {
	
	@Resource(name = "UserServiceImpl")
	private UserService userService;

	@RequestMapping("add")
	@ResponseBody
	public Json add(@RequestBody User user) {
		Json j = new Json();
		j = userService.add(user);
		return j;
	}
}
