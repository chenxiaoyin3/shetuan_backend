package com.hongyu.entity;
// Generated 2017-12-24 21:20:19 by Hibernate Tools 3.6.0.Final

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * HyScenic generated by hbm2java
 */
@Entity
@Table(name = "hy_scenic")
public class HyScenic implements java.io.Serializable {

	private HyScenicId id;

	public HyScenic() {
	}

	public HyScenic(HyScenicId id) {
		this.id = id;
	}

	@EmbeddedId

	@AttributeOverrides({ @AttributeOverride(name = "id", column = @Column(name = "ID", nullable = false)),
			@AttributeOverride(name = "ticketSupplier", column = @Column(name = "ticket_supplier")),
			@AttributeOverride(name = "creater", column = @Column(name = "creater")),
			@AttributeOverride(name = "name", column = @Column(name = "name")),
			@AttributeOverride(name = "area", column = @Column(name = "area")),
			@AttributeOverride(name = "address", column = @Column(name = "address")),
			@AttributeOverride(name = "star", column = @Column(name = "star", length = 10)),
			@AttributeOverride(name = "opentTime", column = @Column(name = "opent_time", length = 8)),
			@AttributeOverride(name = "closeTime", column = @Column(name = "close_time", length = 8)),
			@AttributeOverride(name = "exchangeAddress", column = @Column(name = "exchange_address")) })
	public HyScenicId getId() {
		return this.id;
	}

	public void setId(HyScenicId id) {
		this.id = id;
	}

}