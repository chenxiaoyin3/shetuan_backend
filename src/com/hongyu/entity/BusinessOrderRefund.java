package com.hongyu.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name="hy_business_order_refund")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler",
})
public class BusinessOrderRefund
  implements Serializable
{
  private Long id;
  private BusinessOrder businessOrder;
  /** 1待售后人员确认   2待消费者退货    3待库管入库    4待财务退款   5已完成*/
  private Integer state;   
  private Date refundApplyTime;
  private Date refundAcceptTime;
  private Date shipTime;
  private Date inboundTime;
  private Date returnMoneyTime;
  private Date returnCompleteTime;
  private Boolean isDelivered;
  private Integer deliverType;
  /** 1:平台      2:供应商  **/
  private Integer responsibleParty;
  private BigDecimal refundShipFee;
  private BigDecimal refundAmount;
  private BigDecimal rRefundAmount;
  private BigDecimal qRefundAmount;
  private BigDecimal eRefundAmount;
  private BigDecimal refundTotalamount;
  private WechatAccount wechat;
  private String receiverName;
  private String receivePhone;
  private String refundShiper;
  private String refundShipCode;
  private String refundReason;
  
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  @Column(name="id", unique=true, nullable=false)
  public Long getId() {
	return id;
  }
  public void setId(Long id) {
	this.id = id;
  }
  
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="business_order_id")
  public BusinessOrder getBusinessOrder() {
	return businessOrder;
  }
  public void setBusinessOrder(BusinessOrder businessOrder) {
	this.businessOrder = businessOrder;
  }
  
  @Column(name = "state")
  public Integer getState() {
	return state;
  }
  public void setState(Integer state) {
	this.state = state;
  }
  
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name="refund_apply_time", length=19)
  public Date getRefundApplyTime() {
	return refundApplyTime;
  }
  public void setRefundApplyTime(Date refundApplyTime) {
	this.refundApplyTime = refundApplyTime;
  }
  
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name="refund_accept_time", length=19)
  public Date getRefundAcceptTime() {
	return refundAcceptTime;
  }
  public void setRefundAcceptTime(Date refundAcceptTime) {
	this.refundAcceptTime = refundAcceptTime;
  }
  
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name="ship_time", length=19)
  public Date getShipTime() {
	return shipTime;
  }
  public void setShipTime(Date shipTime) {
	this.shipTime = shipTime;
  }
  
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name="inbound_time", length=19)
  public Date getInboundTime() {
	return inboundTime;
  }
  public void setInboundTime(Date inboundTime) {
	this.inboundTime = inboundTime;
  }
  
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name="return_money_time", length=19)
  public Date getReturnMoneyTime() {
	return returnMoneyTime;
  }
  public void setReturnMoneyTime(Date returnMoneyTime) {
	this.returnMoneyTime = returnMoneyTime;
  }
  
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name="return_complete_time", length=19)
  public Date getReturnCompleteTime() {
	return returnCompleteTime;
  }
  public void setReturnCompleteTime(Date returnCompleteTime) {
	this.returnCompleteTime = returnCompleteTime;
  }
  
  @Column(name="is_delivered")
  public Boolean getIsDelivered() {
	return isDelivered;
  }
  public void setIsDelivered(Boolean isDelivered) {
	this.isDelivered = isDelivered;
  }
  
  @Column(name="deliver_type")
  public Integer getDeliverType() {
	return deliverType;
  }
  public void setDeliverType(Integer deliverType) {
	this.deliverType = deliverType;
  }
  
  @Column(name="responsible_party")
  public Integer getResponsibleParty() {
	return responsibleParty;
  }
  public void setResponsibleParty(Integer responsibleParty) {
	this.responsibleParty = responsibleParty;
  }
  
  @Column(name="refund_ship_fee")
  public BigDecimal getRefundShipFee() {
	return refundShipFee;
  }
  public void setRefundShipFee(BigDecimal refundShipFee) {
	this.refundShipFee = refundShipFee;
  }
  
  @Column(name="refund_amount")
  public BigDecimal getRefundAmount() {
	return refundAmount;
  }
  public void setRefundAmount(BigDecimal refundAmount) {
	this.refundAmount = refundAmount;
  }
  
  @Column(name="rrefund_amount")
  public BigDecimal getrRefundAmount() {
	return rRefundAmount;
  }
  public void setrRefundAmount(BigDecimal rRefundAmount) {
	this.rRefundAmount = rRefundAmount;
  }
  
  @Column(name="qrefund_amount")
  public BigDecimal getqRefundAmount() {
	return qRefundAmount;
  }
  public void setqRefundAmount(BigDecimal qRefundAmount) {
	this.qRefundAmount = qRefundAmount;
  }
  
  @Column(name="erefund_amount")
  public BigDecimal geteRefundAmount() {
	return eRefundAmount;
  }
  public void seteRefundAmount(BigDecimal eRefundAmount) {
	this.eRefundAmount = eRefundAmount;
  }
  
  @Column(name="refund_totalamount")
  public BigDecimal getRefundTotalamount() {
	return refundTotalamount;
  }
  public void setRefundTotalamount(BigDecimal refundTotalamount) {
	this.refundTotalamount = refundTotalamount;
  }
  
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="wechat_id")
  public WechatAccount getWechat() {
	return wechat;
  }
  public void setWechat(WechatAccount wechat) {
	this.wechat = wechat;
  }
  
  @Column(name="receiver_name")
  public String getReceiverName() {
	return receiverName;
  }
  public void setReceiverName(String receiverName) {
	this.receiverName = receiverName;
  }
  
  @Column(name="receive_phone")
  public String getReceivePhone() {
	return receivePhone;
  }
  public void setReceivePhone(String receivePhone) {
	this.receivePhone = receivePhone;
  }
  
  @Column(name="refund_shiper")
  public String getRefundShiper() {
	return refundShiper;
  }
  public void setRefundShiper(String refundShiper) {
	this.refundShiper = refundShiper;
  }
  
  @Column(name="refund_ship_code")
  public String getRefundShipCode() {
	return refundShipCode;
  }
  public void setRefundShipCode(String refundShipCode) {
	this.refundShipCode = refundShipCode;
  }
  
  @Column(name="refund_reson")
  public String getRefundReason() {
	return refundReason;
  }
  public void setRefundReason(String refundReason) {
	this.refundReason = refundReason;
  }
  
  


}
