package com.hongyu.entity;
// Generated 2017-12-24 21:20:19 by Hibernate Tools 3.6.0.Final

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.grain.entity.BaseEntity;
import com.hongyu.util.Constants.AuditStatus;

/**
 * 线路产品Entity
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "hy_line")
public class HyLine extends BaseEntity {
	
	public enum LineType {
		/** 汽车 */
		qiche,
		
		/** 国内 */
		guonei,
		
		/** 出境 */
		chujing,
	}
	
	public enum RefundTypeEnum {
		/** 全额退款 */
		quane,
		
		/** 阶梯退款 */
		jieti,
	}
	
	public enum IsSaleEnum {
		/** 已上 */
		yishang,
		
		/**未上 */
		weishang,
		
		/** 已下 */
		yixia,
	}
	
	public enum ProductTypeEnum {
		/** 线路 */
		xianlu,
	}

	/** 线路合同 */
	private HySupplierContract contract;
	
	/** 线路供应商 */
	private HySupplier hySupplier;
	
	/** 线路合同的ID */
	private Long cid;
	
	/** 线路所在区域 */
	private HyArea area;
	
	/** 操作人计调 */
	private HyAdmin operator;
	
	/** 可见分公司 */
	private Department company;
	
	/** 保险方案 */
	private Insurance insurance;
	
	/** 产品ID */
	private String pn;
	
	/** 线路名称 */
	private String name;
	
	/** 线路类型 */
	private LineType lineType;
	
	/** 线路的二级分类 */
	private LineCatagoryEntity lineCategory;
	
	/** 产品类型-暂时无用 */
	private ProductTypeEnum productType;
	
	/** 行程天数 */
	private Integer days;
	
	/** 退款类型 */
	private RefundTypeEnum refundType;
	
	/** 是否含保险 */
	private Boolean isInsurance;
	
	/** 是否总社投保 */
	private Boolean isHeadInsurance;
	
	/** 消团说明 */
	private String cancelMemo;
	
	/** 出境资料说明 */
	private String outboundMemo;
	
	/** 内部备注 */
	private String memoInner;
	
	/** 备注 */
	private String memo;
	
	/** 产品推广介绍-富文本 */
	private String introduction; //用逗号分开 产品文件的url
	
	/** 线路推广文件 */
	private String lineFile;
	
	/** 线路的所有团的最低价格 */
	private BigDecimal lowestPrice;
	
	/** 上线状态 */
	private IsSaleEnum isSale;  
	
	/** 是否置顶 */
	private Boolean isTop; // 0 : 否   1 ： 是
	
	/** 置顶时间 */
	private Date topEditTime;
	
	/** 线路审核状态 */
	private AuditStatus lineAuditStatus;
	
	/** 该团所有团期总审核状态 */
	private AuditStatus groupAuditStatus;
	
	/** 截止团期 */
	private Date latestGroup; //线路的最新团期
	
