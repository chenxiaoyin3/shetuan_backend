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

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@SuppressWarnings("serial")
@Entity
@Table(name = "hy_ticket_payment_supplier")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler",
})
public class HyPaymentSupplier implements Serializable {
    private Long id;
    private HySupplier piaowubuGongyingshang;
    private Date startTime;
    private Date endTime;
    private BigDecimal money;
    private Date createDate;
    private Integer checkStatus;//0-未审核   1-副总已审核  2-审核通过(财务已审核)，3-驳回
    private Boolean payStatus; //true-已付款,false-未付款
    private String processInstanceId;
    private String remark;
    private HyAdmin operator;
    
    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
    public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="supplier")
	public HySupplier getPiaowubuGongyingshang() {
		return piaowubuGongyingshang;
	}
	public void setPiaowubuGongyingshang(HySupplier piaowubuGongyingshang) {
		this.piaowubuGongyingshang = piaowubuGongyingshang;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="start_time", length=19)
	@DateTimeFormat(pattern="yyyy-MM-dd")
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="end_time", length=19)
	@DateTimeFormat(pattern="yyyy-MM-dd")
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	
	@Column(name="money")
	public BigDecimal getMoney() {
		return money;
	}
	public void setMoney(BigDecimal money) {
		this.money = money;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="create_date", length=19)
	@DateTimeFormat(pattern="yyyy-MM-dd")
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
	@Column(name="check_status")
	public Integer getCheckStatus() {
		return checkStatus;
	}
	public void setCheckStatus(Integer checkStatus) {
		this.checkStatus = checkStatus;
	}
	
	@Column(name="pay_status")
	public Boolean getPayStatus() {
		return payStatus;
	}
	public void setPayStatus(Boolean payStatus) {
		this.payStatus = payStatus;
	}
	
	@Column(name="process_instance_id")
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	
	@Column(name="remark")
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="operator")
	public HyAdmin getOperator() {
		return operator;
	}
	public void setOperator(HyAdmin operator) {
		this.operator = operator;
	}
}
