package com.hongyu.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="hy_mh_setting")
public class MhSetting implements Serializable{
	  private Long id;
	  private String settingName;
	  private String settingValue;
	  private String operator;
	  private Date createTime;
	  private Date endTime;
	  private Boolean isValid;
	  
	  public MhSetting() {}
	  
	  public MhSetting(Long id)
	  {
	    this.id = id;
	  }
	  
	  public MhSetting(Long id, String settingName, String settingValue, String operator, Date createTime, Date endTime, Boolean isValid)
	  {
	    this.id = id;
	    this.settingName = settingName;
	    this.settingValue = settingValue;
	    this.operator = operator;
	    this.createTime = createTime;
	    this.endTime = endTime;
	    this.isValid = isValid;
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
	  
	  @Column(name="setting_name")
	  public String getSettingName()
	  {
	    return this.settingName;
	  }
	  
	  public void setSettingName(String settingName)
	  {
	    this.settingName = settingName;
	  }
	  
	  @Column(name="setting_value")
	  public String getSettingValue()
	  {
	    return this.settingValue;
	  }
	  
	  public void setSettingValue(String settingValue)
	  {
	    this.settingValue = settingValue;
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
