package com.hongyu.controller.hzj03.incomeandexpenses.branch;

import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import com.hongyu.entity.*;
import com.hongyu.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.controller.BaseController;

/**
 * 付款-待付款-分公司
 */
@Controller
@RequestMapping("/admin/unpaid/branch")
public class UnPaidBranchController extends BaseController {
	@Resource(name = "hyAddedServiceSupplierServiceImpl")
	HyAddedServiceSupplierService hyAddedServiceSupplierService;
	
	@Resource(name = "branchPayServicerServiceImpl")
	BranchPayServicerService branchPayServicerService;
	
	@Resource(name = "depositStoreBranchServiceImpl")
	DepositStoreBranchService depositStoreBranchService;
	
	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;
	
	@Resource(name = "payDetailsBranchServiceImpl")
	PayDetailsBranchService payDetailsBranchService;
	
	@Resource(name = "payDepositBranchServiceImpl")
	PayDepositBranchService payDepositBranchService;
	
	@Resource(name = "depositSupplierServiceImpl")
	DepositSupplierService depositSupplierService;

	@Resource(name = "hyCompanyServiceImpl")
	HyCompanyService hyCompanyService;

	@Resource(name = "depositStoreServiceImpl")
	DepositStoreService depositStoreService;

	@Resource(name = "payServicerServiceImpl")
	PayServicerService payServicerService;

	@Resource(name = "payShareProfitServiceImpl")
	PayShareProfitService payShareProfitService;

	@Resource(name = "payGuiderServiceImpl")
	PayGuiderService payGuiderService;

	@Resource(name = "paySettlementServiceImpl")
	PaySettlementService paySettlementService;

	@Resource(name = "payDepositServiceImpl")
	PayDepositService payDepositService;

	@Resource(name = "payDetailsServiceImpl")
	PayDetailsService payDetailsService;

	@Resource(name = "refundInfoServiceImpl")
	RefundInfoService refundInfoService;

	@Resource(name = "refundRecordsServiceImpl")
	RefundRecordsService refundRecordsService;

	@Resource(name = "bankListServiceImpl")
	BankListService bankListService;

	@Resource(name = "storeServiceImpl")
	StoreService storeService;

	@Autowired
	DepartmentService departmentService;

	@Autowired
	HyReceiptRefundService hyReceiptRefundService;

