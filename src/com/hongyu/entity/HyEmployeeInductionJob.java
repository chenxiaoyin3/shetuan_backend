package com.hongyu.entity;

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
@Table(name = "hy_employee_induction_job")
//@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

public class HyEmployeeInductionJob {
	private Long id;
	@JsonIgnore
	private EmployeeInduction employeeInduction;
	private Date startDate;
	private Date endDate;
	private String workingPlace;
	private String jobDuty;
	
	public HyEmployeeInductionJob() {
	}

	public HyEmployeeInductionJob(Long id) {
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
	
	@Column(name="working_place")
	public String getWorkingPlace() {
		return workingPlace;
	}
	public void setWorkingPlace(String workingPlace) {
		this.workingPlace = workingPlace;
	}
	
	@Column(name="job_duty")
	public String getJobDuty() {
		return jobDuty;
	}
	public void setJobDuty(String jobDuty) {
		this.jobDuty = jobDuty;
	}
}
