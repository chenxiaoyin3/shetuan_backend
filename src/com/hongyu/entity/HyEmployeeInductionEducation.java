package com.hongyu.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

//@SuppressWarnings("serial") 
@Entity
@Table(name = "hy_employee_induction_education")
//@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class HyEmployeeInductionEducation implements Serializable {

	private Long id;
	@JsonIgnore
	private EmployeeInduction employeeInduction;
	private Date startDate;
	private Date endDate;
	private String schoolName;
	private String profession;
	
	public HyEmployeeInductionEducation() {
	}

	public HyEmployeeInductionEducation(Long id) {
		this.id = id;
	}
	
	@Id  
	@GeneratedValue(strategy = GenerationType.AUTO)   //主键生成规则
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@JsonIgnore
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.REFRESH,optional = false)
	@JoinColumn(name="employee_id")
	public EmployeeInduction getEmployeeInduction() {
		return employeeInduction;
	}
	public void setEmployeeInduction(EmployeeInduction employeeInduction) {
		this.employeeInduction = employeeInduction;
	}
	

	@Temporal(TemporalType.DATE)
	@Column(name="start_date", length = 19)
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	

	@Temporal(TemporalType.DATE)
	@Column(name="end_date", length = 19)
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	@Column(name="school_name")
	public String getSchoolName() {
		return schoolName;
	}
	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}
	
	@Column(name="profession")
	public String getProfession() {
		return profession;
	}
	public void setProfession(String profession) {
		this.profession = profession;
	}

	
}
