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

import org.quartz.spi.InstanceIdGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;

@SuppressWarnings("serial")
@Entity
@Table(name="mh_appraise")
public class MhAppraise implements Serializable {
  private Long id;
  //评论产品 如果是特产则不为空 否则为空
  //private Specialty specialty;
  
  //订单条目类型 1特产 2其它所有
  private Integer orderItemType;
  
  
  //评论特产规格
  //private SpecialtySpecification specification;
  //特产订单
  //private HyOrder hyOrder;
  
  
  //外键（订单条目ID），1时指向 hy_business_order_item，2时指向 hy_order_item
  private Long orderItemId;
  
//  //评论的微信账号
//  private WechatAccount account;
  
  //官网用户账号
  private HyUser appraiser;
  //评论时间
  private Date appraiseTime;
  //删除时间
  private Date deleteTime;
  //评论内容
  private String appraiseContent;
  //满意度
  private Integer contentLevel;
  //是否晒单 0不晒单 1晒单 默认1
  private Boolean isShow;
  //是否可见 0用户删除 1可见 2系统管理员删除
  private Integer isValid;
  //评论更新时间
  private Date updateDate;
  //是否匿名 0不匿名 1匿名 默认为0
  private Boolean isAnonymous;
  //评论图片
  private List<MhAppraiseImage> images = new ArrayList<MhAppraiseImage>();
  
  public MhAppraise() {}
  
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
  
//  @JsonIgnore
//  @ManyToOne(fetch = FetchType.LAZY)
//  @JoinColumn(name="specialty_id")
//  public Specialty getSpecialty() {
//	return specialty;
//  }
//
//  public void setSpecialty(Specialty specialty) {
//	this.specialty = specialty;
//  }
  
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="appraiser_id")
  public HyUser getAppraiser() {
	return appraiser;
  }

  public void setAppraiser(HyUser appraiser) {
	this.appraiser = appraiser;
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
  public Integer getIsValid() {
	return isValid;
  }

  public void setIsValid(Integer isValid) {
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
  public List<MhAppraiseImage> getImages() {
	return images;
  }

  public void setImages(List<MhAppraiseImage> images) {
	this.images = images;
  }
  
  
  
  	@Column(name="order_item_type")
  	public Integer getOrderItemType() {
  		return orderItemType;
	}
	
	public void setOrderItemType(Integer orderItemType) {
		this.orderItemType = orderItemType;
	}
	
	@Column(name="order_item_id")
	public Long getOrderItemId() {
		return orderItemId;
	}
	
	public void setOrderItemId(Long orderItemId) {
		this.orderItemId = orderItemId;
	}

	@PrePersist
	public void setPrepersist() {
		this.appraiseTime = new Date();
		this.setIsValid(1);
	}
}
