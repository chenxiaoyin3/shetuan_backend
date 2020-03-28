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
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyDepartmentModel;
import com.hongyu.entity.HyGroup;
import com.hongyu.entity.HyLine;
import com.hongyu.entity.HySupplier;
import com.hongyu.entity.LinePromotion;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.HyLineService;
import com.hongyu.service.HySupplierService;
import com.hongyu.service.LinePromotionService;
import com.hongyu.util.Constants;
import com.hongyu.util.Constants.AuditStatus;
import com.hongyu.util.DateUtil;

@Controller
@RequestMapping("/admin/linepromotion")
public class LinePromotionController {
	
	@Resource(name = "linePromotionServiceImpl")
	LinePromotionService linePromotionServiceImpl;
	
	@Resource(name="hyAdminServiceImpl")
	private HyAdminService hyAdminService;
	
	@Resource(name = "hyGroupServiceImpl")
	HyGroupService hyGroupService;
	
	@Resource(name = "hyLineServiceImpl")
	HyLineService hyLineService;
	
	@Resource(name = "departmentServiceImpl")
	DepartmentService departmentServiceImpl;
	
	@Resource
	private HistoryService historyService;
	
	@Resource
	private TaskService taskService;
	
	@Resource
	private RuntimeService runtimeService;
	
	@Resource(name="hySupplierServiceImpl")
	private HySupplierService hySupplierService;
	
