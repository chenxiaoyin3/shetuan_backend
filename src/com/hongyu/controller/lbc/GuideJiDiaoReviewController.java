package com.hongyu.controller.lbc;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.controller.gxz04.LineController;
import com.hongyu.entity.GroupDivide;
import com.hongyu.entity.Guide;
import com.hongyu.entity.GuideAssignment;
import com.hongyu.entity.GuideReviewDetail;
import com.hongyu.entity.GuideReviewForm;
import com.hongyu.entity.GuideReviewFormScore;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyGroup;
import com.hongyu.entity.HyLine;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.ReviewForm;
import com.hongyu.entity.ReviewFormItem;
import com.hongyu.entity.Store;
import com.hongyu.entity.VisitorFeedback;
import com.hongyu.service.GroupDivideService;
import com.hongyu.service.GroupMemberService;
import com.hongyu.service.GuideAssignmentService;
import com.hongyu.service.GuideReviewDetailService;
import com.hongyu.service.GuideReviewFormScoreService;
import com.hongyu.service.GuideReviewFormService;
import com.hongyu.service.GuideService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.HyLineService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.ReviewFormItemService;
import com.hongyu.service.ReviewFormService;
import com.hongyu.service.StoreService;
import com.hongyu.service.VisitorFeedbackService;
import com.hongyu.wrapper.lbc.GuideReviewFormWrapper;


/**
 * 计调导游评价管理
 * @author lbc
 * 2018-8-6 错误筛查到这里
 */
@Controller
@RequestMapping("/admin/guideJiDiaoReview")
public class GuideJiDiaoReviewController {
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
	
	@Resource(name = "hyOrderServiceImpl")
	HyOrderService hyOrderService;
	
	@Resource(name = "storeServiceImpl")
	StoreService storeService;
	
	@Resource(name = "hyLineServiceImpl")
	HyLineService hyLineService;
	
	@Resource(name = "groupDivideServiceImpl")
	GroupDivideService groupDivideService;
	
	@Resource(name = "groupMemberServiceImpl")
	GroupMemberService groupMemberService;
	
	@Resource(name = "guideAssignmentServiceImpl")
	GuideAssignmentService guideAssignmentService;
	
	@Resource(name = "reviewFormServiceImpl")
	ReviewFormService reviewFormService;
	