	/**
	 * 列表数据
	 */
	@RequestMapping("/datagrid/view")
	@ResponseBody
	public Json getDataGrid(Pageable pageable, String name, Integer type, HttpSession session)  {
		
		Json j = new Json();
		HashMap<String, Object> obj = new HashMap<>();
		
		try {
			
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			
			Department department = admin.getDepartment();
			String treePath = department.getTreePath(); //形如  ",1,6,8,15,"的格式
			String[] strings = treePath.split(",");
			Long branchId = Long.parseLong(strings[2]);
			
			//分公司id作为查询条件之一
			List<Filter> filters  = new ArrayList<>();
			filters.add(Filter.eq("branchId", branchId));
			pageable.setFilters(filters);
			
			// 按时间倒序，这里直接使用id
			List<Order> orders = new ArrayList<>();
			orders.add(Order.desc("id"));
			pageable.setOrders(orders);

			if(type == 4){
				List<Filter> storeFilters = new ArrayList<>();
				storeFilters.add(Filter.like("storeName",name));
				List<Store> stores = storeService.findList(null,storeFilters,null);

				List<Filter> receiptRefundFilters = new ArrayList<>();
				receiptRefundFilters.add(Filter.in("store",stores));

				Department branch = departmentService.find(branchId);
				receiptRefundFilters.add(Filter.eq("branch",branch));

				receiptRefundFilters.add(Filter.eq("type",1));

				receiptRefundFilters.add(Filter.eq("status",1));

				pageable.setFilters(receiptRefundFilters);

				Page<HyReceiptRefund> page = hyReceiptRefundService.findPage(pageable);

				List<HashMap<String, Object>> res = new ArrayList<>();
				for(HyReceiptRefund r: page.getRows()){
					HashMap<String, Object> map = new HashMap<>();
					map.put("id", r.getId());
					map.put("name", r.getStore().getStoreName());
					map.put("amount", r.getMoney());
					map.put("type", type);
					map.put("date", r.getCreateTime());
					res.add(map);
				}

				obj.put("rows", res);
				obj.put("total", page.getTotal());
				obj.put("pageNumber", page.getPageNumber());
				obj.put("tatalPage", page.getTotalPages());
				obj.put("pageSize", page.getPageSize());

			}
			if(type == 3){  // (挂靠)门店押金退还
				PayDepositBranch queryParam = new PayDepositBranch();
				queryParam.setHasPaid(0); //0:未付  1:已付
				queryParam.setInstitution(name);
				Page<PayDepositBranch> page = payDepositBranchService.findPage(pageable, queryParam);
				
				List<HashMap<String, Object>> res = new ArrayList<>();
				for(PayDepositBranch payDepositBranch : page.getRows()){
					HashMap<String, Object> map = new HashMap<>();
					map.put("id", payDepositBranch.getId());
					map.put("name", payDepositBranch.getInstitution());
					map.put("amount", payDepositBranch.getAmount());
					map.put("type", type);
					res.add(map);
				}
				
				obj.put("rows", res);
				obj.put("total", page.getTotal());
				obj.put("pageNumber", page.getPageNumber());
				obj.put("tatalPage", page.getTotalPages());
				obj.put("pageSize", page.getPageSize());
			}else if(type == 2){ // 门店增值业务 
				
				BranchPayServicer queryParam = new BranchPayServicer();
				queryParam.setHasPaid(0); //0:未付  1:已付
				queryParam.setServicerName(name);
				Page<BranchPayServicer> page = branchPayServicerService.findPage(pageable, queryParam);
				
				List<HashMap<String, Object>> res = new ArrayList<>();
				for(BranchPayServicer branchPayServicer : page.getRows()){
					HashMap<String, Object> map = new HashMap<>();
					map.put("id", branchPayServicer.getId());
					map.put("name", branchPayServicer.getServicerName());
					map.put("amount", branchPayServicer.getAmount());
					map.put("type", type);
					res.add(map);
				}
				
				obj.put("rows", res);
				obj.put("total", page.getTotal());
				obj.put("pageNumber", page.getPageNumber());
				obj.put("tatalPage", page.getTotalPages());
				obj.put("pageSize", page.getPageSize());
			}else if(type == 1){ // 分公司充值
				
			}
			
			j.setSuccess(true);
			j.setObj(obj);
		} catch (Exception e) {
			j.setMsg("操作失败");
			j.setSuccess(false);
		}
		
		return j;
	}

	// ******************************************************************************************************************//
	// ******************************************************************************************************************//
	// ******************************************************************************************************************//
	// ******************************************************************************************************************//
	// ******************************************************************************************************************//

	
	// 待付 - 退保证金 - (挂靠)门店
	@RequestMapping("/payDepositStore/view")
	@ResponseBody
	public Json PayDepositStoreDetail(PayDeposit payDeposit, HttpSession session)  {
		
		Json j = new Json();
		
		try {
			
			Long id = payDeposit.getId();
			PayDeposit p = payDepositService.find(id);
			BankList bankList = bankListService.find(p.getBankListId());

			HashMap<String, Object> obj = new HashMap<>();

			obj.put("id", id);

			obj.put("accountName", bankList.getAccountName());
			obj.put("bankName", bankList.getBankName());
			obj.put("bankCode", bankList.getBankCode());
			obj.put("bankType", bankList.getBankType());
			obj.put("bankAccount", bankList.getBankAccount());

			obj.put("applyDate", p.getApplyDate());
			obj.put("appliName", p.getAppliName());
			obj.put("storeName", p.getInstitution());
			obj.put("amount", p.getAmount());
			obj.put("balance", p.getBalance());
			obj.put("remark", p.getRemark());
			
			j.setSuccess(true);
			j.setObj(obj);
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("操作失败");
		}
		return j;
	}

	
	// 待付 - 门店增值业务付款
	@RequestMapping("payServicer/view")
	@ResponseBody
	public Json payServiceDetail(Long id, HttpSession session) {
		Json json = new Json();
		try {

			BranchPayServicer p = branchPayServicerService.find(id);
			HyAddedServiceSupplier supplier = hyAddedServiceSupplierService.find(p.getServicerId());
			BankList bankList = supplier.getBankList();

			HashMap<String, Object> obj = new HashMap<>();

			obj.put("id", id);

			obj.put("accountName", bankList.getAccountName());
			obj.put("bankName", bankList.getBankName());
			obj.put("bankCode", bankList.getBankCode());
			obj.put("bankType", bankList.getBankType());
			obj.put("bankAccount", bankList.getBankAccount());

			obj.put("applyDate", p.getApplyDate());
			obj.put("appliName", p.getAppliName());
			obj.put("supplierName", p.getServicerName());
			obj.put("amount", p.getAmount());
			obj.put("remark", p.getRemark());

			json.setSuccess(true);
			json.setObj(obj);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("操作失败");
		}
		return json;
	}
	

