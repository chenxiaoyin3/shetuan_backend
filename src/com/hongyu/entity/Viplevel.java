package com.hongyu.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@SuppressWarnings("serial")
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler",
						})
@Table(name = "hy_viplevel")
public class Viplevel implements Serializable{
    private Long id;
    private String levelname;
    private Integer startvalue; //开始储蓄值
    private Integer endvalue;  //结束储蓄值
    private BigDecimal discount;	//会员折扣
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name="levelname")
	public String getLevelname() {
		return levelname;
	}
	public void setLevelname(String levelname) {
		this.levelname = levelname;
	}
	
	@Column(name="startvalue")
	public Integer getStartvalue() {
		return startvalue;
	}
	public void setStartvalue(Integer startvalue) {
		this.startvalue = startvalue;
	}
	
	@Column(name="endvalue")
	public Integer getEndvalue() {
		return endvalue;
	}
	public void setEndvalue(Integer endvalue) {
		this.endvalue = endvalue;
	}
	
	@Column(name="discount")
	public BigDecimal getDiscount() {
		return discount;
	}
	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}
}
