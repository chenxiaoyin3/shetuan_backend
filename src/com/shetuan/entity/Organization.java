package com.shetuan.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.shetuan.entity.Activity;


@Entity
@Table(name="sk_organization")
@JsonFilter("Organization-Json-Filter")
public class Organization implements java.io.Serializable {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	private Long id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "name_history")
	private String nameHistory;
	
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
	
	@Column(name = "place")
	private String place;
	
	@Column(name = "creator")
	private String creator;
	
	@Column(name = "member")
	private String member;
	
	@Column(name = "leader")
	private String leader;
	
	@Column(name = "secretariat")
	private String secretariat;
	
	@Column(name = "logo_url")
	private String logoUrl;
	
	@Column(name= "click_number")
	private Integer clickNumber;
	
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
	
//	@OneToMany(fetch=FetchType.LAZY,mappedBy="organization",cascade=CascadeType.ALL,orphanRemoval = true)
//	private List<OfficePlace> officePlace;

	@Transient
	private List<HashMap<String, Object>> images;
	
	@Transient
	private List<HashMap<String, Object>> videos;
	
	@Transient
	private List<Audio> audios;
	
	@Transient
	private List<PeopleRelation> relatedPeople;
	
//	@OneToMany(fetch=FetchType.LAZY,mappedBy="organization",cascade=CascadeType.ALL,orphanRemoval = true)
//	private List<Activity> activity;
//	
//	@OneToMany(fetch=FetchType.LAZY,mappedBy="organization",cascade=CascadeType.ALL,orphanRemoval = true)
//	private List<Journal> journal;
//	
//	@OneToMany(fetch=FetchType.LAZY,mappedBy="organization",cascade=CascadeType.ALL,orphanRemoval = true)
//	private List<Literature> literature;
//	
//	@OneToMany(fetch=FetchType.LAZY,mappedBy="organization",cascade=CascadeType.ALL,orphanRemoval = true)
//	private List<RealObject> realObject;
//
//	@OneToMany(fetch=FetchType.LAZY,mappedBy="organization",cascade=CascadeType.ALL,orphanRemoval = true)
//	private List<Constitution> constitution;
//	
//	@OneToMany(fetch=FetchType.LAZY,mappedBy="organization",cascade=CascadeType.ALL,orphanRemoval = true)
//	private List<HistoricalDataIndex> historicalDataIndex;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getNameHistory() {
		return nameHistory;
	}

	public void setNameHistory(String nameHistory) {
		this.nameHistory = nameHistory;
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

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getMember() {
		return member;
	}

	public void setMember(String member) {
		this.member = member;
	}

	public String getLeader() {
		return leader;
	}

	public void setLeader(String leader) {
		this.leader = leader;
	}

	public String getSecretariat() {
		return secretariat;
	}

	public void setSecretariat(String secretariat) {
		this.secretariat = secretariat;
	}

	public String getLogoUrl() {
		return logoUrl;
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	public Integer getClickNumber() {
		return clickNumber;
	}

	public void setClickNumber(Integer clickNumber) {
		this.clickNumber = clickNumber;
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

//	public List<OfficePlace> getOfficePlace() {
//		return officePlace;
//	}
//
//	public void setOfficePlace(List<OfficePlace> officePlace) {
//		this.officePlace = officePlace;
//	}

//	public List<Activity> getActivity() {
//		return activity;
//	}
//
//	public void setActivity(List<Activity> activity) {
//		this.activity = activity;
//	}
//
//	public List<Journal> getJournal() {
//		return journal;
//	}
//
//	public void setJournal(List<Journal> journal) {
//		this.journal = journal;
//	}
//
//	public List<Literature> getLiterature() {
//		return literature;
//	}
//
//	public void setLiterature(List<Literature> literature) {
//		this.literature = literature;
//	}
//
//	public List<RealObject> getRealObject() {
//		return realObject;
//	}
//
//	public void setRealObject(List<RealObject> realObject) {
//		this.realObject = realObject;
//	}
//
//	public List<Constitution> getConstitution() {
//		return constitution;
//	}
//
//	public void setConstitution(List<Constitution> constitution) {
//		this.constitution = constitution;
//	}
//
//	public List<HistoricalDataIndex> getHistoricalDataIndex() {
//		return historicalDataIndex;
//	}
//
//	public void setHistoricalDataIndex(List<HistoricalDataIndex> historicalDataIndex) {
//		this.historicalDataIndex = historicalDataIndex;
//	}


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
