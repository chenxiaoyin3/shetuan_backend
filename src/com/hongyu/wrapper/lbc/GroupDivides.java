package com.hongyu.wrapper.lbc;

import java.util.List;

import com.hongyu.entity.GroupDivide;
import com.hongyu.entity.Guide;

@SuppressWarnings("serial")
public class GroupDivides implements java.io.Serializable{
	
	private List<GroupDivide> groupDivides;

	public List<GroupDivide> getGroupDivides() {
		return groupDivides;
	}

	public void setGroupDivides(List<GroupDivide> groupDivides) {
		this.groupDivides = groupDivides;
	}

}
