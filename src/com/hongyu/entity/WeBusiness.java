package com.hongyu.entity;

import java.io.Serializable;
import java.math.BigDecimal;
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "hy_we_business")
@JsonIgnoreProperties({ "handler", "hibernateLazyInitializer" })
public class WeBusiness implements Serializable {
	private Long id;
	private String name;
	private Integer type;//0虹宇微商,1非虹宇微商,2个人微商
	private Long storeId;
	private String nameOfStore;
	private String mobile;
	private String address;
	private String url;
	private String qrcodeUrl;
	private Date registerTime;
	private Date deadTime;
	private Boolean isActive;
	@JsonIgnore
	private HyAdmin operator;
	@JsonIgnore
	private HyAdmin account;//对应后台账户
	// private String operator;
	private String wechatAccount;
	private WeBusiness introducer;
	// private String introducer;
	private String wechatOpenId;
	private Boolean isLineWechatBusiness;
	private BigDecimal lineDivideProportion;
	private String logo;
	private String shopName;

	@Column(name = "real_name")
	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	private String realName;

	@Column(name = "origin_url")
	public String getOriginUrl() {
		return originUrl;
	}

	public void setOriginUrl(String originUrl) {
		this.originUrl = originUrl;
	}

	private String originUrl;
	public static final int hytype=0;
	public static final int nonhytype=1;
	public static final int personaltype=2;

	public WeBusiness() {
	}

	public WeBusiness(Long id) {
		this.id = id;
	}


	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "name")
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "type")
	public Integer getType() {
		return this.type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Column(name = "store_id")
	public Long getStoreId() {
		return this.storeId;
	}

	public void setStoreId(Long storeId) {
		this.storeId = storeId;
	}
	@Column(name="name_of_store")
	public String getNameOfStore() {
		return nameOfStore;
	}

	public void setNameOfStore(String nameOfStore) {
		this.nameOfStore = nameOfStore;
	}

	@Column(name = "mobile")
	public String getMobile() {
		return this.mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	@Column(name = "address")
	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Column(name = "url")
	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Column(name = "qrcode_url")
	public String getQrcodeUrl() {
		return this.qrcodeUrl;
	}

	public void setQrcodeUrl(String qrcodeUrl) {
		this.qrcodeUrl = qrcodeUrl;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="register_time", length=19)
	public Date getRegisterTime() {
		return this.registerTime;
	}

	public void setRegisterTime(Date registerTime) {
		this.registerTime = registerTime;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="dead_time", length=19)
	public Date getDeadTime() {
		return this.deadTime;
	}

	public void setDeadTime(Date deadTime) {
		this.deadTime = deadTime;
	}

	@Column(name = "is_active")
	public Boolean getIsActive() {
		return this.isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "operator")
	public HyAdmin getOperator() {
		return operator;
	}

	public void setOperator(HyAdmin operator) {
		this.operator = operator;
	}

	// public String getOperator() {
	// return this.operator;
	// }
	//
	// public void setOperator(String operator) {
	// this.operator = operator;
	// }

	@Column(name = "wechat_account")
	public String getWechatAccount() {
		return this.wechatAccount;
	}

	public void setWechatAccount(String wechatAccount) {
		this.wechatAccount = wechatAccount;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "introducer_id")
	public WeBusiness getIntroducer() {
		return introducer;
	}

	public void setIntroducer(WeBusiness introducer) {
		this.introducer = introducer;
	}

	// @Column(name = "introducer")
	// public String getIntroducer() {
	// return this.introducer;
	// }
	//
	// public void setIntroducer(String introducer) {
	// this.introducer = introducer;
	// }

	@Column(name = "wechat_open_id",unique=true)
	public String getWechatOpenId() {
		return wechatOpenId;
	}

	public void setWechatOpenId(String wechatOpenId) {
		this.wechatOpenId = wechatOpenId;
	}
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="account",unique=true)
	public HyAdmin getAccount() {
		return account;
	}

	public void setAccount(HyAdmin account) {
		this.account = account;
	}
	@Column(name = "is_line_wechat_business")
	public Boolean getIsLineWechatBusiness() {
		return isLineWechatBusiness;
	}

	public void setIsLineWechatBusiness(Boolean isLineWechatBusiness) {
		this.isLineWechatBusiness = isLineWechatBusiness;
	}

	@Column(name = "line_divide_proportion")
	public BigDecimal getLineDivideProportion() {
		return lineDivideProportion;
	}

	public void setLineDivideProportion(BigDecimal lineDivideProportion) {
		this.lineDivideProportion = lineDivideProportion;
	}
	@Column(name="logo")
	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}
	
	@Column(name="shop_name")
	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

	@PrePersist
	public void setPrepersist() {
		this.registerTime =new Date();
		this.setIsActive(false);
	}
}
