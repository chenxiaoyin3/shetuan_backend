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
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.SpecialtyAppraise;
import com.hongyu.entity.WechatAccount;
import com.hongyu.service.SpecialtyAppraiseService;
import com.hongyu.service.WechatAccountService;
import com.hongyu.util.DateUtil;

@Controller
@RequestMapping("/admin/business/specialtyAppraiseManagement/")
public class SpecialtyAppraiseManagementController {
	@Resource(name = "specialtyAppraiseServiceImpl")
	SpecialtyAppraiseService specialtyAppraiseService;

	@Resource(name = "wechatAccountServiceImpl")
	WechatAccountService wechatAccountService;

	@RequestMapping("list/view")
	@ResponseBody
	public Json list(Pageable pageable,@DateTimeFormat(iso=ISO.DATE_TIME)Date start,@DateTimeFormat(iso=ISO.DATE_TIME)Date end,String wechatName,String appraiseContent){
		Json json=new Json();
		try {
			List<Filter> filters=new ArrayList<>();
			if(start!=null){
			start=DateUtil.getStartOfDay(start);
			filters.add(Filter.ge("appraiseTime", start));
			}
			if(end!=null){
			end=DateUtil.getEndOfDay(end);
			filters.add(Filter.le("appraiseTime", end));
			}
			if(wechatName!=null){
				List<Filter> filters2=new ArrayList<>();
				filters2.add(Filter.like("wechatName", wechatName));
				List<WechatAccount> wechatAccounts=wechatAccountService.findList(null,filters2,null);
				filters.add(Filter.in("account", wechatAccounts));
			}
			List<Order> orders = new ArrayList<Order>();
			orders.add(Order.desc("id"));
			pageable.setOrders(orders);
			Page<SpecialtyAppraise> page=specialtyAppraiseService.findPage(pageable);
			HashMap<String, Object> hashMap=new HashMap<>();
			hashMap.put("total", page.getTotal());
			hashMap.put("pageNumber", page.getPageNumber());
			hashMap.put("pageSize", page.getPageSize());
			List<HashMap<String, Object>> result=new ArrayList<>();
			for(SpecialtyAppraise specialtyAppraise:page.getRows()){
				HashMap<String, Object> hm=new HashMap<>();
				hm.put("id", specialtyAppraise.getId());
				hm.put("wechatName",specialtyAppraise.getAccount().getWechatName());
				hm.put("appraiseTime", specialtyAppraise.getAppraiseTime());
				hm.put("appraiseContent", specialtyAppraise.getAppraiseContent());
				hm.put("contentLevel", specialtyAppraise.getContentLevel());
				hm.put("isAnonymous", specialtyAppraise.getIsAnonymous());
				hm.put("isShow", specialtyAppraise.getIsShow());
				hm.put("isValid", specialtyAppraise.getIsValid());
				hm.put("specialtyName", specialtyAppraise.getSpecialty().getName());
				hm.put("specification", specialtyAppraise.getSpecification().getSpecification());
				hm.put("orderCode", specialtyAppraise.getBusinessOrder().getOrderCode());
				result.add(hm);
			}
			hashMap.put("rows", result);
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(hashMap);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取失败");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	@RequestMapping("changeStatus")
	@ResponseBody
	public Json changeStatus(Long id){
		Json json=new Json();
		try {
			SpecialtyAppraise specialtyAppraise=specialtyAppraiseService.find(id);
			specialtyAppraise.setIsValid((specialtyAppraise.getIsValid()?false:true));
			specialtyAppraiseService.update(specialtyAppraise);
			json.setSuccess(true);
			json.setMsg("修改成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("修改失败");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	@RequestMapping("detail/view")
	@ResponseBody
	public Json detail(Long id){
		Json json=new Json();
		try {
			SpecialtyAppraise specialtyAppraise=specialtyAppraiseService.find(id);
			if(specialtyAppraise!=null){
				json.setSuccess(true);
				json.setMsg("获取成功");
				json.setObj(specialtyAppraise);
			}else{
				json.setSuccess(false);
				json.setMsg("评价不存在");
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取失败");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	
	@RequestMapping("/delete")
	@ResponseBody
	public Json delete(Long id){
		Json json=new Json();
		try {
			SpecialtyAppraise specialtyAppraise=specialtyAppraiseService.find(id);
			if(specialtyAppraise==null){
				json.setSuccess(false);
				json.setMsg("评价不存在");
			}
			else{
			specialtyAppraise.setIsValid(false);
			specialtyAppraiseService.update(specialtyAppraise);
			json.setSuccess(true);
			json.setMsg("删除成功");
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("删除失败");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}

}
