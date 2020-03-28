package com.hongyu.task.impl;

import java.math.BigInteger;
import java.util.List;

import javax.annotation.Resource;

import org.apache.shiro.util.CollectionUtils;
import org.springframework.stereotype.Component;

import com.hongyu.service.PayablesLineItemService;
import com.hongyu.service.PayablesLineService;
import com.hongyu.service.PaymentSupplierService;
import com.hongyu.task.Processor;

/**
 * @author xyy
 * */
@Component("payServicerProcessor")
public class PayServicerProcessor implements Processor{

	@Resource(name = "payablesLineItemServiceImpl")
	PayablesLineItemService payablesLineItemService;

	@Resource(name = "payablesLineServiceImpl")
	PayablesLineService payablesLineService;
	
	@Resource(name = "paymentSupplierServiceImpl")
	PaymentSupplierService paymentSupplierService;
	@Override
	public void process() {
		try{
			// 筛选条件 payablesLine的金额大于0 且结算日期在明天之前
			// 按合同号分组
            String sql = "SELECT supplier_contract, operator FROM hy_payables_line WHERE money > 0 AND date < date_add(NOW(), interval 1 day) GROUP BY supplier_contract, operator;";
            List<Object[]> objList = paymentSupplierService.statis(sql);

			if(CollectionUtils.isEmpty(objList)){
				return;
			}

			for (Object[] obj : objList) {
				Long supplierContractId = ((BigInteger)obj[0]).longValue();
				String operator = (String) obj[1];
				paymentSupplierService.addPaymentSuppierAuto(supplierContractId, operator);
			}


		}catch (Exception e) {
			e.printStackTrace();
		}
	}

}
