package com.hongyu.entity;

import java.math.BigDecimal;
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
 * 分公司充值
 */
@Entity
@Table(name = "hy_branch_recharge")
public class BranchRecharge implements java.io.Serializable{
	private Long id;
	private String accountAlias;   //总公司账户别名
	private String bankName;      //总公司开户行
	private String bankCode;      //总公司银行联行号
	private int bankType;        //总公司账户类型       1对公
	private String bankAccount;     //总公司银行账号
	private String branchAccountAlias;    //分公司账户别名
	private String branchBankName;       //分公司开户行
	private String branchBankCode;       //分公司银行联行号 
	private int branchBankType;         //分公司账户类型     1对公 0对私
	private String branchAccount;       //分公司银行账号
	private Date createDate;            //申请日期
	private BigDecimal amount;          //支付金额
	private String remark;            //备注
	private Integer status;           //状态   1已提交 待审核2已审核
	private String processInstanceId;//流程实例id
	private String username;   //申请人
	/** 申请人所在部门的部门id*/
	private Long departmentId;
	/** 分公司的hy_department表的id*/
	private Long branchId;
//	private int step;     //步骤，1：分公司提交充值申请 2：总公司审核
//	private String audioOpinion;//审核意见
	
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name = "account_alias")
	public String getAccountAlias() {
		return accountAlias;
	}
	
	public void setAccountAlias(String accountAlias) {
		this.accountAlias = accountAlias;
	}
	
	@Column(name = "bank_name")
	public String getBankName() {
		return bankName;
	}
	
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	
	@Column(name = "bank_code")
	public String getBankCode() {
		return bankCode;
	}
	
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}
	
	@Column(name = "bank_type")
	public int getBankType() {
		return bankType;
	}
	
	public void setBankType(int bankType) {
		this.bankType = bankType;
	}
	
	@Column(name = "bank_account")
	public String getBankAccount() {
		return bankAccount;
	}
	
	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}
	
	@Column(name = "branch_account_alias")
	public String getBranchAccountAlias() {
		return branchAccountAlias;
	}
	
	public void setBranchAccountAlias(String branchAccountAlias) {
		this.branchAccountAlias = branchAccountAlias;
	}
	
	@Column(name = "branch_bank_name")
	public String getBranchBankName() {
		return branchBankName;
	}
	
	public void setBranchBankName(String branchBankName) {
		this.branchBankName = branchBankName;
	}
	
	@Column(name = "branch_bank_code")
	public String getBranchBankCode() {
		return branchBankCode;
	}
	
	public void setBranchBankCode(String branchBankCode) {
		this.branchBankCode = branchBankCode;
	}
	
	@Column(name = "branch_bank_type")
	public int getBranchBankType() {
		return branchBankType;
	}
	
	public void setBranchBankType(int branchBankType) {
		this.branchBankType = branchBankType;
	}
	
	@Column(name = "branch_account")
	public String getBranchAccount() {
		return branchAccount;
	}
	
	public void setBranchAccount(String branchAccount) {
		this.branchAccount = branchAccount;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_date")
	public Date getCreateDate() {
		return createDate;
	}
	
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
	@Column(name = "amount")
	public BigDecimal getAmount() {
		return amount;
	}
	
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
	@Column(name = "remark")
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Column(name = "status")
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Column(name = "process_instance_id")
	public String getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	@Column(name = "user_name")
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Column(name = "department_id")
	public Long getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
	}

	@Column(name = "branch_id")
	public Long getBranchId() {
		return branchId;
	}

	public void setBranchId(Long branchId) {
		this.branchId = branchId;
	}
}
