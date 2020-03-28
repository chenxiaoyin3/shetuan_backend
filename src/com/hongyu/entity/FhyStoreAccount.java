package com.hongyu.entity;

import java.math.BigDecimal;

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

import com.fasterxml.jackson.annotation.JsonIgnore;

@SuppressWarnings("serial")
@Entity
@Table(name="hy_store_fhyaccount")
public class FhyStoreAccount implements java.io.Serializable{
	private Long id;
	@JsonIgnore
	private HyStoreFhynew store;
	private BigDecimal balance;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="store")
	public HyStoreFhynew getStore() {
		return store;
	}
	public void setStore(HyStoreFhynew store) {
		this.store = store;
	}
	@Column(name="balance")
	public BigDecimal getBalance() {
		return balance;
	}
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
	@PrePersist
	public void prePersist(){
		this.setBalance(new BigDecimal(0));
	}

}
