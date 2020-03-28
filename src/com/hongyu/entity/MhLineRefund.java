package com.hongyu.entity;

import java.io.Serializable;
import java.math.BigDecimal;
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
 * 门户完善线路退款规则
 * @author liyang
 * @version 2019年1月4日 上午11:57:58
 */
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler",
	"mhLine",
	})
@Table(name = "mh_line_refund")
public class MhLineRefund implements Serializable{	
	/*序列化版本号*/
	private static final long serialVersionUID = 1L;
	/*主键*/
	private Long id;
	/*开始天数*/
	private Integer startDay;
	/*开始时间*/
	private Integer startTime;
	/*结束天数*/
	private Integer endDay;
	/*结束时间*/
	private Integer endTime;
	/*退款扣除百分比*/
	private BigDecimal percentage;
	/*对应的线路*/
	private MhLine mhLine;
	/*完善人*/
	private HyAdmin operator;
	/*首次完善时间*/
	private Date createTime;
	/*更新完善时间*/
	private Date updateTime;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Column(name = "start_day")
	public Integer getStartDay() {
		return startDay;
	}
	public void setStartDay(Integer startDay) {
		this.startDay = startDay;
	}
	@Column(name = "start_time")
	public Integer getStartTime() {
		return startTime;
	}
	public void setStartTime(Integer startTime) {
		this.startTime = startTime;
	}
	@Column(name = "end_day")
	public Integer getEndDay() {
		return endDay;
	}
	public void setEndDay(Integer endDay) {
		this.endDay = endDay;
	}
	@Column(name = "end_time")
	public Integer getEndTime() {
		return endTime;
	}
	public void setEndTime(Integer endTime) {
		this.endTime = endTime;
	}
	@Column(name = "percentage")
	public BigDecimal getPercentage() {
		return percentage;
	}
	public void setPercentage(BigDecimal percentage) {
		this.percentage = percentage;
	}
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "mh_line")
	public MhLine getMhLine() {
		return mhLine;
	}
	public void setMhLine(MhLine mhLine) {
		this.mhLine = mhLine;
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
	@PrePersist
	public void setPrepersist() {
		this.setCreateTime(new Date());
	}
	@PreUpdate
	public void setUpdate() {
		this.setUpdateTime(new Date());
	}
	
}
