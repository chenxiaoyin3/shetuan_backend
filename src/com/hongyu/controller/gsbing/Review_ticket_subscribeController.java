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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.grain.util.FilterUtil;
import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Pageable;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyTicketInbound;
import com.hongyu.entity.HyTicketPriceInbound;
import com.hongyu.entity.HyTicketSubscribe;
import com.hongyu.entity.HyTicketSubscribePrice;
import com.hongyu.service.CommonSequenceService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyAreaService;
import com.hongyu.service.HySupplierElementService;
import com.hongyu.service.HySupplierService;
import com.hongyu.service.HyTicketInboundService;
import com.hongyu.service.HyTicketSubscribePriceService;
import com.hongyu.service.HyTicketSubscribeService;
import com.hongyu.util.ActivitiUtils;

/**
 * 品控审核认购门票
 * Author:GSbing
 */
@RestController
@RequestMapping("admin/pinkong/subScribe")
public class Review_ticket_subscribeController {
	@Resource
	private TaskService taskService;

	@Resource
	private HistoryService historyService;
	
	@Resource
	private RuntimeService runtimeService;
	
	@Resource(name="hyTicketSubscribeServiceImpl")
	private HyTicketSubscribeService hyTicketSubscribeService;
	
	@Resource(name="hyTicketSubscribePriceServiceImpl")
	private HyTicketSubscribePriceService hyTicketSubscribePriceService;
	
	@Resource(name="hyAdminServiceImpl")
	private HyAdminService hyAdminService;
		
	@Resource(name="hySupplierServiceImpl")
	private HySupplierService hySupplierService;
	
	@Resource(name="hyAreaServiceImpl")
	private HyAreaService hyAreaService;
	@Resource(name="commonSequenceServiceImp")
	private CommonSequenceService commonSequenceService;
	
	@Resource(name="hySupplierElementServiceImpl")
	private HySupplierElementService hySupplierElementService;
	
