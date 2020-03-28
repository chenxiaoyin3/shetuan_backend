package com.hongyu.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "hy_wechat_account")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler",
})
public class WechatAccount implements Serializable {
	private Long id;
	private String wechatName;
	private String wechatOpenid;
	private String phone;
	private Date bindTime;
	private BigDecimal totalbalance;
	private Boolean isActive;
	private Boolean isVip;
	private Date createTime;
	private Boolean isWeBusiness;
	private Integer totalpoint;  //总积分
	private Integer point;  //可用积分
	private Boolean isNew;	//是否是新用户
	//2019.6.5新增 wechatOpenid对应的webusiness id
	private Long customer_uid;

	
	public WechatAccount() {
	}

	public WechatAccount(Long id) {
		this.id = id;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID", unique = true, nullable = false)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "wechat_name")
	public String getWechatName() {
		return this.wechatName;
	}

	public void setWechatName(String wechatName) {
		this.wechatName = wechatName;
	}

	@Column(name = "wechat_openid")
	public String getWechatOpenid() {
		return this.wechatOpenid;
	}

	public void setWechatOpenid(String wechatOpenid) {
		this.wechatOpenid = wechatOpenid;
	}

	@Column(name = "phone")
	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "bind_time", length = 19)
	@DateTimeFormat(iso=ISO.DATE_TIME)
	public Date getBindTime() {
		return this.bindTime;
	}

	public void setBindTime(Date bindTime) {
		this.bindTime = bindTime;
	}

	@Column(name = "totalbalance", precision = 10, scale = 2)
	public BigDecimal getTotalbalance() {
		return this.totalbalance;
	}

	public void setTotalbalance(BigDecimal totalbalance) {
		this.totalbalance = totalbalance;
	}

	@Column(name = "is_active")
	public Boolean getIsActive() {
		return this.isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	@Column(name="is_vip")
	public Boolean getIsVip() {
		return isVip;
	}

	public void setIsVip(Boolean isVip) {
		this.isVip = isVip;
	}
	@Column(name="create_time")
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(iso=ISO.DATE_TIME)
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	@Column(name="is_we_business")
	public Boolean getIsWeBusiness() {
		return isWeBusiness;
	}

	public void setIsWeBusiness(Boolean isWeBusiness) {
		this.isWeBusiness = isWeBusiness;
	}
	
	@Column(name="totalpoint")
	public Integer getTotalpoint() {
		return totalpoint;
	}

	public void setTotalpoint(Integer totalpoint) {
		this.totalpoint = totalpoint;
	}

	@Column(name="point")
	public Integer getPoint() {
		return point;
	}

	public void setPoint(Integer point) {
		this.point = point;
	}

	@PrePersist
	public void prePersist(){
		this.setCreateTime((new Date()));
		this.setIsActive(true);
		this.setIsVip(false);
		this.setTotalbalance(new BigDecimal("0"));
		this.setPoint(0);
		this.setTotalpoint(0);
		this.setIsNew(true);
	}

	@Column(name="is_new")
	public Boolean getIsNew() {
		return isNew;
	}

	public void setIsNew(Boolean isNew) {
		this.isNew = isNew;
	}

	@Transient
	public Long getCustomer_uid() {
		return customer_uid;
	}

	public void setCustomer_uid(Long customer_uid) {
		this.customer_uid = customer_uid;
	}
}
