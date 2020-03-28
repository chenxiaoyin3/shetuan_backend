package com.hongyu.entity;

import java.io.Serializable;
import java.util.ArrayList;
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
@Table(name="hy_specialty_appraise")
public class SpecialtyAppraise
  implements Serializable
{
  private Long id;
  //评论特产
  private Specialty specialty;
  //评论特产规格
  private SpecialtySpecification specification;
  //特产订单
  private BusinessOrder businessOrder;
  //特产订单明细
  private BusinessOrderItem orderItem;
  //评论的微信账号
  private WechatAccount account;
  //评论时间
  private Date appraiseTime;
  //删除时间
  private Date deleteTime;
  //评论内容
  private String appraiseContent;
  //满意度
  private Integer contentLevel;
  //是否晒单
  private Boolean isShow;
  //是否可见
  private Boolean isValid;
  //评论更新时间
  private Date updateDate;
  //是否匿名
  private Boolean isAnonymous;
  //评论图片
  private List<SpecialtyAppraiseImage> images = new ArrayList<SpecialtyAppraiseImage>();
  
  public SpecialtyAppraise() {}
  
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
  
  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="specialty_id")
  public Specialty getSpecialty() {
	return specialty;
  }

  public void setSpecialty(Specialty specialty) {
	this.specialty = specialty;
  }
  
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="account_id")
  public WechatAccount getAccount()
  {
    return this.account;
  }
  
  public void setAccount(WechatAccount account)
  {
    this.account = account;
  }
  
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name="appraise_time", length=19)
  public Date getAppraiseTime()
  {
    return this.appraiseTime;
  }
  
  public void setAppraiseTime(Date appraiseTime)
  {
    this.appraiseTime = appraiseTime;
  }
  
  @Column(name="appraise_content")
  public String getAppraiseContent()
  {
    return this.appraiseContent;
  }
  
  public void setAppraiseContent(String appraiseContent)
  {
    this.appraiseContent = appraiseContent;
  }
  
  @Column(name="content_level")
  public Integer getContentLevel()
  {
    return this.contentLevel;
  }
  
  public void setContentLevel(Integer contentLevel)
  {
    this.contentLevel = contentLevel;
  }
  
  @Column(name="is_show")
  public Boolean getIsShow()
  {
    return this.isShow;
  }
  
  public void setIsShow(Boolean isShow)
  {
    this.isShow = isShow;
  }
  
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name="update_date", length=19)
  public Date getUpdateDate()
  {
    return this.updateDate;
  }
  
  public void setUpdateDate(Date updateDate)
  {
    this.updateDate = updateDate;
  }
  
  @Column(name="is_anonymous")
  public Boolean getIsAnonymous()
  {
    return this.isAnonymous;
  }
  
  public void setIsAnonymous(Boolean isAnonymous)
  {
    this.isAnonymous = isAnonymous;
  }
  
  @Column(name="is_valid")
  public Boolean getIsValid() {
	return isValid;
  }

  public void setIsValid(Boolean isValid) {
	this.isValid = isValid;
  }

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name="delete_date", length=19)
  public Date getDeleteTime() {
	return deleteTime;
  }

  public void setDeleteTime(Date deleteTime) {
	this.deleteTime = deleteTime;
  }
  
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "appraise", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("orders asc")
  public List<SpecialtyAppraiseImage> getImages() {
	return images;
  }

  public void setImages(List<SpecialtyAppraiseImage> images) {
	this.images = images;
  }
  
  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "specification_id")
  public SpecialtySpecification getSpecification() {
	return specification;
  }

  public void setSpecification(SpecialtySpecification specification) {
	this.specification = specification;
  }
  
  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "business_order_id")
  public BusinessOrder getBusinessOrder() {
	return businessOrder;
  }

  public void setBusinessOrder(BusinessOrder businessOrder) {
	this.businessOrder = businessOrder;
  }
  
  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "orderitem_id")
  public BusinessOrderItem getOrderItem() {
	return orderItem;
  }

  public void setOrderItem(BusinessOrderItem orderItem) {
	this.orderItem = orderItem;
  }
  
  	@PrePersist
	public void setPrepersist() {
		this.appraiseTime = new Date();
		this.setIsValid(true);
	}
}
