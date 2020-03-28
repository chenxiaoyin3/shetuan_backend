package com.hongyu.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
@Entity
@Table(name = "hy_dimission_audit_process")
public class HyDimissionAuditProcess implements Serializable {
	private Long id;
	private Date createDate;
	private Date modifyDate;
	private Long applicationId;//离职申请人id
	private Long auditorId;//审核人id
	private Integer stepName;//步骤名称0：员工离职申请         1：部门经理审核         2：行政中心部门经理审核    3：财务审核
	private String auditAdvice;//审核意见
	private Date auditTime;//审核时间
	private Integer passAudit;//审核结果 0：待审核       1：已通过      2：已驳回
	@Id
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_time")
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "modify_time")
	public Date getModifyDate() {
		return modifyDate;
	}
	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}
	@Column(name = "application_id")
	public Long getApplicationId() {
		return applicationId;
	}
	public void setApplicationId(Long applicationId) {
		this.applicationId = applicationId;
	}
	@Column(name = "auditor_id")
	public Long getAuditorId() {
		return auditorId;
	}
	public void setAuditorId(Long auditorId) {
		this.auditorId = auditorId;
	}
	@Column(name = "step_name")
	public Integer getStepName() {
		return stepName;
	}
	public void setStepName(Integer stepName) {
		this.stepName = stepName;
	}
	@Column(name = "audit_advice")
	public String getAuditAdvice() {
		return auditAdvice;
	}
	public void setAuditAdvice(String auditAdvice) {
		this.auditAdvice = auditAdvice;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "audit_time")
	public Date getAuditTime() {
		return auditTime;
	}
	public void setAuditTime(Date auditTime) {
		this.auditTime = auditTime;
	}
	@Column(name = "pass_audit")
	public Integer getPassAudit() {
		return passAudit;
	}
	public void setPassAudit(Integer passAudit) {
		this.passAudit = passAudit;
	}
	@PrePersist
	public void prePersist(){
		//创建之前初始化创建时间
		this.createDate = new Date();
	}
	@PreUpdate
	public void preUpate(){
		this.modifyDate = new Date();
		//审核时间就是更新该表的时间
		this.auditTime = new Date();
	}
	
}
