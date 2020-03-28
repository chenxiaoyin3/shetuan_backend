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

@SuppressWarnings("serial")
@Entity
@Table(name="hy_advance_purchase_provider_balance")
public class AdvancePurchaseBalanceProvider
  implements Serializable
{
  private Long id;
  private String advanceSerialNum;
  private Long purchaseId;
  private Double advanceAmount;
  private String payeeName;
  private String payeeAccount;
  private String payerName;
  private String payerAccount;
  private String applicant;
  private Date applicationTime;
  private Integer status;
  private String reviewer;
  private Date reviewTime;
  private String operator;
  private Date payTime;
  
  public AdvancePurchaseBalanceProvider() {}
  
  public AdvancePurchaseBalanceProvider(Long id)
  {
    this.id = id;
  }
  
  public AdvancePurchaseBalanceProvider(Long id, String advanceSerialNum, Long purchaseId, Double advanceAmount, String payeeName, String payeeAccount, String payerName, String payerAccount, String applicant, Date applicationTime, Integer status, String reviewer, Date reviewTime, String operator, Date payTime)
  {
    this.id = id;
    this.advanceSerialNum = advanceSerialNum;
    this.purchaseId = purchaseId;
    this.advanceAmount = advanceAmount;
    this.payeeName = payeeName;
    this.payeeAccount = payeeAccount;
    this.payerName = payerName;
    this.payerAccount = payerAccount;
    this.applicant = applicant;
    this.applicationTime = applicationTime;
    this.status = status;
    this.reviewer = reviewer;
    this.reviewTime = reviewTime;
    this.operator = operator;
    this.payTime = payTime;
  }
  
  @Id
  @Column(name="ID", unique=true, nullable=false)
  @GeneratedValue(strategy=GenerationType.AUTO)
  public Long getId()
  {
    return this.id;
  }
  
  public void setId(Long id)
  {
    this.id = id;
  }
  
  @Column(name="advance_serial_num")
  public String getAdvanceSerialNum()
  {
    return this.advanceSerialNum;
  }
  
  public void setAdvanceSerialNum(String advanceSerialNum)
  {
    this.advanceSerialNum = advanceSerialNum;
  }
  
  @Column(name="purchase_id")
  public Long getPurchaseId()
  {
    return this.purchaseId;
  }
  
  public void setPurchaseId(Long purchaseId)
  {
    this.purchaseId = purchaseId;
  }
  
  @Column(name="advance_amount", precision=22, scale=0)
  public Double getAdvanceAmount()
  {
    return this.advanceAmount;
  }
  
  public void setAdvanceAmount(Double advanceAmount)
  {
    this.advanceAmount = advanceAmount;
  }
  
  @Column(name="payee_name")
  public String getPayeeName()
  {
    return this.payeeName;
  }
  
  public void setPayeeName(String payeeName)
  {
    this.payeeName = payeeName;
  }
  
  @Column(name="payee_account")
  public String getPayeeAccount()
  {
    return this.payeeAccount;
  }
  
  public void setPayeeAccount(String payeeAccount)
  {
    this.payeeAccount = payeeAccount;
  }
  
  @Column(name="payer_name")
  public String getPayerName()
  {
    return this.payerName;
  }
  
  public void setPayerName(String payerName)
  {
    this.payerName = payerName;
  }
  
  @Column(name="payer_account")
  public String getPayerAccount()
  {
    return this.payerAccount;
  }
  
  public void setPayerAccount(String payerAccount)
  {
    this.payerAccount = payerAccount;
  }
  
  @Column(name="applicant")
  public String getApplicant()
  {
    return this.applicant;
  }
  
  public void setApplicant(String applicant)
  {
    this.applicant = applicant;
  }
  
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name="application_time", length=19)
  public Date getApplicationTime()
  {
    return this.applicationTime;
  }
  
  public void setApplicationTime(Date applicationTime)
  {
    this.applicationTime = applicationTime;
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
  
  @Column(name="reviewer")
  public String getReviewer()
  {
    return this.reviewer;
  }
  
  public void setReviewer(String reviewer)
  {
    this.reviewer = reviewer;
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
  @Column(name="pay_time", length=19)
  public Date getPayTime()
  {
    return this.payTime;
  }
  
  public void setPayTime(Date payTime)
  {
    this.payTime = payTime;
  }
}
