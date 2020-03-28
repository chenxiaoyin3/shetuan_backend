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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@SuppressWarnings("serial")
@Entity
@Table(name = "hy_linelabel")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler","specialtys"})
public class HyLineLabel implements Serializable{

	private Long ID;//ID 也一定是Long
	private String productName;//name
	private String operatorName;//operator
	private Date createTime;//create_time
	private Boolean isActive;//is_active
	private String iconUrl;//icon_url
	private List<Specialty> specialtys;
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID", unique = true, nullable = false)
	public Long getID() {
		return ID;
	}
	public void setID(Long iD) {
		ID = iD;
	}
	
	@Column(name = "name")
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	
	@Column(name = "operator")
	public String getOperatorName() {
		return operatorName;
	}
	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_time", length = 19)
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	@Column(name = "is_active")
	public Boolean getIsActive() {
		return isActive;
	}
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
	
	@Column(name = "icon_url")
	public String getIconUrl() {
		return iconUrl;
	}
	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	
	@ManyToMany(fetch=FetchType.LAZY,cascade=CascadeType.ALL)
	@JoinTable(name="hy_specialty_label",joinColumns= {
			@JoinColumn(name="label_id",nullable=false,updatable=false)},inverseJoinColumns= {
					@JoinColumn(name="specialty_id",nullable=false,updatable=false)})
	public List<Specialty> getSpecialtys() {
		return specialtys;
	}
	public void setSpecialtys(List<Specialty> specialtys) {
		this.specialtys = specialtys;
	}
	


}
