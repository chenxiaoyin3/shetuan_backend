package com.hongyu.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name="hy_service_promise")
public class ServicePromise {
	private Long id;
	//服务承诺
	private String servicePromise;
	//温馨提示
	private String prompt;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id", unique=true, nullable=false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name="service_promise")
	public String getServicePromise() {
		return servicePromise;
	}
	public void setServicePromise(String servicePromise) {
		this.servicePromise = servicePromise;
	}
	
	@Column(name="prompt")
	public String getPrompt() {
		return prompt;
	}
	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}
	
	
}
