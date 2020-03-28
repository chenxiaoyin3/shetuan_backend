package com.hongyu.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

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
import com.hongyu.entity.GuideReviewDetail;
import com.hongyu.entity.GuideReviewForm;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyGroup;
import com.hongyu.entity.HyLine;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.Insurance;
import com.hongyu.entity.Store;
import com.hongyu.entity.VisitorFeedback;
import com.hongyu.entity.VisitorFeedbackScore;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.service.GuideReviewDetailService;
import com.hongyu.service.GuideReviewFormScoreService;
import com.hongyu.service.GuideReviewFormService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.StoreService;
import com.hongyu.service.VisitorFeedbackService;
import com.hongyu.util.AuthorityUtils;
/**
 * 评价管理
 * 1、按照导游id返回评价列表
 * 2、按评价id返回评价详情
 * 3、按照团id获取计调或门店对导游的评价单
 * 4、按照团id返回游客对导游的评价反馈单
 * @author li_yang
 *
 */
@Controller
@RequestMapping("/wechat/guideReview")
public class WechatGuideReviewController {
	
	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;
	
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
	StoreService	storeService;
	/**
	 * 根据导游ID返回带有分页的所有该导游的评价详情单
	 * @param pageable
	 * @param id	导游id
	 * @param session
	 * @param request
	 * @return
	 */
	@RequestMapping("/list/view")
	@ResponseBody
	public Json getReviewListByGuideID(Pageable pageable, Integer id, HttpSession session, HttpServletRequest request) {
		Json json = new Json();
		try {
			HashMap<String, Object> hm = new HashMap<String, Object>();
			List<HashMap<String, Object>> result = new ArrayList<>();
			
			List<Order> orders = new ArrayList<Order>();
			Order order = Order.desc("id");
			orders.add(order);
			
			List<Filter> filters = new ArrayList<Filter>();
			Filter filter = Filter.eq("guideId", id);
			filters.add(filter);
			
			pageable.setFilters(filters);
			pageable.setOrders(orders);
			Page<GuideReviewDetail> page = guideReviewDetailService.findPage(pageable);
			for (GuideReviewDetail tmp : page.getRows()) {
				HashMap<String, Object> m = new HashMap<String, Object>();
				m.put("id", tmp.getId());
				m.put("guideId", tmp.getGuideId());
				m.put("guideSn", tmp.getGuideSn());
				m.put("type", tmp.getType());
				m.put("guideType", tmp.getGuideType());
				m.put("groupId", tmp.getGroupId());
				if(tmp.getGroupId()!=null){
					HyGroup hyGroup = hyGroupService.find(tmp.getGroupId());
					if(hyGroup!=null){
						HyLine hyline = hyGroup.getLine();
						m.put("pn",hyline.getPn());
					}	
				}
				m.put("orderId", tmp.getOrderId());
				m.put("paiqianId", tmp.getPaiqianId());
				m.put("lineSn", tmp.getLineSn());
				m.put("startDate", tmp.getStartDate());
				m.put("testDate", tmp.getTestDate());
				m.put("content", tmp.getContent());
				m.put("score", tmp.getScore());
				m.put("starLevel", tmp.getStarLevel());
				result.add(m);
			}
			hm.put("total", page.getTotal());
			hm.put("pageNumber", page.getPageNumber());
			hm.put("pageSize", page.getPageSize());
			hm.put("rows", result);
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
	 * 通过评价详情id来获取评价详情。不同的评价类型对应不同的返回。
	 * 1.跟团游  需要返回计调评分信息和游客反馈单信息
	 * 2.门店租借  只返回计调评分信息
	 * 3.导游考核  只返回公司考核信息
	 * @param id
	 * @return
	 */
	@RequestMapping("/detail/view")
	@ResponseBody
	public Json getReviewDetailById(Long id){
		Json json = new Json();
		try{
			HashMap<String, Object> result = new HashMap<>();
			GuideReviewDetail guideReviewDetail = guideReviewDetailService.find(id);	
			
			if(guideReviewDetail == null){
				json.setSuccess(false);
				json.setMsg("获取失败");
			}else{
				//封装共有数据
				List<HashMap<String, Object>> ans = new ArrayList<>();
				HashMap<String, Object> m = new HashMap<String, Object>();
				m.put("id", guideReviewDetail.getId());
				m.put("guideId", guideReviewDetail.getGuideId());
				m.put("guideSn", guideReviewDetail.getGuideSn());
				m.put("type", guideReviewDetail.getType());
				m.put("guideType", guideReviewDetail.getGuideType());
				m.put("groupId", guideReviewDetail.getGroupId());
				if(guideReviewDetail.getGroupId()!=null){
					HyGroup hyGroup = hyGroupService.find(guideReviewDetail.getGroupId());
					if(hyGroup!=null){
						HyLine hyline = hyGroup.getLine();
						m.put("pn",hyline.getPn());
					}	
				}
				if(guideReviewDetail.getOrderId()!=null){
					HyOrder order = hyOrderService.find(guideReviewDetail.getOrderId());
					if(order!=null){
						Store store = storeService.find(order.getStoreId());	
						m.put("storeName", store.getStoreName());
					}
				}
				m.put("orderId", guideReviewDetail.getOrderId());
				m.put("paiqianId", guideReviewDetail.getPaiqianId());
				m.put("lineSn", guideReviewDetail.getLineSn());
				m.put("startDate", guideReviewDetail.getStartDate());
				m.put("testDate", guideReviewDetail.getTestDate());
				m.put("content", guideReviewDetail.getContent());
				m.put("score", guideReviewDetail.getScore());
				m.put("starLevel", guideReviewDetail.getStarLevel());
				ans.add(m);
				
				result.put("reviewDetail", ans);
				int type = guideReviewDetail.getType();
				if(type == 0){
					//跟团游评价
					//需要根据导游id和团id来获取该团中的游客对该导游的反馈单
					
					List<Filter> filters = new ArrayList<>();
					filters.add(Filter.eq("guideId", guideReviewDetail.getGuideId()));
					List<Filter> filters2 = new ArrayList<>();
					filters2.add(Filter.eq("guideId", guideReviewDetail.getGuideId()));
					
					if(guideReviewDetail.getGroupId()!=null){
						filters.add(Filter.eq("groupId", guideReviewDetail.getGroupId()));
						filters2.add(Filter.eq("groupId", guideReviewDetail.getGroupId()));	
					}
					filters2.add(Filter.eq("reviewType",0));
					List<VisitorFeedback> visitorFeedbacks = visitorFeedbackService.findList(null,filters,null);
					List<GuideReviewForm> guideReviewForms = guideReviewFormService.findList(null,filters2,null);
					List<GuideReviewForm> reviewForm = new ArrayList<>();
					for(GuideReviewForm guideReviewForm:guideReviewForms) {
						reviewForm.add(guideReviewForm);
					}
					result.put("JiDiaoReview", reviewForm);
					
					List<HashMap<String, Object>> feedbacks = new ArrayList<>();
					//获取产品ID,因为此页中就是针对特定团的评价，特定的团只有一个唯一的产品ID，所以放在result里面，可以上下通用。
					if(guideReviewDetail.getGroupId()!=null){
						HyGroup hyGroup = hyGroupService.find(guideReviewDetail.getGroupId());
						if(hyGroup!=null){
							HyLine hyline = hyGroup.getLine();
							result.put("pn",hyline.getPn());
						}	
					}
					for(VisitorFeedback vf: visitorFeedbacks){
						HashMap<String, Object> tmp = new HashMap<>();
						//此处直接将所有的信息都返回给前端，展示的反馈单的时候就可以不访问服务器了
						tmp.put("id", vf.getId());
						tmp.put("groupId",vf.getGroupId());
						
						tmp.put("guideId", vf.getGuideId());
						//导游姓名
						tmp.put("guideName", vf.getGuideName());
						//开团日期
						tmp.put("startTime", vf.getStartTime());
				        //线路名称
						tmp.put("line", vf.getLine());
						//评分
						tmp.put("scoreTotal", vf.getScoreTotal());
						//评价人
						tmp.put("reviewer", vf.getReviewer());
						//评价时间
						tmp.put("reviewTime", vf.getReviewTime());
						tmp.put("phone",vf.getPhone() );
						tmp.put("title", vf.getTitle());
						tmp.put("content", vf.getContent());
						tmp.put("advice", vf.getAdvice());
						tmp.put("visitorfeedbackScores",vf.getVisitorFeedbackScores());
						feedbacks.add(tmp);
					}
					result.put("feedbacks", feedbacks);
				}
				json.setSuccess(true);
				json.setMsg("获取成功");
				json.setObj(result);
			}
		}catch(Exception e){
			json.setSuccess(false);
			json.setMsg("获取错误: " + e.getMessage());
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
	@RequestMapping("/reviewForm/detail/view")
	@ResponseBody
	public Json getReviewFormById(Long id, Integer guideId ){
		Json json = new Json();
		try{
				
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
			if(guideReviewForm == null){
				json.setSuccess(false);
				json.setMsg("获取失败");
			}else{
				json.setSuccess(true);
				json.setMsg("获取成功");
				json.setObj(guideReviewForm);
			}
		}catch(Exception e){
			json.setSuccess(false);
			json.setMsg("获取错误: " + e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
	
	/**
	 * 根据游客反馈单的Id获取游客反馈单详情。
	 * @param id
	 * @return
	 */
	@RequestMapping("/visitorFeedback/detail/view")
	@ResponseBody
	public Json getVisitorFeedbackDetailById(Long id){
		Json json = new Json();
		try{
			VisitorFeedback visitorFeedback = visitorFeedbackService.find(id);
			HashMap<String, Object> result = new HashMap<>();
			result.put("guideName", visitorFeedback.getGuideName());
			result.put("title", visitorFeedback.getTitle());
			result.put("content", visitorFeedback.getContent());
			result.put("phone",visitorFeedback.getPhone());
			result.put("line", visitorFeedback.getLine());
			result.put("reviewer", visitorFeedback.getReviewer());
			result.put("reviewTime", visitorFeedback.getReviewTime());
			result.put("visitorFeedbackScores", visitorFeedback.getVisitorFeedbackScores());
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(result);
		}catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取错误"+e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
	/*
	*//**
	 * 根据导游ID返回带有分页的所有评价详情单
	 * @param pageable
	 * @param id	导游id
	 * @param session
	 * @param request
	 * @return
	 *//*
	@RequestMapping("/list/view")
	@ResponseBody
	public Json getReviewListByGuideID(Pageable pageable, Integer id, HttpSession session, HttpServletRequest request) {
		Json json = new Json();
		try {
			HashMap<String, Object> hm = new HashMap<String, Object>();
			List<HashMap<String, Object>> result = new ArrayList<>();
			
			List<Order> orders = new ArrayList<Order>();
			Order order = Order.desc("id");
			orders.add(order);
			
			List<Filter> filters = new ArrayList<Filter>();
			Filter filter = Filter.eq("guideId", id);
			filters.add(filter);
			
			pageable.setFilters(filters);
			pageable.setOrders(orders);
			Page<GuideReviewDetail> page = guideReviewDetailService.findPage(pageable);
			for (GuideReviewDetail tmp : page.getRows()) {
				HashMap<String, Object> m = new HashMap<String, Object>();
				
				 * 此处根据前端需求重新封装数据
				 * 暂时没有和前端沟通，可以不写。
				 
				m.put("id", tmp.getId());
				m.put("guideId", tmp.getGuideId());
				m.put("guideSn", tmp.getGuideSn());
				m.put("type", tmp.getType());
				m.put("guideType", tmp.getGuideType());
				m.put("groupId", tmp.getGroupId());
				m.put("orderId", tmp.getOrderId());
				m.put("paiqianId", tmp.getPaiqianId());
				m.put("lineSn", tmp.getLineSn());
				m.put("startDate", tmp.getStartDate());
				m.put("testDate", tmp.getTestDate());
				m.put("content", tmp.getContent());
				m.put("score", tmp.getScore());
				m.put("starLevel", tmp.getStarLevel());
				result.add(m);
			}
			hm.put("total", page.getTotal());
			hm.put("pageNumber", page.getPageNumber());
			hm.put("pageSize", page.getPageSize());
			hm.put("rows", result);
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
	
	*//**
	 * 通过上边方法获取到评价列表之后，就可以拿到每个评价的id
	 * 根据这个id就可以获取到评价的详细情况
	 * 在此处没有做用户权限判断是因为在获取评价列表的时候已经做了权限限制。
	 * @param id
	 * @return  指定id的评价详情
	 *//*
	@RequestMapping("/detail/view")
	@ResponseBody
	public Json getReviewDetailById(Long id){
		Json json = new Json();
		try{
			GuideReviewDetail guideReviewDetail = guideReviewDetailService.find(id);
			if(guideReviewDetail == null){
				json.setSuccess(false);
				json.setMsg("获取失败");
			}else{
				json.setSuccess(true);
				json.setMsg("获取成功");
				json.setObj(guideReviewDetail);
			}
		}catch(Exception e){
			json.setSuccess(false);
			json.setMsg("获取错误: " + e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
	
	*//**
	 * 根据团id和导游id来查询评价表
	 * @param id	团id
	 * @param guideId	导游id
	 * @return	按道理这两个id应该唯一确定一个评价表
	 *//*
	@RequestMapping("/reviewForm/list/view")
	@ResponseBody
	public Json getReviewFormById(Long id, Integer guideId ){
		Json json = new Json();
		try{
				
			
			 * 根据属性创建过滤器列表，筛选特定实体
			 
			List<Filter> filters = new ArrayList<>();
			Filter filter1 = Filter.eq("groupId",id);
			filters.add(filter1);
			Filter filter2 = Filter.eq("guideId",guideId);
			filters.add(filter2);
			
			 * 按照创建日期排序
			 
			List<Order> orders = new ArrayList<Order>();
			Order order = Order.desc("id");
			orders.add(order);
			
			 * 获取在此过滤情况下所有的实体数
			 
			List<GuideReviewForm> guideReviewFormList = 
					guideReviewFormService.findList(null,filters,orders);
			GuideReviewForm guideReviewForm = null;
			
			 * 如果list为空,说明没有该实体
			 * 如果list不为空，将第一个实体返回
			 
			if(guideReviewFormList.size() > 0){
				guideReviewForm = guideReviewFormList.get(0);
			}
			for(GuideReviewForm tmp: guideReviewFormList){
				if(tmp.getGuideId().equals(guideID)){
					guideReviewForm = tmp;
				}
			}
			if(guideReviewForm == null){
				json.setSuccess(false);
				json.setMsg("获取失败");
			}else{
				json.setSuccess(true);
				json.setMsg("获取成功");
				json.setObj(guideReviewForm);
			}
		}catch(Exception e){
			json.setSuccess(false);
			json.setMsg("获取错误: " + e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
	
	*//**
	 * 根据前端传的groupId和guideId来获取带有分页的反馈单列表
	 * @param pageable
	 * @param id	团id
	 * @param guideId	导游id
	 * @param session
	 * @param request
	 * @return
	 *//*
	@RequestMapping("/visitorFeedback/list/view")
	@ResponseBody
	public Json getVisitorFeedbackListByID(Pageable pageable, Long id, Integer guideId, HttpSession session, HttpServletRequest request) {
		Json json = new Json();
		try {
			HashMap<String, Object> hm = new HashMap<String, Object>();
			List<HashMap<String, Object>> result = new ArrayList<>();
			
			*//** 将数据按照时间排序 *//*
			List<Order> orders = new ArrayList<Order>();
			Order order = Order.desc("id");
			orders.add(order);

			*//** 数据按照条件筛选 *//*
			List<Filter> filters = new ArrayList<Filter>();
			Filter filter1 = Filter.eq("groupId",id);
			filters.add(filter1);
			Filter filter2 = Filter.eq("guideId",guideId);
			filters.add(filter2);

			pageable.setFilters(filters);
			pageable.setOrders(orders);
			Page<VisitorFeedback> page = visitorFeedbackService.findPage(pageable);
			for (VisitorFeedback tmp : page.getRows()) {
				HashMap<String, Object> m = new HashMap<String, Object>();
				
				 * 此处根据前端需求重新封装数据
				 * 暂时没有和前端沟通，可以不写。
				 
				m.put("id", tmp.getId());
				m.put("groupId", tmp.getGroupId());
				m.put("paiqianId", tmp.getPaiqianId());
				m.put("guideId",tmp.getGuideId() );
				m.put("guideName",tmp.getGuideName() );
				m.put("startTime",tmp.getStartTime() );
				m.put("line", tmp.getLine());
				m.put("reviewer", tmp.getReviewer());
				m.put("advice",tmp.getAdvice() );
				m.put("reviewTime",tmp.getReviewTime() );
				m.put("scoreTotal",tmp.getScoreTotal() );
				m.put("phone",tmp.getPhone() );
				m.put("title", tmp.getTitle());
				m.put("content", tmp.getContent());
				m.put("visitorFeedbackScores",tmp.getVisitorFeedbackScores());
				result.add(m);
			}
			hm.put("total", page.getTotal());
			hm.put("pageNumber", page.getPageNumber());
			hm.put("pageSize", page.getPageSize());
			hm.put("rows", result);
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(hm);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取失败: " + e.getMessage());
			e.printStackTrace();
		}

		return json;
	}*/
}
