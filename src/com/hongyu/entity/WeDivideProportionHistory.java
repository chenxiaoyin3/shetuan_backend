package com.hongyu.entity;

import java.io.Serializable;
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
@Table(name="hy_we_divide_proportion_history")
public class WeDivideProportionHistory implements Serializable {
	private Long id;
	private Long proportionId;
	private String proportionType;
	private Float proportion;
	private Date createTime;
	private Date endTime;
	private Boolean isValid;
	private String operator;
	
	public WeDivideProportionHistory() {
	}
//	
//	public WeDivideProportionHistory(WeDivideProportion proportion) {
//		this.proportionId = proportion.getId();
//		this.proportionType = proportion.getProportionType();
//		this.proportion = proportion.getProportion();
//		this.createTime = proportion.getCreateTime();
//		this.isValid = proportion.getIsValid();
//		this.setOperator(proportion.getOperator());
//	}
//	
	@Id
	  @GeneratedValue(strategy=GenerationType.AUTO)
	  @Column(name="id", unique=true, nullable=false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name="proportion_id")
	public Long getProportionId() {
		return proportionId;
	}
	public void setProportionId(Long proportionId) {
		this.proportionId = proportionId;
	}
	
	@Column(name="type")
	public String getProportionType() {
		return proportionType;
	}
	public void setProportionType(String proportionType) {
		this.proportionType = proportionType;
	}
	
	@Column(name="proportion", precision=12, scale=0)
	public Float getProportion() {
		return proportion;
	}
	public void setProportion(Float proportion) {
		this.proportion = proportion;
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
	
	@PrePersist
	void setPrepersist() {
		this.endTime = new Date();
	}
	
	@Column(name="operator")
	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}
}
