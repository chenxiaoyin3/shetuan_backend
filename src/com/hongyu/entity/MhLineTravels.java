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
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 门户完善的线路行程
 * @author liyang
 * @version 2019年1月4日 上午11:34:26
 */
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler",
	"mhLine",
})
@Table(name = "mh_line_travels")
public class MhLineTravels implements Serializable{
	/*序列化版本号*/
	private static final long serialVersionUID = 1L;
	/*主键id*/
	private Long id;
	/*交通方式*/
	private TransportEntity transport;
	/*线路行程描述*/
	private String route;
	/*酒店*/
	private String restaurant;
	/*是否含早餐*/
	private Boolean isBreakfast;
	/*是否含午餐*/
	private Boolean isLunch;
	/*是否含晚餐*/
	private Boolean isDinner;
	/*对应的线路*/
	private MhLine mhLine;
	/*完善人*/
	private HyAdmin operator;
	/*首次完善时间*/
	private Date createTime;
	/*更新完善时间*/
	private Date updateTime;
	/*是否住宿*/
	private Integer ifAccommodation;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id",unique = true, nullable = false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "transport")
	public TransportEntity getTransport() {
		return transport;
	}
	public void setTransport(TransportEntity transport) {
		this.transport = transport;
	}
	@Column(name = "route")
	public String getRoute() {
		return route;
	}
	public void setRoute(String route) {
		this.route = route;
	}
	@Column(name = "restaurant")
	public String getRestaurant() {
		return restaurant;
	}
	public void setRestaurant(String restaurant) {
		this.restaurant = restaurant;
	}
	@Column(name = "is_breakfast")
	public Boolean getIsBreakfast() {
		return isBreakfast;
	}
	public void setIsBreakfast(Boolean isBreakfast) {
		this.isBreakfast = isBreakfast;
	}
	@Column(name = "is_lunch")
	public Boolean getIsLunch() {
		return isLunch;
	}
	public void setIsLunch(Boolean isLunch) {
		this.isLunch = isLunch;
	}
	@Column(name = "is_dinner")
	public Boolean getIsDinner() {
		return isDinner;
	}
	public void setIsDinner(Boolean isDinner) {
		this.isDinner = isDinner;
	}
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "mh_line")
	public MhLine getMhLine() {
		return mhLine;
	}
	public void setMhLine(MhLine line) {
		this.mhLine = line;
	}
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "operator")
	public HyAdmin getOperator() {
		return operator;
	}
	public void setOperator(HyAdmin operator) {
		this.operator = operator;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_time",length=19)
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "update_time",length=19)
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	@Column(name = "if_accommodation")
	public Integer getIfAccommodation() {
		return ifAccommodation;
	}
	public void setIfAccommodation(Integer ifAccommodation) {
		this.ifAccommodation = ifAccommodation;
	}
	@PrePersist
	public void setPrepersist() {
		this.setCreateTime(new Date());
	}
	@PreUpdate
	public void setUpdate() {
		this.setUpdateTime(new Date());
	}
}
