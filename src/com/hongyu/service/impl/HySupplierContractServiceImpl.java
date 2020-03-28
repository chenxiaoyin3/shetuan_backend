package com.hongyu.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.Filter;
import com.hongyu.dao.HySupplierContractDao;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HySupplier;
import com.hongyu.entity.HySupplierContract;
import com.hongyu.entity.HySupplierContract.ContractStatus;
import com.hongyu.service.HySupplierContractService;
@Service(value = "hySupplierContractServiceImpl")
public class HySupplierContractServiceImpl extends BaseServiceImpl<HySupplierContract, Long>
		implements HySupplierContractService {
	@Resource(name = "hySupplierContractDaoImpl")
	HySupplierContractDao dao;
	
	@Resource(name = "hySupplierContractDaoImpl")
	public void setBaseDao(HySupplierContractDao dao){
		super.setBaseDao(dao);		
	}

	@Override
	public HySupplierContract getByHySupplier(HySupplier hySupplier) {
		// TODO Auto-generated method stub
		List<Filter> filters = new ArrayList<>();
		filters.add(Filter.eq("hySupplier", hySupplier));
		filters.add(Filter.eq("contractStatus", ContractStatus.zhengchang));
		List<HySupplierContract> contracts = this.findList(null,filters,null);
		if(contracts==null || contracts.isEmpty()){
			return null;
		}
		return contracts.get(0);
	}
	/**
	 * 根据负责人获取对应的合同，这里的负责人对应的票务产品的创建人
	 * @author liyang
	 */
	@Override
	public HySupplierContract getByLiable(HyAdmin liable) {
		if(liable.getHyAdmin()!=null){
			//如果是供应商子帐号，则找到父账号
			liable = liable.getHyAdmin();
		}
		List<Filter> filters = new ArrayList<>();
		filters.add(Filter.eq("liable", liable));
		filters.add(Filter.eq("contractStatus", ContractStatus.zhengchang));
		List<HySupplierContract> contracts = this.findList(null,filters,null);
		if(contracts==null || contracts.isEmpty()){
			return null;
		}
		return contracts.get(0);
	}
		
}
