	package com.hongyu.service.impl;	import javax.annotation.Resource;	import org.springframework.stereotype.Service; import com.grain.service.impl.BaseServiceImpl;	import com.hongyu.dao.PaymentElementItemDao;	import com.hongyu.entity.PaymentElementItem;	import com.hongyu.service.PaymentElementItemService; 	@Service("paymentElementItemServiceImpl")	public class PaymentElementItemServiceImpl extends BaseServiceImpl<PaymentElementItem,Long> implements PaymentElementItemService{		@Resource(name = "paymentElementItemDaoImpl") PaymentElementItemDao dao;		@Resource(name = "paymentElementItemDaoImpl") public void setBaseDao(PaymentElementItemDao dao){super.setBaseDao(dao);	}}