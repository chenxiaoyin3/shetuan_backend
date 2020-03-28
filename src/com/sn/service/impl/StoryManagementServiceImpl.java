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
import com.shetuan.entity.People;
import com.sn.controller.StoryManagementController.WrapStory;
import com.sn.entity.StoryChildSong;
import com.sn.service.StoryChildSongService;
import com.sn.service.StoryManagementService;

@Service("StoryManagementServiceImpl")
public class StoryManagementServiceImpl implements StoryManagementService {
	@Resource(name = "StoryChildSongServiceImpl")
	StoryChildSongService storyChildSongService;

	@Override
	public Json add(WrapStory wrapStory, HttpSession session) {
		// TODO Auto-generated method stub
		Json j = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			StoryChildSong scs = new StoryChildSong();
			scs.setName(wrapStory.getName());
			scs.setType(1);
			scs.setDescription(wrapStory.getDescription());
			scs.setClickNumber(0);
			scs.setLyrics(wrapStory.getLyrics());
			scs.setAudioUrl(wrapStory.getAudioUrl());
			scs.setPhotoUrl(wrapStory.getPhotoUrl());
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
			String sql = "delete from sn_story_child_song where id=" + id + " and type=1";
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
	public Json edit(WrapStory wrapStory, HttpSession session) {
		// TODO Auto-generated method stub
		Json j = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			List<Filter> filter = new ArrayList<>();
			filter.add(Filter.eq("id", wrapStory.getId()));
			filter.add(Filter.eq("type", 1));
			List<StoryChildSong> oldScsList = storyChildSongService.findList(null, filter, null);
			if (oldScsList != null) {
				StoryChildSong oldScs = oldScsList.get(0);
				oldScs.setName(wrapStory.getName());
				oldScs.setType(1);
				oldScs.setDescription(wrapStory.getDescription());
				oldScs.setLyrics(wrapStory.getLyrics());
				oldScs.setAudioUrl(wrapStory.getAudioUrl());
				oldScs.setPhotoUrl(wrapStory.getPhotoUrl());
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
			filter.add(Filter.eq("type", 1));
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
