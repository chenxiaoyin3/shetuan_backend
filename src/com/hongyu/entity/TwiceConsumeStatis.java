package com.hongyu.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/** 二次消费-统计 */
@Entity
@Table(name = "hy_twice_consume_statis")
public class TwiceConsumeStatis implements Serializable {
	private Long id;
	private String consumer;
	private String phone;
	private Integer consumeCount;
	private Float totalAmount;

	private Long wechatId;// 通过wechatId使二次消费记录表和二次消费统计表能够关联

	public TwiceConsumeStatis() {
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

	@Column(name = "consumer")
	public String getConsumer() {
		return this.consumer;
	}

	public void setConsumer(String consumer) {
		this.consumer = consumer;
	}

	@Column(name = "phone")
	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Column(name = "consume_count")
	public Integer getConsumeCount() {
		return this.consumeCount;
	}

	public void setConsumeCount(Integer consumeCount) {
		this.consumeCount = consumeCount;
	}

	@Column(name = "total_amount")
	public Float getTotalAmount() {
		return this.totalAmount;
	}

	public void setTotalAmount(Float totalAmount) {
		this.totalAmount = totalAmount;
	}

	@Column(name = "wechat_id")
	public Long getWechatId() {
		return this.wechatId;
	}

	public void setWechatId(Long wechatId) {
		this.wechatId = wechatId;
	}
}
