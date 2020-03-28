package com.hongyu.controller;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.bouncycastle.jcajce.provider.symmetric.Salsa20;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.Filter;
import com.hongyu.Filter.Operator;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;

import com.hongyu.entity.HySingleitemPromotion;

import com.hongyu.entity.HyLabel;

import com.hongyu.entity.HyVinbound;
import com.hongyu.entity.Inbound;
import com.hongyu.entity.PurchaseItem;
import com.hongyu.entity.Specialty;
import com.hongyu.entity.SpecialtyAppraise;
import com.hongyu.entity.SpecialtyCategory;
import com.hongyu.entity.SpecialtyImage;
import com.hongyu.entity.SpecialtyPrice;
import com.hongyu.entity.SpecialtySpecification;
import com.hongyu.entity.WeBusiness;
import com.hongyu.entity.WeDivideModel;
import com.hongyu.entity.WechatAccount;

import com.hongyu.service.HySingleitemPromotionService;

import com.hongyu.service.HyLabelService;
import com.hongyu.service.HySpecialtyLabelService;

import com.hongyu.service.HyVinboundService;
import com.hongyu.service.InboundService;
import com.hongyu.service.PurchaseItemService;
import com.hongyu.service.SpecialtyAppraiseService;
import com.hongyu.service.SpecialtyCategoryService;
import com.hongyu.service.SpecialtyPriceService;
import com.hongyu.service.SpecialtyService;
import com.hongyu.service.SpecialtySpecificationService;
import com.hongyu.service.WeBusinessService;
import com.hongyu.service.WeDivideModelService;
import com.hongyu.service.WechatAccountService;
import com.hongyu.util.ArrayHandler;
import com.hongyu.util.Constants;
import com.sun.org.apache.bcel.internal.generic.NEWARRAY;
import com.thoughtworks.xstream.security.ForbiddenClassException;

import org.springframework.transaction.annotation.Propagation;
@Controller
@RequestMapping(value={"/ymmall/product"})
@Transactional(propagation = Propagation.REQUIRED)
public class YmmallSpecialtyController {
	

	
	@Resource(name="specialtyServiceImpl")
	SpecialtyService specialtyServiceImpl;
	@Resource(name="specialtyCategoryServiceImpl")
	SpecialtyCategoryService specialtyCategoryServiceImpl;
	@Resource(name="specialtySpecificationServiceImpl")
	SpecialtySpecificationService specialtySpecificationServiceImpl;
	@Resource(name="specialtyPriceServiceImpl")
	SpecialtyPriceService specialtyPriceServiceImpl;
	
	@Resource(name="purchaseItemServiceImpl")
	PurchaseItemService purchaseItemServiceImpl;
	
	@Resource(name="inboundServiceImpl")
	InboundService inboundService;
	
	@Resource(name="weBusinessServiceImpl")
	WeBusinessService weBusinessService;
	
	@Resource(name="weDivideModelServiceImpl")
	WeDivideModelService weDivideModelService;
	
	@Resource(name="specialtyAppraiseServiceImpl")
	SpecialtyAppraiseService specialtyAppraiseService;
	  //获取分区所有子分区列表
	  private List<SpecialtyCategory> getCategoriesTreeList(SpecialtyCategory parent)
	  {
	    List<SpecialtyCategory> list = new ArrayList<SpecialtyCategory>();
	    if (parent.getChildSpecialtyCategory()!=null && 
	    		!parent.getChildSpecialtyCategory().isEmpty()) {
	      for (SpecialtyCategory child : parent.getChildSpecialtyCategory())
	      {
	    	  if(child.getIsActive()){
	    		  list.add(child);
	    	  }
	      }
	      for(SpecialtyCategory child : parent.getChildSpecialtyCategory()){
	    	  if(child.getIsActive()){
	    		  List<SpecialtyCategory> sub=getCategoriesTreeList(child);
	    		  if(sub!=null && !sub.isEmpty()){
	    			  list.addAll(sub);
	    		  }
	    	  }
	      }
	    }
	    return list;
	  }
	  
	  
	  @RequestMapping("/test")
	  @ResponseBody
	  public Json Test(){
		  Json json = new  Json();
		  json.setSuccess(true);
		  json.setMsg("查询成功");
		  json.setObj(null);
		  return json;
	  }
	  
