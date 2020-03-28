package com.hongyu.controller.hzj03.incomeandexpenses;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.Filter;

import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.Filter.Operator;
import com.hongyu.entity.BankList;
import com.hongyu.entity.Guide;
import com.hongyu.entity.PayDeposit;
import com.hongyu.entity.PayDetails;
import com.hongyu.entity.PayGuider;
import com.hongyu.entity.PayServicer;
import com.hongyu.entity.PaySettlement;
import com.hongyu.entity.PayShareProfit;
import com.hongyu.entity.ProfitShareStore;
import com.hongyu.entity.ProfitShareWechatBusiness;
import com.hongyu.entity.RefundInfo;
import com.hongyu.entity.RefundRecords;
import com.hongyu.entitycustom.PaidCustom;
import com.hongyu.service.BankListService;
import com.hongyu.service.GuideService;
import com.hongyu.service.PayDepositService;
import com.hongyu.service.PayDetailsService;
import com.hongyu.service.PayGuiderService;
import com.hongyu.service.PayServicerService;
import com.hongyu.service.PaySettlementService;
import com.hongyu.service.PayShareProfitService;
import com.hongyu.service.RefundInfoService;
import com.hongyu.service.RefundRecordsService;

/**
 * 付款 - 已付款
 */
@Controller
@RequestMapping("/admin/paid")
public class PaidController {

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

    @Resource(name = "guideServiceImpl")
    private GuideService guideService;

