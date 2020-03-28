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
@Table(name="hy_specialty_category_history")
public class SpecialtyCategoryModifyHistory implements Serializable {
	private Long id;
	private Long categoryid;
	private String categoryName;
	private String pCategoryName;
	private String operator;
	private Date createTime;
	private Date deadTime;
	
	public SpecialtyCategoryModifyHistory() {}
	
	/**
	 * 改成一调用modify接口，就更新SpecialtyCategory的createTime，并和history的deadTime保持一致
	 * @param category
	 * @param historyDeadTime 这条SpecialtyCategoryModifyHistory的deadTime，对应SpecialtyCategory的createTime
	 */
	public SpecialtyCategoryModifyHistory(SpecialtyCategory category, Date historyDeadTime) {
		this.categoryid = category.getId();
		this.categoryName = category.getName();
		if (category.getParent() != null) {
			this.pCategoryName = category.getParent().getName();
		}
		this.operator =category.getOperator();
		this.createTime = category.getCreateTime();
		this.deadTime = historyDeadTime;
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
	
	@Column(name="category_id")
	public Long getCategoryid() {
		return categoryid;
	}

	public void setCategoryid(Long categoryid) {
		this.categoryid = categoryid;
	}
	
	@Column(name="category_name")
	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	
	@Column(name="pcategory_name")
	public String getpCategoryName() {
		return pCategoryName;
	}

	public void setpCategoryName(String pCategoryName) {
		this.pCategoryName = pCategoryName;
	}
	
	@Column(name="operator")
	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
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
	@Column(name="dead_time", length=19)
	public Date getDeadTime() {
		return deadTime;
	}

	public void setDeadTime(Date deadTime) {
		this.deadTime = deadTime;
	}
		
}
