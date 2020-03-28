package com.hongyu.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "hy_valid_product")
public class HyValidProduct {

	private Long id;
	private Date recordtime;
	private Integer quantity;

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "recordtime")
	@Temporal(TemporalType.DATE)
	public Date getRecordtime() {
		return recordtime;
	}

	public void setRecordtime(Date recordtime) {
		this.recordtime = recordtime;
	}

	@Column(name="quantity")
	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
}