//	private Long koudianXianlu; //合同里面有
	
	/** 点击量 */
	private Long hits;
	
	/** 销售量 */
	private Integer saleCount;
	
	/** 出境资料 */
	private String outbound;
	
	/** 是否可编辑 */
	private Boolean isEdit;// 0 : 否   1 ：是
	
	/** 取消状态 */
	private Boolean isCancel;
	
	/** 线路行程 */
	private List<HyLineTravels> lineTravels = new ArrayList<>();
	
	/** 线路退款 */
	private List<HyLineRefund> lineRefunds = new ArrayList<>();
	
	/** 是否官网售卖 */
	private Boolean isGuanwang;
	
	private Boolean isInner; //是否是内部
	
	private Boolean isPromotion;
	
	private MhLine mhLine;
	
	private Boolean isAutoconfirm;	//是否自动确认
	
	@JsonProperty
	@Column(name = "is_autoconfirm")
	public Boolean getIsAutoconfirm() {
		return this.isAutoconfirm;
	}

	public void setIsAutoconfirm(Boolean isAutoconfirm) {
		this.isAutoconfirm = isAutoconfirm;
	}

	@JsonProperty
	@Column(name = "pn")
	public String getPn() {
		return this.pn;
	}

	public void setPn(String pn) {
		this.pn = pn;
	}

	@JsonProperty
	@Column(name = "name")
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@JsonProperty
	@Column(name = "product_type")
	public ProductTypeEnum getProductType() {
		return this.productType;
	}

	public void setProductType(ProductTypeEnum productType) {
		this.productType = productType;
	}

	@JsonProperty
	@Column(name = "line_type")
	public LineType getLineType() {
		return this.lineType;
	}

	public void setLineType(LineType lineType) {
		this.lineType = lineType;
	}

	@JsonProperty
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "line_category")
	public LineCatagoryEntity getLineCategory() {
		return this.lineCategory;
	}

	public void setLineCategory(LineCatagoryEntity lineCategory) {
		this.lineCategory = lineCategory;
	}

	@JsonProperty
	@Column(name = "days")
	public Integer getDays() {
		return this.days;
	}

	public void setDays(Integer days) {
		this.days = days;
	}

	@JsonProperty
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "area")
	public HyArea getArea() {
		return this.area;
	}

	public void setArea(HyArea area) {
		this.area = area;
	}

	@JsonProperty
	@Column(name = "refund_type")
	public RefundTypeEnum getRefundType() {
		return this.refundType;
	}

	public void setRefundType(RefundTypeEnum refundType) {
		this.refundType = refundType;
	}

	@JsonProperty
	@Column(name = "is_insurance")
	public Boolean getIsInsurance() {
		return this.isInsurance;
	}

	public void setIsInsurance(Boolean isInsurance) {
		this.isInsurance = isInsurance;
	}

	@JsonProperty
	@Column(name = "is_head_insurance")
	public Boolean getIsHeadInsurance() {
		return this.isHeadInsurance;
	}

	public void setIsHeadInsurance(Boolean isHeadInsurance) {
		this.isHeadInsurance = isHeadInsurance;
	}

	@JsonProperty
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "insurance")
	public Insurance getInsurance() {
		return this.insurance;
	}

	public void setInsurance(Insurance insurance) {
		this.insurance = insurance;
	}

	@JsonProperty
	@Column(name = "cancel_memo")
	public String getCancelMemo() {
		return this.cancelMemo;
	}

	public void setCancelMemo(String cancelMemo) {
		this.cancelMemo = cancelMemo;
	}

	@JsonProperty
	@Column(name = "outbound_memo")
	public String getOutboundMemo() {
		return this.outboundMemo;
	}

	public void setOutboundMemo(String outboundMemo) {
		this.outboundMemo = outboundMemo;
	}

	@JsonProperty
	@Column(name = "memo_inner")
	public String getMemoInner() {
		return this.memoInner;
	}

	public void setMemoInner(String memoInner) {
		this.memoInner = memoInner;
	}

	@JsonProperty
	@Column(name = "memo")
	public String getMemo() {
		return this.memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	@JsonProperty
	@Column(name = "introduction")
	public String getIntroduction() {
		return this.introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	@Column(name = "lowest_price", precision = 20, scale = 2)
	public BigDecimal getLowestPrice() {
		return this.lowestPrice;
	}

	public void setLowestPrice(BigDecimal lowestPrice) {
		this.lowestPrice = lowestPrice;
	}

	@Column(name = "is_sale")
	public IsSaleEnum getIsSale() {
		return this.isSale;
	}

	public void setIsSale(IsSaleEnum isSale) {
		this.isSale = isSale;
	}

	@Column(name = "is_top")
	public Boolean getIsTop() {
		return this.isTop;
	}

	public void setIsTop(Boolean isTop) {
		this.isTop = isTop;
	}

	@DateTimeFormat(iso=ISO.DATE_TIME)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "top_edit_time", length = 19)
	public Date getTopEditTime() {
		return this.topEditTime;
	}

	public void setTopEditTime(Date topEditTime) {
		this.topEditTime = topEditTime;
	}

	@Column(name = "line_audit_status")
	public AuditStatus getLineAuditStatus() {
		return this.lineAuditStatus;
	}

	public void setLineAuditStatus(AuditStatus lineAuditStatus) {
		this.lineAuditStatus = lineAuditStatus;
	}

	@Column(name = "group_audit_status")
	public AuditStatus getGroupAuditStatus() {
		return this.groupAuditStatus;
	}

	public void setGroupAuditStatus(AuditStatus groupAuditStatus) {
		this.groupAuditStatus = groupAuditStatus;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "latest_group", length = 19)
	public Date getLatestGroup() {
		return this.latestGroup;
	}

	public void setLatestGroup(Date latestGroup) {
		this.latestGroup = latestGroup;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "company")
	public Department getCompany() {
		return this.company;
	}

	public void setCompany(Department company) {
		this.company = company;
	}
	
	@JsonProperty
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "contract")
	public HySupplierContract getContract() {
		return this.contract;
	}

	public void setContract(HySupplierContract contract) {
		this.contract = contract;
	}

