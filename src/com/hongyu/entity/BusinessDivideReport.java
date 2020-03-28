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
@Table(name="hy_business_divide_report")
public class BusinessDivideReport
  implements Serializable
{
  private Long id;
  private Integer year;
  private Integer month;
  private Date createTime;
  
  public BusinessDivideReport() {}
  
  public BusinessDivideReport(Long id)
  {
    this.id = id;
  }
  
  public BusinessDivideReport(Long id, Integer year, Integer month, Date createTime)
  {
    this.id = id;
    this.year = year;
    this.month = month;
    this.createTime = createTime;
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
  
  @Column(name="year")
  public Integer getYear()
  {
    return this.year;
  }
  
  public void setYear(Integer year)
  {
    this.year = year;
  }
  
  @Column(name="month")
  public Integer getMonth()
  {
    return this.month;
  }
  
  public void setMonth(Integer month)
  {
    this.month = month;
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
}
