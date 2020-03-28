package com.hongyu.controller.hzj03.audit;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.grain.util.FilterUtil;
import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Filter.Operator;
import com.hongyu.Json;
import com.hongyu.Pageable;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyGroup;
import com.hongyu.entity.HyLine;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HyTicketHotel;
import com.hongyu.entity.HyTicketHotelRoom;
import com.hongyu.entity.HyTicketHotelandscene;
import com.hongyu.entity.HyTicketPriceInbound;
import com.hongyu.entity.SupplierDismissOrderApply;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.HySupplierContractService;
import com.hongyu.service.HyTicketHotelRoomService;
import com.hongyu.service.HyTicketHotelandsceneService;
import com.hongyu.service.HyTicketPriceInboundService;
import com.hongyu.service.HyTicketSceneTicketManagementService;
import com.hongyu.service.SupplierDismissOrderApplyService;

/** 财务 - 审核 - 供应商驳回订单
 *
 * @author xyy
 * */
@Controller
@RequestMapping("admin/accountant/dissmissOrder")
public class AccountantReview_SupplierDismissOrder_Controller {
	@Resource(name = "hyGroupServiceImpl")
	private HyGroupService hyGroupService;
	
	@Resource(name = "hyOrderServiceImpl")
	private HyOrderService hyOrderService;
	
	@Resource(name = "supplierDismissOrderApplyServiceImpl")
	private SupplierDismissOrderApplyService supplierDismissOrderApplyService;

	@Resource(name = "hyAdminServiceImpl")
	private HyAdminService hyAdminService;

	@Resource(name="hyTicketHotelandsceneServiceImpl")
	private HyTicketHotelandsceneService hyTicketHotelandsceneService;
	
	@Resource(name="hyTicketPriceInboundServiceImpl")
	private HyTicketPriceInboundService hyTicketPriceInboundService;

	@Resource
	private TaskService taskService;
	@Resource
	private HistoryService historyService;

