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
import com.hongyu.entity.BiangengkoudianEntity;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyArea;
import com.hongyu.entity.HySupplier;
import com.hongyu.entity.HySupplierContract;
import com.hongyu.entity.XuqianEntity;
import com.hongyu.entity.HySupplierContract.ContractStatus;
import com.hongyu.entity.XuqianEntity.Xuqianleixing;
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
import com.hongyu.service.XuqianService;
import com.hongyu.util.Constants.AuditStatus;
/**
 * 品控审核供应商续签合同申请的Controller
 * @author guoxinze
 *
 */
@RestController
@RequestMapping("/admin/pinkong/xuqian/")
@Transactional(propagation = Propagation.REQUIRED)
public class XuqianController {
	
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
	
	@Resource(name="hyAdminServiceImpl")
	private HyAdminService hyAdminService;
	
	@Resource(name = "xuqianServiceImpl")
	private XuqianService xuqianService;
	
	/**
	 * 合同续签审核的列表页面
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
			
			List<XuqianEntity> xuqians = xuqianService.findList(null, filters, null);
			if(!xuqians.isEmpty()) { //查询到数据才开始下一步
			List<Map<String, Object>> ans = new ArrayList<>();
			Map<String, Object> answer = new HashMap<>();
			if (shenheStatus == null) {
				List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).list();
				for (Task task : tasks) {
					String processInstanceId = task.getProcessInstanceId();
					for (XuqianEntity tmp : xuqians) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							HySupplierContract contract = tmp.getContractId();
							helpler(tmp, contract, ans, "daishenhe");//待审核数据						
						}
					}
				}
				List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
						.finished().taskAssignee(username).list();
				for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
					String processInstanceId = historicTaskInstance.getProcessInstanceId();
					for (XuqianEntity tmp : xuqians) {
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
							if(strs[1] == null) {
								throw new RuntimeException("状态错误");
							}
							helpler(tmp, contract, ans, strs[1]);
						}
					}
				}
			} else if (shenheStatus.equals("daishenhe")) {// 搜索未完成任务
				List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).list();
				for (Task task : tasks) {
					String processInstanceId = task.getProcessInstanceId();
					for (XuqianEntity tmp : xuqians) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							HySupplierContract contract = tmp.getContractId();
							helpler(tmp, contract, ans, "daishenhe");//待审核数据			
						}
					}
				}

			} else if (shenheStatus.equals("yishenhe")) {// 搜索已审核任务
				
				List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
						.finished().taskAssignee(username).list();
				for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
					String processInstanceId = historicTaskInstance.getProcessInstanceId();
					for (XuqianEntity tmp : xuqians) {
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
							if(strs[1] == null) {
								throw new RuntimeException("状态错误");
							}
							helpler(tmp, contract, ans, strs[1]);
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
	 * @param id 续签实体的id
	 * @return
	 */
	@RequestMapping(value="detail/view")
	public Json detail(Long id) {
		Json j = new Json();
		try {
			XuqianEntity xuqian = xuqianService.find(id);
			HySupplierContract c = xuqian.getContractId();
			HySupplier supplier = c.getHySupplier();
			Map<String, Object> map = new HashMap<String, Object>();
			
			/** 供应商详情 */
			Map<String, Object> sMap = new HashMap<String, Object>();
			Map<String, Object> areaMap = new HashMap<String, Object>();
			Map<String, Object> operatorMap = new HashMap<String, Object>();
			
			/** 审核详情 */
			List<HashMap<String, Object>> shenheMap = new ArrayList<>();
			
//			/** 直接续签详情 */
//			Map<String, Object> xuqianMap = new HashMap<String, Object>();
//			
//			//直接续签详情添加
//			if(xuqian.getXqlx() == Xuqianleixing.zhijie) {
//				xuqianMap.put("endDate", xuqian.getEndDate());
//				xuqianMap.put("xinyongdaima", xuqian.getXinyongdaima());
//				xuqianMap.put("yingyezhizhao", xuqian.getYingyezhizhao());
//				xuqianMap.put("jingyingxukezheng", xuqian.getJingyingxukezheng());
//				xuqianMap.put("xukezhengzhaopian", xuqian.getXukezhengzhaopian());
//			}
			
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
				String processInstanceId = xuqian.getProcessInstanceId();
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
				map.put("xuqian", xuqian);
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
	 * 审核(通过或者驳回)供应商续签合同的申请
	 * @param id 续签申请实体id comment 驳回批注   shenheStatus 审核状态
	 * 		
	 * @return
	 */
	@Transactional
	@RequestMapping(value="audit")
	public Json audit(Long id, String comment, String shenheStatus, HttpSession session) {
		Json json = new Json();

		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			XuqianEntity xuqian = xuqianService.find(id); //续签信息
			HySupplierContract hySupplierContract = xuqian.getContractId();//续签的新合同或者原合同
			HyAdmin liable = hySupplierContract.getLiable(); //合同负责人
			HySupplier supplier = hySupplierContract.getHySupplier();
			String processInstanceId = xuqian.getProcessInstanceId();

			if (processInstanceId == null || processInstanceId == "") {
				json.setSuccess(false);
				json.setMsg("审核出错，信息不完整，请重新申请");
			} else {
				Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
				if (shenheStatus.equals("yitongguo")) { //供应商续签审核通过 1、直接续签通过  2、变更续签通过
					xuqian.setAuditStatus(AuditStatus.pass); //续签申请状态通过
					hyAdminService.validateAdmin(liable.getUsername());//如果负责人账号在合同期外到期了需要将账号开启
					if(xuqian.getXqlx() == Xuqianleixing.zhijie) { //如果是直接续签通过					
						//改变合同状态
						hySupplierContract.setDeadDate(xuqian.getEndDate());					
						hySupplierContract.setContractStatus(ContractStatus.zhengchang);//合同状态修改为正常
						supplier.setIsActive(true);
						supplier.setYy(xuqian.getYingyezhizhao());
						supplier.setYycode(xuqian.getXinyongdaima());
						supplier.setJy(xuqian.getXukezhengzhaopian());
						supplier.setJycode(xuqian.getJingyingxukezheng());
						
					} else if (xuqian.getXqlx() == Xuqianleixing.biangeng) { //如果是变更续签通过
						
						//首先设置变更合同的应付押金和应退押金
						HySupplierContract yuanhetong = hySupplierContract.getHySupplierContract();
						BigDecimal yuanyajin = yuanhetong.getDeposit();
						BigDecimal xianyajin = hySupplierContract.getDeposit();
						BigDecimal shouldPay = yuanyajin.compareTo(xianyajin) >= 0 ? BigDecimal.ZERO : xianyajin.subtract(yuanyajin); //应付押金
						BigDecimal shouldReturn = yuanyajin.compareTo(xianyajin) > 0 ? yuanyajin.subtract(xianyajin) : BigDecimal.ZERO; //应退还押金
	
						hySupplierContract.setAuditStatus(AuditStatus.pass);//设置合同审核状态为已通过
						if(shouldPay == BigDecimal.ZERO) { //如果供应商不需要再交额外押金，合同按照交完押金的逻辑 原合同需要退还部分押金
							yuanhetong.setReturnDeposit(shouldReturn); //如果需要退还押金，将退还押金金额写入原合同
							hySupplierContract.setShouldpayDeposit(BigDecimal.ZERO); //将续签的合同应付押金设置为0
							Date curDate = new Date();
							Date preTime = hySupplierContract.getStartDate();
							Date latterTime = hySupplierContract.getDeadDate();
							if(preTime.before(curDate) && curDate.before(latterTime)) {
								hySupplierContract.setContractStatus(ContractStatus.zhengchang); //如果当前期限已经在合同期限内， 就设置为正常
								//设置续签合同的负责人信息--在合同生效的时候才进行设置
								liable.setRole(xuqian.getRoleId());
								liable.setName(xuqian.getFuzeren());
								liable.setMobile(xuqian.getDianhua());
								liable.setQq(xuqian.getQqhao());
								liable.setWechat(xuqian.getWeixin());
								liable.setWechatUrl(xuqian.getWeixinerweima());
								supplier.setIsActive(true);
								hySupplierService.update(supplier);
								yuanhetong.setContractStatus(ContractStatus.yibiangeng); //原合同状态变为已变更
							} else {
								hySupplierContract.setContractStatus(ContractStatus.weishengxiao); //如果当前期限不在合同期限内，设置为未生效
							}
						} else if(shouldPay.compareTo(BigDecimal.ZERO) > 0) { //如果需要交额外押金，还需要走一遍交押金的流程--待修改，再交完押金以后需要修改新合同的负责人信息
							yuanhetong.setReturnDeposit(BigDecimal.ZERO);
							hySupplierContract.setShouldpayDeposit(shouldPay);
							hySupplierContract.setContractStatus(ContractStatus.dongjie);
						}
					}
					
					
				} else if (shenheStatus.equals("yibohui")) { //如果提交的续签申请被驳回
					xuqian.setAuditStatus(AuditStatus.notpass);
					if(xuqian.getXqlx() == Xuqianleixing.biangeng) { //如果是变更续签被驳回
						hySupplierContract.setAuditStatus(AuditStatus.notpass);
						hySupplierContract.setContractStatus(ContractStatus.weitongguo);
					}
				}
				hySupplierContractService.update(hySupplierContract); //更新合同信息同时级联更新供应商和负责人信息--待考证		
				Authentication.setAuthenticatedUserId(username);
				taskService.claim(task.getId(),username);
				taskService.addComment(task.getId(), processInstanceId, comment == null ? "审核通过" + ":" + shenheStatus : comment + ":" + shenheStatus);
				taskService.complete(task.getId());			
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
	
	private void helpler(XuqianEntity entity, HySupplierContract tmp, List<Map<String, Object>> ans, String status) {
		HashMap<String, Object> m = new HashMap<>();
		HySupplier supplier = tmp.getHySupplier();
		HyAdmin applier = hyAdminService.find(entity.getApplyName());
		HyAdmin liable = tmp.getLiable();
		m.put("id", entity.getId());
		m.put("shenheStatus", status);	
		m.put("xuqianleixing", entity.getXqlx());
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

		m.put("applyName", applier.getName());
		m.put("applyTime", entity.getApplyTime());
		ans.add(m);
	}
}
