package com.hongyu.entity;

import java.math.BigDecimal;
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
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="hy_employee_induction")
public class EmployeeInduction implements java.io.Serializable{
	private Long id;//主键
	private Date createtime;//创建时间
	private Date modifytime;//修改时间
	private String name;//姓名
	private Integer sex;//性别 0男 1女
	private Date birthday;//生日
	private String nativePlace;//籍贯
	private String nationality;//民族
	private Integer height;//身高
	private Integer weight;//体重
	private Boolean isMarriage;//婚否 0未婚 1已婚
	private Integer politicalStatus;//政治面貌 1中共党员 2中共预备党员 3共青团员 4民革党员 5民盟盟员 6民建会员 7民进会员 8农工党党员 9致公党党员 10九三学社社员 11台盟盟员 12无党派人士 13群众 
	private Integer health;//健康状态 1良好 2较好 3不合格
	private Integer workingLife;//从业年头
	private Department department;//部门
	private String photo;//个人照片
	private Integer educationBackground;//学历 1小学 2初中 3高中 4本科 5硕士 6博士
	private String profession;//专业
	private String graduateSchool;//毕业院校
	private Boolean isOnBusiness;//是否出差 0不出差 1出差
	private Boolean isOverTime;//能否加班 0不加班 1加班
	private Boolean isWorkTransfer;//是否调剂 0不调剂 1调剂
	private String computerLevel;//计算机水平
	private String foreignLevel;//外语水平
	private String otherSkills;//其它技能
	private String hobbies;//爱好
	private String identificationCardId;//身份证号码
	private String identificationCardAddress;//身份证地址
	private String phone;//手机号码
	private String email;//邮箱
	private String emergentContacts;//紧急联系人姓名
	private String emergentContactsPhone;//紧急联系人电话
	private String probationPeriodTime;//试用期时间
	private String probationPeriodSalary;//试用期薪水
	private String formalSalary;//正式薪水
	private List<HyEmployeeInductionEducation> hyEmployeeInductionEducation;//教育经历
	private List<HyEmployeeInductionJob> hyEmployeeInductionJob;//工作经历
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="id",unique=true,nullable=false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_time", length = 19)
	public Date getCreatetime() {
		return this.createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "modify_time", length = 19)
	public Date getModifytime() {
		return this.modifytime;
	}

	public void setModifytime(Date modifytime) {
		this.modifytime = modifytime;
	}
	
	@Column(name = "name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Column(name = "sex")
	public Integer getSex() {
		return sex;
	}
	public void setSex(Integer sex) {
		this.sex = sex;
	}
	
	@Temporal(TemporalType.DATE)
	@Column(name = "birthday", length = 10)
	public Date getBirthday() {
		return birthday;
	}
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
	
	@Column(name = "native_place")
	public String getNativePlace() {
		return nativePlace;
	}
	public void setNativePlace(String nativePlace) {
		this.nativePlace = nativePlace;
	}
	
	@Column(name = "nationality")
	public String getNationality() {
		return nationality;
	}
	public void setNationality(String nationality) {
		this.nationality = nationality;
	}
	
	@Column(name = "height")
	public Integer getHeight() {
		return height;
	}
	public void setHeight(Integer height) {
		this.height = height;
	}
	
	@Column(name = "weight")
	public Integer getWeight() {
		return weight;
	}
	public void setWeight(Integer weight) {
		this.weight = weight;
	}
	
	@Column(name = "is_marriage")
	public Boolean getIsMarriage() {
		return isMarriage;
	}
	public void setIsMarriage(Boolean isMarriage) {
		this.isMarriage = isMarriage;
	}
	
	@Column(name = "political_status")
	public Integer getPoliticalStatus() {
		return politicalStatus;
	}
	public void setPoliticalStatus(Integer politicalStatus) {
		this.politicalStatus = politicalStatus;
	}
	
	@Column(name = "health")
	public Integer getHealth() {
		return health;
	}
	public void setHealth(Integer health) {
		this.health = health;
	}
	
	@Column(name = "working_life")
	public Integer getWorkingLife() {
		return workingLife;
	}
	public void setWorkingLife(Integer workingLife) {
		this.workingLife = workingLife;
	}
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "department_id")
	public Department getDepartment() {
		return department;
	}
	public void setDepartment(Department department) {
		this.department = department;
	}
	
	@Column(name = "photo")
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	
	@Column(name = "education_background")
	public Integer getEducationBackground() {
		return educationBackground;
	}
	public void setEducationBackground(Integer educationBackground) {
		this.educationBackground = educationBackground;
	}
	
