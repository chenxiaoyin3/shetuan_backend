package com.hongyu.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.grain.service.BaseService;
import com.hongyu.Json;
import com.hongyu.Pageable;
import com.hongyu.entity.PayablesLine;
import com.hongyu.entity.PaymentSupplier;

public interface PaymentSupplierService extends BaseService<PaymentSupplier, Long> {
    /**
     * 打款申请 - 即时提交
     */
    Json addPaymentSuppierInstant(PayablesLine payablesLine) throws Exception;

    /**
     * 打款申请 - 定时扫描提交
     */
    void addPaymentSuppierAuto(Long supplierContractId, String operator) throws Exception;

    /**
     * 打款申请 - 手动提交
     */
    Json addPaymentSupplierSubmit(Long id, Long supplierContractId
            , List<Long> lineOrderIds, List<Long> hotelOrderIds, List<Long> ticketOrderIds, List<Long> hotelAndSceneIds, List<Long> visaOrderIds, List<Long> ticketSoldIds
            , HttpSession httpSession) throws Exception;

    /**
     * 打款申请 - 审核
     */
    Json addPaymentSupplierAudit(Long id, String comment, Integer state, String dismissRemark, BigDecimal modifyAmount, HttpSession session) throws Exception;

    /**
     * (提前打款)提交人 对被驳回的申请进行修改
     */
    Json updateApply(Long id, Integer type, String dismissRemark, BigDecimal modifyAmount, HttpSession session) throws Exception;

    /**
     * (自动打款)财务 对被驳回进行修改
     */
    Json updateApply2(Long id, Integer type, String dismissRemark, BigDecimal modifyAmount, HttpSession session) throws Exception;

    /**
     * 向供应商付款审核 - 列表
     */
    Json payServierPreReviewList(Pageable pageable, Integer state, String supplierName, String orderNumber,String payCode,String sn, HttpSession session) throws Exception;

    /**
     * 取消审核
     */
    Json cancelAudit(Long id, HttpSession session)throws Exception;
    /**
     * 向供应商付款审核 - 详情
     * */
    HashMap<String, Object> getHistoryComments(Long id) throws Exception;

    /** 供应商对账表*/
    List<HashMap<String,Object>> getSupplierReconciliationList(String startDate, String endDate, String name) throws Exception;

    /**
     * 打款单审核列表Excel导出的列表数据的获取
     * 基于payServierPreReviewList() 删除分页相关
     * 未获取到数据返回空的List<Map<String, Object>>
     * */
    List<Map<String,Object>> getAuditList(Integer state, String supplierName, String orderNumber, String username) throws Exception;
}