package com.hongyu.entity;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Digits;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hongyu.util.Constants.AuditStatus;
import com.hongyu.util.Constants.DeductLine;

@SuppressWarnings("serial")
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler",
	"groupId",
	})
@Table(name = "hy_group_biankoudian")
public class GroupBiankoudian implements Serializable {
	private Long id;
	private HyGroup groupId;
	private DeductLine oldType;
	private DeductLine newType;
	@Digits(integer=10, fraction=2)
	private BigDecimal oldRentou;
	@Digits(integer=10, fraction=2)
	private BigDecimal newRentou;
	@Digits(integer=10, fraction=2)
	private BigDecimal oldLiushui;
	@Digits(integer=10, fraction=2)
	private BigDecimal newLiushui;
	private String applyName;
	private Date applyTime;
	private String processInstanceId;
	private AuditStatus auditStatus;
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="group_id")
	public HyGroup getGroupId() {
		return groupId;
	}
	public void setGroupId(HyGroup groupId) {
		this.groupId = groupId;
	}
	public DeductLine getOldType() {
		return oldType;
	}
	public void setOldType(DeductLine oldType) {
		this.oldType = oldType;
	}
	public DeductLine getNewType() {
		return newType;
	}
	public void setNewType(DeductLine newType) {
		this.newType = newType;
	}
	public BigDecimal getOldRentou() {
		return oldRentou;
	}
	public void setOldRentou(BigDecimal oldRentou) {
		this.oldRentou = oldRentou;
	}
	public BigDecimal getNewRentou() {
		return newRentou;
	}
	public void setNewRentou(BigDecimal newRentou) {
		this.newRentou = newRentou;
	}
	public BigDecimal getOldLiushui() {
		return oldLiushui;
	}
	public void setOldLiushui(BigDecimal oldLiushui) {
		this.oldLiushui = oldLiushui;
	}
	public BigDecimal getNewLiushui() {
		return newLiushui;
	}
	public void setNewLiushui(BigDecimal newLiushui) {
		this.newLiushui = newLiushui;
	}
	public String getApplyName() {
		return applyName;
	}
	public void setApplyName(String applyName) {
		this.applyName = applyName;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "apply_time", length = 19)
	public Date getApplyTime() {
		return applyTime;
	}
	public void setApplyTime(Date applyTime) {
		this.applyTime = applyTime;
	}
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	public AuditStatus getAuditStatus() {
		return auditStatus;
	}
	public void setAuditStatus(AuditStatus auditStatus) {
		this.auditStatus = auditStatus;
	}
	
}
