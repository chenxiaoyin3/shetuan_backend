package com.grain.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("fonterCommonController")
@RequestMapping("/fonter")
public class CommonController extends BaseController{

	@RequestMapping(value="index")
	public String getIndex(){
		return "/fonter/index";
	}
}
