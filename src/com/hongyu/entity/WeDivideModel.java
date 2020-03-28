package com.hongyu.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="hy_we_divide_model")
public class WeDivideModel
  implements Serializable
{
  private Long id;
  private String modelType;
  private BigDecimal store;
  private BigDecimal weBusiness;
  private BigDecimal introducer;
  private Date createTime;
  private Date endTime;
  private String operator;
  private Boolean isValid;
  
  public WeDivideModel() {}
  
  public WeDivideModel(Long id)
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
  
  @Column(name="type")
  public String getModelType()
  {
    return this.modelType;
  }
  
  public void setModelType(String modelType)
  {
    this.modelType = modelType;
  }
  
  @Column(name="store", precision=10, scale=2)
  public BigDecimal getStore()
  {
    return this.store;
  }
  
  public void setStore(BigDecimal store)
  {
    this.store = store;
  }
  
  @Column(name="we_business", precision=10, scale=2)
  public BigDecimal getWeBusiness()
  {
    return this.weBusiness;
  }
  
  public void setWeBusiness(BigDecimal weBusiness)
  {
    this.weBusiness = weBusiness;
  }
  
  @Column(name="introducer", precision=10, scale=2)
  public BigDecimal getIntroducer()
  {
    return this.introducer;
  }
  
  public void setIntroducer(BigDecimal introducer)
  {
    this.introducer = introducer;
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
  @Column(name="end_time", length=19)
  public Date getEndTime()
  {
    return this.endTime;
  }
  
  public void setEndTime(Date endTime)
  {
    this.endTime = endTime;
  }
  
  @Column(name = "operator")
  public String getOperator() {
	return operator;
  }

  public void setOperator(String operator) {
	this.operator = operator;
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
  
  @PrePersist
  public void setPrepersist() {
	  this.createTime = new Date();
	  this.isValid = true;
  }
}
