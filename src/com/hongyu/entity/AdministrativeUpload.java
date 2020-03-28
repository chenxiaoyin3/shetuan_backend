package com.hongyu.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "hy_administrative_uploadfile")
public class AdministrativeUpload   implements Serializable
{
	/** 文件ID */
	private Long id;
	/** 文件url */
	private String fileUrl;
	/** 文件有效时间 */
	private Integer effectDate;
	/** 创建者 */
	private String creator;
	/** 文件名 */
	private String fileName;
	/** 创建时间 */
	private Date createTime;
	
	public AdministrativeUpload() {}
	  
	public AdministrativeUpload(Long id)
	{
	  this.id = id;
	}
	  
	public AdministrativeUpload(Long id, String fileUrl, int effectDate, String creator, String fileName, Date createTime)
	{
	  this.id = id;
	  this.fileUrl = fileUrl;
	  this.effectDate = effectDate;
	  this.creator = creator;
	  this.fileName = fileName;
	  this.createTime = createTime;
	  }

	@Id
	@Column(name="ID", unique=true, nullable=false)
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Column(name="file_url")
	public String getFileUrl() {
		return fileUrl;
	}
	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}
	@Column(name="effect_date")
	public Integer getEffectDate() {
		return effectDate;
	}
	public void setEffectDate(int effectDate) {
		this.effectDate = effectDate;
	}
	@Column(name="creator")
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	@Column(name="file_name")
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="create_time", length=19)
	public Date getCreateTime()
	{
	  return this.createTime;
	}
	  
	public void setCreateTime(Date createTime)
	{
	  this.createTime = createTime;
	}
	@PrePersist
	public void setPrepersist() {
		this.createTime = new Date();
	}
}

