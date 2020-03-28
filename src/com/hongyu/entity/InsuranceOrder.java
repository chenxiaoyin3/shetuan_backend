package com.hongyu.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@SuppressWarnings("serial")
@Entity
@Table(name="hy_insurance_order")
public class InsuranceOrder implements Serializable{
	private Long id;//主键id
	private Date createDate;//创建时间
	private Date modifyDate;//修改时间
	private Long orderId;//订单id
	private Long groupId;//团id
	private Long insuranceId;//保险方案id
	private Date insuredTime;//投保日期
	private Date insuranceStarttime;//保险起始日期
	private Date insuranceEndtime;//保险结束日期
	private Integer status;//投保状态，0未确认，1未投保，2已取消，3已投保，4已撤保，5投保失败
	private Integer type;//0团期投保，1自主投保,2网上投保
	private BigDecimal receivedMoney;//实收金额
	private BigDecimal shifuMoney;//实付金额
	private BigDecimal profit;//利润
	private String downloadUrl;//保单下载地址
	private String jtChannelTradeDate;//渠道交易时间
	private String jtChannelTradeSerialNo;//渠道交易流水号
	private String jtChannelOperateCode;//渠道操作员代码
	private String jtPolicyNo;//保单号(来自江泰返回信息)
	private String jtInsureNo;//投保单号(来自江泰返回信息)
	private String jtOrderNo;//江泰订单编号(来自江泰返回信息)
	private BigDecimal jtSumPremium;//总保费(来自江泰返回信息)
	private BigDecimal jtDiscount;
	private List<HyPolicyHolderInfo> policyHolders = new ArrayList<HyPolicyHolderInfo>();
	
	@OneToMany(fetch=FetchType.EAGER,mappedBy="insuranceOrder",cascade=CascadeType.ALL)
	public List<HyPolicyHolderInfo> getPolicyHolders() {
		return policyHolders;
	}
	public void setPolicyHolders(List<HyPolicyHolderInfo> policyHolders) {
		this.policyHolders = policyHolders;
	}
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id",nullable=false,unique=true)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="create_date")
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="modify_date")
	public Date getModifyDate() {
		return modifyDate;
	}
	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}
	@Column(name="order_id")
	public Long getOrderId() {
		return orderId;
	}
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	@Column(name="group_id")
	public Long getGroupId() {
		return groupId;
	}
	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
	@Column(name="insurance_id")
	public Long getInsuranceId() {
		return insuranceId;
	}
	public void setInsuranceId(Long insuranceId) {
		this.insuranceId = insuranceId;
	}
	@Column(name="insured_time")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getInsuredTime() {
		return insuredTime;
	}
	public void setInsuredTime(Date insuredTime) {
		this.insuredTime = insuredTime;
	}
	@Column(name="insurance_starttime")
	public Date getInsuranceStarttime() {
		return insuranceStarttime;
	}
	public void setInsuranceStarttime(Date insuranceStarttime) {
		this.insuranceStarttime = insuranceStarttime;
	}
	@Column(name="insurance_endtime")
	public Date getInsuranceEndtime() {
		return insuranceEndtime;
	}
	public void setInsuranceEndtime(Date insuranceEndtime) {
		this.insuranceEndtime = insuranceEndtime;
	}
	@Column(name="status")
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	@Column(name="type")
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	@Column(name="received_money")
	public BigDecimal getReceivedMoney() {
		return receivedMoney;
	}
	public void setReceivedMoney(BigDecimal receivedMoney) {
		this.receivedMoney = receivedMoney;
	}
	@Column(name="shifu_money")
	public BigDecimal getShifuMoney() {
		return shifuMoney;
	}
	public void setShifuMoney(BigDecimal shifuMoney) {
		this.shifuMoney = shifuMoney;
	}
	@Column(name="profit")
	public BigDecimal getProfit() {
		return profit;
	}
	public void setProfit(BigDecimal profit) {
		this.profit = profit;
	}
	@PrePersist
	public void prePersist(){
		this.setCreateDate(new Date());
		if(this.status == null) {
			this.status = 0;
		}
		
	}
	@PreUpdate
	public void preUpdate(){
		this.setModifyDate(new Date());
	}
	@Column(name="jt_channel_trade_date")
	public String getJtChannelTradeDate() {
		return jtChannelTradeDate;
	}
	public void setJtChannelTradeDate(String channelTradeDate) {
		this.jtChannelTradeDate = channelTradeDate;
	}
	@Column(name="jt_channel_trade_serial_no")
	public String getJtChannelTradeSerialNo() {
		return jtChannelTradeSerialNo;
	}
	public void setJtChannelTradeSerialNo(String channelTradeSerialNo) {
		this.jtChannelTradeSerialNo = channelTradeSerialNo;
	}
	@Column(name="jt_channel_operate_code")
	public String getJtChannelOperateCode() {
		return jtChannelOperateCode;
	}
	public void setJtChannelOperateCode(String channelOperateCode) {
		this.jtChannelOperateCode = channelOperateCode;
	}

	@Column(name="jt_order_no")
	public String getJtOrderNo() {
		return jtOrderNo;
	}
	public void setJtOrderNo(String jtOrderNo) {
		this.jtOrderNo = jtOrderNo;
	}
	@Column(name="jt_sum_premium")
	public BigDecimal getJtSumPremium() {
		return jtSumPremium;
	}
	public void setJtSumPremium(BigDecimal sumPremium) {
		this.jtSumPremium = sumPremium;
	}
	@Column(name="jt_policy_no")
	public String getJtPolicyNo() {
		return jtPolicyNo;
	}
	public void setJtPolicyNo(String jtPolicyNo) {
		this.jtPolicyNo = jtPolicyNo;
	}
	@Column(name="jt_insure_no")
	public String getJtInsureNo() {
		return jtInsureNo;
	}
	
	public void setJtInsureNo(String jtInsureNo) {
		this.jtInsureNo = jtInsureNo;
	}
	@Column(name="jt_discount")
	public BigDecimal getJtDiscount() {
		return jtDiscount;
	}
	public void setJtDiscount(BigDecimal jtDiscount) {
		this.jtDiscount = jtDiscount;
	}
	@Column(name = "download_url")
	public String getDownloadUrl() {
		return downloadUrl;
	}
	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}
	

}
