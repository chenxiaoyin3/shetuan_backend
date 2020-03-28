package com.hongyu.entity;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@SuppressWarnings("serial")
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler",
						})
@Table(name = "hy_mendian_authority")
public class MendianAuthority implements Serializable{
	private Long id;
	private Long authorityId; //权限的ID
	private Integer mendianType; //门店的类型 Constants.StoreType;
	private Integer mendianFanwei; //门店范围 暂时没用，就是departmentId
	
	@Id
	@GeneratedValue(strategy = IDENTITY)

	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getAuthorityId() {
		return authorityId;
	}
	public void setAuthorityId(Long authorityId) {
		this.authorityId = authorityId;
	}
	public Integer getMendianType() {
		return mendianType;
	}
	public void setMendianType(Integer mendianType) {
		this.mendianType = mendianType;
	}
	public Integer getMendianFanwei() {
		return mendianFanwei;
	}
	public void setMendianFanwei(Integer mendianFanwei) {
		this.mendianFanwei = mendianFanwei;
	}
	
	
}
