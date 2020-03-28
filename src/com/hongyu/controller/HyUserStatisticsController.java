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
import com.hongyu.entity.HyUser;
import com.hongyu.service.HyUserService;
import com.hongyu.util.DateUtil;

/*
 * @author LBC
 * 管理门户用户
 */



@Controller
@RequestMapping("/admin/mh_user_statistics/")
public class HyUserStatisticsController {
	@Resource(name="hyUserServiceImpl")
	private HyUserService hyUserService;
	
	@RequestMapping("statistics")
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
			int isValid = 0;
			int isNotValid = 0;
			List<HyUser> hyUsers = hyUserService.findList(null, filters, null);
			accountNum = hyUsers.size();
			for (HyUser hyUser : hyUsers) {
				
				if (hyUser.getIsValid()==true||hyUser.getIsValid()==null) {
					isValid++;
				} else {
					isNotValid++;
				}
			}
			hm.put("accountNum", accountNum);// 账户总数
			hm.put("isValid", isValid);// 有效
			hm.put("isNotValid", isNotValid);// 无效
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
