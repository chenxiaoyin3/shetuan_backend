package com.hongyu.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="hy_we_divide_model_history")
public class WeDivideModelHistory implements Serializable {
	private Long id;
	private Long modelId;
	private String modelType;
	private BigDecimal store;
	private BigDecimal weBusiness;
	private BigDecimal introducer;
	private Date createTime;
	private Date endTime;
	private String operator;
	private Boolean isValid;
	
	public WeDivideModelHistory() {}

	
	public WeDivideModelHistory(Long id) {
		this.id = id;
	}
	
	/**
	 * 改成一调用modify接口，就更新WeDivideModel的createTime，并和history的endTime保持一致
	 * @param model
	 * @param historyEndTime 这条WeDivideModelHistory的endTime，对应WeDivideModel的createTime
	 */
	public WeDivideModelHistory(WeDivideModel model, Date historyEndTime) {
		this.modelId = model.getId();
		this.modelType = model.getModelType();
		this.store = model.getStore();
		this.weBusiness = model.getWeBusiness();
		this.introducer = model.getIntroducer();
		this.createTime = model.getCreateTime();
		this.isValid = model.getIsValid();
		this.operator = model.getOperator();
		this.endTime = historyEndTime;
	}
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID", unique=true, nullable=false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name="model_id")
	public Long getModelId() {
		return modelId;
	}
	public void setModelId(Long modelId) {
		this.modelId = modelId;
	}
	
	@Column(name="type")
	public String getModelType() {
		return modelType;
	}
	public void setModelType(String modelType) {
		this.modelType = modelType;
	}
	
	@Column(name="store", precision=10, scale=2)
	public BigDecimal getStore() {
		return store;
	}
	public void setStore(BigDecimal store) {
		this.store = store;
	}
	
	@Column(name="we_business", precision=10, scale=2)
	public BigDecimal getWeBusiness() {
		return weBusiness;
	}
	public void setWeBusiness(BigDecimal weBusiness) {
		this.weBusiness = weBusiness;
	}
	
	@Column(name="introducer", precision=10, scale=2)
	public BigDecimal getIntroducer() {
		return introducer;
	}
	public void setIntroducer(BigDecimal introducer) {
		this.introducer = introducer;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="create_time", length=19)
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="end_time", length=19)
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	
	@Column(name="is_valid")
	public Boolean getIsValid() {
		return isValid;
	}
	public void setIsValid(Boolean isValid) {
		this.isValid = isValid;
	}
	
	@Column(name="operator")
	public String getOperator() {
		return operator;
	}


	public void setOperator(String operator) {
		this.operator = operator;
	}


	@PrePersist
	void setPrepersist() {
		this.isValid = false;
	}
}
