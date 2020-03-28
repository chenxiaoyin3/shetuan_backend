package com.hongyu.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="hy_specialty_category")
public class SpecialtyCategory
  implements Serializable
{
  private static final Long serialVersionUID = Long.valueOf(-6882511630469364576L);
  private Long id;
  private String name;
  private SpecialtyCategory parent;
  private Long orders;
  private Boolean ishow;
  private String operator;
  private Date createTime;
  private Date deadTime;
  private Boolean isActive;
  private String iconUrl;
  private List<SpecialtyCategory> childSpecialtyCategory;
  
  public SpecialtyCategory() {}
  
  public SpecialtyCategory(Long id)
  {
    this.id = id;
  }
  
  public SpecialtyCategory(Long id, String name, SpecialtyCategory parent, Long orders, Boolean ishow, String operator, Date createTime, Boolean isActive, String iconUrl)
  {
    this.id = id;
    this.name = name;
    this.parent = parent;
    this.orders = orders;
    this.ishow = ishow;
    this.operator = operator;
    this.createTime = createTime;
    this.isActive = isActive;
    this.iconUrl = iconUrl;
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
  
  @Column(name="name")
  public String getName()
  {
    return this.name;
  }
  
  public void setName(String name)
  {
    this.name = name;
  }
  
  @ManyToOne(fetch=FetchType.EAGER)
  @JoinColumn(name="pid")
  public SpecialtyCategory getParent()
  {
    return this.parent;
  }
  
  public void setParent(SpecialtyCategory parent)
  {
    this.parent = parent;
  }
  
  @Column(name="orders")
  public Long getOrders()
  {
    return this.orders;
  }
  
  public void setOrders(Long orders)
  {
    this.orders = orders;
  }
  
  @Column(name="ishow")
  public Boolean getIshow()
  {
    return this.ishow;
  }
  
  public void setIshow(Boolean ishow)
  {
    this.ishow = ishow;
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
  @Column(name="dead_time", length=19)
  public Date getDeadTime() {
	return deadTime;
  }

  public void setDeadTime(Date deadTime) {
	this.deadTime = deadTime;
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
  
  @Column(name="icon_url")
  public String getIconUrl()
  {
    return this.iconUrl;
  }
  
  public void setIconUrl(String iconUrl)
  {
    this.iconUrl = iconUrl;
  }
  
  @JsonIgnore
  @OneToMany(mappedBy="parent", fetch=FetchType.LAZY, cascade={CascadeType.REMOVE})
  @OrderBy("id asc")
  public List<SpecialtyCategory> getChildSpecialtyCategory()
  {
    return this.childSpecialtyCategory;
  }
  
  public void setChildSpecialtyCategory(List<SpecialtyCategory> childSpecialtyCategory)
  {
    this.childSpecialtyCategory = childSpecialtyCategory;
  }
  
  @PrePersist
  public void setPrepersist() {
	  this.setCreateTime(new Date());
  }
}
