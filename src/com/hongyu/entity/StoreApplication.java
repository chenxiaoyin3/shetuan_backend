package com.hongyu.entity;
// Generated 2017-12-12 17:05:00 by Hibernate Tools 3.6.0.Final

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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

/**
 * HyStoreApplication generated by hbm2java
 */
@Entity
@Table(name = "hy_store_application")
public class StoreApplication implements java.io.Serializable {

    private Long id;
    private Integer applicationStatus;//-1驳回，0初始状态,1连锁经理审核,3强制激活
    //	private Long departmentId;
//	private Long storeId;
    private Store store;
    private Integer type;//0注册申请   1门店交押金     2门店申请续签         3交管理费      4退出申请
    private BigDecimal money;//金额
    /**退出增加押金金额和余额*/

    private BigDecimal pledge;//押金
    private BigDecimal balance;//余额

    private Integer payment;//付款方式     0 线上   1转账   2 刷卡  3 现金
    private String payerName;//付款人
    private String payerBank;//付款人银行
    private String payerAccount;//付款人账户 62
    private String payerBankAccount;//付款人联行号

    private Boolean payerBankType;// 付款人对公是false 对私是true

    private Date payDay;//付款日期
    private String payeeName;//收款人
    private String payeeBank;//收款人银行
    private String payeeAccount;//收款人账户
    private String payeeBankAccount;//收款人联行号
    private Boolean payeeBankType;//收款人对公是false 对私是true

    private String accessory;//附件
    //	private String paymentCertificate;//付款凭证
    private String uniqueCode;//统一信用代码
    private String businessLicense;//营业执照
    private String businessCertificate;//经营许可证

    private String contract;//合同号
    private Date validDate;//有效日期
    private BigDecimal managementFee;//管理费

    private String comment;//审核意见
    private HyAdmin operator;//添加人
    private Date createtime;//创建时间
    private String processInstanceId;
    
    private Integer preStatus;  //申请前的状态


	public static final Integer bohui=-1;//驳回
    public static final Integer init=0;//初始化状态
    public static final Integer managerCheck=1;//连锁经理审核，待副总审核
    public static final Integer vicePresident=2;//副总审核，待最后一步
    public static final Integer complete=3;//完成
    public static final Integer forceActive=4;//强制激活
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "status")
    public Integer getApplicationStatus() {
        return this.applicationStatus;
    }

    public void setApplicationStatus(Integer applicationStatus) {
        this.applicationStatus = applicationStatus;
    }

	/*@Column(name = "company_id")
	public Long getDepartmentId() {
		return this.departmentId;
	}

	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
	}*/

    //	@Column(name = "store_id")
//	public Long getStoreId() {
//		return this.storeId;
//	}
//
//	public void setStoreId(Long storeId) {
//		this.storeId = storeId;
//	}
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="store_id")
    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    @Column(name = "type")
    public Integer getType() {
        return this.type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Column(name = "money", precision = 10)
    public BigDecimal getMoney() {
        return this.money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    @Column(name = "pledge")
    public BigDecimal getPledge() {
        return pledge;
    }

    public void setPledge(BigDecimal pledge) {
        this.pledge = pledge;
    }

    @Column(name = "balance")
    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Column(name = "payment")
    public Integer getPayment() {
        return this.payment;
    }

    public void setPayment(Integer payment) {
        this.payment = payment;
    }

    @Column(name="payer_name")
    public String getPayerName() {
        return payerName;
    }

    public void setPayerName(String payerName) {
        this.payerName = payerName;
    }
    @Column(name="payer_bank")
    public String getPayerBank() {
        return payerBank;
    }

    public void setPayerBank(String payerBank) {
        this.payerBank = payerBank;
    }
    @Column(name="payer_account")
    public String getPayerAccount() {
        return payerAccount;
    }

    public void setPayerAccount(String payerAccount) {
        this.payerAccount = payerAccount;
    }
    @Column(name="payer_bank_account")
    public String getPayerBankAccount() {
        return payerBankAccount;
    }

    public void setPayerBankAccount(String payerBankAccount) {
        this.payerBankAccount = payerBankAccount;
    }
    @Column(name="payee_name")
    public String getPayeeName() {
        return payeeName;
    }

    public void setPayeeName(String payeeName) {
        this.payeeName = payeeName;
    }
    @Column(name="payee_bank")
    public String getPayeeBank() {
        return payeeBank;
    }

    public void setPayeeBank(String payeeBank) {
        this.payeeBank = payeeBank;
    }
    @Column(name="payee_account")
    public String getPayeeAccount() {
        return payeeAccount;
    }

    public void setPayeeAccount(String payeeAccount) {
        this.payeeAccount = payeeAccount;
    }
    @Column(name="payee_bank_account")
    public String getPayeeBankAccount() {
        return payeeBankAccount;
    }

    public void setPayeeBankAccount(String payeeBankAccount) {
        this.payeeBankAccount = payeeBankAccount;
    }

    @Column(name = "payer_bank_type")
    public Boolean getPayerBankType() {
        return payerBankType;
    }

    public void setPayerBankType(Boolean payerBankType) {
        this.payerBankType = payerBankType;
    }

    @Column(name = "payee_bank_type")
    public Boolean getPayeeBankType() {
        return payeeBankType;
    }

    public void setPayeeBankType(Boolean payeeBankType) {
        this.payeeBankType = payeeBankType;
    }

    @Column(name = "accessory")
    public String getAccessory() {
        return this.accessory;
    }

    public void setAccessory(String accessory) {
        this.accessory = accessory;
    }
//	@Column(name="payment_certificate")
//	public String getPaymentCertificate() {
//		return paymentCertificate;
//	}
//
//	public void setPaymentCertificate(String paymentCertificate) {
//		this.paymentCertificate = paymentCertificate;
//	}

    @Column(name = "unique_code")
    public String getUniqueCode() {
        return this.uniqueCode;
    }

    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }

    @Column(name = "business_license")
    public String getBusinessLicense() {
        return this.businessLicense;
    }

    public void setBusinessLicense(String businessLicense) {
        this.businessLicense = businessLicense;
    }

    @Column(name = "business_certificate")
    public String getBusinessCertificate() {
        return this.businessCertificate;
    }

    public void setBusinessCertificate(String businessCertificate) {
        this.businessCertificate = businessCertificate;
    }

    @Column(name = "contract")
    public String getContract() {
        return this.contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "valid_date", length = 10)
    public Date getValidDate() {
        return this.validDate;
    }

    public void setValidDate(Date validDate) {
        this.validDate = validDate;
    }

    @Column(name = "management_fee", precision = 10)
    public BigDecimal getManagementFee() {
        return this.managementFee;
    }

    public void setManagementFee(BigDecimal managementFee) {
        this.managementFee = managementFee;
    }
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "operator")
    public HyAdmin getOperator() {
        return this.operator;
    }

    public void setOperator(HyAdmin operator) {
        this.operator = operator;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "createtime", length = 19)
    public Date getCreatetime() {
        return this.createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }
    @Column(name="process_instance_id")
    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }
    @Column(name="comment")
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
    @Column(name="pay_day")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(iso=ISO.DATE_TIME)
    public Date getPayDay() {
        return payDay;
    }

    public void setPayDay(Date payDay) {
        this.payDay = payDay;
    }
    @Column(name = "pre_status")
    public Integer getPreStatus() {
		return preStatus;
	}

	public void setPreStatus(Integer preStatus) {
		this.preStatus = preStatus;
	}
    @PrePersist
    public void prePersist(){
        this.setCreatetime(new Date());
        this.setApplicationStatus(init);
    }

}
