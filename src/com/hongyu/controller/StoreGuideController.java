package com.hongyu.controller;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.Guide;
import com.hongyu.entity.GuideAssignment;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HyLine.LineType;
import com.hongyu.service.GuideAssignmentService;
import com.hongyu.service.GuideService;
import com.hongyu.service.HyOrderService;
import com.hongyu.util.DateUtil;

import oracle.net.aso.r;


@Controller
@RequestMapping("/admin/storeGuide/")
public class StoreGuideController {
	
	@Resource(name="guideServiceImpl")
	GuideService guideService;
	
	@Resource(name="guideAssignmentServiceImpl")
	GuideAssignmentService guideAssignmentService;
	
	@Resource(name = "hyOrderServiceImpl")
	HyOrderService hyOrderService;
	@RequestMapping("/list/view")
	@ResponseBody
	public Json list(Pageable pageable,Integer sex,Integer zongheLevel,String name){
		Json json=new Json();
		try {
			List<Filter> filters=new LinkedList<>();
			if(sex!=null){
				filters.add(Filter.eq("sex", sex));
			}
			if(zongheLevel!=null){
				filters.add(Filter.eq("zongheLevel", zongheLevel));
			}
			if(name!=null){
				filters.add(Filter.like("name", name));
			}
			filters.add(Filter.eq("status", 1));//已审核
			pageable.setFilters(filters);
			Page<Guide> page=guideService.findPage(pageable);
			List<Map<String, Object>> result=new LinkedList<>();
			for(Guide tmp:page.getRows()){
				Map<String, Object> map=new HashMap<>();
				map.put("id", tmp.getId());
				map.put("guideSn", tmp.getGuideSn());
				map.put("name", tmp.getName());
				map.put("sex", tmp.getSex());
				map.put("zongheLevel", tmp.getZongheLevel());
				result.add(map);
			}
			Map<String, Object>hMap=new HashMap<>();
			hMap.put("pageNumber", page.getPageNumber());
			hMap.put("pageSize", page.getPageSize());
			hMap.put("total", page.getTotal());
			hMap.put("rows", result);
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(hMap);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取错误： "+e.getMessage());
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
			Guide guide=guideService.find(id);
			if(guide==null){
				json.setSuccess(false);
				json.setMsg("导游不存在");
			}else{
				json.setSuccess(true);
				json.setMsg("获取成功");
				json.setObj(guide);
			}
		} catch (Exception e) {
			json.setSuccess(true);
			json.setMsg("获取错误： "+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	
	@RequestMapping("search/view")
	@ResponseBody
	public Json search(Integer id,@DateTimeFormat(iso=ISO.DATE)Date startDate,@DateTimeFormat(iso=ISO.DATE)Date endDate){
		Json json=new Json();
		try {
			if (startDate.after(endDate)) {
				json.setSuccess(false);
				json.setMsg("开始日期不能晚于结束日期");
			}else{
				List<GuideAssignment> guideAssignments=new LinkedList<>();
				
				List<Filter> filters=new LinkedList<>();
				filters.add(Filter.eq("guideId", id));
				filters.add(Filter.eq("status", 1));
				filters.add(Filter.le("startDate", startDate));
				filters.add(Filter.ge("endDate", startDate));
				List<GuideAssignment> list=guideAssignmentService.findList(null,filters,null);
				guideAssignments.addAll(list);
				
				List<Filter> filters2=new LinkedList<>();
				filters2.add(Filter.eq("guideId", id));
				filters2.add(Filter.eq("status", 1));
				filters2.add(Filter.le("startDate", endDate));
				filters2.add(Filter.ge("endDate", endDate));
				List<GuideAssignment> list2=guideAssignmentService.findList(null,filters2,null);
				guideAssignments.addAll(list2);
				
				List<Filter> filters3=new LinkedList<>();
				filters3.add(Filter.eq("guideId", id));
				filters3.add(Filter.eq("status", 1));
				filters3.add(Filter.ge("startDate", startDate));
				filters3.add(Filter.le("endDate", endDate));
				List<GuideAssignment> list3=guideAssignmentService.findList(null,filters3,null);
				guideAssignments.addAll(list3);
				List<Map<String, Object>> result=new LinkedList<>();
				for(Date now=startDate;!now.after(endDate);now=DateUtil.getNextDay(now)){
					boolean find=false;
					for(GuideAssignment tmp:guideAssignments){
						if(!now.before(tmp.getStartDate())&&!now.after(tmp.getEndDate())){
							Map<String, Object> map=new HashMap<>();
							map.put("date", getDate(now));
							map.put("canChoose", false);
							result.add(map);
							find=true;
							break;
						}
					}
					if(!find){
						Map<String, Object> map=new HashMap<>();
						map.put("date", getDate(now));
						map.put("canChoose", true);
						result.add(map);
					}
				}
				json.setSuccess(true);
				json.setMsg("查找成功");
				json.setObj(result);
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询错误： "+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	private static String getDate(Date date){
		Calendar current = Calendar.getInstance();
		current.setTime(date);
		int year = current.get(Calendar.YEAR);
		int month = current.get(Calendar.MONTH)+1;
		int day = current.get(Calendar.DAY_OF_MONTH);
		String ans=String.format("%04d", year)+String.format("%02d", month)+String.format("%02d", day);
		return ans;
	}
	
	@RequestMapping("order")
	@ResponseBody
	public Json add(@RequestBody HyOrder hyOrder,HttpSession session){
		Json json=new Json();
		try {
			json=hyOrderService.addGuideOrder(hyOrder, session);
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("下单错误： "+e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
	
	@RequestMapping("caculate")
	@ResponseBody
	public Json caculate(Integer lineType,Integer serviceType,Integer star,Integer days){
		Json json=new Json();
		try {
			LineType lineType2;
			Boolean groupType;
			if(lineType==0||lineType==1){
				lineType2=LineType.qiche;
				if (lineType==0) {
					groupType=false;
				}else {
					groupType=true;
				}
			}else if(lineType==2||lineType==3){
				lineType2=LineType.guonei;
				if (lineType==2) {
					groupType=false;
				}else {
					groupType=true;
				}
			}else {
				lineType2=LineType.chujing;
				if (lineType==4) {
					groupType=false;
				}else {
					groupType=true;
				}
			}
			json=guideService.caculate(lineType2, serviceType, groupType, star, days);
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("获取错误： "+e.getMessage());
			e.printStackTrace();
		}
		return json;
	}

}
