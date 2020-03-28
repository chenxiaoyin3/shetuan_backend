package com.hongyu.entity;


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

@Entity
@Table(name="hy_vinbound")
public class HyVinbound {
	
	private Long id;
	private SpecialtySpecification specification;
	private Integer vinboundNumber;
	private Integer saleNumber;
	private Date vupdateTime;
	@Id
	@Column(name="id",unique=true,nullable=false)
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="specialty_specification_id")
	public SpecialtySpecification getSpecification() {
		return specification;
	}
	public void setSpecification(SpecialtySpecification specification) {
		this.specification = specification;
	}
	
	@Column(name="vinbound_number")
	public Integer getVinboundNumber() {
		return vinboundNumber;
	}
	public void setVinboundNumber(Integer vinboundNumber) {
		this.vinboundNumber = vinboundNumber;
	}
	
	@Column(name="sale_number")
	public Integer getSaleNumber() {
		return saleNumber;
	}
	public void setSaleNumber(Integer saleNumber) {
		this.saleNumber = saleNumber;
	}
	
	@Column(name="vupdate_time")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getVupdateTime() {
		return vupdateTime;
	}
	public void setVupdateTime(Date vupdateTime) {
		this.vupdateTime = vupdateTime;
	}
	
	@PrePersist
	public void setPrepersist() {
		this.saleNumber = 0;
		this.vupdateTime = new Date();
	}
	
	@PreUpdate
	public void setPreupdate() {
		this.vupdateTime = new Date();
	}

}
