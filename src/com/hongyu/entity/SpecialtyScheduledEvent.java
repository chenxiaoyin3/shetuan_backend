package com.hongyu.entity;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

//@Entity
//@Table(name="hy_specialty_scheduled_event")
public class SpecialtyScheduledEvent {
//	private Long id;
//	//0 上架    1 下架
//	private Integer type;
//	private Date scheduledTime;
//	private Boolean isValid;
//	private Specialty specialty;
//	
//	@Id
//	@GeneratedValue(strategypattern=GenerationType.AUTO)
//	@Column(name="id",unique=true,nullable=false)
//	public Long getId() {
//		return id;
//	}
//	public void setId(Long id) {
//		this.id = id;
//	}
//	
//	@ManyToOne(fetch=FetchType.LAZY)
//	@JoinColumn(name="specialty_id")
//	public Specialty getSpecialty() {
//		return specialty;
//	}
//	public void setSpecialty(Specialty specialty) {
//		this.specialty = specialty;
//	}
//	
//	@Column(name="market_price")
//	public Integer getType() {
//		return type;
//	}
//	public void setType(Integer type) {
//		this.type = type;
//	}
//	
//	@Column(name="market_price")
//	public Date getScheduledTime() {
//		return scheduledTime;
//	}
//	public void setScheduledTime(Date scheduledTime) {
//		this.scheduledTime = scheduledTime;
//	}
//	
//	@Column(name="market_price")
//	public Boolean getIsValid() {
//		return isValid;
//	}
//	public void setIsValid(Boolean isValid) {
//		this.isValid = isValid;
//	}
//	
//	@PrePersist
//	public void setPrepersist() {
//		this.isValid = true;
//	}
}
