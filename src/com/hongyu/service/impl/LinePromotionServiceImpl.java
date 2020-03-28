package com.hongyu.service.impl;

import java.util.Date;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.Filter;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.dao.LinePromotionDao;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyDepartmentModel;
import com.hongyu.entity.LinePromotion;
import com.hongyu.service.LinePromotionService;


@Service("linePromotionServiceImpl")
public class LinePromotionServiceImpl extends BaseServiceImpl<LinePromotion, Long> implements LinePromotionService {
	
	@Resource(name="linePromotionDaoImpl")
	LinePromotionDao linePromotionDao;
	@Override
	public LinePromotion findByGroupId(Long id) {
		// TODO Auto-generated method stub
		StringBuilder sBuilder=new StringBuilder();
		SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentTime=simpleDateFormat.format(new Date());
				
		sBuilder.append("select a.id ,a.name from hy_line_promotion a inner join hy_promotion_product_group b on a.id=b.promotion_id where a.is_cancel=0 and a.state=1 ");
		sBuilder.append("and unix_timestamp(a.start_date) <= unix_timestamp( ' " + currentTime + " ') ");
		sBuilder.append("and unix_timestamp(a.end_date) >= unix_timestamp( ' " + currentTime + " ') ");
		sBuilder.append("and b.group_id="+id);
		List<Object[]>lists=linePromotionDao.findBysql(sBuilder.toString());
		
		System.out.print(sBuilder.toString());
		LinePromotion linePromotion;
		if(lists==null||lists.size()==0){
			linePromotion=null;
		}else{
			Long promotionId=((BigInteger)(lists.get(0)[0])).longValue();
			linePromotion=this.find(promotionId);
		}
		return linePromotion;
	}

	@Resource(name = "linePromotionDaoImpl")
	public void setBaseDao(LinePromotionDao dao) {
		super.setBaseDao(dao);
	}

	@Override
	public Page<LinePromotion> findAuditPage(HyAdmin auditor, Pageable pageable, LinePromotion query) {
		HyDepartmentModel d = auditor.getDepartment().getHyDepartmentModel();
		if (d.getName().equals("总公司品控中心")) {
//			String sql = "select a.id, a.name from hy_line_promotion a inner join hy_admin b on a.operator = b.username " +
//                         "inner join hy_department c on b.department = c.id where c.model != " + "'总公司采购部'" + " and c.model != " +
//					     "'分公司汽车部'";
			String sql = "select a.id, a.name from hy_line_promotion a where a.is_caigouti = 0";
			if (query != null && query.getState() != null) {
				sql += " and a.state = " + query.getState();
			}
			sql += " order by a.id desc";
			Page<List<Object[]>> page = this.findPageBysql(sql, pageable);
			List<Object[]> lst = page.getLstObj();
			Long[] ids = new Long[lst.size()];
			for (int i = 0; i < lst.size(); i++) {
				ids[i] = ((BigInteger)lst.get(i)[0]).longValue();
			}
			List<LinePromotion> promotions = this.findList(ids);
			Page<LinePromotion> result = new Page<LinePromotion>(promotions, page.getTotal(), pageable);
			return result;
		} else {   //审核由采购部或者汽车部提出的促销
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("operator", auditor));
			filters.add(Filter.eq("isCaigouti", Boolean.TRUE));
			if (query != null && query.getState() != null) {
				filters.add(Filter.eq("state", query.getState()));
			}
			List<Order> orders = new ArrayList<>();
			orders.add(Order.desc("id"));
			pageable.setFilters(filters);
			pageable.setOrders(orders);
			Page<LinePromotion> result = this.findPage(pageable);
			return result;
		}
	}
}
