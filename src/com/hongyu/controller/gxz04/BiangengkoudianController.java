package com.hongyu.controller.gxz04;

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
import com.hongyu.entity.BiangengkoudianEntity;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyArea;
import com.hongyu.entity.HySupplier;
import com.hongyu.entity.HySupplierContract;
import com.hongyu.service.BiangengkoudianService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HySupplierContractService;
import com.hongyu.service.HySupplierDeductChujingService;
import com.hongyu.service.HySupplierDeductGuoneiService;
import com.hongyu.service.HySupplierDeductPiaowuService;
import com.hongyu.service.HySupplierDeductQianzhengService;
import com.hongyu.service.HySupplierDeductQicheService;
import com.hongyu.service.HySupplierDeductRengouService;
import com.hongyu.service.HySupplierService;
import com.hongyu.util.Constants.AuditStatus;
/**
 * 品控变更供应商扣点审核Controller
 * @author guoxinze
 *
 */
@RestController
@RequestMapping("/admin/pinkong/biankoudian/")
@Transactional(propagation = Propagation.REQUIRED)
public class BiangengkoudianController {
	
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

	@Resource(name = "biangengkoudianServiceImpl")
	private BiangengkoudianService biangengkoudianService;
	
	/**
	 * 品控审核变更扣点列表页
	 * @param pageable
	 * @param shenheStatus daishenhe : 待审核, yishenhe : 已审核
	 * @param supplierName 供应商名称
	 * @param contractCode 合同号
	 * @param startDate endDate 申请时间的时间范围
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
			List<HySupplierContract> hcs = new ArrayList<>();
			if(supplierName != null) {
				filters.add(Filter.like("supplierName", supplierName));
				hss = hySupplierService.findList(null, filters, null);
				filters.clear();
				for(HySupplier s : hss) {
					hcs.addAll(s.getHySupplierContracts());
				}
				if(hcs.isEmpty()) {
					j.setMsg("没有满足条件的数据");
					j.setSuccess(true);
					return j;
				}
				filters.add(Filter.in("contractId", hcs));
			} else if (contractCode != null) { //用合同号进行查询
				filters.add(Filter.like("contractCode", contractCode));
				hcs = hySupplierContractService.findList(null, filters, null);
				if(hcs.isEmpty()) {
					j.setMsg("没有满足条件的数据");
					j.setSuccess(true);
					return j;
				}
				filters.clear();
				filters.add(Filter.in("contractId", hcs));
			} else if (startDate != null && endDate != null) {
				filters.add(Filter.gt("applyTime", startDate));
				filters.add(Filter.lt("applyTime", endDate));
			}
			
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			
			List<BiangengkoudianEntity> bgkds = biangengkoudianService.findList(null, filters, null);
			if(!bgkds.isEmpty()) { //查询到数据才开始下一步
			List<Map<String, Object>> ans = new ArrayList<>();
			Map<String, Object> answer = new HashMap<>();
			if (shenheStatus == null) {
				List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).list();
				for (Task task : tasks) {
					String processInstanceId = task.getProcessInstanceId();
					for (BiangengkoudianEntity tmp : bgkds) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							HySupplierContract contract = tmp.getContractId();
							helpler(tmp, contract.getContractCode(), 
									contract.getHySupplier().getSupplierName(), ans, "daishenhe");//待审核数据						
						}
					}
				}
				List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
						.finished().taskAssignee(username).list();
				for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
					String processInstanceId = historicTaskInstance.getProcessInstanceId();
					for (BiangengkoudianEntity tmp : bgkds) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							HySupplierContract contract = tmp.getContractId();
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
							if(strs.length >= 2) {
								if(strs[1] == null) {
									throw new RuntimeException("状态错误");
								}
								helpler(tmp, contract.getContractCode(), 
										contract.getHySupplier().getSupplierName(), ans, strs[1]);
							}							
						}
					}
				}
			} else if (shenheStatus.equals("daishenhe")) {// 搜索未完成任务
				List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).list();
				for (Task task : tasks) {
					String processInstanceId = task.getProcessInstanceId();
					for (BiangengkoudianEntity tmp : bgkds) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							HySupplierContract contract = tmp.getContractId();
							helpler(tmp, contract.getContractCode(), 
									contract.getHySupplier().getSupplierName(), ans, "daishenhe");//待审核数据			
						}
					}
				}

			} else if (shenheStatus.equals("yishenhe")) {// 搜索已审核任务
				
				List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
						.finished().taskAssignee(username).list();
				for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
					String processInstanceId = historicTaskInstance.getProcessInstanceId();
					for (BiangengkoudianEntity tmp : bgkds) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							HySupplierContract contract = tmp.getContractId();
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
							if(strs.length >= 2) {
								if(strs[1] == null) {
									throw new RuntimeException("状态错误");
								}
								helpler(tmp, contract.getContractCode(), 
										contract.getHySupplier().getSupplierName(), ans, strs[1]);
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
			} else {
				j.setSuccess(true);
				j.setMsg("没有满足条件的数据");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	/**
	 * 审核详情页
	 * @param id 变更扣点实体id
	 * @return
	 */
	@RequestMapping(value="detail/view")
	public Json detail(Long id) {
		Json j = new Json();
		try {
			BiangengkoudianEntity entity = biangengkoudianService.find(id);
			HySupplierContract c = entity.getContractId();
			HySupplier supplier = c.getHySupplier();
			Map<String, Object> map = new HashMap<String, Object>();
			
			/** 供应商详情 */
			Map<String, Object> sMap = new HashMap<String, Object>();
			/** 变更扣点详情 */
			Map<String, Object> koudianMap = new HashMap<>();
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
				
				//**********变更扣点详情添加************
				if(entity.getDeductChujingNew() != null) {
					koudianMap.put("deductChujingNew", hySupplierDeductChujingService.find(entity.getDeductChujingNew()));
					koudianMap.put("deductChujingOld", hySupplierDeductChujingService.find(entity.getDeductChujingOld()));
				}
				
				if(entity.getDeductGuoneiNew() != null) {
					koudianMap.put("deductGuoneiNew", hySupplierDeductGuoneiService.find(entity.getDeductGuoneiNew()));
					koudianMap.put("deductGuoneiOld", hySupplierDeductGuoneiService.find(entity.getDeductGuoneiOld()));
				}
				
				if(entity.getDeductPiaowuNew() != null) {
					koudianMap.put("deductPiaowuNew", hySupplierDeductPiaowuService.find(entity.getDeductPiaowuNew()));
					koudianMap.put("deductPiaowuOld", hySupplierDeductPiaowuService.find(entity.getDeductPiaowuOld()));
				}
				
				if(entity.getDeductQianzhengNew() != null) {
					koudianMap.put("deductQianzhengNew", hySupplierDeductQianzhengService.find(entity.getDeductQianzhengNew()));
					koudianMap.put("deductQianzhengOld", hySupplierDeductQianzhengService.find(entity.getDeductQianzhengOld()));
				}
				
				if(entity.getDeductQicheNew() != null) {
					koudianMap.put("deductQicheNew", hySupplierDeductQicheService.find(entity.getDeductQicheNew()));
					koudianMap.put("deductQicheOld", hySupplierDeductQicheService.find(entity.getDeductQicheOld()));
				}
				
				if(entity.getDeductRengouNew() != null) {
					koudianMap.put("deductRengouNew", hySupplierDeductRengouService.find(entity.getDeductRengouNew()));
					koudianMap.put("deductRengouOld", hySupplierDeductRengouService.find(entity.getDeductRengouOld()));
				}
				
				/**
				 * 审核详情添加
				 */
				String processInstanceId = entity.getProcessInstanceId();
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
				map.put("biangengkoudian", koudianMap);
				map.put("contract", c);
			j.setMsg("查看详情成功");
			j.setSuccess(true);
			j.setObj(map);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	/**
	 * 审核接口
	 * @param id 变更扣点实体id
	 * @param comment 批注，在审核不通过时添加
	 * @param shenheStatus yitongguo:已通过 yibohui:已驳回
	 * @param session
	 * @return
	 */
	@RequestMapping(value="audit")
	public Json audit(Long id, String comment, String shenheStatus, HttpSession session) {
		Json j = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			BiangengkoudianEntity entity = biangengkoudianService.find(id);
			String processInstanceId = entity.getProcessInstanceId();

			if (processInstanceId == null || processInstanceId == "") {
				j.setSuccess(false);
				j.setMsg("审核出错，信息不完整，请重新申请");
			} else {
				Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
				HySupplierContract c = entity.getContractId();
				
				if (shenheStatus.equals("yitongguo")) {
					entity.setAuditStatus(AuditStatus.pass);
					//************** 审核通过将新扣点替换原扣点 *****************
					if(entity.getDeductChujingNew() != null) {
						c.setHySupplierDeductChujing(hySupplierDeductChujingService.find(entity.getDeductChujingNew()));
					}
					
					if(entity.getDeductGuoneiNew() != null) {
						c.setHySupplierDeductGuonei(hySupplierDeductGuoneiService.find(entity.getDeductGuoneiNew()));
					}
					
					if(entity.getDeductPiaowuNew() != null) {
						c.setHySupplierDeductPiaowu(hySupplierDeductPiaowuService.find(entity.getDeductPiaowuNew()));
					}
					
					if(entity.getDeductQianzhengNew() != null) {
						c.setHySupplierDeductQianzheng(hySupplierDeductQianzhengService.find(entity.getDeductQianzhengNew()));
					}
					
					if(entity.getDeductQicheNew() != null) {
						c.setHySupplierDeductQiche(hySupplierDeductQicheService.find(entity.getDeductQicheNew()));
					}
					
					if(entity.getDeductRengouNew() != null) {
						c.setHySupplierDeductRengou(hySupplierDeductRengouService.find(entity.getDeductRengouNew()));
					}
				} else if (shenheStatus.equals("yibohui")) {
					entity.setAuditStatus(AuditStatus.notpass);
				}
				Authentication.setAuthenticatedUserId(username);
				taskService.claim(task.getId(),username);
				taskService.addComment(task.getId(), processInstanceId, comment == null ? "审核通过" + ":" + shenheStatus : comment + ":" + shenheStatus);
				taskService.complete(task.getId());
				c.setAuditStatus(AuditStatus.pass);//不管变更扣点审核通过与否都恢复原来的审核已通过状态
				hySupplierContractService.update(c);
				j.setSuccess(true);
				j.setMsg("审核成功");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	private void helpler(BiangengkoudianEntity bgkd, String contractCode, String supplierName, List<Map<String, Object>> ans, String status) {
		HashMap<String, Object> m = new HashMap<>();
		HyAdmin creator = hyAdminService.find(bgkd.getApplyName());
		m.put("id", bgkd.getId());
		m.put("shenheStatus", status);	
		m.put("supplierName", supplierName);
		m.put("contractCode", contractCode);
		m.put("applyTime", bgkd.getApplyTime());
		if(creator != null) {
			m.put("applyName", creator.getName());
		}
		ans.add(m);
	}
}
