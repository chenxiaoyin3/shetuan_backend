package com.hongyu.controller.gsbing;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.grain.util.FilterUtil;
import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyTicketInbound;
import com.hongyu.entity.HyTicketPriceInbound;
import com.hongyu.entity.HyTicketScene;
import com.hongyu.entity.HyTicketSceneTicketManagement;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyTicketInboundService;
import com.hongyu.service.HyTicketSceneService;
import com.hongyu.service.HyTicketSceneTicketManagementService;
import com.hongyu.util.ActivitiUtils;
/**
 * 品控审核电子门票
 * Author:GSbing
 */
@RestController
@RequestMapping("/admin/pinkong/sceneticketprice")
@Transactional(propagation = Propagation.REQUIRED)
public class Review_ticket_scenePriceController {
	@Resource
	private TaskService taskService;

	@Resource
	private RuntimeService runtimeService;
	
	@Resource
	private HistoryService historyService;
	@Resource(name="hyAdminServiceImpl")
	private HyAdminService hyAdminService;
	@Resource(name="hyTicketSceneServiceImpl")
	private HyTicketSceneService hyTicketSceneService;
	@Resource(name="hyTicketSceneTicketManagementServiceImpl")
	private HyTicketSceneTicketManagementService hyTicketSceneTicketManagementService;
	@Resource(name="hyTicketInboundServiceImpl")
	private HyTicketInboundService hyTicketInboundService;
	
