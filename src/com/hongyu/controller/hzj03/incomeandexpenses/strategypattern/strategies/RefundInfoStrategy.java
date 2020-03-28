package com.hongyu.controller.hzj03.incomeandexpenses.strategypattern.strategies;

import java.util.ArrayList;
import java.util.List;

import com.hongyu.controller.hzj03.incomeandexpenses.MappingUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.ContextLoader;

import com.hongyu.Filter;
import com.hongyu.controller.hzj03.incomeandexpenses.strategypattern.UnPaidStrategy;
import com.hongyu.entity.RefundInfo;
import com.hongyu.entitycustom.UnPaidCustom;
import com.hongyu.service.RefundInfoService;

/**
 * Created by lenovo on 2018/10/18.
 * @author xyy
 */
public class RefundInfoStrategy implements UnPaidStrategy {
	@Override
	public List<UnPaidCustom> getUnPaidList(String name) {
		List<UnPaidCustom> res = new ArrayList<>();
		List<Filter> filters = new ArrayList<>();
		filters.add(Filter.eq("state", 0));
		if (StringUtils.isNotEmpty(name)) {
			filters.add(Filter.like("appliName", name));
		}
		RefundInfoService refundInfoService = ContextLoader.getCurrentWebApplicationContext().getBean(RefundInfoService.class);
		List<RefundInfo> refundInfos = refundInfoService.findList(null, filters, null);
		for (RefundInfo p : refundInfos) {
			UnPaidCustom u = new UnPaidCustom();
			u.setAmount(p.getAmount());
			u.setId(p.getId());
			u.setName(p.getAppliName());
			u.setType(MappingUtil.getKey(p.getType(), MappingUtil.REFUNDINFO_TYPE_SET));
			res.add(u);
		}
		return res;
	}
}
