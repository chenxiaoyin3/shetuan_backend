package com.hongyu.controller;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.Filter.Operator;
import com.hongyu.entity.BusinessBanner;
import com.hongyu.entity.CommonSequence;
import com.hongyu.entity.BusinessBanner.BannerType;
import com.hongyu.entity.CommonSequence.SequenceTypeEnum;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyArea;
import com.hongyu.entity.Provider;
import com.hongyu.entity.Specialty;
import com.hongyu.entity.SpecialtyAppraise;
import com.hongyu.entity.SpecialtyAppraiseImage;
import com.hongyu.entity.SpecialtyCategory;
import com.hongyu.entity.SpecialtyImage;
import com.hongyu.entity.SpecialtyPrice;
import com.hongyu.entity.SpecialtySpecification;
import com.hongyu.entity.WeDivideProportion;
import com.hongyu.entity.WechatAccount;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.entity.HyVinbound;
import com.hongyu.entity.Inbound;
import com.hongyu.service.BusinessBannerService;
import com.hongyu.service.CommonSequenceService;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyAreaService;
import com.hongyu.service.HyVinboundService;
import com.hongyu.service.InboundService;
import com.hongyu.service.PointrecordService;
import com.hongyu.service.ProviderService;
import com.hongyu.service.SpecialtyAppraiseService;
import com.hongyu.service.SpecialtyCategoryService;
import com.hongyu.service.SpecialtyPriceService;
import com.hongyu.service.SpecialtyService;
import com.hongyu.service.SpecialtySpecificationService;
import com.hongyu.service.WeDivideProportionService;
import com.hongyu.service.WechatAccountService;
import com.hongyu.util.AuthorityUtils;
import com.hongyu.util.SpecialtySNGenerator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.dialect.function.TrimFunctionTemplate.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping({"/admin/business/product"})
public class SpecialtyController
{
  @Resource(name="specialtyServiceImpl")
  SpecialtyService specialtyServiceImpl;
  
  @Resource(name = "specialtyCategoryServiceImpl")
  SpecialtyCategoryService specialtyCategoryServiceImpl;
  
  @Resource(name = "providerServiceImpl")
  ProviderService providerServiceImpl;
  
  @Resource(name = "specialtySpecificationServiceImpl")
  SpecialtySpecificationService specialtySpecificationSrv;
  
  @Resource(name = "hyAreaServiceImpl")
  HyAreaService areaServiceImpl;
  
  @Resource(name = "specialtyAppraiseServiceImpl")
  SpecialtyAppraiseService specialtyAppraiseServiceImpl;
  
  @Resource(name="hyAdminServiceImpl")
  private HyAdminService hyAdminService;
  
  @Resource(name="departmentServiceImpl")
  private DepartmentService departmentService;
  
  @Resource(name="wechatAccountServiceImpl")
  WechatAccountService wechatAccountService;

  @Resource(name = "specialtyPriceServiceImpl")
  SpecialtyPriceService specialtyPriceSrv;
  
  @Resource(name="businessBannerServiceImpl")
  private BusinessBannerService businessBannerService;
  
  @Resource(name="hyVinboundServiceImpl")
  private HyVinboundService hyVinboundService;
  
  @Resource(name="weDivideProportionServiceImpl")
  WeDivideProportionService proportionSrv;
  
  @Resource(name="inboundServiceImpl")
  InboundService inboundServiceImpl;
  
  @Resource(name="commonSequenceServiceImp")
  CommonSequenceService commonSequenceService;
  
  private static SimpleDateFormat yyyy_MM_dd_HH_mm_ss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  private static SimpleDateFormat yyyy_MM_dd = new SimpleDateFormat("yyyy-MM-dd");
  private static Object lock = new Object();
  
  static class WrapSpecialty {
	  Specialty specialty;
//	  SpecialtyImage iconUrl;
	
//	  List<SpecialtySpecification> specifications;
	  Long[] recommendIds;
//	  Long providerId;
	  public Specialty getSpecialty() {
		  return specialty;
	  }
	  public void setSpecialty(Specialty specialty) {
		  this.specialty = specialty;
	  }
//	public SpecialtyImage getIconUrl() {
//		return iconUrl;
//	}
//	public void setIconUrl(SpecialtyImage iconUrl) {
//		this.iconUrl = iconUrl;
//	}
	public Long[] getRecommendIds() {
		return recommendIds;
	}
	public void setRecommendIds(Long[] recommendIds) {
		this.recommendIds = recommendIds;
	}
	  
	  
  }
  
  
  @RequestMapping(value = "/page/view")
  @ResponseBody
  public Json specialtyList(Specialty specialty, Pageable pageable, Long categoryid, Long providerid, HttpSession session, HttpServletRequest request)
  {	
	  	Json json = new Json();
	  	List<Filter> filters = new ArrayList<Filter>();
	  	
		/**
		 * 获取当前用户
		 */
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin admin = hyAdminService.find(username);
		
		/** 
		 * 获取用户权限范围
		 */
		CheckedOperation co = (CheckedOperation) request.getAttribute("co");
		
		/** 所有符合条件的账号 ,默认可以看到自己创建的数据 */
		Set<HyAdmin> hyAdmins = AuthorityUtils.getAdmins(session, request);
		
		filters.add(Filter.in("creator", hyAdmins));
		
	  try {
		  SpecialtyCategory category = null;
		  if (categoryid != null) {
			  category = specialtyCategoryServiceImpl.find(categoryid);
			  if (category != null) {
				  specialty.setCategory(category);
			  }
		  }
		  
		  if (StringUtils.isNotEmpty(specialty.getName())) {
		      
		      Filter filter = new Filter("name", Operator.like, specialty.getName());
		      filters.add(filter);
		      specialty.setName(null);
		  }
		  
		  if (providerid != null) {
			  Provider provider = providerServiceImpl.find(providerid);
			  if (provider == null) {
				  Page<Specialty> page1 = new Page<>(new ArrayList<Specialty>(), 0, pageable);
				  json.setSuccess(true);
				  json.setMsg("查询成功");
				  json.setObj(page1);
			  } else {
				  filters.add(Filter.eq("provider", provider));
			  }
			  
		  }
		  
		  pageable.setFilters(filters);
		  List<Order> orders = new ArrayList<Order>();
		  orders.add(Order.desc("id"));
		  pageable.setOrders(orders);
		  Page<Specialty> page = specialtyServiceImpl.findPage(pageable, specialty);
		  for (Specialty s : page.getRows()) {
			  s.setSpecialtiesForRecommendSpecialtyId(null);
			  s.setSpecialtiesForSpeciltyId(null);
			  Long count = specialtyAppraiseServiceImpl.count(Filter.eq("specialty", s));
			  s.setAppraiseCount(count);
			  
			  HyAdmin creator = s.getCreator();
			  /** 当前用户对本条数据的操作权限 */
	  		  if(creator.equals(admin)){
	  			  if(co == CheckedOperation.view) {
	  				  s.setPrivilege("view");
	  			  } else {
	  				s.setPrivilege("edit");
	  			  }
	  		  } else{
	  			  if(co == CheckedOperation.edit) {
	  				s.setPrivilege("edit");
	  			  } else {
	  				s.setPrivilege("view");
	  			  }
	  		  }
		  }
		  json.setSuccess(true);
		  json.setMsg("查询成功");
		  json.setObj(page);
	  } catch (Exception e) {
		  json.setSuccess(false);
		  json.setMsg("查询失败");
		  json.setObj(e);
	  }
      return json;
  }

