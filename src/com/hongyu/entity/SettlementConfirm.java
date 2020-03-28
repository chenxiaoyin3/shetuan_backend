package com.hongyu.entity;
// Generated 2017-12-11 20:56:27 by Hibernate Tools 5.2.3.Final

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * SettlementConfirm 分公司结算确认单
 */
@Entity
@Table(name = "hy_settlement_confirm")
public class SettlementConfirm implements java.io.Serializable {

	private Long id;
	private Integer state;
	private String confirmCode;
	private String branchName;
	private Date applyDate;
	private String appliName;
	private BigDecimal amount;
	private Date payDate;
	private String dismissReason;
	private BigDecimal modifyAmount;
	private String remark;
	private Long bankListId;

	@Id
	@GeneratedValue(strategy = IDENTITY)

	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "state")
	public Integer getState() {
		return this.state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	@Column(name = "confirm_code")
	public String getConfirmCode() {
		return this.confirmCode;
	}

	public void setConfirmCode(String confirmCode) {
		this.confirmCode = confirmCode;
	}

	@Column(name = "branch_name")
	public String getBranchName() {
		return this.branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "apply_date")
	public Date getApplyDate() {
		return this.applyDate;
	}

	public void setApplyDate(Date applyDate) {
		this.applyDate = applyDate;
	}

	@Column(name = "appli_name")
	public String getAppliName() {
		return this.appliName;
	}

	public void setAppliName(String appliName) {
		this.appliName = appliName;
	}

	@Column(name = "amount")
	public BigDecimal getAmount() {
		return this.amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "pay_date")
	public Date getPayDate() {
		return this.payDate;
	}

	public void setPayDate(Date payDate) {
		this.payDate = payDate;
	}

	@Column(name = "dismiss_reason")
	public String getDismissReason() {
		return this.dismissReason;
	}

	public void setDismissReason(String dismissReason) {
		this.dismissReason = dismissReason;
	}

	@Column(name = "modify_amount")
	public BigDecimal getModifyAmount() {
		return this.modifyAmount;
	}

	public void setModifyAmount(BigDecimal modifyAmount) {
		this.modifyAmount = modifyAmount;
	}

	@Column(name = "remark")
	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Column(name = "bank_list_id")
	public Long getBankListId() {
		return this.bankListId;
	}

	public void setBankListId(Long bankListId) {
		this.bankListId = bankListId;
	}

}
