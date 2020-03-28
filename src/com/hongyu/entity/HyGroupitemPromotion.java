package com.hongyu.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

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
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@SuppressWarnings("serial")
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name="hy_groupitem_promotion")
public class HyGroupitemPromotion
  implements Serializable
{
  private Long id;
  private HyPromotion promotionId;
  private BigDecimal sellPrice;
  private BigDecimal marketPrice;
  private Integer limitedNum;
  private WeDivideProportion storeDivide;
  private WeDivideProportion exterStoreDivide;
  private WeDivideProportion businessPersonDivide;
  private Integer havePromoted;
  private Integer promoteNum;
  private Set<HyGroupitemPromotionDetail> hyGroupitemPromotionDetails = new HashSet<>(0);
  
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
  public HyPromotion getPromotionId()
  {
    return this.promotionId;
  }
  
  public void setPromotionId(HyPromotion promotionId)
  {
    this.promotionId = promotionId;
  }
  
  @Column(name="sell_price", precision=10)
  public BigDecimal getSellPrice()
  {
    return this.sellPrice;
  }
  
  public void setSellPrice(BigDecimal sellPrice)
  {
    this.sellPrice = sellPrice;
  }
  
  @Column(name="market_price", precision=10)
  public BigDecimal getMarketPrice()
  {
    return this.marketPrice;
  }
  
  public void setMarketPrice(BigDecimal marketPrice)
  {
    this.marketPrice = marketPrice;
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
  
  @OneToMany(fetch=FetchType.LAZY, mappedBy="hyGroupitemPromotion", cascade={javax.persistence.CascadeType.ALL}, orphanRemoval=true)
  public Set<HyGroupitemPromotionDetail> getHyGroupitemPromotionDetails()
  {
    return this.hyGroupitemPromotionDetails;
  }
  
  public void setHyGroupitemPromotionDetails(Set<HyGroupitemPromotionDetail> hyGroupitemPromotionDetails)
  {
    this.hyGroupitemPromotionDetails = hyGroupitemPromotionDetails;
  }
  
  @PrePersist
  public void prePersist()
  {
    this.havePromoted = Integer.valueOf(0);
  }
  
  @ManyToOne(fetch=FetchType.LAZY)
  @JoinColumn(name="store_divide")
  public WeDivideProportion getStoreDivide()
  {
    return this.storeDivide;
  }
  
  public void setStoreDivide(WeDivideProportion storeDivide)
  {
    this.storeDivide = storeDivide;
  }
  
  @ManyToOne(fetch=FetchType.LAZY)
  @JoinColumn(name="exter_store_divide")
  public WeDivideProportion getExterStoreDivide()
  {
    return this.exterStoreDivide;
  }
  
  public void setExterStoreDivide(WeDivideProportion exterStoreDivide)
  {
    this.exterStoreDivide = exterStoreDivide;
  }
  
  @ManyToOne(fetch=FetchType.LAZY)
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
