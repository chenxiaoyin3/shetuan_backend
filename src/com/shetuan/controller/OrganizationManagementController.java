package com.shetuan.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.hongyu.CommonAttributes;
import com.hongyu.Json;
import com.hongyu.Pageable;
import com.shetuan.entity.Activity;
import com.shetuan.entity.Audio;
import com.shetuan.entity.Constitution;
import com.shetuan.entity.HistoricalDataIndex;
import com.shetuan.entity.Image;
import com.shetuan.entity.Journal;
import com.shetuan.entity.Literature;
import com.shetuan.entity.OfficePlace;
import com.shetuan.entity.Organization;
import com.shetuan.entity.PeopleRelation;
import com.shetuan.entity.RealObject;
import com.shetuan.entity.Video;
import com.shetuan.service.OrganizationManagementService;
import com.shetuan.service.OrganizationService;

@RestController
@RequestMapping("/admin/shetuanManagement/")
public class OrganizationManagementController {
	@Resource(name = "OrganizationManagementServiceImpl")
	private OrganizationManagementService organizationManagementService;

	@Resource(name = "OrganizationServiceImpl")
	private OrganizationService organizationService;

	public static class WrapActivity {
		private Activity activity;
		private List<Image> imageList = new ArrayList<>();
		private List<Video> videoList = new ArrayList<>();
		private List<Audio> audioList = new ArrayList<>();
//		private List<PeopleRelation> relatedPeopleList = new ArrayList<>();
		private List<Integer> relatedPeopleList = new ArrayList<>();

		public Activity getActivity() {
			return activity;
		}

		public void setActivity(Activity activity) {
			this.activity = activity;
		}

		public List<Image> getImageList() {
			return imageList;
		}

		public void setImageList(List<Image> imageList) {
			this.imageList = imageList;
		}

		public List<Video> getVideoList() {
			return videoList;
		}

		public void setVideoList(List<Video> videoList) {
			this.videoList = videoList;
		}

		public List<Audio> getAudioList() {
			return audioList;
		}

		public void setAudioList(List<Audio> audioList) {
			this.audioList = audioList;
		}

		public List<Integer> getRelatedPeopleList() {
			return relatedPeopleList;
		}

		public void setRelatedPeopleList(List<Integer> relatedPeopleList) {
			this.relatedPeopleList = relatedPeopleList;
		}

	}

	public static class WrapJournal {
		private Journal journal;
		private List<Image> imageList = new ArrayList<>();
//		private List<PeopleRelation> relatedPeopleList = new ArrayList<>();
		private List<Integer> relatedPeopleList = new ArrayList<>();

		public Journal getJournal() {
			return journal;
		}

		public void setJournal(Journal journal) {
			this.journal = journal;
		}

		public List<Image> getImageList() {
			return imageList;
		}

		public void setImageList(List<Image> imageList) {
			this.imageList = imageList;
		}

		public List<Integer> getRelatedPeopleList() {
			return relatedPeopleList;
		}

		public void setRelatedPeopleList(List<Integer> relatedPeopleList) {
			this.relatedPeopleList = relatedPeopleList;
		}
	}

	public static class WrapLiterature {
		private Literature literature;
		private List<Image> imageList = new ArrayList<>();
//		private List<PeopleRelation> relatedPeopleList = new ArrayList<>();
		private List<Integer> relatedPeopleList = new ArrayList<>();

		public Literature getLiterature() {
			return literature;
		}

		public void setLiterature(Literature literature) {
			this.literature = literature;
		}

		public List<Image> getImageList() {
			return imageList;
		}

		public void setImageList(List<Image> imageList) {
			this.imageList = imageList;
		}

		public List<Integer> getRelatedPeopleList() {
			return relatedPeopleList;
		}

		public void setRelatedPeopleList(List<Integer> relatedPeopleList) {
			this.relatedPeopleList = relatedPeopleList;
		}

	}

	public static class WrapRealObject {
		private RealObject realObject;
		private List<Image> imageList = new ArrayList<>();
		private List<Audio> audioList = new ArrayList<>();
		private List<Video> videoList = new ArrayList<>();
//		private List<PeopleRelation> relatedPeopleList = new ArrayList<>();
		private List<Integer> relatedPeopleList = new ArrayList<>();

		public RealObject getRealObject() {
			return realObject;
		}

		public void setRealObject(RealObject realObject) {
			this.realObject = realObject;
		}

		public List<Image> getImageList() {
			return imageList;
		}

		public void setImageList(List<Image> imageList) {
			this.imageList = imageList;
		}

		public List<Audio> getAudioList() {
			return audioList;
		}

		public void setAudioList(List<Audio> audioList) {
			this.audioList = audioList;
		}

		public List<Video> getVideoList() {
			return videoList;
		}

		public void setVideoList(List<Video> videoList) {
			this.videoList = videoList;
		}

		public List<Integer> getRelatedPeopleList() {
			return relatedPeopleList;
		}

		public void setRelatedPeopleList(List<Integer> relatedPeopleList) {
			this.relatedPeopleList = relatedPeopleList;
		}

	}

	public static class WrapConstitution {
		private Constitution constitution;
		private List<Image> imageList = new ArrayList<>();

		public Constitution getConstitution() {
			return constitution;
		}

		public void setConstitution(Constitution constitution) {
			this.constitution = constitution;
		}

		public List<Image> getImageList() {
			return imageList;
		}

		public void setImageList(List<Image> imageList) {
			this.imageList = imageList;
		}

	}

