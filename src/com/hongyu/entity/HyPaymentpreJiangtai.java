package com.hongyu.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="hy_paymentpre_jiangtai")
public class HyPaymentpreJiangtai {
	
	private Long id;
	private Date createDate;//申请时间
	private Date modifyDate;//修改时间
//	private BankList bankList;
	private String accountName;//账户名称
	private String bankAccount;//银行账号
	private String bankName;//开户行
	private BigDecimal amount;//金额
//	private Date applicationTime;
	private HyAdmin operator;//操作人
	private Integer applicationStatus;
	private String remark;//备注
	private String processInstanceId;
	public static final Integer cancle=-1;//驳回，重新修改后提交
	public static final Integer daiqueren=0;//初始状态，待经理审核
	public static final Integer managerCheck=1;//经理已审核，待副总审核
	public static final Integer vicePresidentCheck=2;//副总已审核，待财务审核
	public static final Integer complete=3;//财务已审核，完成
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_date", length = 19)
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "modify_date", length = 19)
	public Date getModifyDate() {
		return modifyDate;
	}
	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}
	@Column(name="account_name")
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	@Column(name="bank_account")
	public String getBankAccount() {
		return bankAccount;
	}
	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}
	@Column(name="bank_name")
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	//	@ManyToOne(fetch=FetchType.LAZY)
//	@JoinColumn(name="bank_id")
//	public BankList getBankList() {
//		return bankList;
//	}
//	public void setBankList(BankList bankList) {
//		this.bankList = bankList;
//	}
	@Column(name="amount")
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
//	@Temporal(TemporalType.TIMESTAMP)
//	@Column(name="application_time")
//	public Date getApplicationTime() {
//		return applicationTime;
//	}
//	public void setApplicationTime(Date applicationTime) {
//		this.applicationTime = applicationTime;
//	}
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="operator")
	public HyAdmin getOperator() {
		return operator;
	}
	public void setOperator(HyAdmin operator) {
		this.operator = operator;
	}
	@Column(name="application_status")
	public Integer getApplicationStatus() {
		return applicationStatus;
	}
	public void setApplicationStatus(Integer applicationStatus) {
		this.applicationStatus = applicationStatus;
	}
	@Column(name="remark")
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	@Column(name="process_instance_id")
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	@PrePersist
	public void prePersist(){
		this.setCreateDate(new Date());
		this.setApplicationStatus(daiqueren);
	}
	@PreUpdate
	public void preUpdate(){
		this.setModifyDate(new Date());
	}
	

}
