package com.hongyu.entity;
// Generated 2017-12-12 17:05:00 by Hibernate Tools 3.6.0.Final

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * HyCustomerService generated by hbm2java
 */
@Entity
@Table(name = "hy_customer_service")
public class CustomerService implements java.io.Serializable {

	private Long id;
	private Long lineId;
	private String lineName;
	private Date createDate;
	private Date modifyDate;
	private Date startDate;
	private String advice;
	private Date revisitDate;
	private HyAdmin operator;

	public CustomerService() {
	}

	public CustomerService(Long id) {
		this.id = id;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "line_id")
	public Long getLineId() {
		return this.lineId;
	}

	public void setLineId(Long lineId) {
		this.lineId = lineId;
	}
	@Column(name="line_name")
	public String getLineName() {
		return lineName;
	}

	public void setLineName(String lineName) {
		this.lineName = lineName;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_date", length = 19)
	public Date getCreateDate() {
		return this.createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "modify_date", length = 19)
	public Date getModifyDate() {
		return this.modifyDate;
	}

	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "start_date")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	public Date getStartDate() {
		return this.startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@Column(name = "advice")
	public String getAdvice() {
		return this.advice;
	}

	public void setAdvice(String advice) {
		this.advice = advice;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "revisit_date")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	public Date getRevisitDate() {
		return this.revisitDate;
	}

	public void setRevisitDate(Date revisitDate) {
		this.revisitDate = revisitDate;
	}
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="operator")
	public HyAdmin getOperator() {
		return operator;
	}

	public void setOperator(HyAdmin operator) {
		this.operator = operator;
	}

	@PrePersist
	public void prePersist(){
		this.setCreateDate(new Date());
	}
	@PreUpdate
	public void preUpdate(){
		this.setModifyDate(new Date());
	}
}