package com.hongyu.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@SuppressWarnings("serial")
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name="hy_singleitem_promotion")
public class HySingleitemPromotion
  implements Serializable
{
  private Long id;
  private HyPromotion hyPromotion;
  private Specialty specialtyId;
  private SpecialtySpecification specificationId;
  private Integer limitedNum;
  private WeDivideProportion storeDivide;
  private WeDivideProportion exterStoreDivide;
  private WeDivideProportion businessPersonDivide;
  private Integer havePromoted;
  private Integer promoteNum;
  
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  @Column(name="ID", unique=true, nullable=false)
  public Long getId()
  {
    return this.id;
  }
  
  public void setId(Long id)
  {
    this.id = id;
  }
  
  @ManyToOne(fetch=FetchType.LAZY)
  @JoinColumn(name="promotion_id")
  public HyPromotion getHyPromotion()
  {
    return this.hyPromotion;
  }
  
  public void setHyPromotion(HyPromotion hyPromotion)
  {
    this.hyPromotion = hyPromotion;
  }
  
  @ManyToOne(fetch=FetchType.LAZY)
  @JoinColumn(name="specialty_id")
  public Specialty getSpecialtyId()
  {
    return this.specialtyId;
  }
  
  public void setSpecialtyId(Specialty specialtyId)
  {
    this.specialtyId = specialtyId;
  }
  
  @ManyToOne(fetch=FetchType.LAZY)
  @JoinColumn(name="specification_id")
  public SpecialtySpecification getSpecificationId()
  {
    return this.specificationId;
  }
  
  public void setSpecificationId(SpecialtySpecification specificationId)
  {
    this.specificationId = specificationId;
  }
  
  @Column(name="have_promoted")
  public Integer getHavePromoted()
  {
    return this.havePromoted;
  }
  
  public void setHavePromoted(Integer havePromoted)
  {
    this.havePromoted = havePromoted;
  }
  
  @Column(name="promote_num")
  public Integer getPromoteNum()
  {
    return this.promoteNum;
  }
  
  public void setPromoteNum(Integer promoteNum)
  {
    this.promoteNum = promoteNum;
  }
  
  @PrePersist
  public void prePersist()
  {
    this.havePromoted = Integer.valueOf(0);
  }
  
  @ManyToOne(fetch=FetchType.EAGER)
  @JoinColumn(name="store_divide")
  public WeDivideProportion getStoreDivide()
  {
    return this.storeDivide;
  }
  
  public void setStoreDivide(WeDivideProportion storeDivide)
  {
    this.storeDivide = storeDivide;
  }
  
  @ManyToOne(fetch=FetchType.EAGER)
  @JoinColumn(name="exter_store_divide")
  public WeDivideProportion getExterStoreDivide()
  {
    return this.exterStoreDivide;
  }
  
  public void setExterStoreDivide(WeDivideProportion exterStoreDivide)
  {
    this.exterStoreDivide = exterStoreDivide;
  }
  
  @ManyToOne(fetch=FetchType.EAGER)
  @JoinColumn(name="business_person_divide")
  public WeDivideProportion getBusinessPersonDivide()
  {
    return this.businessPersonDivide;
  }
  
  public void setBusinessPersonDivide(WeDivideProportion businessPersonDivide)
  {
    this.businessPersonDivide = businessPersonDivide;
  }
  
  public Integer getLimitedNum()
  {
    return this.limitedNum;
  }
  
  public void setLimitedNum(Integer limitedNum)
  {
    this.limitedNum = limitedNum;
  }
}
