package com.hongyu.entity;

import static javax.persistence.GenerationType.IDENTITY;

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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hongyu.util.Constants.AuditStatus;

@SuppressWarnings("serial")
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler",
	"hygroup"
	})
@Table(name = "group_swd_shenhe")
public class HyGroupShenheSwd {

	private Long id;//id
	private HyGroup hygroup;//group_id
	private Integer times;
	private HyGroupShenheSwd hyGroupShenheSwd; //指向上一次审核成功的甩尾单实体
	private String applyName;//apply_name
	private Date applyTime;//apply_time
	private AuditStatus auditStatus;//audit_status
	private String processInstanceId;//process_instance_id
	
	
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "ID", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "group_id")
	public HyGroup getHygroup() {
		return hygroup;
	}

	public void setHygroup(HyGroup hygroup) {
		this.hygroup = hygroup;
	}

	@Column(name = "apply_name")
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


	@Column(name = "audit_status")
	public AuditStatus getAuditStatus() {
		return auditStatus;
	}


	public void setAuditStatus(AuditStatus auditStatus) {
		this.auditStatus = auditStatus;
	}

	
	@Column(name = "process_instance_id")
	public String getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	public Integer getTimes() {
		return times;
	}

	public void setTimes(Integer times) {
		this.times = times;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pid")
	public HyGroupShenheSwd getHyGroupShenheSwd() {
		return hyGroupShenheSwd;
	}

	public void setHyGroupShenheSwd(HyGroupShenheSwd hyGroupShenheSwd) {
		this.hyGroupShenheSwd = hyGroupShenheSwd;
	}
	
	
	
	
}
