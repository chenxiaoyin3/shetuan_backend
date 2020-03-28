package com.hongyu.service;

import com.grain.service.BaseService;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HySupplier;
import com.hongyu.entity.HySupplierContract;

public interface HySupplierContractService extends BaseService<HySupplierContract, Long> {
	
	HySupplierContract getByHySupplier(HySupplier hySupplier);
	HySupplierContract getByLiable(HyAdmin liable);

}
