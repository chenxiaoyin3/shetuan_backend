package com.hongyu.controller.hzj03.incomeandexpenses;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import com.hongyu.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.controller.BaseController;
import com.hongyu.entity.ReceiptBillingCycle;
import com.hongyu.entity.ReceiptBranchRecharge;
import com.hongyu.entity.ReceiptDepositServicer;
import com.hongyu.entity.ReceiptDepositStore;
import com.hongyu.entity.ReceiptDetail;
import com.hongyu.entity.ReceiptDistributorRecharge;
import com.hongyu.entity.ReceiptOther;
import com.hongyu.entity.ReceiptStoreRecharge;
import com.hongyu.entitycustom.CollectCustom;
import com.hongyu.entitycustom.UnCollectCustom;
import com.hongyu.service.ReceiptBillingCycleService;
import com.hongyu.service.ReceiptBranchRechargeService;
import com.hongyu.service.ReceiptDepositServicerService;
import com.hongyu.service.ReceiptDepositStoreService;
import com.hongyu.service.ReceiptDetailsService;
import com.hongyu.service.ReceiptDistributorRechargeService;
import com.hongyu.service.ReceiptOtherService;
import com.hongyu.service.ReceiptStoreRechargeService;

/**
 * 已收款
 *
 * @author xyy
 */
@Controller
@RequestMapping("/admin/collected")
public class IncomeController extends BaseController {

	@Resource(name = "receiptDepositStoreServiceImpl")
	private ReceiptDepositStoreService receiptDepositStoreService;

	@Resource(name = "receiptDepositServicerServiceImpl")
	private ReceiptDepositServicerService receiptDepositServicerService;

	@Resource(name = "receiptStoreRechargeServiceImpl")
	private ReceiptStoreRechargeService receiptStoreRechargeService;

	@Resource(name = "receiptBranchRechargeServiceImpl")
	private ReceiptBranchRechargeService receiptBranchRechargeService;

	@Resource(name = "receiptDistributorRechargeServiceImpl")
	private ReceiptDistributorRechargeService receiptDistributorRechargeService;

	@Resource(name = "receiptBillingCycleServiceImpl")
	private ReceiptBillingCycleService receiptBillingCycleService;

	@Resource(name = "receiptOtherServiceImpl")
	private ReceiptOtherService receiptOtherService;

	@Resource(name = "receiptDetailsServiceImpl")
	private ReceiptDetailsService receiptDetailsService;