	  @RequestMapping(value={"/search"})
	  @ResponseBody
	  public Json searchSpecialtys(Pageable pageable,@RequestParam Map<String, Object> params,HttpSession session,Integer condition){
		  Json json=new Json();
		  try {
			  String sqlStr = Constants.SQL_MIN_PRICE_SPEC;
			  List<SpecialtySpecification> specifications = null;
			  if(params.containsKey("specialty_name")){
				  sqlStr += " and s1.name like '%"+params.get("specialty_name")+"%'";
			  }else if(params.containsKey("specialty_id")){
				  sqlStr += " and s1.id="+params.get("specialty_id");
			  }else if(params.containsKey("specification_id")){
				  sqlStr += " and sp1.id="+params.get("specification_id");
			  }else if(params.containsKey("category_id")){
				  String categoryIdStr = getStringByCategoryId(Long.valueOf((String)params.get("category_id")));
				  sqlStr += " and s1.category_id in ("+categoryIdStr+")"; 
			  }
			  
			  sqlStr += " group by p1.id";
			  
			  String sqlTotal = Constants.SQL_MIN_PRICE_SPEC_TOTAL+sqlStr;
			  List totals = specialtyServiceImpl.statis(sqlTotal);
			  Integer total = totals.size();

			  if(condition==null || condition==0){
					//默认排序
			  }else if(condition == 1){
				  sqlStr += " order by hasSold desc";
			  }else if(condition == 2){
				  sqlStr += " order by pPrice asc";
			  }else if(condition == 3){
				  sqlStr += " order by pPrice desc";
			  }	
			  
			  Integer start = (pageable.getPage()-1)*pageable.getRows();
			  start = start>total?total:start;
			  Integer end = start + pageable.getRows();
			  end = end>total?total:end;
			  
			  sqlStr += " limit "+start+","+end;
			  
			  String sqlParams = Constants.SQL_MIN_PRICE_SPEC_PARAMS+sqlStr;
			  
			  List<Object[]> objects = specialtyServiceImpl.statis(sqlParams);
			  String[] keys = new String[] {"spid","spname","sid","sname","pPrice","hasSold","imgUrl"};
			  List<Map<String, Object>> maps = new ArrayList<>();
				
			  for(Object[] object:objects) {
				  maps.add(ArrayHandler.toSpecialtyBaseInfoMap(object));
			  }
			  
			  Page<Map<String, Object>> pages=new Page<>(maps,total,pageable);
			  json.setSuccess(true);
			  json.setMsg("查询成功");
			  json.setObj(pages);
			  
			  
		  } catch (Exception e) {
			// TODO: handle exception
				json.setSuccess(false);
				json.setMsg("查询失败");
				json.setObj(e);
		  }
		  return json;  
	  }
	  

	  public static class MapComparator{
		  public static Comparator<Map<String, Object>> byPriceAsc = new Comparator<Map<String, Object>>(){
			  public int compare(Map<String, Object> mso1,Map<String, Object> mso2){
				  BigDecimal price1 = (BigDecimal)mso1.get("pPrice");
				  BigDecimal price2 = (BigDecimal)mso2.get("pPrice");
				  return (price1.compareTo(price2)==1?1:price1.compareTo(price2)==0?0:-1);
			  }
		  };
		  
		  public static Comparator<Map<String, Object>> byPriceDesc = new Comparator<Map<String, Object>>(){
			  public int compare(Map<String, Object> mso1,Map<String, Object> mso2){
				  BigDecimal price1 = (BigDecimal)mso1.get("pPrice");
				  BigDecimal price2 = (BigDecimal)mso2.get("pPrice");
				  return (price1.compareTo(price2)==-1?1:price1.compareTo(price2)==0?0:-1);
			  }
		  };
		  
		  public static Comparator<Map<String, Object>> bySalesDesc = new Comparator<Map<String, Object>>(){
			  public int compare(Map<String, Object> mso1,Map<String, Object> mso2){
//				  SpecialtySpecification s1 = (SpecialtySpecification)mso1.get("specification");
//				  SpecialtySpecification s2 = (SpecialtySpecification)mso2.get("specification");
//				  
//				  return (s1.getSaleNumber()<s2.getSaleNumber()?1:s1.getSaleNumber()==s2.getSaleNumber()?0:-1);
				  
				  //按销量降序排序
				  Integer saleNumber1 = (Integer)mso1.get("hasSold");
				  Integer saleNumber2 = (Integer)mso2.get("hasSold");
				  return saleNumber1<saleNumber2?1:saleNumber1==saleNumber2?0:-1;
			  }
		  };
		  
	  }
	  
	public List<SpecialtySpecification> pagesBySpecialtyName(String specialtyName){
		try{
			List<Filter> filters=new ArrayList<>();
			filters.add(Filter.like("name", specialtyName));
			filters.add(Filter.eq("isActive", true));
			filters.add(Filter.eq("saleState", 1));
			List<Order> orders = new ArrayList<>();
			orders.add(Order.desc("orders"));
			List<Specialty> specialties=specialtyServiceImpl.findList(null,filters,orders);
			if(specialties==null || specialties.isEmpty()){
				return null;
			}
			List<Filter> filters2=new ArrayList<>();
			filters2.add(Filter.in("specialty", specialties));
			filters2.add(Filter.eq("isActive", true));
			
			List<SpecialtySpecification> specifications=specialtySpecificationServiceImpl.findList(null,filters2,null);
			if(specifications==null || specifications.isEmpty()){
				return null;
			}
			return specifications;
			
		}catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}
	
