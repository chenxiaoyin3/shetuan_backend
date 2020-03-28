package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.Json;
import com.hongyu.entity.BankList;
import com.hongyu.entity.HyAddedServiceSupplier;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.Store;
import com.hongyu.service.BankListService;
import com.hongyu.service.HyAddedServiceSupplierService;
import com.hongyu.service.StoreService;

@Service("hyAddedServiceSupplierServiceImpl")
public class HyAddedServiceSupplierServiceImpl extends BaseServiceImpl<HyAddedServiceSupplier, Long> implements HyAddedServiceSupplierService {

	@Resource(name="bankListServiceImpl")
	BankListService bankListService;
	
	@Resource(name = "storeServiceImpl")
	StoreService storeService;
	@Override
	@Resource(name="hyAddedServiceSupplierDaoImpl")
	public void setBaseDao(BaseDao<HyAddedServiceSupplier, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}

	@Override
	public Json addSupplier(HyAddedServiceSupplier hyAddedServiceSupplier,HyAdmin hyAdmin) {
		// TODO Auto-generated method stub
		Json json=new Json();
		try {
			Store store = storeService.findStore(hyAdmin);
//			if(store==null){
//				json.setSuccess(false);
//				json.setMsg("对应门店不存在");
//				 return json;
//			}
			BankList bankList = hyAddedServiceSupplier.getBankList();
			bankList.setType(BankList.BankType.bank);
			bankList.setAlias(bankList.getAccountName()+"("+bankList.getBankName()+")");
//			bankListService.save(bankList);
			hyAddedServiceSupplier.setStore(store);
			hyAddedServiceSupplier.setBankList(bankList);
			hyAddedServiceSupplier.setOperator(hyAdmin);
			this.save(hyAddedServiceSupplier);
			json.setSuccess(true);
			json.setMsg("添加成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("添加失败： "+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}

	@Override
	public Json editSuppier(HyAddedServiceSupplier hyAddedServiceSupplier) {
		// TODO Auto-generated method stub
		Json json=new Json();
		try{
//			bankListService.update(bankList,"hyCompany","type","bankType","yhlx");
			this.update(hyAddedServiceSupplier);
			json.setSuccess(true);
			json.setMsg("编辑成功");
		}catch(Exception e){
			json.setSuccess(false);
			json.setMsg("添加失败： "+e.getMessage());
			e.printStackTrace();
		}
		return json;
	}

	
	
	
}
