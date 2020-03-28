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
@Table(name="hy_purchase_balance")
public class PurchaseBalance
  implements Serializable
{
  private Long id;
  private String balanceSerialNum;
  private Long purchaseId;
  private Float balanceAmount;
  private String payeeName;
  private String payeeAccount;
  private String payerName;
  private String payerAccount;
  private String applicant;
  private Date applicationTime;
  private Integer status;
  private String purchaseManager;
  private Date purchaseManagerVerifyTime;
  private String financer;
  private Date financerVerifyTime;
  private String manager;
  private Date managerVerifyTime;
  private String operator;
  private Date payTime;
  
  public PurchaseBalance() {}
  
  public PurchaseBalance(Long id)
  {
    this.id = id;
  }
  
  public PurchaseBalance(Long id, String balanceSerialNum, Long purchaseId, Float balanceAmount, String payeeName, String payeeAccount, String payerName, String payerAccount, String applicant, Date applicationTime, Integer status, String purchaseManager, Date purchaseManagerVerifyTime, String financer, Date financerVerifyTime, String manager, Date managerVerifyTime, String operator, Date payTime)
  {
    this.id = id;
    this.balanceSerialNum = balanceSerialNum;
    this.purchaseId = purchaseId;
    this.balanceAmount = balanceAmount;
    this.payeeName = payeeName;
    this.payeeAccount = payeeAccount;
    this.payerName = payerName;
    this.payerAccount = payerAccount;
    this.applicant = applicant;
    this.applicationTime = applicationTime;
    this.status = status;
    this.purchaseManager = purchaseManager;
    this.purchaseManagerVerifyTime = purchaseManagerVerifyTime;
    this.financer = financer;
    this.financerVerifyTime = financerVerifyTime;
    this.manager = manager;
    this.managerVerifyTime = managerVerifyTime;
    this.operator = operator;
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
  
  @Column(name="balance_serial_num")
  public String getBalanceSerialNum()
  {
    return this.balanceSerialNum;
  }
  
  public void setBalanceSerialNum(String balanceSerialNum)
  {
    this.balanceSerialNum = balanceSerialNum;
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
  
  @Column(name="balance_amount", precision=12, scale=0)
  public Float getBalanceAmount()
  {
    return this.balanceAmount;
  }
  
  public void setBalanceAmount(Float balanceAmount)
  {
    this.balanceAmount = balanceAmount;
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
  
  @Column(name="purchase_manager")
  public String getPurchaseManager()
  {
    return this.purchaseManager;
  }
  
  public void setPurchaseManager(String purchaseManager)
  {
    this.purchaseManager = purchaseManager;
  }
  
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name="purchase_manager_verify_time", length=19)
  public Date getPurchaseManagerVerifyTime()
  {
    return this.purchaseManagerVerifyTime;
  }
  
  public void setPurchaseManagerVerifyTime(Date purchaseManagerVerifyTime)
  {
    this.purchaseManagerVerifyTime = purchaseManagerVerifyTime;
  }
  
  @Column(name="financer")
  public String getFinancer()
  {
    return this.financer;
  }
  
  public void setFinancer(String financer)
  {
    this.financer = financer;
  }
  
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name="financer_verify_time", length=19)
  public Date getFinancerVerifyTime()
  {
    return this.financerVerifyTime;
  }
  
  public void setFinancerVerifyTime(Date financerVerifyTime)
  {
    this.financerVerifyTime = financerVerifyTime;
  }
  
  @Column(name="manager")
  public String getManager()
  {
    return this.manager;
  }
  
  public void setManager(String manager)
  {
    this.manager = manager;
  }
  
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name="manager_verify_time", length=19)
  public Date getManagerVerifyTime()
  {
    return this.managerVerifyTime;
  }
  
  public void setManagerVerifyTime(Date managerVerifyTime)
  {
    this.managerVerifyTime = managerVerifyTime;
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
