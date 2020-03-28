package com.hongyu.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@Entity
@Table(name="hy_visitor_feedback")
public class VisitorFeedback implements java.io.Serializable{
	private Long id;
	private Long groupId;
	private Long paiqianId;
	private Integer guideId;
	private String guideName;
	private Date startTime;
	private String line;
	private String reviewer;
	private String advice;
	private Date reviewTime;
	private Integer scoreTotal;
	private String phone;
	private String title;
	private String content;
	private List<VisitorFeedbackScore> visitorFeedbackScores;
	@Id
	@Column(name="id",nullable=false,unique=true)
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Column(name="group_id")
	public Long getGroupId() {
		return groupId;
	}
	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
	@Column(name="paiqian_id")
	public Long getPaiqianId() {
		return paiqianId;
	}
	public void setPaiqianId(Long paiqianId) {
		this.paiqianId = paiqianId;
	}
	@Column(name="guide_id")
	public Integer getGuideId() {
		return guideId;
	}
	public void setGuideId(Integer guideId) {
		this.guideId = guideId;
	}
	@Column(name="guide_name")
	public String getGuideName() {
		return guideName;
	}
	public void setGuideName(String guideName) {
		this.guideName = guideName;
	}
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso=ISO.DATE)
	@Column(name="start_time")
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	@Column(name="line")
	public String getLine() {
		return line;
	}
	public void setLine(String line) {
		this.line = line;
	}
	@Column(name="reviewer")
	public String getReviewer() {
		return reviewer;
	}
	public void setReviewer(String reviewer) {
		this.reviewer = reviewer;
	}
	@Column(name="advice")
	public String getAdvice() {
		return advice;
	}
	public void setAdvice(String advice) {
		this.advice = advice;
	}
	@Column(name="review_time")
	public Date getReviewTime() {
		return reviewTime;
	}
	public void setReviewTime(Date reviewTime) {
		this.reviewTime = reviewTime;
	}
	@Column(name="score_total")
	public Integer getScoreTotal() {
		return scoreTotal;
	}
	public void setScoreTotal(Integer scoreTotal) {
		this.scoreTotal = scoreTotal;
	}
	@Column(name="phone")
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	@Column(name="title")
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	@Column(name="content")
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	@OneToMany(fetch=FetchType.LAZY,mappedBy="visitorFeedback",cascade=CascadeType.ALL,orphanRemoval=true)
	public List<VisitorFeedbackScore> getVisitorFeedbackScores() {
		return visitorFeedbackScores;
	}
	public void setVisitorFeedbackScores(List<VisitorFeedbackScore> visitorFeedbackScores) {
		this.visitorFeedbackScores = visitorFeedbackScores;
	}
	
	

}
