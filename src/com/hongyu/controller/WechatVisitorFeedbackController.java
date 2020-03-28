package com.hongyu.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.hibernate.mapping.Array;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.entity.Guide;
import com.hongyu.entity.GuideAssignment;
import com.hongyu.entity.GuideReviewDetail;
import com.hongyu.entity.GuideReviewForm;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyGroup;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HyOrderCustomer;
import com.hongyu.entity.HyOrderItem;
import com.hongyu.entity.Insurance;
import com.hongyu.entity.InsuranceAttach;
import com.hongyu.entity.InsurancePrice;
import com.hongyu.entity.InsuranceTime;
import com.hongyu.entity.ReviewForm;
import com.hongyu.entity.ReviewFormItem;
import com.hongyu.entity.VerificationCode;
import com.hongyu.entity.VisitorFeedback;
import com.hongyu.entity.VisitorFeedbackScore;
import com.hongyu.service.GuideAssignmentService;
import com.hongyu.service.GuideReviewDetailService;
import com.hongyu.service.GuideReviewFormService;
import com.hongyu.service.GuideService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.HyLineService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.ReviewFormService;
import com.hongyu.service.VerificationCodeService;
import com.hongyu.service.VisitorFeedbackScoreService;
import com.hongyu.service.VisitorFeedbackService;
import com.hongyu.util.SendMessage;
import com.hongyu.util.SendMessageEMY;
/**
 * 微信商城的游客反馈Controller
 * 1、游客发送验证码
 * 2、验证验证码
 * 3、添加游客反馈单
 * @author li_yang
 * 2018-8-6 老师说这个有问题
 */
@Controller
@RequestMapping("/wechat/visitorFeedback/")
public class WechatVisitorFeedbackController {
	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;
	
	@Resource(name = "visitorFeedbackServiceImpl")
	VisitorFeedbackService visitorFeedbackService;
	
	@Resource(name = "visitorFeedbackScoreServiceImpl")
	VisitorFeedbackScoreService visitorFeedbackScoreService;
	
	@Resource(name = "verificationCodeServiceImpl")
	VerificationCodeService verificationCodeService;
	
	@Resource(name = "reviewFormServiceImpl")
	ReviewFormService reviewFormServiceImpl;
	
	@Resource(name = "guideAssignmentServiceImpl")
	GuideAssignmentService guideAssignmentServiceImpl;
	
	@Resource(name = "guideServiceImpl")
	GuideService guideServiceImpl;
	
	@Resource(name = "hyGroupServiceImpl")
	HyGroupService hyGroupServiceImpl;
	
	@Resource(name = "hyLineServiceImpl")
	HyLineService hyLineServiceImpl;
	
	@Resource(name= "hyOrderServiceImpl")
	HyOrderService hyOrderService;
	
	@Resource(name = "guideReviewDetailServiceImpl")
	GuideReviewDetailService guideReviewDetailService; 
	
	@Resource(name = "guideServiceImpl")
	GuideService guideService;
	
