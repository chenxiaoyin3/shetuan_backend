package com.hongyu.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/** 标签 */
@Entity
@Table(name = "hy_tag")
public class Tag implements Serializable {
	private Long id;
	private String tagName;
	private Long tagPid;
	private Integer tagType; // 1国内游 2出境游 3签证 4门票 5酒店 6酒加景 7特产
	private String operator;
	private Date createTime;
	private Date updateTime;
	private Integer tagSort;
	private Date startTime;
	private Date endTime;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID", unique = true, nullable = false)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "tag_name")
	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	@Column(name = "tag_pid")
	public Long getTagPid() {
		return tagPid;
	}

	public void setTagPid(Long tagPid) {
		this.tagPid = tagPid;
	}

	@Column(name = "tag_type")
	public Integer getTagType() {
		return tagType;
	}

	public void setTagType(Integer tagType) {
		this.tagType = tagType;
	}

	@Column(name = "operator")
	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	@Column(name = "create_time")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Column(name = "update_time")
	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	@Column(name = "tag_sort")
	public Integer getTagSort() {
		return tagSort;
	}

	public void setTagSort(Integer tagSort) {
		this.tagSort = tagSort;
	}

	@Column(name = "start_time")
	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	@Column(name = "end_time")
	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

}
