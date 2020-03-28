package com.hongyu.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @author xyy
 *
 * 余额电子券使用记录(专指将余额电子券的金额转为余额)
 */
@Entity
@Table(name = "hy_coupon_balance_use")
public class CouponBalanceUse implements java.io.Serializable {

    private Long id;
    /**
     * 1 线路赠送  2 销售奖励  3 商城销售  4 大客户购买  表的id
     */
    private Long couponId;
    private String orderCode;
    private Long wechatId;
    private Float useAmount;
    /**
     * 绑定时间
     */
    private Date useTime;
    private Integer state;
    /**
     * 绑定的手机号
     */
    private String phone;
    private String couponCode;
    /**
     * 1 线路赠送  2 销售奖励  3 商城销售  4 大客户购买, 5积分兑换, 6首单奖励
     */
    private Integer type;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", unique = true, nullable = false)
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "coupon_id")
    public Long getCouponId() {
        return couponId;
    }

    public void setCouponId(Long couponId) {
        this.couponId = couponId;
    }

    @Column(name = "order_code")
    public String getOrderCode() {
        return this.orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    @Column(name = "wechat_id")
    public Long getWechatId() {
        return this.wechatId;
    }

    public void setWechatId(Long wechatId) {
        this.wechatId = wechatId;
    }

    @Column(name = "use_amount")
    public Float getUseAmount() {
        return this.useAmount;
    }

    public void setUseAmount(Float useAmount) {
        this.useAmount = useAmount;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "use_time")
    public Date getUseTime() {
        return this.useTime;
    }

    public void setUseTime(Date useTime) {
        this.useTime = useTime;
    }

    @Column(name = "state")
    public Integer getState() {
        return this.state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    @Column(name = "phone")
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Column(name = "coupon_code")
    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    @Column(name = "type")
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

}
