package com.shetuan.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="sk_supplement")
public class Supplement {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	private Long id;
	
	@Column(name = "supplement_summary")
	private String supplementSummary;
	
	@Column(name = "supplement_description")
	private String supplementDescription;
	
	@Column(name = "contacts")
	private String contacts;
	
	@Column(name = "contacts_phone")
	private String contactsPhone;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "apply_time", length = 19)
	private Date applyTime;
	
	@Column(name = "audit_status")
	private Integer auditStatus;
	
	@Column(name = "auditor_name")
	private String auditorName;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "audit_time", length = 19)
	private Date auditTime;
	
	@Column(name = "audit_result")
	private String auditResult;
	
	@OneToMany(fetch=FetchType.LAZY,mappedBy="supplement",cascade=CascadeType.ALL,orphanRemoval = true)
	private List<SupplementFile> supplementFile;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSupplementSummary() {
		return supplementSummary;
	}

	public void setSupplementSummary(String supplementSummary) {
		this.supplementSummary = supplementSummary;
	}

	public String getSupplementDescription() {
		return supplementDescription;
	}

	public void setSupplementDescription(String supplementDescription) {
		this.supplementDescription = supplementDescription;
	}

	public String getContacts() {
		return contacts;
	}

	public void setContacts(String contacts) {
		this.contacts = contacts;
	}

	public String getContactsPhone() {
		return contactsPhone;
	}

	public void setContactsPhone(String contactsPhone) {
		this.contactsPhone = contactsPhone;
	}

	public Date getApplyTime() {
		return applyTime;
	}

	public void setApplyTime(Date applyTime) {
		this.applyTime = applyTime;
	}

	public Integer getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(Integer auditStatus) {
		this.auditStatus = auditStatus;
	}

	public String getAuditorName() {
		return auditorName;
	}

	public void setAuditorName(String auditName) {
		this.auditorName = auditName;
	}

	public Date getAuditTime() {
		return auditTime;
	}

	public void setAuditTime(Date auditTime) {
		this.auditTime = auditTime;
	}

	public String getAuditResult() {
		return auditResult;
	}

	public void setAuditResult(String auditResult) {
		this.auditResult = auditResult;
	}

	public List<SupplementFile> getSupplementFile() {
		return supplementFile;
	}

	public void setSupplementFile(List<SupplementFile> supplementFile) {
		this.supplementFile = supplementFile;
	}
	
	
}

