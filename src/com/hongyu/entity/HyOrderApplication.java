package com.hongyu.entity;
// Generated 2017-12-12 17:05:00 by Hibernate Tools 3.6.0.Final

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.id.IncrementGenerator;

/**
 * HyOrderApplication generated by hbm2java
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "hy_order_application")
public class HyOrderApplication implements java.io.Serializable {

	public static final Integer STATUS_REJECT = 0;	//未通过
	public static final Integer STATUS_ACCEPT = 1;	//已通过
	private Long id;
	private Long cancleGroupId;
	private Integer status;
	//订单申请状态，
	//门店退团状态：0待供应商审核，1待品控中心限额审核，2待财务审核，3待退款，4已退款，5已驳回。
	//售后退款状态：0待供应商审核，1待品控中心限额审核，2待财务审核，3待退款，4已退款，5已驳回。
	//供应商消团状态：0待门店审核，1待品控中心限额审核，2待财务审核，3待退款，4已退款，5已驳回。
	//门店确认订单：0驳回，1通过
	//门店取消订单：0驳回，1通过
	private Integer isSubStatis;	//分公司是否分成，默认是0；只有分公司的订单才会用此字段
	private BigDecimal jiesuanMoney;
	private BigDecimal waimaiMoney;
	private BigDecimal baoxianJiesuanMoney;
	private BigDecimal baoxianWaimaiMoney;
	private String content;	//步骤内容
	private Integer outcome;	//暂时不用
	private HyAdmin operator;
	private Long orderId;
	private String orderNumber;
	private Date createtime;
	private List<HyOrderApplicationItem> hyOrderApplicationItems;
	
	private String view;	//日志意见内容
	private String processInstanceId;
	private Integer type;	//订单申请类型。 0：门店退团，1：售后退款，2：供应商消团,3:门店确认订单，
	//							//4：门店取消订单,5:门店订单支付,6供应商确认,7供应商驳回,8门店下订单
	//							//13：门店保险退款 14：微商保险退款 15：官网保险退款

	//申请类型常量
	public static final Integer STORE_CANCEL_GROUP = 0;
	public static final Integer STORE_CUSTOMER_SERVICE = 1;
	public static final Integer PROVIDER_CANCEL_GROUP = 2;
	public static final Integer STORE_CONFIRM_ORDER = 3;
	public static final Integer STORE_CANCEL_ORDER = 4;
	public static final Integer STORE_PAY_ORDER = 5;
	public static final Integer PROVIDER_CONFIRM_ORDER = 6;
	public static final Integer PROVIDER_REJECT_ORDER = 7;
	
	public static final Integer PAY_OVERTIME_CANCEL_ORDER = 11;
	public static final Integer PROVIDER_CONFIRM_OVERTIME_CANCEL_ORDER = 12;
	public static final Integer STORE_INSURANCE_REFUND = 13;
	public static final Integer WEBUSINESS_INSURANCE_REFUND = 14;
	public static final Integer OFFICIAL_WEBSITE_REFUND = 15;
	
	
	// add by guoxinze 20181021
	private Date startDate;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "cancle_group_id")
	public Long getCancleGroupId() {
		return this.cancleGroupId;
	}

	public void setCancleGroupId(Long cancleGroupId) {
		this.cancleGroupId = cancleGroupId;
	}

	@Column(name = "status")
	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Column(name = "jiesuan_money", precision = 10)
	public BigDecimal getJiesuanMoney() {
		return this.jiesuanMoney;
	}

	public void setJiesuanMoney(BigDecimal jiesuanMoney) {
		this.jiesuanMoney = jiesuanMoney;
	}

	@Column(name = "waimai_money", precision = 10)
	public BigDecimal getWaimaiMoney() {
		return this.waimaiMoney;
	}

	public void setWaimaiMoney(BigDecimal waimaiMoney) {
		this.waimaiMoney = waimaiMoney;
	}



	@Column(name="baoxian_jiesuan_money",precision = 10)
	public BigDecimal getBaoxianJiesuanMoney() {
		return baoxianJiesuanMoney;
	}

	public void setBaoxianJiesuanMoney(BigDecimal baoxianJiesuanMoney) {
		this.baoxianJiesuanMoney = baoxianJiesuanMoney;
	}

	@Column(name="baoxian_waimai_money",precision = 10)
	public BigDecimal getBaoxianWaimaiMoney() {
		return baoxianWaimaiMoney;
	}

	public void setBaoxianWaimaiMoney(BigDecimal baoxianWaimaiMoney) {
		this.baoxianWaimaiMoney = baoxianWaimaiMoney;
	}

	@Column(name = "content")
	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Column(name = "outcome")
	public Integer getOutcome() {
		return this.outcome;
	}

	public void setOutcome(Integer outcome) {
		this.outcome = outcome;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "operator_id")
	public HyAdmin getOperator() {
		return this.operator;
	}

	public void setOperator(HyAdmin operator) {
		this.operator = operator;
	}

	@Column(name = "order_id")
	public Long getOrderId() {
		return this.orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "createtime", length = 19)
	public Date getCreatetime() {
		return this.createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "hyOrderApplication", cascade = CascadeType.ALL, orphanRemoval = true)
	public List<HyOrderApplicationItem> getHyOrderApplicationItems() {
		return hyOrderApplicationItems;
	}

	public void setHyOrderApplicationItems(List<HyOrderApplicationItem> hyOrderApplicationItems) {
		this.hyOrderApplicationItems = hyOrderApplicationItems;
	}

	@PrePersist
	public void prePersist() {
		this.setCreatetime(new Date());
		this.isSubStatis = 0;
	}

	@Column(name="view")
	public String getView() {
		return view;
	}

	public void setView(String view) {
		this.view = view;
	}

	@Column(name="process_instance_id")
	public String getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	@Column(name="type")
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
	
	
	@Column(name="is_sub_statis")
	public Integer getIsSubStatis() {
		return isSubStatis;
	}

	public void setIsSubStatis(Integer isSubStatis) {
		this.isSubStatis = isSubStatis;
	}

	@Transient
	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	@Transient
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	

}
