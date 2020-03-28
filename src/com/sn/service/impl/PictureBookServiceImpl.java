package com.sn.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.sn.entity.PictureBook;
import com.sn.entity.PictureBookResources;
import com.sn.service.PictureBookResourcesService;
import com.sn.service.PictureBookService;

@Service("PictureBookServiceImpl")
public class PictureBookServiceImpl extends BaseServiceImpl<PictureBook,Long> implements PictureBookService {
	@Override
	@Resource(name="PictureBookDaoImpl")
	public void setBaseDao(BaseDao<PictureBook,Long> baseDao) {
		super.setBaseDao(baseDao);
	}
	
	@Resource(name = "PictureBookResourcesServiceImpl")
	PictureBookResourcesService pictureBookResourcesService;
	
	@Override
	public Json listView(Pageable pageable, String name, Integer type) {
		Json j = new Json();
		
		try {
			List<Filter> filters = new ArrayList<Filter>();
			if(name != null) filters.add(Filter.like("name", name));
			filters.add(Filter.eq("type", type));
			pageable.setFilters(filters);
			Page<PictureBook> page = this.findPage(pageable);

			HashMap<String,Object> hm=new HashMap<String,Object>();
			List<HashMap<String,Object>> result=new ArrayList<HashMap<String,Object>>();
			
			for(PictureBook pictureBook:page.getRows()) {
				HashMap<String,Object> m=new HashMap<String,Object>();
				m.put("id", pictureBook.getId());
				m.put("name", pictureBook.getName());
				m.put("type", pictureBook.getType());
				m.put("totalNum", pictureBook.getTotalNum());
				if(pictureBook.getPictureSet().size() == 0)
					m.put("imgUrl", "no image");
				else {
					m.put("imgUrl", pictureBook.getPictureSet().get(0).getPhotoUrl());
				}
				result.add(m);
			}
			
			hm.put("total",page.getTotal());
			hm.put("pageNumber",page.getPageNumber());
			hm.put("pageSize",page.getPageSize());
			hm.put("result",result);
			j.setSuccess(true);
			j.setMsg("获取成功");
			j.setObj(hm);
			
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("操作失败");
		}
		return j;
	}

	@Override
	public Json getDetail(Long id) {
		Json j = new Json();
		
		try {
			List<Filter> filters=new ArrayList<>();
			filters.add(Filter.eq("id", id));
			PictureBook pictureBook = this.find(id);
			HashMap<String, Object> m = new HashMap<String, Object>();
			m.put("id", pictureBook.getId());
			m.put("name", pictureBook.getName());
			m.put("type", pictureBook.getType());
			
			List<HashMap<String,Object>> pictureSet=new ArrayList<HashMap<String,Object>>();			
			for(PictureBookResources pictureBookResource : pictureBook.getPictureSet()) {
				HashMap<String, Object> set = new HashMap<String, Object>();
				set.put("id", pictureBookResource.getId());
				set.put("image", pictureBookResource.getPhotoUrl());
				set.put("audio", pictureBookResource.getAudioUrl());
				pictureSet.add(set);
			}
			m.put("pictureSet", pictureSet);
			
			pictureBook.setTotalNum(pictureBook.getTotalNum()+1);
			
			j.setSuccess(true);
			j.setMsg("获取成功");
			j.setObj(m);
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("操作失败");
		}
		return j;
	}

	@Override
	public Json add(@RequestBody PictureBook pictureBook, HttpSession session) {
		Json j = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			pictureBook.setMtime(new Timestamp(System.currentTimeMillis()));
			pictureBook.setMname(username);
			pictureBook.setTotalNum(0);
			this.save(pictureBook);
			j.setSuccess(true);
			j.setMsg("操作成功");
			j.setObj(null);
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("操作失败: " + e.getMessage());
			j.setObj(null);
			e.printStackTrace();
		}
		return j;
	}

	@Override
	public Json edit(@RequestBody PictureBook pictureBook, HttpSession session) {
		Json j = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			PictureBook oldPictureBook = this.find(pictureBook.getId());
			for(PictureBookResources pictureBookResources:oldPictureBook.getPictureSet()) {
				pictureBookResourcesService.delete(pictureBookResources);
			}
			oldPictureBook.setName(pictureBook.getName());
			oldPictureBook.setType(pictureBook.getType());
			oldPictureBook.setPictureSet(pictureBook.getPictureSet());
			oldPictureBook.setMtime(new Timestamp(System.currentTimeMillis()));
			oldPictureBook.setMname(username);
			oldPictureBook.setTotalNum(oldPictureBook.getTotalNum());
			this.update(oldPictureBook);
			j.setSuccess(true);
			j.setMsg("修改成功");
			j.setObj(null);
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("修改失败: " + e.getMessage());
			j.setObj(null);
			e.printStackTrace();
		}
		return j;
	}

	@Override
	public Json Delete(Long id) {
		Json j = new Json();
		try {
			PictureBook pictureBook = this.find(id);
			for(PictureBookResources pictureBookResources:pictureBook.getPictureSet()) {
				pictureBookResourcesService.delete(pictureBookResources);
			}
			this.delete(id);
			j.setSuccess(true);
			j.setMsg("操作成功");
			j.setObj(null);
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("操作失败: " + e.getMessage());
			j.setObj(null);
			e.printStackTrace();
		}
		return j;
	}
}
