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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hongyu.entity.HyLine.IsSaleEnum;
import com.hongyu.entity.HyLine.RefundTypeEnum;

/**
 * 门户完善线路
 * @author liyang
 * @version 2019年1月4日 上午10:42:01
 */
@Entity
@Table(name = "mh_line")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler","hyLine"})
public class MhLine implements Serializable{
	/*序列化版本号*/
	private static final long serialVersionUID = 1L;
	/*主键id*/
	private Long id;
	/*对应的hyline主键*/
	private HyLine hyLine;
	/*排序--越大越越优先展示*/
	private Integer sort;
	/*是否含有自费项目*/
	private Integer ifSelfPaying;
	/*是否含有购物项目*/
	private Integer ifShopping;
	/*是否上架*/
	private IsSaleEnum isSale;
	/*完善人*/
	private HyAdmin operator;
	/*首次完善时间*/
	private Date createTime;
	/*更新完善时间*/
	private Date updateTime;
	/*线路名称*/
	private String name;
	/*退款类型*/
	private RefundTypeEnum refundType;
	/*所需出境资料说明*/
	private String outboundMemo;
	/*出境资料下载地址*/
	private String chujingFileUrl;
	/*产品推广介绍*/
	private String introduction;
	/*该线路当前有效团的最低官网价格*/
	private BigDecimal bottomPrice;
	/*官网销售量*/
	private Integer saleCount;
	/*简短介绍*/
	private String briefDescription;
	/*推荐理由*/
	private String recommendReason;
	/*费用说明*/
	private String feeDescription;
	/*其他注意事项*/
	private String mattersNeedAttention;
	/*预订须知*/
	private String bookingInformation;
	
	/*线路行程 */
	private List<MhLineTravels> mhLineTravels = new ArrayList<>();
	/*线路退款 */
	private List<MhLineRefund> mhLineRefunds = new ArrayList<>();
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id",unique = true,nullable = false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "root_id")
	public HyLine getHyLine() {
		return hyLine;
	}
	public void setHyLine(HyLine hyLine) {
		this.hyLine = hyLine;
	}
	@Column(name = "sort")
	public Integer getSort() {
		return sort;
	}
	public void setSort(Integer sort) {
		this.sort = sort;
	}
	@Column(name = "if_self_paying")
	public Integer getIfSelfPaying() {
		return ifSelfPaying;
	}
	public void setIfSelfPaying(Integer ifSelfPaying) {
		this.ifSelfPaying = ifSelfPaying;
	}
	@Column(name = "if_shopping")
	public Integer getIfShopping() {
		return ifShopping;
	}
	public void setIfShopping(Integer ifShopping) {
		this.ifShopping = ifShopping;
	}
	@Column(name = "is_sale")
	public IsSaleEnum getIsSale() {
		return isSale;
	}
	public void setIsSale(IsSaleEnum isSale) {
		this.isSale = isSale;
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
	@Column(name = "name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Column(name = "refund_type")
	public RefundTypeEnum getRefundType() {
		return refundType;
	}
	public void setRefundType(RefundTypeEnum refundType) {
		this.refundType = refundType;
	}
	@Column(name = "outbound_memo")
	public String getOutboundMemo() {
		return outboundMemo;
	}
	public void setOutboundMemo(String outboundMemo) {
		this.outboundMemo = outboundMemo;
	}
	@Column(name = "chujing_ziliao")
	public String getChujingFileUrl() {
		return chujingFileUrl;
	}
	public void setChujingFileUrl(String chujingFileUrl) {
		this.chujingFileUrl = chujingFileUrl;
	}
	@Column(name = "introduction")
	public String getIntroduction() {
		return introduction;
	}
	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}
	@Column(name = "bottom_price")
	public BigDecimal getBottomPrice() {
		return bottomPrice;
	}
	public void setBottomPrice(BigDecimal bottomPrice) {
		this.bottomPrice = bottomPrice;
	}
	@Column(name = "sale_count")
	public Integer getSaleCount() {
		return saleCount;
	}
	public void setSaleCount(Integer saleCount) {
		this.saleCount = saleCount;
	}
	@Column(name = "brief_description")
	public String getBriefDescription() {
		return briefDescription;
	}
	public void setBriefDescription(String briefDescription) {
		this.briefDescription = briefDescription;
	}
	@Column(name = "recommend_reason")
	public String getRecommendReason() {
		return recommendReason;
	}
	public void setRecommendReason(String recommendReason) {
		this.recommendReason = recommendReason;
	}
	@Column(name = "fee_description")
	public String getFeeDescription() {
		return feeDescription;
	}
	public void setFeeDescription(String feeDescription) {
		this.feeDescription = feeDescription;
	}
	@Column(name = "matters_need_attention")
	public String getMattersNeedAttention() {
		return mattersNeedAttention;
	}
	public void setMattersNeedAttention(String mattersNeedAttention) {
		this.mattersNeedAttention = mattersNeedAttention;
	}
	@Column(name = "booking_information")
	public String getBookingInformation() {
		return bookingInformation;
	}
	public void setBookingInformation(String bookingInformation) {
		this.bookingInformation = bookingInformation;
	}
	@OrderBy("id asc")
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mhLine", cascade = CascadeType.ALL, orphanRemoval = true)
	public List<MhLineTravels> getMhLineTravels() {
		return mhLineTravels;
	}
	public void setMhLineTravels(List<MhLineTravels> mhLineTravels) {
		this.mhLineTravels = mhLineTravels;
	}
	@OrderBy("id asc")
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mhLine", cascade = CascadeType.ALL, orphanRemoval = true)
	public List<MhLineRefund> getMhLineRefunds() {
		return mhLineRefunds;
	}
	public void setMhLineRefunds(List<MhLineRefund> mhLineRefunds) {
		this.mhLineRefunds = mhLineRefunds;
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
