package com.hongyu.entity;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
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

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hongyu.util.Constants.AuditStatus;
@SuppressWarnings("serial")
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"
	})
@Table(name = "hy_supplier_contract_changededuct")
public class BiangengkoudianEntity implements Serializable {
	
	private Long id;
	private HySupplierContract contractId;
	private String processInstanceId;
	private Date applyTime;
	private AuditStatus auditStatus;
	private String applyName;
	private Long deductGuoneiOld;
	private Long deductGuoneiNew;
	private Long deductChujingOld;
	private Long deductChujingNew;
	private Long deductPiaowuOld;
	private Long deductPiaowuNew;
	private Long deductQianzhengOld;
	private Long deductQianzhengNew;
	private Long deductQicheOld;
	private Long deductQicheNew;
	private Long deductRengouOld;
	private Long deductRengouNew;
	
	@Id
	@GeneratedValue(strategy = IDENTITY)

	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "apply_time", length = 19)
	@DateTimeFormat(iso=ISO.DATE_TIME)
	public Date getApplyTime() {
		return applyTime;
	}
	public void setApplyTime(Date applyTime) {
		this.applyTime = applyTime;
	}
	public AuditStatus getAuditStatus() {
		return auditStatus;
	}
	public void setAuditStatus(AuditStatus auditStatus) {
		this.auditStatus = auditStatus;
	}
	public String getApplyName() {
		return applyName;
	}
	public void setApplyName(String applyName) {
		this.applyName = applyName;
	}
	public Long getDeductGuoneiOld() {
		return deductGuoneiOld;
	}
	public void setDeductGuoneiOld(Long deductGuoneiOld) {
		this.deductGuoneiOld = deductGuoneiOld;
	}
	public Long getDeductGuoneiNew() {
		return deductGuoneiNew;
	}
	public void setDeductGuoneiNew(Long deductGuoneiNew) {
		this.deductGuoneiNew = deductGuoneiNew;
	}
	public Long getDeductChujingOld() {
		return deductChujingOld;
	}
	public void setDeductChujingOld(Long deductChujingOld) {
		this.deductChujingOld = deductChujingOld;
	}
	public Long getDeductChujingNew() {
		return deductChujingNew;
	}
	public void setDeductChujingNew(Long deductChujingNew) {
		this.deductChujingNew = deductChujingNew;
	}
	public Long getDeductPiaowuOld() {
		return deductPiaowuOld;
	}
	public void setDeductPiaowuOld(Long deductPiaowuOld) {
		this.deductPiaowuOld = deductPiaowuOld;
	}
	public Long getDeductPiaowuNew() {
		return deductPiaowuNew;
	}
	public void setDeductPiaowuNew(Long deductPiaowuNew) {
		this.deductPiaowuNew = deductPiaowuNew;
	}
	public Long getDeductQianzhengOld() {
		return deductQianzhengOld;
	}
	public void setDeductQianzhengOld(Long deductQianzhengOld) {
		this.deductQianzhengOld = deductQianzhengOld;
	}
	public Long getDeductQianzhengNew() {
		return deductQianzhengNew;
	}
	public void setDeductQianzhengNew(Long deductQianzhengNew) {
		this.deductQianzhengNew = deductQianzhengNew;
	}
	public Long getDeductQicheOld() {
		return deductQicheOld;
	}
	public void setDeductQicheOld(Long deductQicheOld) {
		this.deductQicheOld = deductQicheOld;
	}
	public Long getDeductQicheNew() {
		return deductQicheNew;
	}
	public void setDeductQicheNew(Long deductQicheNew) {
		this.deductQicheNew = deductQicheNew;
	}
	public Long getDeductRengouOld() {
		return deductRengouOld;
	}
	public void setDeductRengouOld(Long deductRengouOld) {
		this.deductRengouOld = deductRengouOld;
	}
	public Long getDeductRengouNew() {
		return deductRengouNew;
	}
	public void setDeductRengouNew(Long deductRengouNew) {
		this.deductRengouNew = deductRengouNew;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "contract_id")
	public HySupplierContract getContractId() {
		return contractId;
	}
	public void setContractId(HySupplierContract contractId) {
		this.contractId = contractId;
	}
	
}

