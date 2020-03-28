package com.hongyu.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * 收款- 1:电子门票-门店 2:电子门票-微商 3:电子门票-官网 
 * 4:签证-门店    5:签证-微商 6:签证-官网 
 * 7:报名-门店 8:报名-微商 9:报名-官网 
 * 10:酒店-门店 11:酒店-官网 12:酒店-微商
 * 13:门店认购门票 14:门店保险
 * 15:酒加景-门店 16:酒加景-官网 17:酒加景-微商
 * 18:门店租导游  19:门店综合服务
 *
 * @author xyy
 */
@Entity
@Table(name = "hy_receipt_other")
public class ReceiptOther implements java.io.Serializable {

	private Long id;
	private Integer type;
	private String orderCode;
	private String institution;
	private BigDecimal amount;
	private Date date;


	@Id
	@GeneratedValue(strategy = IDENTITY)
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

	@Column(name = "order_code")
	public String getOrderCode() {
		return this.orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	@Column(name = "institution")
	public String getInstitution() {
		return this.institution;
	}

	public void setInstitution(String institution) {
		this.institution = institution;
	}

	@Column(name = "amount")
	public BigDecimal getAmount() {
		return this.amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "date")
	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}
