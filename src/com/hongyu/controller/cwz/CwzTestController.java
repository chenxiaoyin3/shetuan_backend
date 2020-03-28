package com.hongyu.controller.cwz;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hongyu.entity.HyVisa;
import com.hongyu.service.HyAreaService;
import com.hongyu.service.HyCountryService;
import com.hongyu.service.HyVisaService;

@RestController
//上面的加一个“Rest”，加上下面这一句，后面就不用再加@responsebody了 表示返回是json格式
@Transactional(propagation = Propagation.REQUIRED)
@RequestMapping("/admin/business/daily/visa/test/")
public class CwzTestController {

	@Resource(name = "hyVisaServiceImpl")
	private HyVisaService hyVisaService;
	
	@Resource(name = "hyAreaServiceImpl")
	private HyAreaService hyAreaService;
	
	@Resource(name = "hyAreaServiceImpl")
	private HyCountryService hyCountryService;
	
	
	@RequestMapping(value = "visaSaveTest")
	public void testSaveVisa() {
		HyVisa hyVisa = new HyVisa();
		hyVisa.setSubmitTime(new Date());
//		hyVisa.SetCountry(hyCountryService.find(3L));
		hyVisa.setCreator(null);
		hyVisa.setTicketSupplier(null);
		hyVisaService.save(hyVisa);
//		HyVisa hyVisa = hyVisaService.find(id)
	}
	
	@RequestMapping(value = "visaUpdateTest")
	public void testUpdateVisa() {
		HyVisa hyVisa = hyVisaService.find(1L);
		hyVisa.setExpireDays("3d");
		hyVisa.setDuration("abc");
		hyVisaService.update(hyVisa);
//		HyVisa hyVisa = hyVisaService.find(id)
	}
	
	

}
