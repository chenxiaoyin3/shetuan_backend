package com.hongyu.entity;
// Generated 2017-12-24 21:20:19 by Hibernate Tools 3.6.0.Final

import static javax.persistence.GenerationType.IDENTITY;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Digits;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * HyGroupSpecialPriceSwd generated by hbm2java
 */
@SuppressWarnings("serial")
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler",
	})
@Table(name = "hy_group_specialprice_swd")
public class HyGroupSpecialPriceSwd implements java.io.Serializable {

	private Long id;//id
	private HyGroup hygroup;//group_id
	private HyGroupShenheSwd shenheSwd;
	private HyGroupSpecialprice hyGroupSpecialprice;//group_special_price
	private Integer times;//times 
	private String intro;//intro
	private Integer visitorNum;//visitor_num
	@Digits(integer=10, fraction=2)
	private BigDecimal specialPrice;//special_price
	@Digits(integer=10, fraction=2)
	private BigDecimal specialPrice1;//special_price1
	@Digits(integer=10, fraction=2)
	private BigDecimal specialPrice4;//special_price4

	public HyGroupSpecialPriceSwd() {
	}

	public HyGroupSpecialPriceSwd(Long id) {
		this.id = id;
	}

	public HyGroupSpecialPriceSwd(Long id, HyGroup hygroup, HyGroupSpecialprice hyGroupSpecialprice, Integer times,
			String intro, Integer visitorNum, BigDecimal specialPrice, BigDecimal specialPrice1, BigDecimal specialPrice4) {
		super();
		this.id = id;
		this.hygroup = hygroup;
		this.hyGroupSpecialprice = hyGroupSpecialprice;
		this.times = times;
		this.intro = intro;
		this.visitorNum = visitorNum;
		this.specialPrice = specialPrice;
		this.specialPrice1 = specialPrice1;
		this.specialPrice4 = specialPrice4;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "group_id")
	public HyGroup getHygroup() {
		return hygroup;
	}

	public void setHygroup(HyGroup hygroup) {
		this.hygroup = hygroup;
	}

	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "group_special_price")
	public HyGroupSpecialprice getHyGroupSpecialprice() {
		return hyGroupSpecialprice;
	}

	public void setHyGroupSpecialprice(HyGroupSpecialprice hyGroupSpecialprice) {
		this.hyGroupSpecialprice = hyGroupSpecialprice;
	}

	
	@Column(name = "times")
	public Integer getTimes() {
		return times;
	}

	public void setTimes(Integer times) {
		this.times = times;
	}

	@Column(name = "intro")
	public String getIntro() {
		return this.intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}
	
	@Column(name = "visitor_num")
	public Integer getVisitorNum() {
		return this.visitorNum;
	}

	public void setVisitorNum(Integer visitorNum) {
		this.visitorNum = visitorNum;
	}

	@Column(name = "special_price", precision = 10, scale = 0)
	public BigDecimal getSpecialPrice() {
		return this.specialPrice;
	}

	public void setSpecialPrice(BigDecimal specialPrice) {
		this.specialPrice = specialPrice;
	}

	@Column(name = "special_price1", precision = 10, scale = 0)
	public BigDecimal getSpecialPrice1() {
		return this.specialPrice1;
	}

	public void setSpecialPrice1(BigDecimal specialPrice1) {
		this.specialPrice1 = specialPrice1;
	}

	@Column(name = "special_price4", precision = 10, scale = 0)
	public BigDecimal getSpecialPrice4() {
		return this.specialPrice4;
	}

	public void setSpecialPrice4(BigDecimal specialPrice4) {
		this.specialPrice4 = specialPrice4;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "shenhe_swd")
	public HyGroupShenheSwd getShenheSwd() {
		return shenheSwd;
	}

	public void setShenheSwd(HyGroupShenheSwd shenheSwd) {
		this.shenheSwd = shenheSwd;
	}

}
