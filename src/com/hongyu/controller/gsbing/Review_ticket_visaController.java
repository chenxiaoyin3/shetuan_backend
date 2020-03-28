package com.hongyu.controller.gsbing;

import java.util.ArrayList;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.grain.util.FilterUtil;
import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Pageable;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyVisa;
import com.hongyu.entity.HyVisaPic;
import com.hongyu.entity.HyVisaPrices;
import com.hongyu.service.CommonSequenceService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyAreaService;
import com.hongyu.service.HySupplierService;
import com.hongyu.service.HyVisaPricesService;
import com.hongyu.service.HyVisaService;
import com.hongyu.util.ActivitiUtils;

/**
 * 品控审核签证列表页
 * Author:GSbing
 */
@Controller
@RequestMapping("admin/pinkong/visaPrice")
public class Review_ticket_visaController {
	@Resource
	private TaskService taskService;

	@Resource
	private HistoryService historyService;
	
	@Resource
	private RuntimeService runtimeService;
	
	@Resource(name="hyAdminServiceImpl")
	private HyAdminService hyAdminService;
	
	@Resource(name="hySupplierServiceImpl")
	private HySupplierService hySupplierService;
	
	@Resource(name="hyAreaServiceImpl")
	private HyAreaService hyAreaService;
	
	@Resource(name="hyVisaServiceImpl")
	private HyVisaService hyVisaService;
	
	@Resource(name="hyVisaPricesServiceImpl")
	private HyVisaPricesService hyVisaPricesService;
	
	@Resource(name="commonSequenceServiceImp")
	private CommonSequenceService commonSequenceService;
	
