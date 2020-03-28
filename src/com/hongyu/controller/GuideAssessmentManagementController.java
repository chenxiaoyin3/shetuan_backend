package com.hongyu.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
import com.hongyu.entity.Guide;
import com.hongyu.entity.GuideReviewDetail;
import com.hongyu.service.GuideReviewDetailService;
import com.hongyu.service.GuideReviewFormScoreService;
import com.hongyu.service.GuideReviewFormService;
import com.hongyu.service.GuideService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.VisitorFeedbackService;

/**
 * 导游考核评分管理
 * 
 * @author li_yang
 *
 */
@Controller
@RequestMapping("/admin/guideAssessmentManagement")
public class GuideAssessmentManagementController {
	
	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;
	
	@Resource(name = "guideServiceImpl")
	GuideService guideService;
	
	@Resource(name = "guideReviewDetailServiceImpl")
	GuideReviewDetailService guideReviewDetailService; 
	
	@Resource(name = "guideReviewFormServiceImpl")
	GuideReviewFormService guideReviewFormService;
	
	@Resource(name = "guideReviewFormScoreServiceImpl")
	GuideReviewFormScoreService guideReviewFormScoreService;
	
	@Resource(name = "visitorFeedbackServiceImpl")
	VisitorFeedbackService visitorFeedbackService;
	
	@Resource(name = "hyGroupServiceImpl")
	HyGroupService hyGroupService;
	
