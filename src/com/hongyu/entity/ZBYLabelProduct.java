package com.hongyu.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 周边游标签和线路产品对应的关系表
 * @author liyang
 * @version 2019年1月14日 下午4:57:28
 */
@Entity
@Table(name = "hy_zby_label_product")
public class ZBYLabelProduct implements Serializable{
	/*自动生成版本号*/
	private static final long serialVersionUID = 1L;
	/*主键*/
	private Long id;
	/*对应标签主键id*/
	private Long labelId;
	/*对应线路产品主键id*/
	private Long lineId;
	/*排序对比值*/
	private Integer sort;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id", unique=true, nullable=false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Column(name = "label_id")
	public Long getLabelId() {
		return labelId;
	}
	public void setLabelId(Long labelId) {
		this.labelId = labelId;
	}
	@Column(name = "line_id")
	public Long getLineId() {
		return lineId;
	}
	public void setLineId(Long lineId) {
		this.lineId = lineId;
	}
	@Column(name = "sort")
	public Integer getSort() {
		return sort;
	}
	public void setSort(Integer sort) {
		this.sort = sort;
	}
	
}
