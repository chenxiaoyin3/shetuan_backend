package com.hongyu.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
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
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 景点产品标签（用于官网）
 * @author liyang
 * @version 2018年12月28日 上午11:26:34
 */
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler","specialtys"})
@Entity
@Table(name = "hy_jd_label")
public class JDLabel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2863689448335966054L;
	
	/*主键*/
	private Long id;
	/*标签名称*/
	private String name;
	/*标签全名称--如果是二级标签的话就包含一级的名称*/
	private String fullName;
	/*父标签*/
	private JDLabel parent;
	/*排序权重*/
	private Long orders;
	/*是否显示（如果是父标签-取决于isActive的状态，如果是子标签-取决于父标签的isActive状态）*/
	private Boolean ishow;
	/*创建人*/
	private String operator;
	/*创建时间*/
	private Date createTime;
	/*死亡时间*/
	private Date deadTime;
	/*是否有效*/
	private Boolean isActive;
	/*图标地址*/
	private String iconUrl;
	/*子标签集合*/
	private List<JDLabel> childJDLabels;
	  
	public JDLabel() {}
	  
	public JDLabel(Long id){
	    this.id = id;
	}
	  
	public JDLabel(Long id, String name, JDLabel parent, 
			  Long orders, Boolean ishow, String operator,
			  Date createTime, Boolean isActive, String iconUrl){
	    this.id = id;
	    this.name = name;
	    this.parent = parent;
	    this.orders = orders;
	    this.ishow = ishow;
	    this.operator = operator;
	    this.createTime = createTime;
	    this.isActive = isActive;
	    this.iconUrl = iconUrl;
	}
	  
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID", unique=true, nullable=false)
	public Long getId(){
		return this.id;
	}
  
	public void setId(Long id) {
		this.id = id;
	}
  
	@Column(name="name")
	public String getName(){
		return this.name;
	}
  
	public void setName(String name){
		this.name = name;
	}
	@Column(name = "full_name")
	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="pid")
	public JDLabel getParent(){
		return this.parent;
	}
  
	public void setParent(JDLabel parent){
		this.parent = parent;
	}
  
	@Column(name="orders")
	public Long getOrders(){
		return this.orders;
	}
  
	public void setOrders(Long orders){
		this.orders = orders;
	}
  
	@Column(name="ishow")
	public Boolean getIshow(){
		return this.ishow;
	}
  
	public void setIshow(Boolean ishow){
		this.ishow = ishow;
	}
  
  
	@Column(name="operator")
	public String getOperator(){
		return this.operator;
	}
  
	public void setOperator(String operator){
		this.operator = operator;
	}
  
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="create_time", length=19)
	public Date getCreateTime(){
		return this.createTime;
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

	@Column(name="is_active")
	public Boolean getIsActive(){
		return this.isActive;
	}
  
	public void setIsActive(Boolean isActive){
		this.isActive = isActive;
	}
  
	@Column(name="icon_url")
	public String getIconUrl(){
		return this.iconUrl;
	}
  
	public void setIconUrl(String iconUrl){
		this.iconUrl = iconUrl;
	}
  
	@JsonIgnore
	@OneToMany(mappedBy="parent", fetch=FetchType.LAZY, cascade={CascadeType.REMOVE})
	@OrderBy("id asc")
	public List<JDLabel> getChildJDLabels(){
		return this.childJDLabels;
	}
  
	public void setChildJDLabels(List<JDLabel> childJDLabels){
		this.childJDLabels = childJDLabels;
	}
  
	@PrePersist
	public void setPrepersist() {
		this.setCreateTime(new Date());
	}
}
