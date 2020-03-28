package com.hongyu.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="hy_visitor_feedback_score")
public class VisitorFeedbackScore {
	private Long id;
	private String item;
	private Integer serviceType;
	private Integer scoreItem;
	@JsonIgnore
	private VisitorFeedback visitorFeedback;
	@Id
	@Column(name="id",unique=true,nullable=false)
	@GeneratedValue(strategy=GenerationType.AUTO )
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Column(name="item")
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	@Column(name="service_type")
	public Integer getServiceType() {
		return serviceType;
	}
	public void setServiceType(Integer serviceType) {
		this.serviceType = serviceType;
	}
	@Column(name="score_item")
	public Integer getScoreItem() {
		return scoreItem;
	}
	public void setScoreItem(Integer scoreItem) {
		this.scoreItem = scoreItem;
	}
	@ManyToOne(fetch=FetchType.LAZY,cascade=CascadeType.REFRESH)
	@JoinColumn(name="visitor_feedback_id")
	public VisitorFeedback getVisitorFeedback() {
		return visitorFeedback;
	}
	public void setVisitorFeedback(VisitorFeedback visitorFeedback) {
		this.visitorFeedback = visitorFeedback;
	}
}
