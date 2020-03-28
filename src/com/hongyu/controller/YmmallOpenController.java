package com.hongyu.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.entity.WeBusiness;
import com.hongyu.entity.WechatAccount;
import com.hongyu.service.WeBusinessService;
import com.hongyu.service.WechatAccountService;

@Controller
@RequestMapping("/ymmall/api/")
public class YmmallOpenController {

	@Resource(name = "weBusinessServiceImpl")
	WeBusinessService weBusinessService;

	@Resource(name = "wechatAccountServiceImpl")
	WechatAccountService wechatAccountService;

	@RequestMapping("postOpenId")
	@ResponseBody
	public Json postOpenId(Long uid, String openId, String wechatName) {
		Json json = new Json();
		try {
			System.out.println("uid:" + uid);
			System.out.println("openId:" + openId);
			System.out.println("wechatName:" + wechatName);
			WeBusiness weBusiness = weBusinessService.find(uid);
			if (weBusiness == null) {
				json.setSuccess(false);
				json.setMsg("uid为 " + uid + " 的微商不存在");
			} else {
				if(weBusiness.getWechatOpenId()!=null) {
					json.setSuccess(false);
					json.setMsg("该微商已绑定其他OpenId");
					return json;
				}
				/** 如果openId已存在，取消绑定 */
				List<Filter> filters = new ArrayList<>();
				filters.add(Filter.eq("wechatOpenId", openId));
				List<WeBusiness> weBusinesses = weBusinessService.findList(null, filters, null);
				if (weBusinesses.size() > 0) {
					json.setSuccess(false);
					json.setMsg("该微信已绑定其余微商");
					json.setObj(weBusinesses.get(0).getUrl());
				} else {
					/** 仅当微商存在且openId为空的时候，设置openId并且生成微信账户 */
					if (weBusiness != null && weBusiness.getWechatOpenId() == null) {
						weBusiness.setWechatOpenId(openId);
						weBusinessService.update(weBusiness);
						List<Filter> filters1 = new ArrayList<>();
						filters1.add(Filter.eq("wechatOpenid", openId));
						List<WechatAccount> lists = wechatAccountService.findList(null, filters1, null);
						if (lists.size() > 0) {
							WechatAccount wechatAccount = lists.get(0);
							if(!wechatAccount.getIsWeBusiness()) {
								//更新微信用户是否是微商字段
								wechatAccount.setIsWeBusiness(true);
								wechatAccountService.update(wechatAccount);
							}
							
							json.setSuccess(true);
							json.setMsg("绑定成功，账户已存在");
						} else {
							WechatAccount wechatAccount = new WechatAccount();
							wechatAccount.setWechatName(wechatName);
							wechatAccount.setWechatOpenid(openId);
							wechatAccount.setIsWeBusiness(true);
							wechatAccountService.save(wechatAccount);
							json.setSuccess(true);
							json.setMsg("绑定成功，创建账户成功");
						}
					}
				}
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("绑定错误");
			e.printStackTrace();
		}
		return json;

	}

	@RequestMapping("createAccount")
	@ResponseBody
	public Json createAccount(String wechatName, String openId) {
		Json json = new Json();
		try {
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("wechatOpenid", openId));
			List<WechatAccount> lists = wechatAccountService.findList(null, filters, null);
			if (lists.size() > 0) {
				json.setSuccess(false);
				json.setMsg("账户已存在");
			} else {
				WechatAccount wechatAccount = new WechatAccount();
				wechatAccount.setWechatName(wechatName);
				wechatAccount.setWechatOpenid(openId);
				wechatAccount.setIsWeBusiness(false);
				wechatAccountService.save(wechatAccount);
				json.setSuccess(true);
				json.setMsg("账户创建成功");
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("创建账户错误");
			e.printStackTrace();
		}
		return json;
	}
}
