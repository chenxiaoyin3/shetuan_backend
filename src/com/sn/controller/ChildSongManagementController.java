package com.sn.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.hongyu.Json;
import com.hongyu.Pageable;
import com.sn.controller.StoryManagementController.WrapStory;
import com.sn.service.ChildSongManagementService;
import com.sn.service.StoryChildSongService;

@RestController
@RequestMapping("/admin/childSongManagement/")
public class ChildSongManagementController {
	@Resource(name = "ChildSongManagementServiceImpl")
	private ChildSongManagementService childSongManagementService;

	public static class WrapChildSong {
		private Long id;
		private String name;
		private String description;
		private String lyrics;
		private String audioUrl;
		private String photoUrl;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getLyrics() {
			return lyrics;
		}

		public void setLyrics(String lyrics) {
			this.lyrics = lyrics;
		}

		public String getAudioUrl() {
			return audioUrl;
		}

		public void setAudioUrl(String audioUrl) {
			this.audioUrl = audioUrl;
		}

		public String getPhotoUrl() {
			return photoUrl;
		}

		public void setPhotoUrl(String photoUrl) {
			this.photoUrl = photoUrl;
		}

	}

	@RequestMapping(value = "add")
	@ResponseBody
	public Json add(@RequestBody WrapChildSong wrapChildSong, HttpSession session) {
		Json j = new Json();
		j=childSongManagementService.add(wrapChildSong, session);
		return j;
	}

	@RequestMapping(value = "delete")
	@ResponseBody
	public Json delete(Long id) {
		Json j = new Json();
		j=childSongManagementService.delete(id);
		return j;
	}

	@RequestMapping(value = "edit")
	@ResponseBody
	public Json edit(@RequestBody WrapChildSong wrapChildSong, HttpSession session) {
		Json j = new Json();
		j=childSongManagementService.edit(wrapChildSong, session);
		return j;
	}

	@RequestMapping(value = "list")
	@ResponseBody
	public Json list(Pageable pageable, String name) {
		Json j = new Json();
		j=childSongManagementService.list(pageable, name);
		return j;
	}
	
	@RequestMapping(value = "detail")
	@ResponseBody
	public Json details(Long id) {
		Json j = new Json();
		j=childSongManagementService.detail(id);
		return j;
	}
}

