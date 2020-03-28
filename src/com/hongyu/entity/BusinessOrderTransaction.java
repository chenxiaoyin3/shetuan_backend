package com.hongyu.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="hy_business_order_transaction")
public class BusinessOrderTransaction
  implements Serializable
{
  private Long id;
  private String serialNum;
  private Long orderId;
  private Float wechatBalance;
  private Long orderCouponId;
  private Float payment;
  private Integer payType;
  private String payAcount;
  private Integer payFlow;
  private String payTime;
  
  public BusinessOrderTransaction() {}
  
  public BusinessOrderTransaction(Long id)
  {
    this.id = id;
  }
  
  public BusinessOrderTransaction(Long id, String serialNum, Long orderId, Float wechatBalance, Long orderCouponId, Float payment, Integer payType, String payAcount, Integer payFlow, String payTime)
  {
    this.id = id;
    this.serialNum = serialNum;
    this.orderId = orderId;
    this.wechatBalance = wechatBalance;
    this.orderCouponId = orderCouponId;
    this.payment = payment;
    this.payType = payType;
    this.payAcount = payAcount;
    this.payFlow = payFlow;
    this.payTime = payTime;
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
  
  @Column(name="serial_num")
  public String getSerialNum()
  {
    return this.serialNum;
  }
  
  public void setSerialNum(String serialNum)
  {
    this.serialNum = serialNum;
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
  
  @Column(name="wechat_balance", precision=12, scale=0)
  public Float getWechatBalance()
  {
    return this.wechatBalance;
  }
  
  public void setWechatBalance(Float wechatBalance)
  {
    this.wechatBalance = wechatBalance;
  }
  
  @Column(name="order_coupon_id")
  public Long getOrderCouponId()
  {
    return this.orderCouponId;
  }
  
  public void setOrderCouponId(Long orderCouponId)
  {
    this.orderCouponId = orderCouponId;
  }
  
  @Column(name="payment", precision=12, scale=0)
  public Float getPayment()
  {
    return this.payment;
  }
  
  public void setPayment(Float payment)
  {
    this.payment = payment;
  }
  
  @Column(name="pay_type")
  public Integer getPayType()
  {
    return this.payType;
  }
  
  public void setPayType(Integer payType)
  {
    this.payType = payType;
  }
  
  @Column(name="pay_acount")
  public String getPayAcount()
  {
    return this.payAcount;
  }
  
  public void setPayAcount(String payAcount)
  {
    this.payAcount = payAcount;
  }
  
  @Column(name="pay_flow")
  public Integer getPayFlow()
  {
    return this.payFlow;
  }
  
  public void setPayFlow(Integer payFlow)
  {
    this.payFlow = payFlow;
  }
  
  @Column(name="pay_time")
  public String getPayTime()
  {
    return this.payTime;
  }
  
  public void setPayTime(String payTime)
  {
    this.payTime = payTime;
  }
}