	public List<SpecialtySpecification> pagesBySpecialtyId(Long specialtyId){
		try{
			Specialty specialty=specialtyServiceImpl.find(specialtyId);
			if(specialty==null){
				return null;
			}
			List<Filter> filters2=new ArrayList<>();
			filters2.add(Filter.eq("specialty", specialty));
			filters2.add(Filter.eq("isActive", true));
			
			List<SpecialtySpecification> specifications=specialtySpecificationServiceImpl.findList(null,filters2,null);
			if(specifications==null || specifications.isEmpty()){
				return null;
			}
			return specifications;
			
		}catch (Exception e) {
			// TODO: handle exception
			return null;
		}	
	}
	
	public List<SpecialtySpecification> pagesBySpecificationId(Long specificationId){
		try{

			List<Filter> filters2=new ArrayList<>();
			filters2.add(Filter.eq("id", specificationId));
			filters2.add(Filter.eq("isActive", true));
			List<SpecialtySpecification> specifications=specialtySpecificationServiceImpl.findList(null,filters2,null);
			if(specifications==null || specifications.isEmpty()){
				return null;
			}
			return specifications;
		}catch (Exception e) {
			// TODO: handle exception
			return null;
		}			
	}
	
	/**
	 * @param specialty
	 * @param pageable
	 * @param categoryId
	 * @return
	 */
	@RequestMapping(value={"/pages_by_category_id"})
	@ResponseBody
	public Json pagesByCategoryId(Pageable pageable,
			@RequestParam("category_id")Long categoryId,HttpSession session,Integer condition){
		Json json=new Json();
		try{
			  String sqlStr = Constants.SQL_MIN_PRICE_SPEC;

			  String categoryIdStr = getStringByCategoryId(categoryId);
			  sqlStr += " and s1.category_id in ("+categoryIdStr+")"; 

			  
			  sqlStr += " group by p1.id";
			  
			  String sqlTotal = Constants.SQL_MIN_PRICE_SPEC_TOTAL+sqlStr;
			  List totals = specialtyServiceImpl.statis(sqlTotal);
			  Integer total = totals.size();

			  if(condition==null || condition==0){
					//默认排序
			  }else if(condition == 1){
				  sqlStr += " order by hasSold desc";
			  }else if(condition == 2){
				  sqlStr += " order by pPrice asc";
			  }else if(condition == 3){
				  sqlStr += " order by pPrice desc";
			  }	
			  
			  Integer start = (pageable.getPage()-1)*pageable.getRows();
			  start = start>total?total:start;
			  Integer end = start + pageable.getRows();
			  end = end>total?total:end;
			  
			  sqlStr += " limit "+start+","+end;
			  
			  String sqlParams = Constants.SQL_MIN_PRICE_SPEC_PARAMS+sqlStr;
			  
			  List<Object[]> objects = specialtyServiceImpl.statis(sqlParams);
			  String[] keys = new String[] {"spid","spname","sid","sname","pPrice","hasSold","imgUrl"};
			  List<Map<String, Object>> maps = new ArrayList<>();
				
			  for(Object[] object:objects) {
				  maps.add(ArrayHandler.toSpecialtyBaseInfoMap(object));
			  }
	
			Page<Map<String, Object>> pages=new Page<>(maps,total,pageable);
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(pages);
		}catch(Exception e){
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(e);
		}
		return json;
	}
	
