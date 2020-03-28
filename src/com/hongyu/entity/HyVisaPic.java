package com.hongyu.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "hy_visa_pic")
public class HyVisaPic implements Serializable {
    private Long id;
    private HyVisa hyVisa;
    private String source; //原图
    private String large; //大图
    private String medium; //中图
    private String thumbnail; //缩略图
    
    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID", unique = true, nullable = false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "visa")
	public HyVisa getHyVisa() {
		return hyVisa;
	}
	public void setHyVisa(HyVisa hyVisa) {
		this.hyVisa = hyVisa;
	}
	
	@Column(name="source")
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	
	@Column(name="large")
	public String getLarge() {
		return large;
	}
	public void setLarge(String large) {
		this.large = large;
	}
	
	@Column(name="medium")
	public String getMedium() {
		return medium;
	}
	public void setMedium(String medium) {
		this.medium = medium;
	}
	
	@Column(name="thumbnail")
	public String getThumbnail() {
		return thumbnail;
	}
	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}
}
