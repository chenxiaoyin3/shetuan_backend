package com.hongyu.service;

import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.hongyu.Json;
import com.hongyu.entity.HyOrderApplication;
import com.hongyu.entity.PayablesLine;

public interface PiaowuConfirmService {
	/*//票务  提前打款申请--首次提交
	public Json addPiaowuSupplierSubmit(Long id, Long supplierContractId,List<Long> lineOrderIds,List<Long> ticketOrders,List<Long> lineRefunds,List<Long> ticketRefundIds,HttpSession httpSession) throws Exception;
	
	//票务打款单    -- 实时打款  --  提交
	public Json addPiaowuSupplierInstant(PayablesLine payablesLine) throws Exception;

	//票务 -- 打款申请 --定时扫描提交
	void addPiaowuSuppierAuto(Long supplierContractId) throws Exception;
	
	//票务 --打款申请 -- 审核
	Json addPiaowuSupplierAudit(Long id, String comment, Integer state,String dismissRemark,BigDecimal modifyAmount, HttpSession session) throws Exception;
	
	//（提前打款）提交人对被驳回的申请进行修改
	Json updatePiaowuApply(Long id,Integer type, String dismissRemark,BigDecimal modifyAmount, HttpSession session);
	
	//(自动打款)财务 对被驳回进行修改
	Json updatePiaowuApply2(Long id,Integer type, String dismissRemark,BigDecimal modifyAmount, HttpSession session);
	*/
	//票务  产品订购  供应商审核 完成后  的财务记录
	// type: 1线路 2酒店 3门票 4酒加景 5签证 6认购门票
	public boolean orderPiaowuConfirm(Long id, Integer type,HttpSession session);
	
	// type: 2酒店 3门票 4酒加景 5签证 6认购门票
	public boolean shouhouPiaowuRefund(HyOrderApplication application,String username,Integer type,String remark);
	
	public boolean piaowuRefund(HyOrderApplication application,String username,Integer type,String remark);
	
	
}
