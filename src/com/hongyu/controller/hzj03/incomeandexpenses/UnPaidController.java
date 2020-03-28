package com.hongyu.controller.hzj03.incomeandexpenses;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.grain.util.FilterUtil;
import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.controller.BaseController;
import com.hongyu.controller.hzj03.incomeandexpenses.strategypattern.UnPaidContext;
import com.hongyu.controller.hzj03.incomeandexpenses.strategypattern.strategies.PayDepositStrategy;
import com.hongyu.controller.hzj03.incomeandexpenses.strategypattern.strategies.PayGuiderStrategy;
import com.hongyu.controller.hzj03.incomeandexpenses.strategypattern.strategies.PayServicerStrategy;
import com.hongyu.controller.hzj03.incomeandexpenses.strategypattern.strategies.PaySettlementStrategy;
import com.hongyu.controller.hzj03.incomeandexpenses.strategypattern.strategies.PayShareProfitStrategy;
import com.hongyu.controller.hzj03.incomeandexpenses.strategypattern.strategies.RefundInfoStrategy;
import com.hongyu.entity.BalanceDueApply;
import com.hongyu.entity.BalanceDueApplyItem;
import com.hongyu.entity.BankList;
import com.hongyu.entity.BranchBalance;
import com.hongyu.entity.BranchPrePay;
import com.hongyu.entity.BranchPrePayDetail;
import com.hongyu.entity.BranchPreSave;
import com.hongyu.entity.Department;
import com.hongyu.entity.DepositStore;
import com.hongyu.entity.DepositSupplier;
import com.hongyu.entity.Guide;
import com.hongyu.entity.GuideAssignment;
import com.hongyu.entity.GuideSettlement;
import com.hongyu.entity.GuideSettlementDetail;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyCompany;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HyPaymentSupplier;
import com.hongyu.entity.PayDeposit;
import com.hongyu.entity.PayDetails;
import com.hongyu.entity.PayGuider;
import com.hongyu.entity.PayServicer;
import com.hongyu.entity.PaySettlement;
import com.hongyu.entity.PayShareProfit;
import com.hongyu.entity.PayablesLineItem;
import com.hongyu.entity.PaymentSupplier;
import com.hongyu.entity.PrePaySupply;
import com.hongyu.entity.ProfitShareConfirm;
import com.hongyu.entity.ProfitShareWechatBusiness;
import com.hongyu.entity.ReceiptServicer;
import com.hongyu.entity.ReceiptTotalServicer;
import com.hongyu.entity.RefundInfo;
import com.hongyu.entity.RefundRecords;
import com.hongyu.entity.WithDrawCash;
import com.hongyu.entity.WithDrawCashSubCompany;
import com.hongyu.entitycustom.UnPaidCustom;
import com.hongyu.service.BalanceDueApplyItemService;
import com.hongyu.service.BalanceDueApplyService;
import com.hongyu.service.BankListService;
import com.hongyu.service.BranchBalanceService;
import com.hongyu.service.BranchPrePayDetailService;
import com.hongyu.service.BranchPrePayService;
import com.hongyu.service.BranchPreSaveService;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.DepositStoreService;
import com.hongyu.service.DepositSupplierService;
import com.hongyu.service.GuideAssignmentService;
import com.hongyu.service.GuideService;
import com.hongyu.service.GuideSettlementDetailService;
import com.hongyu.service.GuideSettlementService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyCompanyService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.HyPaymentSupplierService;
import com.hongyu.service.PayDepositService;
import com.hongyu.service.PayDetailsService;
import com.hongyu.service.PayGuiderService;
import com.hongyu.service.PayServicerService;
import com.hongyu.service.PaySettlementService;
import com.hongyu.service.PayShareProfitService;
import com.hongyu.service.PayablesLineItemService;
import com.hongyu.service.PaymentSupplierService;
import com.hongyu.service.PrePaySupplyService;
import com.hongyu.service.ProfitShareConfirmService;
import com.hongyu.service.ReceiptServicerService;
import com.hongyu.service.ReceiptTotalServicerService;
import com.hongyu.service.RefundInfoService;
import com.hongyu.service.RefundRecordsService;
import com.hongyu.service.WithDrawCashService;
import com.hongyu.service.WithDrawCashSubCompanyService;

/**
 * 付款-待付款
 *
 * @author xyy
 */
@Controller
@RequestMapping("/admin/unpaid")
public class UnPaidController extends BaseController {
    @Resource(name = "balanceDueApplyItemServiceImpl")
    private BalanceDueApplyItemService balanceDueApplyItemService;

    @Resource(name = "hyCompanyServiceImpl")
    private HyCompanyService hyCompanyService;

    @Resource(name = "balanceDueApplyServiceImpl")
    private BalanceDueApplyService balanceDueApplyService;

    @Resource(name = "paymentSupplierServiceImpl")
    private PaymentSupplierService paymentSupplierService;

    @Resource(name = "branchPreSaveServiceImpl")
    private BranchPreSaveService branchPreSaveService;

    @Resource(name = "departmentServiceImpl")
    private DepartmentService departmentService;

    @Resource(name = "prePaySupplyServiceImpl")
    private PrePaySupplyService prePaySupplyService;

    @Resource(name = "branchBalanceServiceImpl")
    private BranchBalanceService branchBalanceService;

    @Resource(name = "hyPaymentSupplierServiceImpl")
    private HyPaymentSupplierService hyPaymentSupplierService;

    @Resource(name = "hyAdminServiceImpl")
    private HyAdminService hyAdminService;

    @Resource(name = "depositSupplierServiceImpl")
    private DepositSupplierService depositSupplierService;

    @Resource(name = "depositStoreServiceImpl")
    private DepositStoreService depositStoreService;

    @Resource(name = "payServicerServiceImpl")
    private PayServicerService payServicerService;

    @Resource(name = "payShareProfitServiceImpl")
    private PayShareProfitService payShareProfitService;

    @Resource(name = "payGuiderServiceImpl")
    private PayGuiderService payGuiderService;

    @Resource(name = "paySettlementServiceImpl")
    private PaySettlementService paySettlementService;

    @Resource(name = "payDepositServiceImpl")
    private PayDepositService payDepositService;

    @Resource(name = "payDetailsServiceImpl")
    private PayDetailsService payDetailsService;

    @Resource(name = "refundInfoServiceImpl")
    private RefundInfoService refundInfoService;

    @Resource(name = "refundRecordsServiceImpl")
    private RefundRecordsService refundRecordsService;

    @Resource(name = "bankListServiceImpl")
    private BankListService bankListService;

    @Resource(name = "branchPrePayServiceImpl")
    private BranchPrePayService branchPrePayService;

    @Resource(name = "branchPrePayDetailServiceImpl")
    private BranchPrePayDetailService branchPrePayDetailService;

    @Resource(name = "profitShareConfirmServiceImpl")
    private ProfitShareConfirmService profitShareConfirmService;

    @Resource(name = "guideSettlementServiceImpl")
    private GuideSettlementService guideSettlementService;

    @Resource(name = "guideSettlementDetailServiceImpl")
    private GuideSettlementDetailService guideSettlementDetailService;

    @Resource(name = "guideAssignmentServiceImpl")
    private GuideAssignmentService guideAssignmentService;

    @Resource(name = "guideServiceImpl")
    private GuideService guideService;

    @Resource(name = "receiptTotalServicerServiceImpl")
    private ReceiptTotalServicerService receiptTotalServicerService;

    @Resource(name = "receiptServicerServiceImpl")
    private ReceiptServicerService receiptServicerService;

    @Resource(name = "payablesLineItemServiceImpl")
    private PayablesLineItemService payablesLineItemService;

    @Resource(name = "hyOrderServiceImpl")
    private HyOrderService hyOrderService;

    @Resource(name = "withDrawCashServiceImpl")
    private WithDrawCashService withDrawCashService;

    @Resource(name = "withDrawCashSubCompanyServiceImpl")
    private WithDrawCashSubCompanyService withDrawCashSubCompanyService;