	/**
	 * 返回所有的导游姓名，提供下拉框选项
	 * @return
	 */
	@RequestMapping("/nameList/view")
	@ResponseBody
	public Json getGuideNameList(){
		Json json = new Json();
		try {
			List<Guide> guides = guideService.findAll();
			List<HashMap<String,Object>> result = new ArrayList<>();
			for (Guide guide : guides) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("id", guide.getId());
				map.put("guideSn", guide.getGuideSn());
				map.put("name", guide.getName());
				result.add(map);
			}
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(result);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取出错"+e.getMessage());
			e.printStackTrace();
			
		}
		return json;
	}
	/**
	 * 获取导游考核评分列表
	 * 返回review_detail表中的所有type为2的数据
	 * 但是只有导游编号，只能通过这个编号去查导游姓名
	 * @param pageable
	 * @param guidename
	 * @return
	 */
	@RequestMapping("/list/view")
	@ResponseBody
	public Json getGuideAssessmentList(Pageable pageable, String guideName) {
		Json json = new Json();
		try {
			HashMap<String, Object> hm = new HashMap<String, Object>();
			List<HashMap<String, Object>> result = new ArrayList<>();
			
			List<Order> orders = new ArrayList<Order>();
			Order order = Order.desc("guideId");
			orders.add(order);
			
			List<Filter> filters = new ArrayList<Filter>();
			if(guideName != null){
				Filter filter = Filter.eq("name", guideName);
				filters.add(filter);
			}
			
			List<Guide> guides = guideService.findList(null,filters,null);
			List<GuideReviewDetail> testList = new ArrayList<>();
			for(Guide guide : guides){
				List<Filter> filters2 = new ArrayList<>();
				filters2.add(Filter.eq("guideId", guide.getId()));
				filters2.add(Filter.eq("type", 2));
				List<GuideReviewDetail> list = guideReviewDetailService.findList(null,filters2,null); 
				testList.addAll(list);
			}
			for(GuideReviewDetail tmp:testList){
				HashMap<String, Object> map = new HashMap<>();
				Guide guide2 = guideService.find(tmp.getGuideId() + 0L);
				map.put("id", tmp.getId());
				map.put("guideId", tmp.getGuideId());
				map.put("guideName", guide2.getName());
				map.put("guideSn", tmp.getGuideSn());
				map.put("content", tmp.getContent());
				map.put("testDate", tmp.getTestDate());
				map.put("score", tmp.getScore());
				result.add(map);
			}
			int pg = pageable.getPage();
			int rows = pageable.getRows();
			hm.put("total", testList.size());
			hm.put("pageNumber", pg);
			hm.put("pageSize", rows);
			hm.put("rows", result.subList((pg-1)*rows, pg*rows>testList.size()?testList.size():pg*rows));
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(hm);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取失败: " + e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
	
	/**
	 * 添加一个新导游考核记录
	 * @param id		导游Id
	 * @param content	考核内容
	 * @param score		分数
	 * @param testDate	考核日期
	 * @return
	 */
	@RequestMapping("/add")
	@ResponseBody
	public Json add(Long id,String content, Integer score,@DateTimeFormat(iso=ISO.DATE)Date testDate){
		Json json = new Json();
		try {
			GuideReviewDetail guideReviewDetail = new GuideReviewDetail();
			guideReviewDetail.setContent(content);
			guideReviewDetail.setGuideId(id);
			guideReviewDetail.setScore(score);
			guideReviewDetail.setTestDate(testDate);
			Guide guide = guideService.find(id + 0L);
			guideReviewDetail.setGuideSn(guide.getGuideSn());
			guideReviewDetail.setType(2);
			guideReviewDetailService.save(guideReviewDetail);
			Long guideId = guideReviewDetail.getGuideId();
			//查询每种guidereviewdetail 分数加和除以总数  赋综合level
			//就按 50-59 一星；60-69 二星；70-79三星；80-89四星；90-100五星
			
			//四级 100 0级0 1级25 2级50
			List<Filter> guideReviewDetailFilter = new ArrayList<Filter>();
			guideReviewDetailFilter.add(Filter.eq("guideId",guideId));
			//3.cwz 看看这个，因为是从这里找到的
			List<GuideReviewDetail> guideReviewDetails = guideReviewDetailService.findList(null, guideReviewDetailFilter, null);
			
			//2.cwz 这里的问题 
			int avgscore = 0;
			for(GuideReviewDetail guideReviewDetail1:guideReviewDetails) {
				//cwz 把每一项拿出来相加
				avgscore += guideReviewDetail1.getScore();
			}
			
			//cwz 如果没有数据，分数肯定是0
			if(guideReviewDetails.size() == 0) {
				avgscore = 0;
			}
			else {
				//cwz 计算平均分数，总分数除以大小
				avgscore = (int)(avgscore / guideReviewDetails.size());
			}
			
			//根据guideId找到guide
			//注意这个 cwz
//			Guide guide = guideService.find(guideId);
			
			//1.cwz 这里的问题 老师出来的评价是0 老师的分数
			if(avgscore < 50) {
				guide.setZongheLevel(0);
			}
			else if(avgscore >=50 && avgscore <= 59) {
				guide.setZongheLevel(1);
			}
			else if(avgscore >=60 && avgscore <= 69) {
				guide.setZongheLevel(2);
			}
			else if(avgscore >=70 && avgscore <= 79) {
				guide.setZongheLevel(3);
			}
			else if(avgscore >=80 && avgscore <= 89) {
				guide.setZongheLevel(4);
			}
			else {
				guide.setZongheLevel(5);
			}
			
			//注意这里 cwz
			guideService.update(guide);
			
			json.setSuccess(true);
			json.setMsg("保存成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("保存失败"+e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
	
	/**
	 * 返回考核信息详情
	 * @param id	考核记录主键id
	 * @return
	 */
	@RequestMapping("/detail/view")
	@ResponseBody
	public Json getReviewDetail(Long id){
		Json json = new Json();
		try {
			GuideReviewDetail guideReviewDetail = guideReviewDetailService.find(id);	
			HashMap<String, Object> result = new HashMap<>();
			result.put("guideReviewDetail", guideReviewDetail);
			Guide guide = guideService.find(guideReviewDetail.getGuideId() + 0L);
			result.put("guideName", guide.getName());
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(result);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取失败"+e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
	/**
	 * 编辑现有的考核信息
	 * @param id		考核详情单的id
	 * @param content	考核内容
	 * @param score		考核分数
	 * @param testDate	考核日期
	 * @return
	 */
	@RequestMapping("/update")
	@ResponseBody
	public Json update(Long id,String content, Integer score,@DateTimeFormat(iso=ISO.DATE)Date testDate){
		Json json = new Json();
		try {
			//
			GuideReviewDetail guideReviewDetail = guideReviewDetailService.find(id);
			guideReviewDetail.setContent(content);
			guideReviewDetail.setScore(score);
			guideReviewDetail.setTestDate(testDate);
			guideReviewDetail.setType(2);
			guideReviewDetailService.update(guideReviewDetail);	
			Long guideId = guideReviewDetail.getGuideId();
			//查询每种guidereviewdetail 分数加和除以总数  赋综合level
			//就按 50-59 一星；60-69 二星；70-79三星；80-89四星；90-100五星
			
			//四级 100 0级0 1级25 2级50
			List<Filter> guideReviewDetailFilter = new ArrayList<Filter>();
			guideReviewDetailFilter.add(Filter.eq("guideId",guideId));
			//3.cwz 看看这个，因为是从这里找到的
			List<GuideReviewDetail> guideReviewDetails = guideReviewDetailService.findList(null, guideReviewDetailFilter, null);
			
			//2.cwz 这里的问题 
			int avgscore = 0;
			for(GuideReviewDetail guideReviewDetail1:guideReviewDetails) {
				//cwz 把每一项拿出来相加
				avgscore += guideReviewDetail1.getScore();
			}
			
			//cwz 如果没有数据，分数肯定是0
			if(guideReviewDetails.size() == 0) {
				avgscore = 0;
			}
			else {
				//cwz 计算平均分数，总分数除以大小
				avgscore = (int)(avgscore / guideReviewDetails.size());
			}
			
			//根据guideId找到guide
			//注意这个 cwz
			Guide guide = guideService.find(guideId);
			
			//1.cwz 这里的问题 老师出来的评价是0 老师的分数
			if(avgscore < 50) {
				guide.setZongheLevel(0);
			}
			else if(avgscore >=50 && avgscore <= 59) {
				guide.setZongheLevel(1);
			}
			else if(avgscore >=60 && avgscore <= 69) {
				guide.setZongheLevel(2);
			}
			else if(avgscore >=70 && avgscore <= 79) {
				guide.setZongheLevel(3);
			}
			else if(avgscore >=80 && avgscore <= 89) {
				guide.setZongheLevel(4);
			}
			else {
				guide.setZongheLevel(5);
			}
			
			//注意这里 cwz
			guideService.update(guide);
			json.setSuccess(true);
			json.setMsg("更新成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("更新失败"+e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
	
	/**
	 * 将实体回传，更新考核记录
	 * @param guideReviewDetail
	 * @return
	 */
/*	@RequestMapping("/update")
	@ResponseBody
	public Json update(GuideReviewDetail guideReviewDetail){
		Json json = new Json();
		try {
			GuideReviewDetail detail = guideReviewDetailService.find(guideReviewDetail.getId());
			detail.setContent(guideReviewDetail.getContent());
			detail.setTestDate(guideReviewDetail.getTestDate());
			detail.setScore(guideReviewDetail.getScore());
			guideReviewDetailService.update(detail);	
			json.setSuccess(true);
			json.setMsg("更新成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("更新失败"+e.getMessage());
			e.printStackTrace();
		}
		return json;
	}*/
	
	/**
	 * 根据考核评价id来删除该考核记录
	 * @param id	考核记录主键id
	 * @return
	 */
	@RequestMapping("/cancel")
	@ResponseBody
	public Json delete(Long id){
		Json json = new Json();
		try {
			guideReviewDetailService.delete(id);	
			
			json.setSuccess(true);
			json.setMsg("取消成功");
			
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("取消失败"+e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
	
}
