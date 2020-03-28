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
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Digits;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;


/**
 * Entity - 特产规格
 * 
 */
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler",
})
@Table(name="hy_specialty_specification")
public class SpecialtySpecification
  implements Serializable
{
  private Long id;
/*  //
  private Integer packingIndex;*/
  //所属产品
  private Specialty specialty;
  //产品规格名称
  private String specification;
/*  //限购数量，空表示不限制购买数量
  private Integer limitNumber;*/
/*  //展示在商城页面的销量
  private Integer salesVolume;*/
/*  //总库存
  private Integer totalStock;
  //平台库存
  private Integer platformStock;*/
  //真实销量
  private Integer hasSold;
  //市场价
  private BigDecimal marketPrice;
  //平台价
  private BigDecimal platformPrice;
  //成本价
  private BigDecimal costPrice;
  //有效状态，false：无效，true：有效
  private Boolean isActive;
/*  //创建人账号
  private String creator;*/
  //创建人姓名
  private String creatorName;
  //创建时间
  private Date createTime;
/*  //删除时间
  private Date deadTime;*/
  //修改时间
  private Date modifyTime;
  
  //修改者姓名
  private String modifierName;
  
  //是否是赠品
  private Boolean isFreeGift;
  //父规格id
  private Long parent;
  //与父规格的倍数关系
  private Integer saleNumber;
  //门店提成比例
  private BigDecimal storeDivide;
  //非虹宇门店提成比例
  private BigDecimal exterStoreDivide;
  //个人提成比例
  private BigDecimal businessPersonDivide;
  //虚拟运费
  private BigDecimal deliverPrice;
  //虚拟库存数量
  private Integer vInboundNumber;
  
  //基础库存
  private Integer baseInbound;


  @Transient
  @JsonSerialize
	public BigDecimal getDividMoney() {
		return dividMoney;
	}

	@JsonIgnore
	public void setDividMoney(BigDecimal dividMoney) {
		this.dividMoney = dividMoney;
	}

	@Transient
  private BigDecimal dividMoney;
  
  
  public SpecialtySpecification() {}
  
  public SpecialtySpecification(Long id)
  {
    this.id = id;
  }
  
  public SpecialtySpecification(SpecialtySpecification spec) {
	  this.id = spec.id;
	  this.costPrice = spec.costPrice;
	  this.createTime = spec.createTime;
	  this.creatorName = spec.creatorName;
	  this.hasSold = spec.hasSold;
	  this.isActive = spec.isActive;
	  this.isFreeGift = spec.isFreeGift;
	  this.marketPrice = spec.marketPrice;
	  this.modifierName = spec.modifierName;
	  this.modifyTime = spec.modifyTime;
	  this.parent = spec.parent;
	  this.platformPrice = spec.platformPrice;
	  this.saleNumber = spec.saleNumber;
	  this.specialty = spec.specialty;
	  this.specification = spec.specification;
	  this.saleNumber = spec.saleNumber;
	  this.baseInbound = spec.baseInbound;	//修改wayne，0912，修正更新错误
  }
  
  
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  @Column(name="ID", unique=true, nullable=false)
  public Long getId()
  {
    return this.id;
  }
  
  public void setId(Long id)
  {
    this.id = id;
  }
  
  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH, optional = false)
  @JoinColumn(name="specialty_id")
  public Specialty getSpecialty()
  {
    return this.specialty;
  }
  
  public void setSpecialty(Specialty specialty)
  {
    this.specialty = specialty;
  }
  
  @Column(name="specification")
  public String getSpecification()
  {
    return this.specification;
  }
  
  public void setSpecification(String specification)
  {
    this.specification = specification;
  }
  
  /*@Column(name="limit_number")
  public Integer getLimitNumber() {
	return limitNumber;
  }

  public void setLimitNumber(Integer limitNumber) {
	this.limitNumber = limitNumber;
  }

@Column(name="sales_volume")
  public Integer getSalesVolume()
  {
    return this.salesVolume;
  }
  
  public void setSalesVolume(Integer salesVolume)
  {
    this.salesVolume = salesVolume;
  }
  
  @Column(name="total_stock")
  public Integer getTotalStock()
  {
    return this.totalStock;
  }
  
  public void setTotalStock(Integer totalStock)
  {
    this.totalStock = totalStock;
  }
  
  @Column(name="platform_stock")
  public Integer getPlatformStock()
  {
    return this.platformStock;
  }
  
  public void setPlatformStock(Integer platformStock)
  {
    this.platformStock = platformStock;
  }*/
  
  @Column(name="has_sold")
  public Integer getHasSold()
  {
    return this.hasSold;
  }
  
  public void setHasSold(Integer hasSold)
  {
    this.hasSold = hasSold;
  }
  
  /*@Column(name="market_price", precision=10, scale=2)
  @Digits(integer=10, fraction=2)
  public BigDecimal getMarketPrice()
  {
    return this.marketPrice;
  }
  
  public void setMarketPrice(BigDecimal marketPrice)
  {
    this.marketPrice = marketPrice;
  }
  
  @Column(name="platform_price", precision=10, scale=2)
  @Digits(integer=10, fraction=2)
  public BigDecimal getPlatformPrice()
  {
    return this.platformPrice;
  }
  
  public void setPlatformPrice(BigDecimal platformPrice)
  {
    this.platformPrice = platformPrice;
  }
  
  @Column(name="cost_price", precision=10, scale=2)
  @Digits(integer=10, fraction=2)
  public BigDecimal getCostPrice()
  {
    return this.costPrice;
  }
  
  public void setCostPrice(BigDecimal costPrice)
  {
    this.costPrice = costPrice;
  }*/
  
  @Column(name="is_active")
  public Boolean getIsActive()
  {
    return this.isActive;
  }
  
  public void setIsActive(Boolean isActive)
  {
    this.isActive = isActive;
  }
  
 /* @Column(name="creator")
  public String getCreator()
  {
    return this.creator;
  }
  
  public void setCreator(String creator)
  {
    this.creator = creator;
  }*/
  
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name="create_time", length=19)
  public Date getCreateTime()
  {
    return this.createTime;
  }
  
  public void setCreateTime(Date createTime)
  {
    this.createTime = createTime;
  }
  
 /* @Temporal(TemporalType.TIMESTAMP)
  @Column(name="dead_time", length=19)
  public Date getDeadTime()
  {
    return this.deadTime;
  }
  
  public void setDeadTime(Date deadTime)
  {
    this.deadTime = deadTime;
  }*/
  
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name="modify_time", length=19)
  public Date getModifyTime() {
	return modifyTime;
  }

  public void setModifyTime(Date modifyTime) {
	this.modifyTime = modifyTime;
  }
  
  @PreUpdate
  public void setPreupdate() {
	  this.modifyTime = new Date();
  }
  
  @PrePersist
  public void setPrepersist() {
	  this.createTime = new Date();
	  if(this.hasSold == null) {
		  this.hasSold = 0;
	  }
	  
	  if(this.isActive == null) {
		  this.isActive = true;
	  }
	  
	  if(this.baseInbound == null) {
		  this.baseInbound = 0;
	  }

	  if(this.isFreeGift == null){
	  	this.isFreeGift = false;
	  }
	  
  }
  
  @Column(name="creator_name")
  public String getCreatorName() {
	return creatorName;
  }

  public void setCreatorName(String creatorName) {
	this.creatorName = creatorName;
  }

  @Column(name="modifier_name")
