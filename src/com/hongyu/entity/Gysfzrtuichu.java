package com.hongyu.entity;

import java.io.Serializable;
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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hongyu.util.Constants.AuditStatus;
/**
 * 供应商负责人合同退出，退还押金
 * @author guoxinze
 *
 */
@SuppressWarnings("serial")
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler",
})
@Table(name="hy_supplier_tuiyajin")
public class Gysfzrtuichu implements Serializable {
	private Long id;
	
	/** 退还押金的文件 */
	private String fileUrl;
	
	/** 退还押金金额 */
	@Digits(integer=10, fraction=2)
	private BigDecimal returnMoney;
	
	/** 申请退出时间 */
	private Date applyTime;
	
	/** 申请人账号 */
	private String applierName;
	
	/** 审核状态 */
	private AuditStatus auditStatus;
	
	/** 流程实例ID */
	private String processInstanceId;
	
	/** 对应的合同 */
	private HySupplierContract contract;


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	public BigDecimal getReturnMoney() {
		return returnMoney;
	}

	public void setReturnMoney(BigDecimal returnMoney) {
		this.returnMoney = returnMoney;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "apply_time", length = 19)
	public Date getApplyTime() {
		return applyTime;
	}

	public void setApplyTime(Date applyTime) {
		this.applyTime = applyTime;
	}

	public String getApplierName() {
		return applierName;
	}

	public void setApplierName(String applierName) {
		this.applierName = applierName;
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
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "contract")
	public HySupplierContract getContract() {
		return contract;
	}

	public void setContract(HySupplierContract contract) {
		this.contract = contract;
	}
	
	
}
