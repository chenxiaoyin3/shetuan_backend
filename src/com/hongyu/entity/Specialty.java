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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler","hyLabel"
})
@Table(name="hy_specialty")
public class Specialty
  implements Serializable
{	
  private Long id;
  //产品编号
  private String code;
  //商品名称
  private String name;
  //特产分区
  private SpecialtyCategory category;
  //产品描述
  private String descriptions;
  //原产地
  private String originalPlace;
  //品牌
  private String brand;
/*  //产品图标
  private String iconUrl;*/
  //储藏方法
  private String storageMethod;
/*  //规格类型，比如箱，袋，盒，罐
  private String specificationType;*/
  //排序
  private Integer orders;
  //是否推荐
  private Boolean isRecommend;
  //推荐序号
  private Integer recommendOrder;
  //是否可退
  private Boolean isReturnable;
 //是否作为赠品赠送,别删，有用
  private Boolean isFreeGift;
  
  //该特产对应的可作为赠品的规格的数量
  private Integer numberOfFreeGift;
  
  //是否可使用电子券
  private Boolean couponAvailable;
  //是否有效
  private Boolean isActive;
  //供应商
  private Provider provider;
  //送货类型，0：免费送货到家，1：加钱送货到家
  private Integer shipType;
  //发货源类型，0：平台，1：供应商
  private Integer deliverType;
  //销售状态，0：未上架，1：销售中
  private Integer saleState;
/*  //审核状态，0：未审核，1：审核通过，2：审核未通过
  private Integer auditState;*/
  //上架时间
  private Date putonTime;
  //下架时间
  private Date putoffTime;
  //创建时间
  private Date createTime;
  //修改时间
  private Date modifyTime;
  //销售地区
  private HyArea area;
  //生产许可证
  private String productionLicenseNumber;
//  //产品标准
//  private String productionStandard;
//  //保质期
//  private String durabilityPeriod;
//  //毛重
//  private String grossWeight;
//  //特殊属性
//  private String specialtyProperty;
  //创建人账号
  private HyAdmin creator;
  //创建人姓名
  private String creatorName;
  
  //修改人姓名
  private String modifierName;
  
  //是否在banner显示
  private Boolean isBanner;
  
  //2018-8-15 cwz label字段
  private List<HyLabel> hyLabel;
  
  private Long appraiseCount;
  
  private List<SpecialtySpecification> specifications = new ArrayList<SpecialtySpecification>();
  //特产图片
  private List<SpecialtyImage> images = new ArrayList<SpecialtyImage>();
  //被哪些产品推荐
  @JsonIgnore
  private List<Specialty> specialtiesForSpeciltyId = new ArrayList<Specialty>();
  //推荐产品列表
//  @JsonIgnore
  private List<Specialty> specialtiesForRecommendSpecialtyId = new ArrayList<Specialty>();
  
  
  /**基础销量*/
  private Integer baseSaleNumber;

  public Specialty() {}
  
  public Specialty(Long id)
  {
    this.id = id;
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
  
  
  @Column(name="code")
  public String getCode()
  {
    return this.code;
  }
  
  //add by cwz 2018-8-15 不一定对 
  @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
	@JoinTable(name = "hy_specialty_label",  joinColumns = {
			@JoinColumn(name = "specialty_id", nullable = false, updatable = false) }, inverseJoinColumns = {
					@JoinColumn(name = "label_id", nullable = false, updatable = false) })
  public List<HyLabel> getHyLabel() {
	  return hyLabel;
  }
  public void setHyLabel(List<HyLabel> hyLabel) {
	  this.hyLabel = hyLabel;
  }

  
public void setCode(String code)
  {
    this.code = code;
  }
  
  @Column(name="name", nullable = false)
  public String getName()
  {
    return this.name;
  }
  
  public void setName(String name)
  {
    this.name = name;
  }
  
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name="category_id", nullable = false)
  public SpecialtyCategory getCategory()
  {
    return this.category;
  }
  
  public void setCategory(SpecialtyCategory category)
  {
    this.category = category;
  }
  
  @Column(name="descriptions")
  public String getDescriptions()
  {
    return this.descriptions;
  }
  
  public void setDescriptions(String descriptions)
  {
    this.descriptions = descriptions;
  }
  
/*  @Column(name="specification_type")
  public String getSpecificationType() {
	return specificationType;
  }

  public void setSpecificationType(String specificationType) {
	this.specificationType = specificationType;
  }*/

  @Column(name="original_place")
  public String getOriginalPlace()
  {
    return this.originalPlace;
  }
  
  public void setOriginalPlace(String originalPlace)
  {
    this.originalPlace = originalPlace;
  }
  
  @Column(name="brand")
  public String getBrand()
  {
    return this.brand;
  }
  
  public void setBrand(String brand)
  {
    this.brand = brand;
  }
  
/*  @Column(name="icon_url")
  public String getIconUrl()
  {
    return this.iconUrl;
  }
  
  public void setIconUrl(String iconUrl)
  {
    this.iconUrl = iconUrl;
  }*/
  
  @Column(name="storage_method")
  public String getStorageMethod()
  {
    return this.storageMethod;
  }
  
  public void setStorageMethod(String storageMethod)
  {
    this.storageMethod = storageMethod;
  }
  
  @Column(name="orders")
  public Integer getOrders()
  {
    return this.orders;
  }
  
  public void setOrders(Integer orders)
  {
    this.orders = orders;
  }
  
  @Column(name="is_recommend")
  public Boolean getIsRecommend()
  {
    return this.isRecommend;
  }
  
  public void setIsRecommend(Boolean isRecommend)
  {
    this.isRecommend = isRecommend;
  }
  
  @Column(name="recommend_order")
  public Integer getRecommendOrder() {
	return recommendOrder;
  }

  public void setRecommendOrder(Integer recommendOrder) {
	this.recommendOrder = recommendOrder;
  }

  @Column(name="is_returnable")
  public Boolean getIsReturnable()
  {
    return this.isReturnable;
  }
  
  public void setIsReturnable(Boolean isReturnable)
  {
    this.isReturnable = isReturnable;
  }
  //别删，有用
 @Column(name="is_free_gift")
  public Boolean getIsFreeGift()
  {
    return this.isFreeGift;
  }
  
  public void setIsFreeGift(Boolean isFreeGift)
  {
    this.isFreeGift = isFreeGift;
  }
  
  @Column(name="coupon_available")
  public Boolean getCouponAvailable()
  {
    return this.couponAvailable;
  }
  
  public void setCouponAvailable(Boolean couponAvailable)
  {
    this.couponAvailable = couponAvailable;
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
  
//  @JsonIgnore
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name="provider_id")
  public Provider getProvider()
  {
    return this.provider;
  }
  
  public void setProvider(Provider provider)
  {
    this.provider = provider;
  }
  
  @Column(name="ship_type")
  public Integer getShipType()
  {
    return this.shipType;
  }
  
  public void setShipType(Integer shipType)
  {
    this.shipType = shipType;
  }
  
  @Column(name="deliver_type")
  public Integer getDeliverType()
  {
    return this.deliverType;
  }
  
  public void setDeliverType(Integer deliverType)
  {
    this.deliverType = deliverType;
  }
  
  @Column(name="sale_state")
  public Integer getSaleState()
  {
    return this.saleState;
  }
  
  public void setSaleState(Integer saleState)
  {
    this.saleState = saleState;
  }
  
/*  @Column(name="audit_state")
  public Integer getAuditState() {
	return auditState;
 }

  public void setAuditState(Integer auditState) {
	this.auditState = auditState;
  }*/

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name="puton_time", length=19)
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  public Date getPutonTime()
  {
    return this.putonTime;
  }
  
  public void setPutonTime(Date putonTime)
  {
    this.putonTime = putonTime;
  }
  
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name="putoff_time", length=19)
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  public Date getPutoffTime()
  {
    return this.putoffTime;
  }
  
  public void setPutoffTime(Date putoffTime)
  {
    this.putoffTime = putoffTime;
  }
  
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name="create_time", length=19)
  public Date getCreateTime() {
	return createTime;
  }

  public void setCreateTime(Date createTime) {
	this.createTime = createTime;
  }
  
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "specialty", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("id asc")
  public List<SpecialtySpecification> getSpecifications() {
	return specifications;
  }

  public void setSpecifications(List<SpecialtySpecification> specifications) {
	this.specifications = specifications;
  }
  
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "specialty", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("id asc")
  	public List<SpecialtyImage> getImages() {
	return images;
  }

  public void setImages(List<SpecialtyImage> images) {
	this.images = images;
  }
  	
	@ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
	@JoinTable(name = "hy_recommend_specialty",  joinColumns = {
			@JoinColumn(name = "recommend_specialty_id", nullable = false, updatable = false) }, inverseJoinColumns = {
					@JoinColumn(name = "specilty_id", nullable = false, updatable = false) })
	public List<Specialty> getSpecialtiesForSpeciltyId() {
		return this.specialtiesForSpeciltyId;
	}

	public void setSpecialtiesForSpeciltyId(List<Specialty> specialtiesForSpeciltyId) {
		this.specialtiesForSpeciltyId = specialtiesForSpeciltyId;
	}
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "hy_recommend_specialty",  joinColumns = {
			@JoinColumn(name = "specilty_id", nullable = false, updatable = false) }, inverseJoinColumns = {
					@JoinColumn(name = "recommend_specialty_id", nullable = false, updatable = false) })
	public List<Specialty> getSpecialtiesForRecommendSpecialtyId() {
		return this.specialtiesForRecommendSpecialtyId;
	}

	public void setSpecialtiesForRecommendSpecialtyId(List<Specialty> specialtiesForRecommendSpecialtyId) {
		this.specialtiesForRecommendSpecialtyId = specialtiesForRecommendSpecialtyId;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="modify_time", length=19)
	public Date getModifyTime() {
		return this.modifyTime;
	}

	public void setModifyTime(Date Time) {
		this.modifyTime = Time;
	}
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "area_id")
	public HyArea getArea() {
		return area;
	}

	public void setArea(HyArea area) {
		this.area = area;
	}
	
	@Column(name="production_license_number")
	public String getProductionLicenseNumber() {
		return productionLicenseNumber;
	}

	public void setProductionLicenseNumber(String productionLicenseNumber) {
		this.productionLicenseNumber = productionLicenseNumber;
	}

	@PrePersist
	public void setPrepersist() {
		this.createTime = new Date();
		if(this.isActive==null) {
			this.isActive = false;
		}
		if(this.saleState==null) {
			this.saleState = 0;
		}
		if(this.isBanner==null) {
			this.isBanner = false;
		}
		if(numberOfFreeGift == null) {
			this.numberOfFreeGift = 0;
		}
		
		if(this.baseSaleNumber == null) {
			this.baseSaleNumber = 0;
		}


		
		/*this.auditState = 0;*/
	}
	
	@PreUpdate
	public void setPreupdate() {
		this.modifyTime = new Date();
	}
	
