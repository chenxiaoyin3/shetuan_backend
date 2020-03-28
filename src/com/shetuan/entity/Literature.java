package com.shetuan.entity;

import java.util.Date;
import java.util.HashMap;
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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="sk_literature")
@JsonFilter("Literature-Json-Filter")
public class Literature {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	private Long id;
	
//	@ManyToOne(cascade = { CascadeType.ALL},fetch=FetchType.LAZY,optional=true )
//	@JoinColumn(name="organization_id")
//	@JsonIgnore
//	private Organization organization;
	
	@Column(name="organization_id")
	private Long organizationId;
	
	@Transient
	private String organizationName;
	
	@Transient
	private String image;
	
	@Column(name = "type")
	private Integer type;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "description")
	private String description;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "create_time")
	private Date createTime;
	
	@Column(name = "create_time_type")
	private Integer createTimeType;
	
	@Column(name = "source")
	private String Source;
	
	@Column(name = "state",nullable = false)
	private Boolean state;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ctime", length = 19)
	private Date ctime;
	
	@Column(name = "cname")
	private String cname;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "mtime", length = 19)
	private Date mtime;
	
	@Column(name = "mname")
	private String mname;
	
	@Column(name = "remark")
	private String remark;

	@Transient
	private List<HashMap<String, Object>> images;
	
	@Transient
	private List<HashMap<String, Object>> videos;
	
	@Transient
	private List<Audio> audios;
	
	@Transient
	private List<PeopleRelation> relatedPeople;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

//	public Organization getOrganization() {
//		return organization;
//	}
//
//	public void setOrganization(Organization organization) {
//		this.organization = organization;
//	}

	public Long getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(Long organizationId) {
		this.organizationId = organizationId;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getCreateTimeType() {
		return createTimeType;
	}

	public void setCreateTimeType(Integer createTimeType) {
		this.createTimeType = createTimeType;
	}

	public String getSource() {
		return Source;
	}

	public void setSource(String source) {
		Source = source;
	}

	public Boolean getState() {
		return state;
	}

	public void setState(Boolean state) {
		this.state = state;
	}

	public Date getCtime() {
		return ctime;
	}

	public void setCtime(Date ctime) {
		this.ctime = ctime;
	}

	public String getCname() {
		return cname;
	}

	public void setCname(String cname) {
		this.cname = cname;
	}

	public Date getMtime() {
		return mtime;
	}

	public void setMtime(Date mtime) {
		this.mtime = mtime;
	}

	public String getMname() {
		return mname;
	}

	public void setMname(String mname) {
		this.mname = mname;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public List<HashMap<String, Object>> getImages() {
		return images;
	}

	public void setImages(List<HashMap<String, Object>> images) {
		this.images = images;
	}

	public List<HashMap<String, Object>> getVideos() {
		return videos;
	}

	public void setVideos(List<HashMap<String, Object>> videos) {
		this.videos = videos;
	}

	public List<Audio> getAudios() {
		return audios;
	}

	public void setAudios(List<Audio> audios) {
		this.audios = audios;
	}

	public List<PeopleRelation> getRelatedPeople() {
		return relatedPeople;
	}

	public void setRelatedPeople(List<PeopleRelation> relatedPeople) {
		this.relatedPeople = relatedPeople;
	}

}

