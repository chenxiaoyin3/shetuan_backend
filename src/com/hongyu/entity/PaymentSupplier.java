package com.hongyu.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

/** 供应商打款单 (申请) 20160615 
 * 	@author xyy
 * */
@Entity
@Table(name = "hy_payment_supplier")
public class PaymentSupplier {
	private Long id;
	/** 打款单的有效状态, 注意:无效状态相当于被删除!  0 无效  1有效  此次申请是否可用 当申请置为下一周期结算 此次申请置为无效 */
	private Integer isValid;
	private String payCode;
	private String supplierName;
    /** 产品计调*/
	private HyAdmin operator;  
	private HySupplierContract supplierContract;
	/** 多条PayablesLineItem的money的和*/
	private BigDecimal moneySum;
	/** 多条PayablesLineItem的koudian的和*/
	private BigDecimal koudianSum;
	private Date createTime;
	private HyAdmin creator;
	/** (0 未审核-未付) 1审核中-未付 2 已通过-未付 3已通过-已付 4已驳回-未付*/
	private Integer status;
	private Date payDate;
	private String processInstanceId;
	/**
	 审核步骤(提前付款使用,去除步骤 2:待供应商确认) 0:被驳回待申请人处理 1:待采购部经理审核 3:待市场部副总限额审核 4:待总公司财务审核
	审核步骤(T+N付款 实时付款使用) 10:被驳回待财务处理 11:待供应商确认 12:待市场部副总限额审核 13:待总公司财务审核
	 * */
	private Integer step; 
    /** 是否进行金额修改 0 否 1是*/
	private Integer modified; 
	/** 调整金额*/
	private BigDecimal modifyAmount; 
	/** 调整原因*/
	private String dismissRemark;
	/** 申请来源 0 系统自动产生 1采购部员工手动提交*/
	private Integer applySource;  
	/** 结算日期*/
	private Date settleDate; 
	/** 本次使用欠款金额*/
	private BigDecimal debtamount;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "pay_code")
	public String getPayCode() {
		return payCode;
	}

	public void setPayCode(String payCode) {
		this.payCode = payCode;
	}

	@Column(name = "supplier_name")
	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "operator")
	public HyAdmin getOperator() {
		return operator;
	}

	public void setOperator(HyAdmin operator) {
		this.operator = operator;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "supplier_contract")
	public HySupplierContract getSupplierContract() {
		return supplierContract;
	}

	public void setSupplierContract(HySupplierContract supplierContract) {
		this.supplierContract = supplierContract;
	}

	@Column(name = "money_sum")
	public BigDecimal getMoneySum() {
		return moneySum;
	}

	public void setMoneySum(BigDecimal moneySum) {
		this.moneySum = moneySum;
	}

	@Column(name = "koudian_sum")
    public BigDecimal getKoudianSum() {
        return koudianSum;
    }

    public void setKoudianSum(BigDecimal koudianSum) {
        this.koudianSum = koudianSum;
    }

    @DateTimeFormat(iso = ISO.DATE_TIME)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_time")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "creator")
	public HyAdmin getCreator() {
		return creator;
	}

	public void setCreator(HyAdmin creator) {
		this.creator = creator;
	}

	@Column(name = "status")
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@DateTimeFormat(iso = ISO.DATE_TIME)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "pay_date")
	public Date getPayDate() {
		return payDate;
	}

	public void setPayDate(Date payDate) {
		this.payDate = payDate;
	}

	@Column(name = "process_instance_id")
	public String getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	@Column(name = "step")
	public Integer getStep() {
		return step;
	}

	public void setStep(Integer step) {
		this.step = step;
	}

	@Column(name = "modified")
	public Integer getModified() {
		return modified;
	}

	public void setModified(Integer modified) {
		this.modified = modified;
	}

	@Column(name = "dismiss_remark")
	public String getDismissRemark() {
		return dismissRemark;
	}

	public void setDismissRemark(String dismissRemark) {
		this.dismissRemark = dismissRemark;
	}

	@Column(name = "modify_amount")
	public BigDecimal getModifyAmount() {
		return modifyAmount;
	}

	public void setModifyAmount(BigDecimal modifyAmount) {
		this.modifyAmount = modifyAmount;
	}

	@Column(name = "is_valid")
	public Integer getIsValid() {
		return isValid;
	}

	public void setIsValid(Integer isValid) {
		this.isValid = isValid;
	}

	@Column(name = "apply_source")
	public Integer getApplySource() {
		return applySource;
	}

	public void setApplySource(Integer applySource) {
		this.applySource = applySource;
	}

	@DateTimeFormat(iso = ISO.DATE_TIME)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "settle_date")
	public Date getSettleDate() {
		return settleDate;
	}

	public void setSettleDate(Date settleDate) {
		this.settleDate = settleDate;
	}

	@Column(name = "debtamount")
	public BigDecimal getDebtamount() {
		return debtamount;
	}

	public void setDebtamount(BigDecimal debtamount) {
		this.debtamount = debtamount;
	}
}
