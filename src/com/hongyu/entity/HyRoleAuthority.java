package com.hongyu.entity;

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
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.cache.annotation.Cacheable;

/**
 * @author gxz
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "hy_role_authority")
public class HyRoleAuthority implements java.io.Serializable{
	
	public enum CheckedRange {
		
		/** 公司 **/
		company,
		
		/** 分公司 **/
		subcompany,
		
		/** 部门 **/
		department,
		
		/** 个人 **/
		individual,
	}
	
	public enum CheckedOperation {
		
		/** 查看 **/
		view,
		
		/** 编辑**/
		edit,
		
		/** 编辑个人 **/
		editIndividual,
	}
	
	private Long id;
	
	/** 角色 */
	private HyRole roles;
	
	/** 权限 */
	private HyAuthority authoritys;
	
	/** 选择的权限范围 */
	private CheckedRange rangeCheckedNumber;
	
	/** 选择的部门 */
	private Set<Department> departments = new HashSet<Department>(0);
	
	/** 选择的操作范围 */
	private CheckedOperation operationCheckedNumber;
	
	/** 创建日期  **/
	private Date createDate;
	
	/** 修改日期 **/
	private Date modifyDate;
	
	@JsonIgnore
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "authoritys")
    //@Cacheable("hyRoleAuthority")
	public HyAuthority getAuthoritys() {
		return authoritys;
	}

	public void setAuthoritys(HyAuthority authoritys) {
		this.authoritys = authoritys;
	}

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "roles")
	public HyRole getRoles() {
		return roles;
	}

	public void setRoles(HyRole roles) {
		this.roles = roles;
	}

	@JsonIgnore
	@Column(name = "range_checked_number")
    //@Cacheable("hyRoleAuthority")
	public CheckedRange getRangeCheckedNumber() {
		return rangeCheckedNumber;
	}

	public void setRangeCheckedNumber(CheckedRange rangeCheckedNumber) {
		this.rangeCheckedNumber = rangeCheckedNumber;
	}
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "hy_roleauthority_department")
    //@Cacheable("hyRoleAuthority")
	public Set<Department> getDepartments() {
		return departments;
	}

	public void setDepartments(Set<Department> departments) {
		this.departments = departments;
	}

	@Column(name = "operation_checked_number")
    //@Cacheable("hyRoleAuthority")
	public CheckedOperation getOperationCheckedNumber() {
		return operationCheckedNumber;
	}

	public void setOperationCheckedNumber(CheckedOperation operationCheckedNumber) {
		this.operationCheckedNumber = operationCheckedNumber;
	}
	
	@Transient
	public Set<String> getRangeCheckedListName() {
		Set<String> names = new HashSet<>();
		if(this.departments.size() > 0) {
			for(Department department : departments) {
				names.add(department.getFullName());
			}
		}
		return names;
	}
	
	@Transient
	public Set<Long> getDepartmentIds() {
		Set<Long> ids = new HashSet<>();
		if(this.departments.size() > 0) {
			for(Department department : departments) {
				ids.add(department.getId());
			}
		}
		return ids;
	}
	
	
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}

	@PrePersist
	public void prePersist() {
		this.setCreateDate(new Date());
		this.setModifyDate(new Date());
	}

	/**
	 * 更新前处理
	 * 
	 * @param entity
	 *            基类
	 */
	@PreUpdate
	public void preUpdate() {
		this.setModifyDate(new Date());
	}


}
