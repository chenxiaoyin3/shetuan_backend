package com.hongyu.entitycustom;

import java.math.BigDecimal;

/**
 * UnCollectCustom和 UnPaidCustom的直接父类
 */
public class CollectOrPayBaseCustom {
	public String name;
	public BigDecimal amount;
	public Long id;
	public Integer type;
	public String payCode;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public String getPayCode() {
		return payCode;
	}

	public void setPayCode(String payCode) {
		this.payCode = payCode;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

}
