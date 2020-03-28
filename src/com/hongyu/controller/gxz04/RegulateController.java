package com.hongyu.controller.gxz04;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.BankList;
import com.hongyu.entity.CommonShenheedu;
import com.hongyu.entity.CouponMoney;
import com.hongyu.entity.Department;
import com.hongyu.entity.Guide;
import com.hongyu.entity.GuideAssignment;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyDepartmentModel;
import com.hongyu.entity.HyGroup;
import com.hongyu.entity.HyLine;
import com.hongyu.entity.HyLine.LineType;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HyOrderApplication;
import com.hongyu.entity.HyOrderCustomer;
import com.hongyu.entity.HyOrderItem;
import com.hongyu.entity.HyPayablesElement;
import com.hongyu.entity.HyRegulate;
import com.hongyu.entity.HyRegulateitemElement;
import com.hongyu.entity.HyRegulateitemGuide;
import com.hongyu.entity.HyRegulateitemOrder;
import com.hongyu.entity.HyRole;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.entity.HySupplier;
import com.hongyu.entity.HySupplierContract;
import com.hongyu.entity.HySupplierContract.ContractStatus;
import com.hongyu.entity.HySupplierDeductChujing;
import com.hongyu.entity.HySupplierDeductGuonei;
import com.hongyu.entity.HySupplierDeductQiche;
import com.hongyu.entity.HySupplierElement;
import com.hongyu.entity.HySupplierElement.SupplierType;
import com.hongyu.entity.Insurance;
import com.hongyu.entity.InsuranceOrder;
import com.hongyu.entity.RegulateGuide;
import com.hongyu.entity.RegulategroupAccount;
import com.hongyu.entity.CommonShenheedu.Eduleixing;
import com.hongyu.service.BankListService;
import com.hongyu.service.CommonEdushenheService;
import com.hongyu.service.CouponMoneyService;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.FinishedGroupItemOrderService;
import com.hongyu.service.GuideAssignmentService;
import com.hongyu.service.GuideService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyDepartmentModelService;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.HyOrderApplicationService;
import com.hongyu.service.HyOrderItemService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.HyPayablesElementService;
import com.hongyu.service.HyRegulateService;
import com.hongyu.service.HyRegulateitemElementService;
import com.hongyu.service.HyRegulateitemGuideService;
import com.hongyu.service.HyRegulateitemOrderService;
import com.hongyu.service.HySupplierContractService;
import com.hongyu.service.HySupplierElementService;
import com.hongyu.service.HySupplierService;
import com.hongyu.service.InsuranceOrderService;
import com.hongyu.service.RegulateGuideService;
import com.hongyu.service.RegulategroupAccountService;
import com.hongyu.service.StoreService;
import com.hongyu.util.AuthorityUtils;
import com.hongyu.util.Constants.DeductLine;

@RestController
@Transactional(propagation = Propagation.REQUIRED)
@RequestMapping("/admin/regulate/")
public class RegulateController {
	
	public static class Wrap {
		public Long regulateId;
		public Long groupId;
		public List<HyRegulateitemGuide> hyRegulateitemGuide;
		public List<HyRegulateitemElement> hyRegulateitemElement;
		public List<HyRegulateitemGuide> getHyRegulateitemGuide() {
			return hyRegulateitemGuide;
		}
		public void setHyRegulateitemGuide(List<HyRegulateitemGuide> hyRegulateitemGuide) {
			this.hyRegulateitemGuide = hyRegulateitemGuide;
		}
		public List<HyRegulateitemElement> getHyRegulateitemElement() {
			return hyRegulateitemElement;
		}
		public void setHyRegulateitemElement(List<HyRegulateitemElement> hyRegulateitemElement) {
			this.hyRegulateitemElement = hyRegulateitemElement;
		}
		public Long getRegulateId() {
			return regulateId;
		}
		public void setRegulateId(Long regulateId) {
			this.regulateId = regulateId;
		}
		public Long getGroupId() {
			return groupId;
		}
		public void setGroupId(Long groupId) {
			this.groupId = groupId;
		}	
		
	}
	
	@Resource
	private TaskService taskService;
	
	@Resource
	private HistoryService historyService;

	@Resource
	private RuntimeService runtimeService;
	
	@Resource(name = "hyDepartmentModelServiceImpl")
	HyDepartmentModelService hyDepartmentModelService;
	
	@Resource(name = "departmentServiceImpl")
	DepartmentService  hyDepartmentService;
	
	@Resource(name = "hyPayablesElementServiceImpl")
	HyPayablesElementService hyPayablesElementService;
	
	@Resource(name = "insuranceOrderServiceImpl")
	InsuranceOrderService insuranceOrderService;
	
	@Resource(name="hySupplierServiceImpl")
	private HySupplierService hySupplierService;
	
	@Resource(name="hySupplierContractServiceImpl")
	private HySupplierContractService hySupplierContractService;
	
	@Resource(name = "hyOrderItemServiceImpl")
	HyOrderItemService hyOrderItemService;
	
	@Resource(name = "hyOrderApplicationServiceImpl")
	HyOrderApplicationService hyOrderApplicationService;
	
	@Resource(name="guideServiceImpl")
	GuideService guideService;
	
	@Resource(name = "hySupplierElementServiceImpl")
	HySupplierElementService  hySupplierElementService;
	
	@Resource(name = "bankListServiceImpl")
	BankListService  bankListService;
	
	@Resource(name = "hyRegulateServiceImpl")
	HyRegulateService hyRegulateService;
	
	@Resource(name = "hyRegulateitemOrderServiceImpl")
	HyRegulateitemOrderService hyRegulateitemOrderService;
	
	@Resource(name = "hyRegulateitemGuideServiceImpl")
	HyRegulateitemGuideService hyRegulateitemGuideService;
	
	@Resource(name = "hyRegulateitemElementServiceImpl")
	HyRegulateitemElementService hyRegulateitemElementService;
	
	@Resource(name="hyAdminServiceImpl")
	private HyAdminService hyAdminService;	
	
	@Resource(name = "hyGroupServiceImpl")
	HyGroupService hyGroupService;
	
	@Resource(name = "guideAssignmentServiceImpl")
	GuideAssignmentService guideAssignmentService;
	
	@Resource(name = "regulategroupAccountServiceImpl")
	RegulategroupAccountService regulategroupAccountService;
	
	@Resource(name = "regulateGuideServiceImpl")
	RegulateGuideService regulateGuideService;
	
	@Resource(name = "hyOrderServiceImpl")
	HyOrderService hyOrderService;
	
	@Resource(name = "couponMoneyServiceImpl")
	CouponMoneyService couponMoneyService;
	
	@Resource(name = "storeServiceImpl")
	private StoreService storeService;
	
	@Resource(name = "commonEdushenheServiceImpl")
	CommonEdushenheService commonEdushenheService;
	
