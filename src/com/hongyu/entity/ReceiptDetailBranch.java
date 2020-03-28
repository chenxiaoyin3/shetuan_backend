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
 * 分公司    收款-详情
 */
@Entity
@Table(name = "hy_receipt_details_branch")
public class ReceiptDetailBranch implements java.io.Serializable {

	private Long id;
	/**1:StoreManageFee 门店管理费 2:BranchProfitShare 分公司分成 3.StoreDepositBranch (挂靠)门店押金*/
	private Integer receiptType;
	private Long receiptId;
	private BigDecimal amount;
	private Long payMethod;
	private String accountName;
	private String shroffAccount;
	private String bankName;
	private Date date;
	private String receiver;
	private String remark;


	@Id
	@GeneratedValue(strategy = IDENTITY)

	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	

	@Column(name = "receipt_type")
	public Integer getReceiptType() {
		return this.receiptType;
	}

	public void setReceiptType(Integer receiptType) {
		this.receiptType = receiptType;
	}

	@Column(name = "receipt_id")
	public Long getReceiptId() {
		return this.receiptId;
	}

	public void setReceiptId(Long receiptId) {
		this.receiptId = receiptId;
	}

	@Column(name = "amount")
	public BigDecimal getAmount() {
		return this.amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	@Column(name = "pay_method")
	public Long getPayMethod() {
		return this.payMethod;
	}

	public void setPayMethod(Long payMethod) {
		this.payMethod = payMethod;
	}

	@Column(name = "account_name")
	public String getAccountName() {
		return this.accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	@Column(name = "shroff_account")
	public String getShroffAccount() {
		return this.shroffAccount;
	}

	public void setShroffAccount(String shroffAccount) {
		this.shroffAccount = shroffAccount;
	}

	@Column(name = "bank_name")
	public String getBankName() {
		return this.bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "date")
	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Column(name = "receiver")
	public String getReceiver() {
		return this.receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	@Column(name = "remark")
	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}