	// *******************************************************************************************************************************//
	// *******************************************************************************************************************************//
	// *******************************************************************************************************************************//
	
	// 待付 - 退保证金 - (挂靠)门店 - 做付款
	@RequestMapping("/payDepositStore/update")
	@ResponseBody
	@Transactional(propagation = Propagation.REQUIRED)
	public Json PayDepositStoreDetailUpdate(@RequestBody List<PayDetailsBranch> list, HttpSession session){
		Json j = new Json();
		
		try {
			
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			
			// 1.保存付款记录
			for (PayDetailsBranch p : list) {
				p.setOperator(admin.getName());
				payDetailsBranchService.save(p);
			}

			// 2.修改付款状态
			PayDetailsBranch payDetailsBranch = list.get(0);
			PayDepositBranch payDepositBranch = payDepositBranchService.find(payDetailsBranch.getPayId());
			payDepositBranch.setHasPaid(1); // 1:已付款
			payDepositBranch.setPayDate(payDetailsBranch.getDate()); // 付款日期
			payDepositBranch.setPayer(admin.getName()); // 付款人
			payDepositBranchService.update(payDepositBranch);

			// 3.财务中心 门店保证金表增加数据
			DepositStoreBranch depositStoreBranch = new DepositStoreBranch();
			depositStoreBranch.setStoreName(payDepositBranch.getInstitution());
			depositStoreBranch.setType(2); // 1:交纳 2:退还
			depositStoreBranch.setDate(payDetailsBranch.getDate());
			depositStoreBranch.setAmount(payDepositBranch.getAmount());
			depositStoreBranch.setBranchId(payDepositBranch.getBranchId());
			// depositStore.setRemark();

			depositStoreBranchService.save(depositStoreBranch);
			
			j.setSuccess(true);
			j.setMsg("操作成功");
		} catch (Exception e) {
			j.setMsg("操作失败");
			j.setSuccess(false);
		}
		return j;
	}

	
	// 待付 - 门店增值业务 - 做付款
	@RequestMapping("/payServicer/update")
	@ResponseBody
	@Transactional(propagation = Propagation.REQUIRED)
	public Json payServicerDetailUpdate(@RequestBody List<PayDetailsBranch> list, HttpSession session){
		Json j = new Json();
		
		try {
			
			j = branchPayServicerService.addbranchPayServicer(list, session);
			
			j.setSuccess(true);
			j.setMsg("操作成功");
		} catch (Exception e) {
			j.setMsg("操作失败");
			j.setSuccess(false);
		}
		return j;
	}
	
	
	/***************************************************************************************************/
	/***************************************************************************************************/
	/***************************************************************************************************/