	@Resource(name = "finishedGroupItemOrderServiceImpl")
	FinishedGroupItemOrderService finishedGroupItemOrderService;
	/**
	 * 报账列表页
	 * @param pageable
	 * @param regulate
	 * @return
	 */
	@RequestMapping(value="list/view")
	public Json list(Pageable pageable, HyRegulate queryParam, String start, String end, HttpSession session, HttpServletRequest request) {
		Json j = new Json();
		try {
			
			Map<String, Object> obj = new HashMap<String, Object>();
			List<Map<String, Object>> lhm = new ArrayList<>();
			List<Filter> filters = new ArrayList<Filter>();
			
			//将字符串日期转成Date类型
			if(start != null && end != null) {
				SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd");
				Date fromDate = simpleFormat.parse(start);
				Date toDate = simpleFormat.parse(end);
				filters.add(Filter.ge("startDate", fromDate));//add by cqx
				filters.add(Filter.le("endDate", toDate));//add by cqx
			}
			
			
			/**
			 * 获取当前用户
			 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			
			/** 
			 * 获取用户权限范围
			 */
			CheckedOperation co = (CheckedOperation) request.getAttribute("co");
			
			/** 所有符合条件的账号 ,默认可以看到自己创建的数据 */
			Set<HyAdmin> hyAdmins = AuthorityUtils.getAdmins(session, request);
			
			
			filters.add(Filter.in("operator", hyAdmins));			

			pageable.setFilters(filters);
			
			List<Order> orders = new ArrayList<>();
			orders.add(Order.desc("id"));
			pageable.setOrders(orders);
			
			Page<HyRegulate> page = hyRegulateService.findPage(pageable, queryParam);
			if(page.getRows().size() > 0) {
				for(HyRegulate hyRegulate : page.getRows()) {
					HashMap<String, Object> hm = new HashMap<String, Object>();
			        HyAdmin creater = hyRegulate.getOperator();
			        Long groupId = hyRegulate.getHyGroup();
			        HyGroup group = hyGroupService.find(groupId);
			        if(group == null) {
			        	throw new RuntimeException("团" + groupId + "不存在");
			        }
			        HyLine line = group.getLine();
			        hm.put("id", hyRegulate.getId());
			        hm.put("groupId", groupId);
			        hm.put("lineId", line.getId());
			        hm.put("lineSn", hyRegulate.getLineSn());
			        hm.put("startDate", hyRegulate.getStartDate());
			        hm.put("endDate", hyRegulate.getEndDate());
			        hm.put("lineName", hyRegulate.getLineName());
			        hm.put("visitorNum", hyRegulate.getVisitorNum());	
			        
			        if (creater != null) {
			        	hm.put("operator", creater.getName());
			        }
			        hm.put("status", hyRegulate.getStatus());
			        
			      	/** 当前用户对本条数据的操作权限 */
					if(creater.equals(admin)){
						if(co == CheckedOperation.view) {
								hm.put("privilege", "view");
						} else {
								hm.put("privilege", "edit");
						}
					} else{
						if(co == CheckedOperation.edit) {
							hm.put("privilege", "edit");
						} else {
							hm.put("privilege", "view");
						}
					}
			        lhm.add(hm);
				}
			}
			
			obj.put("pageSize", Integer.valueOf(page.getPageSize()));
			obj.put("pageNumber", Integer.valueOf(page.getPageNumber()));
			obj.put("total", Long.valueOf(page.getTotal()));
     		obj.put("rows", lhm);
			
			j.setSuccess(true);
			j.setMsg("获取列表成功");
			j.setObj(obj);
			
			j.setSuccess(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}

	/**
	 * 计调报账详情页 -- 缺少线路电子券的
	 * @param id 报账的ID
	 * @return
	 */
	@RequestMapping(value="detail/view")
	public Json detail(Long regulateId) {
		Json j = new Json();
		try {
			/** 审核详情 */
			List<HashMap<String, Object>> shenheMap = new ArrayList<>();
			
			List<Filter> filters = new ArrayList<>();
			
			List<Order> orders = new ArrayList<>();
			
			HyRegulate regulate = hyRegulateService.find(regulateId);
			String processInstanceId = regulate.getProcessInstanceId();
			
			Long groupId = regulate.getHyGroup();
			
			HyGroup group = hyGroupService.find(groupId);
			
			if(group == null) {
				throw new RuntimeException("团" + groupId + "不存在");
			}
			
			HyLine line = group.getLine();
			
		
			
			HashMap<String, Object> obj = new HashMap<>(); //结果
			
			List<Map<String, Object>> lhm = new ArrayList<>(); //导游派遣的数组
			
			List<Map<String, Object>> guideLhm = new ArrayList<>(); //导游的数组
			
			Set<Map<String, Object>> baoxian = new HashSet<>(); //保险的数组
			
			//导游派遣列表 --- 导游
			filters.clear();
			filters.add(Filter.eq("groupId", groupId));	
			//修改status为1的才添加到guideAssignments-20180714
			filters.add(Filter.eq("status", 1));
			orders.clear();
			orders.add(Order.asc("id"));
			List<GuideAssignment> guideAssignments = guideAssignmentService.findList(null, filters, orders); //导游派遣数组
			List<Guide> guides = new ArrayList<>();
			
			for(GuideAssignment a : guideAssignments) {
				HashMap<String, Object> hm = new HashMap<>();
				Guide g = guideService.find(a.getGuideId());
				//修改全陪导游才加到guides里面-20180714
				if(a.getServiceType()==0) {
					guides.add(g);
				}
				hm.put("id", a.getId()); //导游派遣的id
				hm.put("guideId", g.getId());
				hm.put("serviceType", a.getServiceType());
				hm.put("name", g.getName());
				hm.put("guideSn", g.getGuideSn());
				hm.put("visitorNum", regulate.getVisitorNum());
				hm.put("days", line.getDays());
				hm.put("serviceFee", a.getServiceFee());
				hm.put("tip", a.getTip());
				hm.put("money", a.getTotalFee());
				hm.put("kouchu", BigDecimal.ZERO); //初始化扣除为0
				//找到导游的扣除金额
				filters.clear();
				filters.add(Filter.eq("guideAssignment", a));	
				List<HyRegulateitemGuide> rgs = hyRegulateitemGuideService.findList(null, filters, null);
				if(!rgs.isEmpty() && rgs.get(0).getKouchu() != null) {
					hm.put("kouchu", rgs.get(0).getKouchu()); //如果已经扣除过费用就填入扣除金额
				}
				
				BigDecimal money = (BigDecimal) hm.get("money");
				BigDecimal kouchu = (BigDecimal) hm.get("kouchu");
				hm.put("shouldPay", money.subtract(kouchu)); //金额减去扣除金额为应付款
				lhm.add(hm);
			}
			
			obj.put("guideAssignment", lhm);
			
			//导游下拉列表			
			
			for(Guide g : guides) {
				HashMap<String, Object> hm = new HashMap<>();
				hm.put("id", g.getId());
				hm.put("name", g.getName());
				guideLhm.add(hm);
			}
			
			obj.put("guide", guideLhm);
			
			//保险方案列表
			Insurance insurance = line.getInsurance();
			if(insurance != null) {
				Long insuranceId = insurance.getId();
				filters.clear();
				filters.add(Filter.eq("groupId", groupId));	
				filters.add(Filter.eq("insuranceId", insuranceId));

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
			
			obj.put("baoxian", baoxian);
			
			//电子券的表格
			HashMap<String, Object> dianziquan = new HashMap<>();
			if(group.getIsCoupon()) {
				CouponMoney cm = couponMoneyService.find(group.getCouponId());
				filters.clear();
				filters.add(Filter.eq("type", 1));
				filters.add(Filter.eq("productId", groupId)); //根据线路类型和团的ID找到满足条件的订单
				List<HyOrderItem> items = hyOrderItemService.findList(null, filters, null);
				
				//统计电子券的数量
				int number = 0;
				for(HyOrderItem item : items) {
					List<HyOrderCustomer> hyOrderCustomers = item.getHyOrderCustomers();
					for(HyOrderCustomer customer : hyOrderCustomers) {
						if(customer.getIsCoupon() != null && customer.getIsCoupon()) {
							number ++;
						}
					}
				}
				dianziquan.put("num", number); //电子券数量
				dianziquan.put("price", BigDecimal.valueOf(cm.getMoney()).
						multiply(BigDecimal.valueOf(cm.getRebateRatio()))); //计算电子券单价
				dianziquan.put("money", BigDecimal.valueOf(cm.getMoney()).
						multiply(BigDecimal.valueOf(cm.getRebateRatio())).
						multiply(BigDecimal.valueOf(number))); //电子券金额
				dianziquan.put("issueType", cm.getIssueType()); //电子券种类
			}
			
			obj.put("dianziquan", dianziquan);
						
			//如果已经编辑过就将编辑的内容返回给前端
			
			obj.put("hyRegulateitemElements", regulate.getHyRegulateitemElements());
			
			if(regulate.getHyRegulateitemGuides() == null && regulate.getHyRegulateitemElements() == null) {
				obj.put("isNew", true);
			} else {
				obj.put("isNew", false);
			}
			
			/**
			 * 审核详情添加
			 */
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
			
			obj.put("auditRecord", shenheMap);

			
			j.setObj(obj);
			j.setMsg("查看成功");
			j.setSuccess(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	/**
	 * 新建旅游元素的接口
	 * @param hySupplierElement
	 * @param bankList
	 * @return
	 */
	@RequestMapping(value="addSupplierElement", method = RequestMethod.POST)
	@ResponseBody
	public Json addSupplierElement(HySupplierElement hySupplierElement, BankList bankList) {
		Json j = new Json();
		try{
			if(bankList.getBankAccount() != null){
				bankListService.save(bankList);
				hySupplierElement.setBankList(bankList);
				hySupplierElementService.save(hySupplierElement);
			}else {
				hySupplierElementService.save(hySupplierElement);
			}
			j.setSuccess(true);
			j.setMsg("添加成功！");
		}catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg("供应商名字重复");
		}
		return j;
	}
	
	/**
	 * 编辑计调报账的保存
	 * @param session
	 * @param wrap
	 * @return
	 */
	@RequestMapping(value="add")
	public Json add(HttpSession session, @RequestBody Wrap wrap) {
		Json j = new Json();
		try {			
			List<HyRegulateitemGuide> hyRegulateitemGuides = wrap.getHyRegulateitemGuide();
			List<HyRegulateitemElement> hyRegulateitemElements = wrap.getHyRegulateitemElement();
			Long regulateId = wrap.getRegulateId();
			Long groupId = wrap.getGroupId();
			HyRegulate regulate = hyRegulateService.find(regulateId);
			HyGroup hyGroup = hyGroupService.find(groupId);
			HyLine line = null;
			if(null != hyGroup) {
				line = hyGroup.getLine();
			}
			
			//保存导游报账信息
			List<HyRegulateitemGuide> hrgs = regulate.getHyRegulateitemGuides();
			if(!hyRegulateitemGuides.isEmpty()) { 
				for(HyRegulateitemGuide a : hyRegulateitemGuides) {
					Guide guide = guideService.find(a.getGuideId());
					a.setHyRegulate(regulate);
					a.setHyGroup(hyGroup);
					a.setGuideAssignment(guideAssignmentService.find(a.getAssignmentId()));
					a.setGuide(guide);
					a.setYingfu(a.getMoney().subtract(a.getKouchu())); //计算应付
					a.setGuideSn(guide.getGuideSn());
					//hyRegulateitemGuideService.save(a); //报账的时候才生成一条导游的报账表
				}
			}
			hrgs.clear();
			hrgs.addAll(hyRegulateitemGuides);
			
			//保存旅游元素报账信息 --- 有疑问，导游的收入类型交回金额应该算啥
			List<HyRegulateitemElement> hres = regulate.getHyRegulateitemElements();
			if(!hyRegulateitemElements.isEmpty()) {
				for(HyRegulateitemElement a : hyRegulateitemElements) {
					HySupplierElement element = hySupplierElementService.find(a.getSupplierElement());					
					a.setType(element.getSupplierType());
					a.setIsShouru(element.getIsShouru());
					a.setHyGroup(hyGroup);
					a.setHyRegulate(regulate);
					a.setSupplierName(element.getName());
					a.setHySupplierElement(element);
					if(a.getGuideId() != null) {
						a.setGuide(guideService.find(a.getGuideId()));
					}
					if(element.getSupplierType() == SupplierType.insurance) {
						a.setIsDianfu(false);
						a.setRealMoney(BigDecimal.ZERO); //保险的应付款是0
					}
					if(element.getSupplierType() == SupplierType.linelocal && a.getSupplierContractId() != null) { //如果是地接，需要设置合同
						a.setContractId(hySupplierContractService.find(a.getSupplierContractId()));
						a.setContractCode(a.getContractId().getContractCode());
					}
					//hyRegulateitemElementService.save(a); //保存旅游元素报账实体
				}
			}
			hres.clear();
			hres.addAll(hyRegulateitemElements);
			
			hyRegulateService.update(regulate);
			
			//2019051修改，保存的时候生成单团核算表，用于生成报账报表
			/******************************** 生成单团核算表 ******************************/	
			List<Filter> filters = new ArrayList<>();
			List<Order> orders = new ArrayList<>();
			filters.add(Filter.eq("groupId", groupId));	
			filters.add(Filter.eq("status",1));
			orders.clear();
			orders.add(Order.asc("id"));
			List<GuideAssignment> guideAssignments = guideAssignmentService.findList(null, filters, orders); //导游派遣数组
			Set<Guide> baozhangGuides = new HashSet<>(); //本团所有被派遣的导游
			String guideName = "";
			
			for(GuideAssignment a : guideAssignments) {
				baozhangGuides.add(guideService.find(a.getGuideId()));
			}
			
			for(Guide temp : baozhangGuides) {
				String gn = temp.getName();
				guideName = guideName + gn + ",";
			}
			
			if(baozhangGuides.size() > 0) {
				guideName = guideName.substring(0, guideName.length() - 1); //导游合起来的姓名
			}		
			//重新提交以后相当于编辑，将原来的单团核算记录删除，重新用新的
			filters.clear();
			filters.add(Filter.eq("groupId", groupId));
			List<RegulategroupAccount> regulategroupAccounts = regulategroupAccountService.findList(null, filters, null);
			if(!regulategroupAccounts.isEmpty()) {
				RegulategroupAccount temp = regulategroupAccounts.get(0);
				regulategroupAccountService.delete(temp);				
			}
			
			RegulategroupAccount account = new RegulategroupAccount();
			account.setRegulateId(regulate);
			account.setGroupId(hyGroup);
			if(null != line) {
				account.setLineSn(line.getPn());
				account.setLineName(line.getName());
				account.setDays(line.getDays());
				account.setOperatorName(line.getOperator().getName());
			}
		
			account.setStartDate(hyGroup.getStartDay());
			account.setEndDate(hyGroup.getEndDay());
			
			account.setVisitorNo(regulate.getVisitorNum());
			account.setGuide(guideName);
			
			//计算团款
			BigDecimal groupMoney = calculateGroup(groupId);
			account.setGroupMoney(groupMoney);
			
			//计算购物收入	
			BigDecimal shopping = calculateElement(regulate, SupplierType.shopping);
			account.setShopping(shopping);
			
			//计算自费
			BigDecimal selfExpense = calculateElement(regulate, SupplierType.selfpay);
			account.setSelfExpense(selfExpense);
			
			//计算其他收入
			BigDecimal incomes = calculateElement(regulate, SupplierType.otherincome);
			account.setIncomes(incomes);
			
			//计算导游支出
			account.setGuidePrice(calculateGuide(regulate));
			
			//计算用餐支出
			account.setRestaurant(calculateElement(regulate, SupplierType.catering));
			
			//计算车辆支出
			account.setVehicle(calculateElement(regulate, SupplierType.car));
			
			//计算大交通支出
			account.setTraffic(calculateElement(regulate, SupplierType.traffic));
			
			//计算住宿支出
			account.setHotel(calculateElement(regulate, SupplierType.hotel));
			
			//计算保险支出
			account.setInsurance(calculateElement(regulate, SupplierType.insurance));
			
			//计算电子券支出
			account.setCoupon(calculateElement(regulate, SupplierType.coupon));
			
			//计算其他支出
			account.setOutgoings(calculateElement(regulate, SupplierType.otherexpend));
			
			//计算线路地接支出
			account.setDijie(calculateElement(regulate, SupplierType.linelocal));
			
			//计算门票支出
			account.setTicket(calculateElement(regulate, SupplierType.ticket));
			
			//计算总收入
			account.setAllIncome(groupMoney.add(shopping).add(selfExpense).add(incomes));
			
			//计算总支出
			account.setAllExpense(account.getGuidePrice().add(account.getRestaurant()).
								  add(account.getVehicle()).add(account.getTraffic()).
								  add(account.getHotel()).add(account.getOutgoings()).
								  add(account.getInsurance()).add(account.getDijie()).
								  add(account.getCoupon()).add(account.getTicket()));
			
			//计算单团利润
			account.setProfit(account.getAllIncome().subtract(account.getAllExpense()));
			
			//计算人均利润
			if(regulate.getVisitorNum() == 0) {
				account.setAverageProfit(null);
			} else {
				account.setAverageProfit(account.getProfit().divide(BigDecimal.valueOf(regulate.getVisitorNum()), 10, RoundingMode.HALF_DOWN));
			}			
			regulategroupAccountService.save(account); //保存单团核算表
			regulate.setDantuanhesuanbiaoId(account.getId());
			hyRegulateService.update(regulate);
			/*更新的单团利润表,add by liyang 20190519*/
			finishedGroupItemOrderService.updateGroupItemOrder(account, groupId);
			j.setSuccess(true);
			j.setMsg("保存成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	/**
	 * 获取地接供应商的有效合同-渲染页面
	 * @param id 地接供应商id
	 * @return
	 */
//	@RequestMapping(value="contract/view")
//	public Json contract(Long id, Long groupId) {
//		Json j = new Json();
//		try {
//			List<HashMap<String, Object>> lhm = new ArrayList<>();
//			HySupplierElement e = hySupplierElementService.find(id); 
//			HySupplier s = hySupplierService.find(e.getSupplierLine());
//			Set<HySupplierContract> cs = s.getHySupplierContracts();
//			
//			for(HySupplierContract a : cs) {
//				if(a.getContractStatus() == ContractStatus.zhengchang) {
//					HashMap<String, Object> hm = new HashMap<>();
//					hm.put("id", a.getId());
//					hm.put("contractCode", a.getContractCode());
//					
//					//新增
//					HyGroup group = hyGroupService.find(groupId); //获取团
//					
//					HyLine line = group.getLine();
//					
//					LineType lineType = line.getLineType();
//					
//					if(lineType == LineType.chujing) {
//						HySupplierDeductChujing cj = a.getHySupplierDeductChujing();
//						hm.put("deductType", cj.getDeductChujing());
//						if(cj.getDeductChujing() == DeductLine.rentou) {
//							hm.put("deduct", cj.getRentouChujing());
//						} else {
//							hm.put("deduct", cj.getTuankeChujing());
//						}
//						
//					} else if (lineType == LineType.guonei) {
//						HySupplierDeductGuonei gn = a.getHySupplierDeductGuonei();
//						hm.put("deductType", gn.getDeductGuonei());
//						if(gn.getDeductGuonei() == DeductLine.rentou) {
//							hm.put("deduct", gn.getRentouGuonei());
//						} else {
//							hm.put("deduct", gn.getTuankeGuonei());
//						}
//					} else if (lineType == LineType.qiche) {
//						HySupplierDeductQiche qc = a.getHySupplierDeductQiche();
//						hm.put("deductType", qc.getDeductQiche());
//						if(qc.getDeductQiche() == DeductLine.rentou) {
//							hm.put("deduct", qc.getRentouQiche());
//						} else {
//							hm.put("deduct", qc.getTuankeQiche());
//						}
//					}
//									
//					lhm.add(hm);
//				}		
//			}
//			j.setObj(lhm);
//			j.setMsg("查看有效合同成功");
//			j.setSuccess(true);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			j.setSuccess(false);
//			j.setMsg(e.getMessage());
//		}
//		return j;
//	}
	
	/**
	 * 获取地接供应商的扣点-渲染页面
	 * @param contractId 合同ID groupId 团ID
	 * @return
	 */
//	@RequestMapping(value="dijie/view")
//	public Json dijie(Long contractId, Long groupId) {
//		Json j = new Json();
//		try {
//			HashMap<String, Object> hm = new HashMap<>();
//			
//			HySupplierContract c = hySupplierContractService.find(contractId); //获取合同
//			
//			HyGroup group = hyGroupService.find(groupId); //获取团
//			
//			HyLine line = group.getLine();
//			
//			LineType lineType = line.getLineType();
//			
//			if(lineType == LineType.chujing) {
//				HySupplierDeductChujing cj = c.getHySupplierDeductChujing();
//				hm.put("deductType", cj.getDeductChujing());
//				hm.put("money", cj.getTuankeChujing());
//			} else if (lineType == LineType.guonei) {
//				HySupplierDeductGuonei gn = c.getHySupplierDeductGuonei();
//				hm.put("deductType", gn.getDeductGuonei());
//				hm.put("money", gn.getTuankeGuonei());
//			} else if (lineType == LineType.qiche) {
//				HySupplierDeductQiche qc = c.getHySupplierDeductQiche();
//				hm.put("deductType", qc.getDeductQiche());
//				hm.put("money", qc.getTuankeQiche());
//			}
//			
//			j.setSuccess(true);
//			j.setObj(hm);
//			j.setMsg("查看成功");
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			j.setSuccess(false);
//			j.setMsg(e.getMessage());
//		}
//		return j;
//	}
	
	/**
	 * 旅游元素供应商的下拉列表--渲染页面AJAX请求
	 * @param type 旅游元素供应商类型
	 * @return
	 */
	@RequestMapping(value="supplierElement/view")
	public Json supplierElement(SupplierType supplierType, Long groupId) {
		Json j = new Json();
		try {
			//新增
			HyGroup group = hyGroupService.find(groupId); //获取团
			
			HyLine line = group.getLine();
			
			List<Filter> filters = new ArrayList<>();
			
			List<Order> orders = new ArrayList<>();
			
			List<Map<String, Object>> lhm = new ArrayList<>(); 
			
			filters.add(Filter.eq("supplierType", supplierType));
			filters.add(Filter.eq("status", true));
			orders.add(Order.asc("id"));
			List<HySupplierElement> hySupplierElements = hySupplierElementService.findList(null, filters, orders);
			
			for(HySupplierElement h : hySupplierElements) {
				HashMap<String, Object> hm = new HashMap<>();
				hm.put("id", h.getId());
				hm.put("name", h.getName());
				
				
				//如果是地接需要加上合同和扣点
				if(supplierType == SupplierType.linelocal) {
					List<HashMap<String, Object>> contract = new ArrayList<>();
					if (h.getSupplierLine() == null) {
						throw new RuntimeException("地接供应商对应的线路不存在");
					}
				
					HySupplier s = hySupplierService.find(h.getSupplierLine());
					Set<HySupplierContract> cs = s.getHySupplierContracts();
					
					for(HySupplierContract a : cs) {
						HashMap<String, Object> cHm = new HashMap<>();
						if(a.getContractStatus() == ContractStatus.zhengchang) {
							cHm.put("id", a.getId());
							
							cHm.put("contractCode", a.getContractCode());														
							
							LineType lineType = line.getLineType();
							
							if(lineType == LineType.chujing) {
								HySupplierDeductChujing cj = a.getHySupplierDeductChujing();
								if(cj != null) {
									cHm.put("deductType", cj.getDeductChujing());
									if(cj.getDeductChujing() == DeductLine.rentou) {
										cHm.put("deduct", cj.getRentouChujing());
									} else {
										cHm.put("deduct", cj.getTuankeChujing());
									}
								}								
								
							} else if (lineType == LineType.guonei) {
								HySupplierDeductGuonei gn = a.getHySupplierDeductGuonei();
								if(gn != null) {
									cHm.put("deductType", gn.getDeductGuonei());
									if(gn.getDeductGuonei() == DeductLine.rentou) {
										cHm.put("deduct", gn.getRentouGuonei());
									} else {
										cHm.put("deduct", gn.getTuankeGuonei());
									}
								}
								
							} else if (lineType == LineType.qiche) {
								HySupplierDeductQiche qc = a.getHySupplierDeductQiche();
								if(qc != null) {
									cHm.put("deductType", qc.getDeductQiche());
									if(qc.getDeductQiche() == DeductLine.rentou) {
										cHm.put("deduct", qc.getRentouQiche());
									} else {
										cHm.put("deduct", qc.getTuankeQiche());
									}
								}								
							}
											
							contract.add(cHm);
						}
					}
					hm.put("contract", contract);
				}
				lhm.add(hm);
			}
			
			j.setSuccess(true);
			j.setObj(lhm);
			j.setMsg("查看成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
		

	
	/**
	 * 计算团款 --- 同时生成单团核算明细表里面的订单详情
	 * @param groupId
	 * @return
	 */
	public BigDecimal calculateGroup(Long groupId) {
		BigDecimal result = BigDecimal.ZERO;
		try {			
			HyGroup group = hyGroupService.find(groupId);
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("groupId", groupId));
			
			//只筛选订单状态为供应商通过的,added by GSbing,20190227
			filters.add(Filter.eq("status", 3));
			
			List<HyOrder> hyOrders = hyOrderService.findList(null, filters, null); 
			
			//删除团原来的报账订单
			filters.clear();
			filters.add(Filter.eq("hyGroup", group));
			List<HyRegulateitemOrder> hyRegulateitemOrders = hyRegulateitemOrderService.findList(null, filters, null);
			
			for(HyRegulateitemOrder temp : hyRegulateitemOrders) {
				hyRegulateitemOrderService.delete(temp);
			}
			
			for(HyOrder temp : hyOrders) {
				//added by GSbing,报账订单考虑门店退团的情况
				List<HyOrderItem> orderItems=temp.getOrderItems();
				Integer returnQuantity=0;
//				for(HyOrderItem item:orderItems) {
//					if(item!= null && item.getType() == 1)
//						returnQuantity=returnQuantity+item.getNumberOfReturn();
//				}
				
				BigDecimal tuikuanSum = temp.getJiesuanTuikuan().add(temp.getBaoxianJiesuanTuikuan());
				//只显示未完全退团的订单
				if(temp.getPeople() > 0 || tuikuanSum.compareTo(temp.getJiusuanMoney())<0) {
					HyRegulateitemOrder hyRegulateitemOrder = new HyRegulateitemOrder();
					hyRegulateitemOrder.setHyGroup(group);
					hyRegulateitemOrder.setHyOrder(temp);
					hyRegulateitemOrder.setOrderNumber(temp.getOrderNumber());
					hyRegulateitemOrder.setSource(temp.getSource());
					hyRegulateitemOrder.setNum(temp.getPeople()-returnQuantity);
					//结算价格都减去退款和返利以及优惠价格
					hyRegulateitemOrder.setMoney(temp.getJiesuanMoney1().subtract(temp.getJiesuanTuikuan())
							.subtract(temp.getStoreFanLi()).subtract(temp.getDiscountedPrice()));
					hyRegulateitemOrder.setYishou(temp.getJiesuanMoney1().subtract(temp.getJiesuanTuikuan())
							.subtract(temp.getStoreFanLi()).subtract(temp.getDiscountedPrice()));
						
					hyRegulateitemOrder.setYingshou(BigDecimal.ZERO);
					if(temp.getStoreId() != null) {
						String storeName = storeService.find(temp.getStoreId()).getStoreName();
						hyRegulateitemOrder.setMendianName(storeName);
					}
					hyRegulateitemOrderService.save(hyRegulateitemOrder);
						
					if(temp.getJiesuanMoney1() != null) {
						result = result.add(hyRegulateitemOrder.getMoney());
//						result = result.add(temp.getJiesuanMoney1()).add(temp.getAdjustMoney()).subtract(temp.getDiscountedPrice());
					}	
				}		
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 计算旅游元素收入/支出
	 * @param regulate
	 * @return
	 */
	public BigDecimal calculateElement(HyRegulate regulate, SupplierType type) {
		BigDecimal result = BigDecimal.ZERO;
		try {	
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("hyRegulate", regulate));
			filters.add(Filter.eq("type", type));
			List<HyRegulateitemElement> hyRegulateitemElements = hyRegulateitemElementService.findList(null, filters, null);
			
			for(HyRegulateitemElement temp : hyRegulateitemElements) {
				result = result.add(temp.getMoney()); //收入支出都按照money计算
		
			} 			
					
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 计算导游费用
	 * @param regulate
	 * @return
	 */
	public BigDecimal calculateGuide(HyRegulate regulate) {
		BigDecimal result = BigDecimal.ZERO;
		try {	
			List<HyRegulateitemGuide> guides = regulate.getHyRegulateitemGuides();
			
			for(HyRegulateitemGuide temp : guides) {
				result = result.add(temp.getYingfu());
			} 			
					
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 提交报账审核 --- 提交时生成1、单团核算表 2、单团核算明细表 3、导游报账表 
	 * @param regulateId 报账的ID
	 * @return
	 */
	@RequestMapping(value="submit")
	public Json submit(Long regulateId, HttpSession session) {
		Json j = new Json();
		try {
			List<Filter> filters = new ArrayList<>();
			List<Order> orders = new ArrayList<>();	
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			if(regulateId == null) {
				throw new RuntimeException("参数错误");
			}
			HashMap<String, Object> map = new HashMap<>(); //存放流程变量，是哪个部门的审核应该
			
			//added by GSbing,20190307,更改流程，增加产品总（分）公司中心经理限额审核
			HyAdmin hyAdmin=hyAdminService.find(username);
			//获取登录账号角色
			HyRole hyRole=hyAdmin.getRole();
			String roleName=hyRole.getName(); //获取角色名
					
			/****************************************/
			
			
				
			HyRegulate regulate = hyRegulateService.find(regulateId);
			
			Long groupId = regulate.getHyGroup();
			
			HyGroup group = hyGroupService.find(groupId);
			
			if(group == null) {
				throw new RuntimeException("团" + groupId + "不存在");
			}
			
			HyLine line = group.getLine();
			
			
			/******************************** 生成单团核算表 ******************************/	
			filters.clear();
			filters.add(Filter.eq("groupId", groupId));	
			filters.add(Filter.eq("status",1));
			orders.clear();
			orders.add(Order.asc("id"));
			List<GuideAssignment> guideAssignments = guideAssignmentService.findList(null, filters, orders); //导游派遣数组
			Set<Guide> baozhangGuides = new HashSet<>(); //本团所有被派遣的导游
			String guideName = "";
			
			for(GuideAssignment a : guideAssignments) {
				baozhangGuides.add(guideService.find(a.getGuideId()));
			}
			
			for(Guide temp : baozhangGuides) {
				String gn = temp.getName();
				guideName = guideName + gn + ",";
			}
			
			if(baozhangGuides.size() > 0) {
				guideName = guideName.substring(0, guideName.length() - 1); //导游合起来的姓名
			}		
			//重新提交以后相当于编辑，将原来的单团核算记录删除，重新用新的
			filters.clear();
			filters.add(Filter.eq("groupId", groupId));
			List<RegulategroupAccount> regulategroupAccounts = regulategroupAccountService.findList(null, filters, null);
			if(!regulategroupAccounts.isEmpty()) {
				RegulategroupAccount temp = regulategroupAccounts.get(0);
				regulategroupAccountService.delete(temp);				
			}
			
			RegulategroupAccount account = new RegulategroupAccount();
			account.setRegulateId(regulate);
			account.setGroupId(group);
			account.setLineSn(line.getPn());
			account.setLineName(line.getName());
			account.setDays(line.getDays());
			account.setStartDate(group.getStartDay());
			account.setEndDate(group.getEndDay());
			account.setOperatorName(line.getOperator().getName());
			account.setVisitorNo(regulate.getVisitorNum());
			account.setGuide(guideName);
			
			//计算团款
			BigDecimal groupMoney = calculateGroup(groupId);
			account.setGroupMoney(groupMoney);
			
			//计算购物收入	
			BigDecimal shopping = calculateElement(regulate, SupplierType.shopping);
			account.setShopping(shopping);
			
			//计算自费
			BigDecimal selfExpense = calculateElement(regulate, SupplierType.selfpay);
			account.setSelfExpense(selfExpense);
			
			//计算其他收入
			BigDecimal incomes = calculateElement(regulate, SupplierType.otherincome);
			account.setIncomes(incomes);
			
			//计算导游支出
			account.setGuidePrice(calculateGuide(regulate));
			
			//计算用餐支出
			account.setRestaurant(calculateElement(regulate, SupplierType.catering));
			
			//计算车辆支出
			account.setVehicle(calculateElement(regulate, SupplierType.car));
			
			//计算大交通支出
			account.setTraffic(calculateElement(regulate, SupplierType.traffic));
			
			//计算住宿支出
			account.setHotel(calculateElement(regulate, SupplierType.hotel));
			
			//计算保险支出
			account.setInsurance(calculateElement(regulate, SupplierType.insurance));
			
			//计算电子券支出
			account.setCoupon(calculateElement(regulate, SupplierType.coupon));
			
			//计算其他支出
			account.setOutgoings(calculateElement(regulate, SupplierType.otherexpend));
			
			//计算线路地接支出
			account.setDijie(calculateElement(regulate, SupplierType.linelocal));
			
			//计算门票支出
			account.setTicket(calculateElement(regulate, SupplierType.ticket));
			
			//计算总收入
			account.setAllIncome(groupMoney.add(shopping).add(selfExpense).add(incomes));
			
			//计算总支出
			account.setAllExpense(account.getGuidePrice().add(account.getRestaurant()).
								  add(account.getVehicle()).add(account.getTraffic()).
								  add(account.getHotel()).add(account.getOutgoings()).
								  add(account.getInsurance()).add(account.getDijie()).
								  add(account.getCoupon()).add(account.getTicket()));
			
			//计算单团利润
			account.setProfit(account.getAllIncome().subtract(account.getAllExpense()));
			
			//计算人均利润
			if(regulate.getVisitorNum() == 0) {
				account.setAverageProfit(null);
			} else {
				account.setAverageProfit(account.getProfit().divide(BigDecimal.valueOf(regulate.getVisitorNum()), 10, RoundingMode.HALF_DOWN));
			}			
			regulategroupAccountService.save(account); //保存单团核算表
			
			/*更新单团利润表，add by liyang 20190519 ?提交审核的时候更新的话被驳回怎么办？*/
			finishedGroupItemOrderService.updateGroupItemOrder(account, groupId);
			
			regulate.setDantuanhesuanbiaoId(account.getId());
			hyRegulateService.update(regulate);
			
			
//			Long fengongsiId = checkIfZonggongsi(username); //该员工分公司Id
//			if(fengongsiId.equals(-1L)) { //如果是总公司 --找到总公司的产品中心部门 --- 只有一个
//				HyDepartmentModel model = hyDepartmentModelService.find("总公司产品研发中心");
//				List<Department> departments = new ArrayList<>(model.getHyDepartments());
//				map.put("department", departments.get(0));
//			} else { //如果是分公司 --- 找到和提交人相同分公司的产品中心
//				HyDepartmentModel model = hyDepartmentModelService.find("分公司产品中心");
//				Set<Department> departments = model.getHyDepartments();
//				for(Department temp : departments) {
//					List<Long> list = temp.getTreePaths();
//					if(list.size() > 1) {
//						Department department = hyDepartmentService.find(list.get(1));
//						if(department.getId().equals(fengongsiId)) {
//							map.put("department", temp);
//							break;
//						}
//					}							
//				}
//			}
			Department department=hyAdmin.getDepartment(); //找到登录账号的部门
			map.put("department",department); //将部门参数传递进工作流
			
			/**增加产品中心经理限额审核的条件,added by GSbing,20190307*/
			//判断当前登录角色是否是经理
			//如果是经理，跳过产品中心部门经理审核
			if(roleName.contains("经理")) {
				map.put("ifjingli","true");
				List<Filter> filters2 = new ArrayList<>();
				filters2.add(Filter.eq("eduleixing", Eduleixing.regulateJingli));
				List<CommonShenheedu> edu1 = commonEdushenheService.findList(null, filters2, null);
				/**获取产品中心经理审核额度*/
				BigDecimal money1 = edu1.get(0).getMoney(); 
				
				filters2.clear();
				filters.add(Filter.eq("eduleixing", Eduleixing.regulateLimit));
				List<CommonShenheedu> edu2 = commonEdushenheService.findList(null, filters2, null);
				/**获取副总审核额度*/
				BigDecimal money2 = edu2.get(0).getMoney(); 
				if(account.getAllIncome().compareTo(money1)>0) {//超过产品中心经理限额额度
					map.put("money1","more");
				}
				else {
					map.put("money1","less");
					if(account.getAllIncome().compareTo(money2)>0) {//超过副总审核限额
						map.put("money2","more");
					}
					else {
						map.put("money2","less");
					}
				}
			}
			//如果当前登录角色不是经理,则要经过产品中心部门经理审核
			else {
				map.put("ifjingli","false");
			}
			
			
			/********************************驳回以后在原来基础上提交******************************/
			if(regulate.getStatus() == 3) { //如果驳回，在原来基础上提交
				Task task = taskService.createTaskQuery().processInstanceId(regulate.getProcessInstanceId()).singleResult();
				regulate.setStatus(1);
				regulate.setApplyTime(new Date());
				regulate.setApplyName(username);

				hyRegulateService.update(regulate);	
				Authentication.setAuthenticatedUserId(username);
				taskService.addComment(task.getId(), regulate.getProcessInstanceId(), " :1");
				taskService.complete(task.getId(), map);
			} else {
				ProcessInstance pi = runtimeService.startProcessInstanceByKey("regulateprocess");
				// 根据流程实例Id查询任务
				Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
				// 完成 

				regulate.setStatus(1);
				regulate.setApplyTime(new Date());
				regulate.setApplyName(username);
				regulate.setProcessInstanceId(pi.getProcessInstanceId());

				hyRegulateService.update(regulate);	
				Authentication.setAuthenticatedUserId(username);
				taskService.addComment(task.getId(), pi.getProcessInstanceId(), " :1");
				taskService.complete(task.getId(), map);
			}			
			/********************************       结束                    ******************************/
			
			
			/******************************** 生成单团核算明细表 ******************************/	
			//其他收入不在付尾款表中记录
			
			//先删除旧的付尾款数据和报账订单
			filters.clear();
			filters.add(Filter.eq("hyGroup", group));
			List<HyPayablesElement> payableElements = hyPayablesElementService.findList(null, filters, null);
			for(HyPayablesElement temp : payableElements) {
				hyPayablesElementService.delete(temp);
			}									
			
			
			filters.clear();
			filters.add(Filter.eq("hyGroup", group));
			filters.add(Filter.eq("isShouru", false)); //找出支出类的旅游元素供应商
			List<HyRegulateitemElement> es = hyRegulateitemElementService.findList(null, filters, null);
			Set<HySupplierElement> supplierElements = new HashSet<>();
			for(HyRegulateitemElement temp : es) {
				supplierElements.add(temp.getHySupplierElement());
			}
			
			//生成支出类旅游元素应付款记录
			List<HyPayablesElement> payables = new ArrayList<>();
			for(HySupplierElement temp : supplierElements) {
				HyPayablesElement payable = new HyPayablesElement();
				payable.setHyRegulate(regulate);
				payable.setHyGroup(group);
				payable.setOperator(line.getOperator());
				payable.setType(temp.getSupplierType());
				payable.setHySupplierElement(temp);
				payable.setSupplierName(temp.getName());
				payable.setMoney(BigDecimal.ZERO);
				payable.setPayBaozhang(BigDecimal.ZERO);
				payable.setPay(BigDecimal.ZERO);
				payable.setPaid(BigDecimal.ZERO);
				payable.setDebt(BigDecimal.ZERO);
				payable.setBaozhangPaid(BigDecimal.ZERO);
				payable.setIsShouru(temp.getIsShouru());
				hyPayablesElementService.save(payable);		
				payables.add(payable);
			}
			
			//找出该团的支出类旅游元素应付款记录
			for(HyRegulateitemElement temp : es) {
				HySupplierElement ele = temp.getHySupplierElement();
				for(HyPayablesElement inner : payables) {
					HySupplierElement eleinner = inner.getHySupplierElement();
					if(ele.getId() == eleinner.getId()) {
						inner.setMoney(inner.getMoney().add(temp.getMoney()));
						if(ele.getSupplierType() != SupplierType.linelocal) { //如果非地接
							inner.setPay(inner.getPay().add(temp.getRealMoney()));
						} else { //如果地接
							inner.setContractId(temp.getSupplierContractId());
							inner.setPayBaozhang(inner.getPayBaozhang().add(temp.getRealMoney()));
							inner.setKoudianType(temp.getDeductType());
							if(temp.getDeductType() == DeductLine.rentou) {
								inner.setKoudianRentou(temp.getDeduct());								
							} else {
								inner.setKoudianTuanke(temp.getDeduct());
								
							}
						}
						hyPayablesElementService.update(inner);
						break;
					}
				}
			}
			
			//设置每个供应商的已付初始化
			for(HyPayablesElement inner : payables) {
				HySupplierElement eleinner = inner.getHySupplierElement();
				
				if(eleinner.getSupplierType() == SupplierType.linelocal) { //如果是地接 	
					
					if(inner.getKoudianType() == DeductLine.rentou) { //人头扣点
						BigDecimal koudian = inner.getKoudianRentou().multiply(BigDecimal.valueOf(regulate.getVisitorNum()));//或者乘以regulateItemElement.getNum
						inner.setKoudian(koudian);
						inner.setPay(inner.getPayBaozhang().subtract(koudian)); //地接的pay为报账减去扣点
					} else if (inner.getKoudianType() == DeductLine.tuanke) { //团客扣点
						BigDecimal koudian = inner.getMoney().multiply(inner.getKoudianTuanke()).divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_DOWN);
						inner.setKoudian(koudian);
						inner.setPay(inner.getPayBaozhang().subtract(koudian));
					}											
				}
				inner.setPaid(BigDecimal.ZERO); //设置已付
				inner.setBaozhangPaid(inner.getMoney().subtract(inner.getPay()));
				inner.setDebt(inner.getPay());
				hyPayablesElementService.update(inner);
			}
					
			/******************************** 生成导游报账表 ******************************/	
			//生成各自的导游报账表
			
			//首先删除相应的导游报账表
			filters.clear();
			filters.add(Filter.eq("groupId", group));
			List<RegulateGuide> regulateGuides = regulateGuideService.findList(null, filters, null);
			for(RegulateGuide temp : regulateGuides) {
				regulateGuideService.delete(temp);
			}
			
			for(Guide guide : baozhangGuides) {
				
				BigDecimal sr = BigDecimal.ZERO;
				BigDecimal zc = BigDecimal.ZERO;	
				
				//根据导游和团找到该导游的报账
				filters.clear();
				filters.add(Filter.eq("hyGroup", group));
				filters.add(Filter.eq("guide", guide));

				List<HyRegulateitemElement> res = hyRegulateitemElementService.findList(null, filters, null);
				for(HyRegulateitemElement hyRegulateitemElement : res) {
					if(hyRegulateitemElement.getIsShouru()) { //收入类为交回金额相加				
						sr = sr.add(hyRegulateitemElement.getMoney());
					} else { //支出类为垫付金额相加
						if(hyRegulateitemElement.getIsDianfu()) { //如果发生了垫付
							zc = zc.add(hyRegulateitemElement.getDianfu());
						}						
					}
				}
				
				//保存到对应的表中
				RegulateGuide regulateGuide = new RegulateGuide();
				regulateGuide.setGroupId(group);
				regulateGuide.setRegulateId(regulate);
				regulateGuide.setGuideId(guide);
				regulateGuide.setGuideName(guide.getName());
				regulateGuide.setShouru(sr);
				regulateGuide.setZhichu(zc);
				regulateGuide.setBaozhang(zc.subtract(sr));
				regulateGuide.setgId(guide.getId());
				regulateGuideService.save(regulateGuide);								
			}
			
			
			j.setMsg("提交审核成功");
			j.setSuccess(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
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
		if(list.size() > 1) {
			Department department = hyDepartmentService.find(list.get(1));
			HyDepartmentModel model = department.getHyDepartmentModel();
			if(model.getName().equals("分公司")) {
				return department.getId();
			}
		}		
		return -1L;
	}

	/**
	 * 查看单团核算表
	 * @param regulateId
	 * @return
	 */
	@RequestMapping(value="dantuanhesuan/view")
	public Json dantuanhesuan(Long regulateId) {
		Json j = new Json();
		try {	
			RegulategroupAccount account = null;
			
			HyRegulate regulate = hyRegulateService.find(regulateId);
			
			if(regulate.getDantuanhesuanbiaoId() != null) { //如果还没有提交过就没有单团核算表生成
				account = regulategroupAccountService.find(regulate.getDantuanhesuanbiaoId());
			} else {
				j.setSuccess(true);
				j.setMsg("需要提交审核才可以查看单团核算表");
				return j;
			}
	
			j.setSuccess(true);
			j.setMsg("查看单团核算表成功");
			j.setObj(account);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	/**
	 * 查看单团核算明细表
	 * @param regulateId
	 * @return
	 */
	@RequestMapping(value="mingxi/view")
	public Json mingxibiao(Long regulateId) {
		Json j = new Json();
		try {
			List<Filter> filters = new ArrayList<>();
			
			HyRegulate regulate = hyRegulateService.find(regulateId);
			
			Long groupId = regulate.getHyGroup();
			
			HyGroup group = hyGroupService.find(groupId);
			
			RegulategroupAccount account = regulategroupAccountService.find(regulate.getDantuanhesuanbiaoId());
			
			if(account == null) {
				j.setSuccess(true);
				j.setMsg("需要提交审核才可生成明细表");
				return j;
			}
			
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
			
			j.setMsg("查看单团核算明细表成功");
			j.setObj(obj);
			j.setSuccess(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	@RequestMapping(value="daoyoubaozhang/view")
	public Json daoyoubaozhang(Long regulateId) {
		Json j = new Json();
		try {
			List<Filter> filters = new ArrayList<>();
			
			HyRegulate regulate = hyRegulateService.find(regulateId);
			
			Long groupId = regulate.getHyGroup();
			
			HyGroup group = hyGroupService.find(groupId);
			
			RegulategroupAccount account = regulategroupAccountService.find(regulate.getDantuanhesuanbiaoId());
			
			if(account == null) {
				j.setSuccess(true);
				j.setMsg("需要提交审核才可生成导游报账表");
				return j;
			}
			
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
			
			//导游报账收入总表-------------------------------------------------
			//收入明细
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
				if(temp != null && ele != null) {
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
			
			obj.put("shourumingxi", shourumingxi);
			obj.put("zhichumingxi", zhichumingxi);
			obj.put("zongshouru", zongshouru);
			obj.put("zongzhichu", zongzhichu);
			obj.put("baozhang", zongzhichu.subtract(zongshouru));
			
			//各个导游报账表
			filters.clear();
			filters.add(Filter.eq("groupId", group));
			List<RegulateGuide> regulateGuides = regulateGuideService.findList(null, filters, null);
			obj.put("fenbiao", regulateGuides);
			
			j.setObj(obj);
			j.setMsg("查看导游报账表成功");
			j.setSuccess(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	@RequestMapping(value="")
	public Json list(Pageable pageable) {
		Json j = new Json();
		try {
			
			
			j.setSuccess(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
}
