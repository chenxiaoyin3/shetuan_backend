package com.hongyu.entity;
// Generated 2017-12-12 17:05:00 by Hibernate Tools 3.6.0.Final

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
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * HyGuideReviewForm generated by hbm2java
 */
@Entity
@Table(name = "hy_guide_review_form")
public class GuideReviewForm implements java.io.Serializable {

	private Long id;
	private Integer reviewType;
	private Integer guideType;
	private Long groupId;
	private String orderSn;
	private Long orderId;
	private Long paiqianId;
	private Long guideId;
	private String guideSn;
	private String guideName;
	private String lineSn;
	private Date startDate;
	private String line;
	private String reviewer;
	private String advice;
	private Date reviewTime;
	private Integer scoreTotal;
	private String phone;
	private String title;
	private String content;
	private List<GuideReviewFormScore> guideReviewFormScores;
	public GuideReviewForm() {
	}

	public GuideReviewForm(Long id) {
		this.id = id;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "review_type")
	public Integer getReviewType() {
		return this.reviewType;
	}

	public void setReviewType(Integer reviewType) {
		this.reviewType = reviewType;
	}

	@Column(name = "guide_type")
	public Integer getGuideType() {
		return this.guideType;
	}

	public void setGuideType(Integer guideType) {
		this.guideType = guideType;
	}

	@Column(name = "group_id")
	public Long getGroupId() {
		return this.groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	@Column(name = "order_sn")
	public String getOrderSn() {
		return this.orderSn;
	}

	public void setOrderSn(String orderSn) {
		this.orderSn = orderSn;
	}

	@Column(name = "guide_sn")
	public String getGuideSn() {
		return this.guideSn;
	}

	public void setGuideSn(String guideSn) {
		this.guideSn = guideSn;
	}

	@Column(name = "guide_name")
	public String getGuideName() {
		return this.guideName;
	}

	public void setGuideName(String guideName) {
		this.guideName = guideName;
	}

	@Column(name = "line_sn")
	public String getLineSn() {
		return this.lineSn;
	}

	public void setLineSn(String lineSn) {
		this.lineSn = lineSn;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "start_date", length = 10)
	public Date getStartDate() {
		return this.startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@Column(name = "line")
	public String getLine() {
		return this.line;
	}

	public void setLine(String line) {
		this.line = line;
	}

	@Column(name = "reviewer")
	public String getReviewer() {
		return this.reviewer;
	}

	public void setReviewer(String reviewer) {
		this.reviewer = reviewer;
	}

	@Column(name = "advice")
	public String getAdvice() {
		return this.advice;
	}

	public void setAdvice(String advice) {
		this.advice = advice;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "review_time", length = 19)
	public Date getReviewTime() {
		return this.reviewTime;
	}

	public void setReviewTime(Date reviewTime) {
		this.reviewTime = reviewTime;
	}

	@Column(name = "score_total")
	public Integer getScoreTotal() {
		return this.scoreTotal;
	}

	public void setScoreTotal(Integer scoreTotal) {
		this.scoreTotal = scoreTotal;
	}

	@Column(name = "phone")
	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	@Column(name="order_id")
	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	@Column(name="paiqian_id")
	public Long getPaiqianId() {
		return paiqianId;
	}

	public void setPaiqianId(Long paiqianId) {
		this.paiqianId = paiqianId;
	}
	@Column(name="guide_id")
	public Long getGuideId() {
		return guideId;
	}

	public void setGuideId(Long guideId) {
		this.guideId = guideId;
	}
	@Column(name = "title")
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	@Column(name = "content")
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@OneToMany(fetch=FetchType.LAZY,mappedBy="guideReviewForm",cascade=CascadeType.ALL,orphanRemoval=true)
	public List<GuideReviewFormScore> getGuideReviewFormScores() {
		return guideReviewFormScores;
	}

	public void setGuideReviewFormScores(List<GuideReviewFormScore> guideReviewFormScores) {
		this.guideReviewFormScores = guideReviewFormScores;
	}

	@PrePersist
	public void prePersist(){
		this.setReviewTime(new Date());
	}
	
}