	/**
	 * 列表数据
	 */
	@RequestMapping("/datagrid/view")
	@ResponseBody
	public Json getDataGrid(Pageable pageable, String startTime, String endTime, String name, String orderCode, Integer type) {
		Json j = new Json();
		HashMap<String, Object> obj = new HashMap<>();

        // 倒序 这里直接使用id
		List<Order> orders = new ArrayList<>();
		orders.add(Order.desc("id"));
		pageable.setOrders(orders);
        // 时间范围条件
        List<Filter> filters = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
            if (StringUtils.isNotEmpty(startTime)){
                filters.add(new Filter("date", Filter.Operator.ge, sdf.parse(startTime.substring(0, 10) + " " + "00:00:00")));
            }
            if (StringUtils.isNotEmpty(endTime)) {
                filters.add(new Filter("date", Filter.Operator.le, sdf.parse(endTime.substring(0, 10) + " " + "23:59:59")));
            }
		    pageable.setFilters(filters);
            // 已收-门店保证金
			if (type == 1) {
				ReceiptDepositStore queryParam = new ReceiptDepositStore();
				queryParam.setstoreName(name);
                // state: 1 已收款
				queryParam.setState(1);
				Page<ReceiptDepositStore> page = receiptDepositStoreService.findPage(pageable, queryParam);
				List<CollectCustom> res = new ArrayList<>();
				for (ReceiptDepositStore r : page.getRows()) {
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
                Object moneySum = receiptDepositStoreService.getSingleResultByNativeQuery("SELECT SUM(amount) FROM hy_receipt_deposit_store");
				obj.put("moneySum", moneySum);
			}
            // 已收-供应商保证金
			else if (type == 2) {
				ReceiptDepositServicer queryParam = new ReceiptDepositServicer();
				queryParam.setServiceName(name);
                // state: 1 已未收款
				queryParam.setState(1);
				Page<ReceiptDepositServicer> page = receiptDepositServicerService.findPage(pageable, queryParam);
				List<CollectCustom> res = new ArrayList<>();
				for (ReceiptDepositServicer r : page.getRows()) {
					CollectCustom c = new CollectCustom();
					c.setAmount(r.getAmount());
					c.setDate(r.getDate());
					c.setId(r.getId());
					c.setName(r.getServiceName());
					c.setType(type);
					c.setRecervicer(r.getReceiver());
					res.add(c);
				}
				obj.put("rows", res);
				obj.put("total", page.getTotal());
				obj.put("pageNumber", page.getPageNumber());
				obj.put("totalPage", page.getTotalPages());
				obj.put("pageSize", page.getPageSize());
                Object moneySum = receiptDepositServicerService.getSingleResultByNativeQuery("SELECT SUM(amount) FROM hy_receipt_deposit_servicer");
                obj.put("moneySum", moneySum);
			}
            // 已收-门店充值
			else if (type == 3) {
				ReceiptStoreRecharge queryParam = new ReceiptStoreRecharge();
				queryParam.setStoreName(name);
                // state: 1 已收款
				queryParam.setState(1);
				Page<ReceiptStoreRecharge> page = receiptStoreRechargeService.findPage(pageable, queryParam);
				List<CollectCustom> res = new ArrayList<>();
				for (ReceiptStoreRecharge r : page.getRows()) {
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
                Object moneySum = receiptStoreRechargeService.getSingleResultByNativeQuery("SELECT SUM(amount) FROM hy_receipt_store_recharge");
                obj.put("moneySum", moneySum);
			}
            // 已收-分公司充值
			else if (type == 4) {
				ReceiptBranchRecharge queryParam = new ReceiptBranchRecharge();
				queryParam.setBranchName(name);
                // state: 1 已收款
				queryParam.setState(1);
				Page<ReceiptBranchRecharge> page = receiptBranchRechargeService.findPage(pageable, queryParam);
				List<UnCollectCustom> res = new ArrayList<>();
				for (ReceiptBranchRecharge r : page.getRows()) {
					CollectCustom c = new CollectCustom();
					c.setAmount(r.getAmount());
					c.setDate(r.getDate());
					c.setId(r.getId());
					c.setName(r.getBranchName());
					c.setType(type);
					c.setRecervicer(r.getReceiver());
					res.add(c);
				}
				obj.put("rows", res);
				obj.put("total", page.getTotal());
				obj.put("pageNumber", page.getPageNumber());
				obj.put("totalPage", page.getTotalPages());
				obj.put("pageSize", page.getPageSize());
                Object moneySum = receiptBranchRechargeService.getSingleResultByNativeQuery("SELECT SUM(amount) FROM hy_receipt_branch_recharge");
                obj.put("moneySum", moneySum);
			}
            // 已收-门票分销商充值
			else if (type == 5) {
				ReceiptDistributorRecharge queryParam = new ReceiptDistributorRecharge();
				queryParam.setDistributorName(name);
                // state: 1 已收款
				queryParam.setState(1);
				Page<ReceiptDistributorRecharge> page = receiptDistributorRechargeService.findPage(pageable, queryParam);
				List<CollectCustom> res = new ArrayList<>();
				for (ReceiptDistributorRecharge r : page.getRows()) {
					CollectCustom c = new CollectCustom();
					c.setAmount(r.getAmount());
					c.setDate(r.getDate());
					c.setId(r.getId());
					c.setName(r.getDistributorName());
					c.setType(type);
					c.setRecervicer(r.getReceiver());
					res.add(c);
				}
				obj.put("rows", res);
				obj.put("total", page.getTotal());
				obj.put("pageNumber", page.getPageNumber());
				obj.put("totalPage", page.getTotalPages());
				obj.put("pageSize", page.getPageSize());
                Object moneySum = receiptDistributorRechargeService.getSingleResultByNativeQuery("SELECT SUM(amount) FROM hy_receipt_distributor_recharge");
                obj.put("moneySum", moneySum);
			}
            // 已收-门票分销商周期结算
			else if (type == 6) {
				ReceiptBillingCycle queryParam = new ReceiptBillingCycle();
				queryParam.setDistributorName(name);
                // state: 1 已收款
				queryParam.setState(1);
				Page<ReceiptBillingCycle> page = receiptBillingCycleService.findPage(pageable, queryParam);
				List<CollectCustom> res = new ArrayList<>();
				for (ReceiptBillingCycle r : page.getRows()) {
					CollectCustom c = new CollectCustom();
					c.setAmount(r.getAmount());
					c.setDate(r.getDate());
					c.setId(r.getId());
					c.setName(r.getDistributorName());
					c.setType(type);
					c.setRecervicer(r.getReceiver());
					res.add(c);
				}
				obj.put("rows", res);
				obj.put("total", page.getTotal());
				obj.put("pageNumber", page.getPageNumber());
				obj.put("totalPage", page.getTotalPages());
				obj.put("pageSize", page.getPageSize());
                Object moneySum = receiptBillingCycleService.getSingleResultByNativeQuery("SELECT SUM(amount) FROM hy_receipt_billing_cycle");
                obj.put("moneySum", moneySum);
			}
            // 已收-门店认购门票
			else if (type == 11) {
				ReceiptOther queryParam = new ReceiptOther();
				queryParam.setInstitution(name);
				queryParam.setOrderCode(orderCode);
                // ReceiptOther表的type列 13:门店认购门票
				queryParam.setType(13);
				Page<ReceiptOther> page = receiptOtherService.findPage(pageable, queryParam);
				List<CollectCustom> res = new ArrayList<>();
				for (ReceiptOther r : page.getRows()) {
					CollectCustom c = new CollectCustom();
					c.setAmount(r.getAmount());
					c.setDate(r.getDate());
					c.setId(r.getId());
					c.setName(r.getInstitution());
					c.setType(type);
					c.setOrderCoder(r.getOrderCode());
					res.add(c);
				}
				obj.put("rows", res);
				obj.put("total", page.getTotal());
				obj.put("pageNumber", page.getPageNumber());
				obj.put("totalPage", page.getTotalPages());
				obj.put("pageSize", page.getPageSize());
                Object moneySum = receiptOtherService.getSingleResultByNativeQuery("SELECT SUM(amount) FROM hy_receipt_other WHERE type = 13");
                obj.put("moneySum", moneySum);
			}
            // 已收-门店保险
			else if (type == 12) {
				ReceiptOther queryParam = new ReceiptOther();
				queryParam.setInstitution(name);
				queryParam.setOrderCode(orderCode);
                // ReceiptOther表的type列 14:门店保险
				queryParam.setType(14);
				Page<ReceiptOther> page = receiptOtherService.findPage(pageable, queryParam);
				List<CollectCustom> res = new ArrayList<>();
				for (ReceiptOther r : page.getRows()) {
					CollectCustom c = new CollectCustom();
					c.setAmount(r.getAmount());
					c.setDate(r.getDate());
					c.setId(r.getId());
					c.setName(r.getInstitution());
					c.setType(type);
					c.setOrderCoder(r.getOrderCode());
					res.add(c);
				}
				obj.put("rows", res);
				obj.put("total", page.getTotal());
				obj.put("pageNumber", page.getPageNumber());
				obj.put("totalPage", page.getTotalPages());
				obj.put("pageSize", page.getPageSize());
                Object moneySum = receiptOtherService.getSingleResultByNativeQuery("SELECT SUM(amount) FROM hy_receipt_other WHERE type = 14");
                obj.put("moneySum", moneySum);
			}
			else {
				Map<String, Object> params = null;
				String jpql;
				String range = "";
				Object moneySum = null;
				if (type == 7) {
				    // 已收-电子门票(包括门店、微商、官网 1、2、3)
					range = "(1,2,3)";
                    moneySum = receiptOtherService.getSingleResultByNativeQuery("SELECT SUM(amount) FROM hy_receipt_other WHERE type IN (1,2,3)");
				} else if (type == 8) {
				    // 已收-签证(包括门店、微商、官网 4、5、6)
					range = "(4,5,6)";
                    moneySum = receiptOtherService.getSingleResultByNativeQuery("SELECT SUM(amount) FROM hy_receipt_other WHERE type IN (4,5,6)");
                } else if (type == 9) {
				    // 已收-报名(包括门店、微商、官网 7、8、9)
					range = "(7,8,9)";
                    moneySum = receiptOtherService.getSingleResultByNativeQuery("SELECT SUM(amount) FROM hy_receipt_other WHERE type IN (7,8,9)");
                } else if (type == 10) {
				    // 已收-酒店/酒加景(包括门店、微商、官网10、11、12)
					range = "(10,11,12)";
                    moneySum = receiptOtherService.getSingleResultByNativeQuery("SELECT SUM(amount) FROM hy_receipt_other WHERE type IN (10,11,12)");
                }
                // 没有单位名称
				if (StringUtils.isBlank(name)) {
					if (StringUtils.isBlank(orderCode)) {
                        // 没有单位名称也没有订单号
						jpql = "SELECT * FROM receipt_other WHERE type IN " + range;
                    } else {
					    // 没有单位名称而有订单号
						jpql = "SELECT * FROM receipt_other WHERE type IN " + range + " AND  order_code  LIKE :orderCode";
						params = new HashMap<>(1);
						params.put("orderCode", "%" + orderCode + "%");
					}
				} else {
				    // 有单位名称则必然没有订单号
					jpql = "SELECT * FROM receipt_other WHERE type IN " + range + " AND institution LIKE :name";
					params = new HashMap<>(1);
					params.put("name", "%" + name + "%");
				}
				Page<List<Object[]>> page = receiptOtherService.findPageBySqlAndParam(jpql, params, pageable);
				List<Object[]> list = page.getLstObj();
				List<CollectCustom> res = new ArrayList<>();
				for (Object[] arr : list) {
					CollectCustom c = new CollectCustom();
					c.setAmount((BigDecimal) arr[4]);
					c.setDate((Date) arr[5]);
					// arr[0]的类型为BigInteger
					c.setId(Long.valueOf(arr[0].toString()));
					c.setName((String) arr[3]);
					c.setType(type);
					c.setOrderCoder((String) arr[2]);
					res.add(c);
				}
				obj.put("rows", res);
				obj.put("total", page.getTotal());
				obj.put("pageNumber", page.getPageNumber());
				obj.put("totalPage", page.getTotalPages());
				obj.put("pageSize", page.getPageSize());
                obj.put("moneySum", moneySum);
			}
			j.setObj(obj);
			j.setSuccess(true);
		} catch (Exception e) {
			j.setObj("操作失败");
			j.setSuccess(false);
			e.printStackTrace();
		}
		return j;
	}

	/**
	 * 已收款详情-门店保证金
	 */
	@RequestMapping("/storeDeposit/view")
	@ResponseBody
	public Json StoreDepositDetail(Long id, HttpSession session) {

		Json j = new Json();
		HashMap<String, Object> obj = new HashMap<>();

		try {
			ReceiptDepositStore receiptDepositStore = receiptDepositStoreService.find(id);

			ReceiptDetail receiptDetails = new ReceiptDetail();
			receiptDetails.setReceiptType(1); // 1: 门店保证金
			receiptDetails.setReceiptId(id);
			List<ReceiptDetail> list = receiptDetailsService.findPage(new Pageable(), receiptDetails).getRows();

			obj.put("id", id);
			obj.put("storeName", receiptDepositStore.getStoreName());
			obj.put("amount", receiptDepositStore.getAmount());
			obj.put("record", list);

			j.setSuccess(true);
			j.setObj(obj);
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("操作失败");
		}
		return j;
	}

	/**
	 * 已收款详情-供应商保证金
	 */
	@RequestMapping("/servicerDeposit/view")
	@ResponseBody
	public Json ServicerDepositDetail(Long id, HttpSession session) {
		Json j = new Json();

		HashMap<String, Object> obj = new HashMap<>();

		try {
			ReceiptDepositServicer receiptDepositServicer = receiptDepositServicerService.find(id);

			ReceiptDetail receiptDetails = new ReceiptDetail();
			receiptDetails.setReceiptType(2); // 2: 供应商保证金
			receiptDetails.setReceiptId(id);
			List<ReceiptDetail> list = receiptDetailsService.findPage(new Pageable(), receiptDetails).getRows();

			obj.put("id", id);
			obj.put("servicerName", receiptDepositServicer.getServiceName());
			obj.put("contractCode", receiptDepositServicer.getContractCode());
			obj.put("amount", receiptDepositServicer.getAmount());
			obj.put("record", list);

			j.setSuccess(true);
			j.setObj(obj);

		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("操作失败");
		}
		return j;
	}

	/**
	 * 已收款详情-门店充值
	 */
	@RequestMapping("/storerecharge/view")
	@ResponseBody
	public Json StoreRechargeDepositDetail(Long id, HttpSession session) {

		Json j = new Json();

		HashMap<String, Object> obj = new HashMap<>();

		try {

			ReceiptStoreRecharge receiptStoreRecharge = receiptStoreRechargeService.find(id);

			ReceiptDetail receiptDetails = new ReceiptDetail();
			receiptDetails.setReceiptType(3); // 3: 门店充值
			receiptDetails.setReceiptId(id);
			List<ReceiptDetail> list = receiptDetailsService.findPage(new Pageable(), receiptDetails).getRows();

			obj.put("id", id);
			obj.put("storeName", receiptStoreRecharge.getStoreName());
			obj.put("appliName", receiptStoreRecharge.getPayer());
			obj.put("amount", receiptStoreRecharge.getAmount());
			obj.put("record", list);

			j.setSuccess(true);
			j.setObj(obj);

		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("操作失败");
		}
		return j;
	}

	/**
	 * 已收款详情-分公司充值
	 */
	@RequestMapping("/branchrecharge/view")
	@ResponseBody
	public Json BranchRechargeDetail(Long id, HttpSession session) {
		Json j = new Json();
		HashMap<String, Object> obj = new HashMap<>();

		try {
			ReceiptBranchRecharge receiptBranchRecharge = receiptBranchRechargeService.find(id);

			ReceiptDetail receiptDetails = new ReceiptDetail();
			receiptDetails.setReceiptType(4); // 4: 分公司充值
			receiptDetails.setReceiptId(id);
			List<ReceiptDetail> list = receiptDetailsService.findPage(new Pageable(), receiptDetails).getRows();

			obj.put("id", id);
			obj.put("branchName", receiptBranchRecharge.getBranchName());
			obj.put("appliName", receiptBranchRecharge.getPayer());
			obj.put("amount", receiptBranchRecharge.getAmount());
			obj.put("record", list);

			j.setSuccess(true);
			j.setObj(obj);

		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("操作失败");
		}
		return j;
	}

	/**
	 * 已收款详情-门票分销商充值
	 */
	@RequestMapping("/distributorRecharge/view")
	@ResponseBody
	public Json DistributorRechargeDetail(Long id, HttpSession session) {

		Json j = new Json();

		try {

			HashMap<String, Object> obj = new HashMap<>();

			ReceiptDistributorRecharge receiptDistributorRecharge = receiptDistributorRechargeService.find(id);

			ReceiptDetail receiptDetails = new ReceiptDetail();
			receiptDetails.setReceiptType(5); // 5: 门票分销商充值
			receiptDetails.setReceiptId(id);
			List<ReceiptDetail> list = receiptDetailsService.findPage(new Pageable(), receiptDetails).getRows();

			obj.put("id", id);
			obj.put("applyDate", receiptDistributorRecharge.getApplyDate());
			obj.put("appliName", receiptDistributorRecharge.getAppliName());
			obj.put("distributorName", receiptDistributorRecharge.getDistributorName());
			obj.put("amount", receiptDistributorRecharge.getAmount());
			obj.put("record", list);

			j.setSuccess(true);
			j.setObj(obj);

		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("操作失败");
		}
		return j;
	}

	/**
	 * 已收款详情-门票分销商周期结算
	 */
	@RequestMapping("/billingCycle/view")
	@ResponseBody
	public Json BillingCycleDetail(Long id, HttpSession session) {

		Json j = new Json();

		HashMap<String, Object> obj = new HashMap<>();

		try {
			ReceiptBillingCycle receiptBillingCycle = receiptBillingCycleService.find(id);

			ReceiptDetail receiptDetails = new ReceiptDetail();
			receiptDetails.setReceiptType(6); // 6: 门票分销商充值
			receiptDetails.setReceiptId(id);
			List<ReceiptDetail> list = receiptDetailsService.findPage(new Pageable(), receiptDetails).getRows();

			obj.put("id", id);
			obj.put("distributorName", receiptBillingCycle.getDistributorName());
			obj.put("applyDate", receiptBillingCycle.getApplyDate());
			obj.put("appliName", receiptBillingCycle.getAppliName());
			obj.put("billingCycleStart", receiptBillingCycle.getBillingCycleStart());
			obj.put("billingCycleEnd", receiptBillingCycle.getBillingCycleEnd());
			obj.put("amount", receiptBillingCycle.getAmount());
			obj.put("record", list);

			j.setSuccess(true);
			j.setObj(obj);

		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("操作失败");
		}
		return j;
	}

	/**
	 * 已收款详情-Other
	 */
	@RequestMapping("/other/view")
	@ResponseBody
	public Json OtherDetail(Long id, HttpSession session) throws Exception {

		Json j = new Json();

		HashMap<String, Object> obj = new HashMap<>();

		try {
			ReceiptOther receiptOther = receiptOtherService.find(id);

			ReceiptDetail receiptDetails = new ReceiptDetail();
			receiptDetails.setReceiptType(7); // 7: Other
			receiptDetails.setReceiptId(id);
			List<ReceiptDetail> list = receiptDetailsService.findPage(new Pageable(), receiptDetails).getRows();

			obj.put("sort", receiptOther.getType());
			obj.put("orderCode", receiptOther.getOrderCode());
			obj.put("storeName", receiptOther.getInstitution());
			obj.put("amount", receiptOther.getAmount());
			obj.put("record", list);

			j.setSuccess(true);
			j.setObj(obj);

		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("操作失败");
		}
		return j;
	}
}
