package com.sn.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.hongyu.Json;
import com.hongyu.Pageable;
import com.sn.service.StoryManagementService;

@RestController
@RequestMapping("/admin/storyManagement/")
public class StoryManagementController {
	@Resource(name = "StoryManagementServiceImpl")
	private StoryManagementService storyManagementService;

	public static class WrapStory {
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
	public Json add(@RequestBody WrapStory wrapStory, HttpSession session) {
		Json j = new Json();
		j = storyManagementService.add(wrapStory, session);
		return j;
	}

	@RequestMapping(value = "delete")
	@ResponseBody
	public Json delete(Long id) {
		Json j = new Json();
		j = storyManagementService.delete(id);
		return j;
	}

	@RequestMapping(value = "edit")
	@ResponseBody
	public Json edit(@RequestBody WrapStory wrapStory, HttpSession session) {
		Json j = new Json();
		j = storyManagementService.edit(wrapStory, session);
		return j;
	}

	@RequestMapping(value = "list")
	@ResponseBody
	public Json list(Pageable pageable, String name) {
		Json j = new Json();
		j = storyManagementService.list(pageable, name);
		return j;
	}
	
	@RequestMapping(value = "detail")
	@ResponseBody
	public Json details(Long id) {
		Json j = new Json();
		j=storyManagementService.detail(id);
		return j;
	}
}
