package com.hongyu.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="hy_returned_inbound")
public class ReturnedInbound
  implements Serializable
{
  private Long id;
  private String returnedCode;
  private Long orderId;
  private Long providerId;
  private Integer deliverType;
  private Integer deliverStatus;
  private String operator;
  private String orderWechatAccount;
  private String orderPhone;
  private Long wechatBusinessId;
  private String orderAddress;
  
  public ReturnedInbound() {}
  
  public ReturnedInbound(Long id)
  {
    this.id = id;
  }
  
  public ReturnedInbound(Long id, String returnedCode, Long orderId, Long providerId, Integer deliverType, Integer deliverStatus, String operator, String orderWechatAccount, String orderPhone, Long wechatBusinessId, String orderAddress)
  {
    this.id = id;
    this.returnedCode = returnedCode;
    this.orderId = orderId;
    this.providerId = providerId;
    this.deliverType = deliverType;
    this.deliverStatus = deliverStatus;
    this.operator = operator;
    this.orderWechatAccount = orderWechatAccount;
    this.orderPhone = orderPhone;
    this.wechatBusinessId = wechatBusinessId;
    this.orderAddress = orderAddress;
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
  
  @Column(name="returned_code")
  public String getReturnedCode()
  {
    return this.returnedCode;
  }
  
  public void setReturnedCode(String returnedCode)
  {
    this.returnedCode = returnedCode;
  }
  
  @Column(name="order_id")
  public Long getOrderId()
  {
    return this.orderId;
  }
  
  public void setOrderId(Long orderId)
  {
    this.orderId = orderId;
  }
  
  @Column(name="provider_id")
  public Long getProviderId()
  {
    return this.providerId;
  }
  
  public void setProviderId(Long providerId)
  {
    this.providerId = providerId;
  }
  
  @Column(name="deliver_type")
  public Integer getDeliverType()
  {
    return this.deliverType;
  }
  
  public void setDeliverType(Integer deliverType)
  {
    this.deliverType = deliverType;
  }
  
  @Column(name="deliver_status")
  public Integer getDeliverStatus()
  {
    return this.deliverStatus;
  }
  
  public void setDeliverStatus(Integer deliverStatus)
  {
    this.deliverStatus = deliverStatus;
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
  
  @Column(name="order_wechat_account")
  public String getOrderWechatAccount()
  {
    return this.orderWechatAccount;
  }
  
  public void setOrderWechatAccount(String orderWechatAccount)
  {
    this.orderWechatAccount = orderWechatAccount;
  }
  
  @Column(name="order_phone")
  public String getOrderPhone()
  {
    return this.orderPhone;
  }
  
  public void setOrderPhone(String orderPhone)
  {
    this.orderPhone = orderPhone;
  }
  
  @Column(name="wechat_business_id")
  public Long getWechatBusinessId()
  {
    return this.wechatBusinessId;
  }
  
  public void setWechatBusinessId(Long wechatBusinessId)
  {
    this.wechatBusinessId = wechatBusinessId;
  }
  
  @Column(name="order_address")
  public String getOrderAddress()
  {
    return this.orderAddress;
  }
  
  public void setOrderAddress(String orderAddress)
  {
    this.orderAddress = orderAddress;
  }
}