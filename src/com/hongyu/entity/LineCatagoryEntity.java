package com.hongyu.entity;

import javax.persistence.Entity;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.grain.entity.OrderEntity;
import com.hongyu.entity.HyLine.LineType;
/**
 * 线路二级分类Entity
 * @author guoxinze
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "hy_line_catagory")
public class LineCatagoryEntity extends OrderEntity {

	private String name;
	private LineType yijifenlei;
	private Boolean status;
	@JsonProperty
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@JsonProperty
	public LineType getYijifenlei() {
		return yijifenlei;
	}
	public void setYijifenlei(LineType yijifenlei) {
		this.yijifenlei = yijifenlei;
	}
	
	@JsonProperty
	public Boolean getStatus() {
		return status;
	}
	public void setStatus(Boolean status) {
		this.status = status;
	}
	
	@PrePersist
	public void prePersist() {
		this.status = true;
	}
}
