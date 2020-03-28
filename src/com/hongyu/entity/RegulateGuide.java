package com.hongyu.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Digits;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/**
 * 导游报账表
 * @author guoxinze
 *
 */
@SuppressWarnings("serial")
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler",
	"groupId",
	"regulateId",
	"guideId"
	})
@Table(name = "hy_regulate_guide")
public class RegulateGuide implements Serializable {
	private Long id;
	private HyGroup groupId;
	private HyRegulate regulateId;
	private Guide guideId;
	private Long gId;
	@Digits(integer=10, fraction=2)
	private BigDecimal shouru;
	@Digits(integer=10, fraction=2)
	private BigDecimal zhichu;
	@Digits(integer=10, fraction=2)
	private BigDecimal baozhang;
	private String guideName;
	private Integer daituanrenshu;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id", unique=true, nullable=false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "group_id")
	public HyGroup getGroupId() {
		return groupId;
	}
	public void setGroupId(HyGroup groupId) {
		this.groupId = groupId;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "regulate_id")
	public HyRegulate getRegulateId() {
		return regulateId;
	}
	public void setRegulateId(HyRegulate regulateId) {
		this.regulateId = regulateId;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "guide_id")
	public Guide getGuideId() {
		return guideId;
	}
	public void setGuideId(Guide guideId) {
		this.guideId = guideId;
	}
	public BigDecimal getShouru() {
		return shouru;
	}
	public void setShouru(BigDecimal shouru) {
		this.shouru = shouru;
	}
	public BigDecimal getZhichu() {
		return zhichu;
	}
	public void setZhichu(BigDecimal zhichu) {
		this.zhichu = zhichu;
	}
	public String getGuideName() {
		return guideName;
	}
	public void setGuideName(String guideName) {
		this.guideName = guideName;
	}
	public Integer getDaituanrenshu() {
		return daituanrenshu;
	}
	public void setDaituanrenshu(Integer daituanrenshu) {
		this.daituanrenshu = daituanrenshu;
	}
	public BigDecimal getBaozhang() {
		return baozhang;
	}
	public void setBaozhang(BigDecimal baozhang) {
		this.baozhang = baozhang;
	}
	public Long getgId() {
		return gId;
	}
	public void setgId(Long gId) {
		this.gId = gId;
	}
	
	
}
