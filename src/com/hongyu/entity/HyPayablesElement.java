package com.hongyu.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hongyu.entity.HySupplierElement.SupplierType;
import com.hongyu.util.Constants.DeductLine;
/**
 * 旅游元素供应商付尾款
 * @author guoxinze
 *
 */
@SuppressWarnings("serial")
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler",
	"hyGroup",
	"hyRegulate",
	"operator",
	"hySupplierElement"
	})
@Table(name = "hy_payables_element")
public class HyPayablesElement implements Serializable {
    private Long id;
    private HyRegulate hyRegulate;
    private HyGroup hyGroup;
    private HyAdmin operator;
    private SupplierType type;
    private Boolean isShouru;
    private HySupplierElement hySupplierElement;
	/** 供应商名称*/
    private String supplierName;
	/** 金额*/
    private BigDecimal money;
	/** 地接报账金额*/
    private BigDecimal payBaozhang;
    private DeductLine koudianType;
    private BigDecimal koudianTuanke;
    private BigDecimal koudianRentou;
    /** 扣点*/
    private BigDecimal koudian;
	/** 应付款*/
    private BigDecimal pay;
	/** 已付*/
    private BigDecimal paid;
	/** 欠付*/
    private BigDecimal debt;
	/** 给地接已付*/
    private BigDecimal baozhangPaid;
    /** 合同id*/
    private Long contractId;
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="id", unique=true, nullable=false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "regulate_id")
	public HyRegulate getHyRegulate() {
		return hyRegulate;
	}
	public void setHyRegulate(HyRegulate hyRegulate) {
		this.hyRegulate = hyRegulate;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "group_id")
	public HyGroup getHyGroup() {
		return hyGroup;
	}
	public void setHyGroup(HyGroup hyGroup) {
		this.hyGroup = hyGroup;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "operator")
	public HyAdmin getOperator() {
		return operator;
	}
	public void setOperator(HyAdmin operator) {
		this.operator = operator;
	}
	
	@Column(name="type")
	public SupplierType getType() {
		return type;
	}
	public void setType(SupplierType type) {
		this.type = type;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "supplier_element")
	public HySupplierElement getHySupplierElement() {
		return hySupplierElement;
	}
	public void setHySupplierElement(HySupplierElement hySupplierElement) {
		this.hySupplierElement = hySupplierElement;
	}
	
	@Column(name="money")
	public BigDecimal getMoney() {
		return money;
	}
	public void setMoney(BigDecimal money) {
		this.money = money;
	}
	
	@Column(name="pay_baozhang")
	public BigDecimal getPayBaozhang() {
		return payBaozhang;
	}
	public void setPayBaozhang(BigDecimal payBaozhang) {
		this.payBaozhang = payBaozhang;
	}
	
	@Column(name="koudian_type")
	public DeductLine getKoudianType() {
		return koudianType;
	}
	public void setKoudianType(DeductLine koudianType) {
		this.koudianType = koudianType;
	}
	
	@Column(name="koudian_tuanke")
	public BigDecimal getKoudianTuanke() {
		return koudianTuanke;
	}
	public void setKoudianTuanke(BigDecimal koudianTuanke) {
		this.koudianTuanke = koudianTuanke;
	}
	
	@Column(name="koudian_rentou")
	public BigDecimal getKoudianRentou() {
		return koudianRentou;
	}
	public void setKoudianRentou(BigDecimal koudianRentou) {
		this.koudianRentou = koudianRentou;
	}
	
	@Column(name="koudian")
	public BigDecimal getKoudian() {
		return koudian;
	}
	public void setKoudian(BigDecimal koudian) {
		this.koudian = koudian;
	}
	
	@Column(name="pay")
	public BigDecimal getPay() {
		return pay;
	}
	public void setPay(BigDecimal pay) {
		this.pay = pay;
	}
	
	@Column(name="paid")
	public BigDecimal getPaid() {
		return paid;
	}
	public void setPaid(BigDecimal paid) {
		this.paid = paid;
	}
	
	@Column(name="debt")
	public BigDecimal getDebt() {
		return debt;
	}
	public void setDebt(BigDecimal debt) {
		this.debt = debt;
	}
	public BigDecimal getBaozhangPaid() {
		return baozhangPaid;
	}
	public void setBaozhangPaid(BigDecimal baozhangPaid) {
		this.baozhangPaid = baozhangPaid;
	}
	public Boolean getIsShouru() {
		return isShouru;
	}
	public void setIsShouru(Boolean isShouru) {
		this.isShouru = isShouru;
	}
	public String getSupplierName() {
		return supplierName;
	}
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}
	public Long getContractId() {
		return contractId;
	}
	public void setContractId(Long contractId) {
		this.contractId = contractId;
	}
	
    
}