    /**
     * 列表数据
     */
    @RequestMapping("/datagrid/view")
    @ResponseBody
    public Json getDataGrid(Pageable pageable, String startTime, String endTime, String name, Integer type) {
        Json j = new Json();
        // 6:rows total pageNumber totalPage pageSize moneySum
        HashMap<String, Object> obj = new HashMap<>();

        // 按时间列表倒序
        List<Order> orders = new ArrayList<>();
        orders.add(Order.desc("id"));
        pageable.setOrders(orders);
        try {
            // 时间范围条件
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            List<Filter> filters = new ArrayList<>();
            if (StringUtils.isNotEmpty(startTime)) {
                filters.add(new Filter("payDate", Operator.ge, sdf.parse(startTime.substring(0, 10) + " " + "00:00:00")));
            }
            if (StringUtils.isNotEmpty(endTime)) {
                filters.add(new Filter("payDate", Operator.le, sdf.parse(endTime.substring(0, 10) + " " + "23:59:59")));
            }
            pageable.setFilters(filters);
            if (MappingUtil.PAY_SERVICER_TYPE_SET.containsKey(type)) {
                PayServicer queryParam = new PayServicer();
                // 0:未付 1:已付
                queryParam.setHasPaid(1);
                // 供应商名称
                queryParam.setServicerName(name);
                queryParam.setType(MappingUtil.PAY_SERVICER_TYPE_SET.get(type));
                Page<PayServicer> page = payServicerService.findPage(pageable, queryParam);
                List<PaidCustom> res = new ArrayList<>();
                for (PayServicer payServicer : page.getRows()) {
                    PaidCustom p = new PaidCustom();
                    p.setName(payServicer.getServicerName());
                    p.setId(payServicer.getId());
                    p.setAmount(payServicer.getAmount());
                    p.setDate(payServicer.getPayDate());
                    p.setPayer(payServicer.getPayer());
                    p.setType(type);
                    res.add(p);
                }
                obj.put("rows", res);
                obj.put("total", page.getTotal());
                obj.put("pageNumber", page.getPageNumber());
                obj.put("totalPage", page.getTotalPages());
                obj.put("pageSize", page.getPageSize());
                Object moneySum = payServicerService.getSingleResultByNativeQuery("SELECT SUM(amount) FROM hy_pay_servicer WHERE has_paid = 1 AND type = " + MappingUtil.PAY_SERVICER_TYPE_SET.get(type));
                obj.put("moneySum", moneySum);
            } else if (MappingUtil.PAY_SHAREPROFIT_TYPE_SET.containsKey(type)) {
                PayShareProfit queryParm = new PayShareProfit();
                // 1:已付
                queryParm.setHasPaid(1);
                queryParm.setClient(name);
                queryParm.setType(MappingUtil.PAY_SHAREPROFIT_TYPE_SET.get(type));
                Page<PayShareProfit> page = payShareProfitService.findPage(pageable, queryParm);
                List<PaidCustom> res = new ArrayList<>();
                for (PayShareProfit payShareProfit : page.getRows()) {
                    PaidCustom p = new PaidCustom();
                    p.setName(payShareProfit.getClient());
                    p.setId(payShareProfit.getId());
                    p.setAmount(payShareProfit.getAmount());
                    p.setDate(payShareProfit.getPayDate());
                    p.setPayer(payShareProfit.getPayer());
                    p.setType(type);
                    res.add(p);
                }
                obj.put("rows", res);
                obj.put("total", page.getTotal());
                obj.put("pageNumber", page.getPageNumber());
                obj.put("totalPage", page.getTotalPages());
                obj.put("pageSize", page.getPageSize());
                Object moneySum = payShareProfitService.getSingleResultByNativeQuery("SELECT SUM(amount) FROM hy_pay_share_profit WHERE has_paid = 1 AND type = " + MappingUtil.PAY_SHAREPROFIT_TYPE_SET.get(type));
                obj.put("moneySum", moneySum);
            } else if (MappingUtil.PAY_GUIDER_TYPE_SET.containsKey(type)) {
                PayGuider queryParm = new PayGuider();
                // 1:已付
                queryParm.setHasPaid(1);
                // 导游姓名
                queryParm.setGuider(name);
                queryParm.setType(MappingUtil.PAY_GUIDER_TYPE_SET.get(type));
                Page<PayGuider> page = payGuiderService.findPage(pageable, queryParm);
                List<PaidCustom> res = new ArrayList<>();
                for (PayGuider payGuider : page.getRows()) {
                    PaidCustom p = new PaidCustom();
                    p.setName(payGuider.getGuider());
                    p.setId(payGuider.getId());
                    p.setAmount(payGuider.getAmount());
                    p.setDate(payGuider.getPayDate());
                    p.setPayer(payGuider.getPayer());
                    p.setType(type);
                    res.add(p);
                }
                obj.put("rows", res);
                obj.put("total", page.getTotal());
                obj.put("pageNumber", page.getPageNumber());
                obj.put("totalPage", page.getTotalPages());
                obj.put("pageSize", page.getPageSize());
                Object moneySum = payGuiderService.getSingleResultByNativeQuery("SELECT SUM(amount) FROM hy_pay_guider WHERE has_paid = 1 AND type = " + MappingUtil.PAY_GUIDER_TYPE_SET.get(type));
                obj.put("moneySum", moneySum);
            } else if (MappingUtil.PAY_SETTLEMENT_TYPE_SET.containsKey(type)) {
                PaySettlement queryParm = new PaySettlement();
                // 1:已付
                queryParm.setHasPaid(1);
                // 导游姓名
                queryParm.setBranchName(name);
                Page<PaySettlement> page = paySettlementService.findPage(pageable, queryParm);
                List<PaidCustom> res = new ArrayList<>();
                for (PaySettlement paySettlement : page.getRows()) {
                    PaidCustom p = new PaidCustom();
                    p.setName(paySettlement.getBranchName());
                    p.setId(paySettlement.getId());
                    p.setAmount(paySettlement.getAmount());
                    p.setDate(paySettlement.getPayDate());
                    p.setPayer(paySettlement.getPayer());
                    p.setType(type);
                    res.add(p);
                }
                obj.put("rows", res);
                obj.put("total", page.getTotal());
                obj.put("pageNumber", page.getPageNumber());
                obj.put("totalPage", page.getTotalPages());
                obj.put("pageSize", page.getPageSize());
                Object moneySum = paySettlementService.getSingleResultByNativeQuery("SELECT SUM(amount) FROM hy_pay_settlement WHERE has_paid = 1");
                obj.put("moneySum", moneySum);
            } else if (MappingUtil.PAY_DEPOSIT_TYPE_SET.containsKey(type)) {
                PayDeposit queryParm = new PayDeposit();
                // 1:已付
                queryParm.setHasPaid(1);
                // 单位名称
                queryParm.setInstitution(name);
                queryParm.setDepositType(MappingUtil.PAY_DEPOSIT_TYPE_SET.get(type));
                Page<PayDeposit> page = payDepositService.findPage(pageable, queryParm);
                List<PaidCustom> res = new ArrayList<>();
                for (PayDeposit payDeposit : page.getRows()) {
                    PaidCustom p = new PaidCustom();
                    p.setName(payDeposit.getInstitution());
                    p.setId(payDeposit.getId());
                    p.setAmount(payDeposit.getAmount());
                    p.setDate(payDeposit.getPayDate());
                    p.setPayer(payDeposit.getPayer());
                    p.setType(type);
                    res.add(p);
                }
                obj.put("rows", res);
                obj.put("total", page.getTotal());
                obj.put("pageNumber", page.getPageNumber());
                obj.put("totalPage", page.getTotalPages());
                obj.put("pageSize", page.getPageSize());
                Object moneySum = payDepositService.getSingleResultByNativeQuery("SELECT SUM(amount) FROM hy_pay_deposit WHERE has_paid = 1 AND type = " + MappingUtil.PAY_DEPOSIT_TYPE_SET.get(type));
                obj.put("moneySum", moneySum);
            } else if (MappingUtil.REFUNDINFO_TYPE_SET.containsKey(type)) {
                RefundInfo queryParm = new RefundInfo();
                // 1:已付款
                queryParm.setState(1);
                // 单位
                queryParm.setAppliName(name);
                queryParm.setType(MappingUtil.REFUNDINFO_TYPE_SET.get(type));
                Page<RefundInfo> page = refundInfoService.findPage(pageable, queryParm);
                List<PaidCustom> res = new ArrayList<>();
                for (RefundInfo r : page.getRows()) {
                    PaidCustom p = new PaidCustom();
                    p.setName(r.getAppliName());
                    p.setId(r.getId());
                    p.setAmount(r.getAmount());
                    p.setDate(r.getPayDate());
                    p.setPayer(r.getPayer());
                    p.setType(type);
                    res.add(p);
                }
                obj.put("rows", res);
                obj.put("total", page.getTotal());
                obj.put("pageNumber", page.getPageNumber());
                obj.put("totalPage", page.getTotalPages());
                obj.put("pageSize", page.getPageSize());
                Object moneySum = refundInfoService.getSingleResultByNativeQuery("SELECT SUM(amount) FROM hy_refund_info WHERE has_paid = 1 AND type = " + MappingUtil.REFUNDINFO_TYPE_SET.get(type));
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

    // 已付 - 分公司预付款
    @RequestMapping("/branchPrePay/view")
    @ResponseBody
    public Json BranchprepayDetail(PayServicer payServicer, HttpSession session) {
        Json j = new Json();

        try {
            Long id = payServicer.getId();
            PayServicer p = payServicerService.find(id);
            BankList bankList = bankListService.find(p.getBankListId());

            HashMap<String, Object> obj = new HashMap<>();

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

            PayDetails payDetail = new PayDetails();
            payDetail.setSort(1); // 1:PayServicer
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

    // 已付 - T+N
    @RequestMapping("/tnConfirm/view")
    @ResponseBody
    public Json TNConfirmDetail(PayServicer payServicer, HttpSession session) {

        Json j = new Json();

        try {

            Long id = payServicer.getId();
            PayServicer p = payServicerService.find(id);
            BankList bankList = bankListService.find(p.getBankListId());

            HashMap<String, Object> obj = new HashMap<>();

            obj.put("accountName", bankList.getAccountName());
            obj.put("bankName", bankList.getBankName());
            obj.put("bankCode", bankList.getBankCode());
            obj.put("bankType", bankList.getBankType());
            obj.put("bankAccount", bankList.getBankAccount());

            obj.put("servicerName", p.getServicerName());
            obj.put("confirmCode", p.getConfirmCode());
            obj.put("amount", p.getAmount());
            obj.put("remark", p.getRemark());

            PayDetails payDetail = new PayDetails();
            payDetail.setSort(1); // 1:PayServicer
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

    // 已付 - 提前打款
    @RequestMapping("/prePay/view")
    @ResponseBody
    public Json PrePayDetail(PayServicer payServicer, HttpSession session) {

        Json j = new Json();

        try {

            Long id = payServicer.getId();
            PayServicer p = payServicerService.find(id);
            BankList bankList = bankListService.find(p.getBankListId());

            HashMap<String, Object> obj = new HashMap<>();

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

            PayDetails payDetail = new PayDetails();
            payDetail.setSort(1); // 1:PayServicer
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

    // 已付 - 旅游元素供应商尾款
    @RequestMapping("/payElements/view")
    @ResponseBody
    public Json PayElementsDetail(PayServicer payServicer, HttpSession session) {
        Json j = new Json();

        try {
            Long id = payServicer.getId();
            PayServicer p = payServicerService.find(id);
            BankList bankList = bankListService.find(p.getBankListId());

            HashMap<String, Object> obj = new HashMap<>();

            obj.put("applyDate", p.getApplyDate());
            obj.put("appliName", p.getAppliName());
            obj.put("servicerName", p.getServicerName());
            obj.put("amount", p.getAmount());
            obj.put("remark", p.getRemark());

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

            PayDetails payDetail = new PayDetails();
            payDetail.setSort(1); // 1:PayServicer
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

    // 已付 - 向酒店/门票/酒加景供应商付款
    @RequestMapping("/ticketPay/view")
    @ResponseBody
    public Json TicketPayDetail(PayServicer payServicer, HttpSession session) {

        Json j = new Json();

        try {
            Long id = payServicer.getId();
            PayServicer p = payServicerService.find(id);
            BankList bankList = bankListService.find(p.getBankListId());

            HashMap<String, Object> obj = new HashMap<>();

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

            PayDetails payDetail = new PayDetails();
            payDetail.setSort(1); // 1:PayServicer
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

    // 已付 - 江泰预充值
    @RequestMapping("/jtaiPresave/view")
    @ResponseBody
    public Json JtaipresaveDetail(PayServicer payServicer, HttpSession session) {
        Json j = new Json();

        Long id = payServicer.getId();
        PayServicer p = payServicerService.find(id);
        // BankList bankList = bankListService.find(p.getBankListId());

        HashMap<String, Object> obj = new HashMap<>();

        try {
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

            PayDetails payDetail = new PayDetails();
            payDetail.setSort(1); // 1:PayServicer
            payDetail.setPayId(id); //
            List<PayDetails> list = payDetailsService.findPage(new Pageable(), payDetail).getRows();
            obj.put("list", list);

            j.setSuccess(true);
            j.setObj(obj);
        } catch (Exception e) {
            j.setSuccess(false);
            j.setMsg("操作失败");
        }
        return j;
    }

    // 已付 - 门店提现
    @RequestMapping("/withdraw_cash/view")
    @ResponseBody
    public Json WithDrawCashDetail(PayServicer payServicer, HttpSession session) {
        Json j = new Json();

        Long id = payServicer.getId();
        PayServicer p = payServicerService.find(id);
        // BankList bankList = bankListService.find(p.getBankListId());

        HashMap<String, Object> obj = new HashMap<>();

        try {
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

            PayDetails payDetail = new PayDetails();
            payDetail.setSort(1); // 1:PayServicer
            payDetail.setPayId(id); //
            List<PayDetails> list = payDetailsService.findPage(new Pageable(), payDetail).getRows();
            obj.put("list", list);

            j.setSuccess(true);
            j.setObj(obj);
        } catch (Exception e) {
            j.setSuccess(false);
            j.setMsg("操作失败");
        }
        return j;
    }

    // 已付 - 分公司分成
    @RequestMapping("/profitShareConfirm/view")
    @ResponseBody
    public Json ProfitshareconfirmDetail(PayShareProfit payShareProfit, HttpSession session) {
        Json j = new Json();

        try {
            Long id = payShareProfit.getId();
            PayShareProfit p = payShareProfitService.find(id);
            BankList bankList = bankListService.find(p.getBankListId());

            HashMap<String, Object> obj = new HashMap<>();

            obj.put("accountName", bankList.getAccountName());
            obj.put("bankName", bankList.getBankName());
            obj.put("bankCode", bankList.getBankCode());
            obj.put("bankType", bankList.getBankType());
            obj.put("bankAccount", bankList.getBankAccount());

            obj.put("branchName", p.getClient());
            obj.put("confirmCode", p.getConfirmCode());
            obj.put("amount", p.getAmount());
            obj.put("remark", p.getRemark());

            PayDetails payDetail = new PayDetails();
            payDetail.setSort(2); // 2:PayShareProfit
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

    // 已付 - 导游报账应付款
    @RequestMapping("/guideReimbursement/view")
    @ResponseBody
    public Json GuidereimbursementDetail(PayGuider payGuider, HttpSession session) {

        Json j = new Json();

        try {
            Long id = payGuider.getId();
            PayGuider p = payGuiderService.find(id);
            Guide guide = guideService.find(p.getGuiderId());

            HashMap<String, Object> obj = new HashMap<>();

            obj.put("accountName", p.getAccountName());
            obj.put("bankName", p.getBankName());
            obj.put("bankCode", p.getBankLink());
            obj.put("bankType", "对私");
            obj.put("bankAccount", p.getBankAccount());

            obj.put("guiderId", guide.getGuideSn());
            obj.put("guider", p.getGuider());
            obj.put("amount", p.getAmount());
            obj.put("remark", p.getRemark());

            PayDetails payDetail = new PayDetails();
            payDetail.setSort(3); // 3:PayGuider
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

    // 已付 - 导游费用
    @RequestMapping("/guideFee/view")
    @ResponseBody
    public Json GuidefeeDetail(PayGuider payGuider, HttpSession session) {
        Json j = new Json();

        try {
            Long id = payGuider.getId();
            PayGuider p = payGuiderService.find(id);
            Guide guide = guideService.find(p.getGuiderId());

            HashMap<String, Object> obj = new HashMap<>();

            obj.put("accountName", p.getAccountName());
            obj.put("bankName", p.getBankName());
            obj.put("bankCode", p.getBankLink());
            obj.put("bankType", "对私");
            obj.put("bankAccount", p.getBankAccount());

            obj.put("guiderId", guide.getGuideSn());
            obj.put("guider", p.getGuider());
            obj.put("amount", p.getAmount());
            obj.put("remark", p.getRemark());

            PayDetails payDetail = new PayDetails();
            payDetail.setSort(3); // 3:PayGuider
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

    // 已付 - 分公司产品中心结算
    @RequestMapping("/settlement/view")
    @ResponseBody
    public Json SettlementDetail(PaySettlement paySettlement, HttpSession session) {

        Json j = new Json();

        try {
            Long id = paySettlement.getId();
            PaySettlement p = paySettlementService.find(id);
            BankList bankList = bankListService.find(p.getBankListId());

            HashMap<String, Object> obj = new HashMap<>();

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

            PayDetails payDetail = new PayDetails();
            payDetail.setSort(4); // 4:PaySettlement
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

    // 已付 - 退保证金 - 供应商
    @RequestMapping("/payDepositServicer/view")
    @ResponseBody
    public Json PayDepositServicerDetail(PayDeposit payDeposit, HttpSession session) {
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
            obj.put("servicerName", p.getInstitution());
            obj.put("contractCode", p.getContractCode());
            obj.put("amount", p.getAmount());
            obj.put("remark", p.getRemark());

            PayDetails payDetail = new PayDetails();
            payDetail.setSort(5); // 5:PayDeposit
            payDetail.setPayId(id); //

            List<PayDetails> record = payDetailsService.findPage(new Pageable(), payDetail).getRows();
            obj.put("record", record);

            j.setSuccess(true);
            j.setObj(obj);

        } catch (Exception e) {
            j.setMsg("操作失败");
            j.setSuccess(false);
        }
        return j;
    }

    // 已付 - 供应商消团
    @RequestMapping("/revoked/view")
    @ResponseBody
    public Json RevokedDetail(RefundInfo refundInfo, HttpSession session) {
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
            j.setMsg("操作失败");
            j.setSuccess(false);
        }
        return j;
    }

    // 已付 - 游客退团
    @RequestMapping("/refund/view")
    @ResponseBody
    public Json RefundDetail(RefundInfo refundInfo, HttpSession session) {
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
            j.setMsg("操作失败");
            j.setSuccess(false);
        }
        return j;
    }

    // 已付 - 供应商驳回订单
    @RequestMapping("/servicerDismiss/view")
    @ResponseBody
    public Json ServicerdismissDetail(RefundInfo refundInfo, HttpSession session) {
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
            j.setMsg("操作失败");
            j.setSuccess(false);
        }
        return j;
    }

    // 已付款 - 门店驳回订单
    @RequestMapping("/storeDismiss/view")
    @ResponseBody
    public Json StoredismissDetail(RefundInfo refundInfo, HttpSession session) {

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
            j.setMsg("操作失败");
            j.setSuccess(false);
        }
        return j;
    }

    // 已付 - 电子门票退款

    // 已付 - 签证退款

    // 已付 - 酒店/酒加景退款

    // 已付 - 门店保险弃撤
    @RequestMapping("/insuranceCancle/view")
    @ResponseBody
    public Json InsuranceCancleDetail(RefundInfo refundInfo, HttpSession session) {

        Json j = new Json();

        try {

            RefundInfo r = refundInfoService.find(refundInfo.getId());
            RefundRecords refundRecords = new RefundRecords();
            refundRecords.setRefundInfoId(refundInfo.getId());
            List<RefundRecords> list = refundRecordsService.findPage(new Pageable(), refundRecords).getRows();

            HashMap<String, Object> obj = new HashMap<>();
            obj.put("orderCode", list.get(0).getOrderCode());
            obj.put("storeName", r.getAppliName());
            obj.put("amount", r.getAmount());
            obj.put("remark", r.getRemark());

            obj.put("record", list);

            j.setSuccess(true);
            j.setObj(obj);

        } catch (Exception e) {
            j.setMsg("操作失败");
            j.setSuccess(false);
        }
        return j;
    }

    // 待付款 - 微商后返
    @RequestMapping("/profitShareWechatBusiness/view")
    @ResponseBody
    public Json ProfitshareWechatbusinessDetail(ProfitShareWechatBusiness profitShareWechatBusiness,
                                                HttpSession session) {
        Json j = new Json();

        try {

            Long id = profitShareWechatBusiness.getId();
            PayShareProfit p = payShareProfitService.find(id);

            HashMap<String, Object> obj = new HashMap<>();

            obj.put("weChatBusinessName", p.getClient());
            obj.put("billingCycleStart", p.getBillingCycleStart());
            obj.put("billingCycleEnd", p.getBillingCycleEnd());
            obj.put("amount", p.getAmount());
            obj.put("remark", p.getRemark());

            PayDetails payDetail = new PayDetails();
            payDetail.setPayId(id);
            payDetail.setSort(2);
            List<PayDetails> list = payDetailsService.findPage(new Pageable(), payDetail).getRows();

            obj.put("record", list);

            j.setSuccess(true);
            j.setObj(obj);

        } catch (Exception e) {
            j.setMsg("操作失败");
            j.setSuccess(false);
        }
        return j;
    }

    // 待付款 - 门店后返
    @RequestMapping("/profitShareStore/view")
    @ResponseBody
    public Json ProfitshareStoreDetail(ProfitShareStore profitShareStore, HttpSession session) {

        Json j = new Json();

        try {

            Long id = profitShareStore.getId();
            PayShareProfit p = payShareProfitService.find(id);

            HashMap<String, Object> obj = new HashMap<>();

            obj.put("storeName", p.getClient());
            obj.put("billingCycleStart", p.getBillingCycleStart());
            obj.put("billingCycleEnd", p.getBillingCycleEnd());
            obj.put("amount", p.getAmount());
            obj.put("remark", p.getRemark());

            PayDetails payDetail = new PayDetails();
            payDetail.setPayId(id);
            payDetail.setSort(2); //
            List<PayDetails> list = payDetailsService.findPage(new Pageable(), payDetail).getRows();

            obj.put("record", list);

            j.setSuccess(true);
            j.setObj(obj);

        } catch (Exception e) {
            j.setMsg("操作失败");
            j.setSuccess(false);
        }
        return j;
    }

}
