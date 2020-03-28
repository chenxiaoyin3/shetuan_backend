package com.hongyu.controller.gsbing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyVisa;
import com.hongyu.entity.HyVisaPrices;
import com.hongyu.entity.MhProductPicture;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyVisaPricesService;
import com.hongyu.service.HyVisaService;
import com.hongyu.service.MhProductPictureService;


/**门户完善签证相关接口*/
@Controller
@RequestMapping("/admin/menhuPerfect/visa/")
public class MhVisaController {
	@Resource(name="hyAdminServiceImpl")
	private HyAdminService hyAdminService;
	
	@Resource(name="hyVisaServiceImpl")
	private HyVisaService hyVisaService;
	
	@Resource(name="hyVisaPricesServiceImpl")
	private HyVisaPricesService hyVisaPricesService;
	
	@Resource(name="mhProductPictureServiceImpl")
	private MhProductPictureService mhProductPictureService;
	
	
	/**列表页*/
	@RequestMapping(value="list/view")
	@ResponseBody
	public Json listview(Pageable pageable,HyVisa queryParam)
	{
		Json json=new Json();
		try {
			List<HashMap<String, Object>> list = new ArrayList<>();
			Map<String,Object> map=new HashMap<String,Object>();
			List<Filter> filters=new ArrayList<>();
			filters.add(Filter.eq("status", true)); //只筛选正常状态的
			pageable.setFilters(filters);
			List<Order> orders = new ArrayList<Order>();
			orders.add(Order.desc("createTime"));
			pageable.setOrders(orders);
			Page<HyVisa> page=hyVisaService.findPage(pageable, queryParam);
			if(page.getTotal()>0) {
				for(HyVisa hyVisa:page.getRows()) {
					HashMap<String,Object> visaMap=new HashMap<String,Object>();
					visaMap.put("id",hyVisa.getId());
					visaMap.put("productId",hyVisa.getProductId()); //productId
					visaMap.put("productName", hyVisa.getProductName());
					visaMap.put("visaType",hyVisa.getVisaType());
					visaMap.put("saleStatus",hyVisa.getSaleStatus());	
					visaMap.put("mhState",hyVisa.getMhState());
					visaMap.put("mhIsSale",hyVisa.getMhIsSale());
					list.add(visaMap);
				}
			}
			map.put("rows", list);
		    map.put("pageNumber", Integer.valueOf(pageable.getPage()));
		    map.put("pageSize", Integer.valueOf(pageable.getRows()));
		    map.put("total",Long.valueOf(page.getTotal()));
			json.setMsg("查询成功");
		    json.setSuccess(true);
		    json.setObj(map);
		}
		catch(Exception e) {
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	/**详情页*/
	@RequestMapping("detail/view")
	@ResponseBody
	public Json detail(Long id)
	{
		Json json=new Json();
		try{
			HyVisa hyVisa=hyVisaService.find(id);
			HashMap<String,Object> map=new HashMap<String,Object>();
			map.put("productId", hyVisa.getProductId());
			map.put("productName", hyVisa.getProductName());
			map.put("continent",hyVisa.getCountry().getContinent());
			map.put("country", hyVisa.getCountry().getName());
			map.put("visaType", hyVisa.getVisaType());
			map.put("duration", hyVisa.getDuration());
			map.put("times", hyVisa.getTimes());
			map.put("isInterview", hyVisa.getIsInterview());
			map.put("stayDays", hyVisa.getStayDays());
			map.put("serviceContent", hyVisa.getServiceContent());
			map.put("priceContain", hyVisa.getPriceContain());
			map.put("reserveRequirement", hyVisa.getReserveRequirement());
			map.put("accessory", hyVisa.getAccessory()); //附件
			map.put("introduce",hyVisa.getIntroduce()); //签证说明文字			
			List<HyVisaPrices> prices=hyVisa.getHyVisaPrices();
			List<HashMap<String,Object>> priceList=new ArrayList<>();
			for(HyVisaPrices price:prices) {
				HashMap<String,Object> priceMap=new HashMap<String,Object>();
				priceMap.put("priceId",price.getId());
				priceMap.put("startDate", price.getStartDate());
				priceMap.put("endDate",price.getEndDate());
				priceMap.put("displayPrice",price.getDisplayPrice());
				priceMap.put("sellPrice",price.getSellPrice());
				priceMap.put("settlementPrice",price.getSettlementPrice());
				priceMap.put("mhDisplayPrice",price.getMhDisplayPrice());
				priceMap.put("mhSellPrice",price.getMhSellPrice());
				priceMap.put("mhPrice",price.getMhPrice());
				priceList.add(priceMap);
			}						
			map.put("priceList", priceList);
			
			//以下为门户用字段
			map.put("mhProductName",hyVisa.getMhProductName()); //门户产品名称
			map.put("mhPriceContain",hyVisa.getMhPriceContain()); //门户费用包含
			map.put("mhReserveRequirement",hyVisa.getMhReserveRequirement()); //门户预订须知
			map.put("mhAcceptScope",hyVisa.getMhAcceptScope()); //门户签证受理范围
			map.put("mhAttention",hyVisa.getMhAttention()); //门户签证注意事项
			map.put("mhIsSale",hyVisa.getMhIsSale()); //门户是否上线
			map.put("mhCreateTime",hyVisa.getMhCreateTime()); //门户完善时间
			map.put("mhOperator", hyVisa.getMhOperator()); //完善人
			map.put("mhIsHot",hyVisa.getMhIsHot()); //是否热门国家
			
			//产品图片相关信息
			List<Filter> filters=new ArrayList<>();
			filters.add(Filter.eq("type", 5));//5签证
			filters.add(Filter.eq("productId", hyVisa.getId()));
			List<MhProductPicture> productPictures=mhProductPictureService.findList(null,filters,null);
			List<Map<String,Object>> pictureList=new ArrayList<>();
			for(MhProductPicture picture:productPictures) {
				Map<String,Object> obj=new HashMap<>();
				obj.put("source", picture.getSource());
				obj.put("large", picture.getLarge());
				obj.put("medium", picture.getMedium());
				obj.put("thumbnail", picture.getThumbnail());
				obj.put("isMark", picture.getIsMark());
				pictureList.add(obj);
			}
			map.put("mhProductPictures", pictureList);
			json.setMsg("查询成功");
		    json.setSuccess(true);
		    json.setObj(map);
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	/**新建一个内部类,传参*/
	static class WrapVisa{
		private Long visaId;
		private HyVisa hyVisa;
		private List<MhProductPicture> mhProductPictures=new ArrayList<>();
		public Long getVisaId() {
			return visaId;
		}
		public void setVisaId(Long visaId) {
			this.visaId = visaId;
		}
		public HyVisa getHyVisa() {
			return hyVisa;
		}
		public void setHyVisa(HyVisa hyVisa) {
			this.hyVisa = hyVisa;
		}
		public List<MhProductPicture> getMhProductPictures() {
			return mhProductPictures;
		}
		public void setMhProductPictures(List<MhProductPicture> mhProductPictures) {
			this.mhProductPictures = mhProductPictures;
		}
	}
	
	/**签证完善*/
	@RequestMapping("perfect")
	@ResponseBody
	public Json perfect(@RequestBody WrapVisa wrapVisa,HttpSession session)
	{
		Json json=new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			String mhOperator=admin.getName();
			Long visaId=wrapVisa.getVisaId();
			HyVisa preVisa=hyVisaService.find(visaId);
			HyVisa hyVisa=wrapVisa.getHyVisa();
			preVisa.setMhProductName(hyVisa.getMhProductName());
			preVisa.setMhPriceContain(hyVisa.getMhPriceContain());
			preVisa.setMhReserveRequirement(hyVisa.getMhReserveRequirement());
			preVisa.setMhAcceptScope(hyVisa.getMhAcceptScope());
			preVisa.setMhAttention(hyVisa.getMhAttention());
			preVisa.setMhIsHot(hyVisa.getMhIsHot());
			if(preVisa.getMhState()==null || preVisa.getMhState()==0) {
				preVisa.setMhOperator(mhOperator);
				preVisa.setMhCreateTime(new Date());
			}
			else {
				preVisa.setMhUpdateTime(new Date());
			}
			preVisa.setMhState(1);
			hyVisaService.update(preVisa);
			
			List<MhProductPicture> mhProductPictures=wrapVisa.getMhProductPictures();
			
			//产品图片,删掉以前的,增加新的
			List<Filter> filters=new ArrayList<>();
			filters.add(Filter.eq("type", 5)); //5-签证
			filters.add(Filter.eq("productId", visaId)); //酒店id
			List<MhProductPicture> priProductPictures=mhProductPictureService.findList(null,filters,null);
			//删掉以前的
			for(MhProductPicture picture:priProductPictures) {
				mhProductPictureService.delete(picture);
			}
			
			//增加新的
			for(MhProductPicture picture:mhProductPictures) {
				picture.setType(5); //5-签证
				picture.setProductId(visaId);
				mhProductPictureService.save(picture);
			}
			json.setSuccess(true);
			json.setMsg("完善成功");
		}
		catch(Exception e) {
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	/**价格完善*/
	@RequestMapping(value="pricePerfect")
	@ResponseBody
	public Json pricePerfect(Long priceId,BigDecimal mhDisplayPrice,BigDecimal mhSellPrice,BigDecimal mhPrice)
	{
		Json json=new Json();
		try {
			HyVisaPrices price=hyVisaPricesService.find(priceId);
			price.setMhDisplayPrice(mhDisplayPrice);
			price.setMhSellPrice(mhSellPrice);
			price.setMhPrice(mhPrice);
			hyVisaPricesService.update(price);
			json.setSuccess(true);
			json.setMsg("完善成功");
		}
		catch(Exception e) {
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	/**门户上线*/
	@RequestMapping(value="online")
	@ResponseBody
	public Json online(Long visaId)
	{
		Json json=new Json();
		try {
			HyVisa hyVisa=hyVisaService.find(visaId);
			if(hyVisa.getSaleStatus()!=2) {
				json.setSuccess(false);
				json.setMsg("供应商未上线");
				return json;
			}
			List<HyVisaPrices> priceList=hyVisa.getHyVisaPrices();
			for(HyVisaPrices price:priceList) {
				if(price.getMhPrice()==null || price.getMhDisplayPrice()==null || price.getMhSellPrice()==null) {
					json.setSuccess(false);
					json.setMsg("门户相应价格未完善");
					return json;
				}
			}
			hyVisa.setMhIsSale(1); //门户上线
			hyVisaService.update(hyVisa);
			json.setSuccess(true);
			json.setMsg("上线成功");
		}
		catch(Exception e) {
			json.setSuccess(true);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	/**门户下线*/
	@RequestMapping(value="offline")
	@ResponseBody
	public Json offline(Long visaId)
	{
		Json json=new Json();
		try {
			HyVisa hyVisa=hyVisaService.find(visaId);
			hyVisa.setMhIsSale(0); //门户下线
			hyVisaService.update(hyVisa);
			json.setSuccess(true);
			json.setMsg("下线成功");
		}
		catch(Exception e) {
			json.setSuccess(true);
			json.setMsg(e.getMessage());
		}
		return json;
	}
}
