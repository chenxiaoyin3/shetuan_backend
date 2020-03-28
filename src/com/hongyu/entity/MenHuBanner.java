package com.hongyu.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.grain.entity.OrderEntity;

@SuppressWarnings("serial")
@Entity
@Table(name="mh_banner")
public class MenHuBanner extends OrderEntity {

	public static enum BannerType {
		首页,
		国内游,
		出境游,
		周边游,
		签证,
		景点,
		酒店,
		酒加景,
		特产
	}
	private String title;
	private String img;
	private String link;
	private BannerType type;
	private Long targetId;
	private Boolean state;
	private Date startTime;
	private Date endTime;
	private BigDecimal pvPrice;
	private BigDecimal uvPrice;
	private HyAdmin creator;
	//是普通优惠还是组合优惠
	private Integer isCheck;

	@JsonProperty
	@Column(name = "title")
	public String getTitle() {
		return this.title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	@JsonProperty
	public Boolean getState() {
		return state;
	}

	public void setState(Boolean state) {
		this.state = state;
	}

	@JsonProperty
	@Column(name = "img", nullable = false)
	public String getImg() {
		return this.img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	@JsonProperty
	@Column(name = "type", nullable = false)
	public BannerType getType() {
		return this.type;
	}

	public void setType(BannerType type) {
		this.type = type;
	}

	@JsonProperty
	@Column(name = "target_id", nullable = false)
	public Long getTargetId() {
		return this.targetId;
	}

	public void setTargetId(Long targetId) {
		this.targetId = targetId;
	}

	@JsonProperty
	@Column(name = "link")
	public String getLink() {
		return this.link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	@JsonProperty
	@Column(name = "pv_price", precision = 10)
	public BigDecimal getPvPrice() {
		return this.pvPrice;
	}

	public void setPvPrice(BigDecimal pvPrice) {
		this.pvPrice = pvPrice;
	}

	@JsonProperty
	@Column(name = "uv_price", precision = 10)
	public BigDecimal getUvPrice() {
		return this.uvPrice;
	}

	public void setUvPrice(BigDecimal uvPrice) {
		this.uvPrice = uvPrice;
	}
	
	@JsonProperty
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "start_time", length = 19)
	@DateTimeFormat(iso=ISO.DATE_TIME)
	public Date getStartTime() {
		return this.startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	@JsonProperty
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "end_time", length = 19)
	@DateTimeFormat(iso=ISO.DATE_TIME)
	public Date getEndTime() {
		return this.endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	
	@PrePersist
	public void prePersist() {
		this.state = true;
		this.pvPrice = new BigDecimal(0);
		this.uvPrice = new BigDecimal(0);
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "creator")
	public HyAdmin getCreator() {
		return creator;
	}

	public void setCreator(HyAdmin creator) {
		this.creator = creator;
	}
	@JsonProperty
	public Integer getIsCheck() {
		return isCheck;
	}

	public void setIsCheck(Integer isCheck) {
		this.isCheck = isCheck;
	}


}
