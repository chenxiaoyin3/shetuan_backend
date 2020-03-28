package com.hongyu.controller.gxz04;

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
import javax.servlet.http.HttpSession;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grain.util.FilterUtil;
import com.grain.util.StringUtil;
import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Pageable;
import com.hongyu.entity.CommonShenheedu;
import com.hongyu.entity.CommonShenheedu.Eduleixing;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyGroup;
import com.hongyu.entity.HyLine;
import com.hongyu.entity.HyLine.IsSaleEnum;
import com.hongyu.entity.HyRegulate;
import com.hongyu.entity.HySupplier;
import com.hongyu.service.CommonEdushenheService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.HyLineService;
import com.hongyu.service.HyRegulateService;
import com.hongyu.util.Constants.AuditStatus;
import com.hongyu.util.SendMessageEMY;

/**
 * 品控中心审核供应商上产品
 * @author guoxinze
 *
 */
@RestController
@Transactional(propagation = Propagation.REQUIRED)
@RequestMapping("admin/pinkong/xianlu")
public class XianlushenheController {
	
	@Resource
	private TaskService taskService;

	@Resource
	private RuntimeService runtimeService;
	
	@Resource
	private HistoryService historyService;
	
	@Resource(name = "commonEdushenheServiceImpl")
	private CommonEdushenheService commonEdushenheService;
	
	@Resource(name="hyAdminServiceImpl")
	private HyAdminService hyAdminService;
	
	@Resource(name = "hyLineServiceImpl")
	HyLineService hyLineService;
	
	@Resource(name = "hyGroupServiceImpl")
	HyGroupService hyGroupService;
	
	@Resource(name = "hyRegulateServiceImpl")
	HyRegulateService hyRegulateService;
	
	@RequestMapping(value="list/view")
	public Json list(Pageable pageable, HyGroup hyGroup, String shenheStatus, HttpSession session) {

		Json j = new Json();	
		
		try {
			Set<HyGroup> scs = new HashSet<>();
			String username = (String) session.getAttribute(CommonAttributes.Principal);

			List<Map<String, Object>> ans = new ArrayList<>();
			Map<String, Object> answer = new HashMap<>();
			if (shenheStatus == null) {
				List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).list();
				List<String> pis = new ArrayList<>();
				
				for (Task task : tasks) {
					String processInstanceId = task.getProcessInstanceId();
					if(processInstanceId != null) {
						pis.add(processInstanceId);
					}
				}
					List<Filter> filters = FilterUtil.getInstance().getFilter(hyGroup);
					if(!pis.isEmpty()) {
						filters.add(Filter.in("processInstanceId", pis));
						filters.add(Filter.ne("auditStatus", AuditStatus.notpass));
						List<HyGroup> temps = hyGroupService.findList(null, filters, null);
						
						for (HyGroup tmp : temps) {
							helpler(tmp, ans, "daishenhe");//待审核数据						
						}
					}	
					
					
					List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
							.finished().taskAssignee(username).processDefinitionKeyLike("xianlushenheprocess").list();
					List<HistoricTaskInstance> historicTaskInstances1 = historyService.createHistoricTaskInstanceQuery()
							.finished().taskAssignee(username).list();
					List<String> hisPis = new ArrayList<>();
					for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
						String processInstanceId = historicTaskInstance.getProcessInstanceId();
						if(processInstanceId != null) {
							hisPis.add(processInstanceId);
						}
					}
						List<Filter> filters1 = FilterUtil.getInstance().getFilter(hyGroup);
						if(!hisPis.isEmpty()) {
							filters1.add(Filter.in("processInstanceId", hisPis));
							List<HyGroup> temps1 = hyGroupService.findList(null, filters1, null);
							
							for (HyGroup tmp : temps1) {
									if(!scs.contains(tmp)) {
										   scs.add(tmp);
										   List<Comment> comment = taskService.getProcessInstanceComments(tmp.getProcessInstanceId());
										   System.out.println("comment size:" + comment.size() + "process id is :" + tmp.getProcessInstanceId() + "group id is " + tmp.getId() + "\n");
											String str = "";
											for(Comment c : comment){
												if(username.equals(c.getUserId()))
														{
													str = c.getFullMessage();
													System.out.println("comment id is " + c.getId() + "\n");
													break;
														}
													
											}
											System.out.print("str is: "+ str +"\n");
											if(StringUtils.isNotBlank(str)) {
												String[] strs = str.split(":");
												if(strs[1] == null) {
													throw new RuntimeException("状态错误");
												}
												helpler(tmp, ans, strs[1]);
											}						
									}
							}
							
						}
				
		
					
				} else if (shenheStatus.equals("daishenhe")) {// 搜索未完成任务
				    long currentTime = System.currentTimeMillis();
				    List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username)
							.processDefinitionKeyLike("xianlushenheprocess").list();			
					long currentTime1 = System.currentTimeMillis();					
					List<Task> pis2 = taskService.createNativeTaskQuery()
							.sql("SELECT * FROM  ACT_RU_TASK AS rt WHERE rt.ID_ IN "
							+ "(SELECT ri.TASK_ID_ FROM ACT_RU_IDENTITYLINK AS ri WHERE ri.USER_ID_ = 'pinkongyuangong') "
							+ "AND rt.PROC_DEF_ID_ LIKE 'xianlushenheprocess%'").list();
					long currentTime3 = System.currentTimeMillis();
					System.out.println(currentTime1-currentTime +"///" + (currentTime3 - currentTime1));
					List<String> pis = new ArrayList<>();
					
