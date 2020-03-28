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


@SuppressWarnings("serial")
@Entity
@Table(name = "hy_specialty_label")
public class HySpecialtyLabel implements Serializable{

	private Long ID;//ID 也一定是Long
	private HyLabel hyLabel;//label_id
	private Specialty specialty;//specialty_id
	private String operator;//operator
	private Date createTime;//create_time
	private Boolean isMarked;//is_marked
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID", unique = true, nullable = false)
	public Long getID() {
		return ID;
	}
	public void setID(Long iD) {
		ID = iD;
	}
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "label_id")
	public HyLabel getHyLabel() {
		return hyLabel;
	}
	public void setHyLabel(HyLabel hyLabel) {
		this.hyLabel = hyLabel;
	}
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "specialty_id")
	public Specialty getSpecialty() {
		return specialty;
	}
	public void setSpecialty(Specialty specialty) {
		this.specialty = specialty;
	}
	
	@Column(name = "operator")
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	
	@Column(name = "create_time")
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	@Column(name = "is_marked")
	public Boolean getIsMarked() {
		return isMarked;
	}
	public void setIsMarked(Boolean isMarked) {
		this.isMarked = isMarked;
	}
	
}
