package com.hongyu.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="hy_specialty_outbound_detail")
public class SpecialtyOutboundDetail
  implements Serializable
{
  private Long id;
  private Long specialtyOutboundId;
  private String depotCode;
  private Long specialtySpecificationId;
  private Integer outboundQuantity;
  private Date outboundTime;
  private String operator;
  private Date operateTime;
  private Long purchaseItemId;
  
  public SpecialtyOutboundDetail() {}
  
  public SpecialtyOutboundDetail(Long id)
  {
    this.id = id;
  }
  
  public SpecialtyOutboundDetail(Long id, Long specialtyOutboundId, String depotCode, Long specialtySpecificationId, Integer outboundQuantity, Date outboundTime, String operator, Date operateTime, Long purchaseItemId)
  {
    this.id = id;
    this.specialtyOutboundId = specialtyOutboundId;
    this.depotCode = depotCode;
    this.specialtySpecificationId = specialtySpecificationId;
    this.outboundQuantity = outboundQuantity;
    this.outboundTime = outboundTime;
    this.operator = operator;
    this.operateTime = operateTime;
    this.purchaseItemId = purchaseItemId;
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
  
  @Column(name="specialty_outbound_id")
  public Long getSpecialtyOutboundId()
  {
    return this.specialtyOutboundId;
  }
  
  public void setSpecialtyOutboundId(Long specialtyOutboundId)
  {
    this.specialtyOutboundId = specialtyOutboundId;
  }
  
  @Column(name="depot_code")
  public String getDepotCode()
  {
    return this.depotCode;
  }
  
  public void setDepotCode(String depotCode)
  {
    this.depotCode = depotCode;
  }
  
  @Column(name="specialty_specification_id")
  public Long getSpecialtySpecificationId()
  {
    return this.specialtySpecificationId;
  }
  
  public void setSpecialtySpecificationId(Long specialtySpecificationId)
  {
    this.specialtySpecificationId = specialtySpecificationId;
  }
  
  @Column(name="outbound_quantity")
  public Integer getOutboundQuantity()
  {
    return this.outboundQuantity;
  }
  
  public void setOutboundQuantity(Integer outboundQuantity)
  {
    this.outboundQuantity = outboundQuantity;
  }
  
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name="outbound_time", length=19)
  public Date getOutboundTime()
  {
    return this.outboundTime;
  }
  
  public void setOutboundTime(Date outboundTime)
  {
    this.outboundTime = outboundTime;
  }
  
  @Column(name="operator")
  public String getOperator()
  {
    return this.operator;
  }
  
  public void setOperator(String operator)
  {
    this.operator = operator;
  }
  
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name="operate_time", length=19)
  public Date getOperateTime()
  {
    return this.operateTime;
  }
  
  public void setOperateTime(Date operateTime)
  {
    this.operateTime = operateTime;
  }
  
  @Column(name="purchase_item_id")
  public Long getPurchaseItemId()
  {
    return this.purchaseItemId;
  }
  
  public void setPurchaseItemId(Long purchaseItemId)
  {
    this.purchaseItemId = purchaseItemId;
  }
}
