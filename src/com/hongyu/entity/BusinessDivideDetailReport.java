package com.hongyu.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="hy_business_divide_detail_report")
public class BusinessDivideDetailReport
  implements Serializable
{
  private Long id;
  private Long webusinessId;
  private Float salesAmount;
  private Float benefit;
  private Float divideAmount;
  private Long webusinessReportId;
  
  public BusinessDivideDetailReport() {}
  
  public BusinessDivideDetailReport(Long id)
  {
    this.id = id;
  }
  
  public BusinessDivideDetailReport(Long id, Long webusinessId, Float salesAmount, Float benefit, Float divideAmount, Long webusinessReportId)
  {
    this.id = id;
    this.webusinessId = webusinessId;
    this.salesAmount = salesAmount;
    this.benefit = benefit;
    this.divideAmount = divideAmount;
    this.webusinessReportId = webusinessReportId;
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
  
  @Column(name="webusiness_id")
  public Long getWebusinessId()
  {
    return this.webusinessId;
  }
  
  public void setWebusinessId(Long webusinessId)
  {
    this.webusinessId = webusinessId;
  }
  
  @Column(name="sales_amount", precision=12, scale=0)
  public Float getSalesAmount()
  {
    return this.salesAmount;
  }
  
  public void setSalesAmount(Float salesAmount)
  {
    this.salesAmount = salesAmount;
  }
  
  @Column(name="benefit", precision=12, scale=0)
  public Float getBenefit()
  {
    return this.benefit;
  }
  
  public void setBenefit(Float benefit)
  {
    this.benefit = benefit;
  }
  
  @Column(name="divide_amount", precision=12, scale=0)
  public Float getDivideAmount()
  {
    return this.divideAmount;
  }
  
  public void setDivideAmount(Float divideAmount)
  {
    this.divideAmount = divideAmount;
  }
  
  @Column(name="webusiness_report_id")
  public Long getWebusinessReportId()
  {
    return this.webusinessReportId;
  }
  
  public void setWebusinessReportId(Long webusinessReportId)
  {
    this.webusinessReportId = webusinessReportId;
  }
}
