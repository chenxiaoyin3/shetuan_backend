package com.hongyu.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
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
import com.hongyu.controller.LinePromotionController.PromitionWrapper;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyDepartmentModel;
import com.hongyu.entity.HyGroup;
import com.hongyu.entity.HyHotelroom;
import com.hongyu.entity.HyLine;
import com.hongyu.entity.HyPromotionActivity;
import com.hongyu.entity.HySubscribeTicket;
import com.hongyu.entity.HySupplier;
import com.hongyu.entity.HyTicketHotel;
import com.hongyu.entity.HyTicketHotelRoom;
import com.hongyu.entity.HyTicketHotelandscene;
import com.hongyu.entity.HyTicketScene;
import com.hongyu.entity.HyTicketSceneTicketManagement;
import com.hongyu.entity.HyTicketSubscribe;
import com.hongyu.entity.HyVisa;
import com.hongyu.entity.LinePromotion;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyPromotionActivityService;
import com.hongyu.service.HySupplierService;
import com.hongyu.service.HyTicketHotelRoomService;
import com.hongyu.service.HyTicketHotelService;
import com.hongyu.service.HyTicketHotelandsceneService;
import com.hongyu.service.HyTicketSceneService;
import com.hongyu.service.HyTicketSceneTicketManagementService;
import com.hongyu.service.HyTicketSubscribeService;
import com.hongyu.service.HyVisaService;
import com.hongyu.util.Constants;
import com.hongyu.util.DateUtil;
import com.hongyu.util.Constants.AuditStatus;

@Controller
@RequestMapping("/admin/promotionactivity")
public class PromotionActivityController {
	
	@Resource(name="hyPromotionActivityServiceImpl")
	HyPromotionActivityService hyPromotionActivityServiceImpl;
	
	@Resource(name="hyAdminServiceImpl")
	private HyAdminService hyAdminService;
	
	@Resource(name="hyTicketHotelRoomServiceImpl")
	HyTicketHotelRoomService hyTicketHotelRoomServiceImpl;
	
	@Resource(name="hyTicketSceneTicketManagementServiceImpl")
	HyTicketSceneTicketManagementService hyTicketSceneTicketManagementServiceImpl;
	
	@Resource(name="hyTicketHotelandsceneServiceImpl")
	HyTicketHotelandsceneService hyTicketHotelandsceneServiceImpl;
	
	@Resource(name="hyTicketSubscribeServiceImpl")
	HyTicketSubscribeService hyTicketSubscribeServiceImpl;
	
	@Resource(name="hyVisaServiceImpl")
	HyVisaService hyVisaServiceImpl;
	
	@Resource(name="hySupplierServiceImpl")
	private HySupplierService hySupplierService;
	
	@Resource(name = "departmentServiceImpl")
	DepartmentService departmentServiceImpl;
	
	@Resource(name = "hyTicketHotelServiceImpl")
	HyTicketHotelService hyTicketHotelService;
	
	@Resource(name = "hyTicketSceneServiceImpl")
	HyTicketSceneService hyTicketSceneServiceImpl;
	
	@Resource
	TaskService taskService;
	
	@Resource
	private HistoryService historyService;
	
	@Resource
	RuntimeService runtimeService;
	
	public static class PromitionWrapper {
		public String name;
		@DateTimeFormat(iso=ISO.DATE_TIME)
		public Date startDate;
		@DateTimeFormat(iso=ISO.DATE_TIME)
		public Date endDate;
		public Integer promotionType;
		public BigDecimal manjianPrice1;
		public BigDecimal manjianPrice2;
		public BigDecimal dazhe;
		public BigDecimal meirenjian;
		public String remark;
		public Boolean isCaigouti;
		private Integer activityType;
		public Long[] ids;
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Date getStartDate() {
			return startDate;
		}
		public void setStartDate(Date startDate) {
			this.startDate = startDate;
		}
		public Date getEndDate() {
			return endDate;
		}
		public void setEndDate(Date endDate) {
			this.endDate = endDate;
		}
		public Integer getPromotionType() {
			return promotionType;
		}
		public void setPromotionType(Integer promotionType) {
			this.promotionType = promotionType;
		}
		public BigDecimal getManjianPrice1() {
			return manjianPrice1;
		}
		public void setManjianPrice1(BigDecimal manjianPrice1) {
			this.manjianPrice1 = manjianPrice1;
		}
		public BigDecimal getManjianPrice2() {
			return manjianPrice2;
		}
		public void setManjianPrice2(BigDecimal manjianPrice2) {
			this.manjianPrice2 = manjianPrice2;
		}
		public BigDecimal getDazhe() {
			return dazhe;
		}
		public void setDazhe(BigDecimal dazhe) {
			this.dazhe = dazhe;
		}
		public String getRemark() {
			return remark;
		}
		public void setRemark(String remark) {
			this.remark = remark;
		}
		public BigDecimal getMeirenjian() {
			return meirenjian;
		}
		public void setMeirenjian(BigDecimal meirenjian) {
			this.meirenjian = meirenjian;
		}
		public Boolean getIsCaigouti() {
			return isCaigouti;
		}
		public void setIsCaigouti(Boolean isCaigouti) {
			this.isCaigouti = isCaigouti;
		}
		public Integer getActivityType() {
			return activityType;
		}
		public void setActivityType(Integer activityType) {
			this.activityType = activityType;
		}
		public Long[] getIds() {
			return ids;
		}
		public void setIds(Long[] ids) {
			this.ids = ids;
		}
	}
	
