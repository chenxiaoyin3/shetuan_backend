package com.hongyu.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "hy_receiver")
public class Receiver implements Serializable {
	private Long id;
	private Long wechat_id;
	private String receiverName;
	private String receiverAddress;
	private Boolean isDefaultReceiverAddress;
	private String receiverMobile;
	private Boolean isVipAddress;
	

	public Receiver() {
	}

	public Receiver(Long id) {
		this.id = id;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "wechat_id")
	public Long getWechat_id() {
		return this.wechat_id;
	}

	public void setWechat_id(Long wechat_id) {
		this.wechat_id = wechat_id;
	}

	@Column(name = "receiver_name")
	public String getReceiverName() {
		return this.receiverName;
	}

	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}

	@Column(name = "receiver_address")
	public String getReceiverAddress() {
		return this.receiverAddress;
	}

	public void setReceiverAddress(String receiverAddress) {
		this.receiverAddress = receiverAddress;
	}

	@Column(name = "is_default_receiver_address")
	public Boolean getIsDefaultReceiverAddress() {
		return this.isDefaultReceiverAddress;
	}

	public void setIsDefaultReceiverAddress(Boolean isDefaultReceiverAddress) {
		this.isDefaultReceiverAddress = isDefaultReceiverAddress;
	}

	@Column(name = "receiver_mobile")
	public String getReceiverMobile() {
		return this.receiverMobile;
	}

	public void setReceiverMobile(String receiverMobile) {
		this.receiverMobile = receiverMobile;
	}

	@Column(name="is_vip_address")
	public Boolean getIsVipAddress() {
		return isVipAddress;
	}

	public void setIsVipAddress(Boolean isVipAddress) {
		this.isVipAddress = isVipAddress;
	}

}
