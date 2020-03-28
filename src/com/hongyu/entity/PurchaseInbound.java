package com.hongyu.entity;

import java.io.Serializable;
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
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name="hy_purchase_inbound")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler",
})
public class PurchaseInbound
  implements Serializable
{
  private Long id;
  private Purchase purchase;
  private PurchaseItem purchaseItem;
  private String depotCode;
//  private Specialty specialty;
  private SpecialtySpecification specification;
  private Date productDate;
  private Integer durabilityPeriod;
  private HyAdmin inboundOperator;
  private Date inboundDate;
  private Integer inboundNumber;
  private Integer status;
//  private HyAdmin operator;
  private String specialtyName;
  
  public PurchaseInbound() {}
  
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
  
  @ManyToOne(fetch=FetchType.LAZY)
  @JoinColumn(name="purchase_id")
  public Purchase getPurchase()
  {
    return this.purchase;
  }
  
  public void setPurchase(Purchase purchase)
  {
    this.purchase = purchase;
  }
  
  @ManyToOne(fetch=FetchType.LAZY)
  @JoinColumn(name="purchase_item_id")
  public PurchaseItem getPurchaseItem() {
	return purchaseItem;
  }

  public void setPurchaseItem(PurchaseItem purchaseItem) {
	this.purchaseItem = purchaseItem;
  }
  
//  @ManyToOne(fetch=FetchType.LAZY)
//  @JoinColumn(name="specialty_id")
//  public Specialty getSpecialty() {
//	return specialty;
//  }
//
//  public void setSpecialty(Specialty specialty) {
//	this.specialty = specialty;
//  }

  @Column(name="depot_code")
  public String getDepotCode()
  {
    return this.depotCode;
  }
  
  public void setDepotCode(String depotCode)
  {
    this.depotCode = depotCode;
  }
  
  @ManyToOne(fetch=FetchType.LAZY)
  @JoinColumn(name="specification_id")
  public SpecialtySpecification getSpecification()
  {
    return this.specification;
  }
  
  public void setSpecification(SpecialtySpecification specification)
  {
    this.specification = specification;
  }
  
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name="product_date", length=19)
  @DateTimeFormat(pattern="yyyy-MM-dd")
  public Date getProductDate()
  {
    return this.productDate;
  }
  
  public void setProductDate(Date productDate)
  {
    this.productDate = productDate;
  }
  
  @Column(name="durability_period")
  public Integer getDurabilityPeriod()
  {
    return this.durabilityPeriod;
  }
  
  public void setDurabilityPeriod(Integer durabilityPeriod)
  {
    this.durabilityPeriod = durabilityPeriod;
  }
  
  @ManyToOne(fetch=FetchType.LAZY)
  @JoinColumn(name="inbound_operator")
  public HyAdmin getInboundOperator()
  {
    return this.inboundOperator;
  }
  
  public void setInboundOperator(HyAdmin inboundOperator)
  {
    this.inboundOperator = inboundOperator;
  }
  
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name="inbound_date", length=19)
  public Date getInboundDate()
  {
    return this.inboundDate;
  }
  
  public void setInboundDate(Date inboundDate)
  {
    this.inboundDate = inboundDate;
  }
  
  @Column(name="inbound_number")
  public Integer getInboundNumber()
  {
    return this.inboundNumber;
  }
  
  public void setInboundNumber(Integer inboundNumber)
  {
    this.inboundNumber = inboundNumber;
  }
  
  @Column(name="status")
  public Integer getStatus()
  {
    return this.status;
  }
  
  public void setStatus(Integer status)
  {
    this.status = status;
  }
  
  
  
//  @ManyToOne(fetch=FetchType.LAZY)
//  @JoinColumn(name="operator")
//  public HyAdmin getOperator()
//  {
//    return this.operator;
//  }
//  
//  public void setOperator(HyAdmin operator)
//  {
//    this.operator = operator;
//  }
  
  @Transient
  public String getSpecialtyName() {
	return specialtyName;
  }

  public void setSpecialtyName(String specialtyName) {
	this.specialtyName = specialtyName;
  }

@PrePersist
  public void setPrepersist() {
	  this.inboundDate = new Date();
	  this.status = 1;
  }
}