//	@Column(name="production_standard")
//	public String getProductionStandard() {
//		return productionStandard;
//	}
//
//	public void setProductionStandard(String productionStandard) {
//		this.productionStandard = productionStandard;
//	}
	
	@ManyToOne
	@JoinColumn(name="creator")
	public HyAdmin getCreator() {
		return creator;
	}

	public void setCreator(HyAdmin creator) {
		this.creator = creator;
	}
	
	@Column(name="creator_name")
	public String getCreatorName() {
		return creatorName;
	}

	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}

	@Column(name="modifier_name")
	public String getModifierName() {
		return modifierName;
	}

	public void setModifierName(String modifierName) {
		this.modifierName = modifierName;
	}

	@Column(name="is_banner")
	public Boolean getIsBanner() {
		return isBanner;
	}

	public void setIsBanner(Boolean isBanner) {
		this.isBanner = isBanner;
	}
	
//	@Column(name="durability_period")
//	public String getDurabilityPeriod() {
//		return durabilityPeriod;
//	}
//
//	public void setDurabilityPeriod(String durabilityPeriod) {
//		this.durabilityPeriod = durabilityPeriod;
//	}
//	
//	@Column(name="gross_weight")
//	public String getGrossWeight() {
//		return grossWeight;
//	}
//
//	public void setGrossWeight(String grossWeight) {
//		this.grossWeight = grossWeight;
//	}
//	
//	@Column(name="specialty_property")
//	public String getSpecialtyProperty() {
//		return specialtyProperty;
//	}
//
//	public void setSpecialtyProperty(String specialtyProperty) {
//		this.specialtyProperty = specialtyProperty;
//	}
	
	private SpecialtyImage icon;

	@Transient
	public SpecialtyImage getIcon() {
		return icon;
	}

	public void setIcon(SpecialtyImage icon) {
		this.icon = icon;
	}
	
	private String privilege;
	
	@Transient
	public String getPrivilege() {
		return privilege;
	}

	public void setPrivilege(String privilege) {
		this.privilege = privilege;
	}

	@Column(name="number_of_free_gift")
	public Integer getNumberOfFreeGift() {
		return numberOfFreeGift;
	}

	public void setNumberOfFreeGift(Integer numberOfFreeGift) {
		this.numberOfFreeGift = numberOfFreeGift;
	}

	@Column(name="base_sale_number")
	public Integer getBaseSaleNumber() {
		return baseSaleNumber;
	}

	public void setBaseSaleNumber(Integer baseSaleNumber) {
		this.baseSaleNumber = baseSaleNumber;
	}
	
	@Transient
	public Long getAppraiseCount() {
		return appraiseCount;
	}

	public void setAppraiseCount(Long appraiseCount) {
		this.appraiseCount = appraiseCount;
	}
	
}
