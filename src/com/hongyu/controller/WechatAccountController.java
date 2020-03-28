package com.hongyu.controller;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.VerificationCode;
import com.hongyu.entity.WechatAccount;
import com.hongyu.service.VerificationCodeService;
import com.hongyu.service.WechatAccountService;
import com.hongyu.util.SendMessage;

@Controller
@RequestMapping("/admin/business/usermanagement")
public class WechatAccountController {
	
	@Resource(name="wechatAccountServiceImpl")
	WechatAccountService wechatAccountServiceImpl;
	
	@RequestMapping(value = "/page/view")
	@ResponseBody
	public Json wechatAccountList(Pageable pageable, WechatAccount account)
	{
	    Json json = new Json();
	    try {
	    	List<Order> orders = new ArrayList<Order>();
			orders.add(Order.desc("id"));

			pageable.setOrders(orders);
			Page<WechatAccount> page = wechatAccountServiceImpl.findPage(pageable, account);
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(page);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			json.setMsg("查询失败");
			json.setSuccess(false);
			e.printStackTrace();
			
		}
	    return json;
	}
	
	@RequestMapping(value = "/active", method = RequestMethod.POST)
	@ResponseBody
	public Json wechatAccountActive(Long accountid)
	{
	    Json json = new Json();
	    WechatAccount account = wechatAccountServiceImpl.find(accountid);
	    if (account == null) {
	    	json.setSuccess(false);
		    json.setMsg("账户不存在");
		    return json;
	    } else {
	    	try {
				account.setIsActive(true);
				wechatAccountServiceImpl.update(account);
				json.setSuccess(true);
				json.setMsg("设置成功");
				return json;
			} catch (Exception e) {
				json.setSuccess(false);
				json.setMsg("设置失败");
				return json;
			}
	    }
	}
	
	@RequestMapping(value = "/inactive", method = RequestMethod.POST)
	@ResponseBody
	public Json wechatAccountInActive(Long accountid)
	{
	    Json json = new Json();
	    WechatAccount account = wechatAccountServiceImpl.find(accountid);
	    if (account == null) {
	    	json.setSuccess(false);
		    json.setMsg("账户不存在");
		    return json;
	    } else {
	    	try {
				account.setIsActive(false);
				wechatAccountServiceImpl.update(account);
				json.setSuccess(true);
				json.setMsg("设置成功");
				return json;
			} catch (Exception e) {
				json.setSuccess(false);
				json.setMsg("设置失败");
				return json;
			}
	    }
	}


	@RequestMapping(value = "/edit_balance",method = RequestMethod.POST)
	@ResponseBody
	public Json editBalance(Long id,BigDecimal balance){
		Json json = new Json();
		try{
			WechatAccount wechatAccount = wechatAccountServiceImpl.find(id);
			wechatAccount.setTotalbalance(balance);
			wechatAccountServiceImpl.update(wechatAccount);

			json.setObj(wechatAccount);
			json.setMsg("修改成功");
			json.setSuccess(true);
		}catch (Exception e){
			json.setSuccess(false);
			json.setMsg("修改失败");
			json.setObj(e);
		}
		return json;
	}
}
