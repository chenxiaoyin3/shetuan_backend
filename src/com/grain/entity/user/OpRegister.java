package com.grain.entity.user;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * 映射xx_op_register表
 * @author zlw
 */
@Entity
@Table(name = "xx_op_register")
public class OpRegister implements Serializable/*,Comparable<OpRegister>*/ {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7602779435968573165L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "OPname")
	private String opname;

	@Column(name = "URL")
	private String url;

	@Column(name = "Icon")
	private String icon;

	@Column(name = "hasdisplay")
	private Integer hasDisplay;
	// 多对一映射,级联保存更新,多方作为主控方
	@ManyToOne(cascade = { CascadeType.MERGE, CascadeType.PERSIST })
	@JoinColumn(name = "PID")
	private OpRegister parent;

	// 一对多映射,懒加载,级联保存更新，一方作为被控方
	@OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = {
			CascadeType.MERGE, CascadeType.PERSIST })
	private Set<OpRegister> children = new HashSet<OpRegister>();
	
	//本方作为多对多被控方
	@ManyToMany(fetch = FetchType.LAZY,
			mappedBy="ops")
	private Set<Role> roles = new HashSet<Role>();
	
	@Column(name = "description")
	private String note;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOpname() {
		return opname;
	}

	public void setOpname(String opname) {
		this.opname = opname;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public Integer getHasDisplay() {
		return hasDisplay;
	}

	public void setHasDisplay(Integer hasDisplay) {
		this.hasDisplay = hasDisplay;
	}

	public OpRegister getParent() {
		return parent;
	}

	public void setParent(OpRegister parent) {
		this.parent = parent;
	}

	public Set<OpRegister> getChildren() {
		return children;
	}

	public void setChildren(Set<OpRegister> children) {
		this.children = children;
	}
	
	

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

/*	@Override
	public int compareTo(OpRegister o) {
		if (this.getId()>o.getId())
			return 1;
		if (this.getId()<o.getId())
			return -1;
		return 0;
	}*/

}
