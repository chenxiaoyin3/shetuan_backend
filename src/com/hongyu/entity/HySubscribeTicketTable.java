package com.hongyu.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * HySubscribeTicketTable 
 * 
 */
@Entity
@Table(name = "hy_subscribe_ticket_table")
public class HySubscribeTicketTable implements java.io.Serializable {

	private long id;
	private String content;

	public HySubscribeTicketTable() {
	}

	public HySubscribeTicketTable(long id) {
		this.id = id;
	}

	public HySubscribeTicketTable(long id, String content) {
		this.id = id;
		this.content = content;
	}

	@Id

	@Column(name = "ID", unique = true, nullable = false)
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Column(name = "content")
	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
