package com.hongyu.entity;
// Generated 2017-12-25 14:12:49 by Hibernate Tools 3.6.0.Final

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * HyRoleAuthority generated by hbm2java
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "hy_announcement")
public class HyAnnouncement implements java.io.Serializable{
	
	public enum StoreRange {
		
		/** 所有门店 **/
		all,
		
		//修改 2019/5/9 直营门店单独出来作为一个范围
		/** 虹宇门店，包括直营门店 **/
		hongyustore,
		
		/** 挂靠门店 **/
		guakaostore,
		
		/** 所有门店不可见 **/
		disable,
		
		/** 直营门店 **/
		zhiyingstore,
	}
	
	public enum SupplierRange {
		
		/** 所有门店 **/
		all,
		
		/** 内部供应商 **/
		inside,
		
		/** 外部供应商 **/
		outside,
		
		/** 所有供应商不可见 **/
		disable,
		
	}

	private Long id;
	
	/** 公告名称 */
	private String name;
	
	/** 内容*/
	private String content;
	
	/** 创建人*/
	private HyAdmin operator;
	
	/** 创建时间*/
	private Date createTime;
	
	/** 修改时间*/
	private Date updateTime;
	
	/** 是否有效*/
	private Integer isValid;
	
	/** 门店可见范围*/
	/** 0所有门店；1虹宇门店（包括直营门店）；2挂靠门店**/
	private StoreRange storeRange;
	
	/** 供应商可见范围*/
	/** 0所有供应商；1内部供应商；2外部供应商**/
	private SupplierRange supplierRange;
	
	/** 总公司可见范围*/
	/** 0不可见 1可见**/
	private Integer range;
	
	
	/** 选择的部门 */
	/** 0所有部门**/
	private Set<Department> departmentRange = new HashSet<Department>(0);
	
	private Integer isAllDepartment;
	
	/** 选择的公司 */
	/** 0所有分公司**/
	private Set<Department> companyRange = new HashSet<Department>(0);
	
	private Integer isAllCompany;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Column(name = "content")
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "operator")
	public HyAdmin getOperator() {
		return operator;
	}

	public void setOperator(HyAdmin operator) {
		this.operator = operator;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_time", length = 19)
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "update_time", length = 19)
	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	@Column(name = "is_valid")
	public Integer getIsValid() {
		return isValid;
	}

	public void setIsValid(Integer isValid) {
		this.isValid = isValid;
	}

	@Column(name = "store_range")
	public StoreRange getStoreRange() {
		return storeRange;
	}

	public void setStoreRange(StoreRange storeRange) {
		this.storeRange = storeRange;
	}

	@Column(name = "supplier_range")
	public SupplierRange getSupplierRange() {
		return supplierRange;
	}

	public void setSupplierRange(SupplierRange supplierRange) {
		this.supplierRange = supplierRange;
	}

	@Column(name = "head_office_range")
	public Integer getRange() {
		return range;
	}

	public void setRange(Integer range) {
		this.range = range;
	}

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "hy_announcement_department")
	public Set<Department> getDepartmentRange() {
		return departmentRange;
	}

	public void setDepartmentRange(Set<Department> departmentRange) {
		this.departmentRange = departmentRange;
	}

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "hy_announcement_company")
	public Set<Department> getCompanyRange() {
		return companyRange;
	}

	public void setCompanyRange(Set<Department> companyRange) {
		this.companyRange = companyRange;
	}

	

	@Column(name = "is_all_department")
	public Integer getIsAllDepartment() {
		return isAllDepartment;
	}

	public void setIsAllDepartment(Integer isAllDepartment) {
		this.isAllDepartment = isAllDepartment;
	}

	@Column(name = "is_all_company")
	public Integer getIsAllCompany() {
		return isAllCompany;
	}

	public void setIsAllCompany(Integer isAllCompany) {
		this.isAllCompany = isAllCompany;
	}

	@Transient
	public Set<String> getDepartmentRangeName() {
		Set<String> names = new HashSet<>();
		if(this.departmentRange.size() > 0) {
			for(Department department : departmentRange) {
				names.add(department.getFullName());
			}
		}
		return names;
	}
	
	@Transient
	public Set<Long> getDepartmentRangeIds() {
		Set<Long> ids = new HashSet<>();
		if(this.departmentRange.size() > 0) {
			for(Department department : departmentRange) {
				ids.add(department.getId());
			}
		}
		return ids;
	}
	
	@Transient
	public Set<String> getCompanyRangeName() {
		Set<String> names = new HashSet<>();
		if(this.companyRange.size() > 0) {
			for(Department department : companyRange) {
				names.add(department.getFullName());
			}
		}
		return names;
	}
	
	@Transient
	public Set<Long> getCompanyRangeIds() {
		Set<Long> ids = new HashSet<>();
		if(this.companyRange.size() > 0) {
			for(Department department : companyRange) {
				ids.add(department.getId());
			}
		}
		return ids;
	}


}