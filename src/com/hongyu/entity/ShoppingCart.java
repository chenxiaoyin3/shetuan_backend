package com.hongyu.entity;

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

@Entity
@Table(name = "shopping_cart")
public class ShoppingCart {
	private Long id;
	//20180422 xyy 增加user_id列  门户的特产使用
	private Long userId; 
	private WechatAccount wechatAccount;
	private Long specialtyId;
	private Long specialtySpecificationId;
	private Integer quantity;
	private Date addTime;
	private Boolean isGroupPromotion;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "user_id")
	public Long getuserId(){
		return this.userId;
	}
	
	public void setUserId(Long userId){
		this.userId = userId;
	}
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "wechat_id")
	public WechatAccount getWechatAccount() {
		return wechatAccount;
	}

	public void setWechatAccount(WechatAccount wechatAccount) {
		this.wechatAccount = wechatAccount;
	}

	@Column(name = "specialty_id")
	public Long getSpecialtyId() {
		return specialtyId;
	}

	public void setSpecialtyId(Long specialtyId) {
		this.specialtyId = specialtyId;
	}

	@Column(name = "specialty_specification_id")
	public Long getSpecialtySpecificationId() {
		return specialtySpecificationId;
	}

	public void setSpecialtySpecificationId(Long specialtySpecificationId) {
		this.specialtySpecificationId = specialtySpecificationId;
	}

	@Column(name = "quantity")
	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	@Column(name = "add_time")
	public Date getAddTime() {
		return addTime;
	}

	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}

	@Column(name = "is_group_promotion")
	public Boolean getIsGroupPromotion() {
		return isGroupPromotion;
	}

	public void setIsGroupPromotion(Boolean isGroupPromotion) {
		this.isGroupPromotion = isGroupPromotion;
	}
	
	@PrePersist
	public void prePersist(){
		this.setAddTime(new Date());
	}

}