	@Resource(name="hyTicketInboundServiceImpl")
	private HyTicketInboundService hyTicketInboundService;
	
	
	@RequestMapping(value="list/view")
	@ResponseBody
	public Json listview(Pageable pageable,HyTicketSubscribe queryParam,Integer state,HttpSession session)
	{
		Json json=new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			List<Filter> filters = FilterUtil.getInstance().getFilter(queryParam);
			List<HyTicketSubscribe> ticketSubscribeList=hyTicketSubscribeService.findList(null,filters,null);
			List<Map<String, Object>> ans = new ArrayList<>();
			Map<String, Object> answer = new HashMap<>();
			if(state==null) {
//				List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).list();
				List<Task> tasks = ActivitiUtils.getTaskList(username, "subscribeTicket");
				for(Task task:tasks) {
					 String processInstanceId = task.getProcessInstanceId();
					 for(HyTicketSubscribe tmp:ticketSubscribeList) {
						 if(processInstanceId.equals(tmp.getProcessInstanceId())) {
							 HashMap<String, Object> map = new HashMap<>();
							 map.put("id", tmp.getId());
							 map.put("productId",tmp.getProductId());
							 map.put("sceneName",tmp.getSceneName());
							 map.put("createTime", tmp.getCreateTime());
							 map.put("creator", tmp.getCreator().getName());
							 map.put("auditStatus", tmp.getAuditStatus());
							 map.put("state", 0);
							 ans.add(map);
						 }					 
					 }
				 }
//				 List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
//							.finished().taskAssignee(username).list();
				List<HistoricTaskInstance> historicTaskInstances = ActivitiUtils.getHistoryTaskList(username, "subscribeTicket");
				for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
				     String processInstanceId = historicTaskInstance.getProcessInstanceId();
					 for(HyTicketSubscribe tmp:ticketSubscribeList) {
						 if(processInstanceId.equals(tmp.getProcessInstanceId())) {
							 HashMap<String, Object> map = new HashMap<>();
							 map.put("id", tmp.getId());
							 map.put("productId",tmp.getProductId());
							 map.put("sceneName",tmp.getSceneName());
							 map.put("createTime", tmp.getCreateTime());
							 map.put("creator", tmp.getCreator().getName());
							 map.put("auditStatus", tmp.getAuditStatus());
							 map.put("state", 1);
							 ans.add(map);
						}
					}
				}
				 Collections.sort(ans, new Comparator<Map<String, Object>>() {
					    @Override
					    public int compare(Map<String, Object> o1, Map<String, Object> o2) {
					        Date date1 = (Date) o1.get("createTime");
					        Date date2 = (Date) o2.get("createTime");
					        return date1.compareTo(date2); 
					    }
					});
					Collections.reverse(ans);
			}	
			/*搜索未完成任务*/
			else if(state==0) {
//				List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).list();
				List<Task> tasks = ActivitiUtils.getTaskList(username, "subscribeTicket"); 
				for(Task task:tasks) {
					 String processInstanceId = task.getProcessInstanceId();
					 for(HyTicketSubscribe tmp:ticketSubscribeList) {
						 if(processInstanceId.equals(tmp.getProcessInstanceId())) {
							 HashMap<String, Object> map = new HashMap<>();
							 map.put("id", tmp.getId());
							 map.put("productId",tmp.getProductId());
							 map.put("sceneName",tmp.getSceneName());
							 map.put("createTime", tmp.getCreateTime());
							 map.put("creator", tmp.getCreator().getName());
							 map.put("auditStatus", tmp.getAuditStatus());
							 map.put("state", 0);
							 ans.add(map);
						 }					 
					 }
				 }
				 Collections.sort(ans, new Comparator<Map<String, Object>>() {
						@Override
						public int compare(Map<String, Object> o1, Map<String, Object> o2) {
							Date date1 = (Date) o1.get("createTime");
							Date date2 = (Date) o2.get("createTime");
							return date1.compareTo(date2); 
						}
					});
				    Collections.reverse(ans);		
			}
			/*搜索已完成任务*/
			else if(state==1) {
//				List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
//						.finished().taskAssignee(username).list();
				List<HistoricTaskInstance> historicTaskInstances = ActivitiUtils.getHistoryTaskList(username, "subscribeTicket");
				for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
			     String processInstanceId = historicTaskInstance.getProcessInstanceId();
				 for(HyTicketSubscribe tmp:ticketSubscribeList) {
					 if(processInstanceId.equals(tmp.getProcessInstanceId())) {
						 HashMap<String, Object> map = new HashMap<>();
						 map.put("id", tmp.getId());
						 map.put("productId",tmp.getProductId());
						 map.put("sceneName",tmp.getSceneName());
						 map.put("createTime", tmp.getCreateTime());
						 map.put("creator", tmp.getCreator().getName());
						 map.put("auditStatus", tmp.getAuditStatus());
						 map.put("state", 1);
						 ans.add(map);
					}
				}
			}
			 Collections.sort(ans, new Comparator<Map<String, Object>>() {
				    @Override
				    public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				        Date date1 = (Date) o1.get("createTime");
				        Date date2 = (Date) o2.get("createTime");
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
			json.setObj(answer);
			json.setMsg("查询成功");
		}
		catch(Exception e) {
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	
	@RequestMapping(value="detail/view")
	@ResponseBody
	public Json detail(Long id)
	{
		Json json=new Json();
		try{
			HyTicketSubscribe hyTicketSubscribe=hyTicketSubscribeService.find(id);
			String processInstanceId = hyTicketSubscribe.getProcessInstanceId();
			List<Comment> commentList = taskService.getProcessInstanceComments(processInstanceId);
			Collections.reverse(commentList);
			List<Map<String, Object>> list = new LinkedList<>();
			for (Comment comment : commentList) {
				Map<String, Object> obj = new HashMap<>();
				String taskId = comment.getTaskId();
				HistoricTaskInstance task = historyService.createHistoricTaskInstanceQuery().taskId(taskId)
						.singleResult();
				String step = "";
				if (task != null) {
					step = task.getName();
				}
				obj.put("step", step);
				String username = comment.getUserId();
				HyAdmin hyAdmin = hyAdminService.find(username);
				String name = "";
				if (hyAdmin != null) {
					name = hyAdmin.getName();
				}
				obj.put("name", name);
				String str = comment.getFullMessage();
				int index = str.lastIndexOf(":");
				if (index < 0) {
					obj.put("comment", " ");
					obj.put("result", 1);
				} else {
					obj.put("comment", str.substring(0, index));
					obj.put("result", Integer.parseInt(str.substring(index + 1)));
				}
				obj.put("time", comment.getTime());

				list.add(obj);
			}
			HashMap<String,Object> map=new HashMap<String,Object>();
			map.put("auditList", list);	
			map.put("productId", hyTicketSubscribe.getProductId());
			map.put("sceneName", hyTicketSubscribe.getSceneName());
			if(hyTicketSubscribe.getTicketSupplier().getIsInner()==true) {
				map.put("supplierName", hyTicketSubscribe.getHySupplierElement().getName());
			}		
			map.put("areaId", hyTicketSubscribe.getArea().getId());
			map.put("areaName", hyTicketSubscribe.getArea().getFullName());
			map.put("sceneAddress", hyTicketSubscribe.getSceneAddress());
			map.put("star", hyTicketSubscribe.getStar());
			map.put("openTime", hyTicketSubscribe.getOpenTime());
			map.put("closeTime", hyTicketSubscribe.getCloseTime());
			map.put("ticketExchangeAddress", hyTicketSubscribe.getTicketExchangeAddress());
			if(hyTicketSubscribe.getRestrictArea()!=null) {
				map.put("restrictId", hyTicketSubscribe.getRestrictArea().getId());
				map.put("restrictArea", hyTicketSubscribe.getRestrictArea().getFullName());
			}
			else {
				map.put("restrictId", null);
				map.put("restrictArea", null);
			}
			map.put("minPurchaseQuantity", hyTicketSubscribe.getMinPurchaseQuantity());
			map.put("days", hyTicketSubscribe.getDays()); //预约天数
			map.put("reserveTime", hyTicketSubscribe.getReserveTime()); //预约时间
			map.put("refundReq", hyTicketSubscribe.getRefundReq()); //退款说明
			map.put("reserveKnow", hyTicketSubscribe.getReserveKnow()); //预定须知
			map.put("introduction", hyTicketSubscribe.getIntroduction()); //产品介绍
			map.put("ticketFile",hyTicketSubscribe.getTicketFile()); //票务推广文件
			List<HyTicketSubscribePrice> prices=new ArrayList<>(hyTicketSubscribe.getHyTicketSubscribePrices());
			List<HashMap<String,Object>> priceList=new ArrayList<>();
			for(HyTicketSubscribePrice price:prices) {
				HashMap<String,Object> priceMap=new HashMap<>();
				priceMap.put("startDate", price.getStartDate());
				priceMap.put("endDate",price.getEndDate());
				priceMap.put("adultListPrice",price.getAdultListPrice());
				priceMap.put("adultOutsalePrice",price.getAdultOutsalePrice());
				priceMap.put("adultSettlePrice",price.getAdultSettlePrice());
				priceMap.put("childListPrice",price.getChildListPrice());
				priceMap.put("childOutPrice",price.getChildOutPrice());
				priceMap.put("childSettlePrice",price.getChildSettlePrice());
				priceMap.put("studentListPrice",price.getStudentListPrice());
				priceMap.put("studentOutsalePrice",price.getStudentOutsalePrice());
				priceMap.put("studentSettlePrice",price.getStudentSettlePrice());
				priceMap.put("oldListPrice",price.getOldListPrice());
				priceMap.put("oldOutsalePrice",price.getOldOutsalePrice());
				priceMap.put("oldSettlePrice",price.getOldSettlePrice());
				priceMap.put("inventory",price.getInventory());
				priceList.add(priceMap);
			}
			map.put("priceList", priceList);
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
	
	@RequestMapping("audit")
	@ResponseBody
	public Json audit(Long id,String comment,Integer state,HttpSession session)
	{
		Json json=new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyTicketSubscribe hyTicketSubscribe=hyTicketSubscribeService.find(id);
			String processInstanceId=hyTicketSubscribe.getProcessInstanceId();
			if (processInstanceId == null || processInstanceId == "") {
				json.setSuccess(false);
				json.setMsg("审核出错，信息不完整，请重新申请");
			} 
			else {
				Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
				//审核通过
				if(state==1) {
					hyTicketSubscribe.setAuditStatus(3); //审核通过
					hyTicketSubscribe.setSaleStatus(2); //上架
					hyTicketSubscribeService.update(hyTicketSubscribe);
					List<HyTicketSubscribePrice> hyTicketSubscribePrices=new ArrayList<>(hyTicketSubscribe.getHyTicketSubscribePrices());
				    for(HyTicketSubscribePrice ticketSubscribePrice:hyTicketSubscribePrices) {
				    	Date startDate=ticketSubscribePrice.getStartDate();
				    	Date endDate=ticketSubscribePrice.getEndDate();
				    	Integer inventory=ticketSubscribePrice.getInventory();
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
				             ticketInbound.setType(2); //类型为认购门票
				             ticketInbound.setPriceInboundId(ticketSubscribePrice.getId());
				             ticketInbound.setInventory(inventory);
				             hyTicketInboundService.save(ticketInbound);
				             cld.add(Calendar.DATE, 1); //获取指定日期后一天
				             tmp = cld.getTime();
				    	}
				    }
				}
				//驳回
				else {
					hyTicketSubscribe.setAuditStatus(4); //审核驳回
					hyTicketSubscribeService.update(hyTicketSubscribe);
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
		catch(Exception e) {
			e.printStackTrace();
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
}
