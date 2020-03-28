package com.hongyu.controller.hzj03.audit;

import java.text.SimpleDateFormat;
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
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.grain.util.FilterUtil;
import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Pageable;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HySubscribeTicket;
import com.hongyu.entity.HySubscribeTicket.SaleStatus;
import com.hongyu.entity.HySubscribeTicketPrice;
import com.hongyu.entity.HySubscribeTicketPriceItem;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HySubscribeTicketPriceItemService;
import com.hongyu.service.HySubscribeTicketService;
import com.hongyu.util.Constants.AuditStatus;

/** 品控中心 - 审核 - 门店认购门票 */
@Controller
@RequestMapping("admin/productCenter/subscirbeTicket")
public class ProductCenterReview_SubscribeTicket_Controller {
	@Resource(name = "hySubscribeTicketPriceItemServiceImpl")
	HySubscribeTicketPriceItemService hySubscribeTicketPriceItemService;

	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;

	@Resource(name = "hySubscribeTicketServiceImpl")
	HySubscribeTicketService hySubscribeTicketService;

	@Resource
	private RuntimeService runtimeService;
	@Resource
	private TaskService taskService;
	@Resource
	private HistoryService historyService;

	/** 门店认购门票审核 - 列表 */
	@RequestMapping(value = "/list/view")
	@ResponseBody
	public Json subscribeTicketList(Pageable pageable, Integer state, HySubscribeTicket hySubscribeTicket,
			HttpSession session) {
		Json json = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			List<Filter> filters = FilterUtil.getInstance().getFilter(hySubscribeTicket);
			List<HySubscribeTicket> hySubscribeTicketServices = hySubscribeTicketService.findList(null, filters, null);
			List<Map<String, Object>> ans = new ArrayList<>();
			Map<String, Object> answer = new HashMap<>();
			if (state == null) {
				List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).list();
				for (Task task : tasks) {
					String processInstanceId = task.getProcessInstanceId();
					for (HySubscribeTicket tmp : hySubscribeTicketServices) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							HashMap<String, Object> m = new HashMap<>();
							m.put("id", tmp.getId());
							m.put("state", 0);
							m.put("sn", tmp.getSn());
							m.put("sceneName", tmp.getSceneName());
							m.put("createTime", tmp.getCreateTime());
							m.put("creater", tmp.getCreater().getName());
							ans.add(m);
						}
					}
				}
				List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
						.finished().taskAssignee(username).list();
				for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
					String processInstanceId = historicTaskInstance.getProcessInstanceId();
					for (HySubscribeTicket tmp : hySubscribeTicketServices) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							HashMap<String, Object> m = new HashMap<>();
							m.put("id", tmp.getId());
							m.put("state", 1);
							m.put("sn", tmp.getSn());
							m.put("sceneName", tmp.getSceneName());
							m.put("createTime", tmp.getCreateTime());
							m.put("creater", tmp.getCreater().getName());
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
					for (HySubscribeTicket tmp : hySubscribeTicketServices) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							HashMap<String, Object> m = new HashMap<>();
							m.put("id", tmp.getId());
							m.put("state", 0);
							m.put("sn", tmp.getSn());
							m.put("sceneName", tmp.getSceneName());
							m.put("createTime", tmp.getCreateTime());
							m.put("creater", tmp.getCreater().getName());
							ans.add(m);
						}
					}
				}

			} else if (state == 1) {// 搜索已完成任务
				List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
						.orderByHistoricTaskInstanceStartTime().desc().finished().taskAssignee(username).list();

				for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
					String processInstanceId = historicTaskInstance.getProcessInstanceId();
					for (HySubscribeTicket tmp : hySubscribeTicketServices) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							HashMap<String, Object> m = new HashMap<>();
							m.put("id", tmp.getId());
							m.put("state", 1);
							m.put("sn", tmp.getSn());
							m.put("sceneName", tmp.getSceneName());
							m.put("createTime", tmp.getCreateTime());
							m.put("creater", tmp.getCreater().getName());
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

	/** 门店认购门票审核 - 详情 */
	@RequestMapping(value = "detail/view")
	@ResponseBody
	public Json getHistoryComments(Long id) {
		Json json = new Json();
		try {
			HySubscribeTicket hySubscribeTicket = hySubscribeTicketService.find(id);

			// 审核信息
			String processInstanceId = hySubscribeTicket.getProcessInstanceId();
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

			HashMap<String, Object> obj = new HashMap<>();
			obj.put("list", list);

			// 门店认购门票信息
			obj.put("hySubscribeTicket", hySubscribeTicket);

			json.setSuccess(true);
			json.setObj(obj);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("操作失败");
		}
		return json;

	}

	/** 品控中心审核 */
	@RequestMapping("audit")
	@ResponseBody
	@Transactional(propagation = Propagation.REQUIRED)
	public Json audit(Long id, String comment, Integer state, HttpSession session) {
		Json json = new Json();

		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HySubscribeTicket hySubscribeTicket = hySubscribeTicketService.find(id);
			String processInstanceId = hySubscribeTicket.getProcessInstanceId();

			if (processInstanceId == null || processInstanceId == "") {
				json.setSuccess(false);
				json.setMsg("审核出错，信息不完整，请重新申请");
			} else {
				Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
				
				HashMap<String , Object> map = new HashMap<>();
				
				if (state == 1) { // 审核通过
					map.put("result", "tongguo");
					hySubscribeTicket.setAuditStatus(AuditStatus.pass);
					hySubscribeTicket.setSaleStatus(SaleStatus.yishangjia);

					// 门店认购审核通过 生成日库存详情
					Calendar c = Calendar.getInstance();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					for (HySubscribeTicketPrice htp : hySubscribeTicket.getHySubscribeTicketPrices()) {
						Date startDate = sdf.parse(sdf.format(htp.getStartDate()));
						Date endDate = sdf.parse(sdf.format(htp.getEndDate()));
						
						
						while (startDate.compareTo(endDate) <= 0) {
							HySubscribeTicketPriceItem hySubscribeTicketPriceItem = new HySubscribeTicketPriceItem();
							hySubscribeTicketPriceItem.setDay(startDate);
							hySubscribeTicketPriceItem.setInitialInventory(htp.getDayInventory());
							hySubscribeTicketPriceItem.setHySubscribeTicketPriceId(htp.getId());
							hySubscribeTicketPriceItemService.save(hySubscribeTicketPriceItem);

							c.setTime(startDate);
							c.add(Calendar.DATE, 1); // 日期加1天
							startDate = c.getTime();
						}

					}
				} else if (state == 0) { // 审核未通过
					map.put("result", "bohui");
					hySubscribeTicket.setAuditStatus(AuditStatus.notpass);
				}
				Authentication.setAuthenticatedUserId(username);
				taskService.addComment(task.getId(), processInstanceId,
						(comment == null ? " " : comment) + ":" + state);
				taskService.claim(task.getId(), username);
				taskService.complete(task.getId(),map);
				hySubscribeTicketService.update(hySubscribeTicket);
				json.setSuccess(true);
				json.setMsg("审核完成");
			}

		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("审核失败");
			e.printStackTrace();
		}
		return json;

	}
}
