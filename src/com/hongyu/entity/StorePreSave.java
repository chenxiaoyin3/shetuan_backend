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

/**
 * StorePreSave 
 * 总公司 - 财务中心 - 门店预存款
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "hy_store_pre_save")
public class StorePreSave implements java.io.Serializable {

	private Long id;
	private Long storeId;
	private String storeName;
	/**
	 * 1:门店充值 2:报名退款  3:报名冲抵
	 * 4:门店认购门票冲抵 5:电子门票退款 6:保险购买冲抵
	 * 7:保险弃撤 8:签证购买冲抵 9:签证退款
	 * 10:酒店销售 11:酒店退款 12:门店后返
	 * 13:供应商驳回订单 14:门店租导游 15:酒加景销售
	 * 16:酒加景退款 17:门店综合服务18：租借导游退款
	 * 19:保险退款 20:认购门票退款 21：门店门票退款
	 * 22:门店提现
	 * */
	private Integer type;
	private Date date;
	private BigDecimal amount;
	private Long orderId;
	private String orderCode;
	private BigDecimal preSaveBalance;
	private String remark;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "store_id")
	public Long getStoreId() {
		return storeId;
	}

	public void setStoreId(Long storeId) {
		this.storeId = storeId;
	}

	@Column(name = "store_name")
	public String getStoreName() {
		return this.storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	
    @Column(name="type")
    public Integer getType() {
        return this.type;
    }
    
    public void setType(Integer type) {
        this.type = type;
    }

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "date", length = 19)
	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Column(name = "amount")
	public BigDecimal getAmount() {
		return this.amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	@Column(name = "order_id")
	public Long getOrderId() {
		return this.orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	@Column(name = "order_code")
	public String getOrderCode() {
		return this.orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	@Column(name = "pre_save_balance")
	public BigDecimal getPreSaveBalance() {
		return this.preSaveBalance;
	}

	public void setPreSaveBalance(BigDecimal preSaveBalance) {
		this.preSaveBalance = preSaveBalance;
	}

	@Column(name = "remark")
	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}
