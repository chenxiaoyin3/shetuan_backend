package com.hongyu.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.hongyu.Filter;
import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.HyOrderApplicationItemDao;
import com.hongyu.entity.HyOrderApplicationItem;
import com.hongyu.entity.HyOrderCustomer;
import com.hongyu.entity.HyOrderItem;
import com.hongyu.service.HyOrderApplicationItemService;
import com.hongyu.service.HyOrderItemService;

@Service("hyOrderApplicationItemServiceImpl")
public class HyOrderApplicationItemServiceImpl extends BaseServiceImpl<HyOrderApplicationItem,Long> implements HyOrderApplicationItemService {

	@Resource(name="hyOrderItemServiceImpl")
	private HyOrderItemService hyOrderItemService;
	@Resource(name="hyOrderApplicationItemDaoImpl")
	public void setBaseDao(HyOrderApplicationItemDao dao){
		super.setBaseDao(dao);		
	}
	
	@Override
	public Map<String, Object> auditItemHelper(HyOrderApplicationItem item) throws Exception {
		// TODO Auto-generated method stub
		
		HyOrderItem orderItem = hyOrderItemService.find(item.getItemId());
		Map<String, Object> map = new HashMap<>();
		map.put("id", item.getId());
		map.put("itemId", orderItem.getId());
		map.put("type", orderItem.getType());
		map.put("priceType", orderItem.getPriceType());
		map.put("name", orderItem.getName());
		map.put("number", orderItem.getNumber());
		map.put("numberOfReturn", orderItem.getNumberOfReturn());
		map.put("jiesuanPrice", orderItem.getJiesuanPrice());
		map.put("jiesuanRefund", item.getJiesuanRefund());
		map.put("waimaiPrice", orderItem.getWaimaiPrice());
		map.put("waimaiRefund", item.getWaimaiRefund());
		if(orderItem.getHyOrderCustomers()!= null && orderItem.getHyOrderCustomers().size()>0){
			HyOrderCustomer orderCustomer = orderItem.getHyOrderCustomers().get(0);
			map.put("customer", orderCustomer.getName());
		}
		if(orderItem.getType()==1) {	//如果是线路订单，有保险
			map.put("baoxianJiesuanPrice", hyOrderItemService.getBaoxianJiesuanPrice(orderItem));
			map.put("baoxianJiesuanRefund", item.getBaoxianJiesuanRefund());
			map.put("baoxianWaimaiPrice", hyOrderItemService.getBaoxianWaimaiPrice(orderItem));
			map.put("baoxianWaimaiRefund", item.getBaoxianWaimaiRefund());
		}else{	//如果是其他订单，无保险
			map.put("baoxianJiesuanPrice", 0);
			map.put("baoxianJiesuanRefund", 0);
			map.put("baoxianWaimaiPrice", 0);
			map.put("baoxianWaimaiRefund", 0);
		}

		
		return map;
	}

	@Override
	public List<HyOrderApplicationItem> getItemsByOrderItem(Long itemId) {
		List<Filter> filters = new ArrayList<>();

		filters.add(Filter.eq("itemId", itemId));

		List<HyOrderApplicationItem> items = this.findList(null, filters, null);
		return items;


	}

	@Override
	public HyOrderApplicationItem getTotalRefund(List<HyOrderApplicationItem> items){
		HyOrderApplicationItem ans = new HyOrderApplicationItem();
		ans.setWaimaiRefund(BigDecimal.ZERO);
		ans.setJiesuanRefund(BigDecimal.ZERO);
		ans.setBaoxianJiesuanRefund(BigDecimal.ZERO);
		ans.setBaoxianWaimaiRefund(BigDecimal.ZERO);

		if(items == null){
			return ans;
		}

		for(HyOrderApplicationItem item : items){
			ans.setBaoxianWaimaiRefund(item.getBaoxianWaimaiRefund().add(ans.getBaoxianWaimaiRefund()));
			ans.setBaoxianJiesuanRefund(item.getBaoxianJiesuanRefund().add(ans.getBaoxianJiesuanRefund()));
			ans.setWaimaiRefund(item.getWaimaiRefund().add(ans.getWaimaiRefund()));
			ans.setJiesuanRefund(item.getJiesuanRefund().add(ans.getJiesuanRefund()));

		}

		return ans;

	}

}
