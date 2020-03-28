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
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/**
 * 报账订单的表
 * @author guoxinze
 *
 */
@SuppressWarnings("serial")
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler",
	"hyGroup",
	"hyOrder"
	})
@Table(name = "hy_regulateitem_order")
public class HyRegulateitemOrder implements Serializable {
    private Long id;
    private HyGroup hyGroup;
    private HyOrder hyOrder;
    private String orderNumber;
    private Integer source;
    private Integer num;
    private BigDecimal money;
    private BigDecimal yingshou;
    private BigDecimal yishou;
    
    //20181015 add by gxz 门店名称
    private String mendianName;
    
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
	@JoinColumn(name = "regulate_group")
	public HyGroup getHyGroup() {
		return hyGroup;
	}
	public void setHyGroup(HyGroup hyGroup) {
		this.hyGroup = hyGroup;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "hy_order")
	public HyOrder getHyOrder() {
		return hyOrder;
	}
	public void setHyOrder(HyOrder hyOrder) {
		this.hyOrder = hyOrder;
	}
	
	@Column(name="num")
	public Integer getNum() {
		return num;
	}
	public void setNum(Integer num) {
		this.num = num;
	}
	
	@Column(name="money")
	public BigDecimal getMoney() {
		return money;
	}
	public void setMoney(BigDecimal money) {
		this.money = money;
	}
	
	@Column(name="yingshou")
	public BigDecimal getYingshou() {
		return yingshou;
	}
	public void setYingshou(BigDecimal yingshou) {
		this.yingshou = yingshou;
	}
	
	@Column(name="yishou")
	public BigDecimal getYishou() {
		return yishou;
	}
	public void setYishou(BigDecimal yishou) {
		this.yishou = yishou;
	}
	public String getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	public Integer getSource() {
		return source;
	}
	public void setSource(Integer source) {
		this.source = source;
	}
	public String getMendianName() {
		return mendianName;
	}
	public void setMendianName(String mendianName) {
		this.mendianName = mendianName;
	}
	
	
    
}
