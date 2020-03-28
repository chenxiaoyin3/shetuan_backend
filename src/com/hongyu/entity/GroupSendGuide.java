package com.hongyu.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hongyu.entity.HyLine.LineType;
@SuppressWarnings("serial")
@Entity
@JsonIgnoreProperties(value={"hibernateLazyInitializer","handler",	
		"group",
})
@Table(name = "hy_group_send_guide")
public class GroupSendGuide implements Serializable {
	
	public enum GuideStatusEnum {
		/** 取消 */
		quxiao,
		
		/** 正常 */
		zhengchang,
		
		/** 失效 */
		shixiao,
	}
	
	private Long id;
	private HyGroup group;
	private HyAdmin operator;
	private HyAdmin creator;
	private Integer serviceType;//服务类型,0全陪导游，1其他服务
	private Integer guideNo;
	private Boolean isRestrictSex;
	private Integer manNo;
	private Integer womanNo;
	private Integer lowestLevel;
	private Integer lowestStar;
	private BigDecimal xiaofei;
	private Boolean isRestrictLanguage;
	private Integer days;
	private Date startTime;
	private Date endTime;
	private Date createTime;
	private Date expireTime;
	private Date cancelTime;
	private Integer manReceive;
	private Integer womanReceive;
	private Integer allReceive;
	private String fuwuneirong;
	private String languages; //抢单语种要求,用逗号隔开
	private GuideStatusEnum status;
	
	//新加
	private LineType lineType;
	private String name;
	private Boolean teamType;  //0 : 散客   1 : 团队
	private String serviceFee;//服务费
	
	
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
	@JoinColumn(name = "group_id")
	public HyGroup getGroup() {
		return group;
	}
	public void setGroup(HyGroup group) {
		this.group = group;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "operator")
	public HyAdmin getOperator() {
		return operator;
	}
	public void setOperator(HyAdmin operator) {
		this.operator = operator;
	}
	public Integer getServiceType() {
		return serviceType;
	}
	public void setServiceType(Integer serviceType) {
		this.serviceType = serviceType;
	}
	public Integer getGuideNo() {
		return guideNo;
	}
	public void setGuideNo(Integer guideNo) {
		this.guideNo = guideNo;
	}
	public Boolean getIsRestrictSex() {
		return isRestrictSex;
	}
	public void setIsRestrictSex(Boolean isRestrictSex) {
		this.isRestrictSex = isRestrictSex;
	}
	public Integer getManNo() {
		return manNo;
	}
	public void setManNo(Integer manNo) {
		this.manNo = manNo;
	}
	public Integer getWomanNo() {
		return womanNo;
	}
	public void setWomanNo(Integer womanNo) {
		this.womanNo = womanNo;
	}
	public Integer getLowestLevel() {
		return lowestLevel;
	}
	public void setLowestLevel(Integer lowestLevel) {
		this.lowestLevel = lowestLevel;
	}
	public Integer getLowestStar() {
		return lowestStar;
	}
	public void setLowestStar(Integer lowestStar) {
		this.lowestStar = lowestStar;
	}
	
	@Column(name = "xiaofei", precision = 20, scale = 2)
	public BigDecimal getXiaofei() {
		return xiaofei;
	}
	public void setXiaofei(BigDecimal xiaofei) {
		this.xiaofei = xiaofei;
	}
	public Boolean getIsRestrictLanguage() {
		return isRestrictLanguage;
	}
	public void setIsRestrictLanguage(Boolean isRestrictLanguage) {
		this.isRestrictLanguage = isRestrictLanguage;
	}
	public Integer getDays() {
		return days;
	}
	public void setDays(Integer days) {
		this.days = days;
	}
	
	@Temporal(TemporalType.DATE)
	@Column(name = "start_time", length = 19)
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	
	@Temporal(TemporalType.DATE)
	@Column(name = "end_time", length = 19)
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_time", length = 19)
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	@DateTimeFormat(iso=ISO.DATE_TIME)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "expire_time", length = 19)
	public Date getExpireTime() {
		return expireTime;
	}
	public void setExpireTime(Date expireTime) {
		this.expireTime = expireTime;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "cancel_time", length = 19)
	public Date getCancelTime() {
		return cancelTime;
	}
	public void setCancelTime(Date cancelTime) {
		this.cancelTime = cancelTime;
	}
	public Integer getManReceive() {
		return manReceive;
	}
	public void setManReceive(Integer manReceive) {
		this.manReceive = manReceive;
	}
	public Integer getWomanReceive() {
		return womanReceive;
	}
	public void setWomanReceive(Integer womanReceive) {
		this.womanReceive = womanReceive;
	}
	public Integer getAllReceive() {
		return allReceive;
	}
	public void setAllReceive(Integer allReceive) {
		this.allReceive = allReceive;
	}
	public GuideStatusEnum getStatus() {
		return status;
	}
	public void setStatus(GuideStatusEnum status) {
		this.status = status;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "creator")
	public HyAdmin getCreator() {
		return creator;
	}
	public void setCreator(HyAdmin creator) {
		this.creator = creator;
	}
	
	public String getLanguages() {
		return languages;
	}

	public void setLanguages(String languages) {
		this.languages = languages;
	}
	public String getFuwuneirong() {
		return fuwuneirong;
	}
	
	public void setFuwuneirong(String fuwuneirong) {
		this.fuwuneirong = fuwuneirong;
	}
	public LineType getLineType() {
		return lineType;
	}
	public void setLineType(LineType lineType) {
		this.lineType = lineType;
	}
	public Boolean getTeamType() {
		return teamType;
	}
	public void setTeamType(Boolean teamType) {
		this.teamType = teamType;
	}
	public String getServiceFee() {
		return serviceFee;
	}
	public void setServiceFee(String serviceFee) {
		this.serviceFee = serviceFee;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@PrePersist
	public void prePersist() {
		this.createTime = new Date();
		this.setAllReceive(0);
		this.setManReceive(0);
		this.setWomanReceive(0);
	}
	
}
