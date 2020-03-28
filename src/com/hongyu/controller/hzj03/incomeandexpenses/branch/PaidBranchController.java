package com.hongyu.controller.hzj03.incomeandexpenses.branch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import com.hongyu.entity.*;
import com.hongyu.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Filter.Operator;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.util.liyang.EmployeeUtil;

/**
 * 付款 - 已付款 - 分公司 - 收支记录
 */
@Controller
@RequestMapping("/admin/paid/branch")
public class PaidBranchController {
	@Resource(name = "payDepositBranchServiceImpl")
	PayDepositBranchService payDepositBranchService;

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

	@Resource(name = "departmentServiceImpl")
	DepartmentService departmentService;
	
	@Resource(name = "branchRechargeRecordServiceImpl")
	BranchRechargeRecordService branchRechargeRecordService;
	
	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;
	
	@Resource(name = "branchPayServicerServiceImpl")
	BranchPayServicerService branchPayServicerService;
	
	@Resource(name = "payDetailsBranchServiceImpl")
	PayDetailsBranchService payDetailsBranchService;
	
	@Resource(name = "branchRechargeServiceImpl")
	BranchRechargeService branchRechargeService;
	
	@Resource(name = "addedServiceTransferServiceImpl")
	AddedServiceTransferService addedServiceTransferService;

	@Resource(name = "storeServiceImpl")
	StoreService storeService;


	@Autowired
	HyReceiptRefundService hyReceiptRefundService;

	/**
	 * 列表数据
	 */
	@RequestMapping("/datagrid/view")
	@ResponseBody
	public Json getDataGrid(Pageable pageable, String startTime, String endTime, String name, Integer type,
			HttpSession session) throws Exception {
		Json j = new Json();
		HashMap<String, Object> obj = new HashMap<>();

		// 按时间列表倒序
		List<Order> orders = new ArrayList<>();
		orders.add(Order.desc("id"));
		pageable.setOrders(orders);

		try {
			// 时间范围条件
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			List<Filter> filters = new ArrayList<>();
			if (startTime != null && !startTime.equals(""))
				filters.add(
						new Filter("payDate", Operator.ge, sdf.parse(startTime.substring(0, 10) + " " + "00:00:00")));
			if (endTime != null && !endTime.equals(""))
				filters.add(new Filter("payDate", Operator.le, sdf.parse(endTime.substring(0, 10) + " " + "23:59:59")));
			

			if (type == 3) { // (挂靠)门店押金退还
				pageable.setFilters(filters);
				PayDepositBranch queryParm = new PayDepositBranch();
				queryParm.setHasPaid(1); // 1:已付
				queryParm.setInstitution(name);

				Page<PayDepositBranch> page = payDepositBranchService.findPage(pageable, queryParm);
				List<HashMap<String, Object>> res = new ArrayList<>();
				for (PayDepositBranch payDepositBranch : page.getRows()) {
					HashMap<String, Object> map = new HashMap<>();
					map.put("name", payDepositBranch.getInstitution());
					map.put("id", payDepositBranch.getId());
					map.put("amount", payDepositBranch.getAmount());
					map.put("date", payDepositBranch.getPayDate());
					map.put("payer", payDepositBranch.getPayer());
					map.put("type", type);

					res.add(map);
				}

				obj.put("rows", res);
				obj.put("total", page.getTotal());
				obj.put("pageNumber", page.getPageNumber());
				obj.put("totalPage", page.getTotalPages());
				obj.put("pageSize", page.getPageSize());
			}
			
			//add by wj
			else if(type == 1){
				String username = (String) session.getAttribute(CommonAttributes.Principal);
				Department branch = EmployeeUtil.getCompany(hyAdminService.find(username));
				if(name!=null){
					if(name.equals(branch.getName())){
						obj.put("rows", null);
						obj.put("total", 0);
						obj.put("pageNumber",1);
						obj.put("totalPage", 1);
						obj.put("pageSize", 0);
						j.setObj(obj);
						return j;
					}
				}
				filters.add(Filter.eq("branchId", branch.getId()));
				filters.add(Filter.eq("hasPaid", 1));
				pageable.setFilters(filters);
				Page<BranchRechargeRecord> branchRechargeRecords = branchRechargeRecordService.findPage(pageable);
				List<HashMap<String, Object>> res = new ArrayList<>();
				for(BranchRechargeRecord branchRechargeRecord:branchRechargeRecords.getRows()){
					HashMap<String, Object> map = new HashMap<>();
					map.put("name",branch.getName());
					map.put("id", branchRechargeRecord.getId());
					map.put("amount", branchRechargeRecord.getAmount());
					map.put("date", branchRechargeRecord.getPayDate());
					map.put("payer", branchRechargeRecord.getAppliName());
					map.put("type", type);	
					
					res.add(map);
				}
				obj.put("rows", res);
				obj.put("total", branchRechargeRecords.getTotal());
				obj.put("pageNumber", branchRechargeRecords.getPageNumber());
				obj.put("totalPage", branchRechargeRecords.getTotalPages());
				obj.put("pageSize", branchRechargeRecords.getPageSize());
			}
			else if(type == 2){  //增值业务付款
				String username = (String) session.getAttribute(CommonAttributes.Principal);
				Department branch = EmployeeUtil.getCompany(hyAdminService.find(username));
				filters.add(Filter.eq("branchId", branch.getId()));
				filters.add(Filter.eq("hasPaid", 1));
				if(name!=null){
					filters.add(Filter.eq("servicerName", name));
				}
				pageable.setFilters(filters);
				Page<BranchPayServicer> branchPayServicers = branchPayServicerService.findPage(pageable);
				List<HashMap<String, Object>> res = new ArrayList<>();
				for(BranchPayServicer branchPayServicer : branchPayServicers.getRows()){
					HashMap<String, Object> map = new HashMap<>();
					map.put("name",branchPayServicer.getServicerName());
					map.put("id", branchPayServicer.getId());
					map.put("amount", branchPayServicer.getAmount());
					map.put("date", branchPayServicer.getPayDate());
					map.put("payer", branchPayServicer.getAppliName());
					map.put("type", type);	
					
					res.add(map);
				}
				obj.put("rows", res);
				obj.put("total", branchPayServicers.getTotal());
				obj.put("pageNumber", branchPayServicers.getPageNumber());
				obj.put("totalPage", branchPayServicers.getTotalPages());
				obj.put("pageSize", branchPayServicers.getPageSize());
			}
			else if(type == 4){
				String username = (String) session.getAttribute(CommonAttributes.Principal);
				Department branch = EmployeeUtil.getCompany(hyAdminService.find(username));
				List<Filter> storeFilters = new ArrayList<>();
				storeFilters.add(Filter.like("storeName",name));
				List<Store> stores = storeService.findList(null,storeFilters,null);

				List<Filter> receiptRefundFilters = new ArrayList<>();
				receiptRefundFilters.add(Filter.in("store",stores));

				receiptRefundFilters.add(Filter.eq("branch",branch));

				receiptRefundFilters.add(Filter.eq("type",1));

				receiptRefundFilters.add(Filter.eq("status",2));

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
					//收款信息
					PayDetailsBranch payDetailsBranch = new PayDetailsBranch();
					payDetailsBranch.setSort(4);
					payDetailsBranch.setPayId(r.getId());
					Page<PayDetailsBranch> payDetailsBranchs = payDetailsBranchService.findPage(new Pageable(), payDetailsBranch);
					List<HashMap<String, Object>> record = new ArrayList<>();
					if(!payDetailsBranchs.getRows().isEmpty()){
						PayDetailsBranch payDetailsBranch1 = payDetailsBranchs.getRows().get(0);
						map.put("payer",payDetailsBranch1.getOperator());
					}else{
						map.put("payer","");
					}
					obj.put("record", record);
					res.add(map);
				}

				obj.put("rows", res);
				obj.put("total", page.getTotal());
				obj.put("pageNumber", page.getPageNumber());
				obj.put("tatalPage", page.getTotalPages());
				obj.put("pageSize", page.getPageSize());
			}

			j.setSuccess(true);
			j.setObj(obj);

		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("操作失败");
		}