//	@Column(name = "koudian_xianlu")
//	public Long getKoudianXianlu() {
//		return this.koudianXianlu;
//	}
//
//	public void setKoudianXianlu(Long koudianXianlu) {
//		this.koudianXianlu = koudianXianlu;
//	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "operator")
	public HyAdmin getOperator() {
		return this.operator;
	}

	public void setOperator(HyAdmin operator) {
		this.operator = operator;
	}

	@Column(name = "hits")
	public Long getHits() {
		return this.hits;
	}

	public void setHits(Long hits) {
		this.hits = hits;
	}

	@Column(name = "sale_count")
	public Integer getSaleCount() {
		return this.saleCount;
	}

	public void setSaleCount(Integer saleCount) {
		this.saleCount = saleCount;
	}

	@JsonProperty
	@Column(name = "outbound")
	public String getOutbound() {
		return this.outbound;
	}

	public void setOutbound(String outbound) {
		this.outbound = outbound;
	}

	@Column(name = "is_edit")
	public Boolean getIsEdit() {
		return this.isEdit;
	}

	public void setIsEdit(Boolean isEdit) {
		this.isEdit = isEdit;
	}

	@JsonProperty
	@OrderBy("id asc")
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
	public List<HyLineTravels> getLineTravels() {
		return lineTravels;
	}

	public void setLineTravels(List<HyLineTravels> lineTravels) {
		this.lineTravels = lineTravels;
	}

	@JsonProperty
	@OrderBy("id asc")
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
	public List<HyLineRefund> getLineRefunds() {
		return lineRefunds;
	}
	
	public void setLineRefunds(List<HyLineRefund> lineRefunds) {
		this.lineRefunds = lineRefunds;
	}
	@OneToOne(fetch = FetchType.LAZY, mappedBy = "hyLine", cascade = CascadeType.ALL, orphanRemoval = true)
	public MhLine getMhLine() {
		return mhLine;
	}

	public void setMhLine(MhLine mhLine) {
		this.mhLine = mhLine;
	}

	public Boolean getIsCancel() {
		return isCancel;
	}

	public void setIsCancel(Boolean isCancel) {
		this.isCancel = isCancel;
	}
	
	@Column(name = "is_promotion")
	public Boolean getIsPromotion() {
		return isPromotion;
	}

	public void setIsPromotion(Boolean isPromotion) {
		this.isPromotion = isPromotion;
	}

	@JsonProperty
    @Column(name = "line_file")
    public String getLineFile() {
        return lineFile;
    }

    public void setLineFile(String lineFile) {
        this.lineFile = lineFile;
    }





	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "supplier")
	public HySupplier getHySupplier() {
		return hySupplier;
	}

	public void setHySupplier(HySupplier hySupplier) {
		this.hySupplier = hySupplier;
	}

	public Boolean getIsGuanwang() {
		return isGuanwang;
	}

	public void setIsGuanwang(Boolean isGuanwang) {
		this.isGuanwang = isGuanwang;
	}

	public Boolean getIsInner() {
		return isInner;
	}

	public void setIsInner(Boolean isInner) {
		this.isInner = isInner;
	}

	@PrePersist
	public void setPrePersist() {
		this.isPromotion = false;
		if(this.isAutoconfirm==null)
			this.isAutoconfirm = false;
	}

    @JsonProperty
    @Transient
    public Long getCid() {
        return cid;
    }

    public void setCid(Long cid) {
        this.cid = cid;
    }
}
