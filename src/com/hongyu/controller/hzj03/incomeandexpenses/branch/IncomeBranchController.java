package com.hongyu.controller.hzj03.incomeandexpenses.branch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.controller.BaseController;
import com.hongyu.entitycustom.CollectCustom;

/**
 * 已收款-分公司
 */
@Controller
@RequestMapping("/admin/collected/branch")
public class IncomeBranchController extends BaseController {

	@Resource(name = "receiptDepositStoreBranchServiceImpl")
	ReceiptDepositStoreBranchService receiptDepositStoreBranchService;

	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;

	@Resource(name = "receiptManageFeeStoreServiceImpl")
	ReceiptManageFeeStoreService receiptManageFeeStoreService;

	@Resource(name = "receiptDepositStoreServiceImpl")
	ReceiptDepositStoreService receiptDepositStoreService;

	@Resource(name = "receiptDepositServicerServiceImpl")
	ReceiptDepositServicerService receiptDepositServicerService;

	@Resource(name = "receiptStoreRechargeServiceImpl")
	ReceiptStoreRechargeService receiptStoreRechargeService;

	@Resource(name = "receiptBranchRechargeServiceImpl")
	ReceiptBranchRechargeService receiptBranchRechargeService;

	@Resource(name = "receiptDistributorRechargeServiceImpl")
	ReceiptDistributorRechargeService receiptDistributorRechargeService;

	@Resource(name = "receiptBillingCycleServiceImpl")
	ReceiptBillingCycleService receiptBillingCycleService;

	@Resource(name = "receiptOtherServiceImpl")
	ReceiptOtherService receiptOtherService;

	@Resource(name = "receiptDetailsServiceImpl")
	ReceiptDetailsService receiptDetailsService;

	@Resource(name = "receiptDetailBranchServiceImpl")
	ReceiptDetailBranchService receiptDetailBranchService;

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
	public Json getDataGrid(Pageable pageable, String startTime, String endTime, String name, String orderCode,
			Integer type, HttpSession session) throws Exception {
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
			
			//按时间倒序  这里直接使用id
			List<Order> orders = new ArrayList<>();
			orders.add(Order.desc("id"));  
			pageable.setOrders(orders);
			
			
			if (type == 1) { // 已收-门店管理费
				ReceiptManageFeeStore queryParam = new ReceiptManageFeeStore();
				queryParam.setstoreName(name);
				queryParam.setState(1); // state: 1 已收款
				Page<ReceiptManageFeeStore> page = receiptManageFeeStoreService.findPage(pageable, queryParam);

				List<CollectCustom> res = new ArrayList<CollectCustom>();
				for (ReceiptManageFeeStore r : page.getRows()) {
					CollectCustom c = new CollectCustom();
					c.setAmount(r.getAmount());
					c.setDate(r.getDate());
					c.setId(r.getId());
					c.setName(r.getStoreName());
					c.setType(type);
					c.setRecervicer(r.getReceiver());
					res.add(c);
				}

				obj.put("rows", res);
				obj.put("total", page.getTotal());
				obj.put("pageNumber", page.getPageNumber());
				obj.put("totalPage", page.getTotalPages());
				obj.put("pageSize", page.getPageSize());

			}
			
			else if(type == 3){ //(挂靠)门店押金
				ReceiptDepositStoreBranch queryParam = new ReceiptDepositStoreBranch();
				queryParam.setStoreName(name);
				queryParam.setState(1); //0:未付 1:已付
				Page<ReceiptDepositStoreBranch>	page =	receiptDepositStoreBranchService.findPage(pageable,queryParam);
				
				List<HashMap<String, Object>> res = new ArrayList<>();
				for(ReceiptDepositStoreBranch r: page.getRows()){
					HashMap<String, Object> map = new HashMap<>();
					map.put("id", r.getId());
					map.put("name", r.getStoreName());
					map.put("amount", r.getAmount());
					map.put("type", type);
					map.put("date", r.getDate());
					res.add(map);
				}
				
				obj.put("rows", res);
				obj.put("total", page.getTotal());
				obj.put("pageNumber", page.getPageNumber());
				obj.put("tatalPage", page.getTotalPages());
				obj.put("pageSize", page.getPageSize());
			}

			else if(type == 4){
				List<Filter> storeFilters = new ArrayList<>();
				storeFilters.add(Filter.like("storeName",name));
				List<Store> stores = storeService.findList(null,storeFilters,null);

				List<Filter> receiptRefundFilters = new ArrayList<>();
				receiptRefundFilters.add(Filter.in("store",stores));

				Department branch = departmentService.find(branchId);
				receiptRefundFilters.add(Filter.eq("branch",branch));

				receiptRefundFilters.add(Filter.eq("type",0));

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
			
			
			j.setObj(obj);
			j.setSuccess(true);
		} catch (Exception e) {
			j.setMsg("操作失败");
			j.setSuccess(false);
		}
		return j;
	}

	
	/**
	 * 已收款详情-门店管理费
	 */
	@RequestMapping("/storeManageFee/view")
	@ResponseBody
	public Json storeManageFeeDetail(Long id, HttpSession session) {

		Json j = new Json();

		HashMap<String, Object> obj = new HashMap<>();
		try {
			ReceiptManageFeeStore receiptManageFeeStore = receiptManageFeeStoreService.find(id);

			ReceiptDetailBranch receiptDetailBranch = new ReceiptDetailBranch();
			receiptDetailBranch.setReceiptType(1); // 1: 门店保证金
			receiptDetailBranch.setReceiptId(id);
			List<ReceiptDetailBranch> list = receiptDetailBranchService.findPage(new Pageable(), receiptDetailBranch)
					.getRows();

			obj.put("payer", receiptManageFeeStore.getPayer());
			obj.put("id", id);
			obj.put("storeName", receiptManageFeeStore.getStoreName());
			obj.put("amount", receiptManageFeeStore.getAmount());
			obj.put("record", list);
			
			j.setSuccess(true);
			j.setObj(obj);
			
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("操作失败");
		}
		
		return j;
	}

	
	/**已收款-详情-(挂靠)门店押金*/
	@RequestMapping("/storeDeposit/view")
	@ResponseBody
	public Json storeDepositBranchDetail(Long id, HttpSession session){
		Json json = new Json();
		
		try {
			
			HashMap<String, Object> obj = new HashMap<>();

			ReceiptDepositStoreBranch receiptDepositStoreBranch = receiptDepositStoreBranchService.find(id);
			
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("receiptType", 3)); //3:(挂靠)门店保证金
			filters.add(Filter.eq("receiptId", id));
			List<ReceiptDetailBranch> list = receiptDetailBranchService.findList(null, filters, null);
 			
			obj.put("payer", receiptDepositStoreBranch.getPayer());
			obj.put("id", id);
			obj.put("storeName", receiptDepositStoreBranch.getStoreName());
			obj.put("amount", receiptDepositStoreBranch.getAmount());

			obj.put("record", list);

			
			json.setSuccess(true);
			json.setObj(obj);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("操作失败");
		}
	
