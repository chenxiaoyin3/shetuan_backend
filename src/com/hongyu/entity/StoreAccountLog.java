package com.hongyu.entity;

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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="hy_store_account_log")
public class StoreAccountLog {

	private Long id;//主键id
	private Store store;//所属门店
	private Integer type;//类型,0充值，1订单抵扣，2分成，3退团，4消团， 5供应商驳回订单,6海报设计,7租借导游退款
								//8酒店退款  9 门票退款  10酒加景退款 11签证退款 12认购门票退款13保险退款 14门店提现

	private Integer status;//状态  0审核中，1通过  2驳回 4未成功支付 5已支付
	private BigDecimal money;//金额
	private String orderSn;//订单编号
	private String chargeOrderSn; //自动生成的订单编号,用于光大银行充值
	private String profile;//收支概要
	private Date createDate;//创建时间
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id",unique=true,nullable=false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="store_id")
	public Store getStore() {
		return store;
	}
	public void setStore(Store store) {
		this.store = store;
	}
	@Column(name="type")
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	@Column(name="status")
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	@Column(name="money")
	public BigDecimal getMoney() {
		return money;
	}
	public void setMoney(BigDecimal money) {
		this.money = money;
	}
	@Column(name="order_sn")
	public String getOrderSn() {
		return orderSn;
	}
	public void setOrderSn(String orderSn) {
		this.orderSn = orderSn;
	}
	@Column(name="profile")
	public String getProfile() {
		return profile;
	}
	public void setProfile(String profile) {
		this.profile = profile;
	}
	
	@Column(name="charge_order_sn")
	public String getChargeOrderSn() {
		return chargeOrderSn;
	}
	public void setChargeOrderSn(String chargeOrderSn) {
		this.chargeOrderSn = chargeOrderSn;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="create_date")
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	@PrePersist
	public void prePersist(){
		this.setCreateDate(new Date());
	}
}