	@RequestMapping("/recommend_product_pages")
	@ResponseBody
	public Json recommendProductPages(Pageable pageable,HttpSession session,Integer condition) {
		Json json=new Json();
		try{
			  String sqlStr = Constants.SQL_MIN_PRICE_SPEC;

			  sqlStr += " and s1.is_recommend=1"; 

			  
			  sqlStr += " group by p1.id";
			  
			  String sqlTotal = Constants.SQL_MIN_PRICE_SPEC_TOTAL+sqlStr;
			  List totals = specialtyServiceImpl.statis(sqlTotal);
			  Integer total = totals.size();

			  if(condition==null || condition==0){
					//默认排序
			  }else if(condition == 1){
				  sqlStr += " order by hasSold desc";
			  }else if(condition == 2){
				  sqlStr += " order by pPrice asc";
			  }else if(condition == 3){
				  sqlStr += " order by pPrice desc";
			  }	
			  
			  Integer start = (pageable.getPage()-1)*pageable.getRows();
			  start = start>total?total:start;
			  Integer end = start + pageable.getRows();
			  end = end>total?total:end;
			  
			  sqlStr += " limit "+start+","+end;
			  
			  String sqlParams = Constants.SQL_MIN_PRICE_SPEC_PARAMS+sqlStr;
			  
			  List<Object[]> objects = specialtyServiceImpl.statis(sqlParams);
			  String[] keys = new String[] {"spid","spname","sid","sname","pPrice","hasSold","imgUrl"};
			  List<Map<String, Object>> maps = new ArrayList<>();
				
			  for(Object[] object:objects) {
				  maps.add(ArrayHandler.toSpecialtyBaseInfoMap(object));
			  }
	
			Page<Map<String, Object>> pages=new Page<>(maps,total,pageable);
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(pages);
		}catch(Exception e){
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(e);
		}
		return json;
	}
	
	
	@RequestMapping("/sub_list_for_recommend")
	@ResponseBody
	public Json subListForRecommend(Integer size,HttpSession session) {
		Json json=new Json();
		try{
			String sqlstr = Constants.SQL_MIN_PRICE_SPEC_PARAMS+Constants.SQL_MIN_PRICE_SPEC+" and s1.is_recommend=1";
			sqlstr += " group by p1.id";
			sqlstr += " order by hasSold desc";
			sqlstr += " limit 0,"+size;
			List<Object[]> objects = specialtyServiceImpl.statis(sqlstr);
			String[] keys = new String[] {"spid","spname","sid","sname","pPrice","hasSold","imgUrl"};
			List<Map<String, Object>> maps = new ArrayList<>();
			
			for(Object[] object:objects) {
				maps.add(ArrayHandler.toSpecialtyBaseInfoMap(object));
			}
			
			

			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(maps);
		}catch(Exception e){
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(e);
		}
		return json;
	}
	
	
	public List<SpecialtySpecification> pagesByCategoryId(Long categoryId){
		Json json=new Json();
		try{
			List<Filter> filters=new ArrayList<Filter>(); 
			
			List<SpecialtyCategory> categories=new ArrayList<SpecialtyCategory>();
			if(categoryId!=null){
				SpecialtyCategory category=specialtyCategoryServiceImpl.find(categoryId);
				if(category!=null){
					categories.add(category);
					categories.addAll(getCategoriesTreeList(category));
					Filter filter=new Filter("category",Filter.Operator.in,categories);
					filters.add(filter);
				}
			}
			filters.add(Filter.eq("isActive", true));
			filters.add(Filter.eq("saleState", 1));
			List<Order> orders = new ArrayList<>();
			orders.add(Order.desc("orders"));
			List<Specialty> specialties=specialtyServiceImpl.findList(null,filters,orders);
			
			if(specialties==null || specialties.isEmpty()){
				return null;
			}
			List<Filter> filters2=new ArrayList<>();
			filters2.add(Filter.in("specialty", specialties));
			filters2.add(Filter.eq("isActive", true));
			
			List<SpecialtySpecification> specifications=specialtySpecificationServiceImpl.findList(null,filters2,null);
			if(specifications==null || specifications.isEmpty()){
				return null;
			}
			return specifications;
			
		}catch(Exception e){
			return null;
		}
		
	}
	
	public String getStringByCategoryId(Long categoryId) {
		List<SpecialtyCategory> categories=new ArrayList<SpecialtyCategory>();
		if(categoryId!=null){
			SpecialtyCategory category=specialtyCategoryServiceImpl.find(categoryId);
			if(category!=null){
				categories.add(category);
				categories.addAll(getCategoriesTreeList(category));
			}
		}
		

		List<Long> categoryIds = new ArrayList<>();
		for(SpecialtyCategory category:categories) {
			categoryIds.add(category.getId());
		}
		String categoryIdStr = StringUtils.join(categoryIds,",");
		return categoryIdStr;
	}
	@RequestMapping("/sub_list_by_category_id")
	@ResponseBody
	public Json subListByCategoryId(@RequestParam("category_id")Long categoryId,Integer size,HttpSession session){
		Json json=new Json();
		try{
			String categoryIdStr = getStringByCategoryId(categoryId);
			String sqlstr = Constants.SQL_MIN_PRICE_SPEC_PARAMS+Constants.SQL_MIN_PRICE_SPEC+" and s1.category_id in ("+categoryIdStr+")";
			sqlstr += " group by p1.id";
			sqlstr += " order by hasSold desc";
			sqlstr += " limit 0,"+size;
			List<Object[]> objects = specialtyServiceImpl.statis(sqlstr);
			String[] keys = new String[] {"spid","spname","sid","sname","pPrice","hasSold","imgUrl"};
			List<Map<String, Object>> maps = new ArrayList<>();
			
			for(Object[] object:objects) {
				maps.add(ArrayHandler.toSpecialtyBaseInfoMap(object));
			}
			
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(maps);
			
		}catch(Exception e){
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(e);
		}
		
		return json;
	}
	