	/** 供应商驳回订单审核-列表 */
	@RequestMapping(value = "/list/view")
	@ResponseBody
	public Json subscribeTicketList(Pageable pageable,String startDate, String endDate, Integer state, SupplierDismissOrderApply supplierDismissOrderApply,
			HttpSession session) {
		Json json = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			List<Filter> filters = FilterUtil.getInstance().getFilter(supplierDismissOrderApply);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if (startDate != null && !startDate.equals(""))
				filters.add(new Filter("createTime", Operator.ge,
						sdf.parse(startDate.substring(0, 10) + " " + "00:00:00")));
			if (endDate != null && !endDate.equals(""))
				filters.add(
						new Filter("createTime", Operator.le, sdf.parse(endDate.substring(0, 10) + " " + "23:59:59")));
			
			List<SupplierDismissOrderApply> supplierDismissOrderApplys = supplierDismissOrderApplyService.findList(null, filters,
					null);
			List<Map<String, Object>> ans = new ArrayList<>();
			Map<String, Object> answer = new HashMap<>();
			if (state == null) {
				List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).list();
				for (Task task : tasks) {
					String processInstanceId = task.getProcessInstanceId();
					for (SupplierDismissOrderApply tmp : supplierDismissOrderApplys) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							HashMap<String, Object> m = new HashMap<>();
							m.put("id", tmp.getId());
							m.put("state", 0);
							m.put("money", tmp.getMoney()); 
							m.put("applier", tmp.getOperator()==null?"":tmp.getOperator().getName()); // 申请人 

							m.put("createTime", tmp.getCreateTime());
							ans.add(m);
						}
					}
				}
				List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
						.finished().taskAssignee(username).list();
				for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
					String processInstanceId = historicTaskInstance.getProcessInstanceId();
					for (SupplierDismissOrderApply tmp : supplierDismissOrderApplys) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							HashMap<String, Object> m = new HashMap<>();
							m.put("id", tmp.getId());
							m.put("state", 1);
							m.put("money", tmp.getMoney()); 
							m.put("applier", tmp.getOperator()==null?"":tmp.getOperator().getName()); // 申请人 
							m.put("createTime", tmp.getCreateTime());
							ans.add(m);
						}
					}
				}
				Collections.sort(ans, new Comparator<Map<String, Object>>() {
					@Override
					public int compare(Map<String, Object> o1, Map<String, Object> o2) {
						long id1 = (long) o1.get("id");
						long id2 = (long) o2.get("id");
						return id2 > id1 ? 1 : -1;
					}
				});
			} else if (state == 0) {// 搜索未完成任务
				List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).orderByTaskCreateTime()
						.desc().list();

				for (Task task : tasks) {
					String processInstanceId = task.getProcessInstanceId();
					for (SupplierDismissOrderApply tmp : supplierDismissOrderApplys) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							HashMap<String, Object> m = new HashMap<>();
							m.put("id", tmp.getId());
							m.put("state", 0);
							m.put("money", tmp.getMoney()); 
							m.put("applier", tmp.getOperator()==null?"":tmp.getOperator().getName()); // 申请人 
							m.put("createTime", tmp.getCreateTime());
							ans.add(m);
						}
					}
				}

			} else if (state == 1) {// 搜索已完成任务
				List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
						.orderByHistoricTaskInstanceStartTime().desc().finished().taskAssignee(username).list();

				for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
					String processInstanceId = historicTaskInstance.getProcessInstanceId();
					for (SupplierDismissOrderApply tmp : supplierDismissOrderApplys) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							HashMap<String, Object> m = new HashMap<>();
							m.put("id", tmp.getId());
							m.put("state", 1);
							m.put("money", tmp.getMoney()); 
							m.put("applier", tmp.getOperator()==null?"":tmp.getOperator().getName()); // 申请人 
							m.put("createTime", tmp.getCreateTime());
							ans.add(m);
						}
					}
				}
			}
			int page = pageable.getPage();
			int rows = pageable.getRows();
			answer.put("total", ans.size());
			answer.put("pageNumber", page);
			answer.put("pageSize", rows);
			answer.put("rows", ans.subList((page - 1) * rows, page * rows > ans.size() ? ans.size() : page * rows)); // 手动分页？
			json.setSuccess(true);
			if (ans.size() == 0) {
				json.setMsg("未获取到符合条件的数据");
			} else
				json.setMsg("获取成功");
			json.setObj(answer);
			return json;
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取失败");
			e.printStackTrace();
		}
		return json;
	}

	/** 供应商驳回订单审核 - 详情 */
	@RequestMapping(value = "/detail/view")
	@ResponseBody
	public Json getHistoryComments(Long id) {
		Json json = new Json();
		HashMap<String, Object> obj = new HashMap<>();

		try {
			SupplierDismissOrderApply supplierDismissOrderApply = supplierDismissOrderApplyService.find(id);

			// 审核步骤
			String processInstanceId = supplierDismissOrderApply.getProcessInstanceId();
			List<Comment> commentList = taskService.getProcessInstanceComments(processInstanceId);
			Collections.reverse(commentList);
			List<Map<String, Object>> list = new LinkedList<>();
			for (Comment comment : commentList) {
				Map<String, Object> map = new HashMap<>();
				String taskId = comment.getTaskId();
				HistoricTaskInstance task = historyService.createHistoricTaskInstanceQuery().taskId(taskId)
						.singleResult();
				String step = "";
				if (task != null) {
					step = task.getName();
				}
				map.put("step", step);
				String username = comment.getUserId();
				HyAdmin hyAdmin = hyAdminService.find(username);
				String name = "";
				if (hyAdmin != null) {
					name = hyAdmin.getName();
				}
				map.put("name", name);
				String str = comment.getFullMessage();
				int index = str.lastIndexOf(":");
				if (index < 0) {
					map.put("comment", " ");
					map.put("result", 1);
				} else {
					map.put("comment", str.substring(0, index));
					map.put("result", Integer.parseInt(str.substring(index + 1)));
				}
				map.put("time", comment.getTime());

				list.add(map);
			}

			obj.put("auditlist", list);

			// 供应商信息
			HyOrder hyOrder = hyOrderService.find(supplierDismissOrderApply.getOrderId());
			Long productId = hyOrder.getOrderItems().iterator().next().getProductId();
			Integer type=hyOrder.getType();
			//如果是线路订单
			if(type==1) {
				HyGroup hyGroup = hyGroupService.find(productId);
				HyLine hyLine = hyGroup.getLine();
				obj.put("supplierName", hyLine.getHySupplier().getSupplierName());  // 供应商名称
				obj.put("sn", hyLine.getPn());  // 产品编号
				obj.put("productName", hyLine.getName());  // 线路名称
			}
			
			//票务酒店订单
			else if(type==3) {
				Long priceId=hyOrder.getOrderItems().get(0).getPriceId();
				HyTicketPriceInbound priceInbound=hyTicketPriceInboundService.find(priceId);
				obj.put("supplierName", priceInbound.getHyTicketHotelRoom().getHyTicketHotel().getTicketSupplier().getSupplierName());  // 供应商名称
				obj.put("sn", priceInbound.getHyTicketHotelRoom().getProductId());  // 产品编号
				obj.put("productName", priceInbound.getHyTicketHotelRoom().getProductName());  // 产品名称
			}
			
			//票务门票订单
			else if(type==4) {
				Long priceId=hyOrder.getOrderItems().get(0).getPriceId();
				HyTicketPriceInbound priceInbound=hyTicketPriceInboundService.find(priceId);
				obj.put("supplierName", priceInbound.getHyTicketSceneTicketManagement().getHyTicketScene().getTicketSupplier().getSupplierName());  // 供应商名称
				obj.put("sn", priceInbound.getHyTicketSceneTicketManagement().getProductId());  // 产品编号
				obj.put("productName", priceInbound.getHyTicketSceneTicketManagement().getProductName());  // 产品名称
			}
			
			//如果是票务酒加景订单
			else if(type==5) {
				HyTicketHotelandscene hotelandscene=hyTicketHotelandsceneService.find(productId);
				obj.put("supplierName", hotelandscene.getTicketSupplier().getSupplierName());
				obj.put("sn", hotelandscene.getProductId()); //产品编号
				obj.put("productName", hotelandscene.getProductName());  // 产品名称
			}
			obj.put("orderNum", hyOrder.getOrderNumber()); // 订单号
			obj.put("applyDate", supplierDismissOrderApply.getCreateTime());
			obj.put("applier", supplierDismissOrderApply.getOperator().getName());
			obj.put("refundMoney", supplierDismissOrderApply.getMoney());
			
			
			json.setObj(obj);
			json.setSuccess(true);
			
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("操作失败");
		}
		return json;
	}

	/** 供应商驳回订单审核 - 操作 */
	@RequestMapping("/audit")
	@ResponseBody
	public Json audit(Long id, String comment, Integer state, HttpSession session) {
		Json j = new Json();
		try {
			j = supplierDismissOrderApplyService.addSupplierDismissOrderAudit(id, comment, state, session);
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("审核失败");
			e.printStackTrace();
		}
		return j;
	}
}
