package com.hongyu.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("serial")
public class Area implements Serializable{
	private Long id;
	private String fullName;
	private String name;
	private Boolean status;
	private String treePath;
	private Integer order;
	private Long pid;
	private Boolean isleaf;
	private Set<HyArea> children = new HashSet<HyArea>();
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
	public Boolean getStatus() {
		return status;
	}
	public void setStatus(Boolean status) {
		this.status = status;
	}
	public String getTreePath() {
		return treePath;
	}
	public void setTreePath(String treePath) {
		this.treePath = treePath;
	}

	public Integer getOrder() {
		return order;
	}
	public void setOrder(Integer order) {
		this.order = order;
	}
	public Set<HyArea> getChildren() {
		return children;
	}
	public void setChildren(Set<HyArea> children) {
		this.children = children;
	}
	public Long getPid() {
		return pid;
	}
	public void setPid(Long pid) {
		this.pid = pid;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public Boolean getIsleaf() {
		return isleaf;
	}
	public void setIsleaf(Boolean isleaf) {
		this.isleaf = isleaf;
	}
	
}