	@RequestMapping(value={"/specification_detail_by_specification_id"})
	@ResponseBody
	public Json specificationDetailBySpecialtyId(Long id,HttpSession session){
		Json json=new Json();
		try {
			List<Filter> filters2=new ArrayList<>();
			filters2.add(Filter.eq("id", id));
			List<SpecialtySpecification> srows=specialtySpecificationServiceImpl.findList(null,filters2,null);	
			List<Map<String, Object>> rows=filterSpecificationPrice(doSpecialtyList(srows,session));	
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(rows);
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(e);
		}
		return json;
	}
	
	
	@RequestMapping(value={"/specification_detail_by_specialty_id"})
	@ResponseBody
	public Json specificationDetailBySpecificationId(Long id,HttpSession session){
		Json json=new Json();
		try {
			Specialty specialty=specialtyServiceImpl.find(id);
			if(specialty == null){
				json.setSuccess(false);
				json.setMsg("没有该产品");
				json.setObj(null);
				return json;
			}
			List<Filter> filters2=new ArrayList<>();
			filters2.add(Filter.eq("specialty", specialty));
			
			List<SpecialtySpecification> srows=specialtySpecificationServiceImpl.findList(null,filters2,null);
			
			List<Map<String, Object>> rows=filterSpecificationPrice(doSpecialtyList(srows,session));	
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(rows);
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(e);
		}
		return json;		
	}
	
	@RequestMapping(value={"/detail"})
	@ResponseBody
	public Json specialtyDetail(Long id){
		Json json=new Json();
		
		try{
			List<Filter> filters=new ArrayList<Filter>();
			filters.add(new Filter("id",Operator.eq,id));
			List<Specialty> list=specialtyServiceImpl.findList(null, filters, null);
			if(list!=null && !list.isEmpty()){
				json.setSuccess(true);
				json.setMsg("查询成功");
				Specialty s=list.get(0);
				s.setSpecialtiesForRecommendSpecialtyId(null);
				s.setSpecialtiesForSpeciltyId(null);
				json.setObj(s);
			}else{
				json.setSuccess(false);
				json.setMsg("查询失败");
				json.setObj(null);
			}
		}catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(e);
		}
		
