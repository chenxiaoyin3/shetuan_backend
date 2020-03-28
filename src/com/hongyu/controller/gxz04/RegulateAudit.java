package com.hongyu.controller.gxz04;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
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
import com.hongyu.Order;
import com.hongyu.Pageable;
import com.hongyu.entity.CommonShenheedu;
import com.hongyu.entity.Department;
import com.hongyu.entity.Guide;
import com.hongyu.entity.GuideAssignment;
import com.hongyu.entity.GuideSettlementDetail;
import com.hongyu.entity.CommonShenheedu.Eduleixing;
import com.hongyu.entity.Gysfzrtuichu;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyDepartmentModel;
import com.hongyu.entity.HyGroup;
import com.hongyu.entity.HyLine;
import com.hongyu.entity.HyPayablesElement;
import com.hongyu.entity.HyRegulate;
import com.hongyu.entity.HyRegulateitemElement;
import com.hongyu.entity.HyRegulateitemGuide;
import com.hongyu.entity.HyRegulateitemOrder;
import com.hongyu.entity.HySupplier;
import com.hongyu.entity.HySupplierContract;
import com.hongyu.entity.HySupplierElement;
import com.hongyu.entity.Insurance;
import com.hongyu.entity.InsuranceOrder;
import com.hongyu.entity.HySupplierContract.ContractStatus;
import com.hongyu.entity.PayDeposit;
import com.hongyu.entity.PayGuider;
import com.hongyu.entity.PayablesBranchsettle;
import com.hongyu.entity.RegulateGuide;
import com.hongyu.entity.RegulategroupAccount;
import com.hongyu.service.CommonEdushenheService;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.GuideAssignmentService;
import com.hongyu.service.GuideSettlementDetailService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyDepartmentModelService;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.HyPayablesElementService;
import com.hongyu.service.HyRegulateService;
import com.hongyu.service.HyRegulateitemElementService;
import com.hongyu.service.HyRegulateitemOrderService;
import com.hongyu.service.InsuranceOrderService;
import com.hongyu.service.PayGuiderService;
import com.hongyu.service.PayablesBranchsettleService;
import com.hongyu.service.RegulateGuideService;
import com.hongyu.service.RegulategroupAccountService;
import com.hongyu.util.Constants.AuditStatus;
/**
 * 计调报账审核Controller
 * @author guoxinze
 *
 */
@RestController
@RequestMapping("/admin/regulate/shenhe/")
public class RegulateAudit {

	@Resource
	private TaskService taskService;

	@Resource
	private RuntimeService runtimeService;
	
	@Resource
	private HistoryService historyService;
	
	@Resource(name = "hyRegulateitemOrderServiceImpl")
	HyRegulateitemOrderService hyRegulateitemOrderService;
	
	@Resource(name = "hyRegulateitemElementServiceImpl")
	HyRegulateitemElementService hyRegulateitemElementService;
	
	@Resource(name = "hyPayablesElementServiceImpl")
	HyPayablesElementService hyPayablesElementService;
	
	@Resource(name = "regulategroupAccountServiceImpl")
	RegulategroupAccountService regulategroupAccountService;
	
	@Resource(name = "commonEdushenheServiceImpl")
	private CommonEdushenheService commonEdushenheService;
	
	@Resource(name = "departmentServiceImpl")
	DepartmentService  hyDepartmentService;
	
	@Resource(name = "hyDepartmentModelServiceImpl")
	HyDepartmentModelService hyDepartmentModelService;
	
	@Resource(name = "hyRegulateServiceImpl")
	HyRegulateService hyRegulateService;
	
	@Resource(name="hyAdminServiceImpl")
	private HyAdminService hyAdminService;	
	
	@Resource(name = "hyGroupServiceImpl")
	HyGroupService hyGroupService;
	
	@Resource(name = "insuranceOrderServiceImpl")
	InsuranceOrderService insuranceOrderService;
	
	@Resource(name = "regulateGuideServiceImpl")
	RegulateGuideService regulateGuideService;
	
