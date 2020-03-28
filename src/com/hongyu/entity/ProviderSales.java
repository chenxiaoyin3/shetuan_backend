package com.hongyu.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name="hy_provider_sales")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ProviderSales
  implements Serializable
{
  private Long id;
  private Provider providerId;
  private Specialty specialtyId;
  private SpecialtySpecification specialtySpecificationId;
  private Integer salesQuantity;
  private BigDecimal salesAmount;
  private Date balanceTime;
  private Date salesTime;
  
  public ProviderSales() {}
  
  
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
  
  @JoinColumn(name="provider_id")
  public Provider getProviderId()
  {
    return this.providerId;
  }
  
  public void setProviderId(Provider providerId)
  {
    this.providerId = providerId;
  }
  
  @JoinColumn(name="specialty_id")
  public Specialty getSpecialtyId()
  {
    return this.specialtyId;
  }
  
  public void setSpecialtyId(Specialty specialtyId)
  {
    this.specialtyId = specialtyId;
  }
  
  @JoinColumn(name="specialty_specification_id")
  public SpecialtySpecification getSpecialtySpecificationId()
  {
    return this.specialtySpecificationId;
  }
  
  public void setSpecialtySpecificationId(SpecialtySpecification specialtySpecificationId)
  {
    this.specialtySpecificationId = specialtySpecificationId;
  }
  
  @Column(name="sales_quantity")
  public Integer getSalesQuantity()
  {
    return this.salesQuantity;
  }
  
  public void setSalesQuantity(Integer salesQuantity)
  {
    this.salesQuantity = salesQuantity;
  }
  
  @Column(name="sales_amount", precision=10, scale=2)
  public BigDecimal getSalesAmount()
  {
    return this.salesAmount;
  }
  
  public void setSalesAmount(BigDecimal salesAmount)
  {
    this.salesAmount = salesAmount;
  }
  
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name="balance_time", length=19)
  public Date getBalanceTime()
  {
    return this.balanceTime;
  }
  
  public void setBalanceTime(Date balanceTime)
  {
    this.balanceTime = balanceTime;
  }

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name="sales_time", length=19)
  public Date getSalesTime() {
	return salesTime;
  }


  public void setSalesTime(Date salesTime) {
	this.salesTime = salesTime;
  }
  
  @PrePersist
  public void setPrepersist()
  {
    this.balanceTime = new Date();
  }
  
  
}
