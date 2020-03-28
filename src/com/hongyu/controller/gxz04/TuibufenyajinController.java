package com.hongyu.controller.gxz04;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
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
import org.springframework.web.bind.annotation.RestController;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Pageable;
import com.hongyu.entity.CommonShenheedu;
import com.hongyu.entity.Gysfzrtuichu;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HySupplier;
import com.hongyu.entity.HySupplierContract;
import com.hongyu.entity.PayDeposit;
import com.hongyu.entity.CommonShenheedu.Eduleixing;
import com.hongyu.service.CommonEdushenheService;
import com.hongyu.service.GystuiyajinService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HySupplierContractService;
import com.hongyu.service.HySupplierService;
import com.hongyu.service.PayDepositService;
import com.hongyu.util.Constants.AuditStatus;
/**
 * 品控中心退还部分押金审核Controller
 * @author guoxinze
 *
 */
@RestController
@RequestMapping("/admin/pinkong/tuibufen/")
@Transactional(propagation = Propagation.REQUIRED)
public class TuibufenyajinController {
	@Resource
	private TaskService taskService;

	@Resource
	private RuntimeService runtimeService;
	
	@Resource
	private HistoryService historyService;
	
	@Resource(name="hySupplierServiceImpl")
	private HySupplierService hySupplierService;

	@Resource(name="hyAdminServiceImpl")
	private HyAdminService hyAdminService;

	@Resource(name="hySupplierContractServiceImpl")
	private HySupplierContractService hySupplierContractService;

	@Resource(name = "gysfzrtuichuServiceImpl")
	private GystuiyajinService gystuiyajinService;
	
	@Resource(name = "commonEdushenheServiceImpl")
	private CommonEdushenheService commonEdushenheService;
	
	@Resource(name = "payDepositServiceImpl")
	PayDepositService payDepositService;
	
	/**
	 * 退部分押金审核列表
	 * @param pageable
	 * @param supplierName
	 * @param contractCode
	 * @param shenheStatus
	 * @param startDate
	 * @param endDate
	 * @param session
	 * @return
	 */
	@RequestMapping(value="list/view")
	public Json list(Pageable pageable, String supplierName, String contractCode, String shenheStatus, Date startDate, Date endDate, HttpSession session) {
	Json j = new Json();	
		
		try {
            //用供应商名称进行查询
			List<Filter> filters = new ArrayList<>();
			List<HySupplier> hss = new ArrayList<>();
			List<HySupplierContract> hscs = new ArrayList<>();
			if(supplierName != null) {
				filters.add(Filter.like("supplierName", supplierName));
				hss = hySupplierService.findList(null, filters, null);
				filters.clear();
				for(HySupplier s : hss) {
					hscs.addAll(s.getHySupplierContracts());
				}
				if(hscs.isEmpty()) {
					j.setMsg("没有满足条件的数据");
					j.setSuccess(true);
					return j;
				}
				filters.add(Filter.in("contract", hscs));
			} else if (contractCode != null) { //用合同号进行查询
				filters.add(Filter.like("contractCode", contractCode));
				hscs = hySupplierContractService.findList(null, filters, null);
				if(hscs.isEmpty()) {
					j.setMsg("没有满足条件的数据");
					j.setSuccess(true);
					return j;
				}
				filters.clear();
				filters.add(Filter.in("contract", hscs));
			} else if (startDate != null && endDate != null) {
				filters.add(Filter.gt("applyTime", startDate));
				filters.add(Filter.lt("applyTime", endDate));
			}
			
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			
			List<Gysfzrtuichu> fzrtcs = gystuiyajinService.findList(null, filters, null);
			List<Map<String, Object>> ans = new ArrayList<>();
			Map<String, Object> answer = new HashMap<>();
			if (shenheStatus == null) {
				List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).list();
				for (Task task : tasks) {
					String processInstanceId = task.getProcessInstanceId();
					for (Gysfzrtuichu tmp : fzrtcs) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							helpler(tmp, tmp.getContract().getContractCode(), 
									tmp.getContract().getHySupplier().getSupplierName(), ans, "daishenhe");//待审核数据						
						}
					}
				}
				List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
						.finished().taskAssignee(username).list();
				for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
					String processInstanceId = historicTaskInstance.getProcessInstanceId();
					for (Gysfzrtuichu tmp : fzrtcs) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							
							List<Comment> comment = taskService.getProcessInstanceComments(processInstanceId);
							