		return j;
	}

	// ******************************************************************************************************************//
	// ******************************************************************************************************************//
	// ******************************************************************************************************************//


	// 已付 - 退保证金 - 门店
	@RequestMapping("/payDepositStore/view")
	@ResponseBody
	public Json PayDepositStoreDetail(PayDeposit payDeposit, HttpSession session) {

		Json j = new Json();
		try {
			Long id = payDeposit.getId();
			PayDeposit p = payDepositService.find(id);
			BankList bankList = bankListService.find(p.getBankListId());

			HashMap<String, Object> obj = new HashMap<>();

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

			PayDetails payDetail = new PayDetails();
			payDetail.setSort(5); // 5:PayDeposit
			payDetail.setPayId(id); //

			List<PayDetails> record = payDetailsService.findPage(new Pageable(), payDetail).getRows();
			obj.put("record", record);

			j.setSuccess(true);
			j.setObj(obj);


		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("操作失败");
		}
		return j;
	}
	
	//已付 - 分公司充值  
	// add by wj
	@RequestMapping("/branchRechargeRecordDetail/view")
	@ResponseBody
	public Json branchRechargeRecordDetail(Long id){
		Json json = new Json();
		try {
			BranchRechargeRecord branchRechargeRecord = branchRechargeRecordService.find(id);
			HashMap<String, Object> res = new HashMap<String, Object>();
			res.put("amount", branchRechargeRecord.getAmount());
			res.put("appliName", branchRechargeRecord.getAppliName());
			res.put("branchName", departmentService.find(branchRechargeRecord.getBranchId()).getName());
			res.put("remark", branchRechargeRecord.getRemark());	
			
			BranchRecharge branchRecharge = branchRechargeService.find(branchRechargeRecord.getBranchRechargeId());
			res.put("accountAlias", branchRecharge.getAccountAlias());
			res.put("bankAccount",branchRecharge.getBankAccount());
			res.put("bankCode", branchRecharge.getBankCode());
			res.put("bankType", branchRecharge.getBankType());
			res.put("bankName", branchRecharge.getBankName());
			res.put("appliDate",branchRecharge.getCreateDate());
			
			//收款信息
			PayDetailsBranch payDetailsBranch = new PayDetailsBranch();
			payDetailsBranch.setSort(1);
			payDetailsBranch.setPayId(id);			
			Page<PayDetailsBranch> payDetailsBranchs = payDetailsBranchService.findPage(new Pageable(), payDetailsBranch);
			List<HashMap<String, Object>> record = new ArrayList<>();
			for(PayDetailsBranch p:payDetailsBranchs.getRows()){
				HashMap<String, Object> map = new HashMap<>();
				map.put("account",p.getAccount());
				map.put("payMethod", p.getPayMethod());
				map.put("amount", p.getAmount());
				map.put("date", p.getDate());
				map.put("operator", hyAdminService.find(p.getOperator()).getName());
				
				record.add(map);
			}
			
			res.put("record", record);
			
			json.setObj(res);
			json.setMsg("查询成功");
			json.setSuccess(true);
		} catch (Exception e) {
			// TODO: handle exception
			json.setMsg("查询失败");
			json.setSuccess(false);
			e.printStackTrace();
			
		}
		return json;
		
	}
	
	//已付 - 增值业务
	@RequestMapping("/branchPayServicerDetail/view")
	@ResponseBody
	public Json branchPayServicerDetail(Long id){
		Json json = new Json();
		try {
			BranchPayServicer branchPayServicer = branchPayServicerService.find(id);
			HashMap<String, Object> res = new HashMap<String, Object>();
			res.put("amount", branchPayServicer.getAmount());
			res.put("appliName", branchPayServicer.getAppliName());
			res.put("branchName", departmentService.find(branchPayServicer.getBranchId()).getName());
			res.put("remark", branchPayServicer.getRemark());	
			
			AddedServiceTransfer addedServiceTransfer = addedServiceTransferService.find(branchPayServicer.getAddedServiceTransferId());
			BankList bankList = addedServiceTransfer.getSupplier().getBankList();
			res.put("accountAlias", bankList.getAlias());
			res.put("bankAccount",bankList.getBankAccount());
			res.put("bankCode", bankList.getBankCode());
			res.put("bankType", bankList.getBankType());
			res.put("bankName", bankList.getBankName());
			res.put("appliDate",addedServiceTransfer.getCreateTime());
			
			//收款信息
			PayDetailsBranch payDetailsBranch = new PayDetailsBranch();
			payDetailsBranch.setSort(1);
			payDetailsBranch.setPayId(id);
			Page<PayDetailsBranch> payDetailsBranchs = payDetailsBranchService.findPage(new Pageable(), payDetailsBranch);
			List<HashMap<String, Object>> record = new ArrayList<>();
			for(PayDetailsBranch p:payDetailsBranchs.getRows()){
				HashMap<String, Object> map = new HashMap<>();
				map.put("account",p.getAccount());
				map.put("payMethod", p.getPayMethod());
				map.put("amount", p.getAmount());
				map.put("date", p.getDate());
				map.put("operator", hyAdminService.find(p.getOperator()).getName());
				
				record.add(map);
			}
			res.put("record", record);
			
			json.setObj(res);
			json.setMsg("查询成功");
			json.setSuccess(true);
		} catch (Exception e) {
			// TODO: handle exception
			json.setMsg("查询失败");
			json.setSuccess(false);
			e.printStackTrace();
		}
		return json;
		
	}


	/** 已付款-詳情-实退款 **/
	@RequestMapping("/realRefund/view")
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


			//收款信息
			PayDetailsBranch payDetailsBranch = new PayDetailsBranch();
			payDetailsBranch.setSort(4);
			payDetailsBranch.setPayId(id);
			Page<PayDetailsBranch> payDetailsBranchs = payDetailsBranchService.findPage(new Pageable(), payDetailsBranch);
			List<HashMap<String, Object>> record = new ArrayList<>();
			for(PayDetailsBranch p:payDetailsBranchs.getRows()){
				HashMap<String, Object> map = new HashMap<>();
				map.put("account",p.getAccount());
				map.put("payMethod", p.getPayMethod());
				map.put("amount", p.getAmount());
				map.put("date", p.getDate());
				map.put("operator", p.getOperator());

				record.add(map);
			}
			obj.put("record", record);



			json.setSuccess(true);
			json.setObj(obj);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("操作失败");
			json.setObj(e);
			e.printStackTrace();
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
	
	
}
