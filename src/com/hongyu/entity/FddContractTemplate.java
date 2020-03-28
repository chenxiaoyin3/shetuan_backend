package com.hongyu.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;
@Entity
@Table(name = "hy_fddcontract_template")
public class FddContractTemplate implements Serializable {
	private Long id;
	//模板编号
	private String templateId;
	//模板类型  0--国内一日游  1--国内游  2--境外游 5--定义为作废的版本
	private Integer type;
	
	private Date createTime;
	private Date modifyTime;
	//合同模板创建人
	private String operator;
	//合同模板存储路径
	private String path;
	//合同模板名称
	private String templateName;
	//模板备注信息
	private	String remark;
	//模板签署关键字
	private String signKeyWord;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Column(name = "template_id")
	public String getTemplateId() {
		return templateId;
	}
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_date", length = 19)
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "modify_date", length = 19)
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	public Date getModifyTime() {
		return modifyTime;
	}
	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}
	@Column(name = "operator")
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	@Column(name = "path")
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	@Column(name = "template_name")
	public String getTemplateName() {
		return templateName;
	}
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	@Column(name = "remark")
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	@Column(name = "type")
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	@Column(name = "sign_keyword")
	public String getSignKeyWord() {
		return signKeyWord;
	}
	public void setSignKeyWord(String signKeyWord) {
		this.signKeyWord = signKeyWord;
	}
	@PrePersist
	public void prePersist(){
		this.setCreateTime(new Date());
	}
	@PreUpdate
	public void preUpdate(){
		this.setModifyTime(new Date());
	}
	
}
