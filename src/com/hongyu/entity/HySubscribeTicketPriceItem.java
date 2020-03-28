package com.hongyu.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * 
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "hy_subscribe_ticket_price_item")
public class HySubscribeTicketPriceItem implements java.io.Serializable {

	private Long id;
	private Long hySubscribeTicketPriceId;
	private Date day;
	private Integer availableInventory;
	private Integer initialInventory;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "hy_subscribe_ticket_price_id")
	public Long getHySubscribeTicketPriceId() {
		return hySubscribeTicketPriceId;
	}

	public void setHySubscribeTicketPriceId(Long hySubscribeTicketPriceId) {
		this.hySubscribeTicketPriceId = hySubscribeTicketPriceId;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "day")
	public Date getDay() {
		return day;
	}

	public void setDay(Date day) {
		this.day = day;
	}

	@Column(name = "available_inventory")
	public Integer getAvailableInventory() {
		return availableInventory;
	}

	public void setAvailableInventory(Integer availableInventory) {
		this.availableInventory = availableInventory;
	}

	@Column(name = "initial_inventory")
	public Integer getInitialInventory() {
		return initialInventory;
	}

	public void setInitialInventory(Integer initialInventory) {
		this.initialInventory = initialInventory;
	}

}
