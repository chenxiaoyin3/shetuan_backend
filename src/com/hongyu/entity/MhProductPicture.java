package com.hongyu.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**门户完善产品图片表*/
@SuppressWarnings("serial")
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "mh_product_picture")
public class MhProductPicture implements Serializable{
    private Long id; //主键,自增id
    private Integer type; //产品类型,1线路,2酒店,3门票,4酒加景,5签证,6-认购门票,7-保险
    private Long productId; //产品id,根据type字段对应具体产品的id
    private String source; //原图
    private String large; //大图
    private String medium; //中图
    private String thumbnail; //缩略图
    private Boolean isMark; //是否标志图片,1是,0不是
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name="type")
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	
	@Column(name="product_id")
	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
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
	
	@Column(name="is_mark")
	public Boolean getIsMark() {
		return isMark;
	}
	public void setIsMark(Boolean isMark) {
		this.isMark = isMark;
	}
}
