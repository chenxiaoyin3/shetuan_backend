package com.hongyu.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/** 
 hy_added_service表和hy_added_service_transfer表的中间关联表
 * */
@Entity
@Table(name = "hy_added_service_and_service_transfer")
public class AddedServiceAndServiceTransfer {
	private Long id;
	private Long addedServiceId;
	private Long addedServiceTransferId;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "added_service_id")
	public Long getAddedServiceId() {
		return addedServiceId;
	}

	public void setAddedServiceId(Long addedServiceId) {
		this.addedServiceId = addedServiceId;
	}

	@Column(name = "added_service_transfer_id")
	public Long getAddedServiceTransferId() {
		return addedServiceTransferId;
	}

	public void setAddedServiceTransferId(Long addedServiceTransferId) {
		this.addedServiceTransferId = addedServiceTransferId;
	}
	
	
	
}
