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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.grain.util.FilterUtil;
import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyTicketHotel;
import com.hongyu.entity.HyTicketHotelRoom;
import com.hongyu.entity.HyTicketInbound;
import com.hongyu.entity.HyTicketPriceInbound;
import com.hongyu.entity.HyTicketRefund;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyTicketHotelRoomService;
import com.hongyu.service.HyTicketHotelService;
import com.hongyu.service.HyTicketInboundService;
import com.hongyu.service.HyTicketRefundService;
import com.hongyu.util.ActivitiUtils;
/**
 * 品控审核酒店
 * Author:GSbing
 */
@RestController
@RequestMapping("/admin/pinkong/hotelroomprice")
@Transactional(propagation = Propagation.REQUIRED)
public class Review_ticket_hotelPriceController {
	@Resource
	private TaskService taskService;

	@Resource
	private RuntimeService runtimeService;
	
	@Resource
	private HistoryService historyService;
	@Resource(name="hyAdminServiceImpl")
	private HyAdminService hyAdminService;
	@Resource(name="hyTicketHotelServiceImpl")
	private HyTicketHotelService hyTicketHotelService;
	@Resource(name="hyTicketHotelRoomServiceImpl")
	private HyTicketHotelRoomService hyTicketHotelRoomService;
	
	@Resource(name="hyTicketInboundServiceImpl")
	private HyTicketInboundService hyTicketInboundService;
	
	@Resource(name="hyTicketRefundServiceImpl")
	private HyTicketRefundService hyTicketRefundService;
	
