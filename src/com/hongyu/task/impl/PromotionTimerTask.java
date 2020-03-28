package com.hongyu.task.impl;

import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.hongyu.entity.HyPromotion;
import com.hongyu.entity.HyPromotion.PromotionStatus;
import com.hongyu.service.HyPromotionService;

public class PromotionTimerTask implements TimerTask {
	private Long promotionId;
	private boolean isToStart;
	public static HyPromotionService promotionService;
	public long expireTime;
	static {
		WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
		promotionService = webApplicationContext.getBean(HyPromotionService.class);
	}
	
	public PromotionTimerTask(Long promotionId, boolean isToStart, long expireTime) {
		this.promotionId = promotionId;
		this.isToStart = isToStart;
		this.expireTime = expireTime;
	}

	@Override
	public void execute() {
		if (isToStart) {
			//set promotion to start
			HyPromotion promotion = promotionService.find(promotionId);
			if (promotion != null) {
				if (promotion.getStatus() == PromotionStatus.未开始) {
					promotion.setStatus(PromotionStatus.进行中);
					promotionService.update(promotion);
				}
			}		
		} else {
			//set promotion to end
			HyPromotion promotion = promotionService.find(promotionId);
			if (promotion != null) {
				if (promotion.getStatus() == PromotionStatus.进行中) {
					promotion.setStatus(PromotionStatus.已结束);
					promotionService.update(promotion);
				}
			}
		}
		
	}

	@Override
	public int compareTo(TimerTask o) {
		if (this.getExpireTime() == o.getExpireTime()) {
			return 0;
		} else if (this.getExpireTime() < o.getExpireTime()) {
			return -1;
		} else {
			return 1;
		}
	}

	@Override
	public long getExpireTime() {
		return expireTime;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PromotionTimerTask) {
			return this.promotionId == ((PromotionTimerTask)obj).promotionId;
		} else {
			return false;
		}
		
	}
	
	
	
	
}
