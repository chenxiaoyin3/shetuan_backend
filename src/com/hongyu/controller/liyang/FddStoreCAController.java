package com.hongyu.controller.liyang;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.bouncycastle.jce.provider.BrokenJCEBlockCipher.BrokePBEWithMD5AndDES;
import org.springframework.stereotype.Controller;
/**
 * 门店的CA证书管理，存储门店申请的CA号，并提供增删改查接口
 * @author liyang
 *
 */
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.FddStoreCA;
import com.hongyu.entity.Store;
import com.hongyu.service.FddStoreCAService;
import com.hongyu.service.StoreService;
@Controller
@RequestMapping("admin/storeCAManagement/")
public class FddStoreCAController {
	@Resource(name = "fddStoreCAServiceImpl")
	FddStoreCAService fddStoreCAService;
	
	@Resource(name = "storeServiceImpl")
	StoreService storeService;
	@RequestMapping("list/view")
	@ResponseBody
	public Json getlist(Pageable pageable,String storeName){
		Json json = new Json();
		try {
			List<Filter> filters = new ArrayList<>();
			if(storeName!=null){
				filters.add(Filter.like("storeName", storeName));
			}
			pageable.setFilters(filters);
			Page<FddStoreCA> fddStoreCAs = fddStoreCAService.findPage(pageable);
			HashMap<String, Object> hashMap = new HashMap<>();
			List<HashMap<String, Object>> result = new ArrayList<>();
			hashMap.put("pageNumber", fddStoreCAs.getPageNumber());
			hashMap.put("pageSize", fddStoreCAs.getPageSize());
			hashMap.put("total", fddStoreCAs.getTotal());
			hashMap.put("rows", fddStoreCAs.getRows());
			
			json.setMsg("查询成功");
			json.setSuccess(true);
			json.setObj(hashMap);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败："+e.getMessage());
		}
		return json;
	}
	@RequestMapping("storeList")
	@ResponseBody
	public Json getStoreList(){
		Json json = new Json();
		try {
			String jpql = "select id,store_name from hy_store"; 
			List<Object[]> stores = storeService.statis(jpql);
			List<HashMap<String, Object>> result = new ArrayList<>();
			for(Object[] tmp:stores){
				HashMap<String, Object> hMap = new HashMap<>();
				hMap.put("id", (BigInteger)tmp[0]);
				hMap.put("storeName", (String)tmp[1]);
				result.add(hMap);
			}

			json.setMsg("查询成功");
			json.setSuccess(true);
			json.setObj(result);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败："+e.getMessage());
		}
		return json;
	}
	@RequestMapping("add")
	@ResponseBody
	public Json add(FddStoreCA fddStoreCA){
		Json json = new Json();
		try {
			fddStoreCAService.save(fddStoreCA);
			json.setMsg("添加成功");
			json.setSuccess(true);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("添加失败："+e.getMessage());
		}
		return json;
	}
	@RequestMapping("edit")
	@ResponseBody
	public Json edit(Long id,String storeCA){
		Json json = new Json();
		try {
			FddStoreCA fddStoreCA = fddStoreCAService.find(id); 
			fddStoreCA.setStoreCA(storeCA);
			fddStoreCAService.update(fddStoreCA);
			json.setMsg("修改成功");
			json.setSuccess(true);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("修改失败："+e.getMessage());
		}
		return json;
	}
}
