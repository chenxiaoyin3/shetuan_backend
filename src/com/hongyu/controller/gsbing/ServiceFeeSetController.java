package com.hongyu.controller.gsbing;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.hongyu.Json;

@RestController
@RequestMapping("admin/serviceFeeSet/")
public class ServiceFeeSetController {
	
	//服务费天数列表
	@RequestMapping("list/view")
	@ResponseBody
	public Json listview(Integer lineType,Boolean teamType)
	{
		Json json=new Json();
		try {
			
			json.setSuccess(true);
		}
		catch(Exception e) {
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
}
