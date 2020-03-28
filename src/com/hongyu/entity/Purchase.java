package com.hongyu.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hongyu.util.Constants;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="hy_purchase")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Purchase
  implements Serializable
{
  private Long id;
  private String purchaseCode;
  private Integer purchaseType;
  private Integer status;
  private Provider provider;
  private BigDecimal totalMoney;
  private BigDecimal advanceAmount;
  private Date purchaseTime;
  private Date reviewTime;
  private Date advanceTime;
  private Date setDivideProportionTime;
  private Date setShipInfoTime;
  private Date balancePaySubmitTime;
  private Date balancePayTime;
  private Date receiveTime;
  private HyAdmin creator;
  private Boolean isValid;
  private String processInstanceId;
  List<PurchaseItem> purchaseItems = new ArrayList();
  
  public Purchase() {}
  
  public Purchase(Long id)
  {
    this.id = id;
  }
  
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
  
  @Column(name="purchase_code")
  public String getPurchaseCode()
  {
    return this.purchaseCode;
  }
  
  public void setPurchaseCode(String purchaseCode)
  {
    this.purchaseCode = purchaseCode;
  }
  
  @Column(name="purchase_type")
  public Integer getPurchaseType()
  {
    return this.purchaseType;
  }
  
  public void setPurchaseType(Integer purchaseType)
  {
    this.purchaseType = purchaseType;
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
  
  @ManyToOne(fetch=FetchType.LAZY)
  @JoinColumn(name="provider_id")
  public Provider getProvider()
  {
    return this.provider;
  }
  
  public void setProvider(Provider provider)
  {
    this.provider = provider;
  }
  
  @Column(name="total_money", precision=10, scale=2)
  public BigDecimal getTotalMoney()
  {
    return this.totalMoney;
  }
  
  public void setTotalMoney(BigDecimal totalMoney)
  {
    this.totalMoney = totalMoney;
  }
  
  @Column(name="advance_amount", precision=10, scale=2)
  public BigDecimal getAdvanceAmount()
  {
    return this.advanceAmount;
  }
  
  public void setAdvanceAmount(BigDecimal advanceAmount)
  {
    this.advanceAmount = advanceAmount;
  }
  
  @Column(name="is_valid")
  public Boolean getIsValid()
  {
    return this.isValid;
  }
  
  public void setIsValid(Boolean isValid)
  {
    this.isValid = isValid;
  }
  
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name="purchase_time", length=19)
  public Date getPurchaseTime()
  {
    return this.purchaseTime;
  }
  
  public void setPurchaseTime(Date purchaseTime)
  {
    this.purchaseTime = purchaseTime;
  }
  
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name="review_time", length=19)
  public Date getReviewTime()
  {
    return this.reviewTime;
  }
  
  public void setReviewTime(Date reviewTime)
  {
    this.reviewTime = reviewTime;
  }
  
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name="advance_time", length=19)
  public Date getAdvanceTime()
  {
    return this.advanceTime;
  }
  
  public void setAdvanceTime(Date advanceTime)
  {
    this.advanceTime = advanceTime;
  }
  
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name="set_divide_proportion_time", length=19)
  public Date getSetDivideProportionTime()
  {
    return this.setDivideProportionTime;
  }
  
  public void setSetDivideProportionTime(Date setDivideProportionTime)
  {
    this.setDivideProportionTime = setDivideProportionTime;
  }
  
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name="set_ship_info_time", length=19)
  public Date getSetShipInfoTime()
  {
    return this.setShipInfoTime;
  }
  
  public void setSetShipInfoTime(Date setShipInfoTime)
  {
    this.setShipInfoTime = setShipInfoTime;
  }
  
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name="balance_pay_submit_time", length=19)
  public Date getBalancePaySubmitTime()
  {
    return this.balancePaySubmitTime;
  }
  
  public void setBalancePaySubmitTime(Date balancePaySubmitTime)
  {
    this.balancePaySubmitTime = balancePaySubmitTime;
  }
  
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name="balance_pay_time", length=19)
  public Date getBalancePayTime()
  {
    return this.balancePayTime;
  }
  
  public void setBalancePayTime(Date balancePayTime)
  {
    this.balancePayTime = balancePayTime;
  }
  
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name="receive_time", length=19)
  public Date getReceiveTime()
  {
    return this.receiveTime;
  }
  
  public void setReceiveTime(Date receiveTime)
  {
    this.receiveTime = receiveTime;
  }
  
  @ManyToOne(fetch=FetchType.LAZY)
  @JoinColumn(name="creator")
  public HyAdmin getCreator()
  {
    return this.creator;
  }
  
  public void setCreator(HyAdmin creator)
  {
    this.creator = creator;
  }
  
  @OneToMany(fetch=FetchType.LAZY, mappedBy="purchase", cascade={javax.persistence.CascadeType.ALL}, orphanRemoval=true)
  @OrderBy("id asc")
  public List<PurchaseItem> getPurchaseItems()
  {
    return this.purchaseItems;
  }
  
  public void setPurchaseItems(List<PurchaseItem> purchaseItems)
  {
    this.purchaseItems = purchaseItems;
  }
  
  @JsonIgnore
  @Column(name="process_instance_id")
  public String getProcessInstanceId()
  {
    return this.processInstanceId;
  }
  
  public void setProcessInstanceId(String processInstanceId)
  {
    this.processInstanceId = processInstanceId;
  }
  
  @PrePersist
  public void setPrepersist()
  {
    this.purchaseTime = new Date();
    setIsValid(Boolean.valueOf(true));
    if (this.status == null) {
    	this.status = Constants.PURCHASE_STATUS_WAIT_FOR_AUDITED;
    }
  }
}
