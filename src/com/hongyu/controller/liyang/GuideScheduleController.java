package com.hongyu.controller.liyang;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.net.ftp.parser.MacOsPeterFTPEntryParser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.Guide;
import com.hongyu.entity.GuideAssignment;
import com.hongyu.entity.HyGroup;
import com.hongyu.service.GuideAssignmentService;
import com.hongyu.service.GuideService;
import com.hongyu.service.HyGroupService;
import com.hongyu.util.DateUtil;
import com.sun.corba.se.spi.ior.TaggedProfileTemplate;

/**
 * 导游排班表
 * @author liyang
 *
 */
@Controller
@RequestMapping("/admin/guideSchedule/")
public class GuideScheduleController {
	@Resource(name="guideServiceImpl")
	GuideService guideService;
	
	@Resource(name = "guideAssignmentServiceImpl")
	GuideAssignmentService guideAssignmentService;
	
	@Resource(name="hyGroupServiceImpl")
	HyGroupService hyGroupService;
	
	/**
	 * 导游排班表
	 * @param pageable	分页信息
	 * @param name	导游姓名
	 * @return
	 */
	@RequestMapping("list/view")
	@ResponseBody
	public Json guideArrangement(Pageable pageable,String name){
		Json json=new Json();
		try {
			Date  today=DateUtil.getStartOfDay(new Date());
			List<Filter> filters=new LinkedList<>();
			filters.add(Filter.ge("endDate", today));
			filters.add(Filter.eq("status", 1));
			Map<String, Object> hashMap=new HashMap<>();
			if(name != null){
				List<Filter> filters2 = new ArrayList<>();
				filters2.add(Filter.like("name", name));
				List<Guide> guides = guideService.findList(null,filters2,null);
				List<GuideAssignment> guideAssignments = new ArrayList<>();
				for (Guide guide : guides) {
					filters.add(Filter.eq("guideId",guide.getId()));
					List<GuideAssignment> oneGuideAssignment = guideAssignmentService.findList(null,filters,null); 
					guideAssignments.addAll(oneGuideAssignment);		
				}
				
				List<Map<String, Object>>result=new LinkedList<>();
				for(GuideAssignment tmp:guideAssignments){
					Map<String, Object> map=new HashMap<>();
					Guide guide = guideService.find(tmp.getGuideId());
					map.put("guideId", guide.getId());
					map.put("guideName", guide.getName());
					map.put("guideSn", guide.getGuideSn());
					map.put("lineName", tmp.getLineName());
					map.put("startDate", tmp.getStartDate());
					map.put("endDate", tmp.getEndDate());
					map.put("operator", tmp.getOperator());
					//此处将电话改成导游的电话。为了前端方便，暂时不改字段
					map.put("operatorPhone", guide.getPhone());
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
				int pg = pageable.getPage();
				int rows = pageable.getRows();
				hashMap.put("pageNumber", pg);
				hashMap.put("pageSize", rows);
				hashMap.put("total", guideAssignments.size());
				hashMap.put("rows", result.subList((pg-1)*rows, pg*rows>guideAssignments.size()?guideAssignments.size():pg*rows));			
			}else{
				pageable.setFilters(filters);
				Page<GuideAssignment> page=guideAssignmentService.findPage(pageable);
				List<Map<String, Object>>result=new LinkedList<>();
				for(GuideAssignment tmp:page.getRows()){
					Map<String, Object> map=new HashMap<>();
					Guide guide = guideService.find(tmp.getGuideId());
					map.put("guideId", guide.getId());
					map.put("guideName", guide.getName());
					map.put("guideSn", guide.getGuideSn());
					map.put("lineName", tmp.getLineName());
					map.put("startDate", tmp.getStartDate());
					map.put("endDate", tmp.getEndDate());
					map.put("operator", tmp.getOperator());
					//电话改成导游电话，字段为了方便不改
					map.put("operatorPhone", guide.getPhone());
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
				
				hashMap.put("pageNumber", page.getPageNumber());
				hashMap.put("pageSize", page.getPageSize());
				hashMap.put("total", page.getTotal());
				hashMap.put("rows", result);
			}		
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
