package com.hongyu.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import com.hongyu.entity.*;
import com.hongyu.service.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Page;
import com.hongyu.Pageable;

@RestController
@RequestMapping({"/ymmall/promotion/"})
public class YmmallPromotionController
{
  @Resource(name="hyPromotionServiceImpl")
  HyPromotionService hyPromotionService;
  
  @RequestMapping({"normal/list"})
  public Json promotionlist(Pageable pageable, HyPromotion promotion, HttpSession session)
  {
    Json j = new Json();
    Map<String, Object> result = new HashMap();
    List<HashMap<String, Object>> lhm = new ArrayList();
    try
    {
      promotion.setStatus(HyPromotion.PromotionStatus.进行中);
      promotion.setPromotionType(HyPromotion.PromotionType.普通优惠);
      Page<HyPromotion> page = this.hyPromotionService.findPage(pageable, promotion);
      if (page.getRows().size() > 0) {
        for (HyPromotion p : page.getRows())
        {
          HashMap<String, Object> hm = new HashMap();
          hm.put("id", p.getId());
          hm.put("name", p.getPromotionName());
          hm.put("type", p.getPromotionType());
          hm.put("ruleType", p.getPromotionRule());
          hm.put("startTime", p.getPromotionStarttime());
          hm.put("endTime", p.getPromotionEndtime());
          hm.put("fullSubstracts", p.getHyFullSubstracts());
          hm.put("fullPresents", p.getHyFullPresents());
          hm.put("fullDiscounts", p.getHyFullDiscounts());

          hm.put("syncTagpic",p.getSyncTagpic());

          if(p.getSyncTagpic()){

            for (HySingleitemPromotion pro : p.getHySingleitemPromotions()) {
              hm.put("pics", pro.getSpecialtyId().getImages());
            }
          }else{
            hm.put("pics", p.getHyPromotionPics());
          }


          hm.put("divideMoney",p.getDivideMoney().setScale(0, BigDecimal.ROUND_DOWN));//最后展示的分成钱数向下取整，舍弃小数

          for (HySingleitemPromotion pro : p.getHySingleitemPromotions())
          {
            pro.getSpecialtyId().getSpecifications().clear();

            SpecialtySpecification s = pro.getSpecificationId();
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
            s.setMarketPrice(price.getMarketPrice());
            s.setPlatformPrice(price.getPlatformPrice());
            //运费
            s.setDeliverPrice(price.getDeliverPrice());

            Map<String,Object> map = new HashMap<>();
            //找提成比例
            Long weChatId=(Long)session.getAttribute("wechat_id");
            WechatAccount wechatAccount = wechatAccountService.find(weChatId);
            if(wechatAccount!=null) {
              List<Filter> wefilters = new ArrayList<>();
              wefilters.add(Filter.eq("wechatOpenId",wechatAccount.getWechatOpenid()));
              List<WeBusiness> weBusinesses = weBusinessService.findList(null,wefilters,null);

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
              s.setDividMoney(price.getPlatformPrice().subtract(price.getCostPrice()).subtract(
                  price.getDeliverPrice()).multiply((BigDecimal)map.get("divideRatio")).setScale(0, BigDecimal.ROUND_DOWN));//最后展示的分成钱数向下取整，舍弃小数
            }



            if (pro.getBusinessPersonDivide() != null) {
              pro.getBusinessPersonDivide().setOperator(null);
            }
            if (pro.getStoreDivide() != null) {
              pro.getStoreDivide().setOperator(null);
            }
            if (pro.getExterStoreDivide() != null) {
              pro.getExterStoreDivide().setOperator(null);
            }
          }
          hm.put("hySingleitemPromotions", p.getHySingleitemPromotions());

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


  @Resource(name="specialtyPriceServiceImpl")
  SpecialtyPriceService specialtyPriceServiceImpl;

  @Resource(name = "wechatAccountServiceImpl")
  WechatAccountService wechatAccountService;

  @Resource(name="weDivideModelServiceImpl")
  WeDivideModelService weDivideModelService;

  @Resource(name="weBusinessServiceImpl")
  WeBusinessService weBusinessService;

  @RequestMapping({"normal/detail"})
  public Json detail(Long id, HttpSession session)
  {
    Json j = new Json();
    try
    {
      Map<String, Object> result = new HashMap();
      HyPromotion promotion = (HyPromotion)this.hyPromotionService.find(id);
      result.put("id", promotion.getId());
      
      result.put("introduction", promotion.getIntroduction());
      result.put("couponAvailable", promotion.getCouponAvailable());
      result.put("divideMoney",promotion.getDivideMoney().setScale(0, BigDecimal.ROUND_DOWN));//最后展示的分成钱数向下取整，舍弃小数

      for (HySingleitemPromotion p : promotion.getHySingleitemPromotions())
      {
        p.getSpecialtyId().getSpecifications().clear();

        SpecialtySpecification s = p.getSpecificationId();
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
        s.setMarketPrice(price.getMarketPrice());
        s.setPlatformPrice(price.getPlatformPrice());
        //运费
        s.setDeliverPrice(price.getDeliverPrice());

        Map<String,Object> map = new HashMap<>();
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
          s.setDividMoney(price.getPlatformPrice().subtract(price.getCostPrice()).subtract(
              price.getDeliverPrice()).multiply((BigDecimal)map.get("divideRatio")).setScale(0, BigDecimal.ROUND_DOWN));//最后展示的分成钱数向下取整，舍弃小数
        }



        if (p.getBusinessPersonDivide() != null) {
          p.getBusinessPersonDivide().setOperator(null);
        }
        if (p.getStoreDivide() != null) {
          p.getStoreDivide().setOperator(null);
        }
        if (p.getExterStoreDivide() != null) {
          p.getExterStoreDivide().setOperator(null);
        }
      }
      result.put("hySingleitemPromotions", promotion.getHySingleitemPromotions());
      
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
  
  
  @RequestMapping({"normal/sub_list"})
  public Json promotionSublist(Integer size, HyPromotion promotion, HttpSession session)
  {
    Json j = new Json();
    Map<String, Object> result = new HashMap();
    List<HashMap<String, Object>> lhm = new ArrayList();
    try
    {
    	List<Filter> filters = new ArrayList<>();
    	filters.add(Filter.eq("status", HyPromotion.PromotionStatus.进行中));
    	filters.add(Filter.eq("promotionType", HyPromotion.PromotionType.普通优惠));
    	
      List<HyPromotion> rows = this.hyPromotionService.findList(null,filters,null);
      if (rows!=null && !rows.isEmpty()) {
        for (HyPromotion p : rows)
        {
          HashMap<String, Object> hm = new HashMap();
          hm.put("id", p.getId());
          hm.put("name", p.getPromotionName());
          hm.put("type", p.getPromotionType());
          hm.put("ruleType", p.getPromotionRule());
          hm.put("startTime", p.getPromotionStarttime());
          hm.put("endTime", p.getPromotionEndtime());
          hm.put("fullSubstracts", p.getHyFullSubstracts());
          hm.put("fullPresents", p.getHyFullPresents());
          hm.put("fullDiscounts", p.getHyFullDiscounts());

          if(p.getSyncTagpic()){
            for (HySingleitemPromotion pro : p.getHySingleitemPromotions()) {
              hm.put("pics", pro.getSpecialtyId().getImages());
            }
          }else{
            hm.put("pics", p.getHyPromotionPics());
          }

          hm.put("divideMoney",p.getDivideMoney().setScale(0, BigDecimal.ROUND_DOWN));//最后展示的分成钱数向下取整，舍弃小数

          for (HySingleitemPromotion pro : p.getHySingleitemPromotions())
          {
            pro.getSpecialtyId().getSpecifications().clear();

            SpecialtySpecification s = pro.getSpecificationId();
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
            s.setMarketPrice(price.getMarketPrice());
            s.setPlatformPrice(price.getPlatformPrice());
            //运费
            s.setDeliverPrice(price.getDeliverPrice());

            Map<String,Object> map = new HashMap<>();
            //找提成比例
            Long weChatId=(Long)session.getAttribute("wechat_id");
            WechatAccount wechatAccount = wechatAccountService.find(weChatId);
            if(wechatAccount!=null) {
              List<Filter> wefilters = new ArrayList<>();
              wefilters.add(Filter.eq("wechatOpenId",wechatAccount.getWechatOpenid()));
              List<WeBusiness> weBusinesses = weBusinessService.findList(null,wefilters,null);

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
              s.setDividMoney(price.getPlatformPrice().subtract(price.getCostPrice()).subtract(
                  price.getDeliverPrice()).multiply((BigDecimal)map.get("divideRatio")).setScale(0, BigDecimal.ROUND_DOWN));//最后展示的分成钱数向下取整，舍弃小数
            }



            if (pro.getBusinessPersonDivide() != null) {
              pro.getBusinessPersonDivide().setOperator(null);
            }
            if (pro.getStoreDivide() != null) {
              pro.getStoreDivide().setOperator(null);
            }
            if (pro.getExterStoreDivide() != null) {
              pro.getExterStoreDivide().setOperator(null);
            }
          }
          hm.put("hySingleitemPromotions", p.getHySingleitemPromotions());
          lhm.add(hm);
        }
      }

      j.setSuccess(true);
      j.setMsg("查询成功");
      j.setObj(lhm.subList(0, size>lhm.size()?lhm.size():size));
      return j;
    }
    catch (Exception e)
    {
      j.setSuccess(false);
      j.setMsg(e.getMessage());
    }
    return j;
  }
  
  @RequestMapping({"group/list"})
  public Json groupPromotionlist(Pageable pageable, HyPromotion promotion)
  {
    Json j = new Json();
    Map<String, Object> result = new HashMap();
    List<HashMap<String, Object>> lhm = new ArrayList();
    try
    {
      promotion.setStatus(HyPromotion.PromotionStatus.进行中);
      promotion.setPromotionType(HyPromotion.PromotionType.组合优惠);
      Page<HyPromotion> page = this.hyPromotionService.findPage(pageable, promotion);
      if (page.getRows().size() > 0) {
        for (HyPromotion p : page.getRows())
        {
          HashMap<String, Object> hm = new HashMap();
          hm.put("id", p.getId());
          hm.put("name", p.getPromotionName());
          hm.put("type", p.getPromotionType());
          hm.put("ruleType", p.getPromotionRule());
          hm.put("startTime", p.getPromotionStarttime());
          hm.put("endTime", p.getPromotionEndtime());
          hm.put("fullSubstracts", p.getHyFullSubstracts());
          hm.put("fullPresents", p.getHyFullPresents());
          hm.put("fullDiscounts", p.getHyFullDiscounts());
          hm.put("pics", p.getHyPromotionPics());
          hm.put("groupItemPromotions", p.getHyGroupitemPromotions());
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
  
  
  @RequestMapping({"group/sub_list"})
  public Json groupPromotionSublist(Integer size, HyPromotion promotion)
  {
    Json j = new Json();
    Map<String, Object> result = new HashMap();
    List<HashMap<String, Object>> lhm = new ArrayList();
    try
    {
    	List<Filter> filters = new ArrayList<>();
    	filters.add(Filter.eq("status", HyPromotion.PromotionStatus.进行中));
    	filters.add(Filter.eq("promotionType", HyPromotion.PromotionType.组合优惠));

        List<HyPromotion> rows = this.hyPromotionService.findList(null,filters,null);
      if (rows!=null && !rows.isEmpty()) {
        for (HyPromotion p : rows)
        {
          HashMap<String, Object> hm = new HashMap();
          hm.put("id", p.getId());
          hm.put("name", p.getPromotionName());
          hm.put("type", p.getPromotionType());
          hm.put("ruleType", p.getPromotionRule());
          hm.put("startTime", p.getPromotionStarttime());
          hm.put("endTime", p.getPromotionEndtime());
          hm.put("fullSubstracts", p.getHyFullSubstracts());
          hm.put("fullPresents", p.getHyFullPresents());
          hm.put("fullDiscounts", p.getHyFullDiscounts());
          hm.put("pics", p.getHyPromotionPics());
          hm.put("groupItemPromotions", p.getHyGroupitemPromotions());
          lhm.add(hm);
        }
      }

      j.setSuccess(true);
      j.setMsg("查询成功");
      j.setObj(lhm.subList(0, size>lhm.size()?lhm.size():size));
      return j;
    }
    catch (Exception e)
    {
      j.setSuccess(false);
      j.setMsg(e.getMessage());
    }
    return j;
  }
  
  @RequestMapping({"group/detail"})
  public Json groupDetail(Long id,HttpSession session)
  {
    Json j = new Json();
    try
    {
      Map<String, Object> result = new HashMap();
      HyPromotion promotion = (HyPromotion)this.hyPromotionService.find(id);
      result.put("id", promotion.getId());
      result.put("name", promotion.getPromotionName());
      result.put("type", promotion.getPromotionType());
      result.put("ruleType", promotion.getPromotionRule());
      result.put("startTime", promotion.getPromotionStarttime());
      result.put("endTime", promotion.getPromotionEndtime());
      result.put("fullSubstracts", promotion.getHyFullSubstracts());
      result.put("fullPresents", promotion.getHyFullPresents());
      result.put("fullDiscounts", promotion.getHyFullDiscounts());
      result.put("pics", promotion.getHyPromotionPics()); 
      result.put("introduction", promotion.getIntroduction());
      result.put("couponAvailable", promotion.getCouponAvailable());
      result.put("divideMoney", promotion.getDivideMoney().setScale(0, BigDecimal.ROUND_DOWN));//最后展示的分成钱数向下取整，舍弃小数
      for (HyGroupitemPromotion p : promotion.getHyGroupitemPromotions())
      {
        if (p.getBusinessPersonDivide() != null) {
          p.getBusinessPersonDivide().setOperator(null);
        }
        if (p.getStoreDivide() != null) {
          p.getStoreDivide().setOperator(null);
        }
        if (p.getExterStoreDivide() != null) {
          p.getExterStoreDivide().setOperator(null);
        }

        Map<String,Object> map = new HashMap<>();
        //找提成比例
        Long weChatId=(Long)session.getAttribute("wechat_id");
        WechatAccount wechatAccount = wechatAccountService.find(weChatId);


        BigDecimal costPrice = BigDecimal.ZERO; //总成本价
        BigDecimal deliverPrice = BigDecimal.ZERO;  //总运费


        for(HyGroupitemPromotionDetail detail : p.getHyGroupitemPromotionDetails()){
          SpecialtySpecification s = detail.getItemSpecificationId();
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
          s.setMarketPrice(price.getMarketPrice());
          s.setPlatformPrice(price.getPlatformPrice());
          //运费
          s.setDeliverPrice(price.getDeliverPrice());

          costPrice = costPrice.add(price.getCostPrice());
		  deliverPrice = deliverPrice.add(price.getDeliverPrice());

          //找提成比例
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
            s.setDividMoney(price.getPlatformPrice().subtract(price.getCostPrice()).subtract(
                price.getDeliverPrice()).multiply((BigDecimal)map.get("divideRatio")).setScale(0, BigDecimal.ROUND_DOWN));//最后展示的分成钱数向下取整，舍弃小数
          }


        }


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
                map.put("divideRatio", p.getStoreDivide().getProportion().multiply(weDivideModels.get(0).getWeBusiness()));
                break;
              case 1:
                //找提成模型
                List<Filter> filters5=new ArrayList<>();
                filters5.add(Filter.eq("modelType","非虹宇门店"));
                filters5.add(Filter.eq("isValid", true));
                List<WeDivideModel> weDivideModels1=weDivideModelService.findList(null,filters5,null);
                map.put("divideRatio", p.getExterStoreDivide().getProportion().multiply(weDivideModels1.get(0).getWeBusiness()));
                break;
              case 2:
                //找提成模型
                List<Filter> filters6=new ArrayList<>();
                filters6.add(Filter.eq("modelType","个人商贸"));
                filters6.add(Filter.eq("isValid", true));
                List<WeDivideModel> weDivideModels2=weDivideModelService.findList(null,filters6,null);
                map.put("divideRatio", p.getBusinessPersonDivide().getProportion().multiply(weDivideModels2.get(0).getWeBusiness()));
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
          result.put("dividMoney",p.getSellPrice().subtract(costPrice).subtract(
              deliverPrice).multiply((BigDecimal)map.get("divideRatio")).setScale(0, BigDecimal.ROUND_DOWN));//最后展示的分成钱数向下取整，舍弃小数
        }
      }
      result.put("hyGroupitemPromotions", promotion.getHyGroupitemPromotions());
      
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
}
