package com.hongyu.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.cache.annotation.Cacheable;

/**
 * @author gxz
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "hy_authority")
public class HyAuthority implements java.io.Serializable {
	
	/**
	 * 操作权限范围
	 * @author guoxinze
	 *
	 */
	public enum OpRange {
		
		/** 无 */
		none,
		
		/** 公司 */
		one,
		
		/** 公司，分公司 */
		two,
		
		/** 公司，分公司 ，部门 */
		three,
		
		/** 公司，分公司，部门，个人 */
		all,
	}
	
	/**
	 * 编辑范围
	 * @author guoxinze
	 *
	 */
	public enum EditRange {
		
		/** 无 */
		none,
		
		/** 查看 */
		one,
		
		/** 查看，编辑 */
		two,
		
		/** 查看，编辑，编辑个人 */
		three,
		
		/** 编辑 */
		edit,
		
		/** 编辑个人 */
		editSelf,
		
		/** 编辑，编辑个人 */
		editAll,
	}
	
	/** 树路径分隔符 */
	public static final String TREE_PATH_SEPARATOR = ",";
	
	private Long id;
	
	/** 父权限 */
	private HyAuthority hyAuthority;

	/** 子权限 */
	private Set<HyAuthority> hyAuthorities = new HashSet<HyAuthority>(0);

	/** 角色权限 */
	private Set<HyRoleAuthority> hyRoleAuthorities = new HashSet<HyRoleAuthority>(0);
	
	/** 权限名称 */
	private String name;
	
	/** 前端url */
	private String url;
	
	/** 前端url全称 */
	private String fullUrl;
	
	/** 请求url全称 */
	private String requestUrl;
	
	/** 权限图标 */
	private String icon;
	
	/** 操作范围 */
	private OpRange range;
	
	/** 编辑范围 */
	private EditRange operation;
	
	/** 是否在导航栏显示 */
	private Boolean isDisplay;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}


	@Column(name = "name")
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@JsonProperty("path")
	@Column(name = "url")
	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Column(name = "icon", length = 60)
	public String getIcon() {
		return this.icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	
	@Column(name = "range")
	public OpRange getRange() {
		return range;
	}

	public void setRange(OpRange range) {
		this.range = range;
	}
	
	@Column(name = "operation")
	public EditRange getOperation() {
		return operation;
	}

	public void setOperation(EditRange operation) {
		this.operation = operation;
	}
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pID")
	public HyAuthority getHyAuthority() {
		return this.hyAuthority;
	}

	public void setHyAuthority(HyAuthority hyAuthority) {
		this.hyAuthority = hyAuthority;
	}
	
	@JsonProperty("children")
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "hyAuthority")
	@OrderBy("id")
	public Set<HyAuthority> getHyAuthorities() {
		return this.hyAuthorities;
	}

	public void setHyAuthorities(Set<HyAuthority> hyAuthorities) {
		this.hyAuthorities = hyAuthorities;
	}

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "authoritys")
	public Set<HyRoleAuthority> getHyRoleAuthorities() {
		return this.hyRoleAuthorities;
	}

	public void setHyRoleAuthorities(Set<HyRoleAuthority> hyRoleAuthorities) {
		this.hyRoleAuthorities = hyRoleAuthorities;
	}

	@Column(name = "is_display")
	public Boolean getIsDisplay() {
		return isDisplay;
	}

	public void setIsDisplay(Boolean isDisplay) {
		this.isDisplay = isDisplay;
	}
	
	@Column(name = "full_url")
	public String getFullUrl() {
		return fullUrl;
	}

	public void setFullUrl(String fullUrl) {
		this.fullUrl = fullUrl;
	}

	@Column(name = "request_url")
	//@Cacheable("hyAuthority")
	public String getRequestUrl() {
		return requestUrl;
	}

	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

}
