package com.hongyu.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.Pointrecord;
import com.hongyu.entity.Vip;
import com.hongyu.entity.Viplevel;
import com.hongyu.entity.WechatAccount;
import com.hongyu.service.PointrecordService;
import com.hongyu.service.VipService;
import com.hongyu.service.ViplevelService;
import com.hongyu.service.WechatAccountService;

@Controller
@RequestMapping("/ymmall/vip")
public class YmmallVIPController {
	
	@Resource(name = "wechatAccountServiceImpl")
	WechatAccountService wechatAccountService;
	
	@Resource(name = "vipServiceImpl")
	VipService vipService;
	
	@Resource(name = "viplevelServiceImpl")
	ViplevelService viplevelService;
	
	@Resource(name = "pointrecordServiceImpl")
	PointrecordService pointrecordService;
	
	/**
	 * 获取用户会员等级
	 * @param id
	 * @return
	 */
	@RequestMapping("/detail/view")
	@ResponseBody
	public Json detail(Long id) {
		Json json = new Json();
		try {
			Viplevel viplevel = vipService.getViplevelByWechatAccountId(id);
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(viplevel);
			
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("获取失败");
			json.setObj(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping("/pointrecord/page/view")
	@ResponseBody
	public Json pointrecordPage(Pageable pageable,Long id) {
		Json json = new Json();
		try {
			WechatAccount wechatAccount = wechatAccountService.find(id);
			if(wechatAccount == null) {
				throw new Exception("不存在该用户");
			}
			List<Order> orders = new ArrayList<>();
			orders.add(Order.desc("createTime"));
			pageable.setOrders(orders);
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("wechatAccount", wechatAccount));
			pageable.setFilters(filters);
			Page<Pointrecord> pages = pointrecordService.findPage(pageable);
			
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(pages);
			
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("获取失败");
			json.setObj(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping("/point/change")
	@ResponseBody
	public Json pointChange(Long id,Integer changevalue) {
		Json json = new Json();
		try {
			if(changevalue==0) {
				throw new Exception("积分兑换不能是0");
			}
			if(changevalue%10!=0) {
				throw new Exception("积分兑换值必须为10的倍数");
			}
			WechatAccount wechatAccount = wechatAccountService.find(id);
			if(wechatAccount == null) {
				throw new Exception("不存在该用户");
			}	
			if(changevalue>0) {
				changevalue=-changevalue;
			}
			pointrecordService.changeUserPoint(id, changevalue, "兑换余额电子券");
			
			json.setSuccess(true);
			json.setMsg("兑换成功");
			
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("兑换失败");
			json.setObj(e.getMessage());
		}
		return json;
	}
	
	

}
