package com.hongyu.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("serial")
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler",
						})
@Table(name = "hy_store_fhynew")
public class HyStoreFhynew implements Serializable {
    private Long id;
    private String name;
    private Integer type; //1-授信,2-现付
    private HyArea area;
    private String address;
    private String xinyongdaima;
    private String xydmUrl;
    private HyAdmin person; //负责人
    private Boolean isCancel; //true-正常,false-取消
    private BigDecimal creditMoney; //授信额度
    private BigDecimal money; //余额
    private Date createTime;
    private Integer auditStatus; //1-提交，审核中,2-审核通过,3-驳回
    private HyAdmin applyName; //申请人/创建人
    private Date applyTime;
    private String processInstanceId;
    
    /** 授信历史记录*/
    private Set<HyCreditFhy> hyCreditFhys=new HashSet<>();
    
 
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name="name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Column(name="type")
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="area")
	public HyArea getArea() {
		return area;
	}
	public void setArea(HyArea area) {
		this.area = area;
	}
	
	@Column(name="address")
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	@Column(name="xinyongdaima")
	public String getXinyongdaima() {
		return xinyongdaima;
	}
	public void setXinyongdaima(String xinyongdaima) {
		this.xinyongdaima = xinyongdaima;
	}
	
	@Column(name="xydm_url")
	public String getXydmUrl() {
		return xydmUrl;
	}
	public void setXydmUrl(String xydmUrl) {
		this.xydmUrl = xydmUrl;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="person")
	public HyAdmin getPerson() {
		return person;
	}
	public void setPerson(HyAdmin person) {
		this.person = person;
	}
	
	@Column(name="is_cancel")
	public Boolean getIsCancel() {
		return isCancel;
	}
	public void setIsCancel(Boolean isCancel) {
		this.isCancel = isCancel;
	}
	
	@Column(name="credit_money")
	public BigDecimal getCreditMoney() {
		return creditMoney;
	}
	public void setCreditMoney(BigDecimal creditMoney) {
		this.creditMoney = creditMoney;
	}
	
	@Column(name="money")
	public BigDecimal getMoney() {
		return money;
	}
	public void setMoney(BigDecimal money) {
		this.money = money;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_time", length = 19)
	@DateTimeFormat(pattern="yyyy-MM-dd")
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	@Column(name="audit_status")
	public Integer getAuditStatus() {
		return auditStatus;
	}
	public void setAuditStatus(Integer auditStatus) {
		this.auditStatus = auditStatus;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="apply_name")
	public HyAdmin getApplyName() {
		return applyName;
	}
	public void setApplyName(HyAdmin applyName) {
		this.applyName = applyName;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "apply_time", length = 19)
	@DateTimeFormat(iso=ISO.DATE_TIME)
	public Date getApplyTime() {
		return applyTime;
	}
	public void setApplyTime(Date applyTime) {
		this.applyTime = applyTime;
	}
	
	@Column(name="process_instance_id")
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	
	@JsonProperty
	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "hyStoreFhynew")
	public Set<HyCreditFhy> getHyCreditFhys() {
		return hyCreditFhys;
	}
	public void setHyCreditFhys(Set<HyCreditFhy> hyCreditFhys) {
		this.hyCreditFhys = hyCreditFhys;
	}
}