	@Resource(name = "reviewFormItemServiceImpl")
	ReviewFormItemService reviewFormItemService;
	
	
	//计调导游评价列表显示
	@RequestMapping("/guideReviewList/view")
	@ResponseBody
	public Json getGuideReviewList(Pageable pageable, Date startday, Date endday, String pn, String line_name, Integer reviewStatus, HttpSession session, HttpServletRequest request) {
		/**
		 * 获取当前用户
		 */
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin admin = hyAdminService.find(username);
		
		
		Json json = new Json();
		try {
			
			//查表 guide assignment
		
			
			HashMap<String, Object> hm = new HashMap<String, Object>();
			List<HashMap<String, Object>> result = new ArrayList<>();
			
			List<Order> orders = new ArrayList<Order>();
			Order order = Order.desc("id");
			orders.add(order);
			
			//首先根据登录人的计调找到line
			List<Filter> linefilters = new ArrayList<Filter>();
			linefilters.add(Filter.eq("operator", admin));
			
			if(pn != null){
//				List<Filter> filters1 = new ArrayList<Filter>();
//				filters1.add(Filter.eq("pn", pn));
//				List<HyLine> hyLines = hyLineService.findList(null, filters1, null);
//				
//				Filter filter = Filter.in("line", hyLines);
				linefilters.add(Filter.eq("pn", pn));
			}
			if(line_name != null) {
//				List<Filter> filters1 = new ArrayList<Filter>();
//				//也可能是username
//				filters1.add(Filter.eq("name", line_name));
//				List<HyLine> hyLines = hyLineService.findList(null, filters1, null);
//				
//				Filter filter = Filter.in("line", hyLines);
				linefilters.add(Filter.eq("name", line_name));
			}
			
			List<HyLine> hyLines = hyLineService.findList(null, linefilters, null);
			
			if(hyLines.size() == 0) {
				json.setSuccess(false);
				json.setMsg("获取失败: 用户未创建线路");
				return json;
			}
			
			//根据line找到group
			List<Filter> groupfilters = new ArrayList<Filter>();
			groupfilters.add(Filter.in("line", hyLines));
			List<HyGroup> hyGroups = hyGroupService.findList(null, groupfilters, null);
			
			//根据group找到guide assignment
			List<GuideAssignment> guideAssignments = new ArrayList<GuideAssignment>();
			for(HyGroup hyGroup : hyGroups) {
				List<Filter> guideassignmentfilters = new ArrayList<Filter>();
				guideassignmentfilters.add(Filter.eq("groupId", hyGroup.getId()));
				//状态为已确认
				guideassignmentfilters.add(Filter.eq("status", 1));
				//开始日期和结束日期过滤
				if(startday != null){
					Filter filter = Filter.ge("startDate", startday);
					guideassignmentfilters.add(filter);
				}
				if(endday != null){
					Filter filter = Filter.le("endDate", endday);
					guideassignmentfilters.add(filter);
				}
				//是否已评价过滤 1为已评价 0为未评价
				if(reviewStatus != null){
					Filter filter = Filter.eq("reviewStatus", reviewStatus);
					guideassignmentfilters.add(filter);
				}
				
				List<GuideAssignment> guideAssignments2 = 
						guideAssignmentService.findList(null, guideassignmentfilters, null);
				for(GuideAssignment guideAssignment : guideAssignments2) {
					guideAssignments.add(guideAssignment);
				}
			}
			
			//包装在page中
			//Page<GuideAssignment> page = new Page<GuideAssignment>();
			//page.setTotal(guideAssignments.size());
			//页码
			int pageNumber = pageable.getPage();
			//每页记录数
			int pageRow = pageable.getRows();
			//根据页码和每页记录数计算返回列表中的哪几项
			//起始为pagerow*pagenumber
	
//			if(operator_name != null){
//				List<Filter> filters1 = new ArrayList<Filter>();
//				List<Filter> filters2 = new ArrayList<Filter>();
//				
//				//筛选名字是operator_name的operator
//				filters2.add(Filter.eq("name", operator_name));
//				List<HyAdmin> hyAdmins = hyAdminService.findList(null, filters2, null);
//				
//				
//				//筛选operator名字是operator_name的hyline
//				//也可能是username
//				filters1.add(Filter.in("operator", hyAdmins));
//				List<HyLine> hyLines = hyLineService.findList(null, filters1, null);
//				
//				Filter filter = Filter.in("line", hyLines);
//				filters.add(filter);
//			}
			
			for(int i = (pageNumber - 1) * pageRow; i < guideAssignments.size() && i < pageNumber * pageRow; i++) {
				HashMap<String, Object> m = new HashMap<String, Object>();
				GuideAssignment guideAssignment = guideAssignments.get(i);
				/*
				 * 封装导游综合评价页数据
				 */
				m.put("guideAssignment_id", guideAssignment.getId());
				HyGroup hyGroup = hyGroupService.find(guideAssignment.getGroupId());
				m.put("pn", hyGroup.getLine().getPn());
				m.put("startday", guideAssignment.getStartDate());
				m.put("endday", guideAssignment.getEndDate());
				m.put("line_name", guideAssignment.getLineName());
				m.put("guide_name", guideService.find(guideAssignment.getGuideId()).getName());
				m.put("review_status",guideAssignment.getReviewStatus());
				//找到order_id中的人数
				HyOrder hyOrder = hyOrderService.find(guideAssignment.getOrderId());
				if(hyOrder!=null) {
					m.put("people_num", hyOrder.getPeople());
				}
				else {
					m.put("people_num", null);
				}
				
				m.put("operator",admin.getUsername());
				result.add(m);
			}
			
			hm.put("total", guideAssignments.size());
			
			//2018-8-7 疑似错误
			//一共16条，pageRow = 10 ---> 16 % 10=6
			//change by cwz 2018-8-7
			if(guideAssignments.size() % pageRow == 0) {
				hm.put("totalPages", guideAssignments.size() / pageRow);
			}
			else {
				hm.put("totalPages", guideAssignments.size() / pageRow + 1);
			}
			
			hm.put("pageSize", pageRow);
			hm.put("rows", result);
			hm.put("pageNumber", pageable.getPage());
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
	 * 根据团id和导游id来查询评价表
	 * @param id	团id
	 * @param guideId	导游id
	 * @return	按道理这两个id应该唯一确定一个评价表
	 */
	
	//得到导游评价单
	@RequestMapping("/editReviewForm/detail/view")
	@ResponseBody
	public Json getReviewFormById( HttpSession session,Long guideAssignment_id){
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin admin = hyAdminService.find(username);
		Json json = new Json();
		try{
			GuideAssignment guideAssignment = guideAssignmentService.find(guideAssignment_id);
			Long id = guideAssignment.getGroupId();
			Long guideId = guideAssignment.getGuideId();
			
			HyGroup hyGroup = hyGroupService.find(id);
			Guide guide = guideService.find(guideId);
			
			//创建导游评价单模板 导游评价单id是2
			List<Filter> reviewformfilters = new ArrayList<>();
			reviewformfilters.add(Filter.eq("reviewFormType",2));
			List<ReviewForm> reviewForms = reviewFormService.findList(null, reviewformfilters, null);
			ReviewForm reviewForm;
			if(reviewForms.size() > 0) {
				reviewForm = reviewForms.get(0);
			}
			else {
				json.setSuccess(false);
				json.setMsg("无导游评价单模板");
				return json;
			}
			
			//查询review_form_id是导游评价单的review_form_item
			List<Filter> reviewformitemfilters = new ArrayList<>();
			reviewformitemfilters.add(Filter.eq("reviewForm",reviewForm));
			List<ReviewFormItem> reviewFormItems = reviewFormItemService.findList(null, reviewformitemfilters, null);
			
			HashMap<String, Object> result = new HashMap<>();
			
			/*
			 * 根据属性创建过滤器列表，筛选特定实体
			 */
			List<Filter> filters = new ArrayList<>();
			Filter filter1 = Filter.eq("groupId",id);
			filters.add(filter1);
			Filter filter2 = Filter.eq("guideId",guideId);
			filters.add(filter2);
			/*
			 * 按照创建日期排序
			 */
			List<Order> orders = new ArrayList<Order>();
			Order order = Order.desc("id");
			orders.add(order);
			/*
			 * 获取在此过滤情况下所有的实体数
			 */
			List<GuideReviewForm> guideReviewFormList = 
					guideReviewFormService.findList(null,filters,orders);
			GuideReviewForm guideReviewForm = null;
			/*
			 * 如果list为空,说明没有该实体
			 * 如果list不为空，将第一个实体返回
			 */
			if(guideReviewFormList.size() > 0){
				guideReviewForm = guideReviewFormList.get(0);
			}
			result.put("title",reviewForm.getTitle());
			result.put("content",reviewForm.getContent());
			result.put("reviewFormItems", reviewFormItems);
			if(guideReviewForm == null){
				//创建导游评价单
				guideReviewForm = new GuideReviewForm();
				guideReviewForm.setGroupId(id);
				guideReviewForm.setGuideId(guideId);
				//线路名称
				guideReviewForm.setLine(hyGroup.getLine().getName());
				guideReviewForm.setGuideName(guide.getName());
				guideReviewForm.setGuideSn(guide.getGuideSn());
				guideReviewForm.setGuideType(guideAssignment.getServiceType());
				guideReviewForm.setOrderId(guideAssignment.getOrderId());
				guideReviewForm.setPaiqianId(guideAssignment_id);
				//跟团游评价
				guideReviewForm.setReviewType(0);
				guideReviewForm.setOrderId(guideAssignment.getOrderId());
				guideReviewForm.setStartDate(guideAssignment.getStartDate());
				guideReviewForm.setReviewer(username);
				guideReviewForm.setPhone(admin.getMobile());
				guideReviewFormService.save(guideReviewForm);
				//guideReviewForm.setPhone(guide.getPhone());
				result.put("guideReviewForm", guideReviewForm);
				json.setSuccess(true);
				json.setMsg("获取成功,生成新评价单");
				json.setObj(result);
			}else{
				result.put("guideReviewForm", guideReviewForm);
				//找到所有是guidereviewformid的guidereviewformscore
				List<Filter> filters2 = new ArrayList<Filter>();
				filters2.add(Filter.eq("guideReviewForm", guideReviewForm));
				List<GuideReviewFormScore> guideReviewFormScores = guideReviewFormScoreService.findList(null, filters2, null);
				result.put("guideReviewFormScores",guideReviewFormScores);
				json.setSuccess(true);
				json.setMsg("获取成功,编辑原评价单");
				json.setObj(result);
			}
		}catch(Exception e){
			json.setSuccess(false);
			json.setMsg("获取错误: " + e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
	
	
	//更新导游评价单
	//2018-8-6 这个接口的问题 或者是上面的接口 这里面有计算规则
	//cwz 主要矛盾：那个0是算出来的，还是没有获取到 是算出来的
	//type = 0
	//线路的数据库
	@RequestMapping("/editReviewForm/detail/update")
	@ResponseBody
	public Json updateReviewFormById(@RequestBody GuideReviewFormWrapper guideReviewFormWrapper, HttpSession session){
		Json json = new Json();
		try{
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			//HyAdmin admin = hyAdminService.find(username);
			
			GuideAssignment guideAssignment = guideAssignmentService.find(guideReviewFormWrapper.getGuideAssignment_id());
			
			Long groupId = guideAssignment.getGroupId();
			Long guideId = guideAssignment.getGuideId();
			
			//HyGroup hyGroup = hyGroupService.find(groupId);
			//Guide guide = guideService.find(guideId);
			
			//1 已评价
			guideAssignment.setReviewStatus(1);
			guideAssignmentService.update(guideAssignment);
			
			//更新guide_review_form 符合业务逻辑
			GuideReviewForm guideReviewForm = guideReviewFormService.find(guideReviewFormWrapper.getGuideReviewForm_id());
			guideReviewForm.setAdvice(guideReviewFormWrapper.getAdvice());
			guideReviewForm.setPhone(guideReviewFormWrapper.getPhone());
			guideReviewForm.setReviewTime(new Date());
			
			
			List<GuideReviewFormScore> guideReviewFormScores = guideReviewFormWrapper.getGuideReviewFormScores();
			
			int totalScore = 0;
			//更新guideReviewFormScore 符合业务逻辑 
			//每个guideReviewFormScore set guideReviewForm_id
			
			//同样，guideReviewFormScore是计调的几项评价
			for(GuideReviewFormScore guideReviewFormScore : guideReviewFormScores) {
				GuideReviewFormScore guideReviewFormScore1;
				if(guideReviewFormScore.getId()!=null && guideReviewFormScoreService.find(guideReviewFormScore.getId())!=null) {
					guideReviewFormScore1 = guideReviewFormScoreService.find(guideReviewFormScore.getId());
					guideReviewFormScore1.setGuideReviewForm(guideReviewForm);
					//导游服务项目
					//guideReviewFormScore1.setServiceType(0);
					guideReviewFormScore1.setScoreItem(guideReviewFormScore.getScoreItem());
					//2018-8-7 1.更改reviewForm
					guideReviewFormScoreService.update(guideReviewFormScore1);
					totalScore += guideReviewFormScore1.getScoreItem() * 25;
				}
				else {
					guideReviewFormScore.setGuideReviewForm(guideReviewForm);
					//导游服务项目
					//guideReviewFormScore.setServiceType(0);
					//2018-8-7 2.更改reviewFormScore
					guideReviewFormScoreService.save(guideReviewFormScore);
					totalScore += guideReviewFormScore.getScoreItem() * 25;
				}
				
				
			}
			
			//暂时不知道totalscore计算方法
			guideReviewForm.setScoreTotal((int)(totalScore/guideReviewFormScores.size()));
			//每次set的时候计算综合分数，赋值给guide的综合level
			//根据某个导游自己的门店租借评价/游客反馈单/计调评价的这些综合起来算得的分数
			
			
			//评价人名字填当前登录的用户名
			guideReviewForm.setReviewer(username);
			guideReviewFormService.update(guideReviewForm);
			
			List<Filter> guideReviewDetailFilter1 = new ArrayList<Filter>();
			guideReviewDetailFilter1.add(Filter.eq("guideId",guideId));
			guideReviewDetailFilter1.add(Filter.eq("groupId", groupId));
			guideReviewDetailFilter1.add(Filter.eq("type", 0));
			List<GuideReviewDetail> guideReviewDetails2 = guideReviewDetailService.findList(null, guideReviewDetailFilter1, null);
			
			List<Filter> adminFilterOfOldScore = new ArrayList<Filter>();
			List<GuideReviewDetail> guideReviewDetailList = new ArrayList<>();
			Integer oldScore = null;
			
			//嗯 这里中文改成英文 
			//问题在于：派遣ID是否能重复 这里我觉得不能 可以这么筛选
			adminFilterOfOldScore.add(Filter.eq("paiqianId", guideReviewFormWrapper.getGuideAssignment_id()));
			guideReviewDetailList = guideReviewDetailService.findList(null, adminFilterOfOldScore, null);
			if(guideReviewDetailList.size() !=0){
				GuideReviewDetail frontGuideReviewDetail = guideReviewDetailList.get(0);
				//之前的score
				oldScore = frontGuideReviewDetail.getScore();
			}
			
			
			//更新guideReviewFormDetail
			if(guideReviewDetails2.size() == 0) {
				//更新guideReviewFormDetail
				
				//cwz 这里是新建
				GuideReviewDetail guideReviewDetail = new GuideReviewDetail();
				//门店租借
				guideReviewDetail.setType(0);		
				guideReviewDetail.setGroupId(groupId);
				guideReviewDetail.setGuideId(guideId);
				guideReviewDetail.setGuideType(guideAssignment.getServiceType());
				//神坑 2018-8-7 cwz
				guideReviewDetail.setPaiqianId(guideReviewFormWrapper.getGuideAssignment_id());
				
				//感觉不要和游客那边一块测试
				//add by cwz 2018-8-7 中午 这里不需要加
				guideReviewDetail.setScore((int)(totalScore/guideReviewFormScores.size()));
				guideReviewDetail.setStartDate(guideAssignment.getStartDate());
				guideReviewDetail.setOrderId(guideAssignment.getOrderId());
				guideReviewDetail.setTestDate(new Date());
				//2018-8-7 3.更改reviewDetail 符合业务逻辑
				guideReviewDetailService.save(guideReviewDetail);
			}
			else {
				
				//cwz 这里是更新
				GuideReviewDetail guideReviewDetail = guideReviewDetails2.get(0);
				guideReviewDetail.setType(0);		
				guideReviewDetail.setGroupId(groupId);
				guideReviewDetail.setGuideId(guideId);
				guideReviewDetail.setGuideType(guideAssignment.getServiceType());
				guideReviewDetail.setPaiqianId(guideReviewFormWrapper.getGuideAssignment_id());
				
				int updateScore = 0;
				//add by cwz 2018-8-7 中午
				if(oldScore!=null){
					int newScore = (int)(totalScore/guideReviewFormScores.size());
					updateScore = (newScore + oldScore)/2;
				}
				guideReviewDetail.setScore(updateScore);
				guideReviewDetail.setStartDate(guideAssignment.getStartDate());
				guideReviewDetail.setOrderId(guideAssignment.getOrderId());
				guideReviewDetail.setTestDate(new Date());
				//2018-8-7 3.更改reviewDetail
				guideReviewDetailService.update(guideReviewDetail);
			}
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
			json.setMsg("更新成功,编辑原评价单");

		}catch(Exception e){
			json.setSuccess(false);
			json.setMsg("获取错误: " + e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
		

}
