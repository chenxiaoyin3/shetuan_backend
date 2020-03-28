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
 * PayShareProfit
 * @author xyy
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "hy_pay_share_profit")
public class PayShareProfit implements java.io.Serializable {

    private Long id;
    private Integer hasPaid;
    /** 1:分公司分成 2:门店后返 3:微商后返*/
    private Integer type;
    private String client;
    private Date billingCycleStart;
    private Date billingCycleEnd;
    private BigDecimal amount;
    private String remark;
    private Long bankListId;
    private String confirmCode;
    private String payer;
    private Date payDate;

    @Column(name = "payer")
    public String getPayer() {
        return payer;
    }

    public void setPayer(String payer) {
        this.payer = payer;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "pay_date")
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

    @Column(name = "type")
    public Integer getType() {
        return this.type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Column(name = "client")
    public String getClient() {
        return this.client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "billing_cycle_start")
    public Date getBillingCycleStart() {
        return this.billingCycleStart;
    }

    public void setBillingCycleStart(Date billingCycleStart) {
        this.billingCycleStart = billingCycleStart;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "billing_cycle_end")
    public Date getBillingCycleEnd() {
        return this.billingCycleEnd;
    }

    public void setBillingCycleEnd(Date billingCycleEnd) {
        this.billingCycleEnd = billingCycleEnd;
    }

    @Column(name = "amount")
    public BigDecimal getAmount() {
        return this.amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
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

    @Column(name = "confirm_code")
    public String getConfirmCode() {
        return this.confirmCode;
    }

    public void setConfirmCode(String confirmCode) {
        this.confirmCode = confirmCode;
    }
}
