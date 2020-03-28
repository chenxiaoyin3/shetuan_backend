package com.hongyu.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grain.util.FilterUtil;
import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Pageable;
import com.hongyu.entity.GroupDivide;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyArea;
import com.hongyu.entity.HySupplier;
import com.hongyu.entity.HySupplierContract;
import com.hongyu.entity.HySupplierElement;
import com.hongyu.entity.HySupplierContract.ContractStatus;
import com.hongyu.entity.HySupplierElement.SupplierType;
import com.hongyu.service.BankListService;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.GroupDivideService;
import com.hongyu.service.GroupMemberService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyAreaService;
import com.hongyu.service.HyDepartmentModelService;
import com.hongyu.service.HyRoleService;
import com.hongyu.service.HySupplierContractService;
import com.hongyu.service.HySupplierDeductChujingService;
import com.hongyu.service.HySupplierDeductGuoneiService;
import com.hongyu.service.HySupplierDeductPiaowuService;
import com.hongyu.service.HySupplierDeductQianzhengService;
import com.hongyu.service.HySupplierDeductQicheService;
import com.hongyu.service.HySupplierDeductRengouService;
import com.hongyu.service.HySupplierElementService;
import com.hongyu.service.HySupplierService;
import com.hongyu.util.Constants.AuditStatus;
/**
 * 品控中心审核供应商合同接口
 * @author guoxinze
 *
 */

@RestController
@RequestMapping("/admin/pinkong/newcontract/")
public class PinkongGysShenhe {
	
	@Resource
	private TaskService taskService;

	@Resource
	private RuntimeService runtimeService;
	
	@Resource
	private HistoryService historyService;
	
	@Resource(name="hySupplierServiceImpl")
	private HySupplierService hySupplierService;
	
	@Resource(name="hySupplierContractServiceImpl")
	private HySupplierContractService hySupplierContractService;
	
	@Resource(name="hyAreaServiceImpl")
	private HyAreaService hyAreaService;
	
	@Resource(name="hySupplierDeductChujingServiceImpl")
	private HySupplierDeductChujingService hySupplierDeductChujingService;
	
	@Resource(name="hySupplierDeductGuoneiServiceImpl")
	private HySupplierDeductGuoneiService hySupplierDeductGuoneiService;
	
	@Resource(name="hySupplierDeductQicheServiceImpl")
	private HySupplierDeductQicheService hySupplierDeductQicheService;
	
	@Resource(name="hySupplierDeductPiaowuServiceImpl")
	private HySupplierDeductPiaowuService hySupplierDeductPiaowuService;
	
	@Resource(name="hySupplierDeductRengouServiceImpl")
	private HySupplierDeductRengouService hySupplierDeductRengouService;
	
	@Resource(name="hySupplierDeductQianzhengServiceImpl")
	private HySupplierDeductQianzhengService hySupplierDeductQianzhengService;
	
	@Resource(name="hyAdminServiceImpl")
	private HyAdminService hyAdminService;
	
	@Resource(name="hyRoleServiceImpl")
	private HyRoleService hyRoleService;
	
	@Resource(name="departmentServiceImpl")
	private DepartmentService departmentService;
	
	@Resource(name="bankListServiceImpl")
	private BankListService bankListService;
	
	@Resource(name="hyDepartmentModelServiceImpl")
	private HyDepartmentModelService departmentModelService;
	
	@Resource(name = "hySupplierElementServiceImpl")
	HySupplierElementService  hySupplierElementService;
	
	@Resource(name = "groupDivideServiceImpl")
	GroupDivideService groupDivideService;
	
	@Resource(name = "groupMemberServiceImpl")
	GroupMemberService groupMemberService;
	
