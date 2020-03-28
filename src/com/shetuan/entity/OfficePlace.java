package com.shetuan.entity;

import java.util.Date;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "sk_office_place")
public class OfficePlace {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id")
	private Long id;
	
//	@ManyToOne(fetch=FetchType.LAZY, cascade = CascadeType.ALL,optional = false)
//	@JoinColumn(name = "organization_id")
//	@JsonIgnore
//	private Organization organization;
	
	@Column(name="organization_id")
	private Long organizationId;
	
	@Column(name="office_place")
	private String officePlace;
	
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
	
	public String getOfficePlace() {
		return officePlace;
	}

	public void setOfficePlace(String officePlace) {
		this.officePlace = officePlace;
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
}
