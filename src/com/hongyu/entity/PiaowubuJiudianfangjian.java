package com.hongyu.entity;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@SuppressWarnings("serial")
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler",
						})
@Table(name = "hy_piaowubu_jdfj")
public class PiaowubuJiudianfangjian implements Serializable {
	public enum FjAuditStatus {
		未提交,
		审核中,
		已通过,
		已驳回,
	}
	public enum FjSaleStatus {
		未上架,
		已下架,
	}
	private Long id;
	private PiaowubuJiudian hotelId;
	private String pn;
	private String productName;
	private Integer roomType;
	private Integer ifWifi;
	private Integer ifWindow;
	private Integer ifBathroom;
	private Integer available;
	private Integer breakfast;
	private FjSaleStatus saleStatus;
	private FjAuditStatus auditStatus;
	private Set<PiaowubuJdfjjgkc> jgkcs = new HashSet<>();
	
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
	@JoinColumn(name = "hotel_id")
	public PiaowubuJiudian getHotelId() {
		return hotelId;
	}
	public void setHotelId(PiaowubuJiudian hotelId) {
		this.hotelId = hotelId;
	}
	public String getPn() {
		return pn;
	}
	public void setPn(String pn) {
		this.pn = pn;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public Integer getRoomType() {
		return roomType;
	}
	public void setRoomType(Integer roomType) {
		this.roomType = roomType;
	}
	public Integer getIfWifi() {
		return ifWifi;
	}
	public void setIfWifi(Integer ifWifi) {
		this.ifWifi = ifWifi;
	}
	public Integer getIfWindow() {
		return ifWindow;
	}
	public void setIfWindow(Integer ifWindow) {
		this.ifWindow = ifWindow;
	}
	public Integer getIfBathroom() {
		return ifBathroom;
	}
	public void setIfBathroom(Integer ifBathroom) {
		this.ifBathroom = ifBathroom;
	}
	public Integer getAvailable() {
		return available;
	}
	public void setAvailable(Integer available) {
		this.available = available;
	}
	public Integer getBreakfast() {
		return breakfast;
	}
	public void setBreakfast(Integer breakfast) {
		this.breakfast = breakfast;
	}
	public FjSaleStatus getSaleStatus() {
		return saleStatus;
	}
	public void setSaleStatus(FjSaleStatus saleStatus) {
		this.saleStatus = saleStatus;
	}
	public FjAuditStatus getAuditStatus() {
		return auditStatus;
	}
	public void setAuditStatus(FjAuditStatus auditStatus) {
		this.auditStatus = auditStatus;
	}
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "piaowubuJdfj", cascade={CascadeType.PERSIST,CascadeType.MERGE}, orphanRemoval=true)
	public Set<PiaowubuJdfjjgkc> getJgkcs() {
		return jgkcs;
	}
	public void setJgkcs(Set<PiaowubuJdfjjgkc> jgkcs) {
		this.jgkcs = jgkcs;
	}
}
