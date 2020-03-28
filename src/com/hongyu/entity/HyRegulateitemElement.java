package com.hongyu.entity;

import java.io.Serializable;
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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hongyu.entity.HySupplierElement.SupplierType;
import com.hongyu.util.Constants.DeductLine;

@SuppressWarnings("serial")
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler",
	"hyRegulate",
	"guide",
	"hyGroup",
	"hySupplierElement",
	"contractId"
	})
@Table(name = "hy_regulateitem_element")
public class HyRegulateitemElement implements Serializable {
    private Long id;
    private SupplierType type;
    private Boolean isShouru; //true-收入,false-支出
    private HyGroup hyGroup;
    private HyRegulate hyRegulate;
    private HySupplierElement hySupplierElement;
    private Long supplierElement;
    private String supplierName;
    private Date startDate; //开始日期 如果只有一个时间就是这个时间
    private Date endDate; //结束日期
    private BigDecimal price; //单价
    private Integer num; //数量
    private BigDecimal otherMoney; //收入类金额
    private BigDecimal money; //总金额 = 单价乘以数量
    private Boolean isDianfu; //true-导游垫付,false-非垫付
    private Guide guide; //垫付导游
    private Long guideId;
    private BigDecimal dianfu; //垫付金额
    private BigDecimal realMoney; //应付款 应收款
    private HySupplierContract contractId; //合同号
    private Long supplierContractId;
    private String contractCode;
    private DeductLine deductType; //扣点方式 
    private BigDecimal deduct; //扣点
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="id", unique=true, nullable=false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name="type")
	public SupplierType getType() {
		return type;
	}
	public void setType(SupplierType type) {
		this.type = type;
	}
	
	@Column(name="is_shouru")
	public Boolean getIsShouru() {
		return isShouru;
	}
	public void setIsShouru(Boolean isShouru) {
		this.isShouru = isShouru;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "group_id")
	public HyGroup getHyGroup() {
		return hyGroup;
	}
	public void setHyGroup(HyGroup hyGroup) {
		this.hyGroup = hyGroup;
	}
	

	@Column(name="supplier_name")
	public String getSupplierName() {
		return supplierName;
	}
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}
	
	@Temporal(TemporalType.DATE)
	@Column(name="start_date", length=19)
	@DateTimeFormat(pattern="yyyy-MM-dd")
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	@Temporal(TemporalType.DATE)
	@Column(name="end_date", length=19)
	@DateTimeFormat(pattern="yyyy-MM-dd")
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	@Column(name="price")
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	
	@Column(name="num")
	public Integer getNum() {
		return num;
	}
	public void setNum(Integer num) {
		this.num = num;
	}
	
	@Column(name="other_money")
	public BigDecimal getOtherMoney() {
		return otherMoney;
	}
	public void setOtherMoney(BigDecimal otherMoney) {
		this.otherMoney = otherMoney;
	}
	
	@Column(name="money")
	public BigDecimal getMoney() {
		return money;
	}
	public void setMoney(BigDecimal money) {
		this.money = money;
	}
	
	@Column(name="is_dianfu")
	public Boolean getIsDianfu() {
		return isDianfu;
	}
	public void setIsDianfu(Boolean isDianfu) {
		this.isDianfu = isDianfu;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "guide_id")
	public Guide getGuide() {
		return guide;
	}
	public void setGuide(Guide guide) {
		this.guide = guide;
	}
	
	@Column(name="dianfu")
	public BigDecimal getDianfu() {
		return dianfu;
	}
	public void setDianfu(BigDecimal dianfu) {
		this.dianfu = dianfu;
	}
	
	@Column(name="real_money")
	public BigDecimal getRealMoney() {
		return realMoney;
	}
	public void setRealMoney(BigDecimal realMoney) {
		this.realMoney = realMoney;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="contract_id")
	public HySupplierContract getContractId() {
		return contractId;
	}
	public void setContractId(HySupplierContract contractId) {
		this.contractId = contractId;
	}
	
	@Column(name="deduct_type")
	public DeductLine getDeductType() {
		return deductType;
	}
	public void setDeductType(DeductLine deductType) {
		this.deductType = deductType;
	}
	
	@Column(name="deduct")
	public BigDecimal getDeduct() {
		return deduct;
	}
	public void setDeduct(BigDecimal deduct) {
		this.deduct = deduct;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "regulate")
	public HyRegulate getHyRegulate() {
		return hyRegulate;
	}
	public void setHyRegulate(HyRegulate hyRegulate) {
		this.hyRegulate = hyRegulate;
	}
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "supplier_element_id")	
	public HySupplierElement getHySupplierElement() {
		return hySupplierElement;
	}
	public void setHySupplierElement(HySupplierElement hySupplierElement) {
		this.hySupplierElement = hySupplierElement;
	}
	@Column(name="guide")
	public Long getGuideId() {
		return guideId;
	}
	public void setGuideId(Long guideId) {
		this.guideId = guideId;
	}
	public Long getSupplierElement() {
		return supplierElement;
	}
	public void setSupplierElement(Long supplierElement) {
		this.supplierElement = supplierElement;
	}
	public Long getSupplierContractId() {
		return supplierContractId;
	}
	public void setSupplierContractId(Long supplierContractId) {
		this.supplierContractId = supplierContractId;
	}
	public String getContractCode() {
		return contractCode;
	}
	public void setContractCode(String contractCode) {
		this.contractCode = contractCode;
	}
	
}
