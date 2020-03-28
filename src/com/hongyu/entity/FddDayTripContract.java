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

/**
 * 一日游合同
 * @author liyang
 *
 */
@Entity
@Table(name = "hy_fdd_daytrip_contract")
public class FddDayTripContract implements Serializable{
	//id
	private Long id;
	//合同编号
	private String contractId;
	//合同对应订单id
	private Long orderId;
	//合同创建日期
	private Date createDate;
	//合同修改日期
	private Date modifyDate;
	//合同签署日期
	private Date signDate;
	//合同作废日期
	private Date cancelDate;
	//合同状态    1--客户完善信息   2--提交法大大生成成功  3--虹宇已签署   4--客户已签署  5--已取消
	private Integer status = 1;
	//虹宇签约的交易号
	private String hyTransactionId;
	//客户签约的交易号
	private String CustomerTransactionId;
	//合同下载地址
	private String downloadUrl;
	//合同查看地址
	private String viewpdfUrl;
	//合同签署人的CA证书号
	private String customerCANum;
	
	/***以下字段都是合同表中的需要填写的字段****/
	private String customer;//游客姓名(系统数据)
	private String customerIDNum;//游客证件号(调用系统数据)
	private Integer customerNum;//游客数量(调用系统数据)
	private String hyName;//虹宇名称(调用系统数据)
	private String hyId;//虹宇许可证编号(调用系统数据)
	
	
	private String guideName;//导游姓名(非必填)
	private String guideIDNum;//导游证号(非必填)
	private String guidePhone;//导游电话(非必填)
	private Integer ifShopping;//是否购物(1为是  0为否)(调用系统数据)
	private String shoppingAddress;//购物地点名称(非必填)
	
	
	private String lineInfo;//线路信息(调用系统数据)
	private Integer trafficType;//交通方式  (0--包车游  1--合车游)(调用系统数据)
	private String busNumber;//车牌号码(非必填)
	private String driver;//驾驶员(非必填)
	private Integer ifAirConditioner;//车内是否有空调(0--否  1--是)(非必填)
	private String trafficStandard;//交通标准(非必填)
	
	
	private String breakfastAddress;//早餐地点(非必填)
	private String breakfastStandard;//早餐标准(非必填)
	private String lunchAddress;//午餐地点(非必填)
	private String lunchStandard;//午餐标准(非必填)
	private String dinnerAddress;//晚餐地点(非必填)
	private String dinnerStandard;//晚餐标准(非必填)
	
	private Integer adultNum;//成人数量(手动输入)
	private Integer childrenNum;//儿童数量(手动输入)
	private BigDecimal adultTicketPrice;//成人票价(手动输入)
	private BigDecimal childrenTicketPrice;//儿童票价(手动输入)
	private BigDecimal totalPrice;//总价
	private String feeNote;//团费包含内容（备注信息）(从系统获取)
	private String haveNot;//团费中不包含的项目(手动输入)(非必填)
	private Date paymentTime;//支付时间(手动输入)(非必填)
	private Integer paymentType;//支付方式（1.转账 2.支付宝 3.微信支付 4.现金 5.预存款 6.刷卡）(手动输入)(非必填)
	
	/*****出现争议的时候的解决方式******/
	private String negotiationPhone;//协商电话(一般是旅行社电话)(手动输入)(非必填)
	private String complainPhone;//投诉电话默认是12301(手动输入)(非必填)
	private Integer judgementType;//解决方式(1--提交仲裁委员会 2--提交法院)(手动输入)(非必填)
	private String arbitrationCommittee;//仲裁委员会名称(手动输入)(非必填)
	
