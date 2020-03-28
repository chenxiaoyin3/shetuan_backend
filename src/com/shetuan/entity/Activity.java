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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="sk_activity")
@JsonFilter("Activity-Json-Filter")
public class Activity {
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
	
	@Column(name = "type")
	private Integer type;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "place")
	private String place;
	
	@Column(name = "content")
	private String content;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "start_time")
	private Date startTime;
	
	@Column(name = "start_time_type")
	private Integer startTimeType;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "end_time")
	private Date endTime;
	
	@Column(name = "end_time_type")
	private Integer endTimeType;
	
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

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Integer getStartTimeType() {
		return startTimeType;
	}

	public void setStartTimeType(Integer startTimeType) {
		this.startTimeType = startTimeType;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Integer getEndTimeType() {
		return endTimeType;
	}

	public void setEndTimeType(Integer endTimeType) {
		this.endTimeType = endTimeType;
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