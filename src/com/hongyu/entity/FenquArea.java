package com.hongyu.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "fenqu_area")
public class FenquArea {
	
	private Long id;
	private Long fenquLabelId;
	private Long areaId;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "fenqu_label_id")
	public Long getFenquLabelId() {
		return fenquLabelId;
	}

	public void setFenquLabelId(Long fenquLabelId) {
		this.fenquLabelId = fenquLabelId;
	}

	@Column(name = "area_id")
	public Long getAreaId() {
		return areaId;
	}

	public void setAreaId(Long areaId) {
		this.areaId = areaId;
	}
	

}