	//只能看自己创建的
	@RequestMapping("/page/view")
	@ResponseBody
	public Json getPromotionPage(LinePromotion query, Pageable pageable, HttpSession session, HttpServletRequest request) {
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
			Page<LinePromotion> page = linePromotionServiceImpl.findPage(pageable, query);
			List<Map<String, Object>> maps = new ArrayList<>();
			for (LinePromotion promotion : page.getRows()) {
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
	public Json getPromotionAuditPage(LinePromotion query, Pageable pageable, HttpSession session, HttpServletRequest request) {
		Json json = new Json();
		List<Filter> filters = new ArrayList<>();
		
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin admin = hyAdminService.find(username);
		

		try {
			Page<LinePromotion> page = linePromotionServiceImpl.findAuditPage(admin, pageable, query);
			List<Map<String, Object>> maps = new ArrayList<>();
			for (LinePromotion promotion : page.getRows()) {
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
	
	/**
	 * 查找线路
	 * @param contents
	 * @param session
	 * @param request
	 * @return
	 */
	@RequestMapping("/findline/view")
	@ResponseBody
	public Json findJidiaoLines(String pn, Boolean isCaigouti, HttpSession session, HttpServletRequest request) {
		Json json = new Json();
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin admin = hyAdminService.find(username);
		
		try {
			List<Filter> filters = new ArrayList<>();
			if (StringUtils.isNotBlank(pn)) {
				filters.add(Filter.like("pn", pn));
			}
			filters.add(Filter.eq("isSale", HyLine.IsSaleEnum.yishang));
			
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
					filters.add(Filter.in("hySupplier", suppliers));
				}
			}
			
			//如果admin是分公司汽车部员工
			if (d.getName().equals("分公司汽车部")) {
				if (isCaigouti) {
//					List<Filter> fs = new ArrayList<Filter>();
//					List<Filter> adminfs = new ArrayList<Filter>();
//					fs.add(Filter.eq("isCaigouqian", false));
//					fs.add(Filter.eq("isActive", true));
//					adminfs.add(Filter.eq("department", admin.getDepartment()));
//					List<HyAdmin> admins = hyAdminService.findList(null, adminfs, null);
//					if (admins.isEmpty()) {
//						json.setSuccess(true);
//						json.setMsg("查询成功");
//						json.setObj(new ArrayList<Map<String, Object>>());
//					} else {
//						fs.add(Filter.in("operator", admins));
//					}
//					List<HySupplier> suppliers = hySupplierService.findList(null, fs, null);
//					if (suppliers.isEmpty()) {
//						json.setSuccess(true);
//						json.setMsg("查询成功");
//						json.setObj(new ArrayList<Map<String, Object>>());
//					} else {
//						filters.add(Filter.in("hySupplier", suppliers));
//					}
					
					Department company = departmentServiceImpl.findCompanyOfDepartment(admin.getDepartment());
					filters.add(Filter.eq("company", company));
					filters.add(Filter.eq("isInner", Boolean.FALSE));
				} else {
					filters.add(Filter.eq("operator", admin));
				}
			}
			
			//此时为计调
			if (!d.getName().equals("总公司采购部") && !d.getName().equals("分公司汽车部")) {
				filters.add(Filter.eq("operator", admin));
			}
			
						
			List<Order> orders = new ArrayList<>();
			orders.add(Order.desc("id"));
			List<HyLine> lines = hyLineService.findList(null, filters, orders);
			
			Set<HyLine> set = new HashSet<>(lines);
			
			if (StringUtils.isNotBlank(pn)) {
				filters.remove(0);
				filters.add(Filter.like("name", pn));
				
				List<HyLine> otherLines = hyLineService.findList(null, filters, orders);
				Set<HyLine> otherSet = new HashSet<>(otherLines);
				
				set.addAll(otherSet);
			}
			
			
			List<Map<String, Object>> result = new ArrayList<>();
			
			
			for (HyLine line : set) {
				Map<String, Object> map = new HashMap<>();
				map.put("lineId", line.getId());
				map.put("linePn", line.getPn());
				map.put("lineName", line.getName());
				map.put("jidiao", line.getOperator().getName());
				result.add(map);
			}
			
			Collections.sort(result, new Comparator<Map<String, Object>>() {

				@Override
				public int compare(Map<String, Object> o1, Map<String, Object> o2) {
					if (((Long)o1.get("lineId")).compareTo((Long)o2.get("lineId")) > 0) {
						return 1;
					} else if (((Long)o1.get("lineId")).compareTo((Long)o2.get("lineId")) < 0) {
						return -1;
					} else {
						return 0;
					}
				}
			});
			
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
	 * 查找计调的线路
	 * @param contents
	 * @param session
	 * @param request
	 * @return
	 */
//	@RequestMapping("/jidiao/findline/view")
//	@ResponseBody
//	public Json findLines(String contents, HttpSession session, HttpServletRequest request) {
//		Json json = new Json();
//		
//		try {
//			List<Filter> filters = new ArrayList<>();
//			filters.add(Filter.like("pn", contents));
//			filters.add(Filter.eq("isSale", HyLine.IsSaleEnum.yishang));
//			
//			List<Order> orders = new ArrayList<>();
//			orders.add(Order.desc("id"));
//			List<HyLine> lines = hyLineService.findList(null, filters, orders);
//			
//			List<Map<String, Object>> result = new ArrayList<>();
//			
//			for (HyLine line : lines) {
//				Map<String, Object> map = new HashMap<>();
//				map.put("lineId", line.getId());
//				map.put("linePn", line.getPn());
//				map.put("lineName", line.getName());
//				result.add(map);
//			}
//			
//			json.setSuccess(true);
//			json.setMsg("查询成功");
//			json.setObj(result);
//		} catch (Exception e) {
//			json.setSuccess(false);
//			json.setMsg("查询失败");
//			json.setObj(e);
//			e.printStackTrace();
//		}
//		
//		return json;
//	}
	
	@RequestMapping("/findgroupofline/view")
	@ResponseBody
	public Json findGroupsOfLine(Long lineId, HttpSession session, HttpServletRequest request) {
		Json json = new Json();
		
		
		try {
			
			HyLine line = hyLineService.find(lineId);
			if (line == null) {
				json.setSuccess(false);
				json.setMsg("不存在指定的线路产品");
				json.setObj(null);
				return json;
			}
			
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("line", line));
			filters.add(Filter.ge("startDay", DateUtil.getStartOfDay(DateUtil.getNextDay(new Date()))));
			filters.add(Filter.eq("isPromotion", Boolean.FALSE));
			filters.add(Filter.eq("auditStatus", AuditStatus.pass));
			// added at 2018/10/11
			filters.add(Filter.eq("isCancel", Boolean.FALSE));
			List<Order> orders = new ArrayList<>();
			orders.add(Order.asc("id"));
			List<HyGroup> groups = hyGroupService.findList(null, filters, orders);
			
			Map<String, Object> ans = new HashMap<>();
			List<Map<String, Object>> result = new ArrayList<>();
			
			for (HyGroup group : groups) {
				Map<String, Object> map = new HashMap<>();
				map.put("groupId", group.getId());
				map.put("startDay", group.getStartDay());
				result.add(map);
			}
			
			ans.put("lineId", line.getId());
			ans.put("linePn", line.getPn());
			ans.put("name", line.getName());
			ans.put("groupList", result);
			
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
		public Long[] groupids;
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
		public Long[] getGroupids() {
			return groupids;
		}
		public void setGroupids(Long[] groupids) {
			this.groupids = groupids;
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
	}
	
	/**
	 * 计调新增促销
	 * @param wrapper
	 * @param session
	 * @param request
	 * @return
	 */
	@RequestMapping("/add")
	@ResponseBody
	public Json linePromotionAdd(@RequestBody PromitionWrapper wrapper, HttpSession session, HttpServletRequest request) {
		Json json = new Json();
		
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin admin = hyAdminService.find(username);
		
		HashMap<String, Object> map = new HashMap<>();
		
		try {
			Set<Long> lineIds = new HashSet<>();
			List<HyGroup> groups = hyGroupService.findList(wrapper.getGroupids());
			Set<String> operators = new HashSet<>();
			for (HyGroup group : groups) {
				operators.add(group.getCreator().getUsername());
				lineIds.add(group.getLine().getId());
			}
			
			for (String jidiaoName : operators) {
				LinePromotion promotion = new LinePromotion();
				promotion.setName(wrapper.getName());
				promotion.setStartDate(wrapper.getStartDate());
				promotion.setEndDate(wrapper.getEndDate());
				promotion.setPromotionType(wrapper.getPromotionType());
				promotion.setManjianPrice1(wrapper.getManjianPrice1());
				promotion.setManjianPrice2(wrapper.getManjianPrice2());
				promotion.setMeirenjian(wrapper.getMeirenjian());
				promotion.setDazhe(wrapper.getDazhe());
				promotion.setRemark(wrapper.getRemark());
				HyAdmin jidiao = hyAdminService.find(jidiaoName);
				promotion.setOperator(jidiao);
				for (HyGroup group : groups) {
					if (jidiaoName.equals(group.getCreator().getUsername())) {
						promotion.getGroups().add(group);
					}
				}
				promotion.setApplyName(admin);
				
				HyDepartmentModel d = admin.getDepartment().getHyDepartmentModel();
				promotion.setIsCaigouti(wrapper.getIsCaigouti());
//				if(d.getName().equals("总公司采购部") || d.getName().equals("分公司汽车部")) {
//					promotion.setIsCaigouti(true);
//				} else {
//					promotion.setIsCaigouti(false);
//				}
				
				//启动工作流程
				ProcessInstance pi= runtimeService.startProcessInstanceByKey("LinePromotion"); //"LinePromotion"为bpmn文件中的key
				promotion.setProcessInstanceId(pi.getProcessInstanceId());
				Department department=admin.getDepartment();
				//如果是总公司采购部提交审核,且提交审核人和计调不是同一个人,由计调审核
				if(department.getHyDepartmentModel().getName().equals("总公司采购部") && 
						!promotion.getOperator().getUsername().equals(promotion.getApplyName().getUsername())) {
					map.put("auditor", "jidiao"); //向监听器传递参数
					//将计调传进监听器
					map.put("operator",jidiaoName);
				}
				//其他提交审核,由总公司品控部审核
				else {
					map.put("auditor", "pinkong"); //向监听器传递参数
				}
				
				// 根据流程实例Id获取任务
				Task task=taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
				linePromotionServiceImpl.save(promotion);
				// 完成任务		
				taskService.complete(task.getId(),map);
				for (HyGroup group : groups) {
					group.setIsPromotion(Boolean.TRUE);
					hyGroupService.update(group);
				}
				
				List<HyLine> hyLines = hyLineService.findList(lineIds.toArray(new Long[lineIds.size()]));
				for (HyLine hyLine : hyLines) {
					hyLine.setIsPromotion(true);
					hyLineService.update(hyLine);
				}
			}
			
			
			json.setSuccess(true);
			json.setMsg("添加成功");
			json.setObj(null);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("添加失败");
			json.setObj(e);
			e.printStackTrace();
		}
		
		return json;
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
	public Json linePromotionAudit(Long id, String comment, Boolean isApproved, HttpSession session, HttpServletRequest request) {
		Json json = new Json();
		
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin admin = hyAdminService.find(username);
		
		try {
			LinePromotion linePromotion = linePromotionServiceImpl.find(id);
			if (linePromotion == null) {
				json.setSuccess(false);
				json.setMsg("审核失败，不存在指定的线路促销");
				json.setObj(null);
				return json;
			}
			
			Task task=taskService.createTaskQuery().processInstanceId(linePromotion.getProcessInstanceId()).singleResult();
			if (!isApproved) {
				Authentication.setAuthenticatedUserId(username); 
				taskService.addComment(task.getId(), linePromotion.getProcessInstanceId(), comment + ":" + "驳回");
				linePromotion.setState(Constants.LINE_PROMOTION_STATUS_FAIL);
				linePromotion.setIsCancel(true);
			} else {
				Authentication.setAuthenticatedUserId(username); 
				taskService.addComment(task.getId(), linePromotion.getProcessInstanceId(), "同意:通过");
				linePromotion.setState(Constants.LINE_PROMOTION_STATUS_PASS);
			}
			
			if (!isApproved) {
				Set<HyGroup> groups = linePromotion.getGroups();
				for (HyGroup group :groups) {
					group.setIsPromotion(Boolean.FALSE);
					hyGroupService.update(group);
				}
			}
			linePromotion.setAuditor(admin);
			linePromotion.setAuditTime(new Date());
			taskService.complete(task.getId());
			linePromotionServiceImpl.update(linePromotion);
			
			json.setSuccess(true);
			json.setMsg("审核成功");
			json.setObj(null);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("审核失败");
			json.setObj(e);
			e.printStackTrace();
		}
		
		return json;
	}
	
	
	/**
	 * 促销详情
	 * @param wrapper
	 * @param session
	 * @param request
	 * @return
	 */
	@RequestMapping("/detail/view")
	@ResponseBody
	public Json linePromotionDetail(Long id, HttpSession session, HttpServletRequest request) {
		Json json = new Json();
		
		try {
			
			LinePromotion promotion = linePromotionServiceImpl.find(id);
			if (promotion == null) {
				json.setSuccess(false);
				json.setMsg("不存在指定的线路产品");
				json.setObj(null);
				return json;
			}
			
			Map<String, Object> map = new HashMap<>();
			map.put("id", promotion.getId());
			map.put("name", promotion.getName());
			map.put("startDate", promotion.getStartDate());
			map.put("endDate", promotion.getEndDate());
			map.put("promotionType", promotion.getPromotionType());
			map.put("manjianPrice1", promotion.getManjianPrice1());
			map.put("manjianPrice2", promotion.getManjianPrice2());
			map.put("dazhe", promotion.getDazhe());
			map.put("meirenjian", promotion.getMeirenjian());
			map.put("isCancel", promotion.getIsCancel());
			map.put("state", promotion.getState());
			map.put("applyName", promotion.getApplyName().getName());
			map.put("applyTime", promotion.getApplyTime());
			map.put("remark", promotion.getRemark());
			Map<Long, Set<HyGroup>> lineMapGroup = groupHyGroupList(promotion.getGroups());
			List<Map<String, Object>> lines = new ArrayList<>();
			
			for (Long lineId : lineMapGroup.keySet()) {
				HyLine line = hyLineService.find(lineId);
				Set<HyGroup> groups = lineMapGroup.get(lineId);
				Map<String, Object> m = new HashMap<>();
				m.put("linePn", line.getPn());
				m.put("lineName", line.getName());
				m.put("jidiao", line.getOperator().getName());
				List<Date> dates = new ArrayList<>();
				for (HyGroup group : groups) {
					dates.add(group.getStartDay());
				}
				m.put("groups", dates);
				lines.add(m);
			}
			
			map.put("lines", lines);
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(map);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败");
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
			LinePromotion linePromotion = linePromotionServiceImpl.find(id);
			if (linePromotion == null) {
				json.setSuccess(false);
				json.setMsg("审核失败，不存在指定的线路促销");
				json.setObj(null);
				return json;
			}
			
			if (linePromotion.getState() == Constants.LINE_PROMOTION_STATUS_AUDITING) {
				json.setSuccess(false);
				json.setMsg("线路促销尚未审核");
				json.setObj(null);
				return json;
			}
			
			List<Map<String, Object>> result = new ArrayList<>();
			String processInstanceId = linePromotion.getProcessInstanceId();
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
				im.put("audittime", linePromotion.getAuditTime());
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
	
	private Map<Long, Set<HyGroup>> groupHyGroupList(Set<HyGroup> groups) {
		Map<Long, Set<HyGroup>> map = new HashMap<>();
		for (HyGroup group : groups) {
			if (!map.containsKey(group.getLine().getId())) {
				map.put(group.getLine().getId(), new HashSet<HyGroup>());
			}
			map.get(group.getLine().getId()).add(group);
		}
		
		return map;
	}
	
	@RequestMapping("/cancel")
	@ResponseBody
	public Json linePromotionCancel(Long id, HttpSession session, HttpServletRequest request) {
		Json json = new Json();
		
		
		try {
			
			LinePromotion promotion = linePromotionServiceImpl.find(id);
			if (promotion == null) {
				json.setSuccess(false);
				json.setMsg("不存在指定的线路促销");
				json.setObj(null);
				return json;
			}
		
			promotion.setIsCancel(true);
			promotion.setState(Constants.LINE_PROMOTION_STATUS_CANCLED);
			Set<Long> lineIds = new HashSet<>();
			for (HyGroup group : promotion.getGroups()) {
				group.setIsPromotion(false);
				lineIds.add(group.getLine().getId());
				hyGroupService.update(group);
			}
			linePromotionServiceImpl.update(promotion);
			
			List<HyLine> hyLines = hyLineService.findList(lineIds.toArray(new Long[lineIds.size()]));
			for (HyLine hyLine : hyLines) {
				List<Filter> groupFilters = new ArrayList<>();
				groupFilters.add(Filter.eq("line", hyLine));
				groupFilters.add(Filter.eq("isPromotion", true));
				List<HyGroup> groups = hyGroupService.findList(null, groupFilters, null);
				if (groups.isEmpty()) {
					hyLine.setIsPromotion(false);
					hyLineService.update(hyLine);
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

}
