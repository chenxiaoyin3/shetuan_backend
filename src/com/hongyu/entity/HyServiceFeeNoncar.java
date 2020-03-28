package com.hongyu.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.hongyu.entity.HyLine.LineType;

@Entity
@Table(name="hy_service_fee_noncar")
public class HyServiceFeeNoncar {

	private Integer id;
	private LineType lineType;
	private Boolean groupType;
	private Integer star;
	private BigDecimal price;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id",nullable=false,unique=true)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@Column(name="line_type")
	public LineType getLineType() {
		return lineType;
	}
	public void setLineType(LineType lineType) {
		this.lineType = lineType;
	}
	@Column(name="group_type")
	public Boolean getGroupType() {
		return groupType;
	}
	public void setGroupType(Boolean groupType) {
		this.groupType = groupType;
	}
	@Column(name="star")
	public Integer getStar() {
		return star;
	}
	public void setStar(Integer star) {
		this.star = star;
	}
	@Column(name="price")
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
}