					for (Task task : tasks) {
						String processInstanceId = task.getProcessInstanceId();
						if(processInstanceId != null) {
							pis.add(processInstanceId);
						}
					}
						List<Filter> filters = FilterUtil.getInstance().getFilter(hyGroup);
						if(!pis.isEmpty()) {
							filters.add(Filter.in("processInstanceId", pis));
							filters.add(Filter.ne("auditStatus", AuditStatus.notpass));
							List<HyGroup> temps = hyGroupService.findList(null, filters, null);
							
							for (HyGroup tmp : temps) {
								helpler(tmp, ans, "daishenhe");//待审核数据						
							}
						}						
						
//			

			} else if (shenheStatus.equals("yishenhe")) {// 搜索已审核任务
				
				
				List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
						.finished().taskAssignee(username).list();
				List<String> hisPis = new ArrayList<>();
				for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
					String processInstanceId = historicTaskInstance.getProcessInstanceId();
					if(processInstanceId != null) {
						hisPis.add(processInstanceId);
					}
				}
					List<Filter> filters1 = FilterUtil.getInstance().getFilter(hyGroup);
					if(!hisPis.isEmpty()) {
						filters1.add(Filter.in("processInstanceId", hisPis));
						List<HyGroup> temps1 = hyGroupService.findList(null, filters1, null);
						
						for (HyGroup tmp : temps1) {
								if(!scs.contains(tmp)) {
									   scs.add(tmp);
									   List<Comment> comment = taskService.getProcessInstanceComments(tmp.getProcessInstanceId());
									   System.out.println("comment size:" + comment.size() + "process id is :" + tmp.getProcessInstanceId() + "group id is " + tmp.getId() + "\n");
										String str = "";
										for(Comment c : comment){
											if(username.equals(c.getUserId()))
													{
												str = c.getFullMessage();
												System.out.println("comment id is " + c.getId() + "\n");
												break;
													}
												
										}
										System.out.print("str is: "+ str +"\n");
										if(StringUtils.isNotBlank(str)) {
											String[] strs = str.split(":");
											if(strs[1] == null) {
												throw new RuntimeException("状态错误");
											}
											helpler(tmp, ans, strs[1]);
										}						
								}
						}
						
					}
					
				
			} 
			//按照申请日期倒序排列,added by GSbing,20181012
			Collections.sort(ans, new Comparator<Map<String, Object>>() {
				@Override
				public int compare(Map<String, Object> o1, Map<String, Object> o2) {
					Date date1 = (Date) o1.get("applyTime");
					Date date2 = (Date) o2.get("applyTime");
					return date1.compareTo(date2); 
				}
			});
		    Collections.reverse(ans);
//		    System.out.println("7 time is-------:" + new Date());
			int page = pageable.getPage();
			int rows = pageable.getRows();
			answer.put("total", ans.size());
			answer.put("pageNumber", page);
			answer.put("pageSize", rows);
			answer.put("rows", ans.subList((page - 1) * rows, page * rows > ans.size() ? ans.size() : page * rows));
			j.setSuccess(true);
			j.setMsg("获取成功");
			j.setObj(answer);
//			System.out.println("8 time is-------:" + new Date());
		} catch(Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		System.out.println("1 time is-------:" + new Date());
		return j;
	}

	/**
	 * 审核列表页面，根据候选人找出当前用户可以审核的记录 --审核以团为单位
	 * @param id
	 * @return
	 */
//	@RequestMapping(value="list/view")
//	public Json list(Pageable pageable, HyGroup hyGroup, String shenheStatus, HttpSession session) {
//		Json j = new Json();	
//		
//		try {
//			System.out.println("time is-------:" + new Date());
//			Set<HyGroup> scs = new HashSet<>();
//			String username = (String) session.getAttribute(CommonAttributes.Principal);
//			List<Filter> filters = FilterUtil.getInstance().getFilter(hyGroup); 
//			System.out.println("time is-------:" + System.currentTimeMillis());
//			List<HyGroup> hyGroups = hyGroupService.findList(null, filters, null);
//			System.out.println("time is-------:" + System.currentTimeMillis());
//			List<Map<String, Object>> ans = new ArrayList<>();
//			Map<String, Object> answer = new HashMap<>();
//			if (shenheStatus == null) {
//				List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).list();
//				for (Task task : tasks) {
//					String processInstanceId = task.getProcessInstanceId();
//					for (HyGroup tmp : hyGroups) {
//						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
//							helpler(tmp, ans, "daishenhe");//待审核数据						
//						}
//					}
//				}
//				List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
//						.finished().taskCandidateUser(username).list();
//				for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
//					String processInstanceId = historicTaskInstance.getProcessInstanceId();
//					for (HyGroup tmp : hyGroups) {
//						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
//							if(!scs.contains(tmp)) {
//								   scs.add(tmp);
//								   List<Comment> comment = taskService.getProcessInstanceComments(processInstanceId);
//									
//									String str = "";
//									for(Comment c : comment){
//										if(username.equals(c.getUserId()))
//												{
//											str = c.getFullMessage();
//											break;
//												}
//											
//									}
//									
//									String[] strs = str.split(":");
//									if(strs[1] == null) {
//										throw new RuntimeException("状态错误");
//									}
//									helpler(tmp, ans, strs[1]);
//							}
//						}
//					}
//				}
//				
//			} else if (shenheStatus.equals("daishenhe")) {// 搜索未完成任务
//				List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).list();
//				for (Task task : tasks) {
//					String processInstanceId = task.getProcessInstanceId();
//					for (HyGroup tmp : hyGroups) {
//						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
//							helpler(tmp, ans, "daishenhe");//待审核数据		
//						}
//					}
//				}
//
//			} else if (shenheStatus.equals("yishenhe")) {// 搜索已审核任务
//				
//				List<HistoricTaskInstance> ss = historyService.createHistoricTaskInstanceQuery()
//						.finished().taskCandidateUser(username).list();
//				Set<HistoricTaskInstance> historicTaskInstances = new HashSet<>(ss);
//				for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
//					String processInstanceId = historicTaskInstance.getProcessInstanceId();
//					for (HyGroup tmp : hyGroups) {
//						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
//							if(!scs.contains(tmp)) {
//								scs.add(tmp);
//								 List<Comment> comment = taskService.getProcessInstanceComments(processInstanceId);
//									
//									String str = "";
//									for(Comment c : comment){
//										if(username.equals(c.getUserId()))
//												{
//											str = c.getFullMessage();
//											break;
//												}
//											
//									}
//									
//									String[] strs = str.split(":");
//									if(strs[1] == null) {
//										throw new RuntimeException("状态错误");
//									}
//									helpler(tmp, ans, strs[1]);
//							}
//						   
//						}
//					}
//
//				}
//			} 
//			//按照申请日期倒序排列,added by GSbing,20181012
//			Collections.sort(ans, new Comparator<Map<String, Object>>() {
//				@Override
//				public int compare(Map<String, Object> o1, Map<String, Object> o2) {
//					Date date1 = (Date) o1.get("applyTime");
//					Date date2 = (Date) o2.get("applyTime");
//					return date1.compareTo(date2); 
//				}
//			});
//		    Collections.reverse(ans);
//		    
//			int page = pageable.getPage();
//			int rows = pageable.getRows();
//			answer.put("total", ans.size());
//			answer.put("pageNumber", page);
//			answer.put("pageSize", rows);
//			answer.put("rows", ans.subList((page - 1) * rows, page * rows > ans.size() ? ans.size() : page * rows));
//			j.setSuccess(true);
//			j.setMsg("获取成功");
//			j.setObj(answer);
//		} catch(Exception e) {
//			// TODO Auto-generated catch block
//			j.setSuccess(false);
//			j.setMsg(e.getMessage());
//		}
//		return j;
//	}
	
	/**
	 * 审核(通过或者驳回)供应商新建团
	 * @param id 团id comment 驳回批注   shenheStatus 审核状态
	 * 		
	 * @return
	 */
	@Transactional
	@RequestMapping(value="audit")
	public Json audit(Long[] groupIds, String comment, String shenheStatus, HttpSession session) {
		Json json = new Json();

		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			for(int i = 0; i < groupIds.length; i++) {
				HyGroup hyGroup = hyGroupService.find(groupIds[i]);
				HyLine line = hyGroup.getLine();
				String processInstanceId = hyGroup.getProcessInstanceId();

				if (processInstanceId == null || processInstanceId == "") {
					json.setSuccess(false);
					json.setMsg("审核出错，信息不完整，请重新申请");
				} else {
					HashMap<String, Object> map = new HashMap<>();
					Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
					
					if (shenheStatus.equals("yitongguo")) {
						map.put("result", "tongguo");
						String str = task.getTaskDefinitionKey();
						if(str.equals("usertask3")) { //如果是最后一步，需要进行以下操作
							hyGroup.setAuditStatus(AuditStatus.pass);
							line.setIsSale(IsSaleEnum.yishang); 
							Boolean isInner = line.getIsInner();
							line.setLineAuditStatus(AuditStatus.pass);
							hyGroup.setIsDisplay(true); //显示团期
							
							//更新线路的最新团期
							if(line.getLatestGroup() == null) {
								line.setLatestGroup(hyGroup.getStartDay());
							} else {
								if(hyGroup.getStartDay().after(line.getLatestGroup())) {
									line.setLatestGroup(hyGroup.getStartDay());
									hyLineService.update(line);
								}
							}
							
							//团审核通过以后加入一条报账信息,只有内部才有
							if(line.getIsInner()) {
								HyRegulate regulate = new HyRegulate();
								regulate.setHyGroup(hyGroup.getId());
								regulate.setLineSn(line.getPn());
								regulate.setLineName(line.getName());
								regulate.setStartDate(hyGroup.getStartDay());
								regulate.setEndDate(hyGroup.getEndDay());
								regulate.setDays(line.getDays());
								regulate.setVisitorNum(0);
								regulate.setOperator(line.getOperator());
								regulate.setCreateTime(new Date());
								regulate.setStatus(0);
								regulate.setOperatorName(line.getOperator().getName());
								hyRegulateService.save(regulate);
								hyGroup.setRegulateId(regulate.getId());
							}
						
						}
						
					
					} else if (shenheStatus.equals("yibohui")) {
						map.put("result", "bohui");
						hyGroup.setAuditStatus(AuditStatus.notpass);
						if(line.getLineAuditStatus() != AuditStatus.pass) {
							line.setLineAuditStatus(AuditStatus.notpass);
						}
						//add by wj 2019-07-07 线路被驳回提醒
						String phone = null;
						if(hyGroup.getCreator()!=null){
							phone = hyGroup.getCreator().getMobile();
						}
						SendMessageEMY.sendMessage(phone,"",14);
					}
					
					Authentication.setAuthenticatedUserId(username);
					taskService.claim(task.getId(),username);
					taskService.addComment(task.getId(), processInstanceId, comment == null ? "审核通过" + ":" + shenheStatus : comment + ":" + shenheStatus);
					taskService.complete(task.getId(), map);	
					hyGroupService.update(hyGroup);
					json.setSuccess(true);
					json.setMsg("审核成功");
				}
			}
			

		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("审核失败");
			e.printStackTrace();
		}
		return json;
	}
	
	/**
	 * 团审核详情页面
	 * @param id 团id
	 * @return 团审核历史记录
	 */
	@RequestMapping(value="detail/view")
	public Json detail(Long id) {
		Json j = new Json();
		
		try {
			HashMap<String, Object> jiagebili = new HashMap<>();
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("eduleixing", Eduleixing.guoneijiagebili));
			List<CommonShenheedu> edu = commonEdushenheService.findList(null, filters, null);
			BigDecimal money = edu.get(0).getMoney();
			jiagebili.put("guonei", money);
		
			filters.clear();
			filters.add(Filter.eq("eduleixing", Eduleixing.chujingjiagebili));
			List<CommonShenheedu> edu1 = commonEdushenheService.findList(null, filters, null);
			BigDecimal money1 = edu1.get(0).getMoney();
			jiagebili.put("chujing", money1);
		
			filters.clear();
			filters.add(Filter.eq("eduleixing", Eduleixing.qichejiagebili));
			List<CommonShenheedu> edu2 = commonEdushenheService.findList(null, filters, null);
			BigDecimal money2 = edu2.get(0).getMoney();
			jiagebili.put("qiche", money2);
			
			filters.clear();
			filters.add(Filter.eq("eduleixing", Eduleixing.piaowujiagebili));
			List<CommonShenheedu> edu3 = commonEdushenheService.findList(null, filters, null);
			BigDecimal money3 = edu3.get(0).getMoney();
			jiagebili.put("piaowu", money3);

			HyGroup hyGroup = hyGroupService.find(id);
			HyLine line = hyGroup.getLine();
			Map<String, Object> map = new HashMap<String, Object>();
			
			map.put("group", hyGroup); //团详情
			map.put("line", line); //线路详情
			map.put("jiagebili", jiagebili);
	
			/** 审核详情 */
			List<HashMap<String, Object>> shenheMap = new ArrayList<>();
			
				/**
				 * 审核详情添加
				 */
				String processInstanceId = hyGroup.getProcessInstanceId();
				List<Comment> commentList = taskService.getProcessInstanceComments(processInstanceId);
				Collections.reverse(commentList);
				for (Comment comment : commentList) {
					HashMap<String, Object> im = new HashMap<>();
					String taskId = comment.getTaskId();
					HistoricTaskInstance task = historyService.createHistoricTaskInstanceQuery().taskId(taskId)
							.singleResult();
					String step = "";
					if (task != null) {
						step = task.getName();
					}
					im.put("step", step);
					String username = comment.getUserId();
					HyAdmin hyAdmin = hyAdminService.find(username);
					String name = "";
					if (hyAdmin != null) {
						name = hyAdmin.getName();
					}
					im.put("auditName", name);
					String str = comment.getFullMessage();
					String[] strs = str.split(":");
					
				    im.put("comment", strs[0]);
				    if(strs[1].equals("yitongguo")) {
				    	im.put("result", "通过");
				    } else if (strs[1].equals("yibohui")) {
				    	im.put("result", "驳回");
				    } else {
				    	im.put("result", "提交审核");
				    }
					
					im.put("time", comment.getTime());

					shenheMap.add(im);
				}
				
				map.put("auditRecord", shenheMap);			
			j.setMsg("查看成功");
			j.setSuccess(true);
			j.setObj(map);
		} catch(Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	private static void helpler(HyGroup tmp, List<Map<String, Object>> ans, String status) {
		HashMap<String, Object> m = new HashMap<>();
	
		m.put("id", tmp.getId());	
		m.put("shenheStatus", status);	
		HyLine line = tmp.getLine();
		if(line != null) {
			m.put("sn", line.getPn());
			m.put("name", line.getName());
			HySupplier supplier = line.getHySupplier();
			if(supplier != null) {
				m.put("supplierName", supplier.getSupplierName());
			}
		}
		m.put("operator", line.getOperator());
		m.put("applyTime", tmp.getApplyTime());
		m.put("startDay", tmp.getStartDay());
		
		ans.add(m);
	}
}
