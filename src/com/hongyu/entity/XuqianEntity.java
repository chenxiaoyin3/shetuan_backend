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
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hongyu.util.Constants.AuditStatus;
/**
 * 供应商续签合同Entity
 * @author guoxinze
 *
 */
@SuppressWarnings("serial")
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler",
	"contractId",
})
@Table(name="hy_supplier_contract_xuqian")
public class XuqianEntity implements Serializable {
	
	 public enum Xuqianleixing {
		 /** 直接续签 */
		 zhijie,
		 
		 /** 变更续签 */
		 biangeng,
	 }
	 
	 private Long id;
	 
	 private HyRole roleId;
	 
	 private Xuqianleixing xqlx;
	 
     /** 提交申请时间 */
     private Date applyTime;
     
     /** 审核状态 */
     private AuditStatus auditStatus;
     
     /** 流程实例ID */
     private String processInstanceId;
     
     private String applyName;
     
     private HySupplierContract contractId;
     
     private Date endDate;
     
     private String xinyongdaima;
     
     private String yingyezhizhao;
     
     private String jingyingxukezheng;
     
     private String xukezhengzhaopian;
     
     private String fuzeren;
     
     private String dianhua;
     
     private String qqhao;
     
     private String weixin;
     
     private String weixinerweima;

    @Id
 	@GeneratedValue(strategy = GenerationType.IDENTITY)
 	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Xuqianleixing getXqlx() {
		return xqlx;
	}

	public void setXqlx(Xuqianleixing xqlx) {
		this.xqlx = xqlx;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "apply_time", length = 19)
	public Date getApplyTime() {
		return applyTime;
	}

	public void setApplyTime(Date applyTime) {
		this.applyTime = applyTime;
	}

	public AuditStatus getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(AuditStatus auditStatus) {
		this.auditStatus = auditStatus;
	}

	public String getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	public String getApplyName() {
		return applyName;
	}

	public void setApplyName(String applyName) {
		this.applyName = applyName;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "contract_id")
	public HySupplierContract getContractId() {
		return contractId;
	}

	public void setContractId(HySupplierContract contractId) {
		this.contractId = contractId;
	}

	@DateTimeFormat(iso=ISO.DATE_TIME)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "end_date", length = 19)
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getXinyongdaima() {
		return xinyongdaima;
	}

	public void setXinyongdaima(String xinyongdaima) {
		this.xinyongdaima = xinyongdaima;
	}

	public String getYingyezhizhao() {
		return yingyezhizhao;
	}

	public void setYingyezhizhao(String yingyezhizhao) {
		this.yingyezhizhao = yingyezhizhao;
	}

	public String getJingyingxukezheng() {
		return jingyingxukezheng;
	}

	public void setJingyingxukezheng(String jingyingxukezheng) {
		this.jingyingxukezheng = jingyingxukezheng;
	}

	public String getXukezhengzhaopian() {
		return xukezhengzhaopian;
	}

	public void setXukezhengzhaopian(String xukezhengzhaopian) {
		this.xukezhengzhaopian = xukezhengzhaopian;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "role_id")
	public HyRole getRoleId() {
		return roleId;
	}

	public void setRoleId(HyRole roleId) {
		this.roleId = roleId;
	}

	public String getFuzeren() {
		return fuzeren;
	}

	public void setFuzeren(String fuzeren) {
		this.fuzeren = fuzeren;
	}

	public String getDianhua() {
		return dianhua;
	}

	public void setDianhua(String dianhua) {
		this.dianhua = dianhua;
	}

	public String getQqhao() {
		return qqhao;
	}

	public void setQqhao(String qqhao) {
		this.qqhao = qqhao;
	}

	public String getWeixin() {
		return weixin;
	}

	public void setWeixin(String weixin) {
		this.weixin = weixin;
	}

	public String getWeixinerweima() {
		return weixinerweima;
	}

	public void setWeixinerweima(String weixinerweima) {
		this.weixinerweima = weixinerweima;
	}  
     
}