    @RequestMapping("/detail/view")
    @ResponseBody
    public Json specialtyDetail(Long id)
    {	
  	  Json json = new Json();
  	
  	  try {
  		  List<Filter> filters = new ArrayList<Filter>();
  		  filters.add(new Filter("id", Operator.eq, id));
  		  List<Specialty> list = specialtyServiceImpl.findList(1, filters, null);
  		  if (list.size() > 0) {
  			json.setSuccess(true);
  			json.setMsg("查询成功");
  			Specialty s = list.get(0);
  			for (int i = 0; i < s.getImages().size(); i++) {
  				SpecialtyImage image = s.getImages().get(i);
  				if (image.getIsLogo()) {
  					s.setIcon(image);
  					s.getImages().remove(i);
  					break;
  				}
  			}
  			List<SpecialtySpecification> filterSpecifications = new ArrayList<SpecialtySpecification>();
  			for (int i = 0; i < s.getSpecifications().size(); i++) {
  				SpecialtySpecification spe = s.getSpecifications().get(i);
  				// 不展示无效的商品子规格
  				if ((spe.getParent()!=0) && !spe.getIsActive())
  					continue;
//  				if (spe.getIsActive() != null && spe.getIsActive() == true) {
				List<Filter> fs = new ArrayList<Filter>();
  				fs.add(Filter.eq("specification", spe));
  				fs.add(Filter.eq("isActive", true));	//wayne-180918
  				List<SpecialtyPrice> prices = specialtyPriceSrv.findList(null, fs, null);
  				if (prices.size()>0) {
  					spe.setCostPrice(prices.get(0).getCostPrice());
  					spe.setMarketPrice(prices.get(0).getMarketPrice());
  					spe.setPlatformPrice(prices.get(0).getPlatformPrice());
  					spe.setBusinessPersonDivide(prices.get(0).getBusinessPersonDivide());
  					spe.setStoreDivide(prices.get(0).getStoreDivide());
  					spe.setExterStoreDivide(prices.get(0).getExterStoreDivide());
  					spe.setDeliverPrice(prices.get(0).getDeliverPrice());
  					if (spe.getParent().equals(Long.valueOf(0))) {
  						List<Filter> fls = new ArrayList<Filter>();
  						fls.add(Filter.eq("specification", spe));
  						List<HyVinbound> vinbounds = hyVinboundService.findList(null, fls, null);
  						if (vinbounds.size() > 0) {
  							spe.setvInboundNumber(vinbounds.get(0).getVinboundNumber());
  						}
  					}
  				}
  			    filterSpecifications.add(spe);
//  			}
  				
  			}
  			s.setSpecialtiesForRecommendSpecialtyId(null);
  			s.setSpecialtiesForSpeciltyId(null);
  			//设置特产的规格集合为过滤掉isActive为false的集合
  			s.setSpecifications(filterSpecifications);
  			json.setObj(s);
  		  } else {
  			json.setSuccess(false);
  			json.setMsg("查询失败");
  			json.setObj(null);
  		  }
  		  
//		  if (specialty == null) {
//			  json.setMsg("特产不存在");
//			  json.setSuccess(false);
//			  json.setObj(null);
//		  } else {
//			  specialty.setSpecialtiesForRecommendSpecialtyId(null);
//			  specialty.setSpecialtiesForSpeciltyId(null);
//			  json.setSuccess(true);
//			  json.setMsg("查询成功");
//			  json.setObj(specialty);
//		  }
	} catch (Exception e) {
		json.setSuccess(false);
		json.setMsg("查询失败");
		json.setObj(null);
		e.printStackTrace();
	}
  	  
      return json;
    }
    
    @RequestMapping("/add")
    @ResponseBody
    public Json specialtyAdd(@RequestBody WrapSpecialty wrapSpecialty, HttpSession session)
    {	
  	  Json json = new Json();
  	  
  	  /**
		* 获取当前用户
		*/
  	  String username = (String) session.getAttribute(CommonAttributes.Principal);
  	  HyAdmin admin = hyAdminService.find(username);
  	  
  	  try {
  		  Specialty specialty = wrapSpecialty.getSpecialty();
  		  if (specialty.getSpecifications()  != null) {
  			  for (SpecialtySpecification specification : specialty.getSpecifications()) {
  				  specification.setSpecialty(specialty);
  				  specification.setCreatorName(admin.getName());
  			  }
  		  }
  		  if (specialty.getCategory() != null) {
  			  if (specialty.getCategory().getId() != null) {
  				SpecialtyCategory category = specialtyCategoryServiceImpl.find(specialty.getCategory().getId());
  				if (category == null) {
  					json.setSuccess(false);
  		  		  	json.setMsg("所选产品分区不存在");
  		  		  	json.setObj(null);
  		  		  	return json;
  				}
    			specialty.setCategory(category);
  			  }  
  		  }
  		  
  		  //设置推荐产品
//  		  if (specialty.getSpecialtiesForRecommendSpecialtyId() != null && specialty.getSpecialtiesForRecommendSpecialtyId().size() > 0) {
//  			  Long[] ids = new Long[specialty.getSpecialtiesForRecommendSpecialtyId().size()];
//  			  for (int i = 0; i < specialty.getSpecialtiesForRecommendSpecialtyId().size(); i++) {
//  				  ids[i] = specialty.getSpecialtiesForRecommendSpecialtyId().get(i).getId();
//  			  }	  
//  			  List<Specialty> recommends = specialtyServiceImpl.findList(ids);
//  			  specialty.setSpecialtiesForRecommendSpecialtyId(recommends);
//  		  }
  		  //设置供应商
  		  if (specialty.getProvider() != null) {
  			  if (specialty.getProvider().getId() != null) {
  				Provider provider = providerServiceImpl.find(specialty.getProvider().getId());
  				specialty.setProvider(provider);
  			  }  
  		  }
  		  if (specialty.getArea() != null) {
  			  if (specialty.getArea().getId() != null) {
  				  specialty.setArea(areaServiceImpl.find(specialty.getArea().getId()));
  			  }	  
  		  }
  		  
  		  if (specialty.getImages() != null) {
  			  
  			  for (SpecialtyImage image : specialty.getImages()) {
  				  image.setSpecialty(specialty);
  				  image.setIsLogo(false);
  			  }
  			  
  			  //将icon加入到images中
  			  if (specialty.getIcon() != null) {
				  SpecialtyImage icon = specialty.getIcon();
				  icon.setIsLogo(true);
				  icon.setSpecialty(specialty);
				  specialty.getImages().add(icon);
			  }
  		  }
  		  specialty.setCreator(admin);
  		  specialty.setCreatorName(admin.getName());
  		  synchronized (lock) {
  			  List<Filter> fs = new ArrayList<Filter>();
  			  fs.add(Filter.in("type", SequenceTypeEnum.specialtySn));
  			  List<CommonSequence> ss = commonSequenceService.findList(null, fs, null);
  			  CommonSequence c = ss.get(0);
  			  Long value = c.getValue() + 1;
  			  c.setValue(value);
  			  commonSequenceService.update(c);
  			  specialty.setCode(SpecialtySNGenerator.getOrderSN(specialty.getCategory().getId(), value));
		  }
  		  
  		  
  		  
  		  
  		  specialtyServiceImpl.save(specialty);
//  		  if (specialty.getIsBanner()) {
//			  BusinessBanner banner = new BusinessBanner();
//			  banner.setTitle(specialty.getName());
//			  banner.setType(BannerType.产品);
//			  banner.setImg(specialty.getIcon().getSourcePath());
//			  banner.setCreator(admin);
//			  banner.setTargetId(specialty.getId());
//			  banner.setStartTime(specialty.getPutonTime());
//			  banner.setEndTime(specialty.getPutoffTime());
//			  banner.setState(true);
//			  businessBannerService.save(banner);
//		  }
  		  json.setSuccess(true);
  		  json.setMsg("添加成功");
  		  json.setObj(null);
	  } catch (Exception e) {
		  json.setSuccess(false);
  		  json.setMsg("添加失败");
  		  json.setObj(e);
  		  e.printStackTrace();
	  }

      return json;
    }
    
