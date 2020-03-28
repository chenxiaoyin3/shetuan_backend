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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "hy_pre_pay_supply")
public class PrePaySupply {
	private Long id;
	private int type;// 供应商类型
	private HySupplierElement supplierElement;// 供应商名称
	private Department departmentId;// 申请人部门id
	private BankList bankAccount;// 银行账号
	private BigDecimal money;// 预付款金额
	private Date createTime;// 申请时间
	private Date payTime;// 出纳支付时间
	private HyAdmin operator;// 申请人
	private int state;// 状态 0审核中-未付 1 已通过-未付 2已通过-已付 3已驳回-未付
	private String memo;// 备注
	private String processInstanceId;// 流程实例id
	private int step;// 流程步骤，是分公司审核还是副总超额审核等 0：充值，待经理审核 1：公司经理审核 2：副总超额审核，待财务审核 3：总公司财务审核完成
	                 //  2019/3/1改      4待部门经理审核 0：待经理超额审核 1：待副总超额审核 2：待财务审核 3：总公司财务审核完成 

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "type")
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Column(name = "money")
	public BigDecimal getMoney() {
		return money;
	}

	public void setMoney(BigDecimal money) {
		this.money = money;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_time")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "pay_time")
	public Date getPayTime() {
		return payTime;
	}

	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}

	@Column(name = "state")
	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	@Column(name = "memo")
	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	@Column(name = "process_instance_id")
	public String getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	@Column(name = "step")
	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "supplier_element_id")
	public HySupplierElement getSupplierElement() {
		return supplierElement;
	}

	public void setSupplierElement(HySupplierElement supplierElement) {
		this.supplierElement = supplierElement;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "department_id")
	public Department getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Department departmentId) {
		this.departmentId = departmentId;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "bank_account")
	public BankList getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(BankList bankAccount) {
		this.bankAccount = bankAccount;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "operator")
	public HyAdmin getOperator() {
		return operator;
	}

	public void setOperator(HyAdmin operator) {
		this.operator = operator;
	}
}
