package com.hongyu.entity;

import java.io.Serializable;

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
@Table(name="mh_appraise_image")
public class MhAppraiseImage implements Serializable{
	private Long id;
	private MhAppraise appraise;
	private Integer orders;
	private String sourcePath;
	private String largePath;
	private String mediumPath;
	private String thumbnailPath;
	
	public MhAppraiseImage() {
	}

	public MhAppraiseImage(Long id) {
		this.id = id;
	}

	public MhAppraiseImage(Long id, MhAppraise appraise, Integer orders, String sourcePath,
			String largePath, String mediumPath, String thumbnailPath) {
		super();
		this.id = id;
		this.appraise = appraise;
		this.orders = orders;
		this.sourcePath = sourcePath;
		this.largePath = largePath;
		this.mediumPath = mediumPath;
		this.thumbnailPath = thumbnailPath;
	}
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id", unique=true, nullable=false)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH, optional = false)
	@JoinColumn(name="appraise_id")
	public MhAppraise getAppraise() {
		return appraise;
	}

	public void setAppraise(MhAppraise appraise) {
		this.appraise = appraise;
	}
	
	@Column(name="orders")
	public Integer getOrders() {
		return orders;
	}

	public void setOrders(Integer orders) {
		this.orders = orders;
	}
	
	@Column(name="source_path")
	public String getSourcePath() {
		return sourcePath;
	}

	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}
	
	@Column(name="large_path")
	public String getLargePath() {
		return largePath;
	}

	public void setLargePath(String largePath) {
		this.largePath = largePath;
	}
	
	@Column(name="medium_path")
	public String getMediumPath() {
		return mediumPath;
	}

	public void setMediumPath(String mediumPath) {
		this.mediumPath = mediumPath;
	}
	
	@Column(name="thumbnail_path")
	public String getThumbnailPath() {
		return thumbnailPath;
	}

	public void setThumbnailPath(String thumbnailPath) {
		this.thumbnailPath = thumbnailPath;
	}	
}
