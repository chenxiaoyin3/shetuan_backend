package com.sn.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.sn.controller.ChildSongManagementController.WrapChildSong;
import com.sn.entity.StoryChildSong;
import com.sn.service.ChildSongManagementService;
import com.sn.service.StoryChildSongService;

@Service("ChildSongManagementServiceImpl")
public class ChildSongManagementServiceImpl implements ChildSongManagementService {
	@Resource(name = "StoryChildSongServiceImpl")
	StoryChildSongService storyChildSongService;

	@Override
	public Json add(WrapChildSong wrapChildSong, HttpSession session) {
		// TODO Auto-generated method stub
		Json j = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			StoryChildSong scs = new StoryChildSong();
			scs.setName(wrapChildSong.getName());
			scs.setType(2);
			scs.setDescription(wrapChildSong.getDescription());
			scs.setClickNumber(0);
			scs.setLyrics(wrapChildSong.getLyrics());
			scs.setAudioUrl(wrapChildSong.getAudioUrl());
			scs.setPhotoUrl(wrapChildSong.getPhotoUrl());
			scs.setMname(username);
			scs.setMtime(new Timestamp(System.currentTimeMillis()));
			storyChildSongService.save(scs);
			j.setSuccess(true);
			j.setMsg("新建成功");
			j.setObj(null);
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("新建失败: " + e.getMessage());
			j.setObj(null);
			e.printStackTrace();
		}
		return j;
	}

	@Override
	public Json delete(Long id) {
		// TODO Auto-generated method stub
		Json j = new Json();
		try {
			String sql = "delete from sn_story_child_song where id=" + id + " and type=2";
			storyChildSongService.deleteBySql(sql);
			j.setSuccess(true);
			j.setMsg("删除成功");
			j.setObj(null);
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("删除失败: " + e.getMessage());
			j.setObj(null);
			e.printStackTrace();
		}
		return j;
	}

	@Override
	public Json edit(WrapChildSong wrapChildSong, HttpSession session) {
		// TODO Auto-generated method stub
		Json j = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			List<Filter> filter = new ArrayList<>();
			filter.add(Filter.eq("id", wrapChildSong.getId()));
			filter.add(Filter.eq("type", 2));
			List<StoryChildSong> oldScsList = storyChildSongService.findList(null, filter, null);
			if (oldScsList != null) {
				StoryChildSong oldScs = oldScsList.get(0);
				oldScs.setName(wrapChildSong.getName());
				oldScs.setType(2);
				oldScs.setDescription(wrapChildSong.getDescription());
				oldScs.setLyrics(wrapChildSong.getLyrics());
				oldScs.setAudioUrl(wrapChildSong.getAudioUrl());
				oldScs.setPhotoUrl(wrapChildSong.getPhotoUrl());
				oldScs.setMtime(new Timestamp(System.currentTimeMillis()));
				oldScs.setMname(username);
				storyChildSongService.update(oldScs);
				j.setSuccess(true);
				j.setMsg("编辑成功");
				j.setObj(null);
			} else {
				j.setSuccess(false);
				j.setMsg("编辑失败");
				j.setObj(null);
			}
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("编辑失败: " + e.getMessage());
			j.setObj(null);
			e.printStackTrace();
		}
		return j;
	}

	@Override
	public Json list(Pageable pageable, String name) {
		// TODO Auto-generated method stub
		Json j = new Json();
		try {
			HashMap<String, Object> hm = new HashMap<String, Object>();
			List<HashMap<String, Object>> result = new ArrayList<>();
			List<Filter> filter = new ArrayList<Filter>();
			if (name != null) {
				filter.add(Filter.like("name", name));
			}
			filter.add(Filter.eq("type", 2));
			pageable.setFilters(filter);
			Page<StoryChildSong> page = storyChildSongService.findPage(pageable);
			for (StoryChildSong tmp : page.getRows()) {
				HashMap<String, Object> m = new HashMap<String, Object>();
				m.put("id", tmp.getId());
				m.put("name", tmp.getName());
				m.put("type", tmp.getType());
				m.put("description", tmp.getDescription());
				m.put("lyrics", tmp.getLyrics());
				m.put("audioUrl", tmp.getAudioUrl());
				m.put("photoUrl", tmp.getPhotoUrl());
				result.add(m);
			}
			hm.put("total", page.getTotal());
			hm.put("pageNumber", page.getPageNumber());
			hm.put("pageSize", page.getPageSize());
			hm.put("result", result);
			j.setSuccess(true);
			j.setMsg("获取成功");
			j.setObj(hm);
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("获取失败: " + e.getMessage());
			j.setObj(null);
			e.printStackTrace();
		}
		return j;
	}

	@Override
	public Json detail(Long id) {
		// TODO Auto-generated method stub
		Json j = new Json();
		try {
			j.setSuccess(true);
			j.setMsg("获取成功");
			j.setObj(storyChildSongService.find(id));
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("获取失败: " + e.getMessage());
			j.setObj(null);
			e.printStackTrace();
		}
		return j;
	}

	@Override
	public void addClickNumber(Long id) {
		// TODO Auto-generated method stub
		StoryChildSong scs=storyChildSongService.find(id);
		scs.setClickNumber(scs.getClickNumber()+1);
		storyChildSongService.update(scs);
	}

}
