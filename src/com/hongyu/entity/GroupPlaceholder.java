package com.hongyu.entity;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

/**
 * PayDetails 
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "hy_group_placeholder")
public class GroupPlaceholder implements java.io.Serializable {

	private Long id;
	private HyGroup group;
	private HyAdmin creator;
	private Long store_id;
	private Integer number;
	private String signup_phone;
	private Boolean status; //false正常，true清除
	private Boolean store_type; //false虹宇，true非虹宇
	private Date createTime;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "group_id")
	public HyGroup getGroup() {
		return group;
	}
	public void setGroup(HyGroup group) {
		this.group = group;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "creator")
	public HyAdmin getCreator() {
		return creator;
	}
	public void setCreator(HyAdmin creator) {
		this.creator = creator;
	}
	
	@Column(name = "store_id")
	public Long getStore_id() {
		return store_id;
	}
	public void setStore_id(Long store_id) {
		this.store_id = store_id;
	}
	
	@Column(name = "number")
	public Integer getNumber() {
		return number;
	}
	public void setNumber(Integer number) {
		this.number = number;
	}

	@Column(name = "signup_phone")
	public String getSignup_phone() {
		return signup_phone;
	}
	public void setSignup_phone(String signup_phone) {
		this.signup_phone = signup_phone;
	}

	@Column(name = "status")
	public Boolean isStatus() {
		return status;
	}
	public void setStatus(Boolean status) {
		this.status = status;
	}

	@Column(name = "store_type")
	public Boolean isStore_type() {
		return store_type;
	}
	public void setStore_type(Boolean store_type) {
		this.store_type = store_type;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_time", length = 19)
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	

}
