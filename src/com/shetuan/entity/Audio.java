package com.shetuan.entity;

import java.util.Date;
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

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "sk_audio")
public class Audio {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	private Long id;

	@Column(name = "entity_id")
	private Long entityId;

	@Column(name = "type")
	private Integer type;

	@Column(name = "name")
	private String name;

	@Column(name = "url")
	private String url;

	@Column(name = "description")
	private String description;

	@Temporal(TemporalType.DATE)
	@Column(name = "create_time")
	private Date createTime;

	@Column(name = "create_time_type")
	private Integer createTimeType;

	@Column(name = "state", nullable = false)
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
	private List<PeopleRelation> relatedPeople;
	
	@Transient
	private List<Integer> relatedPeopleList;//别删 专用

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getEntityId() {
		return entityId;
	}

	public void setEntityId(Long entityId) {
		this.entityId = entityId;
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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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

	public List<PeopleRelation> getRelatedPeople() {
		return relatedPeople;
	}

	public void setRelatedPeople(List<PeopleRelation> relatedPeople) {
		this.relatedPeople = relatedPeople;
	}

	public List<Integer> getRelatedPeopleList() {
		return relatedPeopleList;
	}

	public void setRelatedPeopleList(List<Integer> relatedPeopleList) {
		this.relatedPeopleList = relatedPeopleList;
	}
	
}
