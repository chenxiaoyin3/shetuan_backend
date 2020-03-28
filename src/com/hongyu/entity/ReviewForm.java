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
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="hy_review_form")
public class ReviewForm implements java.io.Serializable{

	private Long id;
	private Date createDate;
	private Date modifyDate;
	private Integer reviewFormType;//0：游客反馈单_散客   1：游客反馈单_团队   2：导游评价单
	private String title;
	private String content;
	private List<ReviewFormItem> reviewFormItems;
	@Id
	@Column(name="id",nullable=false,unique=true)
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="create_date")
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	@Column(name="modify_date")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getModifyDate() {
		return modifyDate;
	}
	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}
	@Column(name="review_form_type")
	public Integer getReviewFormType() {
		return reviewFormType;
	}
	public void setReviewFormType(Integer reviewFormType) {
		this.reviewFormType = reviewFormType;
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
	@OneToMany(fetch=FetchType.LAZY,mappedBy="reviewForm",cascade=CascadeType.ALL,orphanRemoval=true)
	public List<ReviewFormItem> getReviewFormItems() {
		return reviewFormItems;
	}
	public void setReviewFormItems(List<ReviewFormItem> reviewFormItems) {
		this.reviewFormItems = reviewFormItems;
	}
	
	@PrePersist
	public void prePersist(){
		this.setCreateDate(new Date());
	}
	@PreUpdate
	public void preUpdate(){
		this.setModifyDate(new Date());
	}
}
