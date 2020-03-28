package com.hongyu.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.sun.mail.imap.protocol.Status;

@Entity
@Table(name = "hy_dimission_audit")
public class HyDimissionAudit implements Serializable{
	private Long id;
	private Date createDate;
	private Date modifyDate;
	private HyAdmin applicant;//申请单id
	private Date applicationTime;//申请时间
	private Date dimissionTime;//离职时间
	private String remark;//离职原因
	private Integer status;//0：未提交     1：审核中   2：已通过    3：已驳回
	private Integer stepName;//0：员工离职申请         1：部门经理审核         2：行政中心部门经理审核   
	private Integer passAudit;//0：待审核       1：已通过      2：已驳回
	private String processInstanceId;
	@Id
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_date")
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "modify_date")
	public Date getModifyDate() {
		return modifyDate;
	}
	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}
	
	@JoinColumn(name = "applicant")
	@ManyToOne(fetch = FetchType.LAZY)
	public HyAdmin getApplicant() {
		return applicant;
	}
	public void setApplicant(HyAdmin applicant) {
		this.applicant = applicant;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "application_time")
	public Date getApplicationTime() {
		return applicationTime;
	}
	public void setApplicationTime(Date applicationTime) {
		this.applicationTime = applicationTime;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "dimission_time")
	public Date getDimissionTime() {
		return dimissionTime;
	}
	public void setDimissionTime(Date dimissionTime) {
		this.dimissionTime = dimissionTime;
	}
	@Column(name = "remark")
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	@Column(name = "status")
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	@Column(name = "step_name")
	public Integer getStepName() {
		return stepName;
	}
	public void setStepName(Integer stepName) {
		this.stepName = stepName;
	}
	@Column(name = "pass_audit")
	public Integer getPassAudit() {
		return passAudit;
	}
	public void setPassAudit(Integer passAudit) {
		this.passAudit = passAudit;
	}
	@Column(name = "process_instance_id")
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	@PrePersist
	public void prePersist(){
		//创建之前初始化创建时间
		this.createDate = new Date();
	}
	@PreUpdate
	public void preUpate(){
		this.modifyDate = new Date();
	}
	
}
