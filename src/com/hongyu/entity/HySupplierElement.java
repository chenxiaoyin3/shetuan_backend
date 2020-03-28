package com.hongyu.entity;

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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author guoxinze
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "hy_supplier_element")
@JsonIgnoreProperties(value={"hibernateLazyInitializer","handler",
		"supplierType",
		"memo",
})
public class HySupplierElement implements java.io.Serializable {
	/**
	 * 枚举旅游元素供应商类型
	 */
	public enum SupplierType {
		hotel, //住宿
		ticket, //门票
		catering, //用餐
		car, //车辆
		traffic, //大交通
		insurance, //保险
		shopping, //购物
		selfpay,  //自费
		otherincome, //其他收入
		otherexpend, //其他支出
		elementlocal, //旅游元素地接 --- 暂时用不到
		linelocal, //线路地接
		coupon,//电子券
		piaowuHotel, //票务部酒店
		piaowuTicket, //票务部门票
		piaowuJiujiajing, //票务部酒加景
		piaowuVisa, //票务出境部签证
		piaowuSubscribe, //票务汽车部门认购门票
	}
	private Long id;
	private SupplierType supplierType;
	private String name;
	private String liableperson;
	private String telephone;
	private String memo;
	private HyAdmin operator;
	private Date createTime;
	private Long supplierLine;
	private BankList bankList;
	/** 0:支出 1:收入*/
	private Boolean isShouru;
	private Boolean status;


	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID", unique = true, nullable = false)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	@Column(name = "type", nullable = false)
	public SupplierType getSupplierType() {
		return supplierType;
	}

	public void setSupplierType(SupplierType supplierType) {
		this.supplierType = supplierType;
	}

	@Column(name = "name", nullable = false, unique = true)
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "liableperson")
	public String getLiableperson() {
		return this.liableperson;
	}

	public void setLiableperson(String liableperson) {
		this.liableperson = liableperson;
	}

	@Column(name = "telephone")
	public String getTelephone() {
		return this.telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	@Column(name = "memo")
	public String getMemo() {
		return this.memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "operator")
	public HyAdmin getOperator() {
		return this.operator;
	}

	public void setOperator(HyAdmin operator) {
		this.operator = operator;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_time", length = 19)
	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Column(name = "supplier_line")
	public Long getSupplierLine() {
		return this.supplierLine;
	}

	public void setSupplierLine(Long supplierLine) {
		this.supplierLine = supplierLine;
	}
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "bank_account")
	public BankList getBankList() {
		return bankList;
	}

	public void setBankList(BankList bankList) {
		this.bankList = bankList;
	}

	@PrePersist
	public void prePersist() {
		this.createTime = new Date();
		this.status = true;
	}
	@Column(name = "status")
	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public Boolean getIsShouru() {
		return isShouru;
	}

	public void setIsShouru(Boolean isShouru) {
		this.isShouru = isShouru;
	}

}
