package com.hongyu.entity;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;
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
import javax.persistence.PrePersist;
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
@Table(name = "hy_piaowubu_jiudian")
public class PiaowubuJiudian implements java.io.Serializable {
	private Long id;
	private HyArea area;
	private HyAdmin creator;
	private PiaowubuGongyingshang ticketSupplier;
	private String star;
	private String hotelName;
	private String address;
	private String reserveKnow;
	private Date createTime;
	private Set<PiaowubuJiudianfangjian> jdfjs = new HashSet<>(0);
	@Id
	@GeneratedValue(strategy = IDENTITY)

	@Column(name = "ID", unique = true, nullable = false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "area")
	public HyArea getArea() {
		return area;
	}
	public void setArea(HyArea area) {
		this.area = area;
	}
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "creator")
	public HyAdmin getCreator() {
		return creator;
	}
	public void setCreator(HyAdmin creator) {
		this.creator = creator;
	}
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ticket_supplier")
	public PiaowubuGongyingshang getTicketSupplier() {
		return ticketSupplier;
	}
	public void setTicketSupplier(PiaowubuGongyingshang ticketSupplier) {
		this.ticketSupplier = ticketSupplier;
	}
	public String getStar() {
		return star;
	}
	public void setStar(String star) {
		this.star = star;
	}
	public String getHotelName() {
		return hotelName;
	}
	public void setHotelName(String hotelName) {
		this.hotelName = hotelName;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getReserveKnow() {
		return reserveKnow;
	}
	public void setReserveKnow(String reserveKnow) {
		this.reserveKnow = reserveKnow;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_time", length = 19)
	@DateTimeFormat(iso=ISO.DATE_TIME)
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	@PrePersist
	public void prePersist() {
		this.createTime = new Date();
	}
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "hotelId", cascade={CascadeType.PERSIST,CascadeType.MERGE}, orphanRemoval=true)
	public Set<PiaowubuJiudianfangjian> getJdfjs() {
		return jdfjs;
	}
	public void setJdfjs(Set<PiaowubuJiudianfangjian> jdfjs) {
		this.jdfjs = jdfjs;
	}

}
