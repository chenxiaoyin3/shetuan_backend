package com.hongyu.entity;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by xyy on 2019/4/18.
 *
 * @author xyy
 *
 * 记录商城用户余额变动(使用或退返)
 */
@Entity
@Table(name = "hy_wechat_account_balance")
public class WechatAccountBalance {
    public enum WechatAccountBalanceType {
        // 0 余额使用
        use,
        // 1 余额退还
        refund
    }

    private Long id;
    /**
     * hy_wechat_account表id
     */
    private Long wechatAccountId;
    private WechatAccountBalanceType type;
    /**
     * 余额使用/退还日期
     */
    private Date createTime;
    /**
     * 余额的使用/退还金额
     */
    private BigDecimal amount;
    /**
     * 本次使用/退还后, 用户账户结余
     */
    private BigDecimal surplus;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", unique = true, nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "wechat_account_id")
    public Long getWechatAccountId() {
        return wechatAccountId;
    }

    public void setWechatAccountId(Long wechatAccountId) {
        this.wechatAccountId = wechatAccountId;
    }

    @Column(name = "type")
    public WechatAccountBalanceType getType() {
        return type;
    }

    public void setType(WechatAccountBalanceType type) {
        this.type = type;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_time", length = 19)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Column(name = "amount")
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Column(name = "surplus")
    public BigDecimal getSurplus() {
        return surplus;
    }

    public void setSurplus(BigDecimal surplus) {
        this.surplus = surplus;
    }
}
