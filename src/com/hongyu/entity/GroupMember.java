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
@Table(name = "hy_group_member")
public class GroupMember implements java.io.Serializable{
	
	private Long id;
	private HyGroup hyGroup;
	private HyOrderItem hyOrderItem;
	//private GroupDivide groupDivide;
	private String subGroupsn;
	private HyOrderCustomer hyOrderCustomer;
	private HyOrder hyOrder;
	
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
	public HyGroup getHyGroup() {
		return hyGroup;
	}
	public void setHyGroup(HyGroup hyGroup) {
		this.hyGroup = hyGroup;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_item_id")
	public HyOrderItem getHyOrderItem() {
		return hyOrderItem;
	}
	public void setHyOrderItem(HyOrderItem hyOrderItem) {
		this.hyOrderItem = hyOrderItem;
	}
	
	@Column(name = "sub_group_sn")
	public String getSubGroupsn() {
		return subGroupsn;
	}
	public void setSubGroupsn(String subGroupsn) {
		this.subGroupsn = subGroupsn;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_customer")
	public HyOrderCustomer getHyOrderCustomer() {
		return hyOrderCustomer;
	}
	public void setHyOrderCustomer(HyOrderCustomer hyOrderCustomer) {
		this.hyOrderCustomer = hyOrderCustomer;
	}
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "order_id")
	public HyOrder getHyOrder() {
		return hyOrder;
	}
	public void setHyOrder(HyOrder hyOrder) {
		this.hyOrder = hyOrder;
	}
	
	
}
