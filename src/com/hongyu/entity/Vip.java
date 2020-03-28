package com.hongyu.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@SuppressWarnings("serial")
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler",
						})
@Table(name = "hy_vip")
public class Vip implements Serializable {
    private Long id;
    private WechatAccount wechatAccount;
    private Long viplevelId;
    private Date birthday;
    private Date createTime;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="wechat_account")
	public WechatAccount getWechatAccount() {
		return wechatAccount;
	}
	public void setWechatAccount(WechatAccount wechatAccount) {
		this.wechatAccount = wechatAccount;
	}
	
	@Column(name="viplevel_id")
	public Long getViplevelId() {
		return viplevelId;
	}
	public void setViplevelId(Long viplevelId) {
		this.viplevelId = viplevelId;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="birthday")
	@DateTimeFormat(pattern="yyyy-MM-dd")
	public Date getBirthday() {
		return birthday;
	}
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="create_time")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}