	@Resource(name = "payGuiderServiceImpl")
	PayGuiderService payGuiderService;
	
	@Resource(name = "guideSettlementDetailServiceImpl")
	GuideSettlementDetailService guideSettlementDetailService;
	
	@Resource(name = "guideAssignmentServiceImpl")
	GuideAssignmentService guideAssignmentService;
	
	@Resource(name = "payablesBranchsettleServiceImpl")
	PayablesBranchsettleService payablesBranchsettleService;
	
	/**
	 * 审核列表页-待加入筛选条件部分
	 * @param pageable 分页信息
	 * @param shenheStatus
	 * @param session
	 * @return
	 */
	@RequestMapping(value="list/view")
	public Json list(Pageable pageable, String shenheStatus, String start, String end, HyRegulate regulate, HttpSession session) {
	Json j = new Json();	
		
		try {	
			//得到登录用户
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			List<Filter> filters = FilterUtil.getInstance().getFilter(regulate);
			
			//将字符串日期转成Date类型
			if(start != null && end != null) {
				SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd");
				Date fromDate = simpleFormat.parse(start);
				Date toDate = simpleFormat.parse(end);
				filters.add(Filter.ge("startDate", fromDate));
				filters.add(Filter.lt("endDate", toDate));				
			}
			List<HyRegulate> regulates = hyRegulateService.findList(null, filters, null);
			List<Map<String, Object>> ans = new ArrayList<>();
			Map<String, Object> answer = new HashMap<>();
			if (shenheStatus == null) {
				List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).list();
				for (Task task : tasks) {
					String processInstanceId = task.getProcessInstanceId();
					for (HyRegulate tmp : regulates) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							helpler(tmp, ans, "daishenhe");//待审核数据						
						}
					}
				}
				List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
						.finished().taskAssignee(username).list();
				for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
					String processInstanceId = historicTaskInstance.getProcessInstanceId();
					for (HyRegulate tmp : regulates) {
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
							if(strs.length >= 2) {
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
					for (HyRegulate tmp : regulates) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							helpler(tmp, ans, "daishenhe");//待审核数据		
						}
					}
				}

			} else if (shenheStatus.equals("yishenhe")) {// 搜索已审核任务
				
				List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
						.finished().taskAssignee(username).list();
				for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
					String processInstanceId = historicTaskInstance.getProcessInstanceId();
					for (HyRegulate tmp : regulates) {
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
							if(strs.length >= 2) {
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
	 * 报账申请的ID
	 * @param id
	 * @return
	 */
	@RequestMapping(value="detail/view")
	public Json detail(Long id) {
		Json j = new Json();
		
		try {
				List<Filter> filters = new ArrayList<>();
				
				List<Order> orders = new ArrayList<>();
				
				HyRegulate regulate = hyRegulateService.find(id);	

				Long groupId = regulate.getHyGroup();
				
				HyGroup group = hyGroupService.find(groupId);
				
				HyLine line = group.getLine();
				
				/** 保险详情 */
				Set<Map<String, Object>> baoxian = new HashSet<>(); //保险的数组
				
				Insurance insurance = line.getInsurance();
				if(insurance != null) {
					Long insuranceId = insurance.getId();
					
					//保险方案列表
					filters.clear();
					filters.add(Filter.eq("groupId", groupId));	
					filters.add(Filter.eq("insuranceId", insuranceId));
					orders.clear();
					orders.add(Order.asc("id"));

					List<InsuranceOrder> insuranceOrders = insuranceOrderService.findList(null, filters, orders);
					
					//根据实收款合并
					HashMap<BigDecimal, Integer> hebing = new HashMap<>();
					for(InsuranceOrder i : insuranceOrders) {
						BigDecimal b = i.getReceivedMoney();
						if(hebing.containsKey(b)) {
							hebing.put(b, hebing.get(b) + 1);
						} else {
							hebing.put(b, 1);
						}
					}
					
					for(InsuranceOrder i : insuranceOrders) {
						
						HashMap<String, Object> hm = new HashMap<>();
						BigDecimal receivedMoney = i.getReceivedMoney();
						Integer num = hebing.get(i.getReceivedMoney());
						BigDecimal money = receivedMoney.multiply(BigDecimal.valueOf(num));
						
						hm.put("insuranceSupplierId", insurance.getId());
						hm.put("insuranceCode", insurance.getInsuranceCode());
						hm.put("insuranceStarttime", i.getInsuranceStarttime());
						hm.put("insuranceEndtime", i.getInsuranceEndtime());
						hm.put("receivedMoney", receivedMoney);
						hm.put("num", num);
						hm.put("money", money);
						
						baoxian.add(hm); //不知道去重可不可以
					}
					
				}
				
				
	
				Map<String, Object> map = new HashMap<String, Object>();
								
				
				
				//单团核算表
				RegulategroupAccount account = regulategroupAccountService.find(regulate.getDantuanhesuanbiaoId());
				
				map.put("dantuanhesuan", account);
				
				//单团核算明细表
				HashMap<String, Object> obj = new HashMap<>(); //最终结果
				
				//表头数据--------------------------------------------------------------------
				obj.put("lineSn", account.getLineSn());
				obj.put("lineName", account.getLineName());
				obj.put("days", account.getDays());
				obj.put("startDate", account.getStartDate());
				obj.put("endDate", account.getEndDate());
				obj.put("operatorName", account.getOperatorName());
				obj.put("visitorNo", account.getVisitorNo());
				obj.put("guide", account.getGuide());
				obj.put("allIncome", account.getAllIncome());
				obj.put("allExpense", account.getAllExpense());
				obj.put("profit", account.getProfit());
				obj.put("averageProfit", account.getAverageProfit());
				
				//订单数据--------------------------------------------------------------------
				filters.clear();
				filters.add(Filter.eq("hyGroup", group));
				List<HyRegulateitemOrder> hyRegulateitemOrders = hyRegulateitemOrderService.findList(null, filters, null);
				obj.put("orders", hyRegulateitemOrders);
							
				//收入明细--------------------------------------------------------------------
				filters.clear();
				filters.add(Filter.eq("hyGroup", group));
				filters.add(Filter.eq("isShouru", true)); //找出收入类的旅游元素供应商
				List<HyRegulateitemElement> es = hyRegulateitemElementService.findList(null, filters, null);
				Set<HySupplierElement> supplierElements = new HashSet<>();
				
				for(HyRegulateitemElement temp : es) {
					supplierElements.add(temp.getHySupplierElement());
				}
				
				//生成收入类旅游元素应收入款实体类
				List<HyPayablesElement> payables = new ArrayList<>();
				for(HySupplierElement temp : supplierElements) {
					HyPayablesElement payable = new HyPayablesElement();			
					payable.setHySupplierElement(temp);
					payable.setMoney(BigDecimal.ZERO); //金额
					payables.add(payable);
				}
				
				//找出该团的收入类旅游元素应付款记录
				for(HyRegulateitemElement temp : es) {
					HySupplierElement ele = temp.getHySupplierElement();
					for(HyPayablesElement inner : payables) {
						HySupplierElement eleinner = inner.getHySupplierElement();
						if(ele.getId() == eleinner.getId()) {
							inner.setMoney(inner.getMoney().add(temp.getMoney())); //所有交回金额相加						
							break; //找到一个就跳出循环
						}
					}
				}
				
				List<HashMap<String, Object>> qitashouru = new ArrayList<>();
				
				for(HyPayablesElement temp : payables) {
					HashMap<String, Object> hm = new HashMap<>();
					hm.put("name", temp.getHySupplierElement().getName());
					hm.put("money", temp.getMoney());
					hm.put("yishou", temp.getMoney());
					hm.put("yingshou", BigDecimal.ZERO); //已收等于金额 应收为0
					qitashouru.add(hm);
				}
				
				obj.put("qitashouru", qitashouru);
				
				//支出明细--------------------------------------------------------------------
				filters.clear();
				filters.add(Filter.eq("hyGroup", group));
				List<HyPayablesElement> payableElements = hyPayablesElementService.findList(null, filters, null);
				obj.put("zhichumingxi", payableElements);	
				map.put("mingxi", obj);
				
				//导游报账的
				//导游报账收入总表-------------------------------------------------
				//收入明细
				HashMap<String, Object> obj1 = new HashMap<>(); //最终结果
				List<HashMap<String, Object>> shourumingxi = new ArrayList<>();
				
				//支出明细
				List<HashMap<String, Object>> zhichumingxi = new ArrayList<>();
				
				List<HyRegulateitemElement> eles = regulate.getHyRegulateitemElements();
				
				BigDecimal zongshouru = BigDecimal.ZERO; //总收入
				BigDecimal zongzhichu = BigDecimal.ZERO; //总支出
				
				for(HyRegulateitemElement temp : eles) {
					HashMap<String, Object> hm = new HashMap<>();
					HySupplierElement ele = temp.getHySupplierElement();
					Guide guide = temp.getGuide();
					if(ele != null) {
						if(ele.getIsShouru()) { //收入类
							hm.put("supplierType", ele.getSupplierType());
							hm.put("name", ele.getName());
							hm.put("date", temp.getStartDate()); //收入或支出时间
							if(guide != null) {
								hm.put("gId", guide.getId());
								hm.put("guideName", guide.getName());	
							}
							
							hm.put("money", temp.getMoney()); 
							
							zongshouru = zongshouru.add(temp.getMoney());
							shourumingxi.add(hm);
						} else if (!ele.getIsShouru() && temp.getIsDianfu()){ //支出类垫付
							hm.put("supplierType", ele.getSupplierType());
							hm.put("name", ele.getName());
							hm.put("date", temp.getStartDate()); //收入或支出时间
							if(guide != null) {
								hm.put("gId", guide.getId());
								hm.put("guideName", guide.getName());	
							}
							hm.put("money", temp.getDianfu()); //垫付金额
							
							zongzhichu = zongzhichu.add(temp.getDianfu());
							zhichumingxi.add(hm);
						}
					}
					
				}
				
				obj1.put("shourumingxi", shourumingxi);
				obj1.put("zhichumingxi", zhichumingxi);
				obj1.put("zongshouru", zongshouru);
				obj1.put("zongzhichu", zongzhichu);
				obj1.put("baozhang", zongzhichu.subtract(zongshouru));
				
				//各个导游报账表
				filters.clear();
				filters.add(Filter.eq("groupId", group));
				List<RegulateGuide> regulateGuides = regulateGuideService.findList(null, filters, null);
				obj1.put("fenbiao", regulateGuides);
				map.put("daoyoubaozhang", obj1);
				
				
				
				/** 审核详情 */
				List<HashMap<String, Object>> shenheMap = new ArrayList<>();
				
				map.put("regulate", regulate); //报账详情
				
				
				map.put("baoxian", baoxian); //保险详情
								
				/**
				 * 审核详情添加
				 */
				String processInstanceId = regulate.getProcessInstanceId();
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
	 * 审核(通过或者驳回)报账申请
	 * @param id 报账申请id comment 驳回批注   shenheStatus 审核状态
	 * 		
	 * @return
	 */
	@Transactional
	@RequestMapping(value="audit")
	public Json audit(Long id, String comment, String shenheStatus, HttpSession session) {
		Json json = new Json();

		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyRegulate regulate = hyRegulateService.find(id);
			String applyName = regulate.getApplyName(); //找到提交申请的人
			String processInstanceId = regulate.getProcessInstanceId();

			if (processInstanceId == null || processInstanceId == "") {
				json.setSuccess(false);
				json.setMsg("审核出错，信息不完整，请重新申请");
			} else {
				Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
				HashMap<String, Object> map = new HashMap<>(); //保存流转信息和流程变量信息 下一阶段审核的部门
				
				if(shenheStatus.equals("yitongguo")) {					
					
						map.put("result", "tongguo");
						if(task.getTaskDefinitionKey().equals("usertask2")) { //如果是部门经理需要单独处理，因为有三个流转的地方
							
							//设置下一阶段审核的部门  --- 根据审核额度不同设置不同
							List<Filter> filters = new ArrayList<>();
							filters.add(Filter.eq("eduleixing", Eduleixing.regulateJingli));
							List<CommonShenheedu> edu1 = commonEdushenheService.findList(null, filters, null);
							//获取产品中心经理审核额度
							BigDecimal money1 = edu1.get(0).getMoney();
							filters.clear();
							filters.add(Filter.eq("eduleixing", Eduleixing.regulateLimit));
							List<CommonShenheedu> edu2 = commonEdushenheService.findList(null, filters, null);
							//获取副总审核额度
							BigDecimal money2 = edu2.get(0).getMoney();
							
							if(regulate.getDantuanhesuanbiaoId() != null) {
								RegulategroupAccount account = regulategroupAccountService.find(regulate.getDantuanhesuanbiaoId());
								//超过审核额度,需要产品中心经理审核
								if(account.getAllIncome().compareTo(money1) > 0) {
									map.put("money1","more");	
								}
								//没有超过产品中心经理限额,找产品中心经理审核
								else {
									map.put("money1","less");
									//判断是否超过副总审核限额
									//如果超过副总审核额度,找副总审核
									if(account.getAllIncome().compareTo(money2) > 0) {
										map.put("money2","more");
									}
									//没有超过副总审核限额
									else {
										map.put("money2","less");
									}
								}
							}														

						} 
						else if(task.getTaskDefinitionKey().equals("usertask5")) {//产品中心经理审核
							List<Filter> filters = new ArrayList<>();
							filters.add(Filter.eq("eduleixing", Eduleixing.regulateLimit));
							List<CommonShenheedu> edu = commonEdushenheService.findList(null, filters, null);
							BigDecimal money = edu.get(0).getMoney();
							if(regulate.getDantuanhesuanbiaoId() != null) {
								RegulategroupAccount account = regulategroupAccountService.find(regulate.getDantuanhesuanbiaoId());
							    //如果大于限额
								if(account.getAllIncome().compareTo(money)>0) {
							    	map.put("money","more");
							    }
								//如果小于限额
								else {
									map.put("money","less");
								}
							}
						}
						//如果是副总审核,审核都不用做,直接进入下一步
						else if (task.getTaskDefinitionKey().equals("usertask3")) { //副总限额审核，指定下一阶段财务部门
							
							
						} else if (task.getTaskDefinitionKey().equals("usertask4")) { //财务审核通过
							regulate.setStatus(2); //设置通过状态	
							//导游报账应付款
							List<Filter> filters = new ArrayList<>();
							filters.add(Filter.eq("regulateId", regulate));
							List<RegulateGuide> rguides = regulateGuideService.findList(null, filters, null);					
							for(RegulateGuide r:rguides){
								PayGuider payGuider = new PayGuider();
								payGuider.setType(1);
								payGuider.setGuider(r.getGuideName());
								payGuider.setBankName(r.getGuideId().getBankAccount());
								payGuider.setBankLink(r.getGuideId().getBankLink());
								payGuider.setAccountName(r.getGuideId().getAccountName());
								payGuider.setBankAccount(r.getGuideId().getBankAccount());
								payGuider.setAmount(r.getBaozhang());
								payGuider.setHasPaid(0);					
								payGuider.setRemark(comment);	
								payGuider.setGuiderId(r.getGuideId().getId());
								payGuiderService.save(payGuider);
							}
							//导游费用
							List<HyRegulateitemGuide> hyRegulateitemGuides = regulate.getHyRegulateitemGuides();
							for(HyRegulateitemGuide hyRegulateitemGuide:hyRegulateitemGuides){
								
								GuideSettlementDetail guideSettlementDetail = new GuideSettlementDetail();
								GuideAssignment guideAssignment = guideAssignmentService.find(hyRegulateitemGuide.getAssignmentId());
								
								if(guideAssignment!=null){
									guideSettlementDetail.setStartDate(guideAssignment.getStartDate());
									guideSettlementDetail.setLine(guideAssignment.getLineName());	
									guideSettlementDetail.setDispatchType(guideAssignment.getAssignmentType());  //计调指派
								}
								guideSettlementDetail.setPaiqianId(hyRegulateitemGuide.getAssignmentId());
								guideSettlementDetail.setServiceFee(hyRegulateitemGuide.getFee());
								guideSettlementDetail.setAccountPayable(hyRegulateitemGuide.getMoney());
								guideSettlementDetail.setGuiderId(hyRegulateitemGuide.getGuideId());
								guideSettlementDetail.setGroupId(hyRegulateitemGuide.getHyGroup().getId());
								
								guideSettlementDetail.setServiceType(hyRegulateitemGuide.getType());						
								guideSettlementDetail.setDays(hyRegulateitemGuide.getDays());
								guideSettlementDetail.setTip(hyRegulateitemGuide.getXiaofei());
								guideSettlementDetail.setDeductFee(hyRegulateitemGuide.getKouchu());
								guideSettlementDetail.setIsCanSettle(true);
								guideSettlementDetail.setStatus(0);
								guideSettlementDetail.setRegulate(regulate.getOperatorName());
								guideSettlementDetail.setNumber(hyRegulateitemGuide.getNum());
								guideSettlementDetailService.save(guideSettlementDetail);	
							}
							
						    /*在此插入分公司团结算记录,added by GSbing,20181006*/
							Long groupId=regulate.getHyGroup();
							HyGroup hyGroup=hyGroupService.find(groupId); //找到团
							Department department=hyGroup.getGroupCompany(); //找到团所属公司的部门
							HyDepartmentModel model = department.getHyDepartmentModel();
							if(model.getName().equals("分公司")) { //如果是分公司的团,才进行团结算
								PayablesBranchsettle payablesBranchsettle=new PayablesBranchsettle();
								payablesBranchsettle.setHyGroup(hyGroup);
								payablesBranchsettle.setAdjustMoney(new BigDecimal(0));
								payablesBranchsettle.setAuditStatus(0); //设置为未审核
								payablesBranchsettleService.save(payablesBranchsettle);
							}
						}
					} else if (shenheStatus.equals("yibohui")) {//驳回需要重新提交申请 
						map.put("result", "bohui");
						regulate.setStatus(3); //驳回设置状态
					}
					Authentication.setAuthenticatedUserId(username);
					taskService.claim(task.getId(),username);
					taskService.addComment(task.getId(), processInstanceId, comment == null ? "审核通过" + ":" + shenheStatus : comment + ":" + shenheStatus);
					taskService.complete(task.getId(), map);
					hyRegulateService.update(regulate);
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
	
	private void helpler(HyRegulate regulate, List<Map<String, Object>> ans, String status) {
		HashMap<String, Object> m = new HashMap<>();
		HyGroup group = hyGroupService.find(regulate.getHyGroup());
		HyLine line = group.getLine();
		m.put("id", regulate.getId());
		m.put("shenheStatus", status);	
		m.put("pn", line.getPn());
		m.put("startDay", group.getStartDay());
		m.put("endDay", group.getEndDay());
		m.put("name", line.getName());
		m.put("visitorNum", regulate.getVisitorNum());
		m.put("operator", group.getCreator().getName());
		m.put("applyTime", regulate.getApplyTime());
		ans.add(m);
	}
	
	/**
	 * 判断用户是总公司的还是分公司的
	 * @param username
	 * @return
	 */
	private Long checkIfZonggongsi(String username) {
		HyAdmin admin = hyAdminService.find(username);
		Department depart = admin.getDepartment();
		List<Long> list = depart.getTreePaths();
		Department department = hyDepartmentService.find(list.get(1));
		HyDepartmentModel model = department.getHyDepartmentModel();
		if(model.getName().equals("分公司")) {
			return department.getId();
		}
		return -1L;
	}
	
}
