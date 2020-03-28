package com.hongyu.controller.hzj03.incomeandexpenses.strategypattern.strategies;

import java.util.ArrayList;
import java.util.List;

import com.hongyu.controller.hzj03.incomeandexpenses.MappingUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.ContextLoader;

import com.hongyu.Filter;
import com.hongyu.controller.hzj03.incomeandexpenses.strategypattern.UnPaidStrategy;
import com.hongyu.entity.PayGuider;
import com.hongyu.entitycustom.UnPaidCustom;
import com.hongyu.service.PayGuiderService;

/**
 * Created by lenovo on 2018/10/18.
 * @author xyy
 */
public class PayGuiderStrategy implements UnPaidStrategy{
    @Override
    public List<UnPaidCustom> getUnPaidList(String name) {
        List<UnPaidCustom> res = new ArrayList<>();
        List<Filter> filters = new ArrayList<>();
        filters.add(Filter.eq("hasPaid", 0));
		if (StringUtils.isNotEmpty(name)) {
			filters.add(Filter.like("guider", name));
		}
        PayGuiderService payGuiderService = ContextLoader.getCurrentWebApplicationContext().getBean(PayGuiderService.class);
        List<PayGuider> payGuiders = payGuiderService.findList(null,filters,null);
        for (PayGuider p : payGuiders) {
            UnPaidCustom u = new UnPaidCustom();
            u.setAmount(p.getAmount());
            u.setId(p.getId());
            u.setName(p.getGuider());
            u.setType(MappingUtil.getKey(p.getType(), MappingUtil.PAY_GUIDER_TYPE_SET));
            res.add(u);
        }
        return res;
    }
}