    @RequestMapping("/modify")
    @ResponseBody
//    @Transactional(propagation=Propagation.REQUIRED)
    public Json specialtyModify(@RequestBody WrapSpecialty wrapSpecialty, HttpSession session)
    {	
    	 Json json = new Json();
    	 
    	 /**
 		* 获取当前用户
 		*/
   	  	String username = (String) session.getAttribute(CommonAttributes.Principal);
   	  	HyAdmin admin = hyAdminService.find(username);
     	  
     	  try {
     		  
     		  Specialty specialty = wrapSpecialty.getSpecialty();
     		  Specialty oldSpecialty = specialtyServiceImpl.find(specialty.getId());
     		  
    		  oldSpecialty.setName(specialty.getName());
    		  oldSpecialty.setBrand(specialty.getBrand());
    		  oldSpecialty.setOriginalPlace(specialty.getOriginalPlace());
    		  oldSpecialty.setProductionLicenseNumber(specialty.getProductionLicenseNumber());
    		  oldSpecialty.setStorageMethod(specialty.getStorageMethod());
    		  oldSpecialty.setDescriptions(specialty.getDescriptions());
     		  
//     		  if (specialty.getSpecifications().size() > 0) {
//     			 List<SpecialtySpecification> oldSpecifications = oldSpecialty.getSpecifications();
//     			  for (SpecialtySpecification specification : specialty.getSpecifications()) {
//     				  //主控方应该维护双方关系
//     				  for (SpecialtySpecification oldSpecification : oldSpecifications) {
//     					  if (oldSpecification.getId() == specification.getId()) {
//     						 specification.setCreateTime(oldSpecification.getCreateTime());
//     						 specification.setIsActive(oldSpecification.getIsActive());
//     						 specification.setCreatorName(oldSpecification.getCreatorName());
//     						 specification.setHasSold(oldSpecification.getHasSold());
//     						 specification.setIsFreeGift(oldSpecification.getIsFreeGift());
//     						 specification.setModifierName(admin.getName());
//     						 break;
//     					  }
//     				  }
//     				  specification.setSpecialty(oldSpecialty);
//     			  }
//     			 oldSpecialty.getSpecifications().clear();
//     			 oldSpecialty.getSpecifications().addAll(specialty.getSpecifications());
//     		 }
    		  
    		  if (specialty.getSpecifications().size() > 0) {
    			  Set<Long> newIdS = new HashSet<Long>();
    			  for (SpecialtySpecification s : specialty.getSpecifications()) {
    				  if (s.getId() != null) {
    					  newIdS.add(s.getId());
    				  } 
    			  }
    			  List<SpecialtySpecification> newSpecifications = new ArrayList<SpecialtySpecification>();
      			  List<SpecialtySpecification> oldSpecifications = oldSpecialty.getSpecifications();
      			  for (SpecialtySpecification specification : specialty.getSpecifications()) {
      				  
      				  if (specification.getId() != null) {
      					for (SpecialtySpecification oldSpecification : oldSpecifications) {
        					  if (oldSpecification.getId().equals(specification.getId())) {
        						 SpecialtySpecification newSpecification = new SpecialtySpecification(oldSpecification);
        						 newSpecification.setParent(specification.getParent());
        						 newSpecification.setSpecification(specification.getSpecification());
        						 newSpecification.setSpecialty(specialty);
        						 newSpecification.setSaleNumber(specification.getSaleNumber());

        						 newSpecification.setBaseInbound(oldSpecification.getBaseInbound()==null?0:oldSpecification.getBaseInbound());
        						 
        						 newSpecifications.add(newSpecification);
        						 break;
        					  }
        				  }
      				  } else {
      					 SpecialtySpecification newSpecification = new SpecialtySpecification();
      					 newSpecification.setParent(specification.getParent());
						 newSpecification.setSpecification(specification.getSpecification());
						 newSpecification.setSaleNumber(specification.getSaleNumber());
						 
						 newSpecification.setBaseInbound(specification.getBaseInbound()==null?0:specification.getBaseInbound());
						//主控方应该维护双方关系
						 newSpecification.setSpecialty(oldSpecialty);
						 newSpecifications.add(newSpecification);
      				  }  
      			  }
      			  for (SpecialtySpecification oldSpecification : oldSpecifications) {
      				  if (!newIdS.contains(oldSpecification.getId())) {
      					SpecialtySpecification newSpecification = new SpecialtySpecification(oldSpecification);
      					newSpecification.setIsActive(false);
      					newSpecifications.add(newSpecification);
      					// modify at 2019/04/28, 不清除有效的价格
//      					List<Filter> priceFilters = new ArrayList<>();
//      					priceFilters.add(Filter.eq("specification", oldSpecification));
//      					priceFilters.add(Filter.eq("isActive", true));
//      					List<SpecialtyPrice> prices = specialtyPriceSrv.findList(null, priceFilters, null);
//      					if (prices.size() > 0) {
//      						SpecialtyPrice pri = prices.get(0);
//      						pri.setIsActive(false);
//      						specialtyPriceSrv.save(pri);
//      					}
      				  }
      			  }
      			 oldSpecialty.getSpecifications().clear();
      			 oldSpecialty.getSpecifications().addAll(newSpecifications);
      		 }
     		 if (specialty.getCategory() != null) {
     			  if (specialty.getCategory().getId() != null) {
     				SpecialtyCategory category = specialtyCategoryServiceImpl.find(specialty.getCategory().getId());
     				if (category == null) {
     					json.setSuccess(false);
     		  		  	json.setMsg("所选产品分区不存在");
     		  		  	json.setObj(null);
     		  		  	return json;
     				}
     				oldSpecialty.setCategory(category);
     			  }  
     		  }
//     		  //设置推荐产品
//     		  if (specialty.getSpecialtiesForRecommendSpecialtyId() != null && specialty.getSpecialtiesForRecommendSpecialtyId().size() > 0) {
//     			  Long[] ids = new Long[specialty.getSpecialtiesForRecommendSpecialtyId().size()];
//     			  for (int i = 0; i < specialty.getSpecialtiesForRecommendSpecialtyId().size(); i++) {
//     				  ids[i] = specialty.getSpecialtiesForRecommendSpecialtyId().get(i).getId();
//     			  }	  
//     			  List<Specialty> recommends = specialtyServiceImpl.findList(ids);
//     			  specialty.setSpecialtiesForRecommendSpecialtyId(recommends);
//     		  }
     		  //设置供应商
     		  if (specialty.getProvider() != null) {
     			  if (specialty.getProvider().getId() != null) {
     				Provider provider = providerServiceImpl.find(specialty.getProvider().getId());
     				oldSpecialty.setProvider(provider);
     			  }  
     		  }
     		  if (specialty.getArea() != null) {
     			  if (specialty.getArea().getId() != null) {
     				 oldSpecialty.setArea(areaServiceImpl.find(specialty.getArea().getId()));
     			  }	  
     		  }
     		  
     		 if (specialty.getImages() != null) {
     			  oldSpecialty.getImages().clear();
     			  for (SpecialtyImage image : specialty.getImages()) {
     				  image.setSpecialty(oldSpecialty);
     				  image.setIsLogo(false);
     			  }
     			  
     			//将icon加入到images中
      			  if (specialty.getIcon() != null) {
    				  SpecialtyImage icon = specialty.getIcon();
    				  icon.setIsLogo(true);
    				  icon.setSpecialty(oldSpecialty);
    				  specialty.getImages().add(icon);
    			  }
      			  oldSpecialty.getImages().addAll(specialty.getImages());
     		  }
     		 
//     		  if (!(oldSpecialty.getIsBanner() && specialty.getIsBanner())) {
//     			  if (oldSpecialty.getIsBanner()) {
//     				  oldSpecialty.setIsBanner(specialty.getIsBanner());
//     				  List<Filter> fils = new ArrayList<Filter>();
//     				  fils.add(Filter.eq("targetId", oldSpecialty.getId()));
//     				  List<BusinessBanner> banners = businessBannerService.findList(null, fils, null);
//     				  if (banners.size() > 0) {
//     					  BusinessBanner banner = banners.get(0);
//     					  banner.setState(!banner.getState());
//     					 businessBannerService.update(banner);
//     				  }
//     			  } else {
//     				 oldSpecialty.setIsBanner(specialty.getIsBanner());
//     				 List<Filter> fils = new ArrayList<Filter>();
//    				  fils.add(Filter.eq("targetId", oldSpecialty.getId()));
//    				  List<BusinessBanner> banners = businessBannerService.findList(null, fils, null);
//    				  if (banners.size() > 0) {
//    					  BusinessBanner banner = banners.get(0);
//    					  banner.setState(!banner.getState());
//    					 businessBannerService.update(banner);
//    				  } else {
//    					  BusinessBanner banner = new BusinessBanner();
//         				  banner.setTitle(oldSpecialty.getName());
//         				  banner.setType(BannerType.产品);
//         				  banner.setImg(oldSpecialty.getIcon().getSourcePath());
//         				  banner.setCreator(admin);
//         				  banner.setTargetId(oldSpecialty.getId());
//         				  banner.setStartTime(oldSpecialty.getPutonTime());
//         				  banner.setEndTime(oldSpecialty.getPutoffTime());
//         				  banner.setState(true);
//         				  businessBannerService.save(banner);
//    				  }
//     				    
//     			  }
//     		  }
     		  
     		  specialtyServiceImpl.update(oldSpecialty);
     		  
     		  
     		  json.setSuccess(true);
     		  json.setMsg("修改成功");
     		  json.setObj(null);
   	  } catch (Exception e) {
   		  json.setSuccess(false);
     		  json.setMsg("修改失败");
     		  json.setObj(e);
   	  }

         return json;
    }

    
    @RequestMapping("/qudao/modify")
    @ResponseBody
//    @Transactional(propagation=Propagation.REQUIRED)
    public Json specialtyQudaoModify(@RequestBody WrapSpecialty wrap, HttpSession session)
    {	
    	  Json json = new Json();
    	  /**
    	  * 获取当前用户
    	  */
   	  	  String username = (String) session.getAttribute(CommonAttributes.Principal);
   	  	  HyAdmin admin = hyAdminService.find(username);
     	  
     	  try {
     		  Specialty specialty = wrap.getSpecialty();
//     		  Set<Long> toChangedIds = new HashSet<>();
//     		  for (SpecialtySpecification specification : specialty.getSpecifications()) {
//     			  toChangedIds.add(specification.getId());
//     		  }
     		  Specialty oldSpecialty = specialtyServiceImpl.find(specialty.getId());
     		  oldSpecialty.setOrders(specialty.getOrders());
     		  oldSpecialty.setIsRecommend(specialty.getIsRecommend());
     		  oldSpecialty.setIsReturnable(specialty.getIsReturnable());
     		  oldSpecialty.setSaleState(specialty.getSaleState());
     		  oldSpecialty.setPutonTime(specialty.getPutonTime());
     		  oldSpecialty.setPutoffTime(specialty.getPutoffTime());
     		  oldSpecialty.setCouponAvailable(specialty.getCouponAvailable());
     		  oldSpecialty.setDeliverType(specialty.getDeliverType());
     		  oldSpecialty.setShipType(specialty.getShipType());
//     		  oldSpecialty.setIsBanner(specialty.getIsBanner());
     		  oldSpecialty.setIsActive(specialty.getIsActive());
     		  oldSpecialty.setRecommendOrder(specialty.getRecommendOrder());
     		  oldSpecialty.setBaseSaleNumber(specialty.getBaseSaleNumber());
     		  
     		  
     		  //修改规格价格
//     		  List<Filter> fs = new ArrayList<Filter>();
//     		  fs.add(Filter.in("specification", oldSpecialty.getSpecifications()));
//     		  fs.add(Filter.eq("isActive", true));
//     		  List<SpecialtyPrice> prices = specialtyPriceSrv.findList(null, fs, null);
     		  
     		  if (specialty.getSpecifications().size() > 0) {
     			  for (SpecialtySpecification specification : specialty.getSpecifications()) {
     				 if(specification.getBaseInbound()==null) {
							 specification.setBaseInbound(0);
						 }
     				  for (SpecialtySpecification spe : oldSpecialty.getSpecifications()) {
     					  

     					  //
     					  if (spe.getId().equals(specification.getId())) {
     						  
               				 if(spe.getBaseInbound()==null) {
               					spe.setBaseInbound(0);
          					  }
               				 
     						  //只有父规格才考虑虚拟库存
     						  if (spe.getParent().equals(Long.valueOf(0))) {
     							 HyVinbound hyVinbound = null;
        						  List<Filter> vInboundFilters = new ArrayList<Filter>();
        						  vInboundFilters.add(Filter.eq("specification", spe));
        						  List<HyVinbound> hyVinbounds = hyVinboundService.findList(null, vInboundFilters, null);
        						  //渠道销售修改时才考虑新建虚拟库存或者修改虚拟库存
        						  if (hyVinbounds.size() > 0) {
        							 hyVinbound = hyVinbounds.get(0);
        							 
        							 
        							 //修改虚拟库存的坤良
        							 if (!hyVinbound.getVinboundNumber().equals(specification.getvInboundNumber())) {
        								//修改基本库存*wayne*2018/07/11
        								 if(specification.getBaseInbound()==null) {
        									 specification.setBaseInbound(0);
        								 }
            							 specification.setBaseInbound(spe.getBaseInbound()-
            									 hyVinbound.getVinboundNumber()+specification.getvInboundNumber());
            							
            							 
        								hyVinbound.setVinboundNumber(specification.getvInboundNumber());
        								hyVinboundService.update(hyVinbound);
        							 }else {
        								 specification.setBaseInbound(spe.getBaseInbound());
        							 }
        						  } else {
      								//修改基本库存*wayne*2018/07/11
        							  if(specification.getBaseInbound()==null) {
     									 specification.setBaseInbound(0);
     								 }
         							 specification.setBaseInbound(spe.getBaseInbound()+specification.getvInboundNumber());
         					
         							
        							  hyVinbound = new HyVinbound();
        							  hyVinbound.setSpecification(spe);
        							  hyVinbound.setVinboundNumber(specification.getvInboundNumber());
        							  hyVinboundService.save(hyVinbound);
        						  }
     						  }
     						  
     						  spe.setCostPrice(specification.getCostPrice());
     						  spe.setPlatformPrice(specification.getPlatformPrice());
     						  spe.setMarketPrice(specification.getMarketPrice());
     						  spe.setExterStoreDivide(specification.getExterStoreDivide());
     						  spe.setStoreDivide(specification.getStoreDivide());
     						  spe.setBusinessPersonDivide(specification.getBusinessPersonDivide());
     						  spe.setDeliverPrice(specification.getDeliverPrice());
     						  spe.setIsActive(specification.getIsActive());
     						  spe.setIsFreeGift(specification.getIsFreeGift());
     						  spe.setvInboundNumber(specification.getvInboundNumber());
     						  spe.setBaseInbound(specification.getBaseInbound());
     						  if(spe.getIsFreeGift() != null && specification.getIsFreeGift() != null) {
     							 if(spe.getIsFreeGift() && !specification.getIsFreeGift()) {
        							  int no = specialty.getNumberOfFreeGift();
        							  no--;
        							  specialty.setNumberOfFreeGift(no);
        						  } else if (!spe.getIsFreeGift() && specification.getIsFreeGift()) {
        							  int no = oldSpecialty.getNumberOfFreeGift();
        							  no++;
        							  oldSpecialty.setNumberOfFreeGift(no);
        						  }
     						  }
//     						  spe.setIsFreeGift(specification.getIsFreeGift());
     						  spe.setSpecification(specification.getSpecification());
//     						  spe.setHasSold(specification.getHasSold());
//     						  spe.setIsActive(specification.getIsActive());
     						  spe.setModifierName(username);
     						  break;
     					  }
     				  }
     			  }
     		  }
     		  
//     		  
//     		 if (specialty.getCategory() != null) {
//     			  if (specialty.getCategory().getId() != null) {
//     				SpecialtyCategory category = specialtyCategoryServiceImpl.find(specialty.getCategory().getId());
//     				if (category == null) {
//     					json.setSuccess(false);
//     		  		  	json.setMsg("所选产品分区不存在");
//     		  		  	json.setObj(null);
//     		  		  	return json;
//     				}
//       			specialty.setCategory(category);
//     			  }  
//     		  }
     		  //设置推荐产品
     		  Long[] recommendIds = wrap.getRecommendIds();
     		  if (recommendIds == null || recommendIds.length == 0) {
     			 oldSpecialty.getSpecialtiesForRecommendSpecialtyId().clear();
     		  } else {
     			 List<Specialty> recommends = specialtyServiceImpl.findList(recommendIds);
     			 oldSpecialty.getSpecialtiesForRecommendSpecialtyId().clear();
     			 oldSpecialty.getSpecialtiesForRecommendSpecialtyId().addAll(recommends);
     		  }
//     		  //设置供应商
//     		  if (specialty.getProvider() != null) {
//     			  if (specialty.getProvider().getId() != null) {
//     				Provider provider = providerServiceImpl.find(specialty.getProvider().getId());
//     				specialty.setProvider(provider);
//     			  }  
//     		  }
     		  if (specialty.getArea() != null) {
     			  if (specialty.getArea().getId() != null) {
     				oldSpecialty.setArea(areaServiceImpl.find(specialty.getArea().getId()));
     			  }	  
     		  }
//     		  
//     		 if (specialty.getImages() != null) {
//     			  for (SpecialtyImage image : specialty.getImages()) {
//     				  image.setSpecialty(specialty);
//     			  }
//     		  }
     		  //修改为false
     		 if (oldSpecialty.getIsBanner() && !specialty.getIsBanner()) {
     			 oldSpecialty.setIsBanner(specialty.getIsBanner());
				  List<Filter> fils = new ArrayList<Filter>();
				  fils.add(Filter.eq("targetId", oldSpecialty.getId()));
				  fils.add(Filter.eq("type", BannerType.产品));
				  List<BusinessBanner> banners = businessBannerService.findList(null, fils, null);
				  if (banners.size() > 0) {
					  BusinessBanner banner = banners.get(0);
					  banner.setState(false);
					  businessBannerService.update(banner);
				  }
    		  } else if (!oldSpecialty.getIsBanner() && specialty.getIsBanner()) {
    			  oldSpecialty.setIsBanner(specialty.getIsBanner());
				  List<Filter> fils = new ArrayList<Filter>();
				  fils.add(Filter.eq("targetId", oldSpecialty.getId()));
				  fils.add(Filter.eq("type", BannerType.产品));
				  List<BusinessBanner> banners = businessBannerService.findList(null, fils, null);
				  
				  if (banners.size() > 0) {
					  BusinessBanner banner = banners.get(0);
					  banner.setState(true);
					  banner.setStartTime(specialty.getPutonTime());
					  banner.setEndTime(specialty.getPutoffTime());
					  banner.setCreator(admin);
					  businessBannerService.update(banner);
				  } else {
					  BusinessBanner banner = new BusinessBanner();
    				  banner.setTitle(oldSpecialty.getName());
    				  banner.setType(BannerType.产品);
    				  for (SpecialtyImage image : oldSpecialty.getImages()) {
    					  if (!image.getIsLogo()) {
    						  banner.setImg(image.getSourcePath());
    						  break;
    					  }
    				  }
    				  banner.setCreator(admin);
    				  banner.setTargetId(oldSpecialty.getId());
    				  banner.setStartTime(specialty.getPutonTime());
    				  banner.setEndTime(specialty.getPutoffTime());
    				  banner.setState(true);
    				  businessBannerService.save(banner);
				  }
    		  }
     		 
//     		 if (!(oldSpecialty.getIsBanner() && specialty.getIsBanner())) {
//    			  if (oldSpecialty.getIsBanner()) {
//    				  oldSpecialty.setIsBanner(specialty.getIsBanner());
//    				  List<Filter> fils = new ArrayList<Filter>();
//    				  fils.add(Filter.eq("targetId", oldSpecialty.getId()));
//    				  List<BusinessBanner> banners = businessBannerService.findList(null, fils, null);
//    				  if (banners.size() > 0) {
//    					  BusinessBanner banner = banners.get(0);
//    					  banner.setState(!banner.getState());
//    					 businessBannerService.update(banner);
//    				  }
//    			  } else {
//    				 oldSpecialty.setIsBanner(specialty.getIsBanner());
//    				 List<Filter> fils = new ArrayList<Filter>();
//   				  	 fils.add(Filter.eq("targetId", oldSpecialty.getId()));
//   				  	 List<BusinessBanner> banners = businessBannerService.findList(null, fils, null);
//   				  	 if (banners.size() > 0) {
//   					     BusinessBanner banner = banners.get(0);
//   					     banner.setState(!banner.getState());
//   					     businessBannerService.update(banner);
//   				     } else {
//   					     BusinessBanner banner = new BusinessBanner();
//        				 banner.setTitle(oldSpecialty.getName());
//        				 banner.setType(BannerType.产品);
//        				 banner.setImg(oldSpecialty.getIcon().getSourcePath());
//        				 banner.setCreator(admin);
//        				 banner.setTargetId(oldSpecialty.getId());
//        				 banner.setStartTime(oldSpecialty.getPutonTime());
//        				 banner.setEndTime(oldSpecialty.getPutoffTime());
//        				 banner.setState(true);
//        				 businessBannerService.save(banner);
//   				  }
//    				    
//    			  }
//    		  }
     		    
     		  //修改价格
     		  for (int i = 0; i < oldSpecialty.getSpecifications().size(); i++) {
     			  SpecialtySpecification specific = oldSpecialty.getSpecifications().get(i);
//     			  if (!toChangedIds.contains(specific.getId()))
//     				  continue;
     			  if (specific.getParent()!=0 && !specific.getIsActive())
     				  continue;
     			  List<Filter> filterlist = new ArrayList<Filter>();
     			  filterlist.add(Filter.eq("specification", specific));
     			  filterlist.add(Filter.eq("isActive", true));
     			  List<Order> orders = new ArrayList<Order>();
     			  orders.add(Order.desc("id"));
     			  List<SpecialtyPrice> list = specialtyPriceSrv.findList(null, filterlist, orders);
     			  if (list.size() > 0) {
     				  SpecialtyPrice oldprice = list.get(0);
     				  if( oldprice.getMarketPrice().compareTo(specific.getMarketPrice()) !=0 ||
     					  oldprice.getCostPrice().compareTo(specific.getCostPrice()) !=0     ||
     					  oldprice.getPlatformPrice().compareTo(specific.getPlatformPrice()) != 0 ||
     					  oldprice.getStoreDivide().compareTo(specific.getStoreDivide()) != 0 ||
     					  oldprice.getExterStoreDivide().compareTo(specific.getExterStoreDivide()) != 0 ||
     					  oldprice.getBusinessPersonDivide().compareTo(specific.getBusinessPersonDivide()) != 0 ||
     					  oldprice.getDeliverPrice().compareTo(specific.getDeliverPrice()) != 0) {
     					  SpecialtyPrice newprice = new SpecialtyPrice();
     					 newprice.setMarketPrice(specific.getMarketPrice());
     					 newprice.setCostPrice(specific.getCostPrice());
     					 newprice.setPlatformPrice(specific.getPlatformPrice());
     					 newprice.setCreatorName(username);
     					 newprice.setSpecification(specific);
     					 newprice.setSpecialty(oldSpecialty);
     					 newprice.setBusinessPersonDivide(specific.getBusinessPersonDivide());
     					 newprice.setStoreDivide(specific.getStoreDivide());
     					 newprice.setExterStoreDivide(specific.getExterStoreDivide());
     					 newprice.setDeliverPrice(specific.getDeliverPrice());
     					 oldprice.setIsActive(false);
     					 oldprice.setDeadTime(new Date());
     					 
     					 specialtyPriceSrv.update(oldprice);
     					 specialtyPriceSrv.save(newprice);
     					 
     				  }
     			  } else {
     				 SpecialtyPrice newprice = new SpecialtyPrice();
 					 newprice.setMarketPrice(specific.getMarketPrice());
 					 newprice.setCostPrice(specific.getCostPrice());
 					 newprice.setPlatformPrice(specific.getPlatformPrice());
 					 newprice.setCreatorName(username);
 					 newprice.setSpecification(specific);
 					 newprice.setSpecialty(oldSpecialty);
 					 newprice.setBusinessPersonDivide(specific.getBusinessPersonDivide());
					 newprice.setStoreDivide(specific.getStoreDivide());
					 newprice.setExterStoreDivide(specific.getExterStoreDivide());
					 newprice.setDeliverPrice(specific.getDeliverPrice());
 					specialtyPriceSrv.save(newprice);
     			  }
     		  }
     		  
     		  //草拟吗cc
     		  //在不设置上架时间时，判断是否有库存可以上架
//     		  if (oldSpecialty.getPutonTime() == null) {
// 				 boolean hasInbound = false;
// 				 for (SpecialtySpecification specificaiton : oldSpecialty.getSpecifications()) {
//    				  if (specificaiton.getIsActive()) {
//    					 List<Filter> fls = new ArrayList<Filter>();
//       				  fls.add(Filter.eq("specification", specificaiton));
//       				  fls.add(Filter.gt("inboundNumber", 0));
//       				  List<Inbound> inbounds = inboundServiceImpl.findList(null, fls, null);
//       				  if (!inbounds.isEmpty()) {
//       					 oldSpecialty.setSaleState(1);
//       					 hasInbound = true;
//       					 break;
//       				  }
//       				  
//       				  fls.clear();
//       				  fls.add(Filter.eq("specification", specificaiton));
//       				  fls.add(Filter.gt("vinboundNumber", 0));
//       				  List<HyVinbound> vInbounds = hyVinboundService.findList(null, fls, null);
//       				  if (!vInbounds.isEmpty()) {
//       					 oldSpecialty.setSaleState(1);
//       					 hasInbound = true;
//       					 break;
//       				  }
//    				  }  
//    			  }
// 				 if (!hasInbound) {
// 					oldSpecialty.setSaleState(0);
// 				 }
//     		  }
     		  
     		  //如果将父规格设为无效了，则所有对应的自规格均设为无效
     		  List<SpecialtySpecification> fuSpecifications = new ArrayList<>();
     		  for(SpecialtySpecification specification:oldSpecialty.getSpecifications()){
     			  
     		  }
     		  
     		  
     		  
     		  //若上架时间小于等于当前时间，立即上架
     		  if (oldSpecialty.getPutonTime() != null && oldSpecialty.getPutonTime().compareTo(new Date()) <= 0) {
     			 oldSpecialty.setSaleState(1);
     		  } else if (oldSpecialty.getPutonTime() != null && oldSpecialty.getPutonTime().compareTo(new Date()) > 0) {
     			 oldSpecialty.setSaleState(0);
     		  }
     		  
     		  specialtyServiceImpl.update(oldSpecialty);
     		  json.setSuccess(true);
     		  json.setMsg("修改成功");
     		  json.setObj(null);
   	  } catch (Exception e) {
   		  json.setSuccess(false);
   		  json.setMsg("修改失败");
   		  json.setObj(e);
   		  e.printStackTrace();
   	  }

         return json;
    }
    