	private String otherNote;//其他约定(手动输入)(非必填)
	
	
	private String customerAddress;//游客地址(非必填)
	private String customerPhone;//游客电话(调用系统数据)
	private String customerEmail;//游客电子信箱(非必填)
	private String hyOperator;	//虹宇经办人(调用系统数据)
	private String hyAddress;	//虹宇营业地址(调用系统数据)
	private String hyPhone;	//虹宇电话(调用系统数据)
	private String hyEmail;//虹宇电子信箱(非必填)
	private Date customerSignTime;//游客签约时间(调用系统数据)
	private Date hySignTime;//虹宇签约时间(调用系统数据)
	//在模板表单中有两个签约地点，一个是customerSignAddress，另一个是hySignAddress。需要设定两个值
	private String signAddress="网上签约";	//签约地点(默认为网上签约)
	//还有一个游客确认签字在页面最底端，字段名是customerName
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
	@Column(name = "customer_ca_num")
	public String getCustomerCANum() {
		return customerCANum;
	}
	public void setCustomerCANum(String customerCANum) {
		this.customerCANum = customerCANum;
	}
	@Column(name = "customer")
	public String getCustomer() {
		return customer;
	}
	public void setCustomer(String customer) {
		this.customer = customer;
	}
	@Column(name = "customer_id_num")
	public String getCustomerIDNum() {
		return customerIDNum;
	}
	public void setCustomerIDNum(String customerIDNum) {
		this.customerIDNum = customerIDNum;
	}
	@Column(name = "customer_num")
	public Integer getCustomerNum() {
		return customerNum;
	}
	public void setCustomerNum(Integer customerNum) {
		this.customerNum = customerNum;
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
	@Column(name = "guide_name")
	public String getGuideName() {
		return guideName;
	}
	public void setGuideName(String guideName) {
		this.guideName = guideName;
	}
	@Column(name = "guide_id_num")
	public String getGuideIDNum() {
		return guideIDNum;
	}
	public void setGuideIDNum(String guideIDNum) {
		this.guideIDNum = guideIDNum;
	}
	@Column(name = "guide_phone")
	public String getGuidePhone() {
		return guidePhone;
	}
	public void setGuidePhone(String guidePhone) {
		this.guidePhone = guidePhone;
	}
	@Column(name = "if_shopping")
	public Integer getIfShopping() {
		return ifShopping;
	}
	public void setIfShopping(Integer ifShopping) {
		this.ifShopping = ifShopping;
	}
	@Column(name = "shopping_address")
	public String getShoppingAddress() {
		return shoppingAddress;
	}
	public void setShoppingAddress(String shoppingAddress) {
		this.shoppingAddress = shoppingAddress;
	}
	@Column(name = "line_info")
	public String getLineInfo() {
		return lineInfo;
	}
	public void setLineInfo(String lineInfo) {
		this.lineInfo = lineInfo;
	}
	@Column(name = "traffic_type")
	public Integer getTrafficType() {
		return trafficType;
	}
	public void setTrafficType(Integer trafficType) {
		this.trafficType = trafficType;
	}
	@Column(name = "bus_number")
	public String getBusNumber() {
		return busNumber;
	}
	public void setBusNumber(String busNumber) {
		this.busNumber = busNumber;
	}
	@Column(name = "driver")
	public String getDriver() {
		return driver;
	}
	public void setDriver(String driver) {
		this.driver = driver;
	}
	@Column(name = "if_air_conditioner")
	public Integer getIfAirConditioner() {
		return ifAirConditioner;
	}
	public void setIfAirConditioner(Integer ifAirConditioner) {
		this.ifAirConditioner = ifAirConditioner;
	}
	@Column(name = "traffic_standard")
	public String getTrafficStandard() {
		return trafficStandard;
	}
	public void setTrafficStandard(String trafficStandard) {
		this.trafficStandard = trafficStandard;
	}
	@Column(name = "breakfast_address")
	public String getBreakfastAddress() {
		return breakfastAddress;
	}
	public void setBreakfastAddress(String breakfastAddress) {
		this.breakfastAddress = breakfastAddress;
	}
	@Column(name = "breakfast_standard")
	public String getBreakfastStandard() {
		return breakfastStandard;
	}
	public void setBreakfastStandard(String breakfastStandard) {
		this.breakfastStandard = breakfastStandard;
	}
	@Column(name = "lunch_address")
	public String getLunchAddress() {
		return lunchAddress;
	}
	public void setLunchAddress(String lunchAddress) {
		this.lunchAddress = lunchAddress;
	}
	@Column(name = "lunch_standard")
	public String getLunchStandard() {
		return lunchStandard;
	}
	public void setLunchStandard(String lunchStandard) {
		this.lunchStandard = lunchStandard;
	}
	@Column(name = "dinner_address")
	public String getDinnerAddress() {
		return dinnerAddress;
	}
	public void setDinnerAddress(String dinnerAddress) {
		this.dinnerAddress = dinnerAddress;
	}
	@Column(name = "dinner_standard")
	public String getDinnerStandard() {
		return dinnerStandard;
	}
	public void setDinnerStandard(String dinnerStandard) {
		this.dinnerStandard = dinnerStandard;
	}
	@Column(name = "adult_num")
	public Integer getAdultNum() {
		return adultNum;
	}
	public void setAdultNum(Integer adultNum) {
		this.adultNum = adultNum;
	}
	@Column(name = "children_num")
	public Integer getChildrenNum() {
		return childrenNum;
	}
	public void setChildrenNum(Integer childrenNum) {
		this.childrenNum = childrenNum;
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
	@Column(name = "total_price")
	public BigDecimal getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}
	@Column(name = "fee_note")
	public String getFeeNote() {
		return feeNote;
	}
	public void setFeeNote(String feeNote) {
		this.feeNote = feeNote;
	}
	@Column(name = "have_not")
	public String getHaveNot() {
		return haveNot;
	}
	public void setHaveNot(String haveNot) {
		this.haveNot = haveNot;
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
	@Column(name = "payment_type")
	public Integer getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(Integer paymentType) {
		this.paymentType = paymentType;
	}
	@Column(name = "negotiation_phone")
	public String getNegotiationPhone() {
		return negotiationPhone;
	}
	public void setNegotiationPhone(String negotiationPhone) {
		this.negotiationPhone = negotiationPhone;
	}
	@Column(name = "complain_phone")
	public String getComplainPhone() {
		return complainPhone;
	}
	public void setComplainPhone(String complainPhone) {
		this.complainPhone = complainPhone;
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
	@Column(name = "customer_email")
	public String getCustomerEmail() {
		return customerEmail;
	}
	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
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