    /**
     * 列表数据
     */
    @RequestMapping("/datagrid/view")
    @ResponseBody
    public Json getDataGrid(Pageable pageable, String name, Integer type) {
        Json j = new Json();
        // 6:rows total pageNumber totalPage pageSize moneySum
        HashMap<String, Object> obj = new HashMap<>(6);
        try {
            //查询全部
            if (type == 0) {
                List<UnPaidCustom> res = new ArrayList<>();

                UnPaidContext unPaidContext = new UnPaidContext(new PayServicerStrategy());
                List<UnPaidCustom> payServicerList = unPaidContext.getUnPaidExecute(name);
                if (CollectionUtils.isNotEmpty(payServicerList)) {
                    res.addAll(payServicerList);
                }

                unPaidContext.setStrategy(new PayShareProfitStrategy());
                List<UnPaidCustom> payShareProfitList = unPaidContext.getUnPaidExecute(name);
                if (CollectionUtils.isNotEmpty(payShareProfitList)) {
                    res.addAll(payShareProfitList);
                }

                unPaidContext.setStrategy(new PayGuiderStrategy());
                List<UnPaidCustom> payGuiderList = unPaidContext.getUnPaidExecute(name);
                if (CollectionUtils.isNotEmpty(payGuiderList)) {
                    res.addAll(payGuiderList);
                }

                unPaidContext.setStrategy(new PaySettlementStrategy());
                List<UnPaidCustom> paySettlementList = unPaidContext.getUnPaidExecute(name);
                if (CollectionUtils.isNotEmpty(paySettlementList)) {
                    res.addAll(paySettlementList);
                }

                unPaidContext.setStrategy(new PayDepositStrategy());
                List<UnPaidCustom> payDepositList = unPaidContext.getUnPaidExecute(name);
                if (CollectionUtils.isNotEmpty(payDepositList)) {
                    res.addAll(payDepositList);
                }

                unPaidContext.setStrategy(new RefundInfoStrategy());
                List<UnPaidCustom> refundInfoList = unPaidContext.getUnPaidExecute(name);
                if (CollectionUtils.isNotEmpty(refundInfoList)) {
                    res.addAll(refundInfoList);
                }

                for (UnPaidCustom u : res) {
                    if (u.getType() == null) {
                    }
                }

                // 按type排序
                // Collections.sort(res, (UnPaidCustom o1, UnPaidCustom o2) -> (o1.getType() - o2.getType()));
                Collections.sort(res, new Comparator<UnPaidCustom>() {
                    @Override
                    public int compare(UnPaidCustom o1, UnPaidCustom o2) {
                        Integer id1 = o1.getType();
                        Integer id2 = o2.getType();
                        if (id1.equals(id2)) {
                            return 0;
                        }
                        return id1 > id2 ? 1 : -1;
                    }
                });
                int page = pageable.getPage();
                int rows = pageable.getRows();
                obj.put("total", res.size());
                obj.put("pageNumber", page);
                obj.put("pageSize", rows);
                obj.put("rows", res.subList((page - 1) * rows, page * rows > res.size() ? res.size() : page * rows));
                obj.put("totalPage", res.size() % rows == 0 ? res.size() / rows : res.size() / rows + 1);
            } else if (MappingUtil.PAY_SERVICER_TYPE_SET.containsKey(type)) {
                PayServicer queryParam = new PayServicer();
                // 0:未付 1:已付
                queryParam.setHasPaid(0);
                // 供应商名称
                queryParam.setServicerName(name);
                // PayServicer表的type
                queryParam.setType(MappingUtil.PAY_SERVICER_TYPE_SET.get(type));
                List<Order> orders = new LinkedList<>();
                orders.add(Order.desc("applyDate"));
                pageable.setOrders(orders);
                Page<PayServicer> page = payServicerService.findPage(pageable, queryParam);
                List<UnPaidCustom> res = new ArrayList<>();
                for (PayServicer p : page.getRows()) {
                    UnPaidCustom u = new UnPaidCustom();
                    u.setAmount(p.getAmount());
                    u.setId(p.getId());
                    u.setName(p.getServicerName());
                    u.setType(type);
                    u.setPayCode(p.getConfirmCode());//add by cqx
                    res.add(u);
                }
                obj.put("rows", res);
                obj.put("total", page.getTotal());
                obj.put("pageNumber", page.getPageNumber());
                obj.put("totalPage", page.getTotalPages());
                obj.put("pageSize", page.getPageSize());
                Object moneySum = payServicerService.getSingleResultByNativeQuery("SELECT SUM(amount) FROM hy_pay_servicer WHERE has_paid = 0 AND type = " + MappingUtil.PAY_SERVICER_TYPE_SET.get(type));
                obj.put("moneySum", moneySum);
            } else if (MappingUtil.PAY_SHAREPROFIT_TYPE_SET.containsKey(type)) {
                PayShareProfit queryParam = new PayShareProfit();
                // 0:未付款
                queryParam.setHasPaid(0);
                queryParam.setClient(name);
                queryParam.setType(MappingUtil.PAY_SHAREPROFIT_TYPE_SET.get(type));

                List<Order> orders = new LinkedList<>();
                orders.add(Order.desc("billingCycleEnd"));
                pageable.setOrders(orders);

                Page<PayShareProfit> page = payShareProfitService.findPage(pageable, queryParam);
                List<UnPaidCustom> res = new ArrayList<>();
                for (PayShareProfit p : page.getRows()) {
                    UnPaidCustom u = new UnPaidCustom();
                    u.setAmount(p.getAmount());
                    u.setId(p.getId());
                    u.setName(p.getClient());
                    u.setType(type);
                    res.add(u);
                }
                obj.put("rows", res);
                obj.put("total", page.getTotal());
                obj.put("pageNumber", page.getPageNumber());
                obj.put("totalPage", page.getTotalPages());
                obj.put("pageSize", page.getPageSize());
                Object moneySum = payShareProfitService.getSingleResultByNativeQuery("SELECT SUM(amount) FROM hy_pay_share_profit WHERE has_paid = 0 AND type = " + MappingUtil.PAY_SHAREPROFIT_TYPE_SET.get(type));
                obj.put("moneySum", moneySum);
            } else if (MappingUtil.PAY_GUIDER_TYPE_SET.containsKey(type)) {
                PayGuider queryParam = new PayGuider();
                // 0:未付款
                queryParam.setHasPaid(0);
                // 导游姓名
                queryParam.setGuider(name);
                queryParam.setType(MappingUtil.PAY_GUIDER_TYPE_SET.get(type));
                Page<PayGuider> page = payGuiderService.findPage(pageable, queryParam);
                List<UnPaidCustom> res = new ArrayList<>();
                for (PayGuider p : page.getRows()) {
                    UnPaidCustom u = new UnPaidCustom();
                    u.setAmount(p.getAmount());
                    u.setId(p.getId());
                    u.setName(p.getGuider());
                    u.setType(type);
                    res.add(u);
                }
                obj.put("rows", res);
                obj.put("total", page.getTotal());
                obj.put("pageNumber", page.getPageNumber());
                obj.put("totalPage", page.getTotalPages());
                obj.put("pageSize", page.getPageSize());
                Object moneySum = payGuiderService.getSingleResultByNativeQuery("SELECT SUM(amount) FROM hy_pay_guider WHERE has_paid = 0 AND type = " + MappingUtil.PAY_GUIDER_TYPE_SET.get(type));
                obj.put("moneySum", moneySum);
            } else if (MappingUtil.PAY_SETTLEMENT_TYPE_SET.containsKey(type)) {
                PaySettlement queryParam = new PaySettlement();
                // 0:未付款
                queryParam.setHasPaid(0);
                // 分公司名称
                queryParam.setBranchName(name);
                List<Order> orders = new LinkedList<>();
                orders.add(Order.desc("applyDate"));
                pageable.setOrders(orders);
                Page<PaySettlement> page = paySettlementService.findPage(pageable, queryParam);
                List<UnPaidCustom> res = new ArrayList<>();
                for (PaySettlement p : page.getRows()) {
                    UnPaidCustom u = new UnPaidCustom();
                    u.setAmount(p.getAmount());
                    u.setId(p.getId());
                    u.setName(p.getBranchName());
                    u.setType(type);
                    res.add(u);
                }
                obj.put("rows", res);
                obj.put("total", page.getTotal());
                obj.put("pageNumber", page.getPageNumber());
                obj.put("totalPage", page.getTotalPages());
                obj.put("pageSize", page.getPageSize());
                Object moneySum = paySettlementService.getSingleResultByNativeQuery("SELECT SUM(amount) FROM hy_pay_settlement WHERE has_paid = 0");
                obj.put("moneySum", moneySum);
            } else if (MappingUtil.PAY_DEPOSIT_TYPE_SET.containsKey(type)) {
                PayDeposit queryParam = new PayDeposit();
                // 0:未付款
                queryParam.setHasPaid(0);
                // 单位名称
                queryParam.setInstitution(name);
                queryParam.setDepositType(MappingUtil.PAY_DEPOSIT_TYPE_SET.get(type));
                List<Order> orders = new LinkedList<>();
                orders.add(Order.desc("applyDate"));
                pageable.setOrders(orders);
                Page<PayDeposit> page = payDepositService.findPage(pageable, queryParam);
                List<UnPaidCustom> res = new ArrayList<>();
                for (PayDeposit p : page.getRows()) {
                    UnPaidCustom u = new UnPaidCustom();
                    u.setAmount(p.getAmount());
                    u.setId(p.getId());
                    u.setName(p.getInstitution());
                    u.setType(type);
                    res.add(u);
                }
                obj.put("rows", res);
                obj.put("total", page.getTotal());
                obj.put("pageNumber", page.getPageNumber());
                obj.put("totalPage", page.getTotalPages());
                obj.put("pageSize", page.getPageSize());
                Object moneySum = payDepositService.getSingleResultByNativeQuery("SELECT SUM(amount) FROM hy_pay_deposit WHERE has_paid = 0 AND type = " + MappingUtil.PAY_DEPOSIT_TYPE_SET.get(type));
                obj.put("moneySum", moneySum);
            } else if (MappingUtil.REFUNDINFO_TYPE_SET.containsKey(type)) {
                RefundInfo queryParam = new RefundInfo();
                // 0:未付款
                queryParam.setState(0);
                // 单位名称 ——门店或个人
                queryParam.setAppliName(name);
                queryParam.setType(MappingUtil.REFUNDINFO_TYPE_SET.get(type));
                List<Order> orders = new LinkedList<>();
                orders.add(Order.desc("applyDate"));
                pageable.setOrders(orders);
                Page<RefundInfo> page = refundInfoService.findPage(pageable, queryParam);
                List<UnPaidCustom> res = new ArrayList<>();
                for (RefundInfo p : page.getRows()) {
                    UnPaidCustom u = new UnPaidCustom();
                    u.setAmount(p.getAmount());
                    u.setId(p.getId());
                    u.setName(p.getAppliName());
                    u.setType(type);
                    res.add(u);
                }
                obj.put("rows", res);
                obj.put("total", page.getTotal());
                obj.put("pageNumber", page.getPageNumber());
                obj.put("totalPage", page.getTotalPages());
                obj.put("pageSize", page.getPageSize());
                Object moneySum = refundInfoService.getSingleResultByNativeQuery("SELECT SUM(amount) FROM hy_refund_info WHERE has_paid = 0 AND type = " + MappingUtil.REFUNDINFO_TYPE_SET.get(type));
                obj.put("moneySum", moneySum);
            }
            j.setSuccess(true);
            j.setObj(obj);
        } catch (Exception e) {
            e.printStackTrace();
            j.setSuccess(false);
            j.setMsg("操作失败");
        }
        return j;
    }

    // ******************************************************************************************************************//
    // ******************************************************************************************************************//
    // ******************************************************************************************************************//
    // ******************************************************************************************************************//
    // ******************************************************************************************************************//

    /**
     * 待付 - 分公司预付款
     */
    @RequestMapping("/branchPrePay/view")
    @ResponseBody
    public Json branchprepayDetail(PayServicer payServicer) {
        Json j = new Json();

        Long id = payServicer.getId();
        PayServicer p = payServicerService.find(id);
        BankList bankList = bankListService.find(p.getBankListId());

        HashMap<String, Object> obj = new HashMap<>();

        try {
            obj.put("id", id);

            obj.put("accountName", bankList.getAccountName());
            obj.put("bankName", bankList.getBankName());
            obj.put("bankCode", bankList.getBankCode());
            obj.put("bankType", bankList.getBankType());
            obj.put("bankAccount", bankList.getBankAccount());

            obj.put("applyDate", p.getApplyDate());
            obj.put("appliName", p.getAppliName());
            obj.put("servicerName", p.getServicerName());
            obj.put("amount", p.getAmount());
            obj.put("remark", p.getRemark());

            j.setSuccess(true);
            j.setObj(obj);
        } catch (Exception e) {
            e.printStackTrace();
            j.setSuccess(false);
            j.setMsg("操作失败");
        }
        return j;
    }

    /**
     * 待付 - 总公司预付款
     */
    @RequestMapping("/companyPrePay/view")
    @ResponseBody
    public Json companyprepayDetail(PayServicer payServicer) {
        Json j = new Json();

        Long id = payServicer.getId();
        PayServicer p = payServicerService.find(id);
        BankList bankList = bankListService.find(p.getBankListId());

        HashMap<String, Object> obj = new HashMap<>();

        try {
            obj.put("id", id);

            obj.put("accountName", bankList.getAccountName());
            obj.put("bankName", bankList.getBankName());
            obj.put("bankCode", bankList.getBankCode());
            obj.put("bankType", bankList.getBankType());
            obj.put("bankAccount", bankList.getBankAccount());

            obj.put("applyDate", p.getApplyDate());
            obj.put("appliName", p.getAppliName());
            obj.put("servicerName", p.getServicerName());
            obj.put("amount", p.getAmount());
            obj.put("remark", p.getRemark());

            j.setSuccess(true);
            j.setObj(obj);
        } catch (Exception e) {
            e.printStackTrace();
            j.setSuccess(false);
            j.setMsg("操作失败");
        }
        return j;
    }

    /**
     * 待付 - T+N
     */
    @RequestMapping("/tnConfirm/view")
    @ResponseBody
    public Json tNConfirmDetail(PayServicer payServicer) {
        Json j = new Json();

        Long id = payServicer.getId();
        PayServicer p = payServicerService.find(id);
        BankList bankList = bankListService.find(p.getBankListId());
        String supplierName = paymentSupplierService.find(p.getReviewId()).getSupplierContract().getLiable().getUsername();

        HashMap<String, Object> obj = new HashMap<>();
        try {
            List<Filter> filters = new ArrayList<>();
            filters.add(Filter.eq("supplierName", supplierName));
            List<ReceiptTotalServicer> receiptTotalServicers = receiptTotalServicerService.findList(null, filters, null);
            BigDecimal balance = new BigDecimal(0);
            if (receiptTotalServicers.size() != 0) {
                balance = balance.add(receiptTotalServicers.get(0).getBalance());
            }
            obj.put("balance", balance);
            obj.put("id", id);
            obj.put("accountName", bankList.getAccountName());
            obj.put("bankName", bankList.getBankName());
            obj.put("bankCode", bankList.getBankCode());
            obj.put("bankType", bankList.getBankType());
            obj.put("bankAccount", bankList.getBankAccount());

            obj.put("servicerName", p.getServicerName());
            obj.put("confirmCode", p.getConfirmCode());
            obj.put("amount", p.getAmount());
            obj.put("remark", p.getRemark());

            j.setSuccess(true);
            j.setObj(obj);
        } catch (Exception e) {
            e.printStackTrace();
            j.setSuccess(false);
            j.setMsg("操作失败");
        }
        return j;
    }

