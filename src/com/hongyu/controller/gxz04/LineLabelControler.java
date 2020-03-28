package com.hongyu.controller.gxz04;

import com.hongyu.*;
import com.hongyu.controller.cwz.LineLabelController;
import com.hongyu.entity.*;
import com.hongyu.entity.HyLine.LineType;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.service.*;
import com.hongyu.util.AuthorityUtils;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by guoxinze on 2019/6/20.
 */
@RestController
@Transactional(propagation = Propagation.REQUIRED)
@RequestMapping("/admin/line/label/")
public class LineLabelControler {


    @Resource(name = "hyLineLabelServiceImpl")
    HyLineLabelService hyLineLabelService;

    @Resource(name = "hySpecialtyLineLabelServiceImpl")
    HySpecialtyLineLabelService hySpecialtyLineLabelService;

    @Resource(name = "couponMoneyServiceImpl")
    CouponMoneyService couponMoneyService;

    @Resource
    private TaskService taskService;

    @Resource
    private RuntimeService runtimeService;

    @Resource
    private HistoryService historyService;

    @Resource(name = "commonEdushenheServiceImpl")
    private CommonEdushenheService commonEdushenheService;

    @Resource(name = "storeServiceImpl")
    StoreService storeService;

    @Resource(name = "hyLineServiceImpl")
    HyLineService hyLineService;

    @Resource(name = "hyLineTravelsServiceImpl")
    HyLineTravelsService hyLineTravelsService;

    @Resource(name = "hyLineRefundServiceImpl")
    HyLineRefundService hyLineRefundService;

    @Resource(name = "transportServiceImpl")
    TransportService transportService;

    @Resource(name = "hyGroupServiceImpl")
    HyGroupService hyGroupService;

    @Resource(name = "departmentServiceImpl")
    private DepartmentService departmentService;

    @Resource(name="hyAdminServiceImpl")
    private HyAdminService hyAdminService;

    @Resource(name = "hyAreaServiceImpl")
    HyAreaService  hyAreaService;

    @Resource(name = "insuranceServiceImpl")
    InsuranceService insuranceService;

    @Resource(name="hySupplierContractServiceImpl")
    private HySupplierContractService hySupplierContractService;

    @Resource(name = "lineCatagoryServiceImpl")
    LineCatagoryService lineCatagoryService;

    @Resource(name="commonSequenceServiceImp")
    CommonSequenceService commonSequenceService;

    @Resource(name = "hyGroupOtherpriceServiceImpl")
    HyGroupOtherpriceService hyGroupOtherpriceService;

    @Resource(name = "groupBiankoudianServiceImpl")
    GroupBiankoudianService groupBiankoudianService;

    @Resource(name = "groupDivideServiceImpl")
    GroupDivideService groupDivideService;

    @Resource(name = "groupMemberServiceImpl")
    GroupMemberService groupMemberService;

    @Resource(name = "hyRegulateServiceImpl")
    HyRegulateService hyRegulateService;

    @Resource(name = "hyProviderRebateServiceImpl")
    HyProviderRebateService hyProviderRebateService;

