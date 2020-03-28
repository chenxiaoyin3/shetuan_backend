package com.hongyu.service;

import java.util.HashMap;

import javax.servlet.http.HttpSession;

import com.grain.service.BaseService;
import com.hongyu.Json;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.InsuranceOrder;
import com.sun.javafx.collections.MappingChange.Map;

public interface InsuranceOrderService extends BaseService<InsuranceOrder, Long>{
	public void generate(Long orderId) throws Exception;
	public void generate(HyOrder hyOrder,HttpSession session)throws Exception;
	public void generateStoreInsuranceOrder(HyOrder hyOrder, HttpSession session) throws Exception;
	public Json postOrderToJT(Long[] orderIds) throws Exception;
	/**
	 * 按照商品订单hyOrderId来取消该订单下的所有保单。
	 * @param orderIds
	 * @return
	 * @throws Exception
	 */
	public Json cancelOrder(Long[] orderIds) throws Exception;
	/**
	 * 按照保险单取消。
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Json cancelInsuranceOrder(Long id) throws Exception;
	/**
	 * 因为之前保险价格计算有误，需要重新更新一下旧数据
	 * @throws Exception
	 */
	public void updateOldDataMoney() throws Exception;
}
