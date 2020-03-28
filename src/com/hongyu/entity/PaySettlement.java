package com.hongyu.entity;

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
 * PaySettlement
 * @author xyy
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "hy_pay_settlement")
public class PaySettlement implements java.io.Serializable {

	private Long id;
	private Integer hasPaid;
	private Date applyDate;
	private String appliName;
	private String branchName;
	private String remark;
	private BigDecimal amount;
	private Long bankListId;
	private String settleConfirmCode;
	private String payer;
	private Date payDate;

    @Column(name = "payer")
    public String getPayer() {
        return payer;
    }

    public void setPayer(String payer) {
        this.payer = payer;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "pay_date", length = 10)
    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "has_paid")
    public Integer getHasPaid() {
        return this.hasPaid;
    }

    public void setHasPaid(Integer hasPaid) {
        this.hasPaid = hasPaid;
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

    @Column(name = "branch_name")
    public String getBranchName() {
        return this.branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    @Column(name = "remark")
    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Column(name = "amount")
    public BigDecimal getAmount() {
        return this.amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Column(name = "bank_list_id")
    public Long getBankListId() {
        return this.bankListId;
    }

    public void setBankListId(Long bankListId) {
        this.bankListId = bankListId;
    }

    @Column(name = "settle_confirm_code")
    public String getSettleConfirmCode() {
        return this.settleConfirmCode;
    }

    public void setSettleConfirmCode(String settleConfirmCode) {
        this.settleConfirmCode = settleConfirmCode;
    }

}
