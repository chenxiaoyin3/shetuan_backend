package com.hongyu.controller;

import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.hongyu.Json;

@RestController
@RequestMapping("/admin/contractinfo/")
public class HyContractManageController {

	@RequestMapping(value="detail/view")
	public Json detail(HttpSession session) {
		Json j = new Json();
		
		try {
			
			} catch(Exception e) {
				// TODO Auto-generated catch block
				j.setSuccess(false);
				j.setMsg(e.getMessage());
			}
		return j;
	}
}
