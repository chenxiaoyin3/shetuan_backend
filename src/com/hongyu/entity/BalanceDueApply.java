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

/**
 * 产品中心付尾款申请
 *
 * @author xyy
 */
@Entity
@Table(name = "hy_balance_due_apply")
public class BalanceDueApply {
    private Long id;
    private Long institutionId;
    private HySupplierElement supplierElement;
    private BigDecimal money;
    private HyAdmin operator;
    /**
     * 申请日期
     */
    private Date createTime;
    /**
     * 付款日期
     */
    private Date payDate;
    private String processInstanceId;
    /**
     * (0 未审核-未付) 1审核中-未付 2 已通过-未付 3已通过-已付 4已驳回-未付
     */
    private Integer status;
    /**
     * 审核步骤 1:待(总公司/分公司)产品中心经理审核 2:待(总公司/分公司)副总(限额)审核 3:待总公司财务审核
     */
    private Integer step;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "institution_id")
    public Long getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(Long institutionId) {
        this.institutionId = institutionId;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    public HySupplierElement getSupplierElement() {
        return this.supplierElement;
    }

    public void setSupplierElement(HySupplierElement supplierElement) {
        this.supplierElement = supplierElement;
    }

    @Column(name = "money")
    public BigDecimal getMoney() {
        return this.money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operator_id")
    public HyAdmin getOperator() {
        return this.operator;
    }

    public void setOperator(HyAdmin operator) {
        this.operator = operator;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_time")
    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

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

    @Column(name = "status")
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Column(name = "step")
    public Integer getStep() {
        return step;
    }

    public void setStep(Integer step) {
        this.step = step;
    }
}
