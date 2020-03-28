package com.hongyu.entity;

import java.io.Serializable;
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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@SuppressWarnings("serial")
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler",
						})
@Table(name = "hy_ticket_inbound")
public class HyTicketInbound implements Serializable {
    private Long id;
    private Integer type; //1-酒店,门票,酒加景;2-认购门票
    private Long priceInboundId; //价格库存id(如果type=1,就是酒店,门票,酒加景的价格id;如果type=2就是认购门票id)
    private Date day; //具体日期
    private Integer inventory; //库存量
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name="type")
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	
	@Column(name="price_inbound_id")
	public Long getPriceInboundId() {
		return priceInboundId;
	}
	public void setPriceInboundId(Long priceInboundId) {
		this.priceInboundId = priceInboundId;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="day", length=19)
	@DateTimeFormat(pattern="yyyy-MM-dd")
	public Date getDay() {
		return day;
	}
	public void setDay(Date day) {
		this.day = day;
	}
	
	@Column(name="inventory")
	public Integer getInventory() {
		return inventory;
	}
	public void setInventory(Integer inventory) {
		this.inventory = inventory;
	}
}