		return json;
	}


	/** 已收款-詳情-实收款 **/
	@RequestMapping("/realMoney/view")
	@ResponseBody
	public Json ReceiptRefundDetail(Long id,HttpSession session){


		Json json = new Json();

		try {

			HashMap<String, Object> obj = new HashMap<>();

			HyReceiptRefund hyReceiptRefund = hyReceiptRefundService.find(id);

			obj.put("payer", hyReceiptRefund.getOperator());
			obj.put("id", id);
			obj.put("storeName", hyReceiptRefund.getStore().getStoreName());
			obj.put("amount", hyReceiptRefund.getMoney());

			 toReceiptDetailBranch(hyReceiptRefund);

			List<Map<String,Object>> list = new ArrayList<>();
			list.add(toReceiptDetailBranch(hyReceiptRefund));
			obj.put("record", list);


			json.setSuccess(true);
			json.setObj(obj);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("操作失败");
		}

		return json;
	}

	@Autowired
	BankListService bankListService;

	public Map<String,Object> toReceiptDetailBranch(HyReceiptRefund hyReceiptRefund){
		Map<String,Object> map = new HashMap<>();
		map.put("payMethod",hyReceiptRefund.getMethod());
		map.put("amount",hyReceiptRefund.getMoney());
		map.put("date",hyReceiptRefund.getCollectionTime());
		map.put("remark",hyReceiptRefund.getRemark());
		List<Filter> bankListFilters = new ArrayList<>();
		bankListFilters.add(Filter.eq("bankAccount",hyReceiptRefund.getBankNum()));
		List<BankList> bankLists = bankListService.findList(null,bankListFilters,null);
		if(bankLists!=null && !bankLists.isEmpty()){
			BankList bankList = bankLists.get(0);
			map.put("accountName",bankList.getAccountName());
			map.put("shroffAccount",bankList.getBankAccount());
			map.put("bankName",bankList.getBankName());
		}else{
			map.put("accountName","");
			map.put("shroffAccount","");
			map.put("bankName","");

		}
		return map;
	}


}