							String str = "";
							for(Comment c : comment){
								if(username.equals(c.getUserId()))
										{
									str = c.getFullMessage();
									break;
										}
									
							}
							
							String[] strs = str.split(":");
							if(strs[1] == null) {
								throw new RuntimeException("状态错误");
							}
							helpler(tmp, tmp.getContract().getContractCode(), 
									tmp.getContract().getHySupplier().getSupplierName(), ans, strs[1]);
						}
					}
				}
			} else if (shenheStatus.equals("daishenhe")) {// 搜索未完成任务
				List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).list();
				for (Task task : tasks) {
					String processInstanceId = task.getProcessInstanceId();
					for (Gysfzrtuichu tmp : fzrtcs) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							helpler(tmp, tmp.getContract().getContractCode(), 
									tmp.getContract().getHySupplier().getSupplierName(), ans, "daishenhe");//待审核数据		
						}
					}
				}

			} else if (shenheStatus.equals("yishenhe")) {// 搜索已审核任务
				
				List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
						.finished().taskAssignee(username).list();
				for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
					String processInstanceId = historicTaskInstance.getProcessInstanceId();
					for (Gysfzrtuichu tmp : fzrtcs) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							
						    List<Comment> comment = taskService.getProcessInstanceComments(processInstanceId);
							
							String str = "";
							for(Comment c : comment){
								if(username.equals(c.getUserId()))
										{
									str = c.getFullMessage();
									break;
										}
									
							}
							
							String[] strs = str.split(":");
							if(strs[1] == null) {
								throw new RuntimeException("状态错误");
							}
							helpler(tmp, tmp.getContract().getContractCode(), 
									tmp.getContract().getHySupplier().getSupplierName(), ans, strs[1]);
						}
					}

				}
			} 
			int page = pageable.getPage();
			int rows = pageable.getRows();
			answer.put("total", ans.size());
			answer.put("pageNumber", page);
			answer.put("pageSize", rows);
			answer.put("rows", ans.subList((page - 1) * rows, page * rows > ans.size() ? ans.size() : page * rows));
			j.setSuccess(true);
			j.setMsg("获取成功");
			j.setObj(answer);
		} catch(Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	/**
	 * 退部分押金审核详情页
	 * @param id
	 * @return
	 */
	@RequestMapping(value="detail/view")
	public Json detail(Long id) {
		Json j = new Json();
		
		try {
				Gysfzrtuichu tuichu = gystuiyajinService.find(id);
				HySupplierContract contract = tuichu.getContract();
				HySupplier supplier = contract.getHySupplier();
	
				Map<String, Object> map = new HashMap<String, Object>();
				
				/** 供应商详情 */
				Map<String, Object> sMap = new HashMap<String, Object>();
				/** 审核详情 */
				List<HashMap<String, Object>> shenheMap = new ArrayList<>();
				/**
				 * 供应商详情添加	
				 */
				sMap.put("supplierName",supplier.getSupplierName());
				sMap.put("contractCode", contract.getContractCode());
				sMap.put("liableName", contract.getLiable().getName());
				sMap.put("mobile", contract.getLiable().getMobile());
				sMap.put("deposit", tuichu.getReturnMoney());//续签情况下这个是部分退还的押金
				sMap.put("fileUrl", tuichu.getFileUrl());

				/**
				 * 审核详情添加
				 */
				String processInstanceId = tuichu.getProcessInstanceId();
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
				map.put("supplier", sMap);
				j.setMsg("查看详情成功");
				j.setSuccess(true);
				j.setObj(map);
		} catch(Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	/**
	 * 审核退还部分押金的申请
	 * @param id
	 * @param comment
	 * @param shenheStatus
	 * @param session
	 * @return
	 */
	@RequestMapping(value="audit")
	public Json audit(Long id, String comment, String shenheStatus, HttpSession session) {
		Json json = new Json();

		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			Gysfzrtuichu gysfzrtuichu = gystuiyajinService.find(id);
			HySupplierContract contract = gysfzrtuichu.getContract();
			String processInstanceId = gysfzrtuichu.getProcessInstanceId();

			if (processInstanceId == null || processInstanceId == "") {
				json.setSuccess(false);
				json.setMsg("审核出错，信息不完整，请重新申请");
			} else {
				Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
				HashMap<String, Object> map = new HashMap<>();
					if(shenheStatus.equals("yitongguo")) {
						
						if(task.getTaskDefinitionKey().equals("usertask2")) { //如果是品控审核需要单独处理，因为有三个流转的地方
							
							List<Filter> filters = new ArrayList<>();
							filters.add(Filter.eq("eduleixing", Eduleixing.tuibufenyajinfuzong));
							List<CommonShenheedu> edu = commonEdushenheService.findList(null, filters, null);
							BigDecimal money = edu.get(0).getMoney();
							if(gysfzrtuichu.getReturnMoney().compareTo(money) > 0) {
								map.put("money", "more");
							} else {
								map.put("money", "less");
							}
							
							Authentication.setAuthenticatedUserId(username);
							taskService.claim(task.getId(),username);
							taskService.addComment(task.getId(), processInstanceId, comment == null ? "审核通过" + ":" + shenheStatus : comment + ":" + shenheStatus);
							taskService.complete(task.getId(), map);
						} else if (task.getTaskDefinitionKey().equals("usertask4")) {
							gysfzrtuichu.setAuditStatus(AuditStatus.pass);

							// 供应商退押金审核通过，则需要生成待付款记录,并返回前台待付款记录的id
							// 1、在hy_pay_deposit表中写数据

							PayDeposit payDeposit = new PayDeposit();
							payDeposit.setDepositType(2); // 1:门店保证金退还 2:供应商保证金退还
							payDeposit.setHasPaid(0); // 未付
							payDeposit.setInstitution(gysfzrtuichu.getContract().getHySupplier().getSupplierName());
							payDeposit.setApplyDate(gysfzrtuichu.getApplyTime());
							payDeposit.setAppliName(gysfzrtuichu.getApplierName());
							payDeposit.setContractCode(gysfzrtuichu.getContract().getContractCode());
							payDeposit.setAmount(gysfzrtuichu.getReturnMoney());
							// payDeposit.setPayer(payer);
							// payDeposit.setPayDate(payDate);
							payDeposit.setBankListId(gysfzrtuichu.getContract().getBankList().getId());

							payDepositService.save(payDeposit);
							
							//修改合同中应退押金字段为0
//							contract.setReturnDeposit(BigDecimal.ZERO);
//							hySupplierContractService.update(contract);
							contract.setReturnDeposit(BigDecimal.ZERO);
							hySupplierContractService.update(contract);//更新合同
							Authentication.setAuthenticatedUserId(username);
							taskService.claim(task.getId(),username);
							taskService.addComment(task.getId(), processInstanceId, comment == null ? "审核通过" + ":" + shenheStatus : comment + ":" + shenheStatus);
							taskService.complete(task.getId());
						} else {
							Authentication.setAuthenticatedUserId(username);
							taskService.claim(task.getId(),username);
							taskService.addComment(task.getId(), processInstanceId, comment == null ? "审核通过" + ":" + shenheStatus : comment + ":" + shenheStatus);
							taskService.complete(task.getId());
						}

					} else if (shenheStatus.equals("yibohui")) {//驳回需要重新提交申请 
						gysfzrtuichu.setAuditStatus(AuditStatus.notpass);
						gystuiyajinService.update(gysfzrtuichu);
					}
					
					json.setSuccess(true);
					json.setMsg("审核成功");
			}

		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("审核失败");
			e.printStackTrace();
		}
		return json;
	}
	
	private void helpler(Gysfzrtuichu tuichu, String contractCode, String supplierName, List<Map<String, Object>> ans, String status) {
		HashMap<String, Object> m = new HashMap<>();
		HyAdmin creator = hyAdminService.find(tuichu.getApplierName());
		m.put("id", tuichu.getId());
		m.put("shenheStatus", status);	
		m.put("supplierName", supplierName);
		m.put("contractCode", contractCode);
		m.put("returnMoney", tuichu.getReturnMoney());//应该退还的押金，续签情况下为部分押金
		m.put("applyTime", tuichu.getApplyTime());
		if(creator != null) {
			m.put("applyName", creator.getName());
		}
		ans.add(m);
	}
	
}
