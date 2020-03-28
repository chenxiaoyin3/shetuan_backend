package com.hongyu.service.impl;

import javax.annotation.Resource;

import com.hongyu.util.DESUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.PayablesLineItemDao;
import com.hongyu.entity.PayablesLineItem;
import com.hongyu.service.PayablesLineItemService;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@Service("payablesLineItemServiceImpl")
public class PayablesLineItemServiceImpl extends BaseServiceImpl<PayablesLineItem, Long>
		implements PayablesLineItemService {
	@Resource(name = "payablesLineItemDaoImpl")
	PayablesLineItemDao dao;

	@Resource(name = "payablesLineItemDaoImpl")
	public void setBaseDao(PayablesLineItemDao dao) {
		super.setBaseDao(dao);
	}

	@Override
	public List<HashMap<String, Object>> getSupplierReconciliationDetail(String str) throws Exception {
	    if(StringUtils.isBlank(str)){
	        throw new Exception("参数错误");
        }
		List<HashMap<String, Object>> res = new LinkedList<>();
        StringBuilder sql = new StringBuilder("SELECT pli.product_name, pli.sn,  pli.t_date, hy_order.people, hy_order.store_id AS store_id,  (SELECT store_name FROM hy_store WHERE hy_store.id = store_id), pli.order_money, pli.koudian, pli.money FROM (SELECT order_id, sn, product_name, t_date, order_money, koudian, money  FROM hy_payables_line_item WHERE payment_line_id IN (");
	    String plaintext = DESUtils.getDecryptString(str.trim());
	    // GROUP_CONCAT默认使用","进行拼接
        String[] strs = plaintext.split(",");
        for (String s : strs) {
            sql.append(Long.parseLong(s));
            sql.append(",");
        }
        sql.deleteCharAt(sql.length() - 1);
        sql.append(")) AS pli LEFT JOIN hy_order ON pli.order_id = hy_order.id");

        /*
        obj[0] 产品名称
        obj[1] 产品编号
        obj[2] 发团日期  TODO 票务产品 t_date的含义?
        obj[3] 人数
        obj[4] 门店id
        obj[5] 门店名称
        obj[6] 订单金额
        obj[7] 扣点金额
        obj[8] 应付金额
        */
        List<Object[]> list = super.statis(sql.toString());
        for (Object[] obj : list) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("productName", obj[0]);
            map.put("sn", obj[1]);
            map.put("tDate", obj[2]);
            map.put("num", obj[3]);
            map.put("storeName", obj[5]);
            map.put("orderMoney", obj[6]);
            map.put("deduction", obj[7]);
            map.put("shouldpay", obj[8]);
            res.add(map);
        }
        return res;
	}
}