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
@Table(name="hy_singleitem_promotion")
public class SingleitemPromotion
  implements Serializable
{
  private Long id;
  private Long itemId;
  private Long promotionId;
  private Integer promoteNum;
  private Integer havePromoted;
  private Boolean isActive;
  private Integer sort;
  private String creator;
  private Date createTime;
  private Date deadTime;
  private Float divideProportion;
  
  public SingleitemPromotion() {}
  
  public SingleitemPromotion(Long id)
  {
    this.id = id;
  }
  
  public SingleitemPromotion(Long id, Long itemId, Long promotionId, Integer promoteNum, Integer havePromoted, Boolean isActive, Integer sort, String creator, Date createTime, Date deadTime, Float divideProportion)
  {
    this.id = id;
    this.itemId = itemId;
    this.promotionId = promotionId;
    this.promoteNum = promoteNum;
    this.havePromoted = havePromoted;
    this.isActive = isActive;
    this.sort = sort;
    this.creator = creator;
    this.createTime = createTime;
    this.deadTime = deadTime;
    this.divideProportion = divideProportion;
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
  
  @Column(name="item_id")
  public Long getItemId()
  {
    return this.itemId;
  }
  
  public void setItemId(Long itemId)
  {
    this.itemId = itemId;
  }
  
  @Column(name="promotion_id")
  public Long getPromotionId()
  {
    return this.promotionId;
  }
  
  public void setPromotionId(Long promotionId)
  {
    this.promotionId = promotionId;
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
  
  @Column(name="have_promoted")
  public Integer getHavePromoted()
  {
    return this.havePromoted;
  }
  
  public void setHavePromoted(Integer havePromoted)
  {
    this.havePromoted = havePromoted;
  }
  
  @Column(name="is_active")
  public Boolean getIsActive()
  {
    return this.isActive;
  }
  
  public void setIsActive(Boolean isActive)
  {
    this.isActive = isActive;
  }
  
  @Column(name="sort")
  public Integer getSort()
  {
    return this.sort;
  }
  
  public void setSort(Integer sort)
  {
    this.sort = sort;
  }
  
  @Column(name="creator")
  public String getCreator()
  {
    return this.creator;
  }
  
  public void setCreator(String creator)
  {
    this.creator = creator;
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
  @Column(name="dead_time", length=19)
  public Date getDeadTime()
  {
    return this.deadTime;
  }
  
  public void setDeadTime(Date deadTime)
  {
    this.deadTime = deadTime;
  }
  
  @Column(name="divide_proportion", precision=255, scale=0)
  public Float getDivideProportion()
  {
    return this.divideProportion;
  }
  
  public void setDivideProportion(Float divideProportion)
  {
    this.divideProportion = divideProportion;
  }
}
