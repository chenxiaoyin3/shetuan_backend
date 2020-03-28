package com.hongyu.entity;


import java.math.BigDecimal;
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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 供货商结算明细表
 * @author JZhong
 *
 */

@Entity
@Table(name="hy_provider_balance_item")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ProviderBalanceItem {
	private Long id;
	private Provider provider;
	private String name;
	private Specialty specialty;
	private SpecialtySpecification specification;
	//实际销售数量
	private Integer saleCount;
	private BigDecimal lostCount;
	private BigDecimal costPrice;
	private BigDecimal saleMoney;
	private Date createTime;
	private Date balanceTime;
	private Boolean state;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id", unique=true, nullable=false)
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	@JsonIgnore
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="provider_id")
	public Provider getProvider() {
		return provider;
	}
	
	public void setProvider(Provider provider) {
		this.provider = provider;
	}
	
	@Column(name="name")
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@JsonIgnore
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="specialty_id")
	public Specialty getSpecialty() {
		return specialty;
	}

	public void setSpecialty(Specialty specialty) {
		this.specialty = specialty;
	}
	
	@JsonIgnore
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="specification_id")
	public SpecialtySpecification getSpecification() {
		return specification;
	}

	public void setSpecification(SpecialtySpecification specification) {
		this.specification = specification;
	}
	
	@Column(name="sale_count")
	public Integer getSaleCount() {
		return saleCount;
	}
	
	public void setSaleCount(Integer saleCount) {
		this.saleCount = saleCount;
	}
	
	@Column(name="lost_count")
	public BigDecimal getLostCount() {
		return lostCount;
	}

	public void setLostCount(BigDecimal lostCount) {
		this.lostCount = lostCount;
	}

	@Column(name="cost_price")
	public BigDecimal getCostPrice() {
		return costPrice;
	}
	
	public void setCostPrice(BigDecimal costPrice) {
		this.costPrice = costPrice;
	}
	
	@Column(name="sale_money")
	public BigDecimal getSaleMoney() {
		return saleMoney;
	}
	
	public void setSaleMoney(BigDecimal saleMoney) {
		this.saleMoney = saleMoney;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="create_time", length=19)
	public Date getCreateTime() {
		return createTime;
	}
	
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="balance_time", length=19)
	public Date getBalanceTime() {
		return balanceTime;
	}
	
	public void setBalanceTime(Date balanceTime) {
		this.balanceTime = balanceTime;
	}
	
	@Column(name="state")
	public Boolean getState() {
		return state;
	}
	
	public void setState(Boolean state) {
		this.state = state;
	}
	
	@PrePersist
	public void setPrepersist() {
		this.balanceTime = new Date();
		this.state = false;
	}
}
