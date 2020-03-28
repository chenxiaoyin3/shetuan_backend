package com.hongyu.entity;

import java.io.Serializable;
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
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@SuppressWarnings("serial")
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"
})
@Table(name="hy_ship")
public class Ship
  implements Serializable
{
  private Long id;
  private BusinessOrder orderId;
  private Integer type;
  private HyAdmin deliverOperator;
  private String shipCode;
  private String shipCompany;
  private Date recordTime;
  private String deliveror;
  
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
  @JoinColumn(name="order_id")
  public BusinessOrder getOrderId()
  {
    return this.orderId;
  }
  
  public void setOrderId(BusinessOrder orderId)
  {
    this.orderId = orderId;
  }
  
  @Column(name="type")
  public Integer getType()
  {
    return this.type;
  }
  
  public void setType(Integer type)
  {
    this.type = type;
  }
  
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="deliver_operator")
  public HyAdmin getDeliverOperator()
  {
    return this.deliverOperator;
  }
  
  public void setDeliverOperator(HyAdmin deliverOperator)
  {
    this.deliverOperator = deliverOperator;
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
  @Column(name="record_time", length=19)
  public Date getRecordTime()
  {
    return this.recordTime;
  }
  
  public void setRecordTime(Date recordTime)
  {
    this.recordTime = recordTime;
  }

@PrePersist
  public void prePersist() {
	  this.recordTime = new Date();
  }

public String getDeliveror() {
	return deliveror;
}

public void setDeliveror(String deliveror) {
	this.deliveror = deliveror;
}
}