public String getModifierName() {
	return modifierName;
}

public void setModifierName(String modifierName) {
	this.modifierName = modifierName;
}

@Column(name="is_free_gift")
public Boolean getIsFreeGift() {
	return isFreeGift;
}

public void setIsFreeGift(Boolean isFreeGift) {
	this.isFreeGift = isFreeGift;
}


@Column(name="pid")
public Long getParent() {
	return parent;
}

public void setParent(Long parent) {
	this.parent = parent;
}


@Column(name="sale_number")
public Integer getSaleNumber() {
	return saleNumber;
}

public void setSaleNumber(Integer saleNumber) {
	this.saleNumber = saleNumber;
}

@Transient
public BigDecimal getMarketPrice() {
	return marketPrice;
}

public void setMarketPrice(BigDecimal marketPrice) {
	this.marketPrice = marketPrice;
}

@Transient
public BigDecimal getPlatformPrice() {
	return platformPrice;
}

public void setPlatformPrice(BigDecimal platformPrice) {
	this.platformPrice = platformPrice;
}

@Transient
public BigDecimal getCostPrice() {
	return costPrice;
}

public void setCostPrice(BigDecimal costPrice) {
	this.costPrice = costPrice;
}

@Transient
public BigDecimal getStoreDivide() {
	return storeDivide;
}

public void setStoreDivide(BigDecimal storeDivide) {
	this.storeDivide = storeDivide;
}

@Transient
public BigDecimal getExterStoreDivide() {
	return exterStoreDivide;
}

public void setExterStoreDivide(BigDecimal exterStoreDivide) {
	this.exterStoreDivide = exterStoreDivide;
}

@Transient
public BigDecimal getBusinessPersonDivide() {
	return businessPersonDivide;
}

public void setBusinessPersonDivide(BigDecimal businessPersonDivide) {
	this.businessPersonDivide = businessPersonDivide;
}

@Transient
public BigDecimal getDeliverPrice() {
	return deliverPrice;
}

public void setDeliverPrice(BigDecimal deliverPrice) {
	this.deliverPrice = deliverPrice;
}

@Transient
public Integer getvInboundNumber() {
	return vInboundNumber;
}

public void setvInboundNumber(Integer vInbound) {
	this.vInboundNumber = vInbound;
}

@Column(name="base_inbound")
public Integer getBaseInbound() {
	return baseInbound;
}

public void setBaseInbound(Integer baseInbound) {
	this.baseInbound = baseInbound;
}
  
 /* @Column(name="packing_index")
  public Integer getPackingIndex() {
	return packingIndex;
  }

  public void setPackingIndex(Integer packingIndex) {
	this.packingIndex = packingIndex;
  }*/
  
  
  
  
}
