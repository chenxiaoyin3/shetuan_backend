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
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "hyGroupitemPromotion"})
@Table(name="hy_groupitem_promotion_detail")
public class HyGroupitemPromotionDetail
  implements Serializable
{
  private Long id;
  private HyGroupitemPromotion hyGroupitemPromotion;
  private Integer buyNumber;
  private Specialty itemId;
  private SpecialtySpecification itemSpecificationId;
  
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
  @JoinColumn(name="group_item_promotion_id")
  public HyGroupitemPromotion getHyGroupitemPromotion()
  {
    return this.hyGroupitemPromotion;
  }
  
  public void setHyGroupitemPromotion(HyGroupitemPromotion hyGroupitemPromotion)
  {
    this.hyGroupitemPromotion = hyGroupitemPromotion;
  }
  
  @Column(name="buy_number")
  public Integer getBuyNumber()
  {
    return this.buyNumber;
  }
  
  public void setBuyNumber(Integer buyNumber)
  {
    this.buyNumber = buyNumber;
  }
  
  @ManyToOne(fetch=FetchType.LAZY)
  @JoinColumn(name="item_id")
  public Specialty getItemId()
  {
    return this.itemId;
  }
  
  public void setItemId(Specialty itemId)
  {
    this.itemId = itemId;
  }
  
  @ManyToOne(fetch=FetchType.LAZY)
  @JoinColumn(name="item_specification_id")
  public SpecialtySpecification getItemSpecificationId()
  {
    return this.itemSpecificationId;
  }
  
  public void setItemSpecificationId(SpecialtySpecification itemSpecificationId)
  {
    this.itemSpecificationId = itemSpecificationId;
  }
}