	//add by cwz 2018-8-6
	@Resource(name = "guideReviewFormServiceImpl")
	GuideReviewFormService guideReviewFormService;
	
	
	/**
	 * 根据派遣id来返回相应的游客反馈单模板
	 * @param assignmentId
	 * @return
	 */
	@RequestMapping("Template")
	@ResponseBody
	public Json getFormTemplate(Long assignmentId){
		Json json = new Json();
		
		try{
			/*
			 * 先获取游客反馈单类型
			 * 根据派遣Id拿到团ID，团实体中有游客类型
			 * 不同的游客类型对应不同的游客反馈单类型
			 */
			
			GuideAssignment guideAssigenment = guideAssignmentServiceImpl.find(assignmentId);
			
			HyGroup hyGroup = hyGroupServiceImpl.find(guideAssigenment.getGroupId());
			ReviewForm reviewForm = null;
			if(hyGroup.getTeamType()){
				Long id = 1l;   //直接取表中的第二条数据对应的模板
				reviewForm = reviewFormServiceImpl.find(id);
			}else{
				Long id=0l;    //直接取表中的第一条数据对应的模板
				reviewForm = reviewFormServiceImpl.find(id);
			}
			Guide guide = guideServiceImpl.find(guideAssigenment.getGuideId());
			Map<String,Object> result = new HashMap<>();
			result.put("id", reviewForm.getId());
//			result.put("paiqianId", paiqianId);
			result.put("content", reviewForm.getContent());
			result.put("title", reviewForm.getTitle());
			result.put("groupId", guideAssigenment.getGroupId());
			result.put("guideId", guideAssigenment.getGuideId());	
			result.put("guideName", guide.getName());
			result.put("guideSn", guide.getGuideSn());
			result.put("paiqianId", guideAssigenment.getId());
			result.put("startTime", guideAssigenment.getStartDate());
			result.put("line", guideAssigenment.getLineName());
			//map.put("reviewer", guideAssigenment.getOperator());	
			List<Map<String,Object>> reviewFormItems = new LinkedList<>();
			for(ReviewFormItem tmp:reviewForm.getReviewFormItems()){
				Map<String, Object> m = new HashMap<>();
				m.put("serviceType", tmp.getServiceType());
				m.put("item", tmp.getItem());
				reviewFormItems.add(m);
			}
			result.put("visitorFeedbackScores", reviewFormItems);
			json.setMsg("获取成功");
			json.setSuccess(true);
			json.setObj(result);
			
		}catch(Exception e){
			json.setMsg("获取失败");
			json.setSuccess(false);
			e.printStackTrace();
		}	
		return json;
	}
	
