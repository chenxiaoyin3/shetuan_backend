package com.hongyu.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.Guide;
import com.hongyu.entity.GuideAssignment;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyGroup;
import com.hongyu.service.GuideAssignmentService;
import com.hongyu.service.GuideService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyGroupService;
import com.hongyu.util.DateUtil;

@Controller
@RequestMapping("/wechat/guideArrangement/")
public class WechatGuideArrangementController {
	
	@Resource(name="guideServiceImpl")
	GuideService guideService;
	
	@Resource(name = "guideAssignmentServiceImpl")
	GuideAssignmentService guideAssignmentService;
	
	@Resource(name="hyGroupServiceImpl")
	HyGroupService hyGroupService;
	
	@Resource(name = "hyAdminServiceImpl")
	private HyAdminService hyAdminService;
	
	@RequestMapping("list/view")
	@ResponseBody
	public Json guideArrangement(Pageable pageable,Long guideId){
		Json json=new Json();
		try {
			Date  today=DateUtil.getStartOfDay(new Date());
			List<Filter> filters=new LinkedList<>();
			filters.add(Filter.eq("guideId", guideId));
			filters.add(Filter.ge("endDate", today));
			filters.add(Filter.eq("status", 1));
			pageable.setFilters(filters);
			
			List<Order> orders = new ArrayList<>();
			orders.add(Order.desc("id"));
			pageable.setOrders(orders);
			
			Page<GuideAssignment> page=guideAssignmentService.findPage(pageable);
			Guide guide=guideService.find(guideId);
			List<Map<String, Object>>result=new LinkedList<>();
			for(GuideAssignment tmp:page.getRows()){
				Map<String, Object> map=new HashMap<>();
				map.put("guideName", guide.getName());
				map.put("guideSn", guide.getGuideSn());
				map.put("lineName", tmp.getLineName());
				map.put("startDate", tmp.getStartDate());
				map.put("endDate", tmp.getEndDate());
				HyAdmin hyAdmin = hyAdminService.find(tmp.getOperator());
				map.put("operator", hyAdmin==null?"":hyAdmin.getName());
				map.put("operatorPhone", tmp.getOperatorPhone());
				if(tmp.getAssignmentType()==1||tmp.getGroupId()==null){
					map.put("signupNumber", "人数不详");
				}else{
					HyGroup hyGroup=hyGroupService.find(tmp.getGroupId());
					if(hyGroup==null){
						map.put("signupNumber", "人数不详");
					}else{
						map.put("signupNumber", hyGroup.getSignupNumber()+"");
					}
				}
				result.add(map);
			}
			Map<String, Object> hashMap=new HashMap<>();
			hashMap.put("pageNumber", page.getPageNumber());
			hashMap.put("pageSize", page.getPageSize());
			hashMap.put("total", page.getTotal());
			hashMap.put("rows", result);
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(hashMap);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取错误： "+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}

}
