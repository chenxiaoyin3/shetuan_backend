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
@Table(name="hy_specialty_image")
public class SpecialtyImage
  implements Serializable
{
  private Long id;
  private Specialty specialty;
  private Integer orders;
  private String sourcePath;
  private String largePath;
  private String mediumPath;
  private String thumbnailPath;
  
  //是否是标志图片
  private Boolean isLogo;
  
  public SpecialtyImage() {}
  
  public SpecialtyImage(Long id)
  {
    this.id = id;
  }
  
  public SpecialtyImage(Long id, Specialty specialty, Integer orders, String sourcePath, String largePath,String mediumPath, String thumbnailPath)
  {
    this.id = id;
    this.specialty = specialty;
    this.orders = orders;
    this.sourcePath = sourcePath;
    this.largePath = largePath;
    this.mediumPath = mediumPath;
    this.thumbnailPath = thumbnailPath;
  }
  
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  @Column(name="ID", unique=true, nullable=false)
  public Long getId()
  {
    return this.id;
  }
  
  public void setId(Long id)
  {
    this.id = id;
  }
  
  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH, optional = false)
  @JoinColumn(name="specialty_id")
  public Specialty getSpecialty()
  {
    return this.specialty;
  }
  
  public void setSpecialty(Specialty specialty)
  {
    this.specialty = specialty;
  }
  
  @Column(name="orders")
  public Integer getOrders()
  {
    return this.orders;
  }
  
  public void setOrders(Integer orders)
  {
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

  @Column(name="is_logo")
public Boolean getIsLogo() {
	return isLogo;
}

public void setIsLogo(Boolean isLogo) {
	this.isLogo = isLogo;
}
  
  
}
