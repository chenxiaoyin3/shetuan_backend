package com.hongyu.wrapper.lbc;

import java.util.List;

import com.hongyu.entity.GuideReviewForm;
import com.hongyu.entity.GuideReviewFormScore;

@SuppressWarnings("serial")
public class GuideReviewFormWrapper implements java.io.Serializable{
	private Long guideAssignment_id;
	private Long guideReviewForm_id;
	private String advice;
	private String phone;
	private List<GuideReviewFormScore> guideReviewFormScores;
	
	
	public Long getGuideAssignment_id() {
		return guideAssignment_id;
	}
	public void setGuideAssignment_id(Long guideAssignment_id) {
		this.guideAssignment_id = guideAssignment_id;
	}
	
	public List<GuideReviewFormScore> getGuideReviewFormScores() {
		return guideReviewFormScores;
	}
	public void setGuideReviewFormScores(List<GuideReviewFormScore> guideReviewFormScores) {
		this.guideReviewFormScores = guideReviewFormScores;
	}
	public String getAdvice() {
		return advice;
	}
	public void setAdvice(String advice) {
		this.advice = advice;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public Long getGuideReviewForm_id() {
		return guideReviewForm_id;
	}
	public void setGuideReviewForm_id(Long guideReviewForm_id) {
		this.guideReviewForm_id = guideReviewForm_id;
	}
	
	
	
	
}
