package com.hongyu.controller.hzj03.balancedue;

import com.hongyu.*;
import com.hongyu.Filter.Operator;
import com.hongyu.entity.*;
import com.hongyu.entity.HyLine.RefundTypeEnum;
import com.hongyu.service.*;
import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Comment;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author xyy
 * 旅游元素供应商尾款 */
@Controller
@RequestMapping("/admin/balanceDue")
public class BalanceDueController {
	@Resource(name = "branchBalanceServiceImpl")
	private BranchBalanceService branchBalanceService;
	
	@Resource(name = "balanceDueApplyItemServiceImpl")
    private BalanceDueApplyItemService balanceDueApplyItemService;

	@Resource(name = "balanceDueApplyServiceImpl")
    private BalanceDueApplyService balanceDueApplyService;

	@Resource(name = "branchPrePayServiceImpl")
    private BranchPrePayService branchPrePayService;

	@Resource(name = "hyAdminServiceImpl")
    private HyAdminService hyAdminService;

	@Resource(name = "hyGroupServiceImpl")
    private HyGroupService hyGroupService;
	
	@Resource(name = "hyLineServiceImpl")
    private HyLineService hyLineService;

	@Resource(name = "hyPayablesElementServiceImpl")
    private HyPayablesElementService hyPayablesElementService;

	@Resource(name = "hySupplierElementServiceImpl")
    private HySupplierElementService hySupplierElementService;

	@Resource(name = "hyRegulateServiceImpl")
	private HyRegulateService hyRegulateService;

	@Resource
	private TaskService taskService;

    @Resource
    private HistoryService historyService;

	/** 旅游元素供应商 - 列表(按单位未付  按团未付  按团已付) */
	@RequestMapping("/list/view")
	@ResponseBody
	public Json getList(Pageable pageable, Integer status, String name, String startDate, String endDate, String sn,
			Integer supplierType, HttpSession session) {
		Json json = new Json();

		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin hyAdmin = hyAdminService.find(username);
		try {
		    if(hyAdmin == null){
		        throw new Exception("username为空");
            }
            HashMap<String, Object> obj = hyPayablesElementService.getList(pageable, status, name, startDate, endDate, sn, supplierType, hyAdmin);
			json.setObj(obj);
			json.setSuccess(true);
		} catch (Exception e) {
		    e.printStackTrace();
			json.setSuccess(false);
			json.setMsg("操作失败");
		}
		return json;
	}

	/** 旅游元素供应商 - 按单位付款详情、申请付款详情 */
	@RequestMapping("/detail/supplier/view")
	@ResponseBody
	public Json getDetailBySupplier(Long id, HttpSession session) {
		Json json = new Json();
		HashMap<String, Object> obj = new HashMap<>();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);

			HySupplierElement hySupplierElement = hySupplierElementService.find(id);

			// 预付款信息
			obj.put("supplierName", hySupplierElement.getName());

			Department department = admin.getDepartment();
			obj.put("department", department.getFullName().replace("总公司", "虹宇"));

