package com.hongyu.task.impl;

import com.hongyu.Filter;
import com.hongyu.entity.HyLine;
import com.hongyu.entity.HyValidProduct;
import com.hongyu.service.HyLineService;
import com.hongyu.service.HyValidProductService;
import com.hongyu.task.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component("validProductProcessor")
public class ValidProductProcessor implements Processor {


	@Autowired
	private HyLineService hyLineService;

	@Autowired
	private HyValidProductService hyValidProductService;

	@Override
	public void process() {

		List<Filter> filters = new ArrayList<>();
		filters.add(Filter.eq("isSale", HyLine.IsSaleEnum.yishang));
		List<HyLine> lines = hyLineService.findList(null,filters,null);

		HyValidProduct hyValidProduct = new HyValidProduct();
		hyValidProduct.setRecordtime(new Date());
		hyValidProduct.setQuantity(lines.size());

		hyValidProductService.save(hyValidProduct);

	}
}
