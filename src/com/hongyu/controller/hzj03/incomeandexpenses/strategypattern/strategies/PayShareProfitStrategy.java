package com.hongyu.controller.hzj03.incomeandexpenses.strategypattern.strategies;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import com.hongyu.controller.hzj03.incomeandexpenses.MappingUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.ContextLoader;

import com.hongyu.Filter;
import com.hongyu.controller.hzj03.incomeandexpenses.strategypattern.UnPaidStrategy;
import com.hongyu.entity.PayShareProfit;
import com.hongyu.entitycustom.UnPaidCustom;
import com.hongyu.service.PayShareProfitService;

/**
 * Created by lenovo on 2018/10/18.
 * 
 * @author xyy
 */
public class PayShareProfitStrategy implements UnPaidStrategy {
	@Resource(name = "payShareProfitServiceImpl")
	PayShareProfitService payShareProfitService;

	@Override
	public List<UnPaidCustom> getUnPaidList(String name) {
		List<UnPaidCustom> res = new ArrayList<>();
		List<Filter> filters = new ArrayList<>();
		filters.add(Filter.eq("hasPaid", 0));
		if (StringUtils.isNotEmpty(name)) {
			// 分公司名称 或 微商名称
			filters.add(Filter.like("client", name));
		}
		PayShareProfitService payShareProfitService = ContextLoader.getCurrentWebApplicationContext().getBean(PayShareProfitService.class);
		List<PayShareProfit> payShareProfits = payShareProfitService.findList(null, filters, null);
		for (PayShareProfit p : payShareProfits) {
			UnPaidCustom u = new UnPaidCustom();
			u.setAmount(p.getAmount());
			u.setId(p.getId());
			u.setName(p.getClient());
            u.setType(MappingUtil.getKey(p.getType(), MappingUtil.PAY_SHAREPROFIT_TYPE_SET));
			res.add(u);
		}
		return res;
	}
}