	@RequestMapping(value="list/view")
	@ResponseBody
	public Json ticketList(HyTicketHotelRoom queryParam,String submitName,Integer state,Pageable pageable,HttpSession session)
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
					filters.add(Filter.in("submitter",adminList));
					List<HyTicketHotelRoom> hotelRooms=hyTicketHotelRoomService.findList(null,filters,null);
					List<Map<String, Object>> ans = new ArrayList<>();
					Map<String, Object> answer = new HashMap<>();
					if(state==null) {
//						List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).list();
					    List<Task> tasks=ActivitiUtils.getTaskList(username, "hotelRoomPriceProcess");
						for(Task task:tasks) {
					    	String processInstanceId = task.getProcessInstanceId();
					    	for(HyTicketHotelRoom tmp:hotelRooms) {
					    		if(processInstanceId.equals(tmp.getProcessInstanceId())) {
					    			HashMap<String, Object> map = new HashMap<>();
					    			map.put("id", tmp.getId());
					    			map.put("state", 0);
					    			map.put("productId", tmp.getProductId());
					    			map.put("productName", tmp.getProductName());
									if(tmp.getSubmitter()!=null){
										map.put("submitName", tmp.getSubmitter().getName());
									}						
									map.put("submitTime", tmp.getSubmitTime());
									map.put("auditStatus", tmp.getAuditStatus());
									ans.add(map);
					    		}
					    	}
					    }
//					    List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
//								.finished().taskAssignee(username).list();
						List<HistoricTaskInstance> historicTaskInstances = ActivitiUtils.getHistoryTaskList(username, "hotelRoomPriceProcess");
					    for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
					    	String processInstanceId = historicTaskInstance.getProcessInstanceId();
					    	for(HyTicketHotelRoom tmp:hotelRooms) {
					    		if(processInstanceId.equals(tmp.getProcessInstanceId())) {
					    			HashMap<String, Object> map = new HashMap<>();
					    			map.put("id", tmp.getId());
					    			map.put("state", 1);
					    			map.put("productId", tmp.getProductId());
					    			map.put("productName", tmp.getProductName());
									if(tmp.getSubmitter()!=null){
										map.put("submitName", tmp.getSubmitter().getName());
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
						List<Task> tasks=ActivitiUtils.getTaskList(username, "hotelRoomPriceProcess");
						for(Task task:tasks) {
					    	String processInstanceId = task.getProcessInstanceId();
					    	for(HyTicketHotelRoom tmp:hotelRooms) {
					    		if(processInstanceId.equals(tmp.getProcessInstanceId())) {
					    			HashMap<String, Object> map = new HashMap<>();
					    			map.put("id", tmp.getId());
					    			map.put("state", 0);
					    			map.put("productId", tmp.getProductId());
					    			map.put("productName", tmp.getProductName());
									if(tmp.getSubmitter()!=null){
										map.put("submitName", tmp.getSubmitter().getName());
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
						List<HistoricTaskInstance> historicTaskInstances = ActivitiUtils.getHistoryTaskList(username, "hotelRoomPriceProcess");
						for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
					    	String processInstanceId = historicTaskInstance.getProcessInstanceId();
					    	for(HyTicketHotelRoom tmp:hotelRooms) {
					    		if(processInstanceId.equals(tmp.getProcessInstanceId())) {
					    			HashMap<String, Object> map = new HashMap<>();
					    			map.put("id", tmp.getId());
					    			map.put("state", 1);
					    			map.put("productId", tmp.getProductId());
					    			map.put("productName", tmp.getProductName());
									if(tmp.getSubmitter()!=null){
										map.put("submitName", tmp.getSubmitter().getName());
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
			//submitName==null
			else {
				List<HyTicketHotelRoom> hotelRooms=hyTicketHotelRoomService.findList(null,filters,null);
				List<Map<String, Object>> ans = new ArrayList<>();
				Map<String, Object> answer = new HashMap<>();
				if(state==null) {
//					List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).list();
					List<Task> tasks=ActivitiUtils.getTaskList(username, "hotelRoomPriceProcess");
					for(Task task:tasks) {
				    	String processInstanceId = task.getProcessInstanceId();
				    	for(HyTicketHotelRoom tmp:hotelRooms) {
				    		if(processInstanceId.equals(tmp.getProcessInstanceId())) {
				    			HashMap<String, Object> map = new HashMap<>();
				    			map.put("id", tmp.getId());
				    			map.put("state", 0);
				    			map.put("productId", tmp.getProductId());
				    			map.put("productName", tmp.getProductName());
								if(tmp.getSubmitter()!=null){
									map.put("submitName", tmp.getSubmitter().getName());
								}						
								map.put("submitTime", tmp.getSubmitTime());
								map.put("auditStatus", tmp.getAuditStatus());
								ans.add(map);
				    		}
				    	}
				    }
//				    List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
//							.finished().taskAssignee(username).list();
					List<HistoricTaskInstance> historicTaskInstances = ActivitiUtils.getHistoryTaskList(username, "hotelRoomPriceProcess");
					for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
				    	String processInstanceId = historicTaskInstance.getProcessInstanceId();
				    	for(HyTicketHotelRoom tmp:hotelRooms) {
				    		if(processInstanceId.equals(tmp.getProcessInstanceId())) {
				    			HashMap<String, Object> map = new HashMap<>();
				    			map.put("id", tmp.getId());
				    			map.put("state", 1);
				    			map.put("productId", tmp.getProductId());
				    			map.put("productName", tmp.getProductName());
								if(tmp.getSubmitter()!=null){
									map.put("submitName", tmp.getSubmitter().getName());
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
					List<Task> tasks=ActivitiUtils.getTaskList(username, "hotelRoomPriceProcess");
					for(Task task:tasks) {
				    	String processInstanceId = task.getProcessInstanceId();
				    	for(HyTicketHotelRoom tmp:hotelRooms) {
				    		if(processInstanceId.equals(tmp.getProcessInstanceId())) {
				    			HashMap<String, Object> map = new HashMap<>();
				    			map.put("id", tmp.getId());
				    			map.put("state", 0);
				    			map.put("productId", tmp.getProductId());
				    			map.put("productName", tmp.getProductName());
								if(tmp.getSubmitter()!=null){
									map.put("submitName", tmp.getSubmitter().getName());
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
					List<HistoricTaskInstance> historicTaskInstances = ActivitiUtils.getHistoryTaskList(username, "hotelRoomPriceProcess");
					for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
				    	String processInstanceId = historicTaskInstance.getProcessInstanceId();
				    	for(HyTicketHotelRoom tmp:hotelRooms) {
				    		if(processInstanceId.equals(tmp.getProcessInstanceId())) {
				    			HashMap<String, Object> map = new HashMap<>();
				    			map.put("id", tmp.getId());
				    			map.put("state", 1);
				    			map.put("productId", tmp.getProductId());
				    			map.put("productName", tmp.getProductName());
								if(tmp.getSubmitter()!=null){
									map.put("submitName", tmp.getSubmitter().getName());
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
	public Json detail(Long roomId)
	{
		Json json=new Json();
		try{
			HyTicketHotelRoom room=hyTicketHotelRoomService.find(roomId);
			HyTicketHotel hotel=room.getHyTicketHotel();
			Map<String,Object> map=new HashMap<String,Object>();
			
			 /*酒店信息*/
			map.put("hotelName", hotel.getHotelName());
			map.put("area", hotel.getArea().getFullName());
			if(hotel.getTicketSupplier().getIsInner()==true) {
				map.put("supplierName", hotel.getHySupplierElement().getName());
			}	
			map.put("hotelAddress", hotel.getAddress());
			map.put("star", hotel.getStar());
			map.put("reserveKnow", hotel.getReserveKnow());
			map.put("refundReq", hotel.getRefundReq());
			map.put("refundType", hotel.getRefundType());
			map.put("introduction", hotel.getIntroduction()); //产品介绍
			map.put("ticketFile",hotel.getTicketFile()); //票务推广文件
			List<Map<String,Object>> refundList=new ArrayList<>();			
			List<Filter> filters=new ArrayList<>();
			filters.add(Filter.eq("type", 1));//1-酒店
			filters.add(Filter.eq("productId",hotel.getId()));
			List<HyTicketRefund> ticketRefunds=hyTicketRefundService.findList(null,filters,null);
			for(HyTicketRefund hyTicketRefund:ticketRefunds) {
				Map<String,Object> obj=new HashMap<>();
				obj.put("startDay", hyTicketRefund.getStartDay());
				obj.put("startTime", hyTicketRefund.getStartTime());
				obj.put("endDay", hyTicketRefund.getEndDay());
				obj.put("endTime", hyTicketRefund.getEndTime());
				obj.put("percentage", hyTicketRefund.getPercentage());
				refundList.add(obj);
			}
			map.put("hyTicketRefunds",refundList);
			
			/*审核记录*/
			String processInstanceId = room.getProcessInstanceId();
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
			
			/*房间信息*/
			map.put("productId", room.getProductId());
			map.put("productName", room.getProductName());
			map.put("roomType", room.getRoomType());
			map.put("isWifi", room.getIsWifi());	
			map.put("isWindow", room.getIsWindow());
			map.put("isBathroom", room.getIsBathroom());
			map.put("available", room.getAvailable());
			map.put("breakfast", room.getBreakfast());
			map.put("reserveDays", room.getReserveDays());
			map.put("reserveTime",room.getReserveTime());
			
			/*价格库存信息*/
			List<Map<String, Object>> priceMaplist = new LinkedList<>();
			List<HyTicketPriceInbound> priceList=new ArrayList<>(room.getHyTicketPriceInbounds());
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
	
	/** 酒店房间价格库存提交 - 审核*/
	@RequestMapping("audit")
	@ResponseBody
	@Transactional(propagation = Propagation.REQUIRED)
	public Json audit(Long roomId,String comment,Integer state,HttpSession session)
	{
		Json json=new Json();
		try{
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyTicketHotelRoom room=hyTicketHotelRoomService.find(roomId);
			String processInstanceId=room.getProcessInstanceId();
			if (processInstanceId == null || processInstanceId == "") {
				json.setSuccess(false);
				json.setMsg("审核出错，信息不完整，请重新申请");
			} 
			else {
				Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
				if (state == 1)  // 审核通过
				{
					room.setAuditStatus(3);//审核通过
					room.setSaleStatus(2);//上架
					//修改门户完善状态
					HyTicketHotel hotel=room.getHyTicketHotel();
					if(hotel.getMhState()!=null) {
						if(hotel.getMhState()==1) {
							hotel.setMhState(2); //供应商修改,待完善
							hyTicketHotelService.update(hotel);
						}	
					}			
					hyTicketHotelRoomService.update(room);
				    List<HyTicketPriceInbound> hyTicketPriceInbounds=new ArrayList<>(room.getHyTicketPriceInbounds());
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
					room.setAuditStatus(4); //已驳回
					hyTicketHotelRoomService.update(room);
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
