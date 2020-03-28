package com.hongyu.controller;

import java.math.BigDecimal;
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
import com.hongyu.Pageable;
import com.hongyu.entity.BusinessOrderItem;
import com.hongyu.entity.OrderItemDivide;
import com.hongyu.entity.WeBusiness;
import com.hongyu.service.OrderItemDivideService;
import com.hongyu.service.WeBusinessService;
import com.hongyu.util.DateUtil;

@Controller
@RequestMapping("/admin/business/weDivideForm/")
public class WeDivideFormController {
	@Resource(name = "orderItemDivideServiceImpl")
	OrderItemDivideService orderItemDivideService;

	@Resource(name = "weBusinessServiceImpl")
	WeBusinessService weBusinessService;

	@RequestMapping("list/view")
	@ResponseBody
	public Json list(Pageable pageable, String weBusinessName, @DateTimeFormat(iso = ISO.DATE_TIME) Date begin,
			@DateTimeFormat(iso = ISO.DATE_TIME) Date stop) {
		Json json = new Json();
		try {
			if(begin==null){
				begin=new Date();
			}
			if(stop==null){
				stop=new Date();
			}
			Date beginstart=DateUtil.getStartOfDay(begin);//起始时间的00：00
			Date stopstart=DateUtil.getStartOfDay(stop);//结束时间的00：00
			List<Filter> filters = new ArrayList<>();
			HashMap<String, Object> hm = new HashMap<String, Object>();
			List<HashMap<String, Object>> result = new ArrayList<>();
			if (weBusinessName != null && !"".equals(weBusinessName)) {
				filters.add(Filter.like("name", weBusinessName));
			}
			List<WeBusiness> weBusinesses = weBusinessService.findList(null, filters, null);				
			for (WeBusiness tmp : weBusinesses) {
				for (Date currentDate = beginstart; currentDate.before(DateUtil.getNextDay(stopstart)); currentDate = DateUtil.getNextDay(currentDate)) {
					List<Filter> filters2=new ArrayList<>();
					filters2.add(Filter.eq("weBusiness", tmp));
					filters2.add(Filter.ge("acceptTime", currentDate));
					filters2.add(Filter.le("acceptTime", DateUtil.getEndOfDay(currentDate)));
					List<OrderItemDivide> lists=orderItemDivideService.findList(null, filters2, null);
					if(lists.size()<=0)continue;
					BigDecimal total=new BigDecimal("0.00");
					for(OrderItemDivide orderItem:lists){
						total=total.add(orderItem.getWeBusinessAmount());
					}
					HashMap<String, Object> m = new HashMap<String, Object>();
					m.put("weBusinessName", tmp.getName());
					m.put("date", currentDate);
					m.put("children", lists);
					result.add(m);
					}
				}
			int pageNumber=pageable.getPage();
			int pageSize=pageable.getRows();
			hm.put("total", result.size());
			hm.put("pageNumber",pageNumber);
			hm.put("pageSize",pageSize);
			hm.put("rows", result.subList((pageNumber-1)*pageSize,pageNumber*pageSize>result.size()?result.size():pageNumber*pageSize));
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