    //获取产品的推荐产品列表
//    @RequestMapping("/recommendlist/view")
//    @ResponseBody
//    public Json specialtyRecommendSpecialties(Long specialtyid)
//    {	
//  	  Json json = new Json();
//  	
//  	  try {
//  		  Specialty specialty = specialtyServiceImpl.find(specialtyid);
//  		  
//  		  
//		  if (specialty == null) {
//			  json.setMsg("特产不存在");
//			  json.setSuccess(false);
//			  json.setObj(null);
//		  } else {
//			  List<Specialty> list = specialty.getSpecialtiesForRecommendSpecialtyId();
//	  		  if (list != null) {
//	  			  for (Specialty s : list) {
//	  				  s.setSpecialtiesForRecommendSpecialtyId(null);
//	  				  s.setSpecialtiesForSpeciltyId(null);
//	  			  }
//	  		  }
//			  json.setSuccess(true);
//			  json.setMsg("推荐产品列表查询成功");
//			  json.setObj(list);
//		  }
//  	  } catch (Exception e) {
//		json.setSuccess(false);
//		json.setMsg("推荐产品列表查询失败");
//		json.setObj(null);
//  	  }
//  	  
//      return json;
//    }
    
    /**
     * 选择父规格列表
     * @param specialtyid
     * @param session
     * @return
     */
    @RequestMapping(value = "/getparentspecificationlist/view")
    @ResponseBody
    public Json getParentSpecificationList(Long specialtyid, HttpSession session) {
    	Json json = new Json();
    	
    	try {
			Specialty specialty = specialtyServiceImpl.find(specialtyid);
			
			
			
			List<Filter> filters = new ArrayList<Filter>();
			//filters.add(Filter.eq("isActive", true));
			filters.add(Filter.eq("specialty", specialty));
			filters.add(Filter.eq("parent", Long.valueOf(0)));
			
			List<SpecialtySpecification> list = specialtySpecificationSrv.findList(null, filters, null);
			
			List<Map<String, Object>> res = new ArrayList<>();
			for (SpecialtySpecification specification : list) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", specification.getId());
				map.put("specification", specification.getSpecification());
				res.add(map);
			}
			
			
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(res);	
			
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(e);
			e.printStackTrace();
		}
    	
    	
    	return json;
    }
    
    //下架
    @RequestMapping("/audit")
    @ResponseBody
    public Json specialtyPutDown(Long id, Integer auditstate)
    {	
  	  Json json = new Json();
  	  
  	  try {
  		  Specialty specialty = specialtyServiceImpl.find(id);
  		  if (specialty != null) {
  			  //specialty.setAuditState(auditstate);
  			  specialtyServiceImpl.update(specialty);
  			  json.setSuccess(true);
    		  json.setMsg("审核成功");
    		  json.setObj(null);
    		  return json;
  		  } else {
  			  json.setSuccess(true);
  		      json.setMsg("特产不存在");
  		      json.setObj(null);
  		      return json;
  		  }
	  } catch (Exception e) {
		  json.setSuccess(false);
  		  json.setMsg("审核失败");
  		  json.setObj(null);
	  }

      return json;
    }
    
    //下架
    @RequestMapping("/putdown")
    @ResponseBody
    public Json specialtyPutDown(Long id)
    {	
  	  Json json = new Json();
  	  
  	  try {
  		  Specialty specialty = specialtyServiceImpl.find(id);
  		  if (specialty != null) {
  			  specialty.setSaleState(0);
  			  specialtyServiceImpl.update(specialty);
  			  json.setSuccess(true);
    		  json.setMsg("下架成功");
    		  json.setObj(null);
    		  return json;
  		  } else {
  			  json.setSuccess(true);
  		      json.setMsg("特产不存在");
  		      json.setObj(null);
  		      return json;
  		  }
	  } catch (Exception e) {
		  json.setSuccess(false);
  		  json.setMsg("下架失败");
  		  json.setObj(null);
	  }

      return json;
    }
    
    //上架
    @RequestMapping("/puton")
    @ResponseBody
    public Json specialtyPutOn(Long id)
    {	
  	  Json json = new Json();
  	  
  	  try {
  		  Specialty specialty = specialtyServiceImpl.find(id);
  		  if (specialty != null) {
  			  specialty.setSaleState(1);
  			  specialtyServiceImpl.update(specialty);
  			  json.setSuccess(true);
    		  json.setMsg("上架成功");
    		  json.setObj(null);
    		  return json;
  		  } else {
  			  json.setSuccess(true);
  		      json.setMsg("特产不存在");
  		      json.setObj(null);
  		      return json;
  		  }
	  } catch (Exception e) {
		  json.setSuccess(false);
  		  json.setMsg("上架失败");
  		  json.setObj(null);
	  }

      return json;
    }
    
    //设置无效
    @RequestMapping("/setinactive")
    @ResponseBody
    public Json specialtyInactive(Long id)
    {	
  	  Json json = new Json();
  	  
  	  try {
  		  Specialty specialty = specialtyServiceImpl.find(id);
  		  if (specialty != null) {
  			  specialty.setIsActive(false);
  			  specialtyServiceImpl.update(specialty);
  			  json.setSuccess(true);
    		  json.setMsg("设置成功");
    		  json.setObj(null);
    		  return json;
  		  } else {
  			  json.setSuccess(true);
  		      json.setMsg("特产不存在");
  		      json.setObj(null);
  		      return json;
  		  }
	  } catch (Exception e) {
		  json.setSuccess(false);
  		  json.setMsg("设置失败");
  		  json.setObj(null);
	  }

      return json;
    }
    
    //设置有效
    @RequestMapping("/setactive")
    @ResponseBody
    public Json specialtyActive(Long id)
    {	
  	  Json json = new Json();
  	  
  	  try {
  		  Specialty specialty = specialtyServiceImpl.find(id);
  		  if (specialty != null) {
  			  specialty.setIsActive(true);
  			  specialtyServiceImpl.update(specialty);
  			  json.setSuccess(true);
    		  json.setMsg("设置成功");
    		  json.setObj(null);
    		  return json;
  		  } else {
  			  json.setSuccess(true);
  		      json.setMsg("特产不存在");
  		      json.setObj(null);
  		      return json;
  		  }
	  } catch (Exception e) {
		  json.setSuccess(false);
  		  json.setMsg("设置失败");
  		  json.setObj(null);
	  }

      return json;
    }
    
    
    static final int APPRAISE_SORT_BY_ID = 0;
    static final int APPRAISE_SORT_BY_CONTENT_LEVEL = 1;
    static final int APPRAISE_SORT_BY_TIME = 2;
    
    @RequestMapping("/appraise/page/view")
    @ResponseBody
    public Json specialtyAppraisePage(Long specialtyid,String wechatName, @DateTimeFormat(pattern="yyyy-MM-dd") Date startdate, @DateTimeFormat(pattern="yyyy-MM-dd") Date enddate, Integer sorttype, Pageable pageable) {
    	Json json = new Json();
    	
    	try {
    		Specialty s = specialtyServiceImpl.find(specialtyid);
    		if (s == null) {
    			json.setSuccess(false);
    			json.setMsg("指定特产不存在");
    			json.setObj(null);
    			return json;
    		}
        	SpecialtyAppraise query = new SpecialtyAppraise();
        	query.setSpecialty(s);
        	
        	List<Filter> filters = new ArrayList<Filter>();
        	if(wechatName!=null){
				List<Filter>filters2=new ArrayList<>();
				filters2.add(Filter.like("wechatName", wechatName));
				List<WechatAccount> wechatAccounts=wechatAccountService.findList(null, filters2, null);
				if(wechatAccounts!=null&&wechatAccounts.size()>0){
					filters.add(Filter.in("account", wechatAccounts));
				}else{
					Page<SpecialtyAppraise> page=new Page<>(new LinkedList<SpecialtyAppraise>(),0,pageable);
					json.setSuccess(true);
					json.setMsg("查询成功");
					json.setObj(page);
					return json;
				}
			}
        	if (startdate != null) {
        		filters.add(new Filter("appraiseTime", Operator.ge, startdate));
        	}
        	
        	if (enddate != null) {
        		filters.add(new Filter("appraiseTime", Operator.le, getEndOfDay(enddate)));
        	}
        	
        	List<Order> orders = new ArrayList<Order>();
        	if (sorttype != null) {
        		if (sorttype == APPRAISE_SORT_BY_ID) {
            		orders.add(Order.asc("id"));
            	} else if (sorttype == APPRAISE_SORT_BY_CONTENT_LEVEL) {
            		orders.add(Order.desc("contentLevel"));
            	} else if (sorttype == APPRAISE_SORT_BY_TIME) {
            		orders.add(Order.desc("appraiseTime"));
            	}
        	}
        	
        	pageable.setFilters(filters);
        	pageable.setOrders(orders);
			Page<SpecialtyAppraise> page = specialtyAppraiseServiceImpl.findPage(pageable, query);
			
			Map<String, Object> pageMap = new HashMap<String, Object>();
			pageMap.put("total", page.getTotal());
			pageMap.put("pageNumber", page.getPageNumber());
			pageMap.put("pageSize", page.getPageSize());
			
			List<Map> list = new ArrayList<Map>();
			for (SpecialtyAppraise appraise : page.getRows()) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", appraise.getId());
				map.put("specification", appraise.getSpecification().getSpecification());
				map.put("orderName", appraise.getBusinessOrder().getOrderCode());
				map.put("wechatAccount", appraise.getAccount().getWechatName());
				map.put("appraiseTime", appraise.getAppraiseTime());
				map.put("appraiseContent", appraise.getAppraiseContent());
				map.put("contentLevel", appraise.getContentLevel());
				map.put("isShow", appraise.getIsShow());
				map.put("isAnonymous", appraise.getIsAnonymous());
				map.put("isValid", appraise.getIsValid());
				map.put("deleteTime", appraise.getDeleteTime());
				list.add(map);
			}
    		pageMap.put("rows", list);
			
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(pageMap);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(null);
		}
    	
    	return json;
    }
    //评论查看图片
    @RequestMapping("/appraise/images/view")
    @ResponseBody
    public Json appraiseImages(Long id)
    {
    	Json json=new Json();
    	try {
    		SpecialtyAppraise specialtyAppraise = specialtyAppraiseServiceImpl.find(id);
    		List<SpecialtyAppraiseImage> images=specialtyAppraise.getImages();
    		json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(images);
    	}
    	catch(Exception e) {
    		json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(null);
    	}
    	return json;
    }
    
    @RequestMapping("/appraise/delete")
    @ResponseBody
    public Json specialtyAppraiseDelete(Long id) {
    	Json json = new Json();
    	
    	try {
			SpecialtyAppraise sa = specialtyAppraiseServiceImpl.find(id);
			sa.setIsValid(false);
			specialtyAppraiseServiceImpl.update(sa);
			json.setSuccess(true);
			json.setMsg("删除成功");
			json.setObj(null);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("删除失败");
			json.setObj(null);
		}
    	
    	return json;
    }
    
	@Resource(name = "pointrecordServiceImpl")
	PointrecordService pointrecordService;
	
    @RequestMapping("/appraise/changeStatus")
    @ResponseBody
    public Json specialtyAppraiseChangeStatus(Long id){
    	Json json=new Json();
		try {
			SpecialtyAppraise specialtyAppraise=specialtyAppraiseServiceImpl.find(id);
			specialtyAppraise.setIsValid(specialtyAppraise.getIsValid()?false:true);
			specialtyAppraiseServiceImpl.update(specialtyAppraise);
			
			Integer changevalue = specialtyAppraise.getIsValid()?10:-10;
			String reason = changevalue>0?"评价":"删除评价";
			//评价状态改变，修改用户10积分
			pointrecordService.changeUserPoint(specialtyAppraise.getAccount().getId(), changevalue, reason);
			
			
			json.setSuccess(true);
			json.setMsg("修改成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("修改失败");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
    }
    
    @RequestMapping("/appraise/detail/view")
    @ResponseBody
    public Json specialtyAppraiseDetail(Long id) {
    	Json json = new Json();
    	
    	try {
    		SpecialtyAppraise appraise = specialtyAppraiseServiceImpl.find(id);
    		if (appraise != null) {
    			json.setSuccess(true);
    			json.setMsg("查询成功");
    			json.setObj(appraise);
    		} else {
    			json.setSuccess(false);
    			json.setMsg("评价不存在");
    			json.setObj(null);
    		}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(null);
		}
    	
    	return json;
    }
    
//    @RequestMapping({"/category/treelist/view"})
//    @ResponseBody
//    public Json specialtyCategoryTreeList()
//    {
//      Json json = new Json();
//      List<Filter> filters = new ArrayList<Filter>();
//      Filter filter = new Filter("parent", Filter.Operator.isNull, null);
//      filters.add(filter);
//      List<Order> orders = new ArrayList<Order>();
//      orders.add(Order.asc("id"));
//      List<SpecialtyCategory> list = this.specialtyCategoryServiceImpl.findList(null, filters, orders);
//      List<HashMap<String, Object>> obj = new ArrayList<HashMap<String, Object>>();
//      for (SpecialtyCategory parent : list)
//      {
//        HashMap<String, Object> hm = new HashMap<String, Object>();
//        hm.put("value", parent.getId());
//        hm.put("label", parent.getName());
//        hm.put("children", fieldFilter(parent));
//        obj.add(hm);
//      }
//      json.setSuccess(true);
//      json.setMsg("查询成功");
//      json.setObj(obj);
//      return json;
//    }
//    
    @RequestMapping(value = "/provider/list/view", method = RequestMethod.GET)
	@ResponseBody
	public Json providerList() {
		Json json = new Json();
		
		try {
			List<Provider> list = providerServiceImpl.findAll();
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(list);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(null);
		}
		
		return json;
	}
//    
//    private List<HashMap<String, Object>> fieldFilter(SpecialtyCategory parent)
//    {
//      List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
//      if (parent.getChildSpecialtyCategory().size() > 0) {
//        for (SpecialtyCategory child : parent.getChildSpecialtyCategory())
//        {	
//      	  if(child.getIsActive()) {
//      		  HashMap<String, Object> hm = new HashMap<String, Object>();
//      	      hm.put("value", child.getId());
//      	      hm.put("label", child.getName());
//      	      hm.put("children", fieldFilter(child));
//      	      list.add(hm);
//      	  }
//        }
//      }
//      return list;
//    }
    
    @RequestMapping(value = "/pricehistory/view")
    @ResponseBody
    public Json specificationPriceHistory(Long specificationid, HttpSession session) {
    	Json json = new Json();
    	
    	try {
			SpecialtySpecification specification = specialtySpecificationSrv.find(specificationid);
			
			List<Filter> filters = new ArrayList<Filter>();
			filters.add(Filter.eq("isActive", false));
			filters.add(Filter.eq("specification", specification));
			
			List<SpecialtyPrice> prices = specialtyPriceSrv.findList(null, filters, null);
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(prices);	
			
		} catch (Exception e) {
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(e);
			e.printStackTrace();
		}
    	
    	
    	return json;
    }
    
//    @RequestMapping(value = "/specialtyprice/modify")
//    @ResponseBody
//    public Json specificationPriceModify(Long specificationid, ) {
//    	Json json = new Json();
//    	
//    	
//    	
//    	return json;
//    }
    
    @RequestMapping({"/proportion/view"})
    @ResponseBody
    public Json getDivideProportion(Integer purchasetype)
    {
      Json j = new Json();
      try
      {
        List<Filter> filters = new ArrayList();
        filters.add(Filter.eq("proportionType", purchasetype));
        List<WeDivideProportion> list = this.proportionSrv.findList(null, filters, null);
        j.setMsg("查询成功");
        j.setSuccess(true);
        j.setObj(list);
      }
      catch (Exception e)
      {
        j.setMsg("查询失败");
        j.setSuccess(false);
        j.setObj(e);
        e.printStackTrace();
      }
      return j;
    }
    
    @RequestMapping({"/recommendlist/view"})
    @ResponseBody
    public Json getRecommendList(Long id)
    {
      Json j = new Json();
      try
      {
        Specialty specialty = specialtyServiceImpl.find(id);
        if (specialty == null) {
        	j.setMsg("不存在指定的特产");
            j.setSuccess(false);
            j.setObj(null);
            return j;
        }
        
        List<Specialty> recommendList = specialty.getSpecialtiesForRecommendSpecialtyId();
        List<Map<String, Object>> listMap = new ArrayList<>();
        for (Specialty s : recommendList) {
        	Map<String, Object> m = new HashMap<>();
        	m.put("specialtyid", s.getId());
        	m.put("specialtyname", s.getName());
        	listMap.add(m);
        }
        j.setMsg("查询成功");
        j.setSuccess(true);
        j.setObj(listMap);
      }
      catch (Exception e)
      {
        j.setMsg("查询失败");
        j.setSuccess(false);
        j.setObj(e);
        e.printStackTrace();
      }
      return j;
    }
    
  //查询指定供应商和指定分区的产品
    @RequestMapping("/specialtylist/view")
    @ResponseBody
    public Json specialtyList(Long categoryid)
    {	
  	  Json json = new Json();
  	  
  	  
  	  try {
	  	  SpecialtyCategory category = specialtyCategoryServiceImpl.find(categoryid);
	  	  List<Filter> filters = new ArrayList<Filter>();
	  	  filters.add(Filter.eq("category", category));
	  	  List<Specialty> specialties = specialtyServiceImpl.findList(null, filters, null);
	  	  json.setSuccess(true);
  		  json.setMsg("查询成功");
  		  json.setObj(specialties);
	  } catch (Exception e) {
		  json.setSuccess(false);
  		  json.setMsg("查询失败");
  		  json.setObj(null);
	  }

      return json;
    }
    
    
    public static Date getEndOfDay(Date date) {
        return DateUtils.addMilliseconds(DateUtils.ceiling(date, Calendar.DATE), -1);
    }

    public static Date getStartOfDay(Date date) {
        return DateUtils.truncate(date, Calendar.DATE);
    }
    
    
    
}