    /**
     * 待付 - 提前打款
     */
    @RequestMapping("/prePay/view")
    @ResponseBody
    public Json prePayDetail(PayServicer payServicer) {
        Json j = new Json();

        Long id = payServicer.getId();
        PayServicer p = payServicerService.find(id);
        BankList bankList = bankListService.find(p.getBankListId());
        String supplierName = paymentSupplierService.find(p.getReviewId()).getSupplierContract().getLiable().getUsername();

        HashMap<String, Object> obj = new HashMap<>();

        try {
            List<Filter> filters = new ArrayList<>();
            filters.add(Filter.eq("supplierName", supplierName));
            List<ReceiptTotalServicer> receiptTotalServicers = receiptTotalServicerService.findList(null, filters, null);
            BigDecimal balance = new BigDecimal(0);
            if (receiptTotalServicers.size() != 0) {
                balance = balance.add(receiptTotalServicers.get(0).getBalance());
            }
            obj.put("balance", balance);

            obj.put("id", id);

            obj.put("accountName", bankList.getAccountName());
            obj.put("bankName", bankList.getBankName());
            obj.put("bankCode", bankList.getBankCode());
            obj.put("bankType", bankList.getBankType());
            obj.put("bankAccount", bankList.getBankAccount());

            obj.put("applyDate", p.getApplyDate());
            obj.put("appliName", p.getAppliName());
            obj.put("servicerName", p.getServicerName());
            obj.put("confirmCode", p.getConfirmCode());
            obj.put("amount", p.getAmount());
            obj.put("remark", p.getRemark());

            j.setSuccess(true);
            j.setObj(obj);
        } catch (Exception e) {
            e.printStackTrace();
            j.setSuccess(false);
            j.setMsg("操作失败");
        }
        return j;
    }

    /**
     * 待付 - 旅游元素供应商尾款
     */
    @RequestMapping("/payElements/view")
    @ResponseBody
    public Json payElementsDetail(PayServicer payServicer) {

        Json j = new Json();
        Long id = payServicer.getId();
        PayServicer p = payServicerService.find(id);
        BankList bankList = bankListService.find(p.getBankListId());

        HashMap<String, Object> obj = new HashMap<>();

        try {
            obj.put("id", id);

            if (bankList == null) {
                obj.put("accountName", p.getAccountName());
                obj.put("bankName", p.getBankName());
                obj.put("bankCode", p.getBankCode());
                obj.put("bankType", p.getBankType());
                obj.put("bankAccount", p.getBankAccount());
            } else {
                obj.put("accountName", bankList.getAccountName());
                obj.put("bankName", bankList.getBankName());
                obj.put("bankCode", bankList.getBankCode());
                obj.put("bankType", bankList.getBankType());
                obj.put("bankAccount", bankList.getBankAccount());
            }

            obj.put("applyDate", p.getApplyDate());
            obj.put("appliName", p.getAppliName());
            obj.put("servicerName", p.getServicerName());
            obj.put("amount", p.getAmount());
            obj.put("remark", p.getRemark());

            j.setSuccess(true);
            j.setObj(obj);
        } catch (Exception e) {
            e.printStackTrace();
            j.setSuccess(false);
            j.setMsg("操作失败");
        }
        return j;
    }

    /**
     * 待付 - 向酒店/门票/酒加景供应商付款
     */
    @RequestMapping("/ticketPay/view")
    @ResponseBody
    public Json ticketPayDetail(PayServicer payServicer) {
        Json j = new Json();

        Long id = payServicer.getId();
        PayServicer p = payServicerService.find(id);
        BankList bankList = bankListService.find(p.getBankListId());

        HashMap<String, Object> obj = new HashMap<>();

        try {
            obj.put("id", id);

            obj.put("accountName", bankList.getAccountName());
            obj.put("bankName", bankList.getBankName());
            obj.put("bankCode", bankList.getBankCode());
            obj.put("bankType", bankList.getBankType());
            obj.put("bankAccount", bankList.getBankAccount());

            obj.put("applyDate", p.getApplyDate());
            obj.put("appliName", p.getAppliName());
            obj.put("servicerName", p.getServicerName());
            obj.put("amount", p.getAmount());
            obj.put("remark", p.getRemark());

            j.setSuccess(true);
            j.setObj(obj);
        } catch (Exception e) {
            e.printStackTrace();
            j.setSuccess(false);
            j.setMsg("操作失败");
        }
        return j;
    }

    /**
     * 待付 - 提现审核付款
     */
    @RequestMapping("/withdraw_cash/view")
    @ResponseBody
    public Json withDrawCashPayDetail(PayServicer payServicer) {
        Json j = new Json();

        Long id = payServicer.getId();
        PayServicer p = payServicerService.find(id);
        BankList bankList = bankListService.find(p.getBankListId());

        HashMap<String, Object> obj = new HashMap<>();

        try {
            obj.put("id", id);

            obj.put("accountName", bankList.getAccountName());
            obj.put("bankName", bankList.getBankName());
            obj.put("bankCode", bankList.getBankCode());
            obj.put("bankType", bankList.getBankType());
            obj.put("bankAccount", bankList.getBankAccount());

            obj.put("applyDate", p.getApplyDate());
            obj.put("appliName", p.getAppliName());
            obj.put("servicerName", p.getServicerName());
            obj.put("amount", p.getAmount());
            obj.put("remark", p.getRemark());

            j.setSuccess(true);
            j.setObj(obj);
        } catch (Exception e) {
            e.printStackTrace();
            j.setSuccess(false);
            j.setMsg("操作失败");
        }
        return j;
    }

    /**
     * 待付 - 提现审核付款
     */
    @RequestMapping("/withdraw_cash_subcompany/view")
    @ResponseBody
    public Json withDrawCashSubCompanyPayDetail(PayServicer payServicer) {
        Json j = new Json();

        Long id = payServicer.getId();
        PayServicer p = payServicerService.find(id);
        BankList bankList = bankListService.find(p.getBankListId());

        HashMap<String, Object> obj = new HashMap<>();

        try {
            obj.put("id", id);

            obj.put("accountName", bankList.getAccountName());
            obj.put("bankName", bankList.getBankName());
            obj.put("bankCode", bankList.getBankCode());
            obj.put("bankType", bankList.getBankType());
            obj.put("bankAccount", bankList.getBankAccount());

            obj.put("applyDate", p.getApplyDate());
            obj.put("appliName", p.getAppliName());
            obj.put("servicerName", p.getServicerName());
            obj.put("amount", p.getAmount());
            obj.put("remark", p.getRemark());

            j.setSuccess(true);
            j.setObj(obj);
        } catch (Exception e) {
            e.printStackTrace();
            j.setSuccess(false);
            j.setMsg("操作失败");
        }
        return j;
    }

    /**
     * 待付 - 江泰预充值
     */
    @RequestMapping("/jtaiPresave/view")
    @ResponseBody
    public Json jtaipresaveDetail(PayServicer payServicer) {
        Json j = new Json();

        try {
            Long id = payServicer.getId();
            PayServicer p = payServicerService.find(id);
            HashMap<String, Object> obj = new HashMap<>();
            obj.put("id", id);
            // 预充值申请信息
            obj.put("applyDate", p.getApplyDate());
            obj.put("appliName", p.getAppliName());
            obj.put("servicerName", p.getServicerName());
            obj.put("amount", p.getAmount());
            obj.put("remark", p.getRemark());

            // 供应商信息
            obj.put("accountName", p.getAccountName());
            obj.put("bankName", p.getBankName());
            obj.put("bankCode", p.getBankCode());
            obj.put("bankType", p.getBankType());
            obj.put("bankAccount", p.getBankAccount());

            j.setSuccess(true);
            j.setObj(obj);
        } catch (Exception e) {
            e.printStackTrace();
            j.setSuccess(false);
            j.setMsg("操作失败");
        }
        return j;
    }

    /**
     * 待付 - 分公司分成
     */
    @RequestMapping("/profitShareConfirm/view")
    @ResponseBody
    public Json profitshareconfirmDetail(PayShareProfit payShareProfit) {

        Json j = new Json();

        Long id = payShareProfit.getId();
        PayShareProfit p = payShareProfitService.find(id);
        BankList bankList = bankListService.find(p.getBankListId());

        HashMap<String, Object> obj = new HashMap<>();

        try {
            obj.put("id", id);

            obj.put("accountName", bankList.getAccountName());
            obj.put("bankName", bankList.getBankName());
            obj.put("bankCode", bankList.getBankCode());
            obj.put("bankType", bankList.getBankType());
            obj.put("bankAccount", bankList.getBankAccount());

            obj.put("branchName", p.getClient());
            obj.put("confirmCode", p.getConfirmCode());
            obj.put("amount", p.getAmount());
            obj.put("remark", p.getRemark());

            j.setSuccess(true);
            j.setObj(obj);
        } catch (Exception e) {
            e.printStackTrace();
            j.setSuccess(false);
            j.setMsg("操作失败");
        }
        return j;
    }

    /**
     * 待付 - 导游报账应付款
     */
    @RequestMapping("/guideReimbursement/view")
    @ResponseBody
    public Json guidereimbursementDetail(PayGuider payGuider) {
        Json j = new Json();

        Long id = payGuider.getId();
        PayGuider p = payGuiderService.find(id);
        Guide guide = guideService.find(p.getGuiderId());

        HashMap<String, Object> obj = new HashMap<>();

        try {
            obj.put("id", id);
            obj.put("accountName", p.getAccountName());
            obj.put("bankName", p.getBankName());
            obj.put("bankCode", p.getBankLink());
            obj.put("bankType", "对私");
            obj.put("bankAccount", p.getBankAccount());
            obj.put("guiderId", guide.getGuideSn());
            obj.put("guider", p.getGuider());
            obj.put("amount", p.getAmount());
            obj.put("remark", p.getRemark());

            j.setSuccess(true);
            j.setObj(obj);
        } catch (Exception e) {
            e.printStackTrace();
            j.setSuccess(false);
            j.setMsg("操作失败");
        }
        return j;
    }

    /**
     * 待付 - 导游费用
     */
    @RequestMapping("/guideFee/view")
    @ResponseBody
    public Json guidefeeDetail(PayGuider payGuider) {
        Json j = new Json();

        Long id = payGuider.getId();
        PayGuider p = payGuiderService.find(id);
        Guide guide = guideService.find(p.getGuiderId());

        HashMap<String, Object> obj = new HashMap<>();

        try {
            obj.put("id", id);

            obj.put("accountName", p.getAccountName());
            obj.put("bankName", p.getBankName());
            obj.put("bankCode", p.getBankLink());
            obj.put("bankType", "对私");
            obj.put("bankAccount", p.getBankAccount());

            obj.put("guiderId", guide.getGuideSn());
            obj.put("guider", p.getGuider());
            obj.put("amount", p.getAmount());
            obj.put("remark", p.getRemark());

            j.setSuccess(true);
            j.setObj(obj);
        } catch (Exception e) {
            e.printStackTrace();
            j.setSuccess(false);
            j.setMsg("操作失败");
        }
        return j;
    }

    /**
     * 待付 - 分公司产品中心结算
     */
    @RequestMapping("/settlement/view")
    @ResponseBody
    public Json settlementDetail(PaySettlement paySettlement) {
        Json j = new Json();
        Long id = paySettlement.getId();
        PaySettlement p = paySettlementService.find(id);
        BankList bankList = bankListService.find(p.getBankListId());
        HashMap<String, Object> obj = new HashMap<>();

        try {
            obj.put("id", id);
            obj.put("accountName", bankList.getAccountName());
            obj.put("bankName", bankList.getBankName());
            obj.put("bankCode", bankList.getBankCode());
            obj.put("bankType", bankList.getBankType());
            obj.put("bankAccount", bankList.getBankAccount());

            obj.put("applyDate", p.getApplyDate());
            obj.put("appliName", p.getAppliName());
            obj.put("branchName", p.getBranchName());
            obj.put("settlementConfirmCode", p.getSettleConfirmCode());
            obj.put("amount", p.getAmount());
            obj.put("remark", p.getRemark());

            j.setSuccess(true);
            j.setObj(obj);
        } catch (Exception e) {
            e.printStackTrace();
            j.setSuccess(false);
            j.setMsg("操作失败");
        }
        return j;
    }

    /**
     * 待付 - 退保证金 - 门店
     */
    @RequestMapping("/payDepositStore/view")
    @ResponseBody
    public Json payDepositStoreDetail(PayDeposit payDeposit) {
        Json j = new Json();

        Long id = payDeposit.getId();
        PayDeposit p = payDepositService.find(id);
        BankList bankList = bankListService.find(p.getBankListId());

        HashMap<String, Object> obj = new HashMap<>();
        try {
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
            e.printStackTrace();
            j.setSuccess(false);
            j.setMsg("操作失败");
        }
        return j;
    }

    /**
     * 待付款 - 退保证金 - 供应商
     */
    @RequestMapping("/payDepositServicer/view")
    @ResponseBody
    public Json payDepositServicerDetail(PayDeposit payDeposit) {
        Json j = new Json();

        Long id = payDeposit.getId();
        PayDeposit p = payDepositService.find(id);
        BankList bankList = bankListService.find(p.getBankListId());

        HashMap<String, Object> obj = new HashMap<>();

        try {
            obj.put("id", id);

            obj.put("accountName", bankList.getAccountName());
            obj.put("bankName", bankList.getBankName());
            obj.put("bankCode", bankList.getBankCode());
            obj.put("bankType", bankList.getBankType());
            obj.put("bankAccount", bankList.getBankAccount());

            obj.put("applyDate", p.getApplyDate());
            obj.put("appliName", p.getAppliName());
            obj.put("servicerName", p.getInstitution());
            obj.put("contractCode", p.getContractCode());
            obj.put("amount", p.getAmount());
            obj.put("remark", p.getRemark());

            j.setSuccess(true);
            j.setObj(obj);
        } catch (Exception e) {
            e.printStackTrace();
            j.setSuccess(false);
            j.setMsg("操作失败");
        }
        return j;
    }

    /**
     * 待付款 - 供应商消团
     */
    @RequestMapping("/revoked/view")
    @ResponseBody
    public Json revokedDetail(RefundInfo refundInfo) {

        Json j = new Json();

        RefundInfo r = refundInfoService.find(refundInfo.getId());
        RefundRecords refundRecords = new RefundRecords();
        refundRecords.setRefundInfoId(refundInfo.getId());
        List<RefundRecords> list = refundRecordsService.findPage(new Pageable(), refundRecords).getRows();

        HashMap<String, Object> obj = new HashMap<>();

        try {
            obj.put("id", refundInfo.getId());
            obj.put("applyDate", r.getApplyDate());
            obj.put("appliName", r.getAppliName());
            obj.put("amount", r.getAmount());
            obj.put("remark", r.getRemark());
            obj.put("record", list);

            j.setSuccess(true);
            j.setObj(obj);
        } catch (Exception e) {
            e.printStackTrace();
            j.setSuccess(false);
            j.setMsg("操作失败");
        }
        return j;
    }

    /**
     * 待付款 - 游客退团
     */
    @RequestMapping("/refund/view")
    @ResponseBody
    public Json refundDetail(RefundInfo refundInfo) {
        Json j = new Json();

        RefundInfo r = refundInfoService.find(refundInfo.getId());
        RefundRecords refundRecords = new RefundRecords();
        refundRecords.setRefundInfoId(refundInfo.getId());
        List<RefundRecords> list = refundRecordsService.findPage(new Pageable(), refundRecords).getRows();

        HashMap<String, Object> obj = new HashMap<>();

        try {
            obj.put("id", refundInfo.getId());
            obj.put("applyDate", r.getApplyDate());
            obj.put("appliName", r.getAppliName());
            obj.put("amount", r.getAmount());
            obj.put("remark", r.getRemark());
            obj.put("record", list);

            j.setSuccess(true);
            j.setObj(obj);
        } catch (Exception e) {
            e.printStackTrace();
            j.setSuccess(false);
            j.setMsg("操作失败");
        }
        return j;
    }

    /**
     * 待付款 - 供应商驳回订单
     */
    @RequestMapping("/servicerDismiss/view")
    @ResponseBody
    public Json servicerdismissDetail(RefundInfo refundInfo) {

        Json j = new Json();

        try {

            RefundInfo r = refundInfoService.find(refundInfo.getId());
            RefundRecords refundRecords = new RefundRecords();
            refundRecords.setRefundInfoId(refundInfo.getId());
            List<RefundRecords> list = refundRecordsService.findPage(new Pageable(), refundRecords).getRows();

            HashMap<String, Object> obj = new HashMap<>();
            obj.put("id", refundInfo.getId());
            obj.put("applyDate", r.getApplyDate());
            obj.put("appliName", r.getAppliName());
            obj.put("amount", r.getAmount());
            obj.put("remark", r.getRemark());
            obj.put("record", list);

            j.setSuccess(true);
            j.setObj(obj);

        } catch (Exception e) {
            e.printStackTrace();
            j.setSuccess(false);
            j.setMsg("操作失败");
        }

        return j;
    }

    /**
     * 待付款 - 门店驳回订单
     */
    @RequestMapping("/storeDismiss/view")
    @ResponseBody
    public Json storedismissDetail(RefundInfo refundInfo) {
        Json j = new Json();

        try {
            RefundInfo r = refundInfoService.find(refundInfo.getId());
            RefundRecords refundRecords = new RefundRecords();
            refundRecords.setRefundInfoId(refundInfo.getId());
            List<RefundRecords> list = refundRecordsService.findPage(new Pageable(), refundRecords).getRows();

            HashMap<String, Object> obj = new HashMap<>();
            obj.put("id", refundInfo.getId());
            obj.put("applyDate", r.getApplyDate());
            obj.put("appliName", r.getAppliName());
            obj.put("amount", r.getAmount());
            obj.put("remark", r.getRemark());
            obj.put("record", list);

            j.setSuccess(true);
            j.setObj(obj);

        } catch (Exception e) {
            e.printStackTrace();
            j.setSuccess(false);
            j.setMsg("操作失败");
        }

        return j;
    }

    /**
     * 待付款 - 电子门票退款
     */
    @RequestMapping("/ticketRefundOnLine/view")
    @ResponseBody
    public Json ticketrefundonlineDetail(RefundInfo refundInfo) {

        Json j = new Json();

        try {
            RefundInfo r = refundInfoService.find(refundInfo.getId());
            RefundRecords refundRecords = new RefundRecords();
            refundRecords.setRefundInfoId(refundInfo.getId());
            List<RefundRecords> list = refundRecordsService.findPage(new Pageable(), refundRecords).getRows();

            HashMap<String, Object> obj = new HashMap<>();
            obj.put("id", refundInfo.getId());
            obj.put("amount", r.getAmount());
            obj.put("remark", r.getRemark());
            obj.put("record", list);

            j.setSuccess(true);
            j.setObj(obj);

        } catch (Exception e) {
            e.printStackTrace();
            j.setSuccess(false);
            j.setMsg("操作失败");
        }

        return j;
    }

    /**
     * 待付款 - 签证退款
     */
    @RequestMapping("/visaRefundOnLine/view")
    @ResponseBody
    public Json visarefundonlineDetail(RefundInfo refundInfo) {
        Json j = new Json();

        try {
            RefundInfo r = refundInfoService.find(refundInfo.getId());
            RefundRecords refundRecords = new RefundRecords();
            refundRecords.setRefundInfoId(refundInfo.getId());
            List<RefundRecords> list = refundRecordsService.findPage(new Pageable(), refundRecords).getRows();

            HashMap<String, Object> obj = new HashMap<>();
            obj.put("id", refundInfo.getId());
            obj.put("amount", r.getAmount());
            obj.put("remark", r.getRemark());
            obj.put("record", list);

            j.setSuccess(true);
            j.setObj(obj);

        } catch (Exception e) {
            e.printStackTrace();
            j.setSuccess(false);
            j.setMsg("操作失败");
        }

        return j;
    }

    /**
     * 待付款 - 酒店/酒加景退款
     */
    @RequestMapping("/hotelRefundOnLine/view")
    @ResponseBody
    public Json hotelrefundonlineDetail(RefundInfo refundInfo) {

        Json j = new Json();

        try {

            RefundInfo r = refundInfoService.find(refundInfo.getId());
            RefundRecords refundRecords = new RefundRecords();
            refundRecords.setRefundInfoId(refundInfo.getId());
            List<RefundRecords> list = refundRecordsService.findPage(new Pageable(), refundRecords).getRows();

            HashMap<String, Object> obj = new HashMap<>();
            obj.put("id", refundInfo.getId());
            obj.put("amount", r.getAmount());
            obj.put("remark", r.getRemark());
            obj.put("record", list);

            j.setSuccess(true);
            j.setObj(obj);

        } catch (Exception e) {
            e.printStackTrace();
            j.setSuccess(false);
            j.setMsg("操作失败");
        }

        return j;
    }

    /**
     * 待付款 - 微商后返
     */
    @RequestMapping("/profitShareWechatBusiness/view")
    @ResponseBody
    public Json profitshareWechatbusinessDetail(ProfitShareWechatBusiness profitShareWechatBusiness) {

        Json j = new Json();

        try {
            Long id = profitShareWechatBusiness.getId();
            PayShareProfit p = payShareProfitService.find(id);

            HashMap<String, Object> obj = new HashMap<>();

            obj.put("id", id);

            obj.put("weChatBusinessName", p.getClient());
            obj.put("billingCycleStart", p.getBillingCycleStart());
            obj.put("billingCycleEnd", p.getBillingCycleEnd());
            obj.put("amount", p.getAmount());
            obj.put("remark", p.getRemark());

            j.setSuccess(true);
            j.setObj(obj);

        } catch (Exception e) {
            e.printStackTrace();
            j.setSuccess(false);
            j.setMsg("操作失败");
        }

        return j;
    }

    // *******************************************************************************************************************************//
    // *******************************************************************************************************************************//
    // *******************************************************************************************************************************//
    // *******************************************************************************************************************************//
    // *******************************************************************************************************************************//

