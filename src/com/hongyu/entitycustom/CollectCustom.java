package com.hongyu.entitycustom;

/**
 * 已收款列表 返回给前台为list格式 继承自 UnCollectCustom,增加收款人
 */
public class CollectCustom extends UnCollectCustom {
	private String recervicer;
	private String orderCoder;

	public String getOrderCoder() {
		return orderCoder;
	}

	public void setOrderCoder(String orderCoder) {
		this.orderCoder = orderCoder;
	}

	public String getRecervicer() {
		return recervicer;
	}

	public void setRecervicer(String recervicer) {
		this.recervicer = recervicer;
	}

}
