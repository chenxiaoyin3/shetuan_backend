package com.hongyu.entity;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "hy_group_divide")
public class GroupDivide implements java.io.Serializable{
	
	private Long id;
	private HyGroup group;
	private String subGroupsn;
	private Integer subGroupNo;
	private Guide guide;
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "group_id")
	public HyGroup getGroup() {
		return group;
	}
	public void setGroup(HyGroup group) {
		this.group = group;
	}
	
	@Column(name = "sub_group_sn")
	public String getSubGroupsn() {
		return subGroupsn;
	}
	public void setSubGroupsn(String subGroupsn) {
		this.subGroupsn = subGroupsn;
	}
	
	@Column(name = "sub_group_no")
	public Integer getSubGroupNo() {
		return subGroupNo;
	}
	public void setSubGroupNo(Integer subGroupNo) {
		this.subGroupNo = subGroupNo;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "guide_id")
	public Guide getGuide() {
		return guide;
	}
	public void setGuide(Guide guide) {
		this.guide = guide;
	}
	
	
}