	/**
	 * 审核列表页面，根据候选人找出当前用户可以审核的记录
	 * @param id
	 * @return
	 */
	@RequestMapping(value="list/view")
	public Json list(Pageable pageable, HySupplierContract contract, String shenheStatus, HttpSession session) {
		Json j = new Json();	
		
		try {
			//解决已驳回再提交以后有两条相同数据的问题
			Set<HySupplierContract> scs = new HashSet<>();
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			List<Filter> filters = FilterUtil.getInstance().getFilter(contract);
			List<HySupplierContract> hySupplierContracts = hySupplierContractService.findList(null, filters,
					null);
			List<Map<String, Object>> ans = new ArrayList<>();
			Map<String, Object> answer = new HashMap<>();
			if (shenheStatus == null) {
				List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).list();
				for (Task task : tasks) {
					String processInstanceId = task.getProcessInstanceId();
					for (HySupplierContract tmp : hySupplierContracts) {
						if (processInstanceId.equals(tmp.getProcessId())) {
							helpler(tmp, ans, "daishenhe");//待审核数据						
						}
					}
				}
				List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
						.finished().taskAssignee(username).list();
				for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
					String processInstanceId = historicTaskInstance.getProcessInstanceId();
					for (HySupplierContract tmp : hySupplierContracts) {
						if (processInstanceId.equals(tmp.getProcessId())) {
							if(!scs.contains(tmp)) {
								   scs.add(tmp);
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
									helpler(tmp, ans, strs[1]);
							}
						}
					}
				}
				
			} else if (shenheStatus.equals("daishenhe")) {// 搜索未完成任务
				List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).list();
				for (Task task : tasks) {
					String processInstanceId = task.getProcessInstanceId();
					for (HySupplierContract tmp : hySupplierContracts) {
						if (processInstanceId.equals(tmp.getProcessId())) {
							helpler(tmp, ans, "daishenhe");//待审核数据		
						}
					}
				}

			} else if (shenheStatus.equals("yishenhe")) {// 搜索已审核任务
				
				List<HistoricTaskInstance> ss = historyService.createHistoricTaskInstanceQuery()
						.finished().taskAssignee(username).list();
				Set<HistoricTaskInstance> historicTaskInstances = new HashSet<>(ss);
				for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
					String processInstanceId = historicTaskInstance.getProcessInstanceId();
					for (HySupplierContract tmp : hySupplierContracts) {
						if (processInstanceId.equals(tmp.getProcessId())) {
							if(!scs.contains(tmp)) {
								scs.add(tmp);
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
									helpler(tmp, ans, strs[1]);
							}
						   
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
	 * 审核(通过或者驳回)供应商合同
	 * @param id 供应商合同id comment 驳回批注   shenheStatus 审核状态
	 * 		
	 * @return
	 */
	@Transactional
	@RequestMapping(value="audit")
	public Json audit(Long id, String comment, String shenheStatus, HttpSession session) {
		Json json = new Json();

		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HySupplierContract hySupplierContract = hySupplierContractService.find(id);
			HySupplier hySupplier = hySupplierContract.getHySupplier();
			String processInstanceId = hySupplierContract.getProcessId();

			if (processInstanceId == null || processInstanceId == "") {
				json.setSuccess(false);
				json.setMsg("审核出错，信息不完整，请重新申请");
			} else {
				HashMap<String, Object> map = new HashMap<>();
				Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
				HySupplier s = hySupplierContract.getHySupplier();
				if (shenheStatus.equals("yitongguo")) {
					map.put("result", "tongguo");
					hySupplierContract.setAuditStatus(AuditStatus.pass);
					
					if(hySupplierContract.getDeposit().compareTo(BigDecimal.ZERO) == 0) {
						if(hySupplierContract.getStartDate().before(new Date())) {
							hySupplierContract.setContractStatus(ContractStatus.weishengxiao);
						} else {
							hySupplierContract.setContractStatus(ContractStatus.zhengchang);
						}
					} else {
						hySupplierContract.setContractStatus(ContractStatus.dongjie);
					}
					if(s != null && s.getSupplierStatus() != AuditStatus.pass) {
						s.setSupplierStatus(AuditStatus.pass);
					}
					
					//如果是地接供应商还要在旅游元素里面加入一条数据
					//修改，如果线路供应商没有加入旅游元素表里面，才会进行判断
					List<Filter> fs = new ArrayList<>();
					fs.add(Filter.eq("supplierLine", hySupplier.getId()));
					fs.add(Filter.eq("supplierType", SupplierType.linelocal));
					List<HySupplierElement> elements = hySupplierElementService.findList(null, fs, null);
					if(elements.isEmpty() && hySupplier.getIsDijie()) {
						HySupplierElement element = new HySupplierElement();
						element.setSupplierType(SupplierType.linelocal); //线路地接供应商
						element.setName(hySupplier.getSupplierName());
						element.setLiableperson(hySupplierContract.getLiable().getName());
						element.setTelephone(hySupplierContract.getLiable().getMobile());
						element.setSupplierLine(hySupplier.getId());
						element.setBankList(hySupplierContract.getBankList());
						element.setIsShouru(false);
						hySupplierElementService.save(element);
					}
					
					/* write by lbc
					 * 
					 * 
					 */
						
					//向groupdivide表中加入分团数据
					//hyGroup如何获取？
//					if(hyGroup.getIsInner()) {
//						//如果是内部的团
//						GroupDivide groupDivide = new GroupDivide();
//						groupDivide.setGroup(hyGroup);
//						groupDivide.setGuide(null);
//						
//						//分团号初始化为A，团内人员总数初始化为0
//						groupDivide.setSubGroupsn("A");
//						groupDivide.setSubGroupNo(0);
//						
//						groupDivideService.save(groupDivide);	
//						
//						
//						
//					}
					
					//如果押金为0则不用交押金
					if(null != hySupplierContract && hySupplierContract.getShouldpayDeposit().compareTo(BigDecimal.ZERO) == 0) {						
							Date startTime = hySupplierContract.getStartDate();
							if(startTime.before(new Date())) {
								hySupplierContract.setContractStatus(ContractStatus.zhengchang);
								hySupplier.setIsActive(true);
								hySupplierService.update(hySupplier);
							} else {
								hySupplierContract.setContractStatus(ContractStatus.weishengxiao);
							}
						
						hySupplierContractService.update(hySupplierContract);
					}
					
					
				} else if (shenheStatus.equals("yibohui")) {
					map.put("result", "bohui");
					hySupplierContract.setAuditStatus(AuditStatus.notpass);
					hySupplierContract.setContractStatus(ContractStatus.weitongguo);
					if(s != null && s.getSupplierStatus() != AuditStatus.pass) {
						s.setSupplierStatus(AuditStatus.notpass);
					}
				}
				hySupplierContractService.update(hySupplierContract);
				Authentication.setAuthenticatedUserId(username);
				taskService.claim(task.getId(),username);
				taskService.addComment(task.getId(), processInstanceId, comment == null ? "审核通过" + ":" + shenheStatus : comment + ":" + shenheStatus);
				taskService.complete(task.getId(), map);			
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
	
	/**
	 * 合同审核详情页面
	 * @param id 合同id
	 * @return 合同审核历史记录 供应商信息 合同信息
	 */
	@RequestMapping(value="detail/view")
	public Json detail(Long id) {
		Json j = new Json();
		
		try {

			HySupplierContract c = hySupplierContractService.find(id);
			HySupplier supplier = c.getHySupplier();
			Map<String, Object> map = new HashMap<String, Object>();
			
			/** 供应商详情 */
			Map<String, Object> sMap = new HashMap<String, Object>();
			/** 审核详情 */
			List<HashMap<String, Object>> shenheMap = new ArrayList<>();
			Map<String, Object> areaMap = new HashMap<String, Object>();
			Map<String, Object> operatorMap = new HashMap<String, Object>();
			
			/**
			 * 供应商详情添加	
			 */
			sMap.put("id",supplier.getId());
			sMap.put("supplierName", supplier.getSupplierName());
			sMap.put("isLine", supplier.getIsLine());
			sMap.put("isVip", supplier.getIsVip());
			sMap.put("pinpaiName", supplier.getPinpaiName());
			sMap.put("isInner", supplier.getIsInner());
			sMap.put("isDijie", supplier.getIsDijie());
			sMap.put("isCaigouqian", supplier.getIsCaigouqian());
			sMap.put("intro", supplier.getIntro());
				
				HyArea area = supplier.getArea();
				List<Long> areaIds = area.getTreePaths();
				areaMap.put("id", area.getId());
				areaMap.put("ids", areaIds);
				areaMap.put("fullName", area.getFullName());
				sMap.put("area", areaMap);
				
				sMap.put("address", supplier.getAddress());
				sMap.put("yycode", supplier.getYycode());
				sMap.put("yy", supplier.getYy());
				sMap.put("jycode", supplier.getJycode());
				sMap.put("jy", supplier.getJy());
				sMap.put("supplierStatus", supplier.getSupplierStatus());
				
				operatorMap.put("username", supplier.getOperator().getUsername());
				operatorMap.put("name", supplier.getOperator().getName());
				sMap.put("operator", operatorMap);
				
				/**
				 * 审核详情添加
				 */
				String processInstanceId = c.getProcessId();
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
				map.put("contract", c);
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
	
	private static void helpler(HySupplierContract tmp, List<Map<String, Object>> ans, String status) {
		HashMap<String, Object> m = new HashMap<>();
		HySupplier supplier = tmp.getHySupplier();
		HyAdmin liable = tmp.getLiable();
		HyAdmin creator = tmp.getCreater();
		m.put("id", tmp.getId());
		
		m.put("shenheStatus", status);	
				
		if(supplier != null) {
			m.put("supplierName", supplier.getSupplierName());
			m.put("isLine", supplier.getIsLine());
			m.put("isInner", supplier.getIsInner());
			m.put("isVip", supplier.getIsVip());
			m.put("pinpaiName", supplier.getPinpaiName());
		}
		
		m.put("contractCode", tmp.getContractCode());
		if(liable != null) {
			m.put("liableName", liable.getName());
			m.put("mobile", liable.getMobile());
		}
		
		m.put("startDate", tmp.getStartDate());
		m.put("endDate", tmp.getDeadDate());
		
		if(creator != null) {
			m.put("applyName", creator.getName());
			
		}
		m.put("applyTime", tmp.getApplyTime());
		ans.add(m);
	}
}