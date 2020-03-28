package com.hongyu.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.BusinessBanner;
import com.hongyu.entity.BusinessBanner.BannerType;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyFullDiscount;
import com.hongyu.entity.HyFullPresent;
import com.hongyu.entity.HyFullSubstract;
import com.hongyu.entity.HyGroupitemPromotion;
import com.hongyu.entity.HyGroupitemPromotionDetail;
import com.hongyu.entity.HyPromotion;
import com.hongyu.entity.HyPromotion.PromotionStatus;
import com.hongyu.entity.HyPromotion.PromotionType;
import com.hongyu.entity.HyPromotionPic;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.entity.HySingleitemPromotion;
import com.hongyu.entity.Provider;
import com.hongyu.entity.Specialty;
import com.hongyu.entity.SpecialtyCategory;
import com.hongyu.entity.SpecialtySpecification;
import com.hongyu.entity.WeDivideProportion;
import com.hongyu.service.BusinessBannerService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyFullDiscountService;
import com.hongyu.service.HyFullPresentService;
import com.hongyu.service.HyFullSubstractService;
import com.hongyu.service.HyGroupitemPromotionDetailService;
import com.hongyu.service.HyGroupitemPromotionService;
import com.hongyu.service.HyPromotionPicService;
import com.hongyu.service.HyPromotionService;
import com.hongyu.service.HySingleitemPromotionService;
import com.hongyu.service.ProviderService;
import com.hongyu.service.SpecialtyCategoryService;
import com.hongyu.service.SpecialtyService;
import com.hongyu.service.SpecialtySpecificationService;
import com.hongyu.service.WeDivideProportionService;
import com.hongyu.util.AuthorityUtils;

@RestController
@RequestMapping({"/admin/business/promotion/"})
public class HyPromotionController
{
  @Resource(name="providerServiceImpl")
  ProviderService providerServiceImpl;
  @Resource(name="hyPromotionServiceImpl")
  HyPromotionService hyPromotionService;
  @Resource(name="hySingleitemPromotionServiceImpl")
  HySingleitemPromotionService hySingleitemPromotionService;
  @Resource(name="weDivideProportionServiceImpl")
  WeDivideProportionService proportionSrv;
  @Resource(name="specialtyServiceImpl")
  SpecialtyService specialtyService;
  @Resource(name="specialtySpecificationServiceImpl")
  SpecialtySpecificationService specialtySpecificationService;
  @Resource(name="hyGroupitemPromotionDetailServiceImpl")
  HyGroupitemPromotionDetailService hyGroupitemPromotionDetailService;
  @Resource(name="hyGroupitemPromotionServiceImpl")
  HyGroupitemPromotionService hyGroupitemPromotionService;
  @Resource(name="specialtyCategoryServiceImpl")
  SpecialtyCategoryService specialtyCategoryServiceImpl;
  @Resource(name="hyPromotionPicServiceImpl")
  HyPromotionPicService hyPromotionPicService;
  @Resource(name="hyFullDiscountServiceImpl")
  HyFullDiscountService hyFullDiscountService;
  @Resource(name="hyFullPresentServiceImpl")
  HyFullPresentService hyFullPresentService;
  @Resource(name="hyFullSubstractServiceImpl")
  HyFullSubstractService hyFullSubstractService;
  @Resource(name="hyAdminServiceImpl")
  private HyAdminService hyAdminService;
  @Resource(name="businessBannerServiceImpl")
  private BusinessBannerService businessBannerService;
  @Resource(name="weDivideProportionServiceImpl")
  private WeDivideProportionService weDivideProportionService;
  
	/**
	 * 获取促销详情
	 * @param id
	 * @return
	 */
	@RequestMapping(value="detail/view")
	public Json detail(Long id) {
    Json j = new Json();
    try
    {
      Map<String, Object> result = new HashMap<>();
      HyPromotion promotion = (HyPromotion)this.hyPromotionService.find(id);
      result.put("id", promotion.getId());
      result.put("promotionName", promotion.getPromotionName());
      result.put("promotionType", promotion.getPromotionType());
      result.put("promotionRule", promotion.getPromotionRule());
      result.put("promotionStarttime", promotion.getPromotionStarttime());
      result.put("promotionEndtime", promotion.getPromotionEndtime());
      result.put("status", promotion.getStatus());
      result.put("introduction", promotion.getIntroduction());
      result.put("couponAvailable", promotion.getCouponAvailable());
      result.put("content", promotion.getContent());
      result.put("isBanner", promotion.getIsBanner());
      result.put("deadTime", promotion.getDeadTime());
      result.put("hyFullPresents", promotion.getHyFullPresents());
      result.put("hyFullDiscounts", promotion.getHyFullDiscounts());
      result.put("hyFullSubstracts", promotion.getHyFullSubstracts());
      result.put("hyPromotionPics", promotion.getHyPromotionPics());
      result.put("hySingleitemPromotions", promotion.getHySingleitemPromotions());
      result.put("hyGroupitemPromotions", promotion.getHyGroupitemPromotions());

      result.put("syncTagpic",promotion.getSyncTagpic());

        if(promotion.getSyncTagpic()){

            for (HySingleitemPromotion pro : promotion.getHySingleitemPromotions()) {
                result.put("hyPromotionPics", pro.getSpecialtyId().getImages());
            }
        }else{
            result.put("hyPromotionPics", promotion.getHyPromotionPics());
        }


        result.put("divideMoney",promotion.getDivideMoney());
      j.setMsg("查看成功");
      j.setSuccess(true);
      j.setObj(result);
    }
    catch (Exception e)
    {
      j.setSuccess(false);
      j.setMsg(e.getMessage());
    }
    return j;
  }
  
  
  @Transactional(propagation = Propagation.REQUIRED)
  @RequestMapping(value="add", method = RequestMethod.POST)
  public Json add(@RequestBody HyPromotion wrap)
  {
    Json j = new Json();
    try
    {
      if (wrap.getHyPromotionPics().size() > 0) {
        for (HyPromotionPic hyPromotionPic : wrap.getHyPromotionPics()) {
          hyPromotionPic.setHyPromotion(wrap);
        }
      }
      if (wrap.getHyFullDiscounts().size() > 0) {
        for (HyFullDiscount discount : wrap.getHyFullDiscounts()) {
          discount.setHyPromotion(wrap);
        }
      }
      if (wrap.getHyFullSubstracts().size() > 0) {
        for (HyFullSubstract substract : wrap.getHyFullSubstracts()) {
          substract.setHyPromotion(wrap);
        }
      }
      if (wrap.getHyFullPresents().size() > 0) {
        for (HyFullPresent present : wrap.getHyFullPresents())
        {
          present.setHyPromotion(wrap);
          present.setFullPresentProduct(this.specialtyService.find(present.getFullPresentProduct().getId()));
          present.setFullPresentProductSpecification(this.specialtySpecificationService.find(present.getFullPresentProductSpecification().getId()));
        }
      }
      //添加 优惠活动时，如果开始时间小于当前时间，就设置为 进行中；list列表按 状态和优惠类型排序。

      if(wrap.getPromotionStarttime().before(new Date())) {
    	  wrap.setStatus(PromotionStatus.进行中);
      } else if(wrap.getPromotionStarttime().after(new Date())) {
    	  wrap.setStatus(PromotionStatus.未开始);
      } else {
    	  wrap.setStatus(PromotionStatus.进行中);
      }
      this.hyPromotionService.save(wrap);
      
      if ((wrap.getPromotionType() != null) && (wrap.getPromotionType() == PromotionType.组合优惠))
      {
        if (wrap.getHyGroupitemPromotions().size() > 0)
        {
          Set<HyGroupitemPromotion> gips = wrap.getHyGroupitemPromotions();
          for (HyGroupitemPromotion gip : gips)
          {
            gip.setPromotionId(wrap);
            if (gip.getStoreDivide() != null) {
              gip.setStoreDivide((WeDivideProportion)this.weDivideProportionService.find(gip.getStoreDivide().getId()));
            }
            if (gip.getExterStoreDivide() != null) {
              gip.setExterStoreDivide((WeDivideProportion)this.weDivideProportionService.find(gip.getExterStoreDivide().getId()));
            }
            if (gip.getBusinessPersonDivide() != null) {
              gip.setBusinessPersonDivide((WeDivideProportion)this.weDivideProportionService.find(gip.getBusinessPersonDivide().getId()));
            }
            this.hyGroupitemPromotionService.save(gip);
            for(HyGroupitemPromotionDetail gipd : gip.getHyGroupitemPromotionDetails()) {
				gipd.setHyGroupitemPromotion(gip);
				gipd.setItemId(specialtyService.find(gipd.getItemId().getId()));
				gipd.setItemSpecificationId(specialtySpecificationService.find(gipd.getItemSpecificationId().getId()));
				hyGroupitemPromotionDetailService.save(gipd);
			}
        }
      }
        }
      else if ((wrap.getPromotionType() != null) && (wrap.getPromotionType() == PromotionType.普通优惠) && 
        (wrap.getHySingleitemPromotions().size() > 0))
      {
        Set<HySingleitemPromotion> sips = wrap.getHySingleitemPromotions();
        for (HySingleitemPromotion sip : sips)
        {
          sip.setSpecialtyId((Specialty)this.specialtyService.find(sip.getSpecialtyId().getId()));
          sip.setSpecificationId((SpecialtySpecification)this.specialtySpecificationService.find(sip.getSpecificationId().getId()));
          sip.setHyPromotion(wrap);
          
          if (sip.getStoreDivide() != null) {
            sip.setStoreDivide((WeDivideProportion)this.weDivideProportionService.find(sip.getStoreDivide().getId()));
          }
          if (sip.getExterStoreDivide() != null) {
            sip.setExterStoreDivide((WeDivideProportion)this.weDivideProportionService.find(sip.getExterStoreDivide().getId()));
          }
          if (sip.getBusinessPersonDivide() != null) {
            sip.setBusinessPersonDivide((WeDivideProportion)this.weDivideProportionService.find(sip.getBusinessPersonDivide().getId()));
          }
          
          this.hySingleitemPromotionService.save(sip);
        }
      }
      if ((wrap.getIsBanner() != null) && (wrap.getIsBanner().booleanValue()))
      {
        BusinessBanner businessBanner = new BusinessBanner();
        businessBanner.setTitle(wrap.getPromotionName());
        if (wrap.getHyPromotionPics().size() > 0) {
          for (HyPromotionPic pic : wrap.getHyPromotionPics()) {
            if ((pic.getIsTag() != null) && (!pic.getIsTag().booleanValue()))
            {
              businessBanner.setImg(pic.getSourcePath());
              break;
            }
          }
        }
        businessBanner.setType(BusinessBanner.BannerType.活动);
        businessBanner.setTargetId(wrap.getId());
        businessBanner.setStartTime(wrap.getPromotionStarttime());
        businessBanner.setEndTime(wrap.getPromotionEndtime());
        businessBanner.setState(Boolean.valueOf(true));
        businessBanner.setIsCheck(wrap.getPromotionType() == PromotionType.普通优惠 ? 0 : 1);
        this.businessBannerService.save(businessBanner);
      }
      j.setSuccess(true);
      j.setMsg("添加成功");
    }
    catch (Exception e)
    {
      j.setSuccess(false);
      j.setMsg(e.getMessage());
    }
    return j;
  }
  
	/**
	 * 优惠活动列表页
	 * @return
	 */
  @RequestMapping(value="promotionlist/view")
  public Json promotionlist(Pageable pageable, HyPromotion promotion, HttpSession session, HttpServletRequest request)
  {
    Json j = new Json();
    try
    {
		Map<String, Object> result = new HashMap<String, Object>();
		List<HashMap<String, Object>> lhm = new ArrayList<>();
		
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
		
		List<Filter> filters = new ArrayList<Filter>();
		filters.add(Filter.in("creator", hyAdmins));
		pageable.setFilters(filters);
		List<Order> orders = new ArrayList<>();
		orders.add(Order.desc("createTime"));//2019-08-04改成根据创建时间降序
		orders.add(Order.asc("status"));
		orders.add(Order.asc("promotionType"));
		pageable.setOrders(orders);
      
      Page<HyPromotion> page = this.hyPromotionService.findPage(pageable, promotion);
      if (page.getRows().size() > 0) {
        for (HyPromotion p : page.getRows())
        {
          HashMap<String, Object> hm = new HashMap<String, Object>();
          HyAdmin creater = p.getCreator();
          hm.put("id", p.getId());
          hm.put("promotionName", p.getPromotionName());
          hm.put("promotionType", p.getPromotionType());
          hm.put("promotionStarttime", p.getPromotionStarttime());
          hm.put("promotionEndtime", p.getPromotionEndtime());
          hm.put("promotionRule", p.getPromotionRule());
          hm.put("status", p.getStatus());
          hm.put("orders", p.getOrders());
          hm.put("couponAvailable", p.getCouponAvailable());
          if (p.getCreator() != null) {
            hm.put("creator", p.getCreator().getName());
          }
      		/** 当前用户对本条数据的操作权限 */
			if(creater.equals(admin)){
				if(co == CheckedOperation.view) {
					hm.put("privilege", "view");
				} else {
					hm.put("privilege", "edit");
				}
			} else{
				if(co == CheckedOperation.edit) {
					hm.put("privilege", "edit");
				} else {
					hm.put("privilege", "view");
				}
			}
          lhm.add(hm);
        }
      }
      result.put("pageSize", Integer.valueOf(page.getPageSize()));
      result.put("pageNumber", Integer.valueOf(page.getPageNumber()));
      result.put("total", Long.valueOf(page.getTotal()));
      result.put("rows", lhm);
      j.setSuccess(true);
      j.setMsg("查询成功");
      j.setObj(result);
      return j;
    }
    catch (Exception e)
    {
      j.setSuccess(false);
      j.setMsg(e.getMessage());
    }
    return j;
  }
  
  /**
	 * 编辑活动信息
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	@RequestMapping(value="update", method = RequestMethod.POST)
	public Json update(@RequestBody HyPromotion wrap) {
  
    Json j = new Json();
    try
    {
    	/** 获取优惠信息 */
		HyPromotion hyPromotion = hyPromotionService.find(wrap.getId());
		if(hyPromotion == null) {
			j.setSuccess(false);
			j.setMsg("优惠活动不存在");
			return j;
		}
		
		/**
		 * 更新活动图片信息
		 */
		Set<HyPromotionPic> pics = hyPromotion.getHyPromotionPics();
		if(wrap.getHyPromotionPics().size() > 0) {
			for(HyPromotionPic hyPromotionPic : wrap.getHyPromotionPics()) {
				hyPromotionPic.setHyPromotion(hyPromotion);
			}
		}
		pics.clear();
		pics.addAll(wrap.getHyPromotionPics());
      
		/**
		 * 更新满折信息
		 */
		Set<HyFullDiscount> discounts = hyPromotion.getHyFullDiscounts();
		if(wrap.getHyFullDiscounts().size() > 0) {
			for(HyFullDiscount discount : wrap.getHyFullDiscounts()) {
				discount.setHyPromotion(hyPromotion);
			}
		} 
		discounts.clear();
		discounts.addAll(wrap.getHyFullDiscounts());
		
		/**
		 * 更新满减信息
		 */
		Set<HyFullSubstract> substracts = hyPromotion.getHyFullSubstracts();
		if(wrap.getHyFullSubstracts().size() > 0) {
			for(HyFullSubstract substract : wrap.getHyFullSubstracts()) {
				substract.setHyPromotion(hyPromotion);
			}
		}
		substracts.clear();
		substracts.addAll(wrap.getHyFullSubstracts());
		
		/**
		 * 更新满赠信息
		 */
		Set<HyFullPresent> presents = hyPromotion.getHyFullPresents();
		if(wrap.getHyFullPresents().size() > 0) {
			for(HyFullPresent present : wrap.getHyFullPresents()) {
				present.setHyPromotion(hyPromotion);
				present.setFullPresentProduct(specialtyService.find(present.getFullPresentProduct().getId()));
				present.setFullPresentProductSpecification(specialtySpecificationService.find(present.getFullPresentProductSpecification().getId()));
			}
		}
		presents.clear();
		presents.addAll(wrap.getHyFullPresents());
		
      if ((wrap.getPromotionType() != null) && (wrap.getPromotionType() == PromotionType.组合优惠))
      {
    	Set<HyGroupitemPromotion> gps = hyPromotion.getHyGroupitemPromotions();
        if (wrap.getHyGroupitemPromotions().size() > 0)
        {
        
        	Set<HyGroupitemPromotion> gips = wrap.getHyGroupitemPromotions();
           for (HyGroupitemPromotion gip : gips)
           {
            gip.setPromotionId(wrap);
            if (gip.getHyGroupitemPromotionDetails().size() > 0) {
              for (HyGroupitemPromotionDetail gipd : gip.getHyGroupitemPromotionDetails())
              {
                gipd.setHyGroupitemPromotion(gip);
                gipd.setItemId((Specialty)this.specialtyService.find(gipd.getItemId().getId()));
                gipd.setItemSpecificationId((SpecialtySpecification)this.specialtySpecificationService.find(gipd.getItemSpecificationId().getId()));
              }
            }
            if (gip.getStoreDivide() != null) {
              gip.setStoreDivide((WeDivideProportion)this.weDivideProportionService.find(gip.getStoreDivide().getId()));
            }
            if (gip.getExterStoreDivide() != null) {
              gip.setExterStoreDivide((WeDivideProportion)this.weDivideProportionService.find(gip.getExterStoreDivide().getId()));
            }
            if (gip.getBusinessPersonDivide() != null) {
              gip.setBusinessPersonDivide((WeDivideProportion)this.weDivideProportionService.find(gip.getBusinessPersonDivide().getId()));
            }
          }
        }
        gps.clear();
        gps.addAll(wrap.getHyGroupitemPromotions());
      }
      else if ((wrap.getPromotionType() != null) && (wrap.getPromotionType() == HyPromotion.PromotionType.普通优惠))
      {
    	  Set<HySingleitemPromotion> sps = hyPromotion.getHySingleitemPromotions();
        if (wrap.getHySingleitemPromotions().size() > 0)
        {
          Set<HySingleitemPromotion> sips = wrap.getHySingleitemPromotions();
          for (HySingleitemPromotion sip : sips)
          {
            sip.setSpecialtyId((Specialty)this.specialtyService.find(sip.getSpecialtyId().getId()));
            sip.setSpecificationId((SpecialtySpecification)this.specialtySpecificationService.find(sip.getSpecificationId().getId()));
            sip.setHyPromotion(hyPromotion);
            if (sip.getStoreDivide() != null) {
              sip.setStoreDivide((WeDivideProportion)this.weDivideProportionService.find(sip.getStoreDivide().getId()));
            }
            if (sip.getExterStoreDivide() != null) {
              sip.setExterStoreDivide((WeDivideProportion)this.weDivideProportionService.find(sip.getExterStoreDivide().getId()));
            }
            if (sip.getBusinessPersonDivide() != null) {
              sip.setBusinessPersonDivide((WeDivideProportion)this.weDivideProportionService.find(sip.getBusinessPersonDivide().getId()));
            }
          }
        }
        sps.clear();
        sps.addAll(wrap.getHySingleitemPromotions());
      }
      this.hyPromotionService.update(wrap, "creator", "createTime", "status");
      
		/**
		 * 更新广告信息
		 */
		List<Filter> filters = new ArrayList<>();
		filters.add(Filter.eq("targetId", wrap.getId()));
		filters.add(Filter.eq("type", BannerType.活动));
		List<BusinessBanner> businessBanners = businessBannerService.findList(null, filters, null);
		
		if(businessBanners.size() > 0) {
			BusinessBanner businessBanner = businessBanners.get(0);
			businessBanner.setTitle(wrap.getPromotionName());
			if(wrap.getHyPromotionPics().size() > 0) {
				for(HyPromotionPic pic : wrap.getHyPromotionPics()) {
					if(pic.getIsTag() != null && pic.getIsTag()){
						businessBanner.setImg(pic.getSourcePath());
						break;
					}	
				}
			}
			businessBanner.setStartTime(wrap.getPromotionStarttime());
			businessBanner.setEndTime(wrap.getPromotionEndtime());
			
			PromotionStatus ps = wrap.getStatus();
			if(ps == PromotionStatus.进行中)
				businessBanner.setState(true);
			else
				businessBanner.setState(false);
			businessBannerService.update(businessBanner);
		}
		j.setSuccess(true);
		j.setMsg("更新成功");
    }
    catch (Exception e)
    {
      j.setSuccess(false);
      j.setMsg(e.getMessage());
      e.printStackTrace();
    }
    return j;
  }
  
	@RequestMapping(value="proportion/view")
  public Json proportion(Integer proportionType)
  {
    Json j = new Json();
    try
    {
      List<Filter> filters = new ArrayList<>();
      filters.add(Filter.eq("proportionType", proportionType));
      filters.add(Filter.eq("isValid", Boolean.valueOf(true)));
      List<WeDivideProportion> list = this.proportionSrv.findList(null, filters, null);
      j.setSuccess(true);
      j.setMsg("查询成功");
      j.setObj(list);
    }
    catch (Exception e)
    {
      j.setSuccess(false);
      j.setMsg(e.getMessage());
    }
    return j;
  }
	

	/**
	 * 取消活动
	 * @param id
	 * @return
	 */
	@RequestMapping(value="cancel")
  public Json cancel(Long id)
  {
    Json j = new Json();
    try
    {
      HyPromotion promotion = (HyPromotion)this.hyPromotionService.find(id);
      promotion.setStatus(HyPromotion.PromotionStatus.已取消);
      promotion.setDeadTime(new Date());
      
      //取消广告
      List<Filter> bannerFilters = new ArrayList<>();
      bannerFilters.add(Filter.eq("type", BannerType.活动));
      bannerFilters.add(Filter.eq("targetId", promotion.getId()));
      List<BusinessBanner> banners = businessBannerService.findList(null,bannerFilters,null);
      if(banners!=null && !banners.isEmpty()) {
    	  for(BusinessBanner banner:banners) {
    		  banner.setState(false);
    		  businessBannerService.update(banner);
    	  }
      }
      
      this.hyPromotionService.update(promotion);
      j.setSuccess(true);
      j.setMsg("取消活动成功");
    }
    catch (Exception e)
    {
      j.setSuccess(false);
      j.setMsg(e.getMessage());
    }
    return j;
  }
  
  	//根据分区和供应商获取优惠产品
    @RequestMapping("specialtylist/view")
  public Json specialtyPutDown(Long providerid, Long categoryid, Boolean isFreeGift)
  {
    Json j = new Json();
    try
    {
      List<HashMap<String, Object>> lhm = new ArrayList<>();
      Provider provider = (Provider)this.providerServiceImpl.find(providerid);
      SpecialtyCategory category = (SpecialtyCategory)this.specialtyCategoryServiceImpl.find(categoryid);
      if (category == null)
      {
        j.setSuccess(false);
        j.setMsg("特产分区不存在");
        return j;
      }
      List<Filter> filters = new ArrayList<>();
      filters.add(Filter.eq("provider", provider));
      filters.add(Filter.eq("category", category));
      filters.add(Filter.eq("isActive", Boolean.valueOf(true)));
      if(isFreeGift != null && isFreeGift) {
    	  filters.add(Filter.ne("numberOfFreeGift", 0));
      }
      List<Specialty> specialties = this.specialtyService.findList(null, filters, null);
      for (Specialty specialty : specialties)
      {
        HashMap<String, Object> hm = new HashMap<>();
        hm.put("id", specialty.getId());
        hm.put("name", specialty.getName());
        lhm.add(hm);
      }
      j.setSuccess(true);
      j.setMsg("获取特产成功");
      j.setObj(lhm);
    }
    catch (Exception e)
    {
      j.setSuccess(false);
      j.setMsg("查询失败");
      j.setObj(null);
    }
    return j;
  }
  
	/**
	 * 赠品根据特产获取所有特产规格
	 * @return
	 */
	@RequestMapping(value="specifications/view")
  public Json specifications(Long specialtyid, Boolean isPresent)
  {
    Json j = new Json();
    try
    {
      List<HashMap<String, Object>> lhm = new ArrayList<>();
      Specialty specialty = (Specialty)this.specialtyService.find(specialtyid);
      if (specialty == null)
      {
        j.setSuccess(false);
        j.setMsg("特产不存在");
        return j;
      }
      List<Filter> filters = new ArrayList<>();
      filters.add(Filter.eq("specialty", specialty));
      filters.add(Filter.eq("isActive", Boolean.valueOf(true)));
      if(isPresent != null && isPresent) {
    	  filters.add(Filter.eq("isFreeGift", Boolean.valueOf(true)));
      }
      List<SpecialtySpecification> specifications = this.specialtySpecificationService.findList(null, filters, null);
      for (SpecialtySpecification specification : specifications)
      {
    	  //修改为一个产品只能参加一次优惠活动
    	  List<Filter> filters1 = new ArrayList<>();
    	  filters1.add(Filter.eq("specificationId", specification));
    	  List<HySingleitemPromotion> sps = hySingleitemPromotionService.findList(null, filters1, null);
    	  if(sps.size() > 0) {
    		  if(sps.get(0).getHyPromotion() != null && sps.get(0).getHyPromotion().getStatus().equals(PromotionStatus.进行中)) {
    			  continue;
    		  }
    		  if(sps.get(0).getHyPromotion() != null && sps.get(0).getHyPromotion().getStatus().equals(PromotionStatus.未开始)) {
    			  continue;
    		  }
    	  }
    	  List<Filter> filters2 = new ArrayList<>();
    	  filters2.add(Filter.eq("itemSpecificationId", specification));
    	  List<HyGroupitemPromotionDetail> pds = hyGroupitemPromotionDetailService.findList(null, filters2, null);
    	  if(pds.size() > 0) {
    		  if(pds.get(0).getHyGroupitemPromotion() != null && pds.get(0).getHyGroupitemPromotion().getPromotionId().getStatus().equals(PromotionStatus.进行中)) {
    			  continue;
    		  }
    		  if(pds.get(0).getHyGroupitemPromotion() != null && pds.get(0).getHyGroupitemPromotion().getPromotionId().getStatus().equals(PromotionStatus.未开始)) {
    			  continue;
    		  }
    	  }
    	
        HashMap<String, Object> hm = new HashMap<>();
        hm.put("value", specification.getId());
        hm.put("label", specification.getSpecification());
        lhm.add(hm);
      }
      j.setSuccess(true);
      j.setMsg("获取特产规格成功");
      j.setObj(lhm);
    }
    catch (Exception e)
    {
      j.setSuccess(false);
      j.setMsg(e.getMessage());
    }
    return j;
  }
	  //分区列表
	  @RequestMapping({"/categorytreelist/view"})
  public Json specialtyCategoryTreeList()
  {
    Json json = new Json();
    List<Filter> filters = new ArrayList<>();
    Filter filter = new Filter("parent", Filter.Operator.isNull, null);
    filters.add(filter);
    List<Order> orders = new ArrayList<>();
    orders.add(Order.asc("id"));
    List<SpecialtyCategory> list = this.specialtyCategoryServiceImpl.findList(null, filters, orders);
    List<HashMap<String, Object>> obj = new ArrayList<>();
    for (SpecialtyCategory parent : list)
    {
      HashMap<String, Object> hm = new HashMap<>();
      hm.put("value", parent.getId());
      hm.put("label", parent.getName());
      hm.put("children", fieldFilter(parent));
      obj.add(hm);
    }
    json.setSuccess(true);
    json.setMsg("查询成功");
    json.setObj(obj);
    return json;
  }
  
  private List<HashMap<String, Object>> fieldFilter(SpecialtyCategory parent)
  {
    List<HashMap<String, Object>> list = new ArrayList<>();
    if (parent.getChildSpecialtyCategory().size() > 0) {
      for (SpecialtyCategory child : parent.getChildSpecialtyCategory()) {
        if (child.getIsActive().booleanValue())
        {
          HashMap<String, Object> hm = new HashMap<>();
          hm.put("value", child.getId());
          hm.put("label", child.getName());
          hm.put("children", fieldFilter(child));
          list.add(hm);
        }
      }
    }
    return list;
  }
  
	 /**
	  * 获取特产供应商
	  * @return
	  */
		@RequestMapping(value = "/providers/view", method = RequestMethod.GET)
  public Json providerList()
  {
    Json json = new Json();
    try
    {
      List<Provider> list = this.providerServiceImpl.findAll();
      json.setSuccess(true);
      json.setMsg("查询成功");
      json.setObj(list);
    }
    catch (Exception e)
    {
      json.setSuccess(false);
      json.setMsg("查询失败");
      json.setObj(null);
    }
    return json;
  }
}