    /**
     * 待付 - 分公司预付款 - 做付款
     */
    @RequestMapping("/branchPrePay/update")
    @ResponseBody
    public Json branchprepayDetailUpdate(@RequestBody List<PayDetails> obj, HttpSession session) {

        Json j = new Json();

        try {
            String username = (String) session.getAttribute(CommonAttributes.Principal);
            HyAdmin hyAdmin = hyAdminService.find(username);

            // 1.保存付款记录
            for (PayDetails p : obj) {
                p.setSort(1);// 1:PayServicer 2:PayShareProfit 3:PayGuider
                // 4:PaySettlement 5:PayDeposit
                p.setOperator(hyAdmin.getName());
                payDetailsService.save(p);

            }

            // 2.修改付款状态
            PayDetails payDetail = obj.get(0);
            PayServicer payServicer = payServicerService.find(payDetail.getPayId());
            payServicer.setHasPaid(1); // 1:已付款
            payServicer.setPayDate(payDetail.getDate()); // 付款日期
            payServicer.setPayer(payDetail.getOperator()); // 付款人
            payServicerService.update(payServicer);

            // 3.做付款操对分公司充值余额的修改
            Long departmentId = payServicer.getDepartmentId();
            Department department = departmentService.find(departmentId);
            String treePath = department.getTreePath();
            String[] strings = treePath.split(",");
            Long branchId = Long.parseLong(strings[2]);
//			Department department2 = departmentService.find(branchId); // 在department中找到分公司
            List<Filter> filters = new LinkedList<>();
            filters.add(Filter.eq("branchId", branchId));
            BranchBalance branchBalance = branchBalanceService.findList(null, filters, null).get(0);
            BigDecimal balance = branchBalance.getBranchBalance().subtract(payServicer.getAmount());
            branchBalance.setBranchBalance(balance);
            branchBalanceService.update(branchBalance);
//			for (PayDetails p : obj) {
//				if(p.getPayMethod()==5){
//					BigDecimal balance = branchBalance.getBranchBalance().subtract(p.getAmount());
//					branchBalance.setBranchBalance(balance);
//					branchBalanceService.update(branchBalance);	
//					
//					// 分公司预存款
//					BranchPreSave branchPreSave = new BranchPreSave();
//					branchPreSave.setBranchId(branchId);
//					branchPreSave.setBranchName(department2.getName());  // 分公司名称
//					branchPreSave.setDepartmentName(department.getFullName()); // 部门全名
//					branchPreSave.setType(2); //1余额充值  2预付使用
//					branchPreSave.setDate(new Date());
//					branchPreSave.setAmount(payServicer.getAmount());
//					branchPreSave.setPreSaveBalance(branchBalance.getBranchBalance()); // 预存款使用后的余额
////					branchPreSave.setRemark(remark);
//					branchPreSaveService.save(branchPreSave);
//				}
//			}

            // 4.财务中心的记录(预存款和预付款)
            // 分公司预存款
            BranchPreSave branchPreSave = new BranchPreSave();
            branchPreSave.setBranchId(branchId);
            Department department2 = departmentService.find(branchId); // 在department中找到分公司
            branchPreSave.setBranchName(department2.getName());  // 分公司名称
            branchPreSave.setDepartmentName(department.getFullName()); // 部门全名
            branchPreSave.setType(2); //1余额充值  2预付使用
            branchPreSave.setDate(new Date());
            branchPreSave.setAmount(payServicer.getAmount());
            branchPreSave.setPreSaveBalance(branchBalance.getBranchBalance()); // 预存款使用后的余额
            branchPreSave.setRemark("预付使用");
            branchPreSaveService.save(branchPreSave);
            // 分公司预付款
            // 相当于在供应商处充值
            BranchPrePay branchPrePay = new BranchPrePay();
            branchPrePay.setBranchId(branchId);
            branchPrePay.setBranchName(department2.getFullName());
            branchPrePay.setDepartmentId(department.getId());
            branchPrePay.setDepartmentName(department.getFullName());
            branchPrePay.setServicerName(payServicer.getServicerName());
            branchPrePay.setSupplierElementId(payServicer.getServicerId());
            List<Filter> filters2 = FilterUtil.getInstance().getFilter(branchPrePay);
            List<BranchPrePay> bs = branchPrePayService.findList(null, filters2, null);
            if (bs.size() == 0) {
                branchPrePay.setPrePayBalance(payServicer.getAmount());
                branchPrePayService.save(branchPrePay);
            } else {
                BranchPrePay branchPrePayUpdate = bs.get(0);
                BigDecimal prePayBalance = branchPrePayUpdate.getPrePayBalance().add(payServicer.getAmount());
                branchPrePayUpdate.setPrePayBalance(prePayBalance);
                branchPrePayService.update(branchPrePayUpdate);
            }

            BranchPrePayDetail branchPrePayDetail = new BranchPrePayDetail();
            BranchPrePay branchPrePay2 = branchPrePayService.findList(null, filters2, null).get(0);
            branchPrePayDetail.setType(1);
            branchPrePayDetail.setBranchPrePayId(branchPrePay2.getId());
            branchPrePayDetail.setDate(payDetail.getDate());
            branchPrePayDetail.setAmount(payServicer.getAmount());
            branchPrePayDetail.setAppliname(payServicer.getAppliName());
            branchPrePayDetail.setPrePayBalance(branchPrePay2.getPrePayBalance());
            branchPrePayDetailService.save(branchPrePayDetail);

            // 5.审核申请表中的付款状态
            Long reviewId = payServicer.getReviewId();
            PrePaySupply prePaySupply = prePaySupplyService.find(reviewId);
            prePaySupply.setPayTime(payDetail.getDate());
            prePaySupply.setState(2); // 0审核中-未付 1 已通过-未付 2已通过-已付 3已驳回-未付
            prePaySupplyService.update(prePaySupply);

            j.setSuccess(true);
            j.setMsg("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            j.setSuccess(false);
            j.setMsg("操作失败");
        }
        return j;
    }

    /**
     * 待付 - 总公司预付款 - 做付款
     */
    @RequestMapping("/companyPrePay/update")
    @ResponseBody
    public Json companyPrepayDetailUpdate(@RequestBody List<PayDetails> obj, HttpSession session) {

        Json j = new Json();

        try {
            String username = (String) session.getAttribute(CommonAttributes.Principal);
            HyAdmin hyAdmin = hyAdminService.find(username);

            // 1.保存付款记录
            for (PayDetails p : obj) {
                p.setSort(1);// 1:PayServicer 2:PayShareProfit 3:PayGuider
                // 4:PaySettlement 5:PayDeposit
                p.setOperator(hyAdmin.getName());
                payDetailsService.save(p);
            }

            // 2.修改付款状态
            PayDetails payDetail = obj.get(0);
            PayServicer payServicer = payServicerService.find(payDetail.getPayId());
            payServicer.setHasPaid(1); // 1:已付款
            payServicer.setPayDate(payDetail.getDate()); // 付款日期
            payServicer.setPayer(payDetail.getOperator()); // 付款人
            payServicerService.update(payServicer);

            Long departmentId = payServicer.getDepartmentId();
            Department department = departmentService.find(departmentId);

            BranchPrePay branchPrePay = new BranchPrePay();
            branchPrePay.setBranchId((long) 1);
            branchPrePay.setBranchName("总公司");
            branchPrePay.setDepartmentId(department.getId());
            branchPrePay.setDepartmentName(department.getFullName());
            branchPrePay.setServicerName(payServicer.getServicerName());
            branchPrePay.setSupplierElementId(payServicer.getServicerId());
            List<Filter> filters2 = FilterUtil.getInstance().getFilter(branchPrePay);
            List<BranchPrePay> bs = branchPrePayService.findList(null, filters2, null);
            if (bs.size() == 0) {
                branchPrePay.setPrePayBalance(payServicer.getAmount());
                branchPrePayService.save(branchPrePay);
            } else {
                BranchPrePay branchPrePayUpdate = bs.get(0);
                BigDecimal prePayBalance = branchPrePayUpdate.getPrePayBalance().add(payServicer.getAmount());
                branchPrePayUpdate.setPrePayBalance(prePayBalance);
                branchPrePayService.update(branchPrePayUpdate);
            }

            BranchPrePayDetail branchPrePayDetail = new BranchPrePayDetail();
            BranchPrePay branchPrePay2 = branchPrePayService.findList(null, filters2, null).get(0);
            branchPrePayDetail.setType(1);
            branchPrePayDetail.setBranchPrePayId(branchPrePay2.getId());
            branchPrePayDetail.setDate(payDetail.getDate());
            branchPrePayDetail.setAmount(payServicer.getAmount());
            branchPrePayDetail.setAppliname(payServicer.getAppliName());
            branchPrePayDetail.setPrePayBalance(branchPrePay2.getPrePayBalance());
            branchPrePayDetailService.save(branchPrePayDetail);

            // 3.审核申请表中的付款状态
            Long reviewId = payServicer.getReviewId();
            PrePaySupply prePaySupply = prePaySupplyService.find(reviewId);
            prePaySupply.setPayTime(payDetail.getDate());
            prePaySupply.setState(2); // 0审核中-未付 1 已通过-未付 2已通过-已付 3已驳回-未付
            prePaySupplyService.update(prePaySupply);

            j.setSuccess(true);
            j.setMsg("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            j.setSuccess(false);
            j.setMsg("操作失败");
        }
        return j;
    }

    /**
     * 待付 - T+N- 做付款
     */
    @RequestMapping("/tnConfirm/update")
    @ResponseBody
    public Json tNConfirmDetailUpdate(@RequestBody List<PayDetails> obj, HttpSession session) {

        Json j = new Json();

        try {

            String username = (String) session.getAttribute(CommonAttributes.Principal);
            HyAdmin hyAdmin = hyAdminService.find(username);
            PayDetails payDetail = obj.get(0);
            PayServicer payServicer = payServicerService.find(payDetail.getPayId());
            String supplierName = paymentSupplierService.find(payServicer.getReviewId()).getSupplierContract().getLiable().getUsername();
            PaymentSupplier paymentSupplier = paymentSupplierService.find(payServicer.getReviewId());

            // 1.保存付款记录
            for (PayDetails p : obj) {
                p.setSort(1);// 1:PayServicer 2:PayShareProfit 3:PayGuider
                // 4:PaySettlement 5:PayDeposit
                p.setOperator(hyAdmin.getName());
                payDetailsService.save(p);
            }
            //add by wj
            if (paymentSupplier.getDebtamount() != null && paymentSupplier.getDebtamount().compareTo(new BigDecimal("0")) > 0) {
                //收支明细表
                ReceiptServicer receiptServicer = new ReceiptServicer();
                receiptServicer.setAmount(paymentSupplier.getDebtamount());
                receiptServicer.setDate(payDetail.getDate());
                receiptServicer.setOperator(payDetail.getOperator());
                receiptServicer.setOrderOrPayServicerId(payServicer.getId());
                receiptServicer.setSupplierName(supplierName);
                receiptServicer.setState(1);

                List<Filter> filters = new ArrayList<>();
                filters.add(Filter.eq("supplierName", supplierName));
                List<ReceiptTotalServicer> receiptTotalServicers = receiptTotalServicerService.findList(null, filters, null);
                receiptServicer.setBalance(receiptTotalServicers.get(0).getBalance());
                receiptServicerService.save(receiptServicer);

                //付款记录表添加欠款的付款记录
                PayDetails pay = new PayDetails();
                pay.setAmount(paymentSupplier.getDebtamount());
                pay.setAccount("欠款");
                pay.setDate(new Date());
                // 1:PayServicer 2:PayShareProfit 3:PayGuider 4:PaySettlement 5:PayDeposit
                pay.setSort(1);
                pay.setOperator(hyAdmin.getName());
                //欠款
                pay.setPayMethod(7L);
                pay.setPayId(payServicer.getId());
                payDetailsService.save(pay);

            }

            // 2.修改付款状态
            payServicer.setHasPaid(1); // 1:已付款
            payServicer.setPayDate(payDetail.getDate()); // 付款日期
            payServicer.setPayer(payDetail.getOperator()); // 付款人
            payServicerService.update(payServicer);

            // 3.更新PaymentSupplier的付款状态
            paymentSupplier.setPayDate(new Date());
            paymentSupplier.setStatus(3);
            paymentSupplierService.update(paymentSupplier);

            //更新结算状态
            List<Filter> filters = new ArrayList<>();
            filters.add(Filter.eq("paymentLineId", paymentSupplier.getId()));
            List<PayablesLineItem> payablesLineItems = payablesLineItemService.findList(null, filters, null);
            for (PayablesLineItem payablesLineItem : payablesLineItems) {
                HyOrder hyOrder = payablesLineItem.getHyOrder();
                hyOrder.setIfjiesuan(true);
                hyOrder.setJiesuantime(payServicer.getPayDate());
                hyOrderService.update(hyOrder);
            }

            j.setSuccess(true);
            j.setMsg("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            j.setSuccess(false);
            j.setMsg("操作失败");
        }
        return j;
    }

    /**
     * 待付 - 提前打款- 做付款
     */
    @RequestMapping("/prePay/update")
    @ResponseBody
    public Json prePayDetailUpdate(@RequestBody List<PayDetails> obj, HttpSession session) {
        Json j = new Json();

        try {
            String username = (String) session.getAttribute(CommonAttributes.Principal);
            HyAdmin hyAdmin = hyAdminService.find(username);

            PayDetails payDetail = obj.get(0);
            PayServicer payServicer = payServicerService.find(payDetail.getPayId());
            String supplierName = paymentSupplierService.find(payServicer.getReviewId()).getSupplierContract().getLiable().getUsername();

            // 1.保存付款记录
            for (PayDetails p : obj) {
                // 1:PayServicer 2:PayShareProfit 3:PayGuider 4:PaySettlement 5:PayDeposit
                p.setSort(1);
                p.setOperator(hyAdmin.getName());
                payDetailsService.save(p);

                // 付款方式为欠款付款
                if (p.getPayMethod() == 7) {
                    List<Filter> filters = new ArrayList<>();
                    filters.add(Filter.eq("supplierName", supplierName));
                    List<ReceiptTotalServicer> receiptTotalServicers = receiptTotalServicerService.findList(null, filters, null);
                    BigDecimal balance = new BigDecimal(0);
                    balance = balance.add(receiptTotalServicers.get(0).getBalance());
                    balance = balance.add(p.getAmount());

                    //如果有欠款的话 肯定有相应供应商的余额
                    ReceiptTotalServicer receiptTotalServicer = receiptTotalServicers.get(0);
                    receiptTotalServicer.setSupplierName(supplierName);
                    receiptTotalServicer.setBalance(balance);
                    receiptTotalServicerService.update(receiptTotalServicer);

                    //收支明细表
                    ReceiptServicer receiptServicer = new ReceiptServicer();
                    receiptServicer.setAmount(p.getAmount());
                    receiptServicer.setDate(payDetail.getDate());
                    receiptServicer.setOperator(payDetail.getOperator());
                    receiptServicer.setOrderOrPayServicerId(payServicer.getId());
                    receiptServicer.setSupplierName(supplierName);
                    receiptServicer.setState(1);
                    receiptServicer.setBalance(balance);
                    receiptServicerService.save(receiptServicer);
                }
            }

            // 2.修改付款状态
            // 1:已付款
            payServicer.setHasPaid(1);
            // 付款日期
            payServicer.setPayDate(payDetail.getDate());
            // 付款人
            payServicer.setPayer(payDetail.getOperator());
            payServicerService.update(payServicer);

            // 3.更新PaymentSupplier的付款状态
            PaymentSupplier paymentSupplier = paymentSupplierService.find(payServicer.getReviewId());
            paymentSupplier.setPayDate(new Date());
            paymentSupplier.setStatus(3);
            paymentSupplierService.update(paymentSupplier);

            // 更新结算状态
            List<Filter> filters = new ArrayList<>();
            filters.add(Filter.eq("paymentLineId", paymentSupplier.getId()));
            List<PayablesLineItem> payablesLineItems = payablesLineItemService.findList(null, filters, null);
            for (PayablesLineItem payablesLineItem : payablesLineItems) {
                HyOrder hyOrder = payablesLineItem.getHyOrder();
                hyOrder.setIfjiesuan(true);
                hyOrder.setJiesuantime(payServicer.getPayDate());
                hyOrderService.update(hyOrder);
            }

            j.setSuccess(true);
            j.setMsg("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            j.setSuccess(false);
            j.setMsg("操作失败");
        }
        return j;
    }

    /**
     * 待付 - 旅游元素供应商尾款 - 做付款
     */
    @RequestMapping("/payElements/update")
    @ResponseBody
    public Json payElementsDetailUpate(@RequestBody List<PayDetails> obj, HttpSession session) {
        Json j = new Json();
        try {
            String username = (String) session.getAttribute(CommonAttributes.Principal);
            HyAdmin hyAdmin = hyAdminService.find(username);

            // 1.保存付款记录
            for (PayDetails p : obj) {
                p.setSort(1);// 1:PayServicer 2:PayShareProfit 3:PayGuider
                // 4:PaySettlement 5:PayDeposit
                p.setOperator(hyAdmin.getName());
                payDetailsService.save(p);
            }

            // 2.修改付款状态
            PayDetails payDetail = obj.get(0);
            PayServicer payServicer = payServicerService.find(payDetail.getPayId());
            payServicer.setHasPaid(1); // 1:已付款
            payServicer.setPayDate(payDetail.getDate()); // 付款日期
            payServicer.setPayer(payDetail.getOperator()); // 付款人
            payServicerService.update(payServicer);

            // 修改BalanceDueApply的付款状态
            Long id = payServicer.getReviewId();
            BalanceDueApply balanceDueApply = balanceDueApplyService.find(id);
            balanceDueApply.setPayDate(new Date());
            balanceDueApply.setStatus(3); // 3:已通过-已付
            balanceDueApplyService.update(balanceDueApply);

            // 修改BalanceDueApplyItem 和 HyPayablesElement
            List<Filter> filters = new ArrayList<>();
            filters.add(Filter.eq("balanceDueApplyId", balanceDueApply.getId()));
            List<BalanceDueApplyItem> list = balanceDueApplyItemService.findList(null, filters, null);
            BigDecimal money_use_pre_pay_sum = new BigDecimal(0);
            BigDecimal money_use_pre_save_sum = new BigDecimal(0);
            Long groupId = 0L;
            for (BalanceDueApplyItem bDueApplyItem : list) {
                groupId = bDueApplyItem.getGroupId();
                // 1已付
                bDueApplyItem.setPayStatus(1);
                balanceDueApplyItemService.update(bDueApplyItem);
                money_use_pre_pay_sum = money_use_pre_pay_sum.add(bDueApplyItem.getUsePrePay());
                money_use_pre_save_sum = money_use_pre_save_sum.add(bDueApplyItem.getPayMoney());
                // 提交申请时，已付和欠付已经修改  20180714 xyy

//				HyPayablesElement  hyPayablesElement = hyPayablesElementService.find(bDueApplyItem.getHyPayablesElementId());
//				BigDecimal sum = bDueApplyItem.getUsePrePay().add(bDueApplyItem.getPayMoney());
//				hyPayablesElement.setPaid(hyPayablesElement.getPaid().add(sum));
//				hyPayablesElement.setDebt(hyPayablesElement.getDebt().subtract(sum));
//				
//				hyPayablesElementService.update(hyPayablesElement);
            }

            //修改分公司充值余额
            Department department = departmentService.find(payServicer.getDepartmentId());
            boolean ifBranch = department.getHyDepartmentModel().getName().contains("分公司");
            if (ifBranch) {
                Department branch = department;
                while (!branch.getIsCompany()) {
                    //如果当前部门不是公司，就找到他的父部门继续判断。
                    branch = branch.getHyDepartment();
                }
                Long branchId = branch.getId();
                filters.clear();
                filters.add(Filter.eq("branchId", branchId));
                BranchBalance branchBalance = branchBalanceService.findList(null, filters, null).get(0);
                BigDecimal balance = branchBalance.getBranchBalance().subtract(money_use_pre_save_sum);
                branchBalance.setBranchBalance(balance);
                branchBalanceService.update(branchBalance);

                //生成分公司余额使用
                BranchPreSave branchPreSave = new BranchPreSave();
                branchPreSave.setBranchId(branchId);
                branchPreSave.setBranchName(departmentService.find(branchId).getName());
                branchPreSave.setDate(new Date());
                branchPreSave.setType(4);
                branchPreSave.setAmount(money_use_pre_save_sum);
                branchPreSave.setPreSaveBalance(branchBalance.getBranchBalance());
                branchPreSave.setDepartmentName(department.getFullName());
                branchPreSave.setRemark("供应商尾款使用");
                branchPreSaveService.save(branchPreSave);
            }

            // 生成预付款的冲抵记录
            if (!money_use_pre_pay_sum.equals(new BigDecimal(0))) {
                filters.clear();
                filters.add(Filter.eq("supplierElementId", balanceDueApply.getSupplierElement().getId()));
                filters.add(Filter.eq("departmentId", balanceDueApply.getInstitutionId()));
                List<BranchPrePay> list2 = branchPrePayService.findList(null, filters, null);
                if (!list2.isEmpty() && list2.size() != 0) {
                    BranchPrePay branchPrePay = list2.get(0);

                    BranchPrePayDetail branchPrePayDetail = new BranchPrePayDetail();
                    branchPrePayDetail.setType(2); // 1 预付给供应商(充值)  2使用预付款(冲抵)
                    branchPrePayDetail.setBranchPrePayId(branchPrePay.getId());
                    branchPrePayDetail.setDate(balanceDueApply.getPayDate());
                    branchPrePayDetail.setAmount(money_use_pre_pay_sum);
                    branchPrePayDetail.setAppliname(balanceDueApply.getOperator().getUsername());
                    branchPrePayDetail.setPrePayBalance(branchPrePay.getPrePayBalance());
                    branchPrePayDetail.setGroupId(groupId);
                    branchPrePayDetailService.save(branchPrePayDetail);
                }

            }

            j.setSuccess(true);
            j.setMsg("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            j.setSuccess(false);
            j.setMsg("操作失败");
        }
        return j;
    }

    /**
     * 待付 - 向酒店/门票/酒加景供应商付款 - 做付款
     */
    @RequestMapping("/ticketPay/update")
    @ResponseBody
    public Json ticketPayDetailUpdate(@RequestBody List<PayDetails> obj, HttpSession session) {

        Json j = new Json();
        try {
            String username = (String) session.getAttribute(CommonAttributes.Principal);
            HyAdmin hyAdmin = hyAdminService.find(username);
            // 1.保存付款记录
            for (PayDetails p : obj) {
                p.setSort(1); // 1:PayServicer 2:PayShareProfit 3:PayGuider
                // 4:PaySettlement 5:PayDeposit
                p.setOperator(hyAdmin.getName());
                payDetailsService.save(p);
            }

            // 2.修改付款状态
            PayDetails payDetail = obj.get(0);
            PayServicer payServicer = payServicerService.find(payDetail.getPayId());
            payServicer.setHasPaid(1); // 1:已付款
            payServicer.setPayDate(payDetail.getDate()); // 付款日期
            payServicer.setPayer(payDetail.getOperator()); // 付款人
            payServicerService.update(payServicer);

            // 3.修改HyPaymentSupplier的付款状态
            HyPaymentSupplier hyPaymentSupplier = hyPaymentSupplierService.find(payServicer.getReviewId());
            hyPaymentSupplier.setPayStatus(true);
            hyPaymentSupplierService.update(hyPaymentSupplier);

            j.setSuccess(true);
            j.setMsg("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            j.setSuccess(false);
            j.setMsg("操作失败");
        }
        return j;
    }

    /**
     * 待付 - 江泰预充值- 做付款
     */
    @RequestMapping("/jtaiPresave/update")
    @ResponseBody
    public Json jtaipresaveDetailUpdate(@RequestBody List<PayDetails> obj, HttpSession session) {
        Json j = new Json();

        try {
            String username = (String) session.getAttribute(CommonAttributes.Principal);
            HyAdmin hyAdmin = hyAdminService.find(username);
            // 1.保存付款记录
            for (PayDetails p : obj) {
                p.setSort(1); // 1:PayServicer 2:PayShareProfit 3:PayGuider
                // 4:PaySettlement 5:PayDeposit
                p.setOperator(hyAdmin.getName());
                payDetailsService.save(p);
            }

            // 2.修改付款状态
            PayDetails payDetail = obj.get(0);
            PayServicer payServicer = payServicerService.find(payDetail.getPayId());
            payServicer.setHasPaid(1); // 1:已付款
            payServicer.setPayDate(payDetail.getDate()); // 付款日期
            payServicer.setPayer(hyAdmin.getName());// 付款人
            payServicerService.update(payServicer);

            j.setSuccess(true);
            j.setMsg("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            j.setSuccess(false);
            j.setMsg("操作失败");
        }
        return j;
    }

    /**
     * 待付 - 分公司分成 - 做付款
     */
    @RequestMapping("/profitShareConfirm/update")
    @ResponseBody
    public Json profitshareconfirmDetailUpdate(@RequestBody List<PayDetails> obj, HttpSession session) {
        Json j = new Json();

        try {

            String username = (String) session.getAttribute(CommonAttributes.Principal);
            HyAdmin hyAdmin = hyAdminService.find(username);
            // 1.保存付款记录
            for (PayDetails p : obj) {
                p.setSort(2);// 1:PayServicer 2:PayShareProfit 3:PayGuider
                // 4:PaySettlement 5:PayDeposit
                p.setOperator(hyAdmin.getName());
                payDetailsService.save(p);
            }

            // 2.修改付款状态
            PayDetails payDetail = obj.get(0);
            PayShareProfit payShareProfit = payShareProfitService.find(payDetail.getPayId());
            payShareProfit.setHasPaid(1); // 1:已付款
            payShareProfit.setPayDate(payDetail.getDate()); // 付款日期
            payShareProfit.setPayer(payDetail.getOperator()); // 付款人
            payShareProfitService.update(payShareProfit);

            //3.修改分成确认单状态
            List<Filter> filters = new ArrayList<>();
            filters.add(Filter.eq("confirmNum", payShareProfit.getConfirmCode()));
            List<ProfitShareConfirm> profitShareConfirms = profitShareConfirmService.findList(null, filters, null);
            if (profitShareConfirms == null) {
                j.setSuccess(true);
                j.setMsg("此数据为脏数据");
            } else {
                ProfitShareConfirm profitShareConfirm = profitShareConfirms.get(0);
                profitShareConfirm.setState(3);
                profitShareConfirmService.update(profitShareConfirm);
                j.setSuccess(true);
                j.setMsg("操作成功");

            }
        } catch (Exception e) {
            e.printStackTrace();
            j.setSuccess(false);
            j.setMsg("操作失败");
        }
        return j;
    }

    /**
     * 待付 - 导游报账应付款 - 做付款
     */
    @RequestMapping("/guideReimbursement/update")
    @ResponseBody
    public Json guidereimbursementDetailUpdate(@RequestBody List<PayDetails> obj, HttpSession session) {

        Json j = new Json();

        try {
            String username = (String) session.getAttribute(CommonAttributes.Principal);
            HyAdmin hyAdmin = hyAdminService.find(username);

            // 1.保存付款记录
            for (PayDetails p : obj) {
                p.setSort(3); //// 1:PayServicer 2:PayShareProfit 3:PayGuider
                //// 4:PaySettlement 5:PayDeposit
                p.setOperator(hyAdmin.getName());
                payDetailsService.save(p);
            }

            // 2.修改付款状态
            PayDetails payDetail = obj.get(0);
            PayGuider payGuider = payGuiderService.find(payDetail.getPayId());
            payGuider.setHasPaid(1); // 1:已付款
            payGuider.setPayDate(payDetail.getDate()); // 付款日期
            payGuider.setPayer(payDetail.getOperator()); // 付款人
            payGuiderService.update(payGuider);

            j.setSuccess(true);
            j.setMsg("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            j.setSuccess(false);
            j.setMsg("操作失败");
        }
        return j;
    }

    /**
     * 待付 - 导游费用 - 做付款
     */
    @RequestMapping("/guideFee/update")
    @ResponseBody

    public Json guidefeeDetailUpdate(@RequestBody List<PayDetails> obj, HttpSession session) {

        Json j = new Json();

        try {
            String username = (String) session.getAttribute(CommonAttributes.Principal);
            HyAdmin hyAdmin = hyAdminService.find(username);

            // 1.保存付款记录
            for (PayDetails p : obj) {
                p.setSort(3); //// 1:PayServicer 2:PayShareProfit 3:PayGuider
                //// 4:PaySettlement 5:PayDeposit
                p.setOperator(hyAdmin.getName());
                payDetailsService.save(p);
            }

            // 2.修改付款状态
            PayDetails payDetail = obj.get(0);
            PayGuider payGuider = payGuiderService.find(payDetail.getPayId());
            payGuider.setHasPaid(1); // 1:已付款
            payGuider.setPayDate(payDetail.getDate()); // 付款日期
            payGuider.setPayer(payDetail.getOperator()); // 付款人
            payGuiderService.update(payGuider);

            //修改结算表状态
            GuideSettlement guideSettlement = guideSettlementService.find(payGuider.getSettlementId());
            guideSettlement.setPaymenttime(payDetail.getDate());
            guideSettlementService.save(guideSettlement);

            //修改派遣表状态
            List<Filter> filters = new ArrayList<>();
            filters.add(Filter.eq("settlementId", payGuider.getSettlementId()));
            List<GuideSettlementDetail> guideSettlementDetails = guideSettlementDetailService.findList(null, filters, null);
            for (GuideSettlementDetail gui : guideSettlementDetails) {
                GuideAssignment guideAssignment = guideAssignmentService.find(gui.getPaiqianId());
                guideAssignment.setBalanceStatus(1);
                guideAssignmentService.update(guideAssignment);
            }

            j.setSuccess(true);
            j.setMsg("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            j.setSuccess(false);
            j.setMsg("操作失败");
        }
        return j;
    }

    /**
     * 待付 - 分公司产品中心结算 - 做付款
     */
    @RequestMapping("/settlement/update")
    @ResponseBody
    public Json settlementDetailUpdate(@RequestBody List<PayDetails> obj, HttpSession session) {
        Json j = new Json();

        try {
            String username = (String) session.getAttribute(CommonAttributes.Principal);
            HyAdmin hyAdmin = hyAdminService.find(username);

            // 1.保存付款记录
            for (PayDetails p : obj) {
                p.setSort(4);//1:PayServicer 2:PayShareProfit 3:PayGuider 4:PaySettlement 5:PayDeposit
                p.setOperator(hyAdmin.getName());
                payDetailsService.save(p);
            }

            // 2.修改付款状态
            PayDetails payDetail = obj.get(0);
            PaySettlement paySettlement = paySettlementService.find(payDetail.getPayId());
            paySettlement.setHasPaid(1); // 1:已付款
            paySettlement.setPayDate(payDetail.getDate()); // 付款日期
            paySettlement.setPayer(payDetail.getOperator()); // 付款人
            paySettlementService.update(paySettlement);

            j.setSuccess(true);
            j.setMsg("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            j.setSuccess(false);
            j.setMsg("操作失败");
        }
        return j;
    }

    /**
     * 待付 - 退保证金 - 门店 - 做付
     */
    @RequestMapping("/payDepositStore/update")
    @ResponseBody
    public Json payDepositStoreDetailUpdate(@RequestBody List<PayDetails> list, HttpSession session) {
        Json j = new Json();

        try {
            String username = (String) session.getAttribute(CommonAttributes.Principal);
            HyAdmin hyAdmin = hyAdminService.find(username);
            // 1.保存付款记录
            for (PayDetails p : list) {
                p.setOperator(hyAdmin.getName());
                payDetailsService.save(p);
            }

            // 2.修改付款状态
            PayDetails payDetail = list.get(0);
            PayDeposit payDeposit = payDepositService.find(payDetail.getPayId());
            payDeposit.setHasPaid(1); // 1:已付款
            payDeposit.setPayDate(payDetail.getDate()); // 付款日期
            payDeposit.setPayer(payDetail.getOperator()); // 付款人
            payDepositService.update(payDeposit);

            // 3.财务中心-门店保证金表增加数据
            DepositStore depositStore = new DepositStore();
            depositStore.setStoreName(payDeposit.getInstitution());
            depositStore.setType(2); // 1:交纳 2:退还
            depositStore.setDate(payDetail.getDate());
            depositStore.setAmount(payDeposit.getAmount());
            // depositStore.setRemark();
            depositStoreService.save(depositStore);

            j.setSuccess(true);
            j.setMsg("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            j.setSuccess(false);
            j.setMsg("操作失败");
        }
        return j;
    }

    /**
     * 待付款 - 退保证金 - 供应商 - 做付款
     */
    @RequestMapping("/payDepositServicer/update")
    @ResponseBody
    public Json payDepositServicerDetailUpdate(@RequestBody List<PayDetails> obj, HttpSession session) {
        Json j = new Json();

        try {

            String username = (String) session.getAttribute(CommonAttributes.Principal);
            HyAdmin hyAdmin = hyAdminService.find(username);
            // 1.保存付款记录
            for (PayDetails p : obj) {
                p.setOperator(hyAdmin.getName());
                payDetailsService.save(p);
            }

            // 2.修改付款状态
            PayDetails payDetail = obj.get(0);
            Long id = payDetail.getPayId();
            PayDeposit payDeposit = payDepositService.find(id);
            payDeposit.setHasPaid(1); // 1:已付款
            payDeposit.setPayDate(payDetail.getDate()); // 付款日期
            // 对日期进行比较，将最后的付款日期作为最终的付款日期
            payDeposit.setPayer(payDetail.getOperator()); // 付款人
            payDepositService.update(payDeposit);

            // 3.在财务中心-供应商保证金中增加数据
            DepositSupplier depositSupplier = new DepositSupplier();
            depositSupplier.setSupplierName(payDeposit.getInstitution());
            depositSupplier.setContractCode(payDeposit.getContractCode());
            depositSupplier.setAmount(payDeposit.getAmount());
            depositSupplier.setOweAmount(new BigDecimal("0")); // 退供应商押金 欠退金额？
            // depositSupplier.setPayTime(receiptDepositServicer.getDate());
            // 申请日期直接作为交纳日期？
            depositSupplier.setRefundTime(payDeposit.getPayDate());
            depositSupplier.setContractStatus(3); // 1:正常 2:变更续签 3:退出
            depositSupplier.setRemark(payDeposit.getRemark());
            depositSupplierService.save(depositSupplier);

            // 4.修改供应商合同状态 --returnDeposit字段设置为0--将负责人账号置为无效
            // List<Filter> filters = new ArrayList<>();
            // filters.add(new Filter("",Operator.eq,
            // payDeposit.getContractCode()));
            // List<HySupplierContract> list =
            // hySupplierContractService.findList(null, filters, null);
            // HySupplierContract c = list.get(0);
            // c.setContractStatus(ContractStatus.tuichu);
            // c.setReturnDeposit(BigDecimal.ZERO);
            // HyAdmin liable = c.getLiable();
            // hyAdminService.invalidateAdmin(liable.getUsername());
            // hySupplierContractService.update(c);//更新合同

            j.setSuccess(true);
            j.setMsg("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            j.setSuccess(false);
            j.setMsg("操作失败");
        }
        return j;
    }

    /**
     * 待付款 - 供应商消团 - 做付款
     */
    @RequestMapping("/revoked/update")
    @ResponseBody
    public Json revokedDetailUpdate(@RequestBody List<RefundRecords> obj) {
        Json j = new Json();

        try {

            // 1.保存付款记录
            for (RefundRecords r : obj) {
                refundRecordsService.save(r);
            }

            // 2.修改付款状态
            RefundRecords r = obj.get(0);
            RefundInfo refundInfo = refundInfoService.find(r.getRefundInfoId());
            refundInfo.setState(1); // 1:已付款
            refundInfo.setPayDate(r.getPayDate());
            refundInfo.setPayer(r.getPayer());
            refundInfoService.update(refundInfo);

            j.setSuccess(true);
            j.setMsg("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            j.setSuccess(false);
            j.setMsg("操作失败");
        }
        return j;
    }

    /**
     * 待付款 - 游客退团 - 做付款
     */
    @RequestMapping("/refund/update")
    @ResponseBody
    public Json refundDetailUpdate(@RequestBody List<RefundRecords> obj) {

        Json j = new Json();

        try {
            // 1.保存付款记录
            for (RefundRecords r : obj) {
                refundRecordsService.save(r);
            }

            // 2.修改付款状态
            RefundRecords r = obj.get(0);
            RefundInfo refundInfo = refundInfoService.find(r.getRefundInfoId());
            refundInfo.setState(1); // 1:已付款
            refundInfo.setPayDate(r.getPayDate());
            refundInfo.setPayer(r.getPayer());
            refundInfoService.update(refundInfo);

            j.setSuccess(true);
            j.setMsg("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            j.setSuccess(false);
            j.setMsg("操作失败");
        }
        return j;
    }

    /**
     * 待付款 - 供应商驳回订单 - 做付款
     */
    @RequestMapping("/servicerDismiss/update")
    @ResponseBody
    public Json servicerdismissDetailUpdate(@RequestBody List<RefundRecords> obj) {

        Json j = new Json();

        try {

            // 1.保存付款记录
            for (RefundRecords r : obj) {
                refundRecordsService.save(r);
            }

            // 2.修改付款状态
            RefundRecords r = obj.get(0);
            RefundInfo refundInfo = refundInfoService.find(r.getRefundInfoId());
            refundInfo.setState(1); // 1:已付款
            refundInfo.setPayDate(r.getPayDate());
            refundInfo.setPayer(r.getPayer());
            refundInfoService.update(refundInfo);

            j.setSuccess(true);
            j.setMsg("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            j.setSuccess(false);
            j.setMsg("操作失败");
        }
        return j;
    }

    /**
     * 待付款 - 门店驳回订单 - 做付款
     */
    @RequestMapping("/storeDismiss/update")
    @ResponseBody
    public Json storedismissDetailUpdate(@RequestBody List<RefundRecords> obj) {

        Json j = new Json();

        try {
            // 1.保存付款记录
            for (RefundRecords r : obj) {
                refundRecordsService.save(r);
            }

            // 2.修改付款状态
            RefundRecords r = obj.get(0);
            RefundInfo refundInfo = refundInfoService.find(r.getRefundInfoId());
            refundInfo.setState(1); // 1:已付款
            refundInfo.setPayDate(r.getPayDate());
            refundInfo.setPayer(r.getPayer());
            refundInfoService.update(refundInfo);

            j.setSuccess(true);
            j.setMsg("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            j.setSuccess(false);
            j.setMsg("操作失败");
        }
        return j;
    }

    /**
     * 待付款 - 电子门票退款 - 做付款
     */
    @RequestMapping("/ticketRefundOnLine/update")
    @ResponseBody
    public Json ticketrefundonlineDetailUpdate(@RequestBody List<RefundRecords> obj) {

        Json j = new Json();

        try {

            // 1.保存付款记录
            for (RefundRecords r : obj) {
                refundRecordsService.save(r);
            }

            // 2.修改付款状态
            RefundRecords r = obj.get(0);
            RefundInfo refundInfo = refundInfoService.find(r.getRefundInfoId());
            refundInfo.setState(1); // 1:已付款
            refundInfo.setPayDate(r.getPayDate());
            refundInfo.setPayer(r.getPayer());
            refundInfoService.update(refundInfo);

            j.setSuccess(true);
            j.setMsg("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            j.setSuccess(false);
            j.setMsg("操作失败");
        }
        return j;
    }

    /**
     * 待付款 - 签证退款 - 做付款
     */
    @RequestMapping("/visaRefundOnLine/update")
    @ResponseBody
    public Json visarefundonlineDetailUpdate(@RequestBody List<RefundRecords> obj) {

        Json j = new Json();

        try {
            // 1.保存付款记录
            for (RefundRecords r : obj) {
                refundRecordsService.save(r);
            }

            // 2.修改付款状态
            RefundRecords r = obj.get(0);
            RefundInfo refundInfo = refundInfoService.find(r.getRefundInfoId());
            refundInfo.setState(1); // 1:已付款
            refundInfo.setPayDate(r.getPayDate());
            refundInfo.setPayer(r.getPayer());
            refundInfoService.update(refundInfo);

            j.setSuccess(true);
            j.setMsg("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            j.setSuccess(false);
            j.setMsg("操作失败");
        }
        return j;
    }

    /**
     * 待付款 - 酒店/酒加景退款 -做付款
     */
    @RequestMapping("/hotelRefundOnLine/update")
    @ResponseBody
    public Json hotelrefundonlineDetailUpdate(@RequestBody List<RefundRecords> obj) {

        Json j = new Json();

        try {

            // 1.保存付款记录
            for (RefundRecords r : obj) {
                refundRecordsService.save(r);
            }

            // 2.修改付款状态
            RefundRecords r = obj.get(0);
            RefundInfo refundInfo = refundInfoService.find(r.getRefundInfoId());
            refundInfo.setState(1); // 1:已付款
            refundInfo.setPayDate(r.getPayDate());
            refundInfo.setPayer(r.getPayer());
            refundInfoService.update(refundInfo);

            j.setSuccess(true);
            j.setMsg("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            j.setSuccess(false);
            j.setMsg("操作失败");
        }
        return j;
    }

    /**
     * 待付款 - 微商后返 - 做付款
     */
    @RequestMapping("/profitShareWechatBusiness/update")
    @ResponseBody
    public Json profitshareWechatbusinessDetailUpdate(@RequestBody List<PayDetails> obj, HttpSession session) {
        Json j = new Json();

        try {
            String username = (String) session.getAttribute(CommonAttributes.Principal);
            HyAdmin hyAdmin = hyAdminService.find(username);

            // 1.保存付款记录
            for (PayDetails p : obj) {
                p.setOperator(hyAdmin.getName());
                payDetailsService.save(p);
            }

            // 2.修改付款状态
            PayDetails payDetail = obj.get(0);
            PayGuider payGuider = payGuiderService.find(payDetail.getPayId());
            payGuider.setHasPaid(1); // 1:已付款
            payGuider.setPayDate(payDetail.getDate()); // 付款日期
            payGuider.setPayer(payDetail.getOperator()); // 付款人
            payGuiderService.update(payGuider);

            //修改结算表状态
            GuideSettlement guideSettlement = guideSettlementService.find(payGuider.getSettlementId());
            guideSettlement.setPaymenttime(payDetail.getDate());
            guideSettlementService.save(guideSettlement);

            //修改派遣表状态
            List<Filter> filters = new ArrayList<>();
            filters.add(Filter.eq("settlementId", payGuider.getSettlementId()));
            List<GuideSettlementDetail> guideSettlementDetails = guideSettlementDetailService.findList(null, filters, null);
            for (GuideSettlementDetail gui : guideSettlementDetails) {
                GuideAssignment guideAssignment = guideAssignmentService.find(gui.getPaiqianId());
                guideAssignment.setBalanceStatus(1);
                guideAssignmentService.update(guideAssignment);
            }

            j.setSuccess(true);
            j.setMsg("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            j.setSuccess(false);
            j.setMsg("操作失败");
        }
        return j;
    }

    /**
     * 待付 - 提现金额 - 做付款
     */
    @RequestMapping("/withdraw_cash/update")
    @ResponseBody
    public Json withDrawCashUpdate(@RequestBody List<PayDetails> obj, HttpSession session) {
        Json j = new Json();
        String username = (String) session.getAttribute(CommonAttributes.Principal);
        HyAdmin hyAdmin = hyAdminService.find(username);

        try {
            // 1.保存付款记录
            for (PayDetails p : obj) {
                p.setSort(1);// 1:PayServicer 2:PayShareProfit 3:PayGuider
                // 4:PaySettlement 5:PayDeposit
                p.setOperator(hyAdmin.getName());
                payDetailsService.save(p);
            }

            // 2.修改付款状态
            PayDetails payDetail = obj.get(0);
            PayServicer payServicer = payServicerService.find(payDetail.getPayId());
            payServicer.setHasPaid(1); // 1:已付款
            payServicer.setPayDate(payDetail.getDate()); // 付款日期
            payServicer.setPayer(payDetail.getOperator()); // 付款人
            payServicerService.update(payServicer);

            // 修改BalanceDueApply的付款状态
            Long id = payServicer.getReviewId();

            WithDrawCash withDrawCash = withDrawCashService.find(id);
            withDrawCash.setStatus(2);
            ; // 2:已付款
            withDrawCash.setPayTime(new Date()); // 付款日期
            withDrawCash.setPayer(payDetail.getOperator()); // 付款人
            withDrawCashService.update(withDrawCash);

            j.setSuccess(true);
            j.setMsg("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            j.setSuccess(false);
            j.setMsg("操作失败");
        }
        return j;
    }

    /**
     * 待付 - 提现金额 - 做付款
     */
    @RequestMapping("/withdraw_cash_subcompany/update")
    @ResponseBody
    public Json withDrawCashSubCompanyUpdate(@RequestBody List<PayDetails> obj, HttpSession session) {
        Json j = new Json();
        String username = (String) session.getAttribute(CommonAttributes.Principal);
        HyAdmin hyAdmin = hyAdminService.find(username);

        try {
            // 1.保存付款记录
            for (PayDetails p : obj) {
                p.setSort(1);// 1:PayServicer 2:PayShareProfit 3:PayGuider
                // 4:PaySettlement 5:PayDeposit
                p.setOperator(hyAdmin.getName());
                payDetailsService.save(p);
            }

            // 2.修改付款状态
            PayDetails payDetail = obj.get(0);
            PayServicer payServicer = payServicerService.find(payDetail.getPayId());
            payServicer.setHasPaid(1); // 1:已付款
            payServicer.setPayDate(payDetail.getDate()); // 付款日期
            payServicer.setPayer(payDetail.getOperator()); // 付款人
            payServicerService.update(payServicer);

            // 修改BalanceDueApply的付款状态
            Long id = payServicer.getReviewId();

            WithDrawCashSubCompany withDrawCash = withDrawCashSubCompanyService.find(id);
            withDrawCash.setStatus(2);
            ; // 2:已付款
            withDrawCash.setPayTime(new Date()); // 付款日期
            withDrawCash.setPayer(payDetail.getOperator()); // 付款人
            withDrawCashSubCompanyService.update(withDrawCash);

            j.setSuccess(true);
            j.setMsg("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            j.setSuccess(false);
            j.setMsg("操作失败");
        }
        return j;
    }

    /***************************************************************************************************/
    /***************************************************************************************************/
    /***************************************************************************************************/

    /**
     * 进入做付款详情页，获取所有的支付方式
     */
    @RequestMapping("/account/view")
    @ResponseBody
    public Json getAllKindAccount2(HttpSession session) {
        Json json = new Json();
        List<HashMap<String, Object>> transferAccountList = new LinkedList<>();
        List<HashMap<String, Object>> aliPayAccountList = new LinkedList<>();
        List<HashMap<String, Object>> wechatPayAccountList = new LinkedList<>();
        try {
            HyCompany hyCompany = getCompanyBySession(session);
            List<Filter> filters = new LinkedList<>();
            filters.add(Filter.eq("hyCompany", hyCompany));
            //正常状态的帐号
            filters.add(Filter.eq("bankListStatus", 1));
            List<BankList> list = bankListService.findList(null, filters, null);

            for (BankList bankList : list) {
                HashMap<String, Object> map = new HashMap<>(1);
                map.put("alias", bankList.getAlias());
                map.put("id", bankList.getId());
                if (bankList.getType() == BankList.BankType.bank) {
                    transferAccountList.add(map);
                } else if (bankList.getType() == BankList.BankType.alipay) {
                    aliPayAccountList.add(map);
                } else if (bankList.getType() == BankList.BankType.wechatpay) {
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
        filters.add(Filter.eq("hyDepartment", pDepartment.getId()));
        List<HyCompany> list = hyCompanyService.findList(null, filters, null);
        HyCompany hyCompany = list.get(0);
        return hyCompany;
    }
}
