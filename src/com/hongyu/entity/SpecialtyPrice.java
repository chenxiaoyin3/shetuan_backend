package com.hongyu.entity;

import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name="hy_specialty_price")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler",
})
public class SpecialtyPrice implements Serializable{
	private Long id;
	private Specialty specialty;
	private SpecialtySpecification specification;
	private BigDecimal marketPrice;
	private BigDecimal platformPrice;
	private BigDecimal costPrice;
	private Boolean isActive;
	private Date createTime;
	private Date deadTime;
	private String creatorName;
	private BigDecimal storeDivide;
	private BigDecimal exterStoreDivide;
	private BigDecimal businessPersonDivide;
	private BigDecimal deliverPrice;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id",unique=true,nullable=false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@JsonIgnore
	@ManyToOne(fetch=FetchType.LAZY,cascade=CascadeType.REFRESH,optional=false)
	@JoinColumn(name="specialty_id")
	public Specialty getSpecialty() {
		return specialty;
	}
	public void setSpecialty(Specialty specialty) {
		this.specialty = specialty;
	}
	
	@JsonIgnore
	@ManyToOne(fetch=FetchType.LAZY,cascade=CascadeType.REFRESH,optional=false)
	@JoinColumn(name="specification_id")	
	public SpecialtySpecification getSpecification() {
		return specification;
	}
	public void setSpecification(SpecialtySpecification specification) {
		this.specification = specification;
	}
	
	@Column(name="market_price")
	public BigDecimal getMarketPrice() {
		return marketPrice;
	}
	public void setMarketPrice(BigDecimal marketPrice) {
		this.marketPrice = marketPrice;
	}
	
	@Column(name="platform_price")
	public BigDecimal getPlatformPrice() {
		return platformPrice;
	}
	public void setPlatformPrice(BigDecimal platformPrice) {
		this.platformPrice = platformPrice;
	}
	
	@Column(name="cost_price")
	public BigDecimal getCostPrice() {
		return costPrice;
	}
	public void setCostPrice(BigDecimal costPrice) {
		this.costPrice = costPrice;
	}
	
	@Column(name="is_active")
	public Boolean getIsActive() {
		return isActive;
	}
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="create_time",length=19)
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="dead_time",length=19)
	public Date getDeadTime() {
		return deadTime;
	}
	public void setDeadTime(Date deadTime) {
		this.deadTime = deadTime;
	}
	
	@Column(name="creator_name")
	public String getCreatorName() {
		return creatorName;
	}
	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}
	
	@Column(name="store_divide", precision=10, scale=4)
	public BigDecimal getStoreDivide() {
		return storeDivide;
	}

	public void setStoreDivide(BigDecimal storeDivide) {
		this.storeDivide = storeDivide;
	}

	@Column(name="exter_store_divide", precision=10, scale=4)
	public BigDecimal getExterStoreDivide() {
		return exterStoreDivide;
	}

	public void setExterStoreDivide(BigDecimal exterStoreDivide) {
		this.exterStoreDivide = exterStoreDivide;
	}

	@Column(name="business_person_divide", precision=10, scale=4)
	public BigDecimal getBusinessPersonDivide() {
		return businessPersonDivide;
	}

	public void setBusinessPersonDivide(BigDecimal businessPersonDivide) {
		this.businessPersonDivide = businessPersonDivide;
	}
	
	@Column(name="deliver_price", precision=10, scale=4)
	public BigDecimal getDeliverPrice() {
		return deliverPrice;
	}
	public void setDeliverPrice(BigDecimal deliverPrice) {
		this.deliverPrice = deliverPrice;
	}
	@PrePersist
	public void setPrepersist() {
		this.isActive = true;
		this.createTime = new Date();
	}

}
