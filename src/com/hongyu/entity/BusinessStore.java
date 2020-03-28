package com.hongyu.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "hy_business_store")
@JsonIgnoreProperties({ "handler","hibernateLazyInitializer" })
public class BusinessStore implements Serializable {
	private Long id;
	private String storeName;
	private String address;
	private Integer state;//0无效,1有效
	private WeBusiness headWebusiness;//负责人微商
	private HyAdmin hyAdmin;//负责人
	private WeBusiness introducer;//推荐人
	private Date createTime;
	private Date deadTime;
	private HyAdmin creator;

	public BusinessStore() {
	}

	public BusinessStore(Long id) {
		this.id = id;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "store_name")
	public String getStoreName() {
		return this.storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	@Column(name = "address")
	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_time", length = 19)
	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "dead_time", length = 19)
	public Date getDeadTime() {
		return this.deadTime;
	}

	public void setDeadTime(Date deadTime) {
		this.deadTime = deadTime;
	}

	@Column(name = "state")
	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}
	@ManyToOne(fetch=FetchType.LAZY,cascade={CascadeType.PERSIST,CascadeType.MERGE})
	@JoinColumn(name="head_webusiness_id")
	public WeBusiness getHeadWebusiness() {
		return headWebusiness;
	}

	public void setHeadWebusiness(WeBusiness headWebusiness) {
		this.headWebusiness = headWebusiness;
	}
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="head")
	public HyAdmin getHyAdmin() {
		return hyAdmin;
	}

	public void setHyAdmin(HyAdmin hyAdmin) {
		this.hyAdmin = hyAdmin;
	}
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="introducer_webusiness_id")
	public WeBusiness getIntroducer() {
		return introducer;
	}

	public void setIntroducer(WeBusiness introducer) {
		this.introducer = introducer;
	}
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="creator")
	public HyAdmin getCreator() {
		return creator;
	}

	public void setCreator(HyAdmin creator) {
		this.creator = creator;
	}

	@PrePersist
	public void setBeforePersist() {
		this.createTime = new Date();
	}

}
