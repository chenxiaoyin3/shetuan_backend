package com.hongyu.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
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
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@Entity
@Table(name="hy_provider")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Provider
  implements Serializable
{
  private Long id;
  private Integer providerType;
  private String providerName;
  private String address;
  private String postcode;
  private String introduction;
  private Boolean isContracted;
  private String contractNumber;
  private Date startTime;
  private Date endTime;
  private Boolean state;
  private HyAdmin operator;
  private String contactorName;
  private String contactorMobile;
  private String contactorEmail;
  private String contactorWechat;
  private String contactorQq;
  private String contactorPostcode;
  private String bankName;
  private String accountName;
  private String bankAccount;
  private Integer accountType;
  private String remark;
  private String bankCode;
  private Date createTime;
  private Date modifyTime;
  private Date cancelTime;
  private HyAdmin account;
  //结算类型 0：单结  1：月结
  private Integer balanceType;
  //只在月结的时候起作用，单结时默认为0
  private Integer balanceDate;
  private List<Specialty> specialtyList;
  
  public Provider() {}
  
  public Provider(Long id)
  {
    this.id = id;
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
  
  @Column(name="provider_type")
  public Integer getProviderType()
  {
    return this.providerType;
  }
  
  public void setProviderType(Integer providerType)
  {
    this.providerType = providerType;
  }
  
  @Column(name="provider_name")
  public String getProviderName()
  {
    return this.providerName;
  }
  
  public void setProviderName(String providerName)
  {
    this.providerName = providerName;
  }
  
  @Column(name="address")
  public String getAddress()
  {
    return this.address;
  }
  
  public void setAddress(String address)
  {
    this.address = address;
  }
  
  @Column(name="postcode")
  public String getPostcode()
  {
    return this.postcode;
  }
  
  public void setPostcode(String postcode)
  {
    this.postcode = postcode;
  }
  
  @Column(name="introduction")
  public String getIntroduction()
  {
    return this.introduction;
  }
  
  public void setIntroduction(String introduction)
  {
    this.introduction = introduction;
  }
  
  @Column(name="is_contracted")
  public Boolean getIsContracted()
  {
    return this.isContracted;
  }
  
  public void setIsContracted(Boolean isContracted)
  {
    this.isContracted = isContracted;
  }
  
  @Column(name="contract_number")
  public String getContractNumber()
  {
    return this.contractNumber;
  }
  
  public void setContractNumber(String contractNumber)
  {
    this.contractNumber = contractNumber;
  }
  
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name="start_time", length=19)
  public Date getStartTime()
  {
    return this.startTime;
  }
  
  public void setStartTime(Date startTime)
  {
    this.startTime = startTime;
  }
  
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name="end_time", length=19)
  public Date getEndTime()
  {
    return this.endTime;
  }
  
  public void setEndTime(Date endTime)
  {
    this.endTime = endTime;
  }
  
  @Column(name="state")
  public Boolean getState()
  {
    return this.state;
  }
  
  public void setState(Boolean state)
  {
    this.state = state;
  }
  
  @JsonIgnore
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
  
  @Column(name="contactor_name")
  public String getContactorName()
  {
    return this.contactorName;
  }
  
  public void setContactorName(String contactorName)
  {
    this.contactorName = contactorName;
  }
  
  @Column(name="contactor_mobile")
  public String getContactorMobile()
  {
    return this.contactorMobile;
  }
  
  public void setContactorMobile(String contactorMobile)
  {
    this.contactorMobile = contactorMobile;
  }
  
  @Column(name="contactor_email")
  public String getContactorEmail()
  {
    return this.contactorEmail;
  }
  
  public void setContactorEmail(String contactorEmail)
  {
    this.contactorEmail = contactorEmail;
  }
  
  @Column(name="contactor_wechat")
  public String getContactorWechat()
  {
    return this.contactorWechat;
  }
  
  public void setContactorWechat(String contactorWechat)
  {
    this.contactorWechat = contactorWechat;
  }
  
  @Column(name="contactor_qq")
  public String getContactorQq()
  {
    return this.contactorQq;
  }
  
  public void setContactorQq(String contactorQq)
  {
    this.contactorQq = contactorQq;
  }
  
  @Column(name="contactor_postcode")
  public String getContactorPostcode()
  {
    return this.contactorPostcode;
  }
  
  public void setContactorPostcode(String contactorPostcode)
  {
    this.contactorPostcode = contactorPostcode;
  }
  
  @Column(name="bank_name")
  public String getBankName()
  {
    return this.bankName;
  }
  
  public void setBankName(String bankName)
  {
    this.bankName = bankName;
  }
  
  @Column(name="account_name")
  public String getAccountName()
  {
    return this.accountName;
  }
  
  public void setAccountName(String accountName)
  {
    this.accountName = accountName;
  }
  
  @Column(name="bank_account")
  public String getBankAccount()
  {
    return this.bankAccount;
  }
  
  public void setBankAccount(String bankAccount)
  {
    this.bankAccount = bankAccount;
  }
  
  @Column(name="account_type")
  public Integer getAccountType()
  {
    return this.accountType;
  }
  
  public void setAccountType(Integer accountType)
  {
    this.accountType = accountType;
  }
  
  @Column(name="remark")
  public String getRemark()
  {
    return this.remark;
  }
  
  public void setRemark(String remark)
  {
    this.remark = remark;
  }
  
  @Column(name="bank_code")
  public String getBankCode()
  {
    return this.bankCode;
  }
  
  public void setBankCode(String bankCode)
  {
    this.bankCode = bankCode;
  }
  
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name="create_time", length=19)
  public Date getCreateTime()
  {
    return this.createTime;
  }
  
  public void setCreateTime(Date createTime)
  {
    this.createTime = createTime;
  }
  
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name="modify_time", length=19)
  public Date getModifyTime()
  {
    return this.modifyTime;
  }
  
  public void setModifyTime(Date modifyTime)
  {
    this.modifyTime = modifyTime;
  }
  
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name="cancel_time", length=19)
  public Date getCancelTime()
  {
    return this.cancelTime;
  }
  
  public void setCancelTime(Date cancelTime)
  {
    this.cancelTime = cancelTime;
  }
  
  @Column(name="balance_type")
  public Integer getBalanceType() {
	return balanceType;
  }

  public void setBalanceType(Integer balanceType) {
	this.balanceType = balanceType;
  }
  
  @Column(name="balance_date")
  public Integer getBalanceDate() {
	return balanceDate;
  }

  public void setBalanceDate(Integer balanceDate) {
	this.balanceDate = balanceDate;
  }

  @JsonIgnore
  @OneToMany(fetch=FetchType.LAZY, mappedBy="provider")
  public List<Specialty> getSpecialtyList()
  {
    return this.specialtyList;
  }
  
  public void setSpecialtyList(List<Specialty> specialtyList)
  {
    this.specialtyList = specialtyList;
  }
  
  @ManyToOne(fetch=FetchType.LAZY, cascade={javax.persistence.CascadeType.ALL})
  @JoinColumn(name="account_user")
  public HyAdmin getAccount()
  {
    return this.account;
  }
  
  public void setAccount(HyAdmin account)
  {
    this.account = account;
  }
  
  @PrePersist
  public void setPrepersist()
  {
    this.createTime = new Date();
  }
  
  @PreUpdate
  public void setPreupdate()
  {
    this.modifyTime = new Date();
  }
}
