package com.hongyu.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
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
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Digits;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name="hy_purchase_item")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler",
})
public class PurchaseItem
  implements Serializable
{
  private Long id;
  //采购单
  private Purchase purchase;
  //采购清单对应的产品规格
  private SpecialtySpecification specification;
  //成本价
  private BigDecimal costPrice;
  //采购数量
  private Integer quantity;
  //市场价
  private BigDecimal marketPrice;
  //平台销售价
  private BigDecimal salePrice;
  //运费
  private BigDecimal deliverPrice;
  //创建时间
  private Date createTime;
  //状态，0：未入库，1：已入库
  private Boolean state;
  //是否设置提成比例
  private Boolean setState;
  //是否有效
  private Boolean isValid;
  //虹宇门店提成比例
  private BigDecimal storeDivide;
  //非虹宇门店提成比例
  private BigDecimal exterStoreDivide;
  //微商个人提成比例
  private BigDecimal businessPersonDivide;
  //设置时间
  private Date setProportionTime;
  //设置人
  private HyAdmin setProportionOperator;
  //已入库数量，不保存到数据库的
  private Integer inboundedQuantity;
  //损失产品数量,不保存到数据库的
  private Integer lostQuantity;
  //特产名称，不保存到数据库的
  private String specialtyName;
  private String purchaseCode;
  private Integer auditedLostQuantity;
  
  public PurchaseItem() {}
  
  public PurchaseItem(Long id)
  {
    this.id = id;
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
  @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.REFRESH, CascadeType.MERGE}, optional = false)
  @JoinColumn(name="purchase_id")
  public Purchase getPurchase()
  {
    return this.purchase;
  }
  
  public void setPurchase(Purchase purchase)
  {
    this.purchase = purchase;
  }
  
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="specification_id")
  public SpecialtySpecification getSpecification()
  {
    return this.specification;
  }
  
  public void setSpecification(SpecialtySpecification specification)
  {
    this.specification = specification;
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
  }
  
  @Column(name="quantity")
  public Integer getQuantity()
  {
    return this.quantity;
  }
  
  public void setQuantity(Integer quantity)
  {
    this.quantity = quantity;
  }
  
  @Column(name="market_price", precision=10, scale=2)
  @Digits(integer=10, fraction=2)
  public BigDecimal getMarketPrice()
  {
    return this.marketPrice;
  }
  
  public void setMarketPrice(BigDecimal marketPrice)
  {
    this.marketPrice = marketPrice;
  }
  
  @Column(name="sale_price", precision=10, scale=2)
  @Digits(integer=10, fraction=2)
  public BigDecimal getSalePrice()
  {
    return this.salePrice;
  }
  
  public void setSalePrice(BigDecimal salePrice)
  {
    this.salePrice = salePrice;
  }
  
  @Column(name="deliver_price", precision=10, scale=2)
  @Digits(integer=10, fraction=2)
  public BigDecimal getDeliverPrice() {
	return deliverPrice;
  }

  public void setDeliverPrice(BigDecimal deliverPrice) {
	this.deliverPrice = deliverPrice;
  }

@Column(name="state")
  public Boolean getState()
  {
    return this.state;
  }
  
  public void setState(Boolean state)
  {
    this.state = state;
  }
  
  @Column(name="store_divide", precision=10, scale=2)
  public BigDecimal getStoreDivide()
  {
    return this.storeDivide;
  }
  
  public void setStoreDivide(BigDecimal storeDivide)
  {
    this.storeDivide = storeDivide;
  }
  
  @Column(name="exter_store_divide", precision=10, scale=2)
  public BigDecimal getExterStoreDivide()
  {
    return this.exterStoreDivide;
  }
  
  public void setExterStoreDivide(BigDecimal exterStoreDivide)
  {
    this.exterStoreDivide = exterStoreDivide;
  }
  
  @Column(name="business_person_divide", precision=10, scale=2)
  public BigDecimal getBusinessPersonDivide()
  {
    return this.businessPersonDivide;
  }
  
  public void setBusinessPersonDivide(BigDecimal businessPersonDivide)
  {
    this.businessPersonDivide = businessPersonDivide;
  }
  
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name="create_time", length=19, updatable=false)
  public Date getCreateTime() {
	return createTime;
  }

  public void setCreateTime(Date createTime) {
	this.createTime = createTime;
  }
  
  @Column(name="is_valid")
  public Boolean getIsValid() {
	return isValid;
  }

  public void setIsValid(Boolean isValid) {
	this.isValid = isValid;
  }
  
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name="set_proportion_time", length=19)
  public Date getSetProportionTime() {
	return setProportionTime;
  }

  public void setSetProportionTime(Date setProportionTime) {
	this.setProportionTime = setProportionTime;
  }
  
  @ManyToOne(fetch=FetchType.LAZY)
  @JoinColumn(name="set_proportion_operator")
  public HyAdmin getSetProportionOperator() {
	return setProportionOperator;
  }

  public void setSetProportionOperator(HyAdmin setProportionOperator) {
	this.setProportionOperator = setProportionOperator;
  }
  
  @Column(name="set_state")
  public Boolean getSetState() {
	return setState;
  }

  public void setSetState(Boolean setState) {
	this.setState = setState;
  }

@Transient
  public Integer getInboundedQuantity() {
	return inboundedQuantity;
  }

  public void setInboundedQuantity(Integer inboundedQuantity) {
	this.inboundedQuantity = inboundedQuantity;
  }
  
  @Transient
  public Integer getLostQuantity() {
	return lostQuantity;
  }

  public void setLostQuantity(Integer lostQuantity) {
	this.lostQuantity = lostQuantity;
  }
  
  @Transient
  public String getSpecialtyName() {
	return specialtyName;
  }

  public void setSpecialtyName(String specialtyName) {
	this.specialtyName = specialtyName;
  }
  
  @Transient
  public String getPurchaseCode()
  {
    return this.purchaseCode;
  }
  
  public void setPurchaseCode(String purchaseCode)
  {
    this.purchaseCode = purchaseCode;
  }
  
  @Transient
  public Integer getAuditedLostQuantity()
  {
    return this.auditedLostQuantity;
  }
  
  public void setAuditedLostQuantity(Integer auditedLostQuantity)
  {
    this.auditedLostQuantity = auditedLostQuantity;
  }
  
  

  @PrePersist
  public void setPrepersist() {
	  this.createTime = new Date();
	  this.isValid = true;
	  this.state = false;
	  this.setState = false;
  }
  
}
