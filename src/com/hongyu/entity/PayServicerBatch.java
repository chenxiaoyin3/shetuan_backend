package com.hongyu.entity;

import javax.persistence.*;

import java.util.Date;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * Created by xyy on 2019/4/13.
 *
 * @author xyy
 *
 * 待付款的批量处理
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "hy_pay_servicer_batch")
public class PayServicerBatch implements java.io.Serializable {

    /** 批次的执行状态*/
    public enum ProcessStatus {
        /** 0 有批次 未执行*/
        processing,
        /** 1 有批次 已执行*/
        processed,
    }

    private Long id;
    /** hy_pay_servicer表的id*/
    private Long payServicerId;
    private ProcessStatus processStatus;
    private Date createDate;
    private Date modifyDate;
    /** 批次号*/
    private String batchCode;
    /** 批次创建人*/
    private String creator;
    /** 未执行到已执行的操作人*/
    private String operator;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "pay_servicer_id")
    public Long getPayServicerId() {
        return payServicerId;
    }

    public void setPayServicerId(Long departmentId) {
        this.payServicerId = payServicerId;
    }

    @Column(name = "process_status")
    public ProcessStatus getProcessStatus() {
        return this.processStatus;
    }

    public void setProcessStatus(ProcessStatus processStatus) {
        this.processStatus = processStatus;
    }

    @Column(name = "create_date")
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Column(name = "modify_date")
    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }

    @Column(name = "batch_code")
    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    @Column(name = "creator")
    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    @Column(name = "operator")
    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}
