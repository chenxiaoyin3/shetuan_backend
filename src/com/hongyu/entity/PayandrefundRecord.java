package com.hongyu.entity;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "hy_payandrefund_record")
public class PayandrefundRecord implements java.io.Serializable {

	private Long id;
	//0付款，1退款
	private Integer type;
	
	//付款：1已付款
	//退款：1已退款
	private Integer status;
	
	// 1.转账 2.支付宝 3.微信支付 4.现金 5.预存款 6.刷卡
	private Integer payMethod;
	private Date createtime;
	private BigDecimal money;
	private Long orderId;

	public PayandrefundRecord() {
	}

	public PayandrefundRecord(Long id) {
		this.id = id;
	}
	public PayandrefundRecord(Long id, Integer type, Integer status,
			Integer payMethod, Date createtime, BigDecimal money,
			Long orderId) {
		this.id = id;
		this.type = type;
		this.status = status;
		this.payMethod = payMethod;
		this.createtime = createtime;
		this.money = money;
		this.orderId = orderId;
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

	@Column(name = "pay_method")
	public Integer getPayMethod() {
		return this.payMethod;
	}

	public void setPayMethod(Integer payMethod) {
		this.payMethod = payMethod;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "createtime", length = 19)
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	public Date getCreatetime() {
		return this.createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	@Column(name = "money", precision = 10)
	public BigDecimal getMoney() {
		return this.money;
	}

	public void setMoney(BigDecimal money) {
		this.money = money;
	}

	@Column(name = "order_id")
	public Long getOrderId() {
		return this.orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

}
