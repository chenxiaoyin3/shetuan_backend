package com.hongyu.wrapper.lbc;

import java.util.List;

import com.hongyu.entity.GroupMember;

@SuppressWarnings("serial")
public class GroupMemberListWrapper implements java.io.Serializable{
	
	private List<GroupMember> groupMembers;
	private Long group_id;
	private Integer subGroupNum;
	
	
	public List<GroupMember> getGroupMembers() {
		return groupMembers;
	}
	public void setGroupMembers(List<GroupMember> groupMembers) {
		this.groupMembers = groupMembers;
	}
	public Long getGroup_id() {
		return group_id;
	}
	public void setGroup_id(Long group_id) {
		this.group_id = group_id;
	}
	public Integer getSubGroupNum() {
		return subGroupNum;
	}
	public void setSubGroupNum(Integer subGroupNum) {
		this.subGroupNum = subGroupNum;
	}
	
	
}
