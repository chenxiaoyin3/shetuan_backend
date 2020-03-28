package com.hongyu.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.entity.BusinessBanner;
import com.hongyu.service.BusinessBannerService;

@RestController
@RequestMapping("/ymmall/banner")
public class YmmallBannerController {
	
	@Resource(name="businessBannerServiceImpl")
	private BusinessBannerService businessBannerService;
	
	/**
	 * 商城展示广告列表
	 * @param 
	 * @return
	 */
	@RequestMapping(value="/listad")
	public Json bannerList(){
		Json j = new Json();
		try {
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("state", true));
			List<BusinessBanner> list = businessBannerService.findList(null, filters, null);
			j.setSuccess(true);
			j.setMsg("查询成功");
			j.setObj(list);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}

}
