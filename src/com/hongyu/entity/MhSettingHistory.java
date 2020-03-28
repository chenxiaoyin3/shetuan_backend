package com.hongyu.entity;

import static org.junit.Assert.assertTrue;

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
@Table(name="hy_mh_setting_history")
public class MhSettingHistory implements Serializable {
	private Long id;
	private Long settingid;
	private String settingName;
	private String settingValue;
	private String operator;
	private Date createTime;
	private Date endTime;

	
	public MhSettingHistory() {}
	
	public MhSettingHistory(MhSetting old_setting) {
		this.settingid = old_setting.getId();
		this.settingName = old_setting.getSettingName();
		this.settingValue = old_setting.getSettingValue();
		this.operator = old_setting.getOperator();
		this.createTime = new Date();

	}
	
	@Id
	@Column(name="ID", unique=true, nullable=false)
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name="setting_name")
	public String getSettingName() {
		return settingName;
	}

	public void setSettingName(String settingName) {
		this.settingName = settingName;
	}
	
	@Column(name="setting_value")
	public String getSettingValue() {
		return settingValue;
	}

	public void setSettingValue(String settingValue) {
		this.settingValue = settingValue;
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
	@Column(name="end_time", length=19)
	public Date getEndTime() {
		return endTime;
	}
	
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	
	@Column(name="setting_id")
	public Long getSettingid() {
		return settingid;
	}

	public void setSettingid(Long settingid) {
		this.settingid = settingid;
	}

	@PrePersist
	public void setPrepersist() {
		this.endTime = new Date();
	}


}