	@RequestMapping(value="list/view")
	@ResponseBody
	public Json ticketList(HyTicketSceneTicketManagement queryParam,Integer state,String submitName,Pageable pageable,HttpSession session)
	{
		Json json=new Json();
		try{
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			List<Filter> filters = FilterUtil.getInstance().getFilter(queryParam);
			if(submitName!=null&&!submitName.equals(""))
			{
				List<Filter> adminFilter=new ArrayList<Filter>();
				adminFilter.add(Filter.like("name", submitName));
				List<HyAdmin> adminList=hyAdminService.findList(null,adminFilter,null);
				if(adminList.size()==0){
					json.setMsg("查询成功");
				    json.setSuccess(true);
				    json.setObj(new Page<HyAdmin>());
				}
				else{
					filters.add(Filter.in("operator",adminList));
					List<HyTicketSceneTicketManagement> sceneTicketList=hyTicketSceneTicketManagementService.findList(null,filters,null);
					List<Map<String, Object>> ans = new ArrayList<>();
					Map<String, Object> answer = new HashMap<>();
					if(state==null) {
//						List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).list();
					    List<Task> tasks = ActivitiUtils.getTaskList(username, "sceneticketPriceProcess");
						for(Task task:tasks) {
					    	String processInstanceId = task.getProcessInstanceId();
					    	for(HyTicketSceneTicketManagement tmp:sceneTicketList) {
					    		if(processInstanceId.equals(tmp.getProcessInstanceId())) {
					    			HashMap<String, Object> map = new HashMap<>();
					    			map.put("id", tmp.getId());
					    			map.put("state", 0);
					    			map.put("productId", tmp.getProductId());
					    			map.put("productName", tmp.getProductName());
									if(tmp.getOperator()!=null){
										map.put("submitName", tmp.getOperator().getName());
									}						
									map.put("submitTime", tmp.getSubmitTime());
									map.put("auditStatus", tmp.getAuditStatus());
									ans.add(map);
					    		}
					    	}
					    }
//					    List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
//								.finished().taskAssignee(username).list();
						List<HistoricTaskInstance> historicTaskInstances = ActivitiUtils.getHistoryTaskList(username, "sceneticketPriceProcess");
					    for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
					    	String processInstanceId = historicTaskInstance.getProcessInstanceId();
					    	for(HyTicketSceneTicketManagement tmp:sceneTicketList) {
					    		if(processInstanceId.equals(tmp.getProcessInstanceId())) {
					    			HashMap<String, Object> map = new HashMap<>();
					    			map.put("id", tmp.getId());
					    			map.put("state", 1);
					    			map.put("productId", tmp.getProductId());
					    			map.put("productName", tmp.getProductName());
									if(tmp.getOperator()!=null){
										map.put("submitName", tmp.getOperator().getName());
									}						
									map.put("submitTime", tmp.getSubmitTime());
									map.put("auditStatus", tmp.getAuditStatus());
									ans.add(map);
					    		}
					    	}
					    }
					    Collections.sort(ans, new Comparator<Map<String, Object>>() {
							@Override
							public int compare(Map<String, Object> o1, Map<String, Object> o2) {
								Date date1 = (Date) o1.get("submitTime");
								Date date2 = (Date) o2.get("submitTime");
								return date1.compareTo(date2); 
							}
						});
					    Collections.reverse(ans);	
					}
					/*搜索未完成任务*/
					else if(state==0) {
//						List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).list();
						List<Task> tasks = ActivitiUtils.getTaskList(username, "sceneticketPriceProcess");
						for(Task task:tasks) {
					    	String processInstanceId = task.getProcessInstanceId();
					    	for(HyTicketSceneTicketManagement tmp:sceneTicketList) {
					    		if(processInstanceId.equals(tmp.getProcessInstanceId())) {
					    			HashMap<String, Object> map = new HashMap<>();
					    			map.put("id", tmp.getId());
					    			map.put("state", 0);
					    			map.put("productId", tmp.getProductId());
					    			map.put("productName", tmp.getProductName());
									if(tmp.getOperator()!=null){
										map.put("submitName", tmp.getOperator().getName());
									}						
									map.put("submitTime", tmp.getSubmitTime());
									map.put("auditStatus", tmp.getAuditStatus());
									ans.add(map);
					    		}
					    	}
					    }
					    Collections.sort(ans, new Comparator<Map<String, Object>>() {
							@Override
							public int compare(Map<String, Object> o1, Map<String, Object> o2) {
								Date date1 = (Date) o1.get("submitTime");
								Date date2 = (Date) o2.get("submitTime");
								return date1.compareTo(date2); 
							}
						});
					    Collections.reverse(ans);	
					}
					/*搜索已完成任务*/
					else if(state==1) {
//						List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
//								.finished().taskAssignee(username).list();
						List<HistoricTaskInstance> historicTaskInstances = ActivitiUtils.getHistoryTaskList(username, "sceneticketPriceProcess");
						for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
					    	String processInstanceId = historicTaskInstance.getProcessInstanceId();
					    	for(HyTicketSceneTicketManagement tmp:sceneTicketList) {
					    		if(processInstanceId.equals(tmp.getProcessInstanceId())) {
					    			HashMap<String, Object> map = new HashMap<>();
					    			map.put("id", tmp.getId());
					    			map.put("state", 1);
					    			map.put("productId", tmp.getProductId());
					    			map.put("productName", tmp.getProductName());
									if(tmp.getOperator()!=null){
										map.put("submitName", tmp.getOperator().getName());
									}						
									map.put("submitTime", tmp.getSubmitTime());
									map.put("auditStatus", tmp.getAuditStatus());
									ans.add(map);
					    		}
					    	}
					    }
					    Collections.sort(ans, new Comparator<Map<String, Object>>() {
							@Override
							public int compare(Map<String, Object> o1, Map<String, Object> o2) {
								Date date1 = (Date) o1.get("submitTime");
								Date date2 = (Date) o2.get("submitTime");
								return date1.compareTo(date2); 
							}
						});
					    Collections.reverse(ans);	
					}
					int page = pageable.getPage();
					int rows = pageable.getRows();
					answer.put("total", ans.size());
					answer.put("pageNumber", page);
					answer.put("pageSize", rows);
					answer.put("rows", ans);
					json.setSuccess(true);
					json.setMsg("查询成功");
					json.setObj(answer);
				}
			}	
			//submitName=null
			else {
				List<HyTicketSceneTicketManagement> sceneTicketList=hyTicketSceneTicketManagementService.findList(null,filters,null);
				List<Map<String, Object>> ans = new ArrayList<>();
				Map<String, Object> answer = new HashMap<>();
				if(state==null) {
//					List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).list();
					List<Task> tasks = ActivitiUtils.getTaskList(username, "sceneticketPriceProcess");
					for(Task task:tasks) {
				    	String processInstanceId = task.getProcessInstanceId();
				    	for(HyTicketSceneTicketManagement tmp:sceneTicketList) {
				    		if(processInstanceId.equals(tmp.getProcessInstanceId())) {
				    			HashMap<String, Object> map = new HashMap<>();
				    			map.put("id", tmp.getId());
				    			map.put("state", 0);
				    			map.put("productId", tmp.getProductId());
				    			map.put("productName", tmp.getProductName());
								if(tmp.getOperator()!=null){
									map.put("submitName", tmp.getOperator().getName());
								}						
								map.put("submitTime", tmp.getSubmitTime());
								map.put("auditStatus", tmp.getAuditStatus());
								ans.add(map);
				    		}
				    	}
				    }
//				    List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
//							.finished().taskAssignee(username).list();
					List<HistoricTaskInstance> historicTaskInstances = ActivitiUtils.getHistoryTaskList(username, "sceneticketPriceProcess");
					for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
				    	String processInstanceId = historicTaskInstance.getProcessInstanceId();
				    	for(HyTicketSceneTicketManagement tmp:sceneTicketList) {
				    		if(processInstanceId.equals(tmp.getProcessInstanceId())) {
				    			HashMap<String, Object> map = new HashMap<>();
				    			map.put("id", tmp.getId());
				    			map.put("state", 1);
				    			map.put("productId", tmp.getProductId());
				    			map.put("productName", tmp.getProductName());
								if(tmp.getOperator()!=null){
									map.put("submitName", tmp.getOperator().getName());
								}						
								map.put("submitTime", tmp.getSubmitTime());
								map.put("auditStatus", tmp.getAuditStatus());
								ans.add(map);
				    		}
				    	}
				    }
				    Collections.sort(ans, new Comparator<Map<String, Object>>() {
						@Override
						public int compare(Map<String, Object> o1, Map<String, Object> o2) {
							Date date1 = (Date) o1.get("submitTime");
							Date date2 = (Date) o2.get("submitTime");
							return date1.compareTo(date2); 
						}
					});
				    Collections.reverse(ans);	
				}
				/*搜索未完成任务*/
				else if(state==0) {
//					List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).list();
					List<Task> tasks = ActivitiUtils.getTaskList(username, "sceneticketPriceProcess");
					for(Task task:tasks) {
				    	String processInstanceId = task.getProcessInstanceId();
				    	for(HyTicketSceneTicketManagement tmp:sceneTicketList) {
				    		if(processInstanceId.equals(tmp.getProcessInstanceId())) {
				    			HashMap<String, Object> map = new HashMap<>();
				    			map.put("id", tmp.getId());
				    			map.put("state", 0);
				    			map.put("productId", tmp.getProductId());
				    			map.put("productName", tmp.getProductName());
								if(tmp.getOperator()!=null){
									map.put("submitName", tmp.getOperator().getName());
								}						
								map.put("submitTime", tmp.getSubmitTime());
								map.put("auditStatus", tmp.getAuditStatus());
								ans.add(map);
				    		}
				    	}
				    }
				    Collections.sort(ans, new Comparator<Map<String, Object>>() {
						@Override
						public int compare(Map<String, Object> o1, Map<String, Object> o2) {
							Date date1 = (Date) o1.get("submitTime");
							Date date2 = (Date) o2.get("submitTime");
							return date1.compareTo(date2); 
						}
					});
				    Collections.reverse(ans);	
				}
				/*搜索已完成任务*/
				else if(state==1) {
//					List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
//							.finished().taskAssignee(username).list();
					List<HistoricTaskInstance> historicTaskInstances = ActivitiUtils.getHistoryTaskList(username, "sceneticketPriceProcess");
					for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
				    	String processInstanceId = historicTaskInstance.getProcessInstanceId();
				    	for(HyTicketSceneTicketManagement tmp:sceneTicketList) {
				    		if(processInstanceId.equals(tmp.getProcessInstanceId())) {
				    			HashMap<String, Object> map = new HashMap<>();
				    			map.put("id", tmp.getId());
				    			map.put("state", 1);
				    			map.put("productId", tmp.getProductId());
				    			map.put("productName", tmp.getProductName());
								if(tmp.getOperator()!=null){
									map.put("submitName", tmp.getOperator().getName());
								}						
								map.put("submitTime", tmp.getSubmitTime());
								map.put("auditStatus", tmp.getAuditStatus());
								ans.add(map);
				    		}
				    	}
				    }
				    Collections.sort(ans, new Comparator<Map<String, Object>>() {
						@Override
						public int compare(Map<String, Object> o1, Map<String, Object> o2) {
							Date date1 = (Date) o1.get("submitTime");
							Date date2 = (Date) o2.get("submitTime");
							return date1.compareTo(date2); 
						}
					});
				    Collections.reverse(ans);	
				}
				int page = pageable.getPage();
				int rows = pageable.getRows();
				answer.put("total", ans.size());
				answer.put("pageNumber", page);
				answer.put("pageSize", rows);
				answer.put("rows", ans);
				json.setSuccess(true);
				json.setMsg("查询成功");
				json.setObj(answer);			
			}
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping("detail/view")
	@ResponseBody
	public Json detail(Long ticketId)
	{
		Json json=new Json();
		try{
			HyTicketSceneTicketManagement ticket=hyTicketSceneTicketManagementService.find(ticketId);
			HyTicketScene scene=ticket.getHyTicketScene();
			Map<String,Object> map=new HashMap<String,Object>();
			
			 /*景区信息*/
			map.put("sceneName", scene.getSceneName());
			map.put("area", scene.getArea().getFullName());
			if(scene.getTicketSupplier().getIsInner()==true) {
				map.put("supplierName", scene.getHySupplierElement().getName());
			}	
			map.put("sceneAddress", scene.getSceneAddress());
			map.put("star", scene.getStar());
			map.put("openTime", scene.getOpenTime());
			map.put("closeTime", scene.getCloseTime());
			map.put("ticketExchangeAddress", scene.getTicketExchangeAddress());
			map.put("introduction", scene.getIntroduction()); //产品介绍
			map.put("ticketFile",scene.getTicketFile()); //票务推广文件
			
			/*审核记录*/
			String processInstanceId = ticket.getProcessInstanceId();
			List<Comment> commentList = taskService.getProcessInstanceComments(processInstanceId);
			Collections.reverse(commentList);
			List<Map<String, Object>> list = new LinkedList<>();
			for(Comment comment:commentList){
				Map<String, Object> commentMap = new HashMap<>();
				String taskId = comment.getTaskId();
				HistoricTaskInstance task = historyService.createHistoricTaskInstanceQuery().taskId(taskId)
						.singleResult();
				String step = "";
				if (task != null) {
					step = task.getName();
				}
				commentMap.put("step", step);
				String username = comment.getUserId();
				HyAdmin hyAdmin = hyAdminService.find(username);
				String name = "";
				if (hyAdmin != null) {
					name = hyAdmin.getName();
				}
				commentMap.put("name", name);
				String str = comment.getFullMessage();
				int index = str.lastIndexOf(":");
				if (index < 0) {
					commentMap.put("comment", " ");
					commentMap.put("result", 1);
				} else {
					commentMap.put("comment", str.substring(0, index));
					commentMap.put("result", Integer.parseInt(str.substring(index + 1)));
				}
				commentMap.put("time", comment.getTime());
				list.add(commentMap);
			}
			map.put("auditList", list);
			
			/*门票信息*/
			map.put("productId", ticket.getProductId());
			map.put("productName", ticket.getProductName());
			map.put("ticketType", ticket.getTicketType());
			map.put("isReserve", ticket.getIsReserve());
			if(ticket.getIsReserve()==true){
				map.put("days", ticket.getDays());
				map.put("times", ticket.getTimes());
			}
			map.put("isRealName", ticket.getIsRealName());
			if(ticket.getIsRealName()==true){
				map.put("realNameRemark", ticket.getRealNameRemark());
			}
			map.put("refundReq", ticket.getRefundReq());
			map.put("reserveReq", ticket.getReserveReq());
			
			/*价格库存信息*/
			List<Map<String, Object>> priceMaplist = new LinkedList<>();
			List<HyTicketPriceInbound> priceList=new ArrayList<>(ticket.getHyTicketPriceInbounds());
			for(HyTicketPriceInbound price:priceList){
				Map<String, Object> priceMap = new HashMap<>();
				priceMap.put("startDate", price.getStartDate());
				priceMap.put("endDate", price.getEndDate());
				priceMap.put("displayPrice", price.getDisplayPrice());
				priceMap.put("sellPrice", price.getSellPrice());
				priceMap.put("settlementPrice", price.getSettlementPrice());
				priceMap.put("inventory", price.getInventory());
				priceMaplist.add(priceMap);
			}
			map.put("priceList", priceMaplist);
			json.setMsg("查询成功");
			json.setSuccess(true);
			json.setObj(map);
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	/** 景区门票价格库存提交 - 审核*/
	@RequestMapping("audit")
	@ResponseBody
	@Transactional(propagation = Propagation.REQUIRED)
	public Json audit(Long ticketId,String comment,Integer state,HttpSession session)
	{
		Json json=new Json();
		try{
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyTicketSceneTicketManagement ticket=hyTicketSceneTicketManagementService.find(ticketId);
			String processInstanceId=ticket.getProcessInstanceId();
			if (processInstanceId == null || processInstanceId == "") {
				json.setSuccess(false);
				json.setMsg("审核出错，信息不完整，请重新申请");
			} 
			else {
				Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
				if (state == 1)  // 审核通过
				{
					ticket.setAuditStatus(3);//审核通过
					ticket.setSaleStatus(2);//上架
					HyTicketScene scene=ticket.getHyTicketScene();
					if(scene.getMhState()!=null) {
						if(scene.getMhState()==1) {
							scene.setMhState(2); //设置完善状态为供应商有修改,待完善
							hyTicketSceneService.update(scene);
						}
					}	
					hyTicketSceneTicketManagementService.update(ticket);
					List<HyTicketPriceInbound> hyTicketPriceInbounds=new ArrayList<>(ticket.getHyTicketPriceInbounds());
				    for(HyTicketPriceInbound ticketPriceInbound:hyTicketPriceInbounds) {
				    	Date startDate=ticketPriceInbound.getStartDate();
				    	Date endDate=ticketPriceInbound.getEndDate();
				    	Integer inventory=ticketPriceInbound.getInventory();
				    	//计算两个日期之间相差天数
				    	long a=(long)(endDate.getTime()-startDate.getTime());
				    	int days=(int)(a/(1000*3600*24))+1;
				    	Calendar cld = Calendar.getInstance();
			    		cld.setTime(startDate);
			    		Date tmp=startDate;
			    		//依次存入产品的具体库存
				    	for(int i=0;i<days;i++) {
				             HyTicketInbound ticketInbound=new HyTicketInbound();
				             ticketInbound.setDay(tmp);
				             ticketInbound.setType(1); //类型为酒店,门票,酒加景
				             ticketInbound.setPriceInboundId(ticketPriceInbound.getId());
				             ticketInbound.setInventory(inventory);
				             hyTicketInboundService.save(ticketInbound);
				             cld.add(Calendar.DATE, 1); //获取指定日期后一天
				             tmp = cld.getTime();
				    	}
				    }
				}
				else if(state==0)
				{
					ticket.setAuditStatus(4); //已驳回
					hyTicketSceneTicketManagementService.update(ticket);
				}
				Authentication.setAuthenticatedUserId(username);
				taskService.addComment(task.getId(), processInstanceId, comment == null ? " " : comment + ":" + state);
				HashMap<String, Object> map = new HashMap<>();
				map.put("state", state);
				taskService.claim(task.getId(), username);
				taskService.complete(task.getId(), map);
				json.setSuccess(true);
				json.setMsg("操作成功");
		    }
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
}
