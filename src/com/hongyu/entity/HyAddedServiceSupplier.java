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
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 增值业务供应商
 * */
@SuppressWarnings("serial")
@Entity
@Table(name="hy_added_service_supplier")
@JsonIgnoreProperties(value={"hibernateLazyInitializer","handler"})
public class HyAddedServiceSupplier implements Serializable{

	private Long id;
	private Store store;
	private String name;
	private String contact;
	private String phone;
	private BankList bankList;
	private HyAdmin operator;
	private Date createtime;
	private Date modifytime;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID", unique = true, nullable = false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="store_id")
	public Store getStore() {
		return store;
	}
	public void setStore(Store store) {
		this.store = store;
	}
	@Column(name="name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Column(name="contact")
	public String getContact() {
		return contact;
	}
	public void setContact(String contact) {
		this.contact = contact;
	}
	@Column(name="phone")
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	@ManyToOne(fetch=FetchType.LAZY,cascade = CascadeType.ALL,optional = false)
	@JoinColumn(name="bank_id")
	public BankList getBankList() {
		return bankList;
	}
	public void setBankList(BankList bankList) {
		this.bankList = bankList;
	}
	@ManyToOne(fetch=FetchType.LAZY)
	public HyAdmin getOperator() {
		return operator;
	}
	public void setOperator(HyAdmin operator) {
		this.operator = operator;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="createtime")
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="modifytime")
	public Date getModifytime() {
		return modifytime;
	}
	public void setModifytime(Date modifytime) {
		this.modifytime = modifytime;
	}
	@PrePersist
	public void prePersist(){
		this.setCreatetime(new Date());
	}
	@PreUpdate
	public void preUpdate(){
		this.setModifytime(new Date());
	}
}
