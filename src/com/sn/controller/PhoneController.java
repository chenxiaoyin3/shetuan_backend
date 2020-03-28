package com.sn.controller;

import javax.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.hongyu.Json;
import com.sn.service.UserService;

@RestController
@RequestMapping("/sn_officalWebsite/phoneTopic/")
public class PhoneController {
	@Resource(name = "UserServiceImpl")
	UserService userService;

	@RequestMapping("send")
	@ResponseBody
	public Json send(String phone) {
		Json j=new Json();
		j = userService.send(phone);
		return j;
	}
	
	@RequestMapping("testoid")
	@ResponseBody
	public Json testoid(String openid) {
		Json j=new Json();
		j = userService.testoid(openid);
		return j;
	}
}
