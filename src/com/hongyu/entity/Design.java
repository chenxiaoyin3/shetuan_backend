package com.hongyu.entity;
// Generated 2017-12-12 17:05:00 by Hibernate Tools 3.6.0.Final

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * HyDesign generated by hbm2java
 */
@Entity
@Table(name = "hy_design")
public class Design implements java.io.Serializable {

	private Long id;
	private Integer type;
	private Integer status;//0待估价，1待支付，2待确认，3设计中，4已完成，5已取消
	private String topic;
	private String content;
	private BigDecimal price;
	private String mail;
	private String phone;
	private Store store;
	private HyAdmin proposer;
	private HyAdmin operator;
	private Date createDate;
	private Date modifyDate;

	public Design() {
	}

	public Design(long id) {
		this.id = id;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "type")
	public Integer getType() {
		return this.type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Column(name = "status")
	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Column(name = "topic")
	public String getTopic() {
		return this.topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	@Column(name = "content")
	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Column(name = "price", precision = 10)
	public BigDecimal getPrice() {
		return this.price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	@Column(name = "mail")
	public String getMail() {
		return this.mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	@Column(name = "phone")
	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "store")
	public Store getStore() {
		return this.store;
	}

	public void setStore(Store store) {
		this.store = store;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "proposer")
	public HyAdmin getProposer() {
		return this.proposer;
	}

	public void setProposer(HyAdmin proposer) {
		this.proposer = proposer;
	}
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "operator")
	public HyAdmin getOperator() {
		return this.operator;
	}

	public void setOperator(HyAdmin operator) {
		this.operator = operator;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_date", length = 19)
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	public Date getCreateDate() {
		return this.createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "modify_date", length = 19)
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	public Date getModifyDate() {
		return this.modifyDate;
	}

	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}

	@PrePersist
	public void prePersist(){
		this.setCreateDate(new Date());
		this.setStatus(0);
	}
	@PreUpdate
	public void preUpdate(){
		this.setModifyDate(new Date());
	}
}
