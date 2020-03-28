package com.hongyu.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@SuppressWarnings("serial")
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler",
	"hyGroup",
	"operator"
	})
@Table(name = "hy_regulate")
public class HyRegulate implements Serializable {
    private Long id;
    private Long hyGroup;
    private String lineSn; //产品ID
    private String lineName;
    private Date startDate;
    private Integer days;
    private Date endDate;
    private Integer visitorNum;
    private HyAdmin operator;
    private String operatorName;
    private Date createTime;
    private Integer status; //0-计调中,1-审核中,2-通过,3-驳回
    private List<HyRegulateitemGuide> hyRegulateitemGuides = new ArrayList<>();
    private List<HyRegulateitemElement> hyRegulateitemElements = new ArrayList<>();
    
    private String applyName;
    private String processInstanceId;
    private Date applyTime;
    private Long dantuanhesuanbiaoId;
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="id", unique=true, nullable=false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name = "group_id")
	public Long getHyGroup() {
		return hyGroup;
	}
	public void setHyGroup(Long hyGroup) {
		this.hyGroup = hyGroup;
	}
	
	@Column(name="line_sn")
	public String getLineSn() {
		return lineSn;
	}
	public void setLineSn(String lineSn) {
		this.lineSn = lineSn;
	}
	
	@Column(name="line_name")
	public String getLineName() {
		return lineName;
	}
	public void setLineName(String lineName) {
		this.lineName = lineName;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="start_date", length=19)
	@DateTimeFormat(pattern="yyyy-MM-dd")
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	@Column(name="days")
	public Integer getDays() {
		return days;
	}
	public void setDays(Integer days) {
		this.days = days;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="end_date", length=19)
	@DateTimeFormat(pattern="yyyy-MM-dd")
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	@Column(name="visitor_num")
	public Integer getVisitorNum() {
		return visitorNum;
	}
	public void setVisitorNum(Integer visitorNum) {
		this.visitorNum = visitorNum;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "operator")
	public HyAdmin getOperator() {
		return operator;
	}
	public void setOperator(HyAdmin operator) {
		this.operator = operator;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="create_time", length=19)
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	@Column(name="status")
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	
	@OrderBy("id asc")
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "hyRegulate", cascade = CascadeType.ALL, orphanRemoval = true)
	public List<HyRegulateitemGuide> getHyRegulateitemGuides() {
		return hyRegulateitemGuides;
	}
	public void setHyRegulateitemGuides(List<HyRegulateitemGuide> hyRegulateitemGuides) {
		this.hyRegulateitemGuides = hyRegulateitemGuides;
	}
	
	@OrderBy("id asc")
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "hyRegulate", cascade = CascadeType.ALL, orphanRemoval = true)
	public List<HyRegulateitemElement> getHyRegulateitemElements() {
		return hyRegulateitemElements;
	}
	public void setHyRegulateitemElements(List<HyRegulateitemElement> hyRegulateitemElements) {
		this.hyRegulateitemElements = hyRegulateitemElements;
	}
	public String getOperatorName() {
		return operatorName;
	}
	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}
	public String getApplyName() {
		return applyName;
	}
	public void setApplyName(String applyName) {
		this.applyName = applyName;
	}
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="apply_time", length=19)
	public Date getApplyTime() {
		return applyTime;
	}
	public void setApplyTime(Date applyTime) {
		this.applyTime = applyTime;
	}
	public Long getDantuanhesuanbiaoId() {
		return dantuanhesuanbiaoId;
	}
	public void setDantuanhesuanbiaoId(Long dantuanhesuanbiaoId) {
		this.dantuanhesuanbiaoId = dantuanhesuanbiaoId;
	}
    
}
