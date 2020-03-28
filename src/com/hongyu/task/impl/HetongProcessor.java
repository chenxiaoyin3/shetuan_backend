package com.hongyu.task.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.hongyu.Filter;
import com.hongyu.entity.HySupplier;
import com.hongyu.entity.HySupplierContract;
import com.hongyu.entity.HySupplierContract.ContractStatus;
import com.hongyu.service.HySupplierContractService;
import com.hongyu.service.HySupplierService;
import com.hongyu.task.Processor;
/**
 * 扫描合同是否过期的定时器，每天凌晨0点定时扫描-先放着，不敢碰太他妈麻烦了
 * @author guoxinze
 *
 */
@Component("hetongProcessor")
public class HetongProcessor implements Processor {
	
	@Resource(name="hySupplierContractServiceImpl")
	private HySupplierContractService hySupplierContractService;
	
	@Resource(name="hySupplierServiceImpl")
	private HySupplierService hySupplierService;

	@Override
	public void process() {
		// TODO Auto-generated method stub
		try{
			//当合同到期以后供应商负责人账号不可以登录
			Date curDate = new Date();
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("contractStatus", ContractStatus.zhengchang));
			List<HySupplierContract> supplierContracts = hySupplierContractService.findList(null, filters, null);
			for(HySupplierContract c : supplierContracts) {
				if(c.getDeadDate().before(curDate)) {
					c.setContractStatus(ContractStatus.dongjie);
					hySupplierContractService.update(c);
				}
			}
			
			filters.clear();
			filters.add(Filter.eq("contractStatus", ContractStatus.weishengxiao));
			filters.add(Filter.eq("contractStatus", ContractStatus.yibiangeng));
			List<HySupplierContract> supplierContracts1 = hySupplierContractService.findList(null, filters, null);
			for(HySupplierContract c : supplierContracts1) {
				if(c.getStartDate().before(new Date())) {
					c.setContractStatus(ContractStatus.zhengchang);
					hySupplierContractService.update(c);
				}
			}
			
			//20181210新增修改供应商isactive的逻辑
			List<HySupplier> suppliers = hySupplierService.findAll();
			for(HySupplier temp : suppliers) {
				Set<HySupplierContract> contracts = temp.getHySupplierContracts();
				temp.setIsActive(false);
				for(HySupplierContract itemp : contracts) {
					if(itemp.getContractStatus() == ContractStatus.zhengchang){
						temp.setIsActive(true);
						break;
					}									
				}
				hySupplierService.update(temp);
			}
			
		} catch (Exception e) {
		      e.printStackTrace();
		}
	}
}