		return json;
	}
	
	@RequestMapping("/appraisedetail")
	@ResponseBody
	public Json appraisedetail(Pageable pageable,Long id){
		Json json=new Json();
		try {
			Specialty specialty=specialtyServiceImpl.find(id);
			if(specialty==null){
				json.setSuccess(false);
				json.setMsg("特产不存在");
			}else{
				List<Filter> filters=new ArrayList<>();
				filters.add(Filter.eq("specialty", specialty));
				filters.add(Filter.eq("isShow", true));
				filters.add(Filter.eq("isValid", true));
				
				List<Order> orders=new ArrayList<>();
				orders.add(Order.desc("appraiseTime"));
				pageable.setFilters(filters);
				Page<SpecialtyAppraise> page=specialtyAppraiseService.findPage(pageable);
				HashMap<String, Object> hashMap=new HashMap<>();
				List<HashMap<String, Object>> result=new ArrayList<>();
				for(SpecialtyAppraise tmp:page.getRows()){
					HashMap<String, Object> hm=new HashMap<>();
					hm.put("id", tmp.getId());
					if(tmp.getIsAnonymous()==null||tmp.getIsAnonymous()==true){
						hm.put("wechatName", "*****");
					}else{
						hm.put("wechatName", tmp.getAccount().getWechatName());
					}
					hm.put("appraiseTime", tmp.getAppraiseTime());
					hm.put("appraiseContent", tmp.getAppraiseContent());
					hm.put("contentLevel", tmp.getContentLevel());
					hm.put("images", tmp.getImages());
					hm.put("isAnonymous", tmp.getIsAnonymous());
					result.add(hm);
				}
				hashMap.put("total", page.getTotal());
				hashMap.put("pageNumber", page.getPageNumber());
				hashMap.put("pageSize", page.getPageSize());
				hashMap.put("rows", result);
				json.setSuccess(true);
				json.setMsg("查询成功");
				json.setObj(hashMap);
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	
	@Resource(name= "hyVinboundServiceImpl")
	HyVinboundService hyVinboundService;
	
	/*获取某一规格有效采购批次*/
	private PurchaseItem getValidPurchaseItem(Long specialtySpecificationId){
		SpecialtySpecification specialtySpecification=specialtySpecificationServiceImpl.find(specialtySpecificationId);
		List<Filter> purchaseFilters=new ArrayList<Filter>();
		purchaseFilters.add(new Filter("specification",Filter.Operator.eq,specialtySpecification));
		purchaseFilters.add(new Filter("isValid",Filter.Operator.eq,true));
		purchaseFilters.add(new Filter("state",Filter.Operator.eq,true));
		purchaseFilters.add(Filter.eq("setState", true));
		List<Order> purchaseOrders=new ArrayList<Order>();
		purchaseOrders.add(Order.asc("id"));
		List<PurchaseItem> purchaseItems=purchaseItemServiceImpl.findList(null,purchaseFilters,purchaseOrders);
		if(purchaseItems == null || purchaseItems.isEmpty()){
			return null;
		}
		//获取当前有效批次
		PurchaseItem validPurchaseItem=purchaseItems.get(0);
		
		return validPurchaseItem;
	}
	
	private List<Map<String, Object>> filterSpecificationPrice(List<Map<String, Object>> srows){
		Map<Object,Map<String,Object>> spMap = new HashMap<>();	//特产基本信息Map
		Map<Object, Integer> saMap = new HashMap<>();	//特产销量Map
		for(Map<String, Object> sp : srows){
			//最低价格
			if(spMap.containsKey(sp.get("specialty"))){
				Map<String, Object> tmp = spMap.get(sp.get("specialty"));
				BigDecimal spPrice = (BigDecimal)sp.get("pPrice");
				BigDecimal tmpPrice = (BigDecimal)tmp.get("pPrice");
				if(spPrice.compareTo(tmpPrice)<0){
					spMap.put(sp.get("specialty"), sp);
				}
			}else{
				spMap.put(sp.get("specialty"), sp);
			}
			//总销量
			if(saMap.containsKey(sp.get("specialty"))){
				Integer saleNumber = saMap.get(sp.get("specialty"));
				SpecialtySpecification specification = (SpecialtySpecification)sp.get("specification");
				saMap.put(sp.get("specialty"), saleNumber+specification.getHasSold()*specification.getSaleNumber());
			}else{
				Specialty specialty = (Specialty)sp.get("specialty");
				SpecialtySpecification specification = (SpecialtySpecification)sp.get("specification");
				saMap.put(sp.get("specialty"), specialty.getBaseSaleNumber()+specification.getHasSold()*specification.getSaleNumber());
			}	
		}
		
		List<Map<String, Object>> list = new ArrayList<>();
		for(Entry<Object,Map<String,Object>> entry:spMap.entrySet()){
			Map<String, Object> value = entry.getValue();
			Object key = entry.getKey();
			if(!value.containsKey("hasSold")) {
				value.put("hasSold", saMap.get(key));
			}
			
			//把specialty的specifications列表变成第一个是价格最低的规格
			List<SpecialtySpecification> specifications = ((Specialty)value.get("specialty")).getSpecifications();
			SpecialtySpecification specialtySpecification = (SpecialtySpecification)value.get("specification");
			//如果价格最低的规格不是列表里的第一个，就交换
			if(specifications.size()>0 && !specifications.get(0).getId().equals(specialtySpecification.getId())){
				Map<Long, Integer> specificationIdListIndexMap = new HashMap<>();
				//specificationIdListIndexMap的key是规格id，value是列表的下标
				for(int i=0;i<specifications.size();++i){
					specificationIdListIndexMap.put(specifications.get(i).getId(), i);
				}
				Collections.swap(specifications, specificationIdListIndexMap.get(specialtySpecification.getId()), 0);
			}
			
			list.add(value);
		}
		return list;
		
		
	}
	
	@Resource(name = "wechatAccountServiceImpl")
	WechatAccountService wechatAccountService;
	
	@Resource(name = "hySingleitemPromotionServiceImpl")
	HySingleitemPromotionService hySingleitemPromotionServiceImpl;
	
	  /*重新处理List，获得新的List*/
	  private List<Map<String, Object>> doSpecialtyList(List<SpecialtySpecification> srows,HttpSession session){
			List<Map<String, Object>> rows=new ArrayList<>();
			Map<Long,Integer> hasSolds = new HashMap<>();
			
			for(SpecialtySpecification s:srows){
				Map<String,Object> map=new HashMap<>();
				Specialty specialty = s.getSpecialty();
				if(specialty.getIsActive() == null || !specialty.getIsActive() || 
						specialty.getSaleState() == null || specialty.getSaleState() == 0 || 
						s.getIsActive() == null || !s.getIsActive()){
					continue;
				}
				
//				PurchaseItem purchaseItem=this.getValidPurchaseItem((Long)s.getId());
//				if(purchaseItem == null){	//如果没有有效的采购批次，则不返回
//					continue;
//				}
				//产品信息
				map.put("specialty", specialty);

				//产品图标
				List<SpecialtyImage> images=specialty.getImages();
				for(SpecialtyImage image:images){
					if(image.getIsLogo()!=null && image.getIsLogo()){
						map.put("iconURL", image);
						break;
					}
				}
				//规格信息
				map.put("specification", s);
				
				//找价格
				//先去价格变化表里面查
				List<Filter> priceFilters=new ArrayList<Filter>();
				priceFilters.add(Filter.eq("specification", s));
				priceFilters.add(Filter.eq("isActive", true));
				List<SpecialtyPrice> specialtyPrices=specialtyPriceServiceImpl.findList(null,priceFilters,null);
				
				if(specialtyPrices==null || specialtyPrices.isEmpty()){
					continue;
				}
				SpecialtyPrice price=specialtyPrices.get(0); 
				map.put("mPrice", price.getMarketPrice());
				map.put("pPrice",price.getPlatformPrice());
				//运费
				map.put("deliverPrice",price.getDeliverPrice());
				//找库存
				//找父规格
				SpecialtySpecification fuSpecification = specialtySpecificationServiceImpl.getParentSpecification(s);
				if(fuSpecification==null)
					continue;
				
				Integer totalInbound = fuSpecification.getBaseInbound();
				if(totalInbound == 0){
					 continue;
				}
				map.put("inbound", totalInbound/s.getSaleNumber());
				
//				List<Inbound> inbounds=inboundService.getInboundListBySpecificationId(fuSpecification.getId(), 0);
//				
//				if(inbounds!=null && !inbounds.isEmpty()){
//					for(Inbound inbound:inbounds){
//						totalInbound+=inbound.getInboundNumber();	//真实库存
//					}
//				}
//				
//				List<Filter>filtersV = new ArrayList<>();
//				filtersV.add(Filter.eq("specification", fuSpecification));
//				List<HyVinbound> vinbounds = hyVinboundService.findList(1,filtersV,null);
//				if(vinbounds!=null && !vinbounds.isEmpty()){
//					totalInbound+=vinbounds.get(0).getVinboundNumber();	//虚拟库存
//				}
//				

				
				//找提成比例
				Long weChatId=(Long)session.getAttribute("wechat_id");
				WechatAccount wechatAccount = wechatAccountService.find(weChatId);
				if(wechatAccount!=null) {
					List<Filter> filters = new ArrayList<>();
					filters.add(Filter.eq("wechatOpenId",wechatAccount.getWechatOpenid()));
					List<WeBusiness> weBusinesses = weBusinessService.findList(null,filters,null);
					
					if(weBusinesses!=null && !weBusinesses.isEmpty()){
						WeBusiness weBusiness = weBusinesses.get(0);
						switch (weBusiness.getType()) {
						case 0:
							//找提成模型
							List<Filter> filters4=new ArrayList<>();
							filters4.add(Filter.eq("modelType","虹宇门店"));
							filters4.add(Filter.eq("isValid", true));
							List<WeDivideModel> weDivideModels=weDivideModelService.findList(null,filters4,null);
							map.put("divideRatio", price.getStoreDivide().multiply(weDivideModels.get(0).getWeBusiness()));
							break;
						case 1:
							//找提成模型
							List<Filter> filters5=new ArrayList<>();
							filters5.add(Filter.eq("modelType","非虹宇门店"));
							filters5.add(Filter.eq("isValid", true));
							List<WeDivideModel> weDivideModels1=weDivideModelService.findList(null,filters5,null);
							map.put("divideRatio", price.getExterStoreDivide().multiply(weDivideModels1.get(0).getWeBusiness()));
							break;
						case 2:
							//找提成模型
							List<Filter> filters6=new ArrayList<>();
							filters6.add(Filter.eq("modelType","个人商贸"));
							filters6.add(Filter.eq("isValid", true));
							List<WeDivideModel> weDivideModels2=weDivideModelService.findList(null,filters6,null);
							map.put("divideRatio", price.getBusinessPersonDivide().multiply(weDivideModels2.get(0).getWeBusiness()));
							break;
						default:
							break;
						}
					}else{
						map.put("divideRatio",null);
					}
				}else {
					map.put("divideRatio",null);
				}
				//提成金额
				if(map.get("divideRatio")!=null) {
					map.put("divideMoney", price.getPlatformPrice().subtract(price.getCostPrice()).subtract(
							price.getDeliverPrice()).multiply((BigDecimal)map.get("divideRatio")).setScale(0, BigDecimal.ROUND_DOWN));//最后展示的分成钱数向下取整，舍弃小数
				}
				


				
				//获取推荐产品
				map.put("recommends", s.getSpecialty().getSpecialtiesForRecommendSpecialtyId());
				

				HySingleitemPromotion singleitemPromotion = hySingleitemPromotionServiceImpl.getValidSingleitemPromotion(s.getId());
				
				map.put("promotion", singleitemPromotion);

				//获取标签
				List<HyLabel> hyLabels = specialty.getHyLabel();
				List<HyLabel> tmp = new ArrayList<>(); 
				for(HyLabel label:hyLabels) {
					if(label.getIsActive() && hySpecialtyLabelService.isMarked(label, specialty)) {
						tmp.add(label);
					}
				}
				
				map.put("labels", tmp);
				
				//找销量
				if(hasSolds.containsKey(specialty.getId())) {
					map.put("hasSold", hasSolds.get(specialty.getId()));
				}else {
					Integer hasSold = specialty.getBaseSaleNumber();
					for(SpecialtySpecification specification:specialty.getSpecifications()) {
						hasSold += specification.getSaleNumber()*specification.getHasSold();
					}
					map.put("hasSold", hasSold);
					hasSolds.put(specialty.getId(), hasSold);
				}

				rows.add(map);
			}
			
			return rows;
			
	
	  }
	  
	  
		@Resource(name = "hyLabelServiceImpl")
		HyLabelService hyLabelService;
		
		@Resource(name = "hySpecialtyLabelServiceImpl")
		HySpecialtyLabelService hySpecialtyLabelService;
		
		
		@RequestMapping("/labels/list/view")
		@ResponseBody
		public Json labelsView() {
			Json json = new Json();
			try {
				List<Filter> labelFilters = new ArrayList<>();
				labelFilters.add(Filter.eq("isActive", true));
			  
				List<HyLabel> hyLabels = hyLabelService.findList(null,labelFilters,null);
				List<HyLabel> ans = new ArrayList<>();
				for(HyLabel hyLabel:hyLabels) {
					List<Specialty> specialties = hyLabel.getSpecialtys();
					if(specialties!=null && !specialties.isEmpty()) {
						for(Specialty specialty:specialties) {
							if(hySpecialtyLabelService.isMarked(hyLabel,specialty)) {
								ans.add(hyLabel);
								break;
							}
						}
					}
				}
				json.setSuccess(true);
				json.setMsg("查询成功");
				json.setObj(ans);
			
				} catch (Exception e) {
					// TODO: handle exception
					json.setSuccess(false);
					json.setMsg("查询失败");
					json.setObj(e);
				}
			return json;
		}
	  
		@RequestMapping("/label_specialtys/page/view")
		@ResponseBody
		public Json labelSpecialtysPageView(Pageable pageable,Integer condition,Long id,HttpSession session) {
			Json json = new Json();
			try {
				  String sqlStr = Constants.SQL_MIN_PRICE_SPEC_BY_LABEL;

				  sqlStr += " and sl1.label_id="+id; 

				  
				  sqlStr += " group by p1.id";
				  
				  String sqlTotal = Constants.SQL_MIN_PRICE_SPEC_TOTAL+sqlStr;
				  List totals = specialtyServiceImpl.statis(sqlTotal);
				  Integer total = totals.size();

				  if(condition==null || condition==0){
						//默认排序
				  }else if(condition == 1){
					  sqlStr += " order by hasSold desc";
				  }else if(condition == 2){
					  sqlStr += " order by pPrice asc";
				  }else if(condition == 3){
					  sqlStr += " order by pPrice desc";
				  }	
				  
				  Integer start = (pageable.getPage()-1)*pageable.getRows();
				  start = start>total?total:start;
				  Integer end = start + pageable.getRows();
				  end = end>total?total:end;
				  
				  sqlStr += " limit "+start+","+end;
				  
				  String sqlParams = Constants.SQL_MIN_PRICE_SPEC_PARAMS+sqlStr;
				  
				  List<Object[]> objects = specialtyServiceImpl.statis(sqlParams);
				  String[] keys = new String[] {"spid","spname","sid","sname","pPrice","hasSold","imgUrl"};
				  List<Map<String, Object>> maps = new ArrayList<>();
					
				  for(Object[] object:objects) {
					  maps.add(ArrayHandler.toSpecialtyBaseInfoMap(object));
				  }
		
				Page<Map<String, Object>> pages=new Page<>(maps,total,pageable);
				json.setSuccess(true);
				json.setMsg("查询成功");
				json.setObj(pages);
			
			} catch (Exception e) {
				// TODO: handle exception
			}
			return json;
		}

		  
			@RequestMapping("/sub_list_by_label_id")
			@ResponseBody
			public Json subListByLabelId(Long id,Integer size,HttpSession session) {
				Json json = new Json();
				try {

					String sqlstr = Constants.SQL_MIN_PRICE_SPEC_PARAMS+Constants.SQL_MIN_PRICE_SPEC_BY_LABEL+" and sl1.label_id="+id;
					sqlstr += " group by p1.id";
					sqlstr += " order by hasSold desc";
					sqlstr += " limit 0,"+size;
					List<Object[]> objects = specialtyServiceImpl.statis(sqlstr);
					String[] keys = new String[] {"spid","spname","sid","sname","pPrice","hasSold","imgUrl"};
					List<Map<String, Object>> maps = new ArrayList<>();
					
					for(Object[] object:objects) {
						maps.add(ArrayHandler.toSpecialtyBaseInfoMap(object));
					}
					
					

					json.setSuccess(true);
					json.setMsg("查询成功");
					json.setObj(maps);
				
				} catch (Exception e) {
					// TODO: handle exception
				}
				return json;
			}	  
	  

}

