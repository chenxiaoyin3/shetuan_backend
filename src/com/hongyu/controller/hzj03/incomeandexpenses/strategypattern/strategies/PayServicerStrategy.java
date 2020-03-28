package com.hongyu.controller.hzj03.incomeandexpenses.strategypattern.strategies;

import java.util.ArrayList;
import java.util.List;

import com.hongyu.controller.hzj03.incomeandexpenses.MappingUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.ContextLoader;

import com.hongyu.Filter;
import com.hongyu.controller.hzj03.incomeandexpenses.strategypattern.UnPaidStrategy;
import com.hongyu.entity.PayServicer;
import com.hongyu.entitycustom.UnPaidCustom;
import com.hongyu.service.PayServicerService;

/**
 * Created by xyy on 2018/10/18.
 * @author xyy
 */
public class PayServicerStrategy implements UnPaidStrategy{
    @Override
    public List<UnPaidCustom> getUnPaidList(String name) {
        List<UnPaidCustom> res = new ArrayList<>();
        List<Filter> filters = new ArrayList<>();
        filters.add(Filter.eq("hasPaid", 0));
        if(StringUtils.isNotEmpty(name)){
        	filters.add(Filter.like("servicerName", name));
        }
        PayServicerService payServicerService = ContextLoader.getCurrentWebApplicationContext().getBean(PayServicerService.class);
        List<PayServicer> payServicers = payServicerService.findList(null,filters,null);
        for (PayServicer p : payServicers) {
            UnPaidCustom u = new UnPaidCustom();
            u.setAmount(p.getAmount());
            u.setId(p.getId());
            u.setName(p.getServicerName());
            u.setType(MappingUtil.getKey(p.getType(), MappingUtil.PAY_SERVICER_TYPE_SET));
            res.add(u);
        }
        return res;
    }
}
