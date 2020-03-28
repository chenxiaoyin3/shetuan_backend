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
import javax.validation.constraints.Digits;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/**
 * 门户完善团价格
 * @author liyang
 * @version 2019年1月4日 下午12:08:05
 */
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler",
	"hyGroupPriceSwds",
	"hyGroup",
	"hyGroupPrice"
	})
@Table(name = "mh_group_price")
public class MhGroupPrice implements Serializable{
	/*序列化版本号*/
	private static final long serialVersionUID = 1L;
	/*主键id*/
	private Long id;
	/*对应的团*/
	private HyGroup hyGroup;
	/*完善人*/
	private HyAdmin operator;
	/*首次完善时间*/
	private Date createTime;
	/*更新完善时间*/
	private Date updateTime;
	/*对应的门店团价格 */
	private HyGroupPrice hyGroupPrice;
	/*官网成人销售价*/
	private BigDecimal mhAdultSalePrice;
	/*官网成人外卖价 */
	private BigDecimal mhAdultWaimaiPrice;
	/*官网儿童销售价*/
	private BigDecimal mhChildrenSalePrice;
	/*官网儿童外卖价 */
	private BigDecimal mhChildrenWaimaiPrice;
	/*官网老人销售价*/
	private BigDecimal mhOldSalePrice;
	/*官网老人外卖价 */
	private BigDecimal mhOldWaimaiPrice;
	/*官网学生销售价*/
	private BigDecimal mhStudentSalePrice;
	/*官网学生外卖价 */
	private BigDecimal mhStudentWaimaiPrice;
	
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
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "root_id")
	public HyGroupPrice getHyGroupPrice() {
		return hyGroupPrice;
	}
	
	public void setHyGroupPrice(HyGroupPrice hyGroupPrice) {
		this.hyGroupPrice = hyGroupPrice;
	}
	@Column(name = "adult_price1")
	public BigDecimal getMhAdultSalePrice() {
		return mhAdultSalePrice;
	}

	public void setMhAdultSalePrice(BigDecimal mhAdultSalePrice) {
		this.mhAdultSalePrice = mhAdultSalePrice;
	}
	@Column(name = "adult_price6")
	public BigDecimal getMhAdultWaimaiPrice() {
		return mhAdultWaimaiPrice;
	}

	public void setMhAdultWaimaiPrice(BigDecimal mhAdultWaimaiPrice) {
		this.mhAdultWaimaiPrice = mhAdultWaimaiPrice;
	}
	@Column(name = "children_price1")
	public BigDecimal getMhChildrenSalePrice() {
		return mhChildrenSalePrice;
	}

	public void setMhChildrenSalePrice(BigDecimal mhChildrenSalePrice) {
		this.mhChildrenSalePrice = mhChildrenSalePrice;
	}
	@Column(name = "children_price6")
	public BigDecimal getMhChildrenWaimaiPrice() {
		return mhChildrenWaimaiPrice;
	}

	public void setMhChildrenWaimaiPrice(BigDecimal mhChildrenWaimaiPrice) {
		this.mhChildrenWaimaiPrice = mhChildrenWaimaiPrice;
	}
	@Column(name = "old_price1")
	public BigDecimal getMhOldSalePrice() {
		return mhOldSalePrice;
	}

	public void setMhOldSalePrice(BigDecimal mhOldSalePrice) {
		this.mhOldSalePrice = mhOldSalePrice;
	}
	@Column(name = "old_price6")
	public BigDecimal getMhOldWaimaiPrice() {
		return mhOldWaimaiPrice;
	}

	public void setMhOldWaimaiPrice(BigDecimal mhOldWaimaiPrice) {
		this.mhOldWaimaiPrice = mhOldWaimaiPrice;
	}
	@Column(name = "student_price1")
	public BigDecimal getMhStudentSalePrice() {
		return mhStudentSalePrice;
	}

	public void setMhStudentSalePrice(BigDecimal mhStudentSalePrice) {
		this.mhStudentSalePrice = mhStudentSalePrice;
	}
	@Column(name = "student_price6")
	public BigDecimal getMhStudentWaimaiPrice() {
		return mhStudentWaimaiPrice;
	}

	public void setMhStudentWaimaiPrice(BigDecimal mhStudentWaimaiPrice) {
		this.mhStudentWaimaiPrice = mhStudentWaimaiPrice;
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