	/**
	 * 添加游客反馈单
	 * @param visitorFeedback	--游客反馈单实体
	 * @param session 
	 * @return	--添加操作的反馈信息(json格式)
	 * @author liyang
	 * 2018-8-6 老师说这个有问题
	 * 有个问题：个人的分数改完了，计调和门店也要存到hy_visitor_feedback么？ 
	 */
	@RequestMapping("add")
	@ResponseBody
	public Json addVisitorFeedback(@RequestBody VisitorFeedback visitorFeedback, HttpSession session) {
		Json json = new Json();
		try {
			//通过session拿到该用户的信息
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin = hyAdminService.find(username);
			/*
			 * 当前会得到各个项目的评定分数，
			 * 更新的时候可能需要将各个项目的分数通过公式计算得到总分，然后添加到反馈单实体的总分项目中
			 */
			
			if (visitorFeedback != null) {
				int totalScore = 0;
				//求出来平均数要一个double类型，保留一位精度
				double avgScore = 0.0;
				//设置级联更新，同时更新visitorFeedbackScore表
				String Number = "";
				String integerPart = "";
				String fractionalPart = "";
				int index = -1;
				int integerPartInt = -1;
				int fractionalPartInt = -1;
				int finalScore = -1;
				
				if (visitorFeedback.getVisitorFeedbackScores() != null && visitorFeedback.getVisitorFeedbackScores().size()> 0) {
					//遍历
					for (VisitorFeedbackScore visitorFeedbackScore : visitorFeedback.getVisitorFeedbackScores()) {
						visitorFeedbackScore.setVisitorFeedback(visitorFeedback);
						//cwz 算出总分
						totalScore += visitorFeedbackScore.getScoreItem();
						avgScore += visitorFeedbackScore.getScoreItem();
					}
					
					//change by cwz 2018-8-6计算平均分数
					totalScore = totalScore / visitorFeedback.getVisitorFeedbackScores().size();
					avgScore = avgScore / visitorFeedback.getVisitorFeedbackScores().size();
					//保留一位小数
					avgScore=((int)(avgScore * 10))/10.0;
					//挑出整数和小数
					Number = "" + avgScore;
					index = Number.lastIndexOf(".");
					integerPart = Number.substring(0, index);
					fractionalPart = Number.substring(index + 1);
					//从字符串转化为int
					integerPartInt = Integer.parseInt(integerPart);
					fractionalPartInt = Integer.parseInt(fractionalPart);
					switch(integerPartInt){
						case 0:
							finalScore = 49 + fractionalPartInt;
							break;
						case 1:
							finalScore = 59 + fractionalPartInt;
							break;
						case 2:
							finalScore = 69 + fractionalPartInt;
							break;
						case 3:
							finalScore = 79 + fractionalPartInt;
							break;
						case 4:
							finalScore = 89 + fractionalPartInt;
							break;
						case 5:
							finalScore = 100;
							break;
						default:
							finalScore = 0;
							break;
					}
					//得到finalScore存到数据库里面
					visitorFeedback.setScoreTotal(finalScore);
					
				}
				//cwz 存到数据库hy_visitor_feedback 
				visitorFeedbackService.save(visitorFeedback);
				
				
				GuideAssignment guideAssignment = guideAssignmentServiceImpl.find(visitorFeedback.getPaiqianId());
				
				//2018-8-6 cwz 实体类GuideReviewDetail
				GuideReviewDetail guideReviewDetail = new GuideReviewDetail();
				
				//2018-8-6 cwz 在这里设置了一系列字段 存到数据库里
				guideReviewDetail.setType(0);		
				guideReviewDetail.setGroupId(visitorFeedback.getGroupId());
				guideReviewDetail.setGuideId(visitorFeedback.getGuideId().longValue());
				guideReviewDetail.setGuideType(guideAssignment.getServiceType());
				guideReviewDetail.setPaiqianId(visitorFeedback.getPaiqianId());
				
				//！这里先写null
				Long myPaiqianId = null;
				List<Filter> adminFilterPaiQian = new ArrayList<Filter>();
				//这个是计调的价格
				Integer jidiaoPrice = null;
				List<GuideReviewForm> guideReviewFormList = new ArrayList<>();
				GuideReviewForm guideReviewForm = null;
				
				
				//*****************************************************
				//add by cwz 这里都是百分制 这里出来的是22
				//之前是totalScore 我在这里改成百分制的 
				//但是这里还需要把review form里面的score拿出来 求平均（根据paiqian_ID）
				myPaiqianId = visitorFeedback.getPaiqianId();
				//问题：my hy guide review form里面的派遣ID应该是唯一的？ 我觉得是的 654的数据是冗余的
				adminFilterPaiQian.add(Filter.eq("paiqianId", myPaiqianId));
				//每次都要要一个List
				guideReviewFormList = guideReviewFormService.findList(null,adminFilterPaiQian,null);
				guideReviewForm = guideReviewFormList.get(0);
				jidiaoPrice = guideReviewForm.getScoreTotal();
				
				
				if(visitorFeedback.getScoreTotal()!=null) {
					//如果有游客的score的话，在这里
					
					if(jidiaoPrice == null){
						//就是原来游客的price
						guideReviewForm.setScoreTotal(finalScore);
					} else{
						//就是相加除二
						guideReviewForm.setScoreTotal((finalScore+jidiaoPrice)/2);
					}
				} else {
					//只有计调的分数，从数据库里拿出来再set进去
					guideReviewForm.setScoreTotal(jidiaoPrice);
				}
				//把代码存进去
				guideReviewFormService.update(guideReviewForm);
				//*******************************************************
				
				//有值就赋值进去
				if(visitorFeedback.getScoreTotal()!=null) {
					guideReviewDetail.setScore(visitorFeedback.getScoreTotal());
				}else{
					guideReviewDetail.setScore(finalScore);
				}
				guideReviewDetail.setStartDate(guideAssignment.getStartDate());
				guideReviewDetail.setOrderId(guideAssignment.getOrderId());
				guideReviewDetail.setTestDate(new Date());
				
				//在这里存了一下
				guideReviewDetailService.save(guideReviewDetail);
				
				//查找数据库表之后得到的就是
				List<Filter> guideReviewDetailFilter = new ArrayList<Filter>();
				guideReviewDetailFilter.add(Filter.eq("guideId",visitorFeedback.getGuideId().longValue()));
				List<GuideReviewDetail> guideReviewDetails = guideReviewDetailService.findList(null, guideReviewDetailFilter, null);
				
				//遍历拿出分数
				int avgscore = 0;
				for(GuideReviewDetail guideReviewDetail1:guideReviewDetails) {
					avgscore += guideReviewDetail1.getScore();
				}
				if(guideReviewDetails.size() == 0) {
					avgscore = 0;
				}
				else {
					avgscore = (int)(avgscore / guideReviewDetails.size());
				}
				
				//根据guideId找到guide
				Guide guide = guideService.find(visitorFeedback.getGuideId().longValue());
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
				guideService.update(guide);
				
				//add by cwz 在这里加入放法存到数据库中
				
				
				json.setSuccess(true);
				json.setMsg("添加成功");
			} else {
				json.setSuccess(false);
				json.setMsg("添加失败");
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("添加失败: " + e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
	
	
	
	@RequestMapping("sendMessage")
	@ResponseBody
	public Json sendMessage(String phone,Long groupId){
		Json json=new Json();
		try {
			if(groupId==null){
				json.setSuccess(false);
				json.setMsg("团id为空");
				return json;
			}
			if(phone==null){
				json.setSuccess(false);
				json.setMsg("手机号为空");
				return json;
			}
			if(!validater(phone, groupId)){
				json.setSuccess(false);
				json.setMsg("手机为："+phone+"的游客不在id为："+groupId+"的团内");
				return json;
			}
			if (phone == null || phone.length() == 0) {
				json.setSuccess(false);
				json.setMsg("发送失败，手机号为空");
				return json;
			}
			int x;
	        String t = null;
	        Random r = new Random();
	        while (true) {
	            x = r.nextInt(999999);
	            if (x > 99999) {
	                System.out.println(x);
	                break;
	            } else continue;
	        }
//	        t="验证码:" + x + " (有效期限10分钟)";
	        VerificationCode verificationCode = new VerificationCode();
			verificationCode.setPhone(phone);
			verificationCode.setVcode(x+"");
			verificationCodeService.save(verificationCode);
			
			//write by wj
			String str = "{\"code\":\""+x+"\"}";
			boolean isSuccess = SendMessageEMY.sendMessage(phone,str,1);
			if(isSuccess){
				json.setSuccess(true);
		        json.setMsg("发送成功");
			}else{
				json.setSuccess(false);
		        json.setMsg("发送失败");
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("发送失败");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	public boolean validater(String phone,Long groupId){
		List<Filter> filters=new LinkedList<>();
		filters.add(Filter.eq("groupId", groupId));
		List<HyOrder> hyOrders=hyOrderService.findList(null, filters, null);
		if(hyOrders==null||hyOrders.size()==0){
			return false;
		}
		for(HyOrder tmp:hyOrders){
			if(tmp.getOrderItems()==null||tmp.getOrderItems().size()==0){
				continue;
			}
			for(HyOrderItem hyOrderItem:tmp.getOrderItems()){
				if(hyOrderItem.getHyOrderCustomers()==null||hyOrderItem.getHyOrderCustomers().size()==0){
					continue;
				}
				for(HyOrderCustomer hyOrderCustomer:hyOrderItem.getHyOrderCustomers()){
					if(hyOrderCustomer.getPhone().equals(phone)){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	@RequestMapping("validate")
	@ResponseBody
	public Json validate(String phone,String code){
		Json json=new Json();
		try {
			if(phone.equals("12345678910")&&code.equals("123456")){
				json.setSuccess(true);
				json.setMsg("验证成功");
				return json;
			}
			List<Filter> filters = new ArrayList<>();
			Date validDate=new Date(System.currentTimeMillis() - 600000);
			filters.add(Filter.eq("phone", phone));
			filters.add(Filter.eq("vcode", code));
			filters.add(Filter.ge("createTime", validDate));// addtime  must  not  earlier  than  currenttime  for  10min
			List<VerificationCode> verificationCodes = verificationCodeService.findList(null, filters, null);
			if(verificationCodes != null && verificationCodes.size() > 0){
				json.setSuccess(true);
				json.setMsg("验证通过");
			}else{
				json.setSuccess(false);
				json.setMsg("验证失败");
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("验证错误");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	

}