	// 进入做付款详情页，获取各分公司的所有的支付方式
	@RequestMapping("/account/branch/view")
	@ResponseBody
	public Json getAllKindAccount(HttpSession session) {
		Json json = new Json();
		List<HashMap<String, Object>> transferAccountList = new LinkedList<>();
		List<HashMap<String, Object>> aliPayAccountList = new LinkedList<>();
		List<HashMap<String, Object>> wechatPayAccountList = new LinkedList<>();
		try {
			HyCompany hyCompany = getCompanyBySession(session);
			List<Filter> filters = new LinkedList<>();
			filters.add(Filter.eq("hyCompany", hyCompany));
			//正常状态的帐号
			filters.add(Filter.eq("bankListStatus",1));
			List<BankList> list = bankListService.findList(null, filters, null);

			for (BankList bankList : list) {
				HashMap<String, Object> map = new HashMap<>(1);
				map.put("alias", bankList.getAlias());
				map.put("id", bankList.getId());
				// 1.银行帐号
				if(bankList.getType() == BankList.BankType.bank ){
					transferAccountList.add(map);
				}
				// 2.支付宝
				else if(bankList.getType() == BankList.BankType.alipay){
					aliPayAccountList.add(map);
				}
				// 3.微信支付
				else if(bankList.getType() == BankList.BankType.wechatpay){
					wechatPayAccountList.add(map);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		HashMap<String, Object> obj = new HashMap<>();
		obj.put("transferAccount", transferAccountList);
		obj.put("wechatPayAccountList", wechatPayAccountList);
		obj.put("aliPayAccountList", aliPayAccountList);

		json.setSuccess(true);
		json.setObj(obj);
		return json;
	}

	/**
	 * 根据session获取Company
	 */
	private HyCompany getCompanyBySession(HttpSession session) {
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin admin = hyAdminService.find(username);
		Department pDepartment = admin.getDepartment().getHyDepartment();
		List<Filter> filters = new ArrayList<>(1);
		filters.add(Filter.eq("hyDepartment",pDepartment.getId()));
		List<HyCompany> list = hyCompanyService.findList(null, filters, null);
		HyCompany hyCompany = list.get(0);
		return hyCompany;
	}


	/** 待付款-詳情-实退款 **/
	@RequestMapping("/payRealRefund/view")
	@ResponseBody
	public Json ReceiptRefundDetail(Long id,HttpSession session){

		Json json = new Json();

		try {

			HashMap<String, Object> obj = new HashMap<>();

			HyReceiptRefund hyReceiptRefund = hyReceiptRefundService.find(id);

			obj.put("amount", hyReceiptRefund.getMoney());


			obj.put("id", id);

			obj.put("bankCode", hyReceiptRefund.getBankNum());

			obj.put("applyDate", hyReceiptRefund.getCollectionTime());
			obj.put("appliName", hyReceiptRefund.getOperator().getName());
			obj.put("remark", hyReceiptRefund.getRemark());

			obj.put("accountName", hyReceiptRefund.getCusName());
			obj.put("bankName", hyReceiptRefund.getCusBank());
			obj.put("bankCode", hyReceiptRefund.getCusUninum());
			obj.put("bankAccount", hyReceiptRefund.getBankNum());


			ReceiptDetailBranch receiptDetailBranch = toReceiptDetailBranch(hyReceiptRefund);

			List<ReceiptDetailBranch> list = new ArrayList<>();
			list.add(receiptDetailBranch);
	//		obj.put("record", list);


			json.setSuccess(true);
			json.setObj(obj);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("操作失败");
		}

		return json;
	}

	public ReceiptDetailBranch toReceiptDetailBranch(HyReceiptRefund hyReceiptRefund){
		ReceiptDetailBranch receiptDepositStoreBranch = new ReceiptDetailBranch();
		receiptDepositStoreBranch.setAmount(hyReceiptRefund.getMoney());
		receiptDepositStoreBranch.setDate(hyReceiptRefund.getCollectionTime());
		receiptDepositStoreBranch.setRemark(hyReceiptRefund.getRemark());
		receiptDepositStoreBranch.setReceiver(hyReceiptRefund.getCusBank());
		receiptDepositStoreBranch.setAccountName(hyReceiptRefund.getBankNum());
		return receiptDepositStoreBranch;
	}

	// 待付 - 实退款 - 做付款
	@RequestMapping("/payRealRefund/update")
	@ResponseBody
	@Transactional(propagation = Propagation.REQUIRED)
	public Json payRealRefundUpdate(@RequestBody List<PayDetailsBranch> list, HttpSession session){
		Json j = new Json();

		try {



			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);

			// 1.保存付款记录
			for (PayDetailsBranch p : list) {
				p.setOperator(admin.getName());
				p.setSort(4);
				payDetailsBranchService.save(p);
			}

			// 2.修改付款状态
			PayDetailsBranch payDetailsBranch = list.get(0);
			HyReceiptRefund hyReceiptRefund = hyReceiptRefundService.find(payDetailsBranch.getPayId());
			hyReceiptRefund.setCollectionTime(new Date());
			hyReceiptRefund.setStatus(2);   //已付款

			hyReceiptRefundService.update(hyReceiptRefund);

			j.setSuccess(true);
			j.setMsg("操作成功");
		} catch (Exception e) {
			j.setMsg("操作失败");
			j.setSuccess(false);
		}
		return j;
	}


}