	@RequestMapping(value="list/view")
	@ResponseBody
	public Json listview(Pageable pageable,HyVisa queryParam,Integer state,HttpSession session)
	{
		Json json=new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			List<Filter> filters = FilterUtil.getInstance().getFilter(queryParam);
			List<HyVisa> visaList=hyVisaService.findList(null,filters,null);
			List<Map<String, Object>> ans = new ArrayList<>();
			Map<String, Object> answer = new HashMap<>();
			if(state==null) {
//				List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).list();
				List<Task> tasks = ActivitiUtils.getTaskList(username, "visaPriceProcess");
				for(Task task:tasks) {
					 String processInstanceId = task.getProcessInstanceId();
					 for(HyVisa tmp:visaList) {
						 if(processInstanceId.equals(tmp.getProcessInstanceId())) {
							 HashMap<String, Object> map = new HashMap<>();
							 map.put("id", tmp.getId());
							 map.put("productId",tmp.getProductId());
							 map.put("productName",tmp.getProductName());
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
				 List<HistoricTaskInstance> historicTaskInstances = ActivitiUtils.getHistoryTaskList(username, "visaPriceProcess");
				 for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
				     String processInstanceId = historicTaskInstance.getProcessInstanceId();
					 for(HyVisa tmp:visaList) {
						 if(processInstanceId.equals(tmp.getProcessInstanceId())) {
							 HashMap<String, Object> map = new HashMap<>();
							 map.put("id", tmp.getId());
							 map.put("productId",tmp.getProductId());
							 map.put("productName",tmp.getProductName());
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
				List<Task> tasks = ActivitiUtils.getTaskList(username, "visaPriceProcess"); 
				for(Task task:tasks) {
					 String processInstanceId = task.getProcessInstanceId();
					 for(HyVisa tmp:visaList) {
						 if(processInstanceId.equals(tmp.getProcessInstanceId())) {
							 HashMap<String, Object> map = new HashMap<>();
							 map.put("id", tmp.getId());
							 map.put("productId",tmp.getProductId());
							 map.put("productName",tmp.getProductName());
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
				List<HistoricTaskInstance> historicTaskInstances = ActivitiUtils.getHistoryTaskList(username, "visaPriceProcess");
				for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
					String processInstanceId = historicTaskInstance.getProcessInstanceId();
					for(HyVisa tmp:visaList) {
						if(processInstanceId.equals(tmp.getProcessInstanceId())) {
							HashMap<String, Object> map = new HashMap<>();
							map.put("id", tmp.getId());
							map.put("productId",tmp.getProductId());
							map.put("productName",tmp.getProductName());
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
			json.setMsg("查询成功");
			json.setObj(answer);
		}
		catch(Exception e) {
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping("detail/view")
	@ResponseBody
	public Json detail(Long id)
	{
		Json json=new Json();
		try{
			HyVisa hyVisa=hyVisaService.find(id);
			String processInstanceId = hyVisa.getProcessInstanceId();
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
			if(hyVisa.getTicketSupplier()!=null) {
				map.put("supplierName",hyVisa.getTicketSupplier().getSupplierName());
			}
			else {
				map.put("supplierName",null);
			}
			map.put("productId", hyVisa.getProductId());
			map.put("productName", hyVisa.getProductName());
			map.put("creator", hyVisa.getCreator().getName());
			map.put("area", hyVisa.getCountry().getName());
			map.put("visaType", hyVisa.getVisaType());
			map.put("duration", hyVisa.getDuration());
			map.put("times", hyVisa.getTimes());
			map.put("isInterview", hyVisa.getIsInterview());
			map.put("stayDays", hyVisa.getStayDays());
			map.put("expireDays", hyVisa.getExpireDays());
			map.put("serviceContent", hyVisa.getServiceContent());
			map.put("priceContain", hyVisa.getPriceContain());
			map.put("reserveRequirement", hyVisa.getReserveRequirement());
			map.put("accessory",hyVisa.getAccessory());
			map.put("introduce",hyVisa.getIntroduce());
			map.put("introduction", hyVisa.getIntroduction()); //产品介绍
			map.put("ticketFile",hyVisa.getTicketFile()); //票务推广文件
//			List<HyVisaPic> pics= hyVisa.getHyVisaPics();
//			List<HashMap<String,Object>> picList=new ArrayList<>();
//			for(HyVisaPic visaPic:pics) {
//				HashMap<String,Object> picMap=new HashMap<String,Object>();
//				picMap.put("source", visaPic.getSource());
//				picMap.put("large",visaPic.getLarge());
//				picMap.put("medium",visaPic.getMedium());
//				picMap.put("thumbnail", visaPic.getThumbnail());
//				picList.add(picMap);
//			}
//			map.put("hyVisaPics", picList);
			List<HyVisaPrices> prices=hyVisa.getHyVisaPrices();
			List<HashMap<String,Object>> priceList=new ArrayList<>();
			for(HyVisaPrices price:prices) {
				HashMap<String,Object> priceMap=new HashMap<String,Object>();
				priceMap.put("startDate", price.getStartDate());
				priceMap.put("endDate",price.getEndDate());
				priceMap.put("displayPrice",price.getDisplayPrice());
				priceMap.put("sellPrice",price.getSellPrice());
				priceMap.put("settlementPrice",price.getSettlementPrice());
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
	
	/*审核*/
	@RequestMapping("audit")
	@ResponseBody
	public Json audit(Long id,String comment,Integer state,HttpSession session)
	{
		Json json=new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyVisa hyVisa=hyVisaService.find(id);
			String processInstanceId=hyVisa.getProcessInstanceId();
			if (processInstanceId == null || processInstanceId == "") {
				json.setSuccess(false);
				json.setMsg("审核出错，信息不完整，请重新申请");
			} 
			else {
				Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
				//审核通过
				if(state==1) {
					hyVisa.setAuditStatus(3); //审核通过
					hyVisa.setSaleStatus(2); //上架
					if(hyVisa.getMhState()!=null) {
						if(hyVisa.getMhState()==1) {
							hyVisa.setMhState(2); //供应商有修改,待完善
						}
					}			
					hyVisaService.update(hyVisa);
				}
				//驳回
				else {
					hyVisa.setAuditStatus(4); //审核驳回
					hyVisaService.update(hyVisa);
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
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
}
