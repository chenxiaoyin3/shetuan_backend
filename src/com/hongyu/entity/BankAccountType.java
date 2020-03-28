package com.hongyu.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.grain.entity.OrderEntity;
@Entity
@Table(name = "hy_bankaccount_type")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "hy_bankaccount_type_sequence")
public class BankAccountType extends OrderEntity {
	private static final long serialVersionUID = 1L;
	private String name;
	private Boolean status;
	@JsonProperty
	@Column(name = "status")
	public Boolean getStatus() {
		return status;
	}
	public void setStatus(Boolean status) {
		this.status = status;
	}
	@JsonProperty
	@Column(name = "name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

}