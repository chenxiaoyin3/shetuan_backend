package com.hongyu.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.grain.entity.OrderEntity;
/**
 * 参数设置-交通类型
 * @author guoxinze
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "hy_line_transport")
public class TransportEntity extends OrderEntity {

	private String name;
	
	private Boolean status;

	@JsonProperty
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@JsonProperty
	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}
}
