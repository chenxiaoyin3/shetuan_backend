package com.hongyu.entity;
// Generated 2017-11-20 20:19:04 by Hibernate Tools 5.2.3.Final


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
import javax.validation.constraints.Digits;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hongyu.util.Constants.AuditStatus;

/**
 * 提交申请-业务-供应商押金
 */
@SuppressWarnings("serial")
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler",
	"servicerName",
	"contractId",
})
@Table(name="hy_deposit_servicer")
public class DepositServicer  implements java.io.Serializable {

	 public enum Fukuanfangshi {
		 /** 线下支付 */
		 xxzf,
	 }

     private Long id;
     
     /** 交押金合同负责人 */
     private HyAdmin servicerName;
     
     /** 供应商合同 */
     private HySupplierContract contractId;
     
     /** 付款时间 */
     private Date payTime;
     
     /** 提交申请时间 */
     private Date applyTime;
     
     /** 付款金额 */
 	 @Digits(integer=10, fraction=2)
     private BigDecimal payAmount;
     
     /** 付款方式 */
     private Fukuanfangshi fkfs;
     
	/** 银行卡号 */
     private String bankAccount;
     
     /** 开户人 */
     private String kaihuren;
     
     /** 账号类型 */
     private Boolean accountType;//0对公 1对私
     
     /** 开户行 */
     private String kaihuhang;
     
     /** 联行号 */
     private String lianhanghao;
     
     /** 虹宇总部财务收款账号 */
     private BankList bankList;
     
     /** 审核状态 */
     private AuditStatus auditStatus;
     
     /** 流程实例ID */
     private String processInstanceId;

    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "servicer_name")
    public HyAdmin getServicerName() {
        return this.servicerName;
    }
    
    public void setServicerName(HyAdmin servicerName) {
        this.servicerName = servicerName;
    }


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "contract_id")
	public HySupplierContract getContractId() {
		return contractId;
	}


	public void setContractId(HySupplierContract contractId) {
		this.contractId = contractId;
	}

	@DateTimeFormat(iso=ISO.DATE_TIME)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "pay_time", length = 19)
	public Date getPayTime() {
		return payTime;
	}


	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}


	public Fukuanfangshi getFkfs() {
		return fkfs;
	}


	public void setFkfs(Fukuanfangshi fkfs) {
		this.fkfs = fkfs;
	}


	public String getBankAccount() {
		return bankAccount;
	}


	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}


	public String getKaihuren() {
		return kaihuren;
	}


	public void setKaihuren(String kaihuren) {
		this.kaihuren = kaihuren;
	}


	public Boolean getAccountType() {
		return accountType;
	}


	public void setAccountType(Boolean accountType) {
		this.accountType = accountType;
	}


	public String getKaihuhang() {
		return kaihuhang;
	}


	public void setKaihuhang(String kaihuhang) {
		this.kaihuhang = kaihuhang;
	}


	public String getLianhanghao() {
		return lianhanghao;
	}


	public void setLianhanghao(String lianhanghao) {
		this.lianhanghao = lianhanghao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "apply_time", length = 19)
	public Date getApplyTime() {
		return applyTime;
	}
	public void setApplyTime(Date applyTime) {
		this.applyTime = applyTime;
	}
	
    public BigDecimal getPayAmount() {
		return payAmount;
	}
	public void setPayAmount(BigDecimal payAmount) {
		this.payAmount = payAmount;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "bank_id")
	public BankList getBankList() {
		return bankList;
	}


	public void setBankList(BankList bankList) {
		this.bankList = bankList;
	}


	public AuditStatus getAuditStatus() {
		return auditStatus;
	}


	public void setAuditStatus(AuditStatus auditStatus) {
		this.auditStatus = auditStatus;
	}


	public String getProcessInstanceId() {
		return processInstanceId;
	}


	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

}


