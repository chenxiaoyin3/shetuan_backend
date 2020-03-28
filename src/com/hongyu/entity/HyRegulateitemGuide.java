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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@SuppressWarnings("serial")
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler",
	"hyRegulate",
	"guideAssignment",
	"guide",
	"hyGroup"
	})
@Table(name = "hy_regulateitem_guide")
public class HyRegulateitemGuide implements Serializable {
    private Long id;
    private HyRegulate hyRegulate;
    private HyGroup hyGroup;
    private GuideAssignment guideAssignment;
    private Guide guide;
    private Long guideId;
    private Long assignmentId; //导游派遣的id
    private Integer type; //0-全陪,1-导游
    private String guideName; 
    private Integer days;
    private Integer num; //带团人数
    private BigDecimal fee; //服务费
    private BigDecimal xiaofei; //小费
    private BigDecimal kouchu; //扣除金额
    private BigDecimal yingfu; //应付款
    private BigDecimal money; //金额
    
    private String guideSn; //导游编号
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="regulate_id")
	public HyRegulate getHyRegulate() {
		return hyRegulate;
	}
	public void setHyRegulate(HyRegulate hyRegulate) {
		this.hyRegulate = hyRegulate;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="group_id")
	public HyGroup getHyGroup() {
		return hyGroup;
	}
	public void setHyGroup(HyGroup hyGroup) {
		this.hyGroup = hyGroup;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="guide_record_id")
	public GuideAssignment getGuideAssignment() {
		return guideAssignment;
	}
	public void setGuideAssignment(GuideAssignment guideAssignment) {
		this.guideAssignment = guideAssignment;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="guide_id")
	public Guide getGuide() {
		return guide;
	}
	public void setGuide(Guide guide) {
		this.guide = guide;
	}
	
	@Column(name="type")
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	
	@Column(name="guide_name")
	public String getGuideName() {
		return guideName;
	}
	public void setGuideName(String guideName) {
		this.guideName = guideName;
	}
	
	@Column(name="days")
	public Integer getDays() {
		return days;
	}
	public void setDays(Integer days) {
		this.days = days;
	}
	
	@Column(name="num")
	public Integer getNum() {
		return num;
	}
	public void setNum(Integer num) {
		this.num = num;
	}
	
	@Column(name="fee")
	public BigDecimal getFee() {
		return fee;
	}
	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}
	
	@Column(name="xiaofei")
	public BigDecimal getXiaofei() {
		return xiaofei;
	}
	public void setXiaofei(BigDecimal xiaofei) {
		this.xiaofei = xiaofei;
	}
	
	@Column(name="kouchu")
	public BigDecimal getKouchu() {
		return kouchu;
	}
	public void setKouchu(BigDecimal kouchu) {
		this.kouchu = kouchu;
	}
	
	@Column(name="yingfu")
	public BigDecimal getYingfu() {
		return yingfu;
	}
	public void setYingfu(BigDecimal yingfu) {
		this.yingfu = yingfu;
	}
	
	@Column(name="money")
	public BigDecimal getMoney() {
		return money;
	}
	public void setMoney(BigDecimal money) {
		this.money = money;
	}
	@Column(name="guide")
	public Long getGuideId() {
		return guideId;
	}
	public void setGuideId(Long guideId) {
		this.guideId = guideId;
	}
	public Long getAssignmentId() {
		return assignmentId;
	}
	public void setAssignmentId(Long assignmentId) {
		this.assignmentId = assignmentId;
	}
	public String getGuideSn() {
		return guideSn;
	}
	public void setGuideSn(String guideSn) {
		this.guideSn = guideSn;
	}
    
}