    /**
     * 线路的列表页
     * @param lineCatagoryId 线路类型
     * @param lineAuditStatus 线路状态
     * @param groupAuditStatus 团期状态
     * @param isSale 上线状态
     * @param isCancel 取消状态
     * @return
     */
    @RequestMapping(value="list/view")
    public Json list(Pageable pageable, HyLine hyLine, Long lineCategory,
                     HttpSession session, HttpServletRequest request) {
        Json j = new Json();
        try {
            Map<String, Object> obj = new HashMap<String, Object>();
            List<Map<String, Object>> lhm = new ArrayList<>();

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
            filters.add(Filter.in("operator", hyAdmins));
            if(lineCategory != null) {
                filters.add(Filter.eq("lineCategory", lineCatagoryService.find(lineCategory)));
            }

            //新增合同负责人可以看到子账号产品的逻辑

            Set<HySupplierContract> cs =admin.getLiableContracts();
            if(!cs.isEmpty()) {
                filters.add(Filter.in("contract", cs));
            }


            List<Order> orders = new ArrayList<>();
            orders.add(Order.desc("createDate"));
            pageable.setFilters(filters);
            pageable.setOrders(orders);
            Page<HyLine> lines = hyLineService.findPage(pageable, hyLine);

            if(lines.getRows().size() > 0) {
                for(HyLine line : lines.getRows()) {
                    HashMap<String, Object> hm = new HashMap<String, Object>();
                    HyAdmin creater = line.getOperator();
                    hm.put("id", line.getId());
                    hm.put("pn", line.getPn());
                    hm.put("supplierName", line.getContract().getHySupplier().getSupplierName());
                    hm.put("lineName", line.getName());
                    String province = hyAreaService.find(line.getArea().getTreePaths().get(0)).getName(); //得到省份信息
                    hm.put("area", province);
                    hm.put("lineType", line.getLineType());
                    hm.put("lineCategory", line.getLineCategory());
                    hm.put("days", line.getDays());

                    hm.put("latestGroup", line.getLatestGroup());
                    hm.put("lineAuditStatus", line.getLineAuditStatus());
                    hm.put("groupAuditStatus", line.getGroupAuditStatus());
                    hm.put("isSale", line.getIsSale());
                    hm.put("isInner", line.getIsInner());
                    hm.put("isCancel", line.getIsCancel());
                    hm.put("isEdit", line.getIsEdit());
                    hm.put("isTop", line.getIsTop());

                    if (creater != null) {
                        hm.put("operator", creater.getName());
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

            obj.put("pageSize", Integer.valueOf(lines.getPageSize()));
            obj.put("pageNumber", Integer.valueOf(lines.getPageNumber()));
            obj.put("total", Long.valueOf(lines.getTotal()));
            obj.put("rows", lhm);

            j.setSuccess(true);
            j.setMsg("获取列表成功");
            j.setObj(obj);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            j.setSuccess(false);
            j.setMsg(e.getMessage());
        }
        return j;
    }

    /**
     * 获取线路二级分类
     * @param pageable
     * @param entity
     * @return
     */
    @RequestMapping(value="catagory/view")
    public Json catagory(LineType yijifenlei) {
        Json j = new Json();
        try{
            List<Filter> filters = new ArrayList<>();
            filters.add(Filter.eq("yijifenlei", yijifenlei));
            filters.add(Filter.eq("status", true));
            List<LineCatagoryEntity> lineCatagoryEntities = lineCatagoryService.findList(null, filters, null);
            j.setSuccess(true);
            j.setMsg("查看成功！");
            j.setObj(lineCatagoryEntities);
        }catch (Exception e) {
            // TODO Auto-generated catch block
            j.setSuccess(false);
            j.setMsg(e.getMessage());
        }
        return j;
    }

    /**
     * 详情页 -包括新建页面和编辑页面
     */
    @RequestMapping(value = "detail/view")
    public Json detail(Long id, HttpSession session) {
        Json j = new Json();
        try {
            /**
             * 获取当前用户
             */
            String username = (String) session.getAttribute(CommonAttributes.Principal);
            HyAdmin admin = hyAdminService.find(username);
            //********************** 页面公共的部分 **********************
            HashMap<String, Object> obj = new HashMap<>();
            //合同下拉列表
            List<Map<String, Object>> hetongs = new ArrayList<>();
            //保险方案下拉列表
            List<Map<String, Object>> baoxians = new ArrayList<>();

            HySupplierContract contract = null;

            HyAdmin liable = admin;

            if(admin.getHyAdmin() != null) {
                liable = admin.getHyAdmin();
            }

            Set<HySupplierContract> cs = liable.getLiableContracts();
            for(HySupplierContract c : cs) {
                HashMap<String, Object> hm = new HashMap<>();
                if(c.getContractStatus() == HySupplierContract.ContractStatus.zhengchang) {
                    hm.put("id", c.getId());
                    hm.put("contractCode", c.getContractCode());
                    contract = c;
                    hetongs.add(hm);
                    break;
                }
            }


            obj.put("contracts", hetongs);
            if(contract != null) {
                if(admin.getHyAdmin() != null) {
                    obj.put("chujingAreas", admin.getAreaChujing());
                    obj.put("guoneiAreas", admin.getAreaGuonei());
                    obj.put("qicheAreas", admin.getAreaQiche());
                } else {
                    obj.put("chujingAreas", contract.getChujingAreas());
                    obj.put("guoneiAreas", contract.getGuoneiAreas());
                    obj.put("qicheAreas", contract.getQicheAreas());
                }

            } else if(id != null) {
                HyLine line = hyLineService.find(id);
                if(line.getArea() != null && line.getContract() != null) {
                    obj.put("areaName", line.getArea().getName());
                    obj.put("contractCode", line.getContract().getContractCode());
                }

            } else { //如果过期不让新建线路
                throw new RuntimeException("合同过期，无法新建线路!");
            }

            List<Filter> filters = new ArrayList<Filter>();
            filters.add(Filter.in("status", true));
            obj.put("transports", transportService.findList(null, filters, null));

            List<Insurance> ins = insuranceService.findAll();
            for(Insurance in : ins) {
                HashMap<String, Object> hm = new HashMap<>();
                hm.put("id", in.getId());
                hm.put("insuranceCode", in.getInsuranceCode());
                hm.put("remark", in.getRemark());
                baoxians.add(hm);
            }
            obj.put("baoxian", baoxians);

            if(contract != null && contract.getHySupplier() != null) {
                obj.put("isInner", contract.getHySupplier().getIsInner());
            }


            //************* 判断是不是编辑的页面 *************
            if(id != null) {
                HyLine line = hyLineService.find(id);
                obj.put("line", line);
            }
            j.setSuccess(true);
            j.setMsg("查看详情成功");
            j.setObj(obj);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            j.setSuccess(false);
            j.setMsg(e.getMessage());
        }
        return j;
    }

    @RequestMapping(value = "listGroup/view")
    public Json listGroup(Pageable pageable, Long lineId, HyGroup hyGroup) {
        Json j = new Json();
        try {
            HashMap<String, Object> jiagebili = new HashMap<>();
            Map<String, Object> obj = new HashMap<String, Object>();
            List<Map<String, Object>> lhm = new ArrayList<>();

            HyLine line = hyLineService.find(lineId);
            List<Filter> filters = new ArrayList<Filter>();
            filters.add(Filter.eq("line", line));

            List<Order> orders = new ArrayList<>();
            orders.add(Order.desc("createDate"));
            pageable.setFilters(filters);
            pageable.setOrders(orders);

            Page<HyGroup> groups = hyGroupService.findPage(pageable, hyGroup);

            if(groups.getRows().size() > 0) {
                for(HyGroup group : groups.getRows()) {
                    HashMap<String, Object> hm = new HashMap<String, Object>();
                    hm.put("id", group.getId());
                    hm.put("startDay", group.getStartDay());
                    hm.put("endDay", group.getEndDay());
                    hm.put("lowestPrice", group.getLowestPrice());
                    hm.put("teamType", group.getTeamType());
                    hm.put("publishRange", group.getPublishRange());
                    hm.put("stock", group.getStock());
                    hm.put("auditStatus", group.getAuditStatus());
                    hm.put("groupState", group.getGroupState());
                    hm.put("signupNumber", group.getSignupNumber());
                    hm.put("occupyNumber", group.getOccupyNumber());
                    List<HyGroupPrice> gps = new ArrayList<>(group.getHyGroupPrices());
                    if(!gps.isEmpty()) {
                        hm.put("adultPrice", gps.get(0).getAdultPrice());
                        hm.put("adultPrice1", gps.get(0).getAdultPrice1());
                    }

                    lhm.add(hm);
                }
            }

            obj.put("pageSize", Integer.valueOf(groups.getPageSize()));
            obj.put("pageNumber", Integer.valueOf(groups.getPageNumber()));
            obj.put("total", Long.valueOf(groups.getTotal()));
            if(line.getLineType() == LineType.guonei) {
                obj.put("koudianXianlu", line.getContract().getHySupplierDeductGuonei());
            } else if (line.getLineType() == LineType.chujing) {
                obj.put("koudianXianlu", line.getContract().getHySupplierDeductChujing());
            } else if (line.getLineType() == LineType.qiche) {
                obj.put("koudianXianlu", line.getContract().getHySupplierDeductQiche());
            }
            obj.put("endDate", line.getContract().getDeadDate().getTime() - (long)(line.getDays()-1)*24*60*60*1000);

            //加入价格比例
            filters.clear();
            filters.add(Filter.eq("eduleixing", CommonShenheedu.Eduleixing.guoneijiagebili));
            List<CommonShenheedu> edu = commonEdushenheService.findList(null, filters, null);
            BigDecimal money = edu.get(0).getMoney();
            jiagebili.put("guonei", money);

            filters.clear();
            filters.add(Filter.eq("eduleixing", CommonShenheedu.Eduleixing.chujingjiagebili));
            List<CommonShenheedu> edu1 = commonEdushenheService.findList(null, filters, null);
            BigDecimal money1 = edu1.get(0).getMoney();
            jiagebili.put("chujing", money1);

            filters.clear();
            filters.add(Filter.eq("eduleixing", CommonShenheedu.Eduleixing.qichejiagebili));
            List<CommonShenheedu> edu2 = commonEdushenheService.findList(null, filters, null);
            BigDecimal money2 = edu2.get(0).getMoney();
            jiagebili.put("qiche", money2);

            filters.clear();
            filters.add(Filter.eq("eduleixing", CommonShenheedu.Eduleixing.piaowujiagebili));
            List<CommonShenheedu> edu3 = commonEdushenheService.findList(null, filters, null);
            BigDecimal money3 = edu3.get(0).getMoney();
            jiagebili.put("piaowu", money3);

            obj.put("rows", lhm);
            obj.put("jiagebili", jiagebili);
            j.setSuccess(true);
            j.setMsg("获取列表成功");
            j.setObj(obj);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            j.setSuccess(false);
            j.setMsg(e.getMessage());
        }
        return j;
    }

    /**
     * 团详情列表
     * @param id 团id
     * @param session
     * @return
     */
    @RequestMapping(value = "detailGroup/view")
    public Json detailGroup(Long id, HttpSession session) {
        Json j = new Json();
        try {

            HyGroup group = hyGroupService.find(id);

            j.setSuccess(true);
            j.setMsg("查看详情成功");
            j.setObj(group);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            j.setSuccess(false);
            j.setMsg(e.getMessage());
        }
        return j;
    }

    //状态status（没有值查出来全部的 0无效 1有效） 标签名称labelName 是筛选条件
    //全用get方法
    @RequestMapping(value = "view") // 1 没问题
    public Json labelview(Integer status, String labelName, Pageable pageable) {
        // 接收前端传过来的json数据
        Json j = new Json();

        List<HyLineLabel> resAll = new ArrayList<>();
        Page<List<Object[]>> pageResults = null;
        Map<String,Object> map = new HashMap<String,Object>();

        try {
            //————————————————如果没传过来 就把数据库所有字段都拿出来 同时名称也是null
            if (status == null) {

                Map<String, Object> params = new HashMap<>();
                List<Object[]> list = new ArrayList<>();

                if(labelName == null){
                    //查询数据库hy_label 所有字段
                    String jpql = "select * from hy_linelabel";
                    pageResults = hyLineLabelService.findPageBysql(jpql, pageable);
                    list = pageResults.getLstObj();

                } else{

                    String jpql = "select * from hy_linelabel h where h.name = :hyLabelName";
                    params.put("hyLabelName",labelName);
                    pageResults = hyLineLabelService.findPageBySqlAndParam(jpql,params,pageable);
                    list = pageResults.getLstObj();
                }

                List<HyLineLabel> res = new ArrayList<>();
                HyLineLabel hyLabel = null;

                for (Object[] arr : list) {
                    hyLabel = new HyLineLabel();

                    //难点在于 把原始的timeStamp换为Date
                    java.sql.Timestamp timestamp = (Timestamp) arr[3];


                    hyLabel.setID(Long.valueOf(arr[0].toString()));
                    hyLabel.setProductName((String)arr[1]);
                    hyLabel.setOperatorName((String)arr[2]);
                    hyLabel.setCreateTime(new java.sql.Date(timestamp.getTime()));
                    hyLabel.setIsActive((Boolean)arr[4]);
                    hyLabel.setIconUrl((String)arr[5]);

                    res.add(hyLabel);
                }
                resAll=res;

                //——————————————————如果status是0的话
            } else if (status == 0) {

                Map<String, Object> params = new HashMap<>();
                List<Object[]> list = new ArrayList<>();
                //有名字
                if(labelName!=null){

                    String jpql = "select * from hy_linelabel h where h.name = :hyLabelName and h.is_active = :isActive";
                    params.put("hyLabelName",labelName);
                    params.put("isActive",0);
                    pageResults = hyLineLabelService.findPageBySqlAndParam(jpql,params,pageable);
                    list = pageResults.getLstObj();

                    //没有名字
                } else{

                    String jpql = "select * from hy_linelabel h where h.is_active = :isActive";
                    params.put("isActive",0);
                    pageResults = hyLineLabelService.findPageBySqlAndParam(jpql,params,pageable);
                    list = pageResults.getLstObj();
                }

                List<HyLineLabel> res = new ArrayList<>();
                HyLineLabel hyLabel = null;

                for (Object[] arr : list) {
                    hyLabel = new HyLineLabel();

                    //难点在于 把原始的timeStamp换为Date
                    java.sql.Timestamp timestamp = (Timestamp) arr[3];

                    hyLabel.setID(Long.valueOf(arr[0].toString()));
                    hyLabel.setProductName((String)arr[1]);
                    hyLabel.setOperatorName((String)arr[2]);
                    hyLabel.setCreateTime(new java.sql.Date(timestamp.getTime()));
                    hyLabel.setIsActive((Boolean)arr[4]);
                    hyLabel.setIconUrl((String)arr[5]);

                    res.add(hyLabel);
                }
                resAll = res;

                //——————————————————如果status是1的话
            } else if (status == 1) {

                Map<String, Object> params = new HashMap<>();
                List<Object[]> list = new ArrayList<>();
                //有名字
                if(labelName!=null){

                    String jpql = "select * from hy_linelabel h where h.name = :hyLabelName and h.is_active = :isActive";
                    params.put("hyLabelName",labelName);
                    params.put("isActive",1);
                    pageResults = hyLineLabelService.findPageBySqlAndParam(jpql,params,pageable);
                    list = pageResults.getLstObj();

                    //没有名字
                } else{

                    String jpql = "select * from hy_linelabel h where h.is_active = :isActive";
                    params.put("isActive",1);
                    pageResults = hyLineLabelService.findPageBySqlAndParam(jpql,params,pageable);
                    list = pageResults.getLstObj();
                }

                List<HyLineLabel> res = new ArrayList<>();
                HyLineLabel hyLabel = null;

                for (Object[] arr : list) {
                    hyLabel = new HyLineLabel();

                    //难点在于 把原始的timeStamp换为Date
                    java.sql.Timestamp timestamp = (Timestamp) arr[3];

                    hyLabel.setID(Long.valueOf(arr[0].toString()));
                    hyLabel.setProductName((String)arr[1]);
                    hyLabel.setOperatorName((String)arr[2]);
                    hyLabel.setCreateTime(new java.sql.Date(timestamp.getTime()));
                    hyLabel.setIsActive((Boolean)arr[4]);
                    hyLabel.setIconUrl((String)arr[5]);

                    res.add(hyLabel);
                }
                resAll = res;
                //resAll.add(pageable);

            } else {
                j.setSuccess(false);
                j.setMsg("进入非法区域");
            }

            map.put("rows", resAll);//黄色的字可以和前台约定，随便起
            map.put("pageNumber", Integer.valueOf(pageable.getPage()));//当前是第几页
            map.put("pageSize", Integer.valueOf(pageable.getRows()));//每一页有多少条 默认是一页多少
            map.put("total",Long.valueOf(pageResults.getTotal()));
        } catch (Exception e) {
            j.setSuccess(false);
            j.setMsg(e.getMessage());
        }
        j.setSuccess(true);
        j.setObj(map);
        return j;
    }


    @RequestMapping(value = "add") // 2
    public Json labelAdd(String labelName, Boolean status, String iconURL, HttpSession httpSession) {
        //insert into hy_label values (null,'老二','小三','2018-08-15 17:18:23',1,'www.baidu.com')
        // 接收前端传过来的json数据
        Json j = new Json();

        String operatorName = (String) httpSession.getAttribute(CommonAttributes.Principal);
        String username = labelName;

        try {
            //-------------容错
            if(operatorName == null){
                operatorName = "无名氏";
            }
            if(username == null){
                username = "也是无名氏";
            }
            if(status == null){
                status = false;
            }
            if(iconURL == null){
                iconURL = "www.null.com";
            }

//				Map<String, Object> params = new HashMap<>();
//				Date date = new Date();
//
//				//由于不知道能不能用sql插入 先放在这里
//				String jpql = "insert into hy_label h values (null,'h.name = :userName1',"
//						+ "'h.operator = :operaterName1',h.create_time = :newDate,h.is_active = :isActive,"
//						+ "'h.icon_url = :iconURL1')";
//				params.put("userName1",username);
//				params.put("operaterName1",operatorName);
//				params.put("newDate",date);
//				params.put("isActive",status);
//				params.put("iconURL1",iconURL);
//				params.put("isActive",1);
//				hyLabelService.findPageBySqlAndParam(jpql,params,new Pageable());

            HyLineLabel hyLabel = new HyLineLabel();
            hyLabel.setProductName(username);
            hyLabel.setOperatorName(operatorName);
            hyLabel.setCreateTime(new Date());
//				if(status == 0){
//					hyLabel.setIsActive(false);
//				}else if(status == 1) {
//					hyLabel.setIsActive(true);
//				}
            hyLabel.setIsActive(status);
            hyLabel.setIconUrl(iconURL);

            hyLineLabelService.save(hyLabel);

        } catch (Exception e) {
            j.setSuccess(false);
            j.setMsg(e.getMessage());
        }
        j.setSuccess(true);
        j.setMsg("已经成功保存");
        return j;
    }

    @RequestMapping(value = "update") // 3
    public Json labelUpdate(Long ID, String labelName, String operatorName, Boolean status, String iconURL, HttpSession httpSession) {
        // 接收前端传过来的json数据
        Json j = new Json();
        try {

            //String username = (String) httpSession.getAttribute(CommonAttributes.Principal);
            String username = labelName;

            //-------------容错
            if(username == null){
                username = "前端没传";
            }
            if(operatorName == null){
                operatorName = "更改无名氏";
            }
            if(status == null){
                status = false;
            }
            if(iconURL == null){
                iconURL = "www.nothingchange.com";
            }

            HyLineLabel hyLabel = new HyLineLabel();
            hyLabel.setID(ID);
            hyLabel.setProductName(username);
            hyLabel.setOperatorName(operatorName);
            hyLabel.setCreateTime(new Date());
//			if(status == 0){
//				hyLabel.setIsActive(false);
//			}else if(status == 1) {
//				hyLabel.setIsActive(true);
//			}
            hyLabel.setIsActive(status);
            hyLabel.setIconUrl(iconURL);

            hyLineLabelService.update(hyLabel,"ID");


        } catch (Exception e) {
            j.setSuccess(false);
            j.setMsg(e.getMessage());
        }
        j.setSuccess(true);
        j.setMsg("更新成功");
        return j;
    }



    @RequestMapping(value = "check") // 4 没问题
    public Json labelCheck(Long lineID, Pageable pageable) {
        // 接收前端传过来的json数据
        Json j = new Json();
        List<LineLabelController.Wrap> resAll = new ArrayList<>();
        Map<String,Object> map = new HashMap<String,Object>();
        pageable.setPage(1);
        pageable.setRows(10000);

        try {

            //根据spetialtyID去找labelID，去label表里读出所有数据
            //select b.name,b.is_active from hy_label b,hy_specialty_label s where b.ID = s.label_id and s.specialty_id = 77
            Map<String, Object> params = new HashMap<>();
            List<Object[]> list = new ArrayList<>();

//			String jpql = "select b.ID,b.name,s.is_marked from hy_label b,hy_specialty_label s where b.ID = s.label_id "
//					+ "and s.specialty_id = :specialtyId and b.is_active = 1";

            //select hy_label.*,hy_specialty_label.is_marked is_marked from hy_label left join hy_specialty_label on hy_label.ID=hy_specialty_label.label_id and hy_specialty_label.specialty_id=123 and hy_specialty_label.is_marked=1;
//			String jpql = "select b.ID,b.name from hy_label b where b.is_active = :isActiveNow";

//			String jpql = "select hy_label.ID,hy_label.name,hy_specialty_label.is_marked is_marked "
//					+ "from hy_label left join hy_specialty_label on hy_label.ID=hy_specialty_label.label_id "
//					+ "where hy_specialty_label.specialty_id=:specialtyId and hy_label.is_active=1";

            String jpql = "select a.ID,a.name,b.is_marked is_marked "
                    +"from (select * from hy_linelabel where is_active=1) as a left join"
                    +"(select * from hy_specialty_linelabel where line_id=:lineId) as b on a.ID = b.label_id";

            params.put("lineId",lineID);
//			params.put("isActiveNow",1);
            Page<List<Object[]>> pageResults = hyLineLabelService.findPageBySqlAndParam(jpql,params,pageable);
            list = pageResults.getLstObj();


            List<LineLabelController.Wrap> res = new ArrayList<>();
            LineLabelController.Wrap wrap = null;

            for (Object[] arr : list) {
                wrap = new LineLabelController.Wrap();

                BigInteger a = (BigInteger)arr[0];
                wrap.setID(a.longValue());
                wrap.setProductName((String)arr[1]);
                if(arr[2] == null){
                    wrap.setIsMarked(false);
                } else{
                    wrap.setIsMarked((Boolean)arr[2]);
                }

                res.add(wrap);
            }
            resAll = res;

            map.put("rows", resAll);//黄色的字可以和前台约定，随便起
            map.put("pageNumber", Integer.valueOf(pageable.getPage()));//当前是第几页
            map.put("pageSize", Integer.valueOf(pageable.getRows()));//每一页有多少条 默认是一页多少
            map.put("total",Long.valueOf(pageResults.getTotal()));
        } catch (Exception e) {
            j.setSuccess(false);
            j.setMsg(e.getMessage());
        }
        j.setSuccess(true);
        //不要分页
        j.setObj(resAll);
        j.setMsg("成功");
        return j;
    }

    public static class Wrap{
        private Long labelID;//ID 也一定是Long
        private String productName;//name
        private Boolean isMarked;//is_marked
        public Long getID() {
            return labelID;
        }
        public void setID(Long iD) {
            labelID = iD;
        }
        public String getProductName() {
            return productName;
        }
        public void setProductName(String productName) {
            this.productName = productName;
        }
        public Boolean getIsMarked() {
            return isMarked;
        }
        public void setIsMarked(Boolean isMarked) {
            this.isMarked = isMarked;
        }


    }


    @RequestMapping(value = "check/update") // 5
    public Json labelCheckUpdate(LineLabelController.WrapFive wrapFive, Pageable pageable, HttpSession httpSession) {
        // 接收前端传过来的json数据
        Json j = new Json();

        try {

            String username = (String) httpSession.getAttribute(CommonAttributes.Principal);

            Long lineID = wrapFive.getLineID();
            //发过来的是选中了的ID
            List<Long> LabelIDs = wrapFive.getLabelID();

            //用specialtyId找值如果List 找得到就更新，找不到就存进去
            //有问题 还是用eq
            List<Filter> adminFilter = new ArrayList<Filter>();
            HyLine hyLine = hyLineService.find(lineID);
            adminFilter.add(Filter.eq("hyLine", hyLine));//specialty是specialty_id

            //找出来是所有的
            List<HySpecialtyLineLabel> specialtyLabel = hySpecialtyLineLabelService.findList(null, adminFilter, null);

            //如果之前有值 是非空的
            if(!specialtyLabel.isEmpty()){

                //问题在于，第四个接口展示了很多数据，第五个接口点击更改的时候，hySpecialtyLabel里的数据不够
                //要更改，就要在这个接口点击提交的时候，把新数据加进去 原来的数据也不用删，因为挨个判断了
                //select * from hy_specialty_label where specialtyID = specialtyID 拿出labelID看看是否和传来的一样
                //先遍历LabelIDs 因为这个多
                for(Long tempID1 : LabelIDs){
                    boolean flag = false;//默认是数据库中没有

                    for(HySpecialtyLineLabel tempData : specialtyLabel){
                        //如果有一个ID相等
                        if(tempData.getHyLabel().getID() == tempID1){
                            flag = true;//找到匹配的 不需要做操作
                        }
                    }

                    if(flag == false){//如果判断半天还是没有 就加进去
                        HySpecialtyLineLabel hySpecialtyLabel = new HySpecialtyLineLabel();
                        hySpecialtyLabel.setCreateTime(new Date());
                        //~~~~~~~~做一件事，把数据库里的实体拿出来便于更新
                        //find
                        HyLineLabel hyLabel = hyLineLabelService.find(tempID1);
                        hySpecialtyLabel.setHyLabel(hyLabel);
                        hySpecialtyLabel.setHyLine(hyLine);
                        hySpecialtyLabel.setIsMarked(true);
                        hySpecialtyLabel.setOperator(username);

                        hySpecialtyLineLabelService.save(hySpecialtyLabel);
                    }

                }


                //更新回去 更新关系表就行 不用更新Label表
                for(HySpecialtyLineLabel temp : specialtyLabel){

                    //进来先设置为未标记 如果找到再设置已标记
                    temp.setIsMarked(false);
                    for(Long tempID : LabelIDs){
                        //如果找到匹配的ID
                        if(temp.getHyLabel().getID().equals(tempID)){
                            temp.setIsMarked(true);
                        }
                    }

                    //更新回数据库
                    hySpecialtyLineLabelService.update(temp);
                }


            } else {
                //向数据库里面添加 那个List是空值 这个接口想了一下 没问题
                //有多条label 就要一个循环
                for(Long temp: LabelIDs){
                    HySpecialtyLineLabel hySpecialtyLabel = new HySpecialtyLineLabel();
                    hySpecialtyLabel.setCreateTime(new Date());
                    //~~~~~~~~做一件事，把数据库里的实体拿出来便于更新
                    //find
                    HyLineLabel hyLabel = hyLineLabelService.find(temp);
                    hySpecialtyLabel.setHyLabel(hyLabel);
                    hySpecialtyLabel.setHyLine(hyLine);
                    hySpecialtyLabel.setIsMarked(true);
                    hySpecialtyLabel.setOperator(username);

                    hySpecialtyLineLabelService.save(hySpecialtyLabel);
                }
            }


        } catch (Exception e) {
            j.setSuccess(false);
            j.setMsg(e.getMessage());
        }
        j.setSuccess(true);
        j.setMsg("更新成功");
        return j;
    }

    public static class WrapFive{
        private Long lineID;
        private List<Long> LabelID;

        public Long getLineID() {
            return lineID;
        }
        public void setLineID(Long lineID) {
            this.lineID = lineID;
        }
        public List<Long> getLabelID() {
            return LabelID;
        }
        public void setLabelID(List<Long> labelID) {
            LabelID = labelID;
        }

    }



    @SuppressWarnings("unused")
    private Json getResults(String msg, boolean success, Page<HashMap<String, Object>> page) {
        Json json = new Json();
        json.setMsg(msg);
        json.setSuccess(success);
        json.setObj(page);
        return json;
    }

    /** 产品置顶*/
    @RequestMapping("topLine")
    @ResponseBody
    public Json topLine(Long lineId)
    {
        Json json=new Json();
        try{
            HyLine hyLine=hyLineService.find(lineId);
            hyLine.setIsTop(true);
            hyLine.setTopEditTime(new Date());
            hyLineService.update(hyLine);
            json.setSuccess(true);
            json.setMsg("置顶成功！");
        }
        catch(Exception e){
            json.setSuccess(false);
            json.setMsg(e.getMessage());
        }
        return json;
    }


    /** 取消产品置顶*/
    @RequestMapping("cancelTopLine")
    @ResponseBody
    public Json cancelTopLine(Long lineId)
    {
        Json json=new Json();
        try{
            HyLine hyLine=hyLineService.find(lineId);
            hyLine.setIsTop(false);
            hyLineService.update(hyLine);
            json.setSuccess(true);
            json.setMsg("取消置顶成功！");
        }
        catch(Exception e){
            json.setSuccess(false);
            json.setMsg(e.getMessage());
        }
        return json;
    }

}
