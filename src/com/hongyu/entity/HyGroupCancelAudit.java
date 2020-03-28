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
import javax.validation.constraints.Digits;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hongyu.util.Constants.AuditStatus;
@SuppressWarnings("serial")
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler","hyGroup",
	})
@Table(name = "hy_group_cancel_audit")
public class HyGroupCancelAudit implements Serializable {
    private Long id;
    private HyGroup hyGroup;
    private HyAdmin applyName;
    private Date applyTime;
    private String processInstanceId;
    private AuditStatus auditStatus;
    @Digits(integer=10, fraction=2)
    private BigDecimal money; //消团的所有订单的金额，用来和审核额度比较
    
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
	@JoinColumn(name = "group_id")
	public HyGroup getHyGroup() {
		return hyGroup;
	}
	public void setHyGroup(HyGroup hyGroup) {
		this.hyGroup = hyGroup;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "apply_name")
	public HyAdmin getApplyName() {
		return applyName;
	}
	public void setApplyName(HyAdmin applyName) {
		this.applyName = applyName;
	}
	
	@JsonProperty
	@DateTimeFormat(iso=ISO.DATE_TIME)
	@Column(name = "apply_time", length = 19)
	public Date getApplyTime() {
		return applyTime;
	}
	public void setApplyTime(Date applyTime) {
		this.applyTime = applyTime;
	}
	
	@Column(name="process_instance_id")
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	
	@Column(name="audit_status")
	public AuditStatus getAuditStatus() {
		return auditStatus;
	}
	public void setAuditStatus(AuditStatus auditStatus) {
		this.auditStatus = auditStatus;
	}
	public BigDecimal getMoney() {
		return money;
	}
	public void setMoney(BigDecimal money) {
		this.money = money;
	}
    
}
