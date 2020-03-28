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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name="hy_purchase_ship")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PurchaseShip
  implements Serializable
{
  private Long id;
  private Purchase purchase;
  private String shipCode;
  private String shipCompany;
  private Date shipDate;
  private Date arriveDate;
  private Boolean status;
  private HyAdmin operator;
  
  public PurchaseShip() {}
  
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
  
  @Column(name="ship_code")
  public String getShipCode()
  {
    return this.shipCode;
  }
  
  public void setShipCode(String shipCode)
  {
    this.shipCode = shipCode;
  }
  
  @Column(name="ship_company")
  public String getShipCompany()
  {
    return this.shipCompany;
  }
  
  public void setShipCompany(String shipCompany)
  {
    this.shipCompany = shipCompany;
  }
  
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name="ship_date", length=19)
  public Date getShipDate()
  {
    return this.shipDate;
  }
  
  public void setShipDate(Date shipDate)
  {
    this.shipDate = shipDate;
  }
  
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name="arrive_date", length=19)
  public Date getArriveDate()
  {
    return this.arriveDate;
  }
  
  public void setArriveDate(Date arriveDate)
  {
    this.arriveDate = arriveDate;
  }
  
  @Column(name="status")
  public Boolean getStatus()
  {
    return this.status;
  }
  
  public void setStatus(Boolean status)
  {
    this.status = status;
  }
  
  @ManyToOne(fetch=FetchType.LAZY)
  @JoinColumn(name="operator")
  public HyAdmin getOperator()
  {
    return this.operator;
  }
  
  public void setOperator(HyAdmin operator)
  {
    this.operator = operator;
  }
  
  @PrePersist
  public void setPrepersist() {
	  this.shipDate = new Date();
	  this.status = true;
  }
}
