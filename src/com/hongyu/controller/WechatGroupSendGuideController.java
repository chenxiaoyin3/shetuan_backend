package com.hongyu.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.entity.GroupSendGuide;
import com.hongyu.entity.Guide;
import com.hongyu.entity.GuideAssignment;
import com.hongyu.entity.HyGroup;
import com.hongyu.service.GroupSendGuideService;
import com.hongyu.service.GuideAssignmentService;
import com.hongyu.service.GuideService;
import com.hongyu.service.HyGroupService;

@Controller
@RequestMapping("/wechat/sendGuide/")
public class WechatGroupSendGuideController {
	@Resource(name="groupSendGuideServiceImp")
	GroupSendGuideService groupSendGuideService;
	
	@Resource(name="guideServiceImpl")
	GuideService guideService;
	
	@Resource(name = "hyGroupServiceImpl")
	HyGroupService hyGroupService;
	
	@Resource(name = "guideAssignmentServiceImpl")
	GuideAssignmentService guideAssignmentService;
	
	@RequestMapping("detail/view")
	@ResponseBody
	public Json detail(Long id, Long guideId){
		Json json=new Json();
		try {
			GroupSendGuide groupSendGuide=groupSendGuideService.find(id);
			Guide guide = guideService.find(guideId);
			if(groupSendGuide!=null && guide != null){
				Map<String, Object>map=new HashMap<>();
				map.put("id", groupSendGuide.getId());
				map.put("name", groupSendGuide.getName());
				map.put("operator", groupSendGuide.getOperator().getName());
				map.put("operatorPhone", groupSendGuide.getOperator().getMobile());
				map.put("startTime", groupSendGuide.getStartTime());
				map.put("endTime", groupSendGuide.getEndTime());
				map.put("travelProfile", groupSendGuide.getFuwuneirong());
				Json json2 = guideService.caculate(groupSendGuide.getLineType(), 
				groupSendGuide.getServiceType(),groupSendGuide.getTeamType() , guide.getZongheLevel(), groupSendGuide.getDays());
				BigDecimal serviceFee1;
				if (!json2.isSuccess()) {					
					return json;
				} else {
					serviceFee1 = (BigDecimal) json2.getObj();
				}
				map.put("serviceFee", serviceFee1);
				map.put("xiaofei", groupSendGuide.getXiaofei());
				//新增已抢单导游的ID列表
				String guideidstring = "";
				Long groupId = groupSendGuide.getGroup().getId();
				List<Filter> filters = new ArrayList<>();
				filters.add(Filter.eq("groupId", groupId));
				filters.add(Filter.eq("status", 1));
				filters.add(Filter.eq("qiangdanId", id));
				List<GuideAssignment> guideAssignments = guideAssignmentService.findList(null, filters, null);
				if(!guideAssignments.isEmpty()) {
					
					for(GuideAssignment temp : guideAssignments) {
						guideidstring = guideidstring + temp.getGuideId() + ",";
					}
					
				}
				map.put("guideidstring", guideidstring);
				json.setSuccess(true);
				json.setMsg("获取成功");
				json.setObj(map);
			}else{
				json.setSuccess(false);
				json.setMsg("抢单信息不存在");
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("抢单信息错误： "+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	
	@RequestMapping("takeOrder")
	@ResponseBody
	public Json takeOrder(Long id,Long guideId){
		Json json=new Json();
		json=groupSendGuideService.addOrder(id, guideId);
		return json;
	}
	
	/**
	 * add 2018131
	 * 获取抢单导游是否已满
	 * @return
	 */
	@RequestMapping(value = "checkQiangdan/view")
	@ResponseBody
	public Json checkQiangdan(Long guideId, Long groupSendGuideId) {
		Json j = new Json(); 
		try {
			Boolean flag = false; //默认导游抢单没有满
			Guide guide = guideService.find(guideId);
			GroupSendGuide temp = groupSendGuideService.find(groupSendGuideId);
			if(temp.getAllReceive() == temp.getGuideNo()) { //如果全部被抢完
				flag = true;
			} else if (temp.getIsRestrictSex()) { //如果限制性别
				if(guide.getSex() == 0 && (temp.getWomanReceive() == temp.getWomanNo())) { //如果导游是女性
					flag = true;
				} else if(guide.getSex() == 1 && (temp.getManReceive() == temp.getManNo())) { //如果导游是男性
					flag = true;
				}
			}					
				
			j.setSuccess(true);
			j.setMsg("获取抢单导游是否已满成功");	
			j.setObj(flag);	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
}
