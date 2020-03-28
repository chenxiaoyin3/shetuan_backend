package com.hongyu.entity;

import static javax.persistence.GenerationType.IDENTITY;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@SuppressWarnings("serial")
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler",
						})
@Table(name = "hy_piaowubu_jdfjjgkc")
public class PiaowubuJdfjjgkc implements java.io.Serializable {
	public enum JgAuditStatus {
		未提交,
		审核中,
		通过,
		驳回,
	}
	private Long id;
	private PiaowubuJiudianfangjian piaowubuJdfj;
	private Date startDate;
	private Date endDate;
	private BigDecimal displayPrice;
	private BigDecimal sellPrice;
	private BigDecimal settlementPrice;
	private Integer inventory;
	private JgAuditStatus auditStatus;
	@Id
	@GeneratedValue(strategy = IDENTITY)

	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "piaowubu_jdfj")
	public PiaowubuJiudianfangjian getPiaowubuJdfj() {
		return piaowubuJdfj;
	}
	public void setPiaowubuJdfj(PiaowubuJiudianfangjian piaowubuJdfj) {
		this.piaowubuJdfj = piaowubuJdfj;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "start_date", length = 19)
	@DateTimeFormat(iso=ISO.DATE_TIME)
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "end_date", length = 19)
	@DateTimeFormat(iso=ISO.DATE_TIME)
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	@Column(name = "display_price", precision = 10)
	public BigDecimal getDisplayPrice() {
		return displayPrice;
	}
	public void setDisplayPrice(BigDecimal displayPrice) {
		this.displayPrice = displayPrice;
	}
	@Column(name = "sell_price", precision = 10)
	public BigDecimal getSellPrice() {
		return sellPrice;
	}
	public void setSellPrice(BigDecimal sellPrice) {
		this.sellPrice = sellPrice;
	}
	@Column(name = "settlement_price", precision = 10)
	public BigDecimal getSettlementPrice() {
		return settlementPrice;
	}
	public void setSettlementPrice(BigDecimal settlementPrice) {
		this.settlementPrice = settlementPrice;
	}
	public Integer getInventory() {
		return inventory;
	}
	public void setInventory(Integer inventory) {
		this.inventory = inventory;
	}
	public JgAuditStatus getAuditStatus() {
		return auditStatus;
	}
	public void setAuditStatus(JgAuditStatus auditStatus) {
		this.auditStatus = auditStatus;
	}
	

}