	@RequestMapping("/add")
	@ResponseBody
	public Json promotionAdd(@RequestBody PromitionWrapper wrapper, HttpSession session, HttpServletRequest request) {
		Json j = new Json();
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin admin = hyAdminService.find(username);
		try {
			Set<String> operators = new HashSet<>();
			HashMap<String, Object> map = new HashMap<>();
			
			// 根绝activtyType划分，同时可能会产生多个促销，因为需要将促销按照计调人划分
			if (wrapper.getActivityType()!=null) {
				int activityType = wrapper.getActivityType();
				switch (activityType) {
				case 0: {  //门票
					List<HyTicketSceneTicketManagement> list = hyTicketSceneTicketManagementServiceImpl.findList(wrapper.getIds());
//					activity.getTicketScenes().addAll(list);
					for (HyTicketSceneTicketManagement management : list) {
						operators.add(management.getOperator().getUsername());
					}
					
					for (String operator : operators) {
						HyPromotionActivity activity = new HyPromotionActivity();
						BeanUtils.copyProperties(wrapper, activity);
						for (HyTicketSceneTicketManagement management : list) {
							if (management.getOperator().getUsername().equals(operator)) {
								activity.getTicketScenes().add(management);
								management.setHyPromotionActivity(activity);
							}
						}
						HyAdmin jidiao = hyAdminService.find(operator);
						activity.setJidiao(jidiao);
						activity.setApplyName(admin);
						
						//启动工作流程
						ProcessInstance pi= runtimeService.startProcessInstanceByKey("LinePromotion"); //"LinePromotion"为bpmn文件中的key
						activity.setProcessInstanceId(pi.getProcessInstanceId());
						// 根据流程实例Id获取任务
						Task task=taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
						
						hyPromotionActivityServiceImpl.save(activity);
						Department department=admin.getDepartment();
						//如果是总公司采购部提交审核,且提交审核人和计调不是同一个人,由计调审核
						if(department.getHyDepartmentModel().getName().equals("总公司采购部") && 
								!activity.getJidiao().getUsername().equals(activity.getApplyName().getUsername())) {
							map.put("auditor", "jidiao"); //向监听器传递参数
							//将计调传进监听器
							map.put("operator",operator);
						}
						//其他提交审核,由总公司品控部审核
						else {
							map.put("auditor", "pinkong"); //向监听器传递参数
						}
						// 完成任务		
						taskService.complete(task.getId(),map);
					}
					break;
				}
				case 1: {   // 酒店
					List<HyTicketHotelRoom> list = hyTicketHotelRoomServiceImpl.findList(wrapper.getIds());
					
					for (HyTicketHotelRoom room : list) {
						operators.add(room.getHyTicketHotel().getCreator().getUsername());
					}
					for (String operator : operators) {
						HyPromotionActivity activity = new HyPromotionActivity();
						BeanUtils.copyProperties(wrapper, activity);
						
						for (HyTicketHotelRoom room : list) {
							if (room.getHyTicketHotel().getCreator().getUsername().equals(operator)) {
								activity.getRooms().add(room);
								room.setPromotionActivity(activity);
							}
						}
						
						HyAdmin jidiao = hyAdminService.find(operator);
						activity.setJidiao(jidiao);
						activity.setApplyName(admin);
						
						//启动工作流程
						ProcessInstance pi= runtimeService.startProcessInstanceByKey("LinePromotion"); //"LinePromotion"为bpmn文件中的key
						activity.setProcessInstanceId(pi.getProcessInstanceId());
						// 根据流程实例Id获取任务
						Task task=taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
						
						hyPromotionActivityServiceImpl.save(activity);
						
						Department department=admin.getDepartment();
						//如果是总公司采购部提交审核,且提交审核人和计调不是同一个人,由计调审核
						if(department.getHyDepartmentModel().getName().equals("总公司采购部") && 
								!activity.getJidiao().getUsername().equals(activity.getApplyName().getUsername())) {
							map.put("auditor", "jidiao"); //向监听器传递参数
							//将计调传进监听器
							map.put("operator",operator);
						}
						//其他提交审核,由总公司品控部审核
						else {
							map.put("auditor", "pinkong"); //向监听器传递参数
						}
						
						// 完成任务		
						taskService.complete(task.getId(),map);
					}
					break;
				}
				case 2: {   // 酒+景
					List<HyTicketHotelandscene> list = hyTicketHotelandsceneServiceImpl.findList(wrapper.getIds());
					for (HyTicketHotelandscene hotelandscene : list) {
						operators.add(hotelandscene.getCreator().getUsername());
					}
					
					
					
					for (String operator : operators) {
						HyPromotionActivity activity = new HyPromotionActivity();
						BeanUtils.copyProperties(wrapper, activity);
						
						for (HyTicketHotelandscene hotelandscene : list) {
							if (hotelandscene.getCreator().getUsername().equals(operator)) {
								activity.getHotelAndScenes().add(hotelandscene);
								hotelandscene.setHyPromotionActivity(activity);
							}
						}
						
						HyAdmin jidiao = hyAdminService.find(operator);
						activity.setJidiao(jidiao);
						activity.setApplyName(admin);
						
						//启动工作流程
						ProcessInstance pi= runtimeService.startProcessInstanceByKey("LinePromotion"); //"LinePromotion"为bpmn文件中的key
						activity.setProcessInstanceId(pi.getProcessInstanceId());
						// 根据流程实例Id获取任务
						Task task=taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
						
						hyPromotionActivityServiceImpl.save(activity);
						
						Department department=admin.getDepartment();
						//如果是总公司采购部提交审核,且提交审核人和计调不是同一个人,由计调审核
						if(department.getHyDepartmentModel().getName().equals("总公司采购部") && 
								!activity.getJidiao().getUsername().equals(activity.getApplyName().getUsername())) {
							map.put("auditor", "jidiao"); //向监听器传递参数
							//将计调传进监听器
							map.put("operator",operator);
						}
						//其他提交审核,由总公司品控部审核
						else {
							map.put("auditor", "pinkong"); //向监听器传递参数
						}
						
						// 完成任务		
						taskService.complete(task.getId(),map);
					}
					break;
				}
				case 3: {   // 认购门票
					List<HyTicketSubscribe> list = hyTicketSubscribeServiceImpl.findList(wrapper.getIds());
					for (HyTicketSubscribe ticketSubscribe : list) {
						operators.add(ticketSubscribe.getCreator().getUsername());
					}
					
					for (String operator : operators) {
						HyPromotionActivity activity = new HyPromotionActivity();
						BeanUtils.copyProperties(wrapper, activity);
						
						for (HyTicketSubscribe ticketSubscribe : list) {
							if (ticketSubscribe.getCreator().getUsername().equals(operator)) {
								activity.getTicketSubscribes().add(ticketSubscribe);
								// 缺少一行在ticketSubscribe上设置activity
								// !!!!
								ticketSubscribe.setHyPromotionActivity(activity);
								
							}
						}
						
						HyAdmin jidiao = hyAdminService.find(operator);
						activity.setJidiao(jidiao);
						activity.setApplyName(admin);
						
						//启动工作流程
						ProcessInstance pi= runtimeService.startProcessInstanceByKey("LinePromotion"); //"LinePromotion"为bpmn文件中的key
						activity.setProcessInstanceId(pi.getProcessInstanceId());
						// 根据流程实例Id获取任务
						Task task=taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
						
						hyPromotionActivityServiceImpl.save(activity);
						
						Department department=admin.getDepartment();
						//如果是总公司采购部提交审核,且提交审核人和计调不是同一个人,由计调审核
						if(department.getHyDepartmentModel().getName().equals("总公司采购部") && 
								!activity.getJidiao().getUsername().equals(activity.getApplyName().getUsername())) {
							map.put("auditor", "jidiao"); //向监听器传递参数
							//将计调传进监听器
							map.put("operator",operator);
						}
						//其他提交审核,由总公司品控部审核
						else {
							map.put("auditor", "pinkong"); //向监听器传递参数
						}
						
						// 完成任务		
						taskService.complete(task.getId(),map);
					}
					
					break;
				}
				case 4: {   // 签证
					List<HyVisa> list = hyVisaServiceImpl.findList(wrapper.getIds());
					for (HyVisa visa : list) {
						operators.add(visa.getCreator().getUsername());
					}
					
					for (String operator : operators) {
						HyPromotionActivity activity = new HyPromotionActivity();
						BeanUtils.copyProperties(wrapper, activity);
						
						for (HyVisa visa : list) {
							if (visa.getCreator().getUsername().equals(operator)) {
								activity.getVisas().add(visa);
								// 缺少一行在ticketSubscribe上设置activity
								// !!!!
								
								visa.setHyPromotionActivity(activity);
							}
						}
						
						HyAdmin jidiao = hyAdminService.find(operator);
						activity.setJidiao(jidiao);
						activity.setApplyName(admin);
						
						//启动工作流程
						ProcessInstance pi= runtimeService.startProcessInstanceByKey("LinePromotion"); //"LinePromotion"为bpmn文件中的key
						activity.setProcessInstanceId(pi.getProcessInstanceId());
						// 根据流程实例Id获取任务
						Task task=taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
						
						hyPromotionActivityServiceImpl.save(activity);
						
						Department department=admin.getDepartment();
						//如果是总公司采购部提交审核,且提交审核人和计调不是同一个人,由计调审核
						if(department.getHyDepartmentModel().getName().equals("总公司采购部") && 
								!activity.getJidiao().getUsername().equals(activity.getApplyName().getUsername())) {
							map.put("auditor", "jidiao"); //向监听器传递参数
							//将计调传进监听器
							map.put("operator",operator);
						}
						//其他提交审核,由总公司品控部审核
						else {
							map.put("auditor", "pinkong"); //向监听器传递参数
						}
						
						// 完成任务		
						taskService.complete(task.getId(),map);
					}
					break;
				} 
				default: {
					j.setSuccess(false);
					j.setMsg("促销活动类型输入错误!");
					return j;
				}
				}
					
						
			} else {
				j.setSuccess(false);
				j.setMsg("缺少促销活动类型!");
				return j;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			j.setSuccess(false);
			j.setMsg("促销创建失败!");
			return j;
		}
		
		
		
		j.setSuccess(true);
		j.setMsg("促销创建成功!");
		return j;
	}
	
	/**
	 * 促销审核
	 * @param wrapper
	 * @param session
	 * @param request
	 * @return
	 */
	@RequestMapping("/audit")
	@ResponseBody
	public Json promotionActivityAudit(Long id, String comment, Boolean isApproved, HttpSession session, HttpServletRequest request) {
		Json json = new Json();
		
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin admin = hyAdminService.find(username);
		
		try {
			HyPromotionActivity activity = hyPromotionActivityServiceImpl.find(id);
			if (activity == null) {
				json.setSuccess(false);
				json.setMsg("审核失败，不存在指定的线路促销");
				json.setObj(null);
				return json;
			}
			
			Task task=taskService.createTaskQuery().processInstanceId(activity.getProcessInstanceId()).singleResult();
			if (!isApproved) {
				Authentication.setAuthenticatedUserId(username); 
				taskService.addComment(task.getId(), activity.getProcessInstanceId(), comment + ":" + "驳回");
				activity.setState(Constants.PROMOTION_ACTIVITY_STATUS_FAIL);
			} else {
				Authentication.setAuthenticatedUserId(username); 
				taskService.addComment(task.getId(), activity.getProcessInstanceId(), "同意:通过");
				activity.setState(Constants.PROMOTION_ACTIVITY_STATUS_PASS);
			}
			
			if (!isApproved) {
					int activityType = activity.getActivityType();
					
					switch (activityType) {
					case 0: {
						for (HyTicketSceneTicketManagement scene : activity.getTicketScenes()) {
							scene.setHyPromotionActivity(null);
							hyTicketSceneTicketManagementServiceImpl.update(scene);
						}
						
						break;
					}
					case 1: {
						for (HyTicketHotelRoom room : activity.getRooms()) {
							room.setPromotionActivity(null);
							hyTicketHotelRoomServiceImpl.update(room);
						}
						
						break;
					}
					case 2: {
						for (HyTicketHotelandscene hotelandscene : activity.getHotelAndScenes()) {
							hotelandscene.setHyPromotionActivity(null);
							
							hyTicketHotelandsceneServiceImpl.update(hotelandscene);
						}
						
						break;
					}
					case 3: {
						for (HyTicketSubscribe subscribe : activity.getTicketSubscribes()) {
							subscribe.setHyPromotionActivity(null);
							hyTicketSubscribeServiceImpl.update(subscribe);
						}
						
						break;
					}
					case 4: {
						for (HyVisa visa : activity.getVisas()) {
							visa.setHyPromotionActivity(null);
							
							hyVisaServiceImpl.update(visa);
						}
						
						break;
					}
					default: {
						break;
					}
						
					}
			}
			activity.setAuditor(admin);
			activity.setAuditTime(new Date());
			taskService.complete(task.getId());
			hyPromotionActivityServiceImpl.update(activity);
			
			json.setSuccess(true);
			json.setMsg("审核成功");
			json.setObj(null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return json;
	}
	

	/**
	 * 促销详情
	 * @param id
	 * @param session
	 * @param request
	 * @return
	 */
	@RequestMapping("/detail/view")
	@ResponseBody
	public Json promotionActicityDetail(Long id, HttpSession session, HttpServletRequest request) {
		Json j = new Json();
		
		HyPromotionActivity activity = hyPromotionActivityServiceImpl.find(id);
		
		if (activity == null) {
			j.setSuccess(false);
			j.setMsg("不存在指定的线路产品");
			j.setObj(null);
			return j;
		}
		
		Map<String, Object> map = new HashMap<>();
		map.put("id", activity.getId());
		map.put("name", activity.getName());
		map.put("startDate", activity.getStartDate());
		map.put("endDate", activity.getEndDate());
		map.put("promotionType", activity.getPromotionType());
		map.put("manjianPrice1", activity.getManjianPrice1());
		map.put("manjianPrice2", activity.getManjianPrice2());
		map.put("dazhe", activity.getDazhe());
		map.put("meirenjian", activity.getMeirenjian());
		map.put("state", activity.getState());
		map.put("applyName", activity.getApplyName().getName());
		map.put("applyTime", activity.getApplyTime());
		map.put("remark", activity.getRemark());
		map.put("activityType", activity.getActivityType());
		
		List<Map<String, Object>> items = new ArrayList<>();
		switch (activity.getActivityType()) {
		case 0: {  //门票
			Map<Long, HyTicketScene> scenes = new HashMap<>();
			for (HyTicketSceneTicketManagement ticketManagement : activity.getTicketScenes()) {
				scenes.putIfAbsent(ticketManagement.getHyTicketScene().getId(), ticketManagement.getHyTicketScene());
			}
			
			Set<Entry<Long, HyTicketScene>> entries = scenes.entrySet();
			for (Entry<Long, HyTicketScene> entry : entries) {
				Map<String, Object> item = new HashMap<>();
				List<Map<String, Object>> subitems = new ArrayList<>();
				item.put("jidiao", entry.getValue().getCreator().getName());
				item.put("sceneName", entry.getValue().getSceneName());
				item.put("scenePn", entry.getValue().getPn());
				for (HyTicketSceneTicketManagement ticketManagement : activity.getTicketScenes()) {
					if (ticketManagement.getHyTicketScene().getId().equals(entry.getKey())) {
						Map<String, Object> subItem = new HashMap<>();
						subItem.put("ticketId", ticketManagement.getId());
						subItem.put("ticketType", ticketManagement.getTicketType());
						subItem.put("productName", ticketManagement.getProductName());
						subitems.add(subItem);
					}
				}
				item.put("tickets", subitems);
				items.add(item);
			}
			break;
		}
		case 1: {   // 酒店
			Map<Long, HyTicketHotel> hotels = new HashMap<>();
			for (HyTicketHotelRoom room : activity.getRooms()) {
				hotels.putIfAbsent(room.getHyTicketHotel().getId(), room.getHyTicketHotel());
			}
			
			Set<Entry<Long, HyTicketHotel>> entries = hotels.entrySet();
			for (Entry<Long, HyTicketHotel> entry : entries) {
				Map<String, Object> item = new HashMap<>();
				List<Map<String, Object>> subitems = new ArrayList<>();
				item.put("jidiao", entry.getValue().getCreator().getName());
				item.put("hotelName", entry.getValue().getHotelName());
				item.put("hotelPn", entry.getValue().getPn());
				for (HyTicketHotelRoom room : activity.getRooms()) {
					if (room.getHyTicketHotel().getId().equals(entry.getKey())) {
						Map<String, Object> subItem = new HashMap<>();
						subItem.put("roomId", room.getId());
						subItem.put("roomType", room.getRoomType());
						subItem.put("productId", room.getProductId());
						subItem.put("productName", room.getProductName());
						subitems.add(subItem);
					}
				}
				item.put("rooms", subitems);
				items.add(item);
			}
			
			break;
		}
		case 2: {   // 酒+景
			for (HyTicketHotelandscene ticketandscene : activity.getHotelAndScenes()) {
				Map<String, Object> item = new HashMap<>();
				item.put("productId", ticketandscene.getId());
				item.put("productSN", ticketandscene.getProductId());
				item.put("productName", ticketandscene.getProductName());
				item.put("sceneName", ticketandscene.getSceneName());
				item.put("hotelName", ticketandscene.getHotelName());
				item.put("jidiao", ticketandscene.getCreator().getName());
				items.add(item);
			}
			break;
		}
		case 3: {   // 认购门票
			for (HyTicketSubscribe subscribe : activity.getTicketSubscribes()) {
				Map<String, Object> item = new HashMap<>();
				item.put("productId", subscribe.getId());
				item.put("productSN", subscribe.getProductId());
				item.put("sceneName", subscribe.getSceneName());
				item.put("jidiao", subscribe.getCreator().getName());
				items.add(item);
			}
			break;
		}
		case 4: {   // 签证
			
			for (HyVisa visa : activity.getVisas()) {
				Map<String, Object> item = new HashMap<>();
				item.put("productId", visa.getId());
				item.put("productSN", visa.getProductId());
				item.put("productName", visa.getProductName());
				item.put("jidiao", visa.getCreator().getName());
				items.add(item);
			}
			break;
		} 
		default: {
			j.setSuccess(false);
			j.setMsg("促销活动类型输入错误!");
			return j;
		}
		}
		
		map.put("items", items);
		j.setSuccess(true);
		j.setMsg("查看成功!");
		j.setObj(map);
		
		return j;
	}
	
	//只能看自己创建的
	@RequestMapping("/page/view")
	@ResponseBody
	public Json getPromotionPage(HyPromotionActivity query, Pageable pageable, HttpSession session, HttpServletRequest request) {
		Json json = new Json();
		List<Filter> filters = new ArrayList<>();
		
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin admin = hyAdminService.find(username);
		filters.add(Filter.eq("applyName", admin));
		
		if (query != null) {
			if (query.getState() != null) {
				filters.add(Filter.eq("state", query.getState()));
			}
		}
		List<Order> orders = new ArrayList<>();
		orders.add(Order.desc("id"));
		pageable.setOrders(orders);
		pageable.setFilters(filters);
		try {
			Page<HyPromotionActivity> page = hyPromotionActivityServiceImpl.findPage(pageable, query);
			List<Map<String, Object>> maps = new ArrayList<>();
			for (HyPromotionActivity promotion : page.getRows()) {
				Map<String, Object> map = new HashMap<>();
				map.put("id", promotion.getId());
				map.put("name", promotion.getName());
				map.put("startDate", promotion.getStartDate());
				map.put("endDate", promotion.getEndDate());
				map.put("state", promotion.getState());
				maps.add(map);
			}
			Page<Map<String, Object>> result = new Page<>(maps, page.getTotal(), pageable);
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(result);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(e);
			e.printStackTrace();
		}
		
		return json;
	}
	
	//查询审核列表
	@RequestMapping("/auditpage/view")
	@ResponseBody
	public Json getPromotionAuditPage(HyPromotionActivity query, Pageable pageable, HttpSession session, HttpServletRequest request) {
		Json json = new Json();
		List<Filter> filters = new ArrayList<>();
		
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin admin = hyAdminService.find(username);
		
		try {
			Page<HyPromotionActivity> page = hyPromotionActivityServiceImpl.findAuditPage(admin, pageable, query);
			
			List<Map<String, Object>> maps = new ArrayList<>();
			for (HyPromotionActivity promotion : page.getRows()) {
				Map<String, Object> map = new HashMap<>();
				map.put("id", promotion.getId());
				map.put("name", promotion.getName());
				map.put("startDate", promotion.getStartDate());
				map.put("endDate", promotion.getEndDate());
				map.put("state", promotion.getState());
				maps.add(map);
			}
			Page<Map<String, Object>> result = new Page<>(maps, page.getTotal(), pageable);
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(result);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(e);
			e.printStackTrace();
		}
		
		
		return json;
	}
	
	@RequestMapping("/cancel")
	@ResponseBody
	public Json promotionCancel(Long id, HttpSession session, HttpServletRequest request) {
		Json json = new Json();
		
		
		try {
			
			HyPromotionActivity promotion = hyPromotionActivityServiceImpl.find(id);
			if (promotion == null) {
				json.setSuccess(false);
				json.setMsg("不存在指定的线路促销");
				json.setObj(null);
				return json;
			}
		
			promotion.setState(Constants.PROMOTION_ACTIVITY_STATUS_CANCLED);
			int activityType = promotion.getActivityType();
			
			switch (activityType) {
			case 0: {
				for (HyTicketSceneTicketManagement scene : promotion.getTicketScenes()) {
					scene.setHyPromotionActivity(null);
					hyTicketSceneTicketManagementServiceImpl.update(scene);
				}
				
				hyPromotionActivityServiceImpl.update(promotion);
				break;
			}
			case 1: {
				for (HyTicketHotelRoom room : promotion.getRooms()) {
					room.setPromotionActivity(null);
					hyTicketHotelRoomServiceImpl.update(room);
				}
				
				hyPromotionActivityServiceImpl.update(promotion);
				break;
			}
			case 2: {
				for (HyTicketHotelandscene hotelandscene : promotion.getHotelAndScenes()) {
					hotelandscene.setHyPromotionActivity(null);
					
					hyTicketHotelandsceneServiceImpl.update(hotelandscene);
				}
				
				hyPromotionActivityServiceImpl.update(promotion);
				break;
			}
			case 3: {
				for (HyTicketSubscribe subscribe : promotion.getTicketSubscribes()) {
					subscribe.setHyPromotionActivity(null);
					hyTicketSubscribeServiceImpl.update(subscribe);
				}
				
				hyPromotionActivityServiceImpl.update(promotion);
				break;
			}
			case 4: {
				for (HyVisa visa : promotion.getVisas()) {
					visa.setHyPromotionActivity(null);
					
					hyVisaServiceImpl.update(visa);
				}
				
				hyPromotionActivityServiceImpl.update(promotion);
				break;
			}
			default: {
				break;
			}
				
			}
			

			
			json.setSuccess(true);
			json.setMsg("取消成功");
			json.setObj(null);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("取消失败");
			json.setObj(e);
			e.printStackTrace();
		}
		
		return json;
	}
	
	/**
	 * 促销审核意见
	 * @param wrapper
	 * @param session
	 * @param request
	 * @return
	 */
	@RequestMapping("/auditcomment/view")
	@ResponseBody
	public Json linePromotionAuditComment(Long id, HttpSession session, HttpServletRequest request) {
		Json json = new Json();
		
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin admin = hyAdminService.find(username);
		
		try {
			HyPromotionActivity promotion = hyPromotionActivityServiceImpl.find(id);
			if (promotion == null) {
				json.setSuccess(false);
				json.setMsg("审核失败，不存在指定的线路促销");
				json.setObj(null);
				return json;
			}
			
			if (promotion.getState() == Constants.PROMOTION_ACTIVITY_STATUS_AUDITING) {
				json.setSuccess(false);
				json.setMsg("线路促销尚未审核");
				json.setObj(null);
				return json;
			}
			
			List<Map<String, Object>> result = new ArrayList<>();
			String processInstanceId = promotion.getProcessInstanceId();
			List<Comment> commentList = taskService.getProcessInstanceComments(processInstanceId);
			Collections.reverse(commentList);
			for (Comment comment : commentList) {
				HashMap<String, Object> im = new HashMap<>();
				String taskId = comment.getTaskId();
				HistoricTaskInstance task = historyService.createHistoricTaskInstanceQuery().taskId(taskId)
						.singleResult();
				
				String step = task.getName();
				String auditorname = comment.getUserId();
				HyAdmin auditor = hyAdminService.find(auditorname);
				String com = comment.getFullMessage();
				String[] str = com.split(":");
				im.put("step", step);
				im.put("name", auditor.getName());
//				System.out.println(str[0]);
//				System.out.println(str[1]);
				im.put("comment", str[0]);
				im.put("result", str[1]);
				im.put("audittime", promotion.getAuditTime());
				result.add(im);
			}
			
			
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(result);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(e);
			e.printStackTrace();
		}
		
		return json;
	}
	
	/**
	 * 新建促销时获取酒+景产品列表
	 * @param pn
	 * @param isCaigouti
	 * @param session
	 * @param request
	 * @return
	 */
	@RequestMapping("/findhotelscenelist/view")
	@ResponseBody
	public Json findHotelAndScene(String pn, Boolean isCaigouti,HttpSession session, HttpServletRequest request) {
		Json json = new Json();
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin admin = hyAdminService.find(username);
		
		try {
			List<Filter> filters = new ArrayList<>();
	
			
			HyDepartmentModel d = admin.getDepartment().getHyDepartmentModel();
			//如果admin是总公司采购部员工
			if (d.getName().equals("总公司采购部")) {
				List<Filter> fs = new ArrayList<Filter>();
				fs.add(Filter.eq("isCaigouqian", true));
				fs.add(Filter.eq("isActive", true));
				fs.add(Filter.eq("supplierStatus", AuditStatus.pass));
				List<HySupplier> suppliers = hySupplierService.findList(null, fs, null);
				if (suppliers.isEmpty()) {
					json.setSuccess(true);
					json.setMsg("查询成功");
					json.setObj(new ArrayList<Map<String, Object>>());
					return json;
				} else {
					filters.add(Filter.in("ticketSupplier", suppliers));
				}
			}
			
			
			//此时为计调
			if (!d.getName().equals("总公司采购部")) {
				filters.add(Filter.eq("creator", admin));
			}
			
			List<Order> orders = new ArrayList<>();
			orders.add(Order.desc("id"));
			
			filters.add(Filter.isNull("hyPromotionActivity"));
			
			List<Map<String, Object>> result = new ArrayList<>();
			
			//因为需要合并查询结果，pn可以同时用于productId和productName
			if (StringUtils.isNotBlank(pn)) {
				Map<Long, Map<String, Object>> mapResult = new HashMap<>();
				filters.add(Filter.like("productId", pn));
				List<HyTicketHotelandscene> hotelandscenes1 = hyTicketHotelandsceneServiceImpl.findList(null, filters, orders);
				for (HyTicketHotelandscene hotelandscene : hotelandscenes1) {
					Map<String, Object> map = new HashMap<>();
					map.put("id", hotelandscene.getId());
					map.put("productId", hotelandscene.getProductId());
					map.put("productName", hotelandscene.getProductName());
					map.put("jidiao", hotelandscene.getCreator().getName());
					mapResult.putIfAbsent(hotelandscene.getId(), map);
				}
				
				filters.remove(filters.size()-1);
				filters.add(Filter.like("productName", pn));
				List<HyTicketHotelandscene> hotelandscenes2 = hyTicketHotelandsceneServiceImpl.findList(null, filters, orders);
				for (HyTicketHotelandscene hotelandscene : hotelandscenes2) {
					Map<String, Object> map = new HashMap<>();
					map.put("id", hotelandscene.getId());
					map.put("productId", hotelandscene.getProductId());
					map.put("productName", hotelandscene.getProductName());
					map.put("jidiao", hotelandscene.getCreator().getName());
					mapResult.putIfAbsent(hotelandscene.getId(), map);
				}
				
				result.addAll(mapResult.values());
				Collections.sort(result, new Comparator<Map<String, Object>>() {
				
								@Override
								public int compare(Map<String, Object> o1, Map<String, Object> o2) {
									if (((Long)o1.get("id")).compareTo((Long)o2.get("id")) > 0) {
										return 1;
									} else if (((Long)o1.get("id")).compareTo((Long)o2.get("id")) < 0) {
										return -1;
									} else {
										return 0;
									}
								}
							});
				
			} else {  //没有传pn值
				List<HyTicketHotelandscene> hotelandscenes = hyTicketHotelandsceneServiceImpl.findList(null, filters, orders);
				for (HyTicketHotelandscene hotelandscene : hotelandscenes) {
					Map<String, Object> map = new HashMap<>();
					map.put("id", hotelandscene.getId());
					map.put("productId", hotelandscene.getProductId());
					map.put("productName", hotelandscene.getProductName());
					map.put("jidiao", hotelandscene.getCreator().getName());
					result.add(map);
				}
			}
			
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(result);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(e);
			e.printStackTrace();
		}
		
		return json;
	}
	
	/**
	 * 新建促销时获取签证产品列表
	 * @param pn
	 * @param isCaigouti
	 * @param session
	 * @param request
	 * @return
	 */
	@RequestMapping("/findvisalist/view")
	@ResponseBody
	public Json findVisaList(String pn, Boolean isCaigouti,HttpSession session, HttpServletRequest request) {
		Json json = new Json();
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin admin = hyAdminService.find(username);
		
		try {
			List<Filter> filters = new ArrayList<>();
	
			
			HyDepartmentModel d = admin.getDepartment().getHyDepartmentModel();
			//如果admin是总公司采购部员工
			if (d.getName().equals("总公司采购部")) {
				List<Filter> fs = new ArrayList<Filter>();
				fs.add(Filter.eq("isCaigouqian", true));
				fs.add(Filter.eq("isActive", true));
				fs.add(Filter.eq("supplierStatus", AuditStatus.pass));
				List<HySupplier> suppliers = hySupplierService.findList(null, fs, null);
				if (suppliers.isEmpty()) {
					json.setSuccess(true);
					json.setMsg("查询成功");
					json.setObj(new ArrayList<Map<String, Object>>());
					return json;
				} else {
					filters.add(Filter.in("ticketSupplier", suppliers));
				}
			}
			
			//此时为计调
			if (!d.getName().equals("总公司采购部")) {
				filters.add(Filter.eq("creator", admin));
			}
			
			if (StringUtils.isNotBlank(pn)) {
				filters.add(Filter.like("productId", pn));
			}
			
			filters.add(Filter.isNull("hyPromotionActivity"));
			
			List<Order> orders = new ArrayList<>();
			orders.add(Order.desc("id"));
			
			List<Map<String, Object>> result = new ArrayList<>();		
			
			if (StringUtils.isNotBlank(pn)) {
				Map<Long, Map<String, Object>> mapResult = new HashMap<>();
				filters.add(Filter.like("productId", pn));
				List<HyVisa> visas1 = hyVisaServiceImpl.findList(null, filters, orders);
				for (HyVisa visa : visas1) {
					Map<String, Object> map = new HashMap<>();
					map.put("id", visa.getId());
					map.put("productId", visa.getProductId());
					map.put("productName", visa.getProductName());
					map.put("jidiao", visa.getCreator().getName());
					mapResult.putIfAbsent(visa.getId(), map);
				}

				
				filters.remove(filters.size()-1);
				filters.add(Filter.like("productName", pn));
				List<HyVisa> visas2 = hyVisaServiceImpl.findList(null, filters, orders);
				for (HyVisa visa : visas2) {
					Map<String, Object> map = new HashMap<>();
					map.put("id", visa.getId());
					map.put("productId", visa.getProductId());
					map.put("productName", visa.getProductName());
					map.put("jidiao", visa.getCreator().getName());
					mapResult.putIfAbsent(visa.getId(), map);
				}
				
				result.addAll(mapResult.values());
				Collections.sort(result, new Comparator<Map<String, Object>>() {
				
								@Override
								public int compare(Map<String, Object> o1, Map<String, Object> o2) {
									if (((Long)o1.get("id")).compareTo((Long)o2.get("id")) > 0) {
										return 1;
									} else if (((Long)o1.get("id")).compareTo((Long)o2.get("id")) < 0) {
										return -1;
									} else {
										return 0;
									}
								}
							});
				
			} else {  //没有传pn值
				List<HyVisa> visas2 = hyVisaServiceImpl.findList(null, filters, orders);
				for (HyVisa visa : visas2) {
					Map<String, Object> map = new HashMap<>();
					map.put("id", visa.getId());
					map.put("productId", visa.getProductId());
					map.put("productName", visa.getProductName());
					map.put("jidiao", visa.getCreator().getName());
					result.add(map);
				}
			}
			
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(result);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(e);
			e.printStackTrace();
		}
		
		return json;
	}
	
	/**
	 * 获取酒店列表
	 * @param hotelname
	 * @param pn
	 * @param isCaigouti
	 * @param session
	 * @param request
	 * @return
	 */
	@RequestMapping("/findhotellist/view")
	@ResponseBody
	public Json findHotel(String hotelname, String pn, Boolean isCaigouti,HttpSession session, HttpServletRequest request) {
		Json json = new Json();
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin admin = hyAdminService.find(username);
		
		try {
			List<Filter> filters = new ArrayList<>();
	
			
			HyDepartmentModel d = admin.getDepartment().getHyDepartmentModel();
			//如果admin是总公司采购部员工
			if (d.getName().equals("总公司采购部")) {
				List<Filter> fs = new ArrayList<Filter>();
				fs.add(Filter.eq("isCaigouqian", true));
				fs.add(Filter.eq("isActive", true));
				fs.add(Filter.eq("supplierStatus", AuditStatus.pass));
				List<HySupplier> suppliers = hySupplierService.findList(null, fs, null);
				if (suppliers.isEmpty()) {
					json.setSuccess(true);
					json.setMsg("查询成功");
					json.setObj(new ArrayList<Map<String, Object>>());
					return json;
				} else {
					filters.add(Filter.in("ticketSupplier", suppliers));
				}
			}
			
			//此时为计调
			if (!d.getName().equals("总公司采购部")) {
				filters.add(Filter.eq("creator", admin));
			}
			
			List<Order> orders = new ArrayList<>();
			orders.add(Order.desc("id"));
			
			List<Map<String, Object>> result = new ArrayList<>();
			
			//因为需要合并查询结果，pn可以同时用于productId和productName
			if (StringUtils.isNotBlank(pn)) {
				Map<Long, Map<String, Object>> mapResult = new HashMap<>();
				filters.add(Filter.like("pn", pn));
				List<HyTicketHotel> hotels1 = hyTicketHotelService.findList(null, filters, orders);
				for (HyTicketHotel hotel : hotels1) {
					Map<String, Object> map = new HashMap<>();
					map.put("id", hotel.getId());
					map.put("hotelName", hotel.getHotelName());
					map.put("pn", hotel.getPn());
					map.put("jidiao", hotel.getCreator().getName());
					mapResult.putIfAbsent(hotel.getId(), map);
				}
				
				filters.remove(filters.size()-1);
				filters.add(Filter.like("hotelName", pn));
				List<HyTicketHotel> hotels2 = hyTicketHotelService.findList(null, filters, orders);
				for (HyTicketHotel hotel : hotels2) {
					Map<String, Object> map = new HashMap<>();
					map.put("id", hotel.getId());
					map.put("hotelName", hotel.getHotelName());
					map.put("pn", hotel.getPn());
					map.put("jidiao", hotel.getCreator().getName());
					mapResult.putIfAbsent(hotel.getId(), map);
				}
				
				result.addAll(mapResult.values());
				Collections.sort(result, new Comparator<Map<String, Object>>() {
				
								@Override
								public int compare(Map<String, Object> o1, Map<String, Object> o2) {
									if (((Long)o1.get("id")).compareTo((Long)o2.get("id")) > 0) {
										return 1;
									} else if (((Long)o1.get("id")).compareTo((Long)o2.get("id")) < 0) {
										return -1;
									} else {
										return 0;
									}
								}
							});
				
			} else {  //没有传pn值
				List<HyTicketHotel> hotels = hyTicketHotelService.findList(null, filters, orders);
				for (HyTicketHotel hotel : hotels) {
					Map<String, Object> map = new HashMap<>();
					map.put("id", hotel.getId());
					map.put("hotelName", hotel.getHotelName());
					map.put("pn", hotel.getPn());
					map.put("jidiao", hotel.getCreator().getName());
					result.add(map);
				}
			}
			
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(result);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(e);
			e.printStackTrace();
		}
		
		return json;
	}
	
	/**
	 * 获取酒店房型
	 * @param hotelId
	 * @param session
	 * @param request
	 * @return
	 */
	@RequestMapping("/findroomofhotel/view")
	@ResponseBody
	public Json findRoomsOfLine(Long hotelId, HttpSession session, HttpServletRequest request) {
		Json json = new Json();
		
		
		try {
			
			HyTicketHotel hotel = hyTicketHotelService.find(hotelId);
			if (hotel == null) {
				json.setSuccess(false);
				json.setMsg("不存在指定的酒店产品");
				json.setObj(null);
				return json;
			}
			
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("hyTicketHotel", hotel));
			filters.add(Filter.isNull("promotionActivity"));
			// 已上架的房型
			filters.add(Filter.eq("saleStatus", 2));
			List<Order> orders = new ArrayList<>();
			orders.add(Order.asc("id"));
			List<HyTicketHotelRoom> rooms = hyTicketHotelRoomServiceImpl.findList(null, filters, orders);
			
			Map<String, Object> ans = new HashMap<>();
			List<Map<String, Object>> result = new ArrayList<>();
			
			for (HyTicketHotelRoom room : rooms) {
				Map<String, Object> map = new HashMap<>();
				map.put("roomId", room.getId());
				map.put("roomType", room.getRoomType());
				map.put("productId", room.getProductId());
				map.put("productName", room.getProductName());
				result.add(map);
			}
			
			ans.put("hotelId", hotel.getId());
			ans.put("hotelPn", hotel.getPn());
			ans.put("hotelName", hotel.getHotelName());
			ans.put("roomList", result);
			
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(result);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(e);
			e.printStackTrace();
		}
		
		return json;
	}
	
	/**
	 * 获取景区产品列表
	 * @param scenename
	 * @param pn
	 * @param isCaigouti
	 * @param session
	 * @param request
	 * @return
	 */
	@RequestMapping("/findscenelist/view")
	@ResponseBody
	public Json findScene(String scenename, String pn, Boolean isCaigouti,HttpSession session, HttpServletRequest request) {
		Json json = new Json();
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin admin = hyAdminService.find(username);
		
		try {
			List<Filter> filters = new ArrayList<>();
	
			
			HyDepartmentModel d = admin.getDepartment().getHyDepartmentModel();
			//如果admin是总公司采购部员工
			if (d.getName().equals("总公司采购部")) {
				List<Filter> fs = new ArrayList<Filter>();
				fs.add(Filter.eq("isCaigouqian", true));
				fs.add(Filter.eq("isActive", true));
				fs.add(Filter.eq("supplierStatus", AuditStatus.pass));
				List<HySupplier> suppliers = hySupplierService.findList(null, fs, null);
				if (suppliers.isEmpty()) {
					json.setSuccess(true);
					json.setMsg("查询成功");
					json.setObj(new ArrayList<Map<String, Object>>());
					return json;
				} else {
					filters.add(Filter.in("ticketSupplier", suppliers));
				}
			}
			
			//此时为计调
			if (!d.getName().equals("总公司采购部")) {
				filters.add(Filter.eq("creator", admin));
			}
			
			List<Order> orders = new ArrayList<>();
			orders.add(Order.desc("id"));
			
			List<Map<String, Object>> result = new ArrayList<>();
			
			//因为需要合并查询结果，pn可以同时用于productId和productName
			if (StringUtils.isNotBlank(pn)) {
				Map<Long, Map<String, Object>> mapResult = new HashMap<>();
				filters.add(Filter.like("pn", pn));
				List<HyTicketScene> scenes1 = hyTicketSceneServiceImpl.findList(null, filters, orders);
				for (HyTicketScene scene : scenes1) {
					Map<String, Object> map = new HashMap<>();
					map.put("id", scene.getId());
					map.put("sceneName", scene.getSceneName());
					map.put("pn", scene.getPn());
					map.put("jidiao", scene.getCreator().getName());
					mapResult.putIfAbsent(scene.getId(), map);
				}

				filters.remove(filters.size()-1);
				filters.add(Filter.like("sceneName", pn));
				List<HyTicketScene> scenes2 = hyTicketSceneServiceImpl.findList(null, filters, orders);
				for (HyTicketScene scene : scenes2) {
					Map<String, Object> map = new HashMap<>();
					map.put("id", scene.getId());
					map.put("sceneName", scene.getSceneName());
					map.put("pn", scene.getPn());
					map.put("jidiao", scene.getCreator().getName());
					mapResult.putIfAbsent(scene.getId(), map);
				}
				
				result.addAll(mapResult.values());
				Collections.sort(result, new Comparator<Map<String, Object>>() {
				
								@Override
								public int compare(Map<String, Object> o1, Map<String, Object> o2) {
									if (((Long)o1.get("id")).compareTo((Long)o2.get("id")) > 0) {
										return 1;
									} else if (((Long)o1.get("id")).compareTo((Long)o2.get("id")) < 0) {
										return -1;
									} else {
										return 0;
									}
								}
							});
				
			} else {  //没有传pn值
				List<HyTicketScene> scenes = hyTicketSceneServiceImpl.findList(null, filters, orders);
				for (HyTicketScene scene : scenes) {
					Map<String, Object> map = new HashMap<>();
					map.put("id", scene.getId());
					map.put("sceneName", scene.getSceneName());
					map.put("pn", scene.getPn());
					map.put("jidiao", scene.getCreator().getName());
					result.add(map);
				}
			}
			
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(result);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(e);
			e.printStackTrace();
		}
		
		return json;
	}
	
	@RequestMapping("/findmanagemntofscene/view")
	@ResponseBody
	public Json findManagementsOfScene(Long sceneId, HttpSession session, HttpServletRequest request) {
		Json json = new Json();
		
		
		try {
			
			HyTicketScene scene = hyTicketSceneServiceImpl.find(sceneId);
			if (scene == null) {
				json.setSuccess(false);
				json.setMsg("不存在指定的景区产品");
				json.setObj(null);
				return json;
			}
			
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("hyTicketScene", scene));
			filters.add(Filter.isNull("hyPromotionActivity"));
			// 已上架的门票
			filters.add(Filter.eq("saleStatus", 2));
			List<Order> orders = new ArrayList<>();
			orders.add(Order.asc("id"));
			List<HyTicketSceneTicketManagement> managements = hyTicketSceneTicketManagementServiceImpl.findList(null, filters, orders);
			
			Map<String, Object> ans = new HashMap<>();
			List<Map<String, Object>> result = new ArrayList<>();
			
			for (HyTicketSceneTicketManagement management : managements) {
				Map<String, Object> map = new HashMap<>();
				map.put("managementId", management.getId());
				map.put("ticketType", management.getTicketType());
				map.put("productId", management.getProductId());
				map.put("productName", management.getProductName());
				result.add(map);
			}
			
			ans.put("sceneId", scene.getId());
			ans.put("scenePn", scene.getPn());
			ans.put("sceneName", scene.getSceneName());
			ans.put("managementList", result);
			
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(result);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(e);
			e.printStackTrace();
		}
		
		return json;
	}
	
	@RequestMapping("/findsubsribe/view")
	@ResponseBody
	public Json findSubscribes(String pn, Boolean isCaigouti, HttpSession session, HttpServletRequest request) {
		Json json = new Json();
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin admin = hyAdminService.find(username);
		
		try {
			List<Filter> filters = new ArrayList<>();
			
			HyDepartmentModel d = admin.getDepartment().getHyDepartmentModel();
			//如果admin是总公司采购部员工
			if (d.getName().equals("总公司采购部")) {
				List<Filter> fs = new ArrayList<Filter>();
				fs.add(Filter.eq("isCaigouqian", true));
				fs.add(Filter.eq("isActive", true));
				fs.add(Filter.eq("supplierStatus", AuditStatus.pass));
				List<HySupplier> suppliers = hySupplierService.findList(null, fs, null);
				if (suppliers.isEmpty()) {
					json.setSuccess(true);
					json.setMsg("查询成功");
					json.setObj(new ArrayList<Map<String, Object>>());
					return json;
				} else {
					filters.add(Filter.in("ticketSupplier", suppliers));
				}
			}
			
			//如果admin是分公司汽车部员工
			if (d.getName().equals("分公司汽车部")) {
				if (isCaigouti) {
					List<Filter> fs = new ArrayList<>();
					fs.add(Filter.eq("department", admin.getDepartment()));
					List<HyAdmin> employees = hyAdminService.findList(null, fs, null);
					List<Filter> supplierfs = new ArrayList<Filter>();
					supplierfs.add(Filter.in("operator", employees));
					supplierfs.add(Filter.eq("isActive", true));
					supplierfs.add(Filter.eq("supplierStatus", AuditStatus.pass));
					List<HySupplier> suppliers = hySupplierService.findList(null, supplierfs, null);
					if (suppliers.isEmpty()) {
						json.setSuccess(true);
						json.setMsg("查询成功");
						json.setObj(new ArrayList<Map<String, Object>>());
						return json;
					} else {
						filters.add(Filter.in("ticketSupplier", suppliers));
					}
				} else {
					filters.add(Filter.eq("creator", admin));
				}
			}
			
			//此时为计调
			if (!d.getName().equals("总公司采购部") && !d.getName().equals("分公司汽车部")) {
				filters.add(Filter.eq("creator", admin));
			}
			
			filters.add(Filter.isNull("hyPromotionActivity"));
			
			List<Order> orders = new ArrayList<>();
			orders.add(Order.desc("id"));
			
			List<Map<String, Object>> result = new ArrayList<>();
			
			//因为需要合并查询结果，pn可以同时用于productId和productName
			if (StringUtils.isNotBlank(pn)) {
				Map<Long, Map<String, Object>> mapResult = new HashMap<>();
				filters.add(Filter.like("productId", pn));
				List<HyTicketSubscribe> subscribes1 = hyTicketSubscribeServiceImpl.findList(null, filters, orders);
				for (HyTicketSubscribe subscribe : subscribes1) {
					Map<String, Object> map = new HashMap<>();
					map.put("subscribeId", subscribe.getId());
					map.put("pn", subscribe.getProductId());
					map.put("sceneName", subscribe.getSceneName());
					map.put("jidiao", subscribe.getCreator().getName());
					mapResult.putIfAbsent(subscribe.getId(), map);
				}

				filters.remove(filters.size()-1);
				filters.add(Filter.like("sceneName", pn));
				List<HyTicketSubscribe> subscribes2 = hyTicketSubscribeServiceImpl.findList(null, filters, orders);
				for (HyTicketSubscribe subscribe : subscribes2) {
					Map<String, Object> map = new HashMap<>();
					map.put("subscribeId", subscribe.getId());
					map.put("pn", subscribe.getProductId());
					map.put("sceneName", subscribe.getSceneName());
					map.put("jidiao", subscribe.getCreator().getName());
					mapResult.putIfAbsent(subscribe.getId(), map);
				}
				
				result.addAll(mapResult.values());
				Collections.sort(result, new Comparator<Map<String, Object>>() {
				
								@Override
								public int compare(Map<String, Object> o1, Map<String, Object> o2) {
									if (((Long)o1.get("id")).compareTo((Long)o2.get("id")) > 0) {
										return 1;
									} else if (((Long)o1.get("id")).compareTo((Long)o2.get("id")) < 0) {
										return -1;
									} else {
										return 0;
									}
								}
							});
				
			} else {  //没有传pn值
				List<HyTicketSubscribe> subscribes = hyTicketSubscribeServiceImpl.findList(null, filters, orders);				
				
				for (HyTicketSubscribe subscribe : subscribes) {
					Map<String, Object> map = new HashMap<>();
					map.put("subscribeId", subscribe.getId());
					map.put("pn", subscribe.getProductId());
					map.put("sceneName", subscribe.getSceneName());
					map.put("jidiao", subscribe.getCreator().getName());
					result.add(map);
				}
			}
			
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(result);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(e);
			e.printStackTrace();
		}
		
		return json;
	}
}
