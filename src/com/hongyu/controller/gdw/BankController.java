package com.hongyu.controller.gdw;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.util.BankUtil;
import com.hongyu.util.bankEntity.Response;

@Controller
@RequestMapping("/bank/")
public class BankController {

	@RequestMapping("testQuery")
	@ResponseBody
	public void bankTest(){
		Response response=BankUtil.testQuery();
		System.out.println(response);
	}
	@RequestMapping("testLocalPay")
	@ResponseBody
	public String testLocalPay(){
		String response=BankUtil.testLocalPay();
		System.out.println(response);
		return response;
	}
}
