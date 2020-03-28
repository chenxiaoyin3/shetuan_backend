package com.hongyu.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="hy_review_form_entry_score")
public class ReviewFormEntryScore {
	private String level;
	private Integer score;
	@Id
	@Column(name="level",nullable=false,unique=true)
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	@Column(name="score")
	public Integer getScore() {
		return score;
	}
	public void setScore(Integer score) {
		this.score = score;
	}

}
