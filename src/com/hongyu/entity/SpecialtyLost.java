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

import org.hibernate.annotations.ManyToAny;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hongyu.util.Constants;

@Entity
@Table(name="hy_specialty_lost")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler",
})
public class SpecialtyLost
  implements Serializable
{
  private Long id;
  private PurchaseItem purchaseItem;
  //损失数量
  private Integer lostCount;
  private Specialty specialty;
  private SpecialtySpecification specialtySpecification;
  private String purchaseCode;
  private Inbound inbound;
//  private String depotCode;
  //0:过期，1:破损  2:其他
  private Integer lostType;
  //0:供货商，1:平台，2：购买者
  private Integer loster;
  private String lostReason;
  private Date recordTime;
  private HyAdmin operator;
  //0:待审核,1:审核通过,2:审核未通过
  private Integer status;
  private HyAdmin checker;
  private Date checkTime;
  private String reason;
  private Date productDate;
  private Integer durabilityPeriod;
  private String specialtyName;
  private String specification;
  
  
  public SpecialtyLost() {} 
  
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  @Column(name="id", unique=true, nullable=false)
  public Long getId()
  {
    return this.id;
  }
  
  public void setId(Long id)
  {
    this.id = id;
  }
  
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="purchase_item_id")
  public PurchaseItem getPurchaseItem()
  {
    return this.purchaseItem;
  }
  
  public void setPurchaseItem(PurchaseItem purchaseItem)
  {
    this.purchaseItem = purchaseItem;
  }
  
  @Column(name="lost_count")
  public Integer getLostCount()
  {
    return this.lostCount;
  }
  
  public void setLostCount(Integer lostCount)
  {
    this.lostCount = lostCount;
  }
  
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="specialty_id")
  public Specialty getSpecialty()
  {
    return this.specialty;
  }
  
  public void setSpecialty(Specialty specialty)
  {
    this.specialty = specialty;
  }
  
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="specialty_specification_id")
  public SpecialtySpecification getSpecialtySpecification()
  {
    return this.specialtySpecification;
  }
  
  public void setSpecialtySpecification(SpecialtySpecification specialtySpecification)
  {
    this.specialtySpecification = specialtySpecification;
  }

//  @Column(name="depot_code")
//  public String getDepotCode()
//  {
//    return this.depotCode;
//  }
//  
//  public void setDepotCode(String depotCode)
//  {
//    this.depotCode = depotCode;
//  }
  
  @JsonIgnore
  @ManyToOne(fetch=FetchType.LAZY)
  @JoinColumn(name="inbound_id")
  public Inbound getInbound()
  {
    return this.inbound;
  }
  
  public void setInbound(Inbound inbound)
  {
    this.inbound = inbound;
  }
  
  @Column(name="lost_type")
  public Integer getLostType()
  {
    return this.lostType;
  }
  
  public void setLostType(Integer lostType)
  {
    this.lostType = lostType;
  }
  
  @Column(name="lost_reason")
  public String getLostReason()
  {
    return this.lostReason;
  }
  
  public void setLostReason(String lostReason)
  {
    this.lostReason = lostReason;
  }
  
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name="record_time", length=19)
  public Date getRecordTime()
  {
    return this.recordTime;
  }
  
  public void setRecordTime(Date recordTime)
  {
    this.recordTime = recordTime;
  }
  
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="operator")
  public HyAdmin getOperator()
  {
    return this.operator;
  }
  
  public void setOperator(HyAdmin operator)
  {
    this.operator = operator;
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
  
  @Column(name="purchase_code")
  public String getPurchaseCode() {
	return purchaseCode;
  }

  public void setPurchaseCode(String purchaseCode) {
	this.purchaseCode = purchaseCode;
  }
  
  @Column(name="loster")
  public Integer getLoster() {
	return loster;
  }

  public void setLoster(Integer loster) {
	this.loster = loster;
  }
  
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="checker")
  public HyAdmin getChecker() {
	return checker;
  }

  public void setChecker(HyAdmin checker) {
	this.checker = checker;
  }
  
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name="check_time", length=19)
  public Date getCheckTime() {
	return checkTime;
  }

  public void setCheckTime(Date checkTime) {
	this.checkTime = checkTime;
  }
  
  @Column(name="reason")
  public String getReason() {
	return reason;
  }

  public void setReason(String reason) {
	this.reason = reason;
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
  
  @Transient
  public String getSpecialtyName()
  {
    return this.specialtyName;
  }
  
  public void setSpecialtyName(String specialtyName)
  {
    this.specialtyName = specialtyName;
  }
  
  @Transient
  public String getSpecification()
  {
    return this.specification;
  }
  
  public void setSpecification(String specification)
  {
    this.specification = specification;
  }
  
  @PrePersist
  public void prepersist()
  {
    this.status = Constants.SPECIALTY_LOST_STATUS_WAIT_FOR_AUDITED;
    this.recordTime = new Date();
  }
  
  
}
