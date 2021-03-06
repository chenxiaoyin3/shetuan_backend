package com.hongyu.entity;
// Generated 2017-12-24 21:20:19 by Hibernate Tools 3.6.0.Final

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * HyTicket generated by hbm2java
 */
@Entity
@Table(name = "hy_ticket")
public class HyTicket implements java.io.Serializable {

	private long id;
	private Long scene;
	private String productId;
	private String type;
	private Boolean reserve;
	private Integer reserveDays;
	private Date reserveTime;
	private Boolean realname;
	private String realnameRequest;
	private String refundRequest;
	private String reserveRequest;
	private String audit;
	private String oncarriage;

	public HyTicket() {
	}

	public HyTicket(long id) {
		this.id = id;
	}

	public HyTicket(long id, Long scene, String productId, String type, Boolean reserve, Integer reserveDays,
			Date reserveTime, Boolean realname, String realnameRequest, String refundRequest, String reserveRequest,
			String audit, String oncarriage) {
		this.id = id;
		this.scene = scene;
		this.productId = productId;
		this.type = type;
		this.reserve = reserve;
		this.reserveDays = reserveDays;
		this.reserveTime = reserveTime;
		this.realname = realname;
		this.realnameRequest = realnameRequest;
		this.refundRequest = refundRequest;
		this.reserveRequest = reserveRequest;
		this.audit = audit;
		this.oncarriage = oncarriage;
	}

	@Id

	@Column(name = "ID", unique = true, nullable = false)
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Column(name = "scene")
	public Long getScene() {
		return this.scene;
	}

	public void setScene(Long scene) {
		this.scene = scene;
	}

	@Column(name = "product_id")
	public String getProductId() {
		return this.productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	@Column(name = "type", length = 11)
	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Column(name = "reserve")
	public Boolean getReserve() {
		return this.reserve;
	}

	public void setReserve(Boolean reserve) {
		this.reserve = reserve;
	}

	@Column(name = "reserve_days")
	public Integer getReserveDays() {
		return this.reserveDays;
	}

	public void setReserveDays(Integer reserveDays) {
		this.reserveDays = reserveDays;
	}

	@Temporal(TemporalType.TIME)
	@Column(name = "reserve_time", length = 8)
	public Date getReserveTime() {
		return this.reserveTime;
	}

	public void setReserveTime(Date reserveTime) {
		this.reserveTime = reserveTime;
	}

	@Column(name = "realname")
	public Boolean getRealname() {
		return this.realname;
	}

	public void setRealname(Boolean realname) {
		this.realname = realname;
	}

	@Column(name = "realname_request")
	public String getRealnameRequest() {
		return this.realnameRequest;
	}

	public void setRealnameRequest(String realnameRequest) {
		this.realnameRequest = realnameRequest;
	}

	@Column(name = "refund_request")
	public String getRefundRequest() {
		return this.refundRequest;
	}

	public void setRefundRequest(String refundRequest) {
		this.refundRequest = refundRequest;
	}

	@Column(name = "reserve_request")
	public String getReserveRequest() {
		return this.reserveRequest;
	}

	public void setReserveRequest(String reserveRequest) {
		this.reserveRequest = reserveRequest;
	}

	@Column(name = "audit", length = 11)
	public String getAudit() {
		return this.audit;
	}

	public void setAudit(String audit) {
		this.audit = audit;
	}

	@Column(name = "oncarriage", length = 11)
	public String getOncarriage() {
		return this.oncarriage;
	}

	public void setOncarriage(String oncarriage) {
		this.oncarriage = oncarriage;
	}

}
