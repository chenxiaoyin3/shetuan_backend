package com.hongyu.entity;
// Generated 2017-12-12 17:05:00 by Hibernate Tools 3.6.0.Final

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * HyTouristInfo generated by hbm2java
 */
@Entity
@Table(name = "hy_tourist_info", catalog = "hongyu")
public class TouristInfo implements java.io.Serializable {

	private Long id;
	private String name;
	private Integer type;
	private Integer gender;
	private Date birthday;
	private String phone;
	private Integer certificateType;
	private String certificate;
	private Date certificateTime;
	private Date passportTime;
	private String country;
	private String surname;
	private String firstname;

	public TouristInfo() {
	}

	public TouristInfo(Long id) {
		this.id = id;
	}
	public TouristInfo(Long id, String name, Integer type, Integer gender,
			Date birthday, String phone, Integer certificateType,
			String certificate, Date certificateTime, Date passportTime,
			String country, String surname, String firstname) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.gender = gender;
		this.birthday = birthday;
		this.phone = phone;
		this.certificateType = certificateType;
		this.certificate = certificate;
		this.certificateTime = certificateTime;
		this.passportTime = passportTime;
		this.country = country;
		this.surname = surname;
		this.firstname = firstname;
	}

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

	@Column(name = "type")
	public Integer getType() {
		return this.type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Column(name = "gender")
	public Integer getGender() {
		return this.gender;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "birthday", length = 10)
	public Date getBirthday() {
		return this.birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	@Column(name = "phone")
	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Column(name = "certificate_type")
	public Integer getCertificateType() {
		return this.certificateType;
	}

	public void setCertificateType(Integer certificateType) {
		this.certificateType = certificateType;
	}

	@Column(name = "certificate")
	public String getCertificate() {
		return this.certificate;
	}

	public void setCertificate(String certificate) {
		this.certificate = certificate;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "certificate_time", length = 10)
	public Date getCertificateTime() {
		return this.certificateTime;
	}

	public void setCertificateTime(Date certificateTime) {
		this.certificateTime = certificateTime;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "passport_time", length = 10)
	public Date getPassportTime() {
		return this.passportTime;
	}

	public void setPassportTime(Date passportTime) {
		this.passportTime = passportTime;
	}

	@Column(name = "country")
	public String getCountry() {
		return this.country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	@Column(name = "surname")
	public String getSurname() {
		return this.surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	@Column(name = "firstname")
	public String getFirstname() {
		return this.firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

}