	public static class WrapHistoricalDataIndex {
		private HistoricalDataIndex historicalDataIndex;
		private List<Image> imageList = new ArrayList<>();

		public HistoricalDataIndex getHistoricalDataIndex() {
			return historicalDataIndex;
		}

		public void setHistoricalDataIndex(HistoricalDataIndex historicalDataIndex) {
			this.historicalDataIndex = historicalDataIndex;
		}

		public List<Image> getImageList() {
			return imageList;
		}

		public void setImageList(List<Image> imageList) {
			this.imageList = imageList;
		}
	}

	public static class WrapOrganization {
		private Organization organization;
		private List<OfficePlace> officePlaceList = new ArrayList<>();
		private List<WrapActivity> wrapActivityList = new ArrayList<>();// 图片音频视频
		private List<WrapJournal> wrapJournalList = new ArrayList<>();// 只有图片
		private List<WrapLiterature> wrapLiteratureList = new ArrayList<>();// 只有图片
		private List<WrapRealObject> wrapRealObjectList = new ArrayList<>();// 只有图片
		private List<WrapConstitution> wrapConstitutionList = new ArrayList<>();// 只有图片
		private List<WrapHistoricalDataIndex> wrapHistoricalDataIndexList = new ArrayList<>();
//		private List<Image> imageList = new ArrayList<>();
//		private List<Video> videoList = new ArrayList<>();
//		private List<Audio> audioList = new ArrayList<>();
		private List<PeopleRelation> relatedPeopleList = new ArrayList<>();

		public Organization getOrganization() {
			return organization;
		}

		public void setOrganization(Organization organization) {
			this.organization = organization;
		}

		public List<OfficePlace> getOfficePlaceList() {
			return officePlaceList;
		}

		public void setOfficePlaceList(List<OfficePlace> officePlaceList) {
			this.officePlaceList = officePlaceList;
		}

		public List<WrapActivity> getWrapActivityList() {
			return wrapActivityList;
		}

		public void setWrapActivityList(List<WrapActivity> wrapActivityList) {
			this.wrapActivityList = wrapActivityList;
		}

		public List<WrapJournal> getWrapJournalList() {
			return wrapJournalList;
		}

		public void setWrapJournalList(List<WrapJournal> wrapJournalList) {
			this.wrapJournalList = wrapJournalList;
		}

		public List<WrapLiterature> getWrapLiteratureList() {
			return wrapLiteratureList;
		}

		public void setWrapLiteratureList(List<WrapLiterature> wrapLiteratureList) {
			this.wrapLiteratureList = wrapLiteratureList;
		}

		public List<WrapRealObject> getWrapRealObjectList() {
			return wrapRealObjectList;
		}

		public void setWrapRealObjectList(List<WrapRealObject> wrapRealObjectList) {
			this.wrapRealObjectList = wrapRealObjectList;
		}

		public List<WrapConstitution> getWrapConstitutionList() {
			return wrapConstitutionList;
		}

		public void setWrapConstitutionList(List<WrapConstitution> wrapConstitutionList) {
			this.wrapConstitutionList = wrapConstitutionList;
		}

		public List<WrapHistoricalDataIndex> getWrapHistoricalDataIndexList() {
			return wrapHistoricalDataIndexList;
		}

		public void setWrapHistoricalDataIndexList(List<WrapHistoricalDataIndex> wrapHistoricalDataIndexList) {
			this.wrapHistoricalDataIndexList = wrapHistoricalDataIndexList;
		}

//		public List<Image> getImageList() {
//			return imageList;
//		}
//
//		public void setImageList(List<Image> imageList) {
//			this.imageList = imageList;
//		}
//
//		public List<Video> getVideoList() {
//			return videoList;
//		}
//
//		public void setVideoList(List<Video> videoList) {
//			this.videoList = videoList;
//		}
//
//		public List<Audio> getAudioList() {
//			return audioList;
//		}
//
//		public void setAudioList(List<Audio> audioList) {
//			this.audioList = audioList;
//		}

		public List<PeopleRelation> getRelatedPeopleList() {
			return relatedPeopleList;
		}

		public void setRelatedPeopleList(List<PeopleRelation> relatedPeopleList) {
			this.relatedPeopleList = relatedPeopleList;
		}
	}

	@RequestMapping(value = "list")
	@ResponseBody
	public Json list(Pageable pageable, String name, boolean state) {
		Json j = new Json();
		j = organizationManagementService.list(pageable, name, state);
		return j;
	}

	@RequestMapping(value = "detailById")
	@ResponseBody
	public Json detailById(Long organizationId) {
		Json j = new Json();
		j = organizationService.detailById(organizationId);
		return j;
	}

	@RequestMapping(value = "add")
	@ResponseBody
	@Transactional(propagation = Propagation.REQUIRED)
	public Json add(@RequestBody WrapOrganization wrapOrganization, HttpSession session) {
		Json j = new Json();
		j = organizationManagementService.add(wrapOrganization, session);
		return j;
	}

	@RequestMapping(value = "edit")
	@ResponseBody
	@Transactional(propagation = Propagation.REQUIRED)
	public Json edit(@RequestBody WrapOrganization wrapOrganization, HttpSession session) {
		Json j = new Json();
		j = organizationManagementService.edit(wrapOrganization, session);
		return j;
	}

	@RequestMapping(value = "deleteById")
	@ResponseBody
	@Transactional(propagation = Propagation.REQUIRED)
	public Json deleteById(Long organizationId) {
		Json j = new Json();
		j = organizationManagementService.deleteById(organizationId);
		return j;
	}
}
