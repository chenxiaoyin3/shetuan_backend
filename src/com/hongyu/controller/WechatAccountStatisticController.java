package com.hongyu.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.entity.WechatAccount;
import com.hongyu.service.WechatAccountService;
import com.hongyu.util.DateUtil;

@Controller
@RequestMapping("/admin/business/userStatistic")
public class WechatAccountStatisticController {
	@Resource(name = "wechatAccountServiceImpl")
	WechatAccountService wechatAccountService;

	@RequestMapping("/statistics")
	@ResponseBody
	public Json statistics(@DateTimeFormat(iso = ISO.DATE_TIME) Date start,
			@DateTimeFormat(iso = ISO.DATE_TIME) Date end) {
		Json json = new Json();
		try {
			HashMap<String, Object> hm = new HashMap<>();
			List<Filter> filters = new ArrayList<>();
			if (start != null) {
				start = DateUtil.getStartOfDay(start);
				filters.add(Filter.ge("createTime", start));
			}
			if (end != null) {
				end = DateUtil.getEndOfDay(end);
				filters.add(Filter.le("createTime", end));
			}
			int accountNum = 0;
			int phoneBinded = 0;
			int phoneUnbinded = 0;
			int isVip = 0;
			int isNotVip = 0;
			int isActive = 0;
			int isNotActive = 0;
			List<WechatAccount> wechatAccounts = wechatAccountService.findList(null, filters, null);
			accountNum = wechatAccounts.size();
			for (WechatAccount wechatAccount : wechatAccounts) {
				if (wechatAccount.getPhone() != null && !wechatAccount.getPhone().equals("")) {
					phoneBinded++;
				} else {
					phoneUnbinded++;
				}
				if (wechatAccount.getIsVip() != null &&wechatAccount.getIsVip()==true) {
					isVip++;
				} else {
					isNotVip++;
				}
				if (wechatAccount.getIsActive()==true||wechatAccount.getIsActive()==null) {
					isActive++;
				} else {
					isNotActive++;
				}
			}
			hm.put("accountNum", accountNum);// 账户总数
			hm.put("phoneBinded", phoneBinded);// 绑定手机账户数
			hm.put("phoneUnbinded", phoneUnbinded);// 未绑定账户数
			hm.put("isVip", isVip);// 会员数
			hm.put("isNotVip", isNotVip);// 非会员人数
			hm.put("isActive", isActive);// 有效
			hm.put("isNotActive", isNotActive);// 无效
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(hm);
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("获取失败");
			e.printStackTrace();
		}
		return json;
	}
}
