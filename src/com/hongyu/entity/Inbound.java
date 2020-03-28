package com.hongyu.entity;

import java.io.Serializable;
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

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/****
 * 
 * 商贸特产库存表
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name="hy_inbound")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler",
})
public class Inbound
implements Serializable {
	private Long id;
	private PurchaseItem purchaseItem;
	private SpecialtySpecification specification;
	private Date productDate;
	private Date expiration;
	private String depotCode;
	private Integer durabilityPeriod;
	private Integer inboundNumber;
	private Date updateTime;
	
	private Integer saleNumber;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID", unique=true, nullable=false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="purchase_item_id")
	public PurchaseItem getPurchaseItem() {
		return purchaseItem;
	}
	public void setPurchaseItem(PurchaseItem purchaseitem) {
		this.purchaseItem = purchaseitem;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="specialty_specification_id")
	public SpecialtySpecification getSpecification() {
		return specification;
	}
	public void setSpecification(SpecialtySpecification specification) {
		this.specification = specification;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="product_date", length=19)
	@DateTimeFormat(pattern="yyyy-MM-dd")
	public Date getProductDate() {
		return productDate;
	}
	public void setProductDate(Date productDate) {
		this.productDate = productDate;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="expiration", length=19)
	@DateTimeFormat(pattern="yyyy-MM-dd")
	public Date getExpiration() {
		return expiration;
	}
	public void setExpiration(Date expiration) {
		this.expiration = expiration;
	}
	@Column(name = "depot_code")
	public String getDepotCode() {
		return depotCode;
	}
	public void setDepotCode(String depotCode) {
		this.depotCode = depotCode;
	}
	
	@Column(name = "durability_period")
	public Integer getDurabilityPeriod() {
		return durabilityPeriod;
	}
	public void setDurabilityPeriod(Integer durabilityPeriod) {
		this.durabilityPeriod = durabilityPeriod;
	}
	
	@Column(name = "inbound_number")
	public Integer getInboundNumber() {
		return inboundNumber;
	}
	public void setInboundNumber(Integer inboundNumber) {
		this.inboundNumber = inboundNumber;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="update_time", length=19)
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	
	@Column(name="sale_number")
	public Integer getSaleNumber() {
		return saleNumber;
	}
	public void setSaleNumber(Integer saleNumber) {
		this.saleNumber = saleNumber;
	}
	
	//add by gxz 
	@PrePersist
	public void prePersist() {
		this.saleNumber = 0;
	}
	
}
