package com.hongyu.entity;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * RefundInfo 退款信息
 * @author xyy
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "hy_refund_info")
public class RefundInfo implements java.io.Serializable {

	private Long id;
	/** 0 未付  1已付*/
	private Integer state;
    /**
     * 1:游客退团（门店退团） 2:供应商消团 3:供应商驳回订单 4:门店驳回订单
     * 5:电子门票退款-官网/微商 6:签证退款-官网/微商 7:酒店/酒加景退款-官网/微商
     * 8:电子门票退款-门店 9:签证退款-门店 10:酒店/酒加景退款-门店
     * 11:保险弃撤-门店   12、租借导游退款  13、门店售后退款
     * 14、保险退款  15认购门票退款-门店
     */
    private Integer type;
	private Date applyDate;
	private String appliName;
	private BigDecimal amount;
	private String remark;
	private String payer;
	private Date payDate;
	private BigDecimal koudian;
	private Boolean ifTongji;
	private Long orderId;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "state")
    public Integer getState() {
        return this.state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    @Column(name = "type")
    public Integer getType() {
        return this.type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "apply_date")
    public Date getApplyDate() {
        return this.applyDate;
    }

    public void setApplyDate(Date applyDate) {
        this.applyDate = applyDate;
    }

    @Column(name = "appli_name")
    public String getAppliName() {
        return this.appliName;
    }

    public void setAppliName(String appliName) {
        this.appliName = appliName;
    }

    @Column(name = "amount")
    public BigDecimal getAmount() {
        return this.amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Column(name = "remark")
    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Column(name = "payer")
    public String getPayer() {
        return payer;
    }

    public void setPayer(String payer) {
        this.payer = payer;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "pay_date")
    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

	@Column(name = "koudian")
	public BigDecimal getKoudian() {
		return koudian;
	}

	public void setKoudian(BigDecimal koudian) {
		this.koudian = koudian;
	}

	@Column(name = "if_tongji")
	public Boolean getIfTongji() {
		return ifTongji;
	}

	public void setIfTongji(Boolean ifTongji) {
		this.ifTongji = ifTongji;
	}

	@Column(name = "order_id")
	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	
	
	

}