			// 从BranchPrePay中获取 部门在某个供应商处预付款余额
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("departmentId", department.getId()));
			filters.add(Filter.eq("supplierElementId", hySupplierElement.getId()));
			List<BranchPrePay> branchPrePayList = branchPrePayService.findList(null, filters, null);
			
			if(branchPrePayList == null || branchPrePayList.isEmpty()){
				obj.put("prePayBalance", 0);
			}else{
				BranchPrePay branchPrePay = branchPrePayList.get(0);
				obj.put("prePayBalance", branchPrePay.getPrePayBalance());
			}
			
			// 分公司的充值余额
			String[] strings = department.getTreePath().split(",");
			List<Filter> f = new ArrayList<>();
			f.add(Filter.eq("branchId", Long.parseLong(strings[2])));
			List<BranchBalance> branchBalanceList =  branchBalanceService.findList(null, f, null);
			if(CollectionUtils.isEmpty(branchBalanceList)){
				// 总公司无需考虑充值余额
				if(department.getHyDepartmentModel().getName().startsWith("总公司")){
					obj.put("transferBalance", -1);
				}else{
					obj.put("transferBalance", 0);
				}
			}else{
				obj.put("transferBalance", branchBalanceList.get(0).getBranchBalance());
			}
			

			// 供应商信息
			obj.put("contact", hySupplierElement.getOperator().getName()); // 旅游元素供应商联系人
			obj.put("supplierType", hySupplierElement.getSupplierType());
			obj.put("bankList", hySupplierElement.getBankList());

			// 组团信息
			filters.clear();
			List<Order> orders = new LinkedList<>();
			orders.add(Order.desc("hyGroup"));
			filters.add(Filter.eq("hySupplierElement", hySupplierElement));
			filters.add(Filter.gt("debt", 0)); // 筛选欠付大于0
			filters.add(Filter.eq("operator", admin)); // 只看自己建的团

			// 筛选计调报账审核已经通过的
			List<Filter> filters_hyRegulate = new ArrayList<>();
			filters_hyRegulate.add(Filter.eq("status", 2));
			List<HyRegulate> hyRegulates = hyRegulateService.findList(null, filters_hyRegulate, null);
			if(hyRegulates!=null && !hyRegulates.isEmpty()) {

				filters.add(Filter.in("hyRegulate", hyRegulates));
			}

			List<HyPayablesElement> payablesElements = hyPayablesElementService.findList(null, filters, orders);

			List<HashMap<String, Object>> list = new LinkedList<>();
			BigDecimal ownTotal = new BigDecimal(0.00);
			for (HyPayablesElement tmp : payablesElements) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("id", tmp.getId());
				map.put("groupId", tmp.getHyGroup().getId());
				map.put("sn", tmp.getHyGroup().getGroupLinePn());
				map.put("launchDate", tmp.getHyGroup().getStartDay());
				map.put("lineName", tmp.getHyGroup().getGroupLineName());
				map.put("operator", tmp.getHyGroup().getOperatorName());
				map.put("amount", tmp.getHyGroup().getSignupNumber());
				map.put("shouldPay", tmp.getPay());
				map.put("hasPaid", tmp.getPaid());
				map.put("own", tmp.getDebt());
				map.put("money", tmp.getMoney());
				map.put("deductionPoint", tmp.getKoudian());
				list.add(map);
				ownTotal = ownTotal.add(tmp.getDebt());
			}

			obj.put("groupInfo", list);
			obj.put("ownTotal", ownTotal);

			json.setObj(obj);
			json.setSuccess(true);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("操作失败");
		}

		return json;
	}

	/** 旅游元素供应商 - 按团付款详情、申请付款详情 */
	@RequestMapping("/detail/group/view")
	@ResponseBody
	public Json getDetailByGroup(Long id, HttpSession session) {
		Json json = new Json();
		HashMap<String, Object> obj = new HashMap<>();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);

			HyPayablesElement hyPayablesElement = hyPayablesElementService.find(id);
			HySupplierElement hySupplierElement = hyPayablesElement.getHySupplierElement();
			Department department = admin.getDepartment();
			obj.put("department", department.getFullName().replace("总公司", "虹宇"));

			// 从BranchPrePay中获取 部门在某个供应商处预付款余额
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("departmentId", department.getId()));
			filters.add(Filter.eq("supplierElementId", hySupplierElement.getId()));
			List<BranchPrePay> branchPrePayList = branchPrePayService.findList(null, filters, null);
			
			if(branchPrePayList == null || branchPrePayList.isEmpty()){
				obj.put("prePayBalance", 0);
			}else{
				BranchPrePay branchPrePay = branchPrePayList.get(0);
				obj.put("prePayBalance", branchPrePay.getPrePayBalance());
			}
			
			// 分公司的充值余额
			String[] strings = department.getTreePath().split(",");
			List<Filter> f = new ArrayList<>();
			f.add(Filter.eq("branchId", Long.parseLong(strings[2])));
			List<BranchBalance> branchBalanceList = branchBalanceService.findList(null, f, null);
			if (CollectionUtils.isEmpty(branchBalanceList)) {
				// 总公司无需考虑充值余额
				if(department.getHyDepartmentModel().getName().startsWith("总公司")){
					obj.put("transferBalance", -1);
				}else{
					obj.put("transferBalance", 0);
				}
			} else {
				obj.put("transferBalance", branchBalanceList.get(0).getBranchBalance());
			}
			
			// 供应商信息
			obj.put("contact", hySupplierElement.getOperator().getName()); // 旅游元素供应商联系人
			obj.put("supplierType", hySupplierElement.getSupplierType());
			obj.put("bankList", hySupplierElement.getBankList());

			// 组团信息
			List<HashMap<String, Object>> list = new LinkedList<>();
			HashMap<String, Object> map = new HashMap<>();
			map.put("id", hyPayablesElement.getId());
			map.put("groupId", hyPayablesElement.getHyGroup().getId());
			map.put("sn", hyPayablesElement.getHyGroup().getGroupLinePn());
			map.put("launchDate", hyPayablesElement.getHyGroup().getStartDay());
			map.put("lineName", hyPayablesElement.getHyGroup().getGroupLineName());
			map.put("operator", hyPayablesElement.getHyGroup().getOperatorName());
			map.put("amount", hyPayablesElement.getHyGroup().getSignupNumber());
			map.put("shouldPay", hyPayablesElement.getPay());
			map.put("hasPaid", hyPayablesElement.getPaid());
			map.put("own", hyPayablesElement.getDebt());
			map.put("money", hyPayablesElement.getMoney());
			map.put("deductionPoint", hyPayablesElement.getKoudian());
			list.add(map);

			obj.put("groupInfo", list);
			obj.put("ownTotal", hyPayablesElement.getDebt());

			json.setObj(obj);
			json.setSuccess(true);
		} catch (Exception e) {
		    e.printStackTrace();
			json.setSuccess(false);
			json.setMsg("操作失败");
		}
		return json;
	}

	/** 旅游元素供应商 - 提交申请 */
	@RequestMapping("/submit")
	@ResponseBody
	public Json submitAudit(@RequestBody Wrap wrap, HttpSession session) {
		Json json = new Json();

		try {
			List<HashMap<String, Object>> list2 = new ArrayList<>();
			List<ElementsPay> list = wrap.getList();

			for (ElementsPay e : list) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("id", e.getId());
				map.put("prePayMoney", e.getPrePayMoney());
				map.put("transferMoney", e.getTransferMoney());
				list2.add(map);
			}

			json = balanceDueApplyService.addApply(list2, session);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("操作失败");
			e.printStackTrace();
		}

		return json;
	}

	/** 旅游元素供应商 - 审核 - 列表 */
	@RequestMapping("/audit/list/view")
	@ResponseBody
	public Json getAuditList(Pageable pageable, Integer state, String supplierName, String startDate, String endDate,
			HttpSession session) {
		Json json = new Json();

		try {
			// 倒序
			List<Order> orders = new LinkedList<>();
			orders.add(Order.desc("id"));
			pageable.setOrders(orders);

			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin = hyAdminService.find(username);

			List<Filter> filters = new LinkedList<>();
			filters.add(Filter.eq("operator", hyAdmin));

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if (StringUtils.isNotBlank(startDate)){
				filters.add(new Filter("createTime", Operator.ge, sdf.parse(startDate.substring(0, 10) + " " + "00:00:00")));
			}
			if (StringUtils.isNotBlank(endDate)){
				filters.add(new Filter("createTime", Operator.le, sdf.parse(endDate.substring(0, 10) + " " + "23:59:59")));
			}

			if (state != null) {
				if (state == 0) { // 审核中
					filters.add(Filter.eq("status", 1));
				} else if (state == 1) { // 已审核
					filters.add(Filter.gt("status", 1));
					filters.add(Filter.lt("status", 4));
				} else if (state == 2) { // 已驳回
					filters.add(Filter.eq("status", 4));
				}
			}

			// 根据名称筛选供应商
			List<Filter> f = new LinkedList<>();
			f.add(Filter.like("name", supplierName));
			List<HySupplierElement> elements = hySupplierElementService.findList(null, f, null);
			if(elements!=null && !elements.isEmpty()) {
				
				filters.add(Filter.in("supplierElement", elements));
			}

			pageable.setFilters(filters);
			Page<BalanceDueApply> page = balanceDueApplyService.findPage(pageable);

			List<HashMap<String, Object>> list = new LinkedList<>();
			for (BalanceDueApply bd : page.getRows()) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("id", bd.getId());
				map.put("supplierName", bd.getSupplierElement().getName());
				map.put("supplierType", bd.getSupplierElement().getSupplierType());
				map.put("money", bd.getMoney());
				map.put("applier", bd.getOperator().getName());
				map.put("applyDate", bd.getCreateTime());
				map.put("payDate",bd.getPayDate());

				Integer status = null;
				if (bd.getStatus() == 1) {
					status = 0;
				} else if (bd.getStatus() == 2 || bd.getStatus() == 3) {
					status = 1;
				} else if (bd.getStatus() == 4) {
					status = 2;
				}
				map.put("status", status);
				list.add(map);
			}
			json.setObj(list);
			json.setSuccess(true);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("操作失败");
		}

		return json;
	}

	/** 旅游元素供应商 - 审核 - 详情 */
	@RequestMapping("/audit/detail/view")
	@ResponseBody
	public Json getAuditDetail(Long id) {
		Json json = new Json();
		HashMap<String, Object> obj = new HashMap<>();
		try {
			BalanceDueApply balanceDueApply = balanceDueApplyService.find(id);

			// 审核步骤
			String processInstanceId = balanceDueApply.getProcessInstanceId();
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
			obj.put("supplierName", balanceDueApply.getSupplierElement().getName());
			obj.put("bankList", balanceDueApply.getSupplierElement().getBankList());
			obj.put("contact", balanceDueApply.getOperator().getName());
			obj.put("supplierType", balanceDueApply.getSupplierElement().getSupplierType());
			obj.put("money", balanceDueApply.getMoney());
			obj.put("applyDate", balanceDueApply.getCreateTime());

			// 付款信息
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("balanceDueApplyId", id));
			List<BalanceDueApplyItem> bDueApplyItems = balanceDueApplyItemService.findList(null, filters, null);
			
			List<HashMap<String, Object>> items = new ArrayList<>();
			for(BalanceDueApplyItem b : bDueApplyItems){
				HashMap<String, Object> map = new HashMap<>();
				HyGroup hyGroup = hyGroupService.find(b.getGroupId());
				map.put("sn", hyGroup.getGroupLinePn());
				map.put("launchDate", b.getLaunchDate());
				map.put("lineName", b.getLineName());
				map.put("creator", hyGroup.getCreator().getName());
				map.put("amount", b.getAmount());
				map.put("usePrePay", b.getUsePrePay());
				map.put("transferMoney", b.getPayMoney());
				map.put("applier", b.getApplier().getName());
				map.put("applyDate", b.getCreateTime());
				
				items.add(map);
			}
			
			
			obj.put("groupInfo", items);

			json.setObj(obj);
			json.setSuccess(true);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("操作失败");
		}

		return json;
	}

	/** 旅游元素供应商- 按团 -已付款-详情**/
	@RequestMapping("/detail/view")
	@ResponseBody
	public Json getPaidDetailByGroup(Long id,HttpSession session){   //这个id是payable element的id
		Json json = new Json();
		HashMap<String, Object> obj = new HashMap<>();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);

			HyPayablesElement hyPayablesElement = hyPayablesElementService.find(id);
			HySupplierElement hySupplierElement = hyPayablesElement.getHySupplierElement();
			Department department = admin.getDepartment();
			obj.put("department", department.getFullName().replace("总公司", "虹宇"));

			// 供应商信息
			obj.put("contact", hySupplierElement.getOperator().getName()); // 旅游元素供应商联系人
			obj.put("supplierType", hySupplierElement.getSupplierType());
			obj.put("bankList", hySupplierElement.getBankList());
			obj.put("supplierName", hySupplierElement.getName());

			// 组团信息
			List<HashMap<String, Object>> list = new LinkedList<>();
			HashMap<String, Object> map = new HashMap<>();
			map.put("id", hyPayablesElement.getId());
			map.put("groupId", hyPayablesElement.getHyGroup().getId());
			map.put("sn", hyPayablesElement.getHyGroup().getGroupLinePn());
			map.put("launchDate", hyPayablesElement.getHyGroup().getStartDay());
			map.put("lineName", hyPayablesElement.getHyGroup().getGroupLineName());
			map.put("operator", hyPayablesElement.getHyGroup().getOperatorName());
			map.put("amount", hyPayablesElement.getHyGroup().getSignupNumber());
			map.put("shouldPay", hyPayablesElement.getPay());
			map.put("hasPaid", hyPayablesElement.getPaid());
			map.put("own", hyPayablesElement.getDebt());
			map.put("money", hyPayablesElement.getMoney());
			map.put("deductionPoint", hyPayablesElement.getKoudian());
			list.add(map);

			obj.put("groupInfo", list);
			obj.put("ownTotal", hyPayablesElement.getDebt());
			
			//付款信息
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("hyPayablesElementId",id));
			List<BalanceDueApplyItem> balanceDueApplyItems = balanceDueApplyItemService.findList(null,filters,null);
			List<HashMap<String, Object>> items = new ArrayList<>();
			for(BalanceDueApplyItem b:balanceDueApplyItems){
				BalanceDueApply balanceDueApply = balanceDueApplyService.find(b.getBalanceDueApplyId());
				HashMap<String, Object> map2 = new HashMap<>();
				HyGroup hyGroup = hyGroupService.find(b.getGroupId());
				map2.put("sn", hyGroup.getGroupLinePn());
				map2.put("launchDate", b.getLaunchDate());
				map2.put("lineName", b.getLineName());
				map2.put("creator", hyGroup.getCreator().getName());
				map2.put("amount", b.getAmount());
				map2.put("usePrePay", b.getUsePrePay());
				map2.put("transferMoney", b.getPayMoney());
				map2.put("applier", b.getApplier().getName());
				map2.put("applyDate", b.getCreateTime());
				map2.put("payDate", balanceDueApply.getPayDate());

				items.add(map2);
			}
			obj.put("payInfo", items);

			json.setObj(obj);
			json.setMsg("获取成功");
			json.setSuccess(true);
		} catch (Exception e) {
			e.printStackTrace();
			json.setMsg("获取失败");
			json.setSuccess(false);
		}
		return json;
	}
	
	/** 内部类 接收提交付尾款申请时的数据 */
	static class ElementsPay {
		private Long id;
		private BigDecimal prePayMoney;
		private BigDecimal transferMoney;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public BigDecimal getPrePayMoney() {
			return prePayMoney;
		}

		public void setPrePayMoney(BigDecimal prePayMoney) {
			this.prePayMoney = prePayMoney;
		}

		public BigDecimal getTransferMoney() {
			return transferMoney;
		}

		public void setTransferMoney(BigDecimal transferMoney) {
			this.transferMoney = transferMoney;
		}

	}
	
	static class Wrap {
		private List<ElementsPay> list;

		public List<ElementsPay> getList() {
			return list;
		}

		public void setList(List<ElementsPay> list) {
			this.list = list;
		}
		
	}

	@RequestMapping("/product/view")
	@ResponseBody
	public Json productview(String pn)
	{
		Json json=new Json();
		try {
			List<Filter> filters=new ArrayList<Filter>();
			filters.add(Filter.eq("pn", pn));
			List<HyLine> hyLines=hyLineService.findList(null,filters,null);
			HyLine hyLine=hyLines.get(0);
			HashMap<String,Object> map=new HashMap<String,Object>();
			map.put("contractCode", hyLine.getContract().getContractCode());
			map.put("pn",hyLine.getPn()); //productID
			map.put("name", hyLine.getName()); //线路名称
			map.put("lineCategory",hyLine.getLineCategory().getName());
			map.put("area",hyLine.getArea().getFullName());
			map.put("isInsurance", hyLine.getIsInsurance());
			map.put("isHeadInsurance",hyLine.getIsHeadInsurance());
			if(hyLine.getIsInsurance()==true) {
				map.put("insuranceCode",hyLine.getInsurance().getInsuranceCode());
				map.put("insuranceRmark",hyLine.getInsurance().getRemark());
			}
			else {
				map.put("insuranceCode",null);
				map.put("insuranceRmark",null);
			}
			map.put("days",hyLine.getDays());
			map.put("refundType",hyLine.getRefundType());
			List<Map<String, Object>> lineInfos = new ArrayList<>();
			List<HyLineTravels> lineTravelList=hyLine.getLineTravels();
			if(lineTravelList.size()>0) {
				for(HyLineTravels hyLineTravels:lineTravelList) {
					HashMap<String,Object> lineMap=new HashMap<String,Object>();
					lineMap.put("name", hyLineTravels.getTransport().getName());
					lineMap.put("route",hyLineTravels.getRoute());
					lineMap.put("isBreakfast",hyLineTravels.getIsBreakfast());
					lineMap.put("isLunch", hyLineTravels.getIsLunch());
					lineMap.put("isDinner",hyLineTravels.getIsDinner());
					lineMap.put("restaurant",hyLineTravels.getRestaurant());
					lineInfos.add(lineMap);
				}
			}
			map.put("lineTravels",lineInfos);
			
			List<Map<String, Object>> refundInfos = new ArrayList<>();
			List<HyLineRefund> lineRefundList=hyLine.getLineRefunds();
			if(hyLine.getRefundType()==RefundTypeEnum.jieti) {			
				if(lineRefundList.size()>0) {
					for(HyLineRefund hyLineRefund:lineRefundList) {
						HashMap<String,Object> refundMap=new HashMap<String,Object>();
						refundMap.put("startDay", hyLineRefund.getStartDay());
						refundMap.put("startTime", hyLineRefund.getStartTime());
						refundMap.put("endDay",hyLineRefund.getEndDay());
						refundMap.put("endTime",hyLineRefund.getEndTime());
						refundMap.put("percentage",hyLineRefund.getPercentage());
						refundInfos.add(refundMap);
					}
				}
			}
			map.put("lineRefunds",refundInfos);
			map.put("cancelMemo",hyLine.getCancelMemo());
			map.put("memo",hyLine.getMemo());
			map.put("memoInner", hyLine.getMemoInner());
			map.put("introduction", hyLine.getIntroduction());
			json.setMsg("获取成功");
			json.setSuccess(true);
			json.setObj(map);
		}
		catch(Exception e) {
			e.printStackTrace();
			json.setMsg("获取失败");
			json.setSuccess(false);
		}
		return json;
	}
}
