package com.hongyu.entity;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/**
 * 门户完善团其他价格信息
 * @author liyang
 * @version 2019年1月4日 下午12:13:52
 */
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler",
	"hyGroup",
	"hyGroupOtherprice"
	})
@Table(name = "mh_group_otherprice")
public class MhGroupOtherPrice implements Serializable{
	/*序列化版本号*/
	private static final long serialVersionUID = 1L;
	/*主键id*/
	private Long id;
	/*对应的团*/
	private HyGroup hyGroup;
	/*完善人*/
	private HyAdmin operator;
	/*对应门店的其他价格*/
	private HyGroupOtherprice hyGroupOtherprice;
	/*首次完善时间*/
	private Date createTime;
	/*更新完善时间*/
	private Date updateTime;
	
	/*官网单房差销售价*/
	private BigDecimal mhDanfangchaSalePrice;
	/*官网单房差外卖价*/
	private BigDecimal mhDanfangchaWaimaiPrice;
	
	/*官网补卧铺销售价*/
	private BigDecimal mhBuwopuSalePrice;
	/*官网补卧铺外卖价*/
	private BigDecimal mhBuwopuWaimaiPrice;
	
	/*官网补门票销售价*/
	private BigDecimal mhBumenpiaoSalePrice;
	/*官网补门票外卖价*/
	private BigDecimal mhBumenpiaoWaimaiPrice;
	
	/*官网儿童占床销售价*/
	private BigDecimal mhErtongzhanchuangSalePrice;
	/*官网儿童占床外卖价*/
	private BigDecimal mhErtongzhanchuangWaimaiPrice;
	
	/*官网补床位销售价*/
	private BigDecimal mhBuchuangweiSalePrice;
	/*官网补床位外卖价*/
	private BigDecimal mhBuchuangweiWaimaiPrice;
	


	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "group_id")
	public HyGroup getHyGroup() {
		return this.hyGroup;
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
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_time",length=19)
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "update_time",length=19)
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "root_id")
	public HyGroupOtherprice getHyGroupOtherprice() {
		return hyGroupOtherprice;
	}

	public void setHyGroupOtherprice(HyGroupOtherprice hyGroupOtherprice) {
		this.hyGroupOtherprice = hyGroupOtherprice;
	}
	@Column(name = "danfangcha_price1")
	public BigDecimal getMhDanfangchaSalePrice() {
		return mhDanfangchaSalePrice;
	}

	public void setMhDanfangchaSalePrice(BigDecimal mhDanfangchaSalePrice) {
		this.mhDanfangchaSalePrice = mhDanfangchaSalePrice;
	}
	@Column(name = "danfangcha_price6")
	public BigDecimal getMhDanfangchaWaimaiPrice() {
		return mhDanfangchaWaimaiPrice;
	}

	public void setMhDanfangchaWaimaiPrice(BigDecimal mhDanfangchaWaimaiPrice) {
		this.mhDanfangchaWaimaiPrice = mhDanfangchaWaimaiPrice;
	}
	@Column(name = "buwopu_price1")
	public BigDecimal getMhBuwopuSalePrice() {
		return mhBuwopuSalePrice;
	}

	public void setMhBuwopuSalePrice(BigDecimal mhBuwopuSalePrice) {
		this.mhBuwopuSalePrice = mhBuwopuSalePrice;
	}
	@Column(name = "buwopu_price6")
	public BigDecimal getMhBuwopuWaimaiPrice() {
		return mhBuwopuWaimaiPrice;
	}

	public void setMhBuwopuWaimaiPrice(BigDecimal mhBuwopuWaimaiPrice) {
		this.mhBuwopuWaimaiPrice = mhBuwopuWaimaiPrice;
	}
	@Column(name = "bumenpiao_price1")
	public BigDecimal getMhBumenpiaoSalePrice() {
		return mhBumenpiaoSalePrice;
	}

	public void setMhBumenpiaoSalePrice(BigDecimal mhBumenpiaoSalePrice) {
		this.mhBumenpiaoSalePrice = mhBumenpiaoSalePrice;
	}
	@Column(name = "bumenpiao_price6")
	public BigDecimal getMhBumenpiaoWaimaiPrice() {
		return mhBumenpiaoWaimaiPrice;
	}

	public void setMhBumenpiaoWaimaiPrice(BigDecimal mhBumenpiaoWaimaiPrice) {
		this.mhBumenpiaoWaimaiPrice = mhBumenpiaoWaimaiPrice;
	}
	@Column(name = "ertongzhanchuang_price1")
	public BigDecimal getMhErtongzhanchuangSalePrice() {
		return mhErtongzhanchuangSalePrice;
	}

	public void setMhErtongzhanchuangSalePrice(BigDecimal mhErtongzhanchuangSalePrice) {
		this.mhErtongzhanchuangSalePrice = mhErtongzhanchuangSalePrice;
	}
	@Column(name = "ertongzhanchuang_price6")
	public BigDecimal getMhErtongzhanchuangWaimaiPrice() {
		return mhErtongzhanchuangWaimaiPrice;
	}

	public void setMhErtongzhanchuangWaimaiPrice(BigDecimal mhErtongzhanchuangWaimaiPrice) {
		this.mhErtongzhanchuangWaimaiPrice = mhErtongzhanchuangWaimaiPrice;
	}
	@Column(name = "buchuangwei_price1")
	public BigDecimal getMhBuchuangweiSalePrice() {
		return mhBuchuangweiSalePrice;
	}

	public void setMhBuchuangweiSalePrice(BigDecimal mhBuchuangweiSalePrice) {
		this.mhBuchuangweiSalePrice = mhBuchuangweiSalePrice;
	}
	@Column(name = "buchuangwei_price6")
	public BigDecimal getMhBuchuangweiWaimaiPrice() {
		return mhBuchuangweiWaimaiPrice;
	}

	public void setMhBuchuangweiWaimaiPrice(BigDecimal mhBuchuangweiWaimaiPrice) {
		this.mhBuchuangweiWaimaiPrice = mhBuchuangweiWaimaiPrice;
	}

	@PrePersist
	public void setPrepersist() {
		this.setCreateTime(new Date());
	}
	@PreUpdate
	public void setUpdate() {
		this.setUpdateTime(new Date());
	}
}
