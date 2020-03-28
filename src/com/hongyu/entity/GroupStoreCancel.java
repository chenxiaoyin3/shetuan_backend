package com.hongyu.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.grain.entity.BaseEntity;
/**
 * 门店消团辅助判断的实体
 * @author guoxinze
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "hy_groupstorecancel")
public class GroupStoreCancel extends BaseEntity {
	private Long groupId;
	private Long storeId;
	
	@JsonProperty
	public Long getGroupId() {
		return groupId;
	}
	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
	
	@JsonProperty
	public Long getStoreId() {
		return storeId;
	}
	public void setStoreId(Long storeId) {
		this.storeId = storeId;
	}

}
