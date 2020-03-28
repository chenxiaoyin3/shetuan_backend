package com.hongyu.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;
@Entity
@Table(name = "hy_fddcontract")
public class FddContract implements Serializable{
	private static final long serialVersionUID = 1L;
	//id
	private Long id;
	//合同编号
	private String contractId;
	//合同对应订单id
	private Long orderId;
	//合同类型   0--国内一日游  1--国内游  2--境外游
	private Integer type;
	//合同创建日期
	private Date createDate;
	//合同修改日期
	private Date modifyDate;
	//合同签署日期
	private Date signDate;
	//合同作废日期
	private Date cancelDate;
	//合同状态    1--客户完善信息   2--提交法大大生成成功  3--虹宇已签署   4--客户已签署  5--已取消  6--部分退团需重签
	private Integer status = 1;
	//虹宇签约的交易号
	private String hyTransactionId;
	//客户签约的交易号
	private String CustomerTransactionId;
	//合同下载地址
	private String downloadUrl;
	//合同查看地址
	private String viewpdfUrl;
	//旅游团代表--合同签署人(调用系统数据)
	private String customer;
	//合同签署人的CA证书号
	private String customerCANum;
	//该订单的团总共包含游客数量(调用系统数据)
	private Integer customerNum;
	//旅行社名称(调用系统数据)
	private String hyName;
	//旅行社许可编号(调用系统数据)
	private String hyId;
	//发团时间(调用系统数据)
	private Date startTime;
	//收团时间(调用系统数据)
	private Date endTime;
	//旅游天数(调用系统数据)
	private Integer days;
	//饭店住宿天数(调用系统数据)
	private Integer stayAtHotelDays;
	//成人票价/人(手动输入)
	private BigDecimal adultTicketPrice;
	//儿童票价/人(手动填写)
	private BigDecimal childrenTicketPrice;
	//导游服务费(手动填写)
	private BigDecimal guideServiceFee;
	//旅游总价(手动填写)
	private BigDecimal totalPrice;
	//支付方式 （1.转账 2.支付宝 3.微信支付 4.现金 5.预存款 6.刷卡）(手动输入)
	private Integer paymentMethod;
	//支付时间(手动填写)
	private Date paymentTime;
	//保险购买方式		1---委托旅行社购买  2---自行购买  3---不购买(手动填写)
	private Integer buyInsuranceType = 1;
	//如果委托旅行社购买，那么购买的保险名称是。(手动填写)
	private String insuranceName;
	//成团最低人数(手动填写)
	private Integer minGroupPersonNum = 16;
	//撤团方式1    0--不同意，1--同意(默认值)
	private Integer cancelMethod1 = 0;
	//撤团方式1==委托其他旅行社旅行合同
	//受委托旅行社名称(调用系统数据)
	private String otherTravelAgency;
	//撤团方式2--延期出团	 0--不同意，1--同意(默认值)
	private Integer cancelMethod2 = 0;
	//撤团方式3--换其他线路出团	 0--不同意，1--同意(默认值)
	private Integer cancelMethod3 = 0;
	//撤团方式4--解除合同	 0--不同意，1--同意(默认值)
	private Integer cancelMethod4 = 0;
	//拼团	 0--不同意，1--同意(默认值)
	private Integer ifPintuan = 0;
	//拼团的旅行社(手动填写)
	private String pintuanTravelAgency;
	//纠纷解决方案	1--提交仲裁委员会  2--向法院起诉(手动填写)
	private Integer judgementType = 2;
	//仲裁委员会名称(手动填写)
	private String arbitrationCommittee;
	//其他约定(旅行社手动填写)(非必填)
	private String otherNote;
	//合同份数(调用系统数据)(默认值)
	private Integer contractNum = 2;
	//双方各持份数(调用系统数据)(默认值)
	private Integer oneHaveNum = 1;
	//游客证件号码(调用系统数据)
	private String customerIDNum;
	//游客地址(调用系统数据)(非必填)
	private String customerAddress;
	//游客电话(调用系统数据)
	private String customerPhone;
	//游客邮编(非必填)
	private String customerPostcode;
	//游客电子信箱(非必填)
	private String customerEmail;
	//虹宇经办人(调用系统数据)
	private String hyOperator;
	//虹宇营业地址(调用系统数据)
	private String hyAddress;
	//虹宇电话(调用系统数据)
	private String hyPhone;
	//虹宇邮编(非必填)
	private String hyPostcode;
	//虹宇电子信箱(非必填)
	private String hyEmail;
	//游客签约时间(调用系统数据)
	private Date customerSignTime;
	//虹宇签约时间(调用系统数据)
	private Date hySignTime;
	//签约地点(默认值)
	private String signAddress = "网上签约";
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Column(name = "contract_id")
	public String getContractId() {
		return contractId;
	}
	public void setContractId(String contractId) {
		this.contractId = contractId;
	}
	@Column(name = "order_id")
	public Long getOrderId() {
		return orderId;
	}
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	@Column(name = "type")
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_date", length = 19)
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "modify_date", length = 19)
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	public Date getModifyDate() {
		return modifyDate;
	}
	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "sign_date", length = 19)
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	public Date getSignDate() {
		return signDate;
	}
	public void setSignDate(Date signDate) {
		this.signDate = signDate;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "cancel_date", length = 19)
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	public Date getCancelDate() {
		return cancelDate;
	}
	public void setCancelDate(Date cancelDate) {
		this.cancelDate = cancelDate;
	}
	@Column(name = "status")
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	@Column(name = "hy_transaction_id")
	public String getHyTransactionId() {
		return hyTransactionId;
	}
	public void setHyTransactionId(String hyTransactionId) {
		this.hyTransactionId = hyTransactionId;
	}
	@Column(name = "customer_transaction_id")
	public String getCustomerTransactionId() {
		return CustomerTransactionId;
	}
	public void setCustomerTransactionId(String customerTransactionId) {
		CustomerTransactionId = customerTransactionId;
	}
	@Column(name = "download_url")
	public String getDownloadUrl() {
		return downloadUrl;
	}
	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}
	@Column(name = "viewpdf_url")
	public String getViewpdfUrl() {
		return viewpdfUrl;
	}
	public void setViewpdfUrl(String viewpdfUrl) {
		this.viewpdfUrl = viewpdfUrl;
	}
	@Column(name = "customer")
	public String getCustomer() {
		return customer;
	}
	public void setCustomer(String customer) {
		this.customer = customer;
	}
	@Column(name = "customer_ca_num")
	public String getCustomerCANum() {
		return customerCANum;
	}
	public void setCustomerCANum(String customerCANum) {
		this.customerCANum = customerCANum;
	}
	@Column(name = "customer_num")
	public Integer getCustomerNum() {
		return customerNum;
	}
	public void setCustomerNum(Integer customerNum) {
		this.customerNum = customerNum;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "start_time", length = 19)
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "end_time", length = 19)
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	@Column(name = "days")
	public Integer getDays() {
		return days;
	}
	public void setDays(Integer days) {
		this.days = days;
	}
	@Column(name = "stay_at_hotel_days")
	public Integer getStayAtHotelDays() {
		return stayAtHotelDays;
	}
	public void setStayAtHotelDays(Integer stayAtHotelDays) {
		this.stayAtHotelDays = stayAtHotelDays;
	}
	@Column(name = "adult_ticket_price")
	public BigDecimal getAdultTicketPrice() {
		return adultTicketPrice;
	}
	public void setAdultTicketPrice(BigDecimal adultTicketPrice) {
		this.adultTicketPrice = adultTicketPrice;
	}
	@Column(name = "children_ticket_price")
	public BigDecimal getChildrenTicketPrice() {
		return childrenTicketPrice;
	}
	public void setChildrenTicketPrice(BigDecimal childrenTicketPrice) {
		this.childrenTicketPrice = childrenTicketPrice;
	}
	@Column(name = "guide_service_fee")
	public BigDecimal getGuideServiceFee() {
		return guideServiceFee;
	}
	public void setGuideServiceFee(BigDecimal guideServiceFee) {
		this.guideServiceFee = guideServiceFee;
	}
	@Column(name = "total_price")
	public BigDecimal getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}
	@Column(name = "payment_method")
	public Integer getPaymentMethod() {
		return paymentMethod;
	}
	public void setPaymentMethod(Integer paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "payment_time", length = 19)
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	public Date getPaymentTime() {
		return paymentTime;
	}
	public void setPaymentTime(Date paymentTime) {
		this.paymentTime = paymentTime;
	}
	@Column(name = "buy_insurance_type")
	public Integer getBuyInsuranceType() {
		return buyInsuranceType;
	}
	public void setBuyInsuranceType(Integer buyInsuranceType) {
		this.buyInsuranceType = buyInsuranceType;
	}
	@Column(name = "insurance_name")
	public String getInsuranceName() {
		return insuranceName;
	}
	public void setInsuranceName(String insuranceName) {
		this.insuranceName = insuranceName;
	}
	@Column(name = "min_group_person_num")
	public Integer getMinGroupPersonNum() {
		return minGroupPersonNum;
	}
	public void setMinGroupPersonNum(Integer minGroupPersonNum) {
		this.minGroupPersonNum = minGroupPersonNum;
	}
	@Column(name = "cancel_method_1")
	public Integer getCancelMethod1() {
		return cancelMethod1;
	}
	public void setCancelMethod1(Integer cancelMethod1) {
		this.cancelMethod1 = cancelMethod1;
	}
	@Column(name = "other_travel_agency")
	public String getOtherTravelAgency() {
		return otherTravelAgency;
	}
	public void setOtherTravelAgency(String otherTravelAgency) {
		this.otherTravelAgency = otherTravelAgency;
	}
	@Column(name = "cancel_method_2")
	public Integer getCancelMethod2() {
		return cancelMethod2;
	}
	public void setCancelMethod2(Integer cancelMethod2) {
		this.cancelMethod2 = cancelMethod2;
	}
	@Column(name = "cancel_method_3")
	public Integer getCancelMethod3() {
		return cancelMethod3;
	}
	public void setCancelMethod3(Integer cancelMethod3) {
		this.cancelMethod3 = cancelMethod3;
	}
	@Column(name = "cancel_method_4")
	public Integer getCancelMethod4() {
		return cancelMethod4;
	}
	public void setCancelMethod4(Integer cancelMethod4) {
		this.cancelMethod4 = cancelMethod4;
	}
	@Column(name = "if_pintuan")
	public Integer getIfPintuan() {
		return ifPintuan;
	}
	public void setIfPintuan(Integer ifPintuan) {
		this.ifPintuan = ifPintuan;
	}
	@Column(name = "pintuan_travel_agency")
	public String getPintuanTravelAgency() {
		return pintuanTravelAgency;
	}
	public void setPintuanTravelAgency(String pintuanTravelAgency) {
		this.pintuanTravelAgency = pintuanTravelAgency;
	}
	@Column(name = "judgement_type")
	public Integer getJudgementType() {
		return judgementType;
	}
	public void setJudgementType(Integer judgementType) {
		this.judgementType = judgementType;
	}
	@Column(name = "arbitration_committee")
	public String getArbitrationCommittee() {
		return arbitrationCommittee;
	}
	public void setArbitrationCommittee(String arbitrationCommittee) {
		this.arbitrationCommittee = arbitrationCommittee;
	}
	@Column(name = "other_note")
	public String getOtherNote() {
		return otherNote;
	}
	public void setOtherNote(String otherNote) {
		this.otherNote = otherNote;
	}
	@Column(name = "contract_num")
	public Integer getContractNum() {
		return contractNum;
	}
	public void setContractNum(Integer contractNum) {
		this.contractNum = contractNum;
	}
	@Column(name = "one_have_num")
	public Integer getOneHaveNum() {
		return oneHaveNum;
	}
	public void setOneHaveNum(Integer oneHaveNum) {
		this.oneHaveNum = oneHaveNum;
	}
	@Column(name = "customer_IDnum")
	public String getCustomerIDNum() {
		return customerIDNum;
	}
	public void setCustomerIDNum(String customerIDNum) {
		this.customerIDNum = customerIDNum;
	}
	@Column(name = "customer_address")
	public String getCustomerAddress() {
		return customerAddress;
	}
	public void setCustomerAddress(String customerAddress) {
		this.customerAddress = customerAddress;
	}
	@Column(name = "customer_phone")
	public String getCustomerPhone() {
		return customerPhone;
	}
	public void setCustomerPhone(String customerPhone) {
		this.customerPhone = customerPhone;
	}
	@Column(name = "customer_postcode")
	public String getCustomerPostcode() {
		return customerPostcode;
	}
	public void setCustomerPostcode(String customerPostcode) {
		this.customerPostcode = customerPostcode;
	}
	@Column(name = "customer_email")
	public String getCustomerEmail() {
		return customerEmail;
	}
	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}
	@Column(name = "hy_name")
	public String getHyName() {
		return hyName;
	}
	public void setHyName(String hyName) {
		this.hyName = hyName;
	}
	@Column(name = "hy_id")
	public String getHyId() {
		return hyId;
	}
	public void setHyId(String hyId) {
		this.hyId = hyId;
	}
	@Column(name = "hy_operator")
	public String getHyOperator() {
		return hyOperator;
	}
	public void setHyOperator(String hyOperator) {
		this.hyOperator = hyOperator;
	}
	@Column(name = "hy_address")
	public String getHyAddress() {
		return hyAddress;
	}
	public void setHyAddress(String hyAddress) {
		this.hyAddress = hyAddress;
	}
	@Column(name = "hy_phone")
	public String getHyPhone() {
		return hyPhone;
	}
	public void setHyPhone(String hyPhone) {
		this.hyPhone = hyPhone;
	}
	@Column(name = "hy_postcode")
	public String getHyPostcode() {
		return hyPostcode;
	}
	public void setHyPostcode(String hyPostcode) {
		this.hyPostcode = hyPostcode;
	}
	@Column(name = "hy_email")
	public String getHyEmail() {
		return hyEmail;
	}
	public void setHyEmail(String hyEmail) {
		this.hyEmail = hyEmail;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "customer_sign_time", length = 19)
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	public Date getCustomerSignTime() {
		return customerSignTime;
	}
	public void setCustomerSignTime(Date customerSignTime) {
		this.customerSignTime = customerSignTime;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "hy_sign_time", length = 19)
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	public Date getHySignTime() {
		return hySignTime;
	}
	public void setHySignTime(Date hySignTime) {
		this.hySignTime = hySignTime;
	}
	@Column(name = "sign_address")
	public String getSignAddress() {
		return signAddress;
	}
	public void setSignAddress(String signAddress) {
		this.signAddress = signAddress;
	}
	@PrePersist
	public void prePersist(){
		this.setCreateDate(new Date());
	}
	@PreUpdate
	public void preUpdate(){
		this.setModifyDate(new Date());
	}
	
}