	@Column(name = "profession")
	public String getProfession() {
		return profession;
	}
	public void setProfession(String profession) {
		this.profession = profession;
	}
	
	@Column(name = "graduated_school")
	public String getGraduateSchool() {
		return graduateSchool;
	}
	public void setGraduateSchool(String graduateSchool) {
		this.graduateSchool = graduateSchool;
	}
	
	@Column(name = "is_on_business")
	public Boolean getIsOnBusiness() {
		return isOnBusiness;
	}
	public void setIsOnBusiness(Boolean isOnBusiness) {
		this.isOnBusiness = isOnBusiness;
	}
	
	@Column(name = "is_over_time")
	public Boolean getIsOverTime() {
		return isOverTime;
	}
	public void setIsOverTime(Boolean isOverTime) {
		this.isOverTime = isOverTime;
	}

	@Column(name = "is_work_transfer")
	public Boolean getIsWorkTransfer() {
		return isWorkTransfer;
	}
	public void setIsWorkTransfer(Boolean isWorkTransfer) {
		this.isWorkTransfer = isWorkTransfer;
	}
	
	@Column(name = "computer_level")
	public String getComputerLevel() {
		return computerLevel;
	}
	public void setComputerLevel(String computerLevel) {
		this.computerLevel = computerLevel;
	}
	
	@Column(name = "foreign_level")
	public String getForeignLevel() {
		return foreignLevel;
	}
	public void setForeignLevel(String foreignLevel) {
		this.foreignLevel = foreignLevel;
	}
	
	@Column(name = "other_skills")
	public String getOtherSkills() {
		return otherSkills;
	}
	public void setOtherSkills(String otherSkills) {
		this.otherSkills = otherSkills;
	}
	
	@Column(name = "hobbies")
	public String getHobbies() {
		return hobbies;
	}
	public void setHobbies(String hobbies) {
		this.hobbies = hobbies;
	}
	
	@Column(name = "identification_card_id")
	public String getIdentificationCardId() {
		return identificationCardId;
	}
	public void setIdentificationCardId(String identificationCardId) {
		this.identificationCardId = identificationCardId;
	}
	
	@Column(name = "identification_card_address")
	public String getIdentificationCardAddress() {
		return identificationCardAddress;
	}
	public void setIdentificationCardAddress(String identificationCardAddress) {
		this.identificationCardAddress = identificationCardAddress;
	}
	
	@Column(name = "phone")
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	@Column(name = "email")
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	@Column(name = "emergent_contacts")
	public String getEmergentContacts() {
		return emergentContacts;
	}
	public void setEmergentContacts(String emergentContacts) {
		this.emergentContacts = emergentContacts;
	}
	
	@Column(name = "emergent_contacts_phone")
	public String getEmergentContactsPhone() {
		return emergentContactsPhone;
	}
	public void setEmergentContactsPhone(String emergentContactsPhone) {
		this.emergentContactsPhone = emergentContactsPhone;
	}
	
	@Column(name = "probation_period_time")
	public String getProbationPeriodTime() {
		return probationPeriodTime;
	}
	public void setProbationPeriodTime(String probationPeriodTime) {
		this.probationPeriodTime = probationPeriodTime;
	}
	
	@Column(name = "probation_period_salary")
	public String getProbationPeriodSalary() {
		return probationPeriodSalary;
	}
	public void setProbationPeriodSalary(String probationPeriodSalary) {
		this.probationPeriodSalary = probationPeriodSalary;
	}
	
	@Column(name = "formal_salary")
	public String getFormalSalary() {
		return formalSalary;
	}
	public void setFormalSalary(String formalSalary) {
		this.formalSalary = formalSalary;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "employeeInduction", cascade = CascadeType.ALL, orphanRemoval = true)
	public List<HyEmployeeInductionEducation> getHyEmployeeInductionEducation() {
		return hyEmployeeInductionEducation;
	}
	public void setHyEmployeeInductionEducation(List<HyEmployeeInductionEducation> hyEmployeeInductionEducation) {
		this.hyEmployeeInductionEducation = hyEmployeeInductionEducation;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "employeeInduction", cascade = CascadeType.ALL, orphanRemoval = true)
	public List<HyEmployeeInductionJob> getHyEmployeeInductionJob() {
		return hyEmployeeInductionJob;
	}
	public void setHyEmployeeInductionJob(List<HyEmployeeInductionJob> hyEmployeeInductionJob) {
		this.hyEmployeeInductionJob = hyEmployeeInductionJob;
	}
	
	@PrePersist
	public void prePersist(){
		this.setCreatetime(new Date());
	}
	@PreUpdate
	public void preUpdate(){
		this.setModifytime(new Date());
	}
}
