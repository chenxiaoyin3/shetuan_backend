package com.shetuan.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.shetuan.controller.OrganizationManagementController.WrapOrganization;
import com.shetuan.controller.OrganizationManagementController.WrapRealObject;
import com.shetuan.controller.OrganizationManagementController.WrapActivity;
import com.shetuan.controller.OrganizationManagementController.WrapConstitution;
import com.shetuan.controller.OrganizationManagementController.WrapHistoricalDataIndex;
import com.shetuan.controller.OrganizationManagementController.WrapJournal;
import com.shetuan.controller.OrganizationManagementController.WrapLiterature;
import com.shetuan.entity.Activity;
import com.shetuan.entity.Audio;
import com.shetuan.entity.Constitution;
import com.shetuan.entity.HistoricalDataIndex;
import com.shetuan.entity.Image;
import com.shetuan.entity.Journal;
import com.shetuan.entity.Literature;
import com.shetuan.entity.OfficePlace;
import com.shetuan.entity.Organization;
import com.shetuan.entity.People;
import com.shetuan.entity.PeopleRelation;
import com.shetuan.entity.RealObject;
import com.shetuan.entity.SupplementFile;
import com.shetuan.entity.Video;
import com.shetuan.service.ActivityService;
import com.shetuan.service.AudioService;
import com.shetuan.service.ConstitutionService;
import com.shetuan.service.HistoricalDataIndexService;
import com.shetuan.service.ImageService;
import com.shetuan.service.JournalService;
import com.shetuan.service.LiteratureService;
import com.shetuan.service.OfficePlaceService;
import com.shetuan.service.OrganizationManagementService;
import com.shetuan.service.OrganizationService;
import com.shetuan.service.PeopleRelationService;
import com.shetuan.service.PeopleService;
import com.shetuan.service.RealObjectService;
import com.shetuan.service.VideoService;

@Service("OrganizationManagementServiceImpl")
public class OrganizationManagementServiceImpl implements OrganizationManagementService {
	@Resource(name = "OrganizationServiceImpl")
	OrganizationService organizationService;

	@Resource(name = "OfficePlaceServiceImpl")
	OfficePlaceService officePlaceService;

	@Resource(name = "ActivityServiceImpl")
	ActivityService activityService;

	@Resource(name = "JournalServiceImpl")
	JournalService journalService;

	@Resource(name = "LiteratureServiceImpl")
	LiteratureService literatureService;

	@Resource(name = "RealObjectServiceImpl")
	RealObjectService realObjectService;

	@Resource(name = "ConstitutionServiceImpl")
	ConstitutionService constitutionService;

	@Resource(name = "HistoricalDataIndexServiceImpl")
	HistoricalDataIndexService historicalDataIndexService;

	@Resource(name = "ImageServiceImpl")
	ImageService imageService;

	@Resource(name = "AudioServiceImpl")
	AudioService audioService;

	@Resource(name = "VideoServiceImpl")
	VideoService videoService;

	@Resource(name = "PeopleRelationServiceImpl")
	PeopleRelationService peopleRelationService;

	@Resource(name = "PeopleServiceImpl")
	PeopleService peopleService;

	@Override
	public Json list(Pageable pageable, String name, boolean state) {
		// TODO Auto-generated method stub
		Json j = new Json();
		try {
			HashMap<String, Object> hm = new HashMap<String, Object>();
			List<HashMap<String, Object>> result = new ArrayList<>();
			List<Filter> filter = new ArrayList<Filter>();
			filter.add(Filter.eq("state", state));
			if (name != null) {
				filter.add(Filter.like("name", name));
			}
			filter.add(Filter.like("state", state));
			pageable.setFilters(filter);
			Page<Organization> page = organizationService.findPage(pageable);
			for (Organization tmp : page.getRows()) {
				HashMap<String, Object> m = new HashMap<String, Object>();
				m.put("id", tmp.getId());
				m.put("name", tmp.getName());
				m.put("description", tmp.getDescription());
				m.put("nameHistory", tmp.getNameHistory());
				m.put("startTime", tmp.getStartTime());
				m.put("endTime", tmp.getEndTime());
				m.put("place", tmp.getPlace());
				m.put("creator", tmp.getCreator());
				m.put("member", tmp.getMember());
				m.put("leader", tmp.getLeader());
				m.put("secretariat", tmp.getSecretariat());
				m.put("logoUrl", tmp.getLogoUrl());
				m.put("clickNumber", tmp.getClickNumber());
				m.put("state", tmp.getState());
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
	public Json detailById(Long organizationId) {
		// TODO Auto-generated method stub
		Json j = new Json();
		try {
			HashMap<String, Object> result = new HashMap<String, Object>();
			Organization organization = organizationService.find(organizationId);
			List<Filter> filter = new ArrayList<>();
			filter.add(Filter.eq("type", 1));
			filter.add(Filter.eq("entityId", organization.getId()));
			List<PeopleRelation> peopleRelationList = peopleRelationService.findList(null, filter, null);
			Iterator<PeopleRelation> i = peopleRelationList.iterator();
			List<People> relatedPeopleList = new ArrayList<>();
			while (i.hasNext()) {
				relatedPeopleList = peopleService.findList(i.next().getPeopleId());
			}
			result.put("id", organization.getId());
			result.put("name", organization.getName());
			result.put("description", organization.getDescription());
			result.put("nameHistory", organization.getNameHistory());
			result.put("startTime", organization.getStartTime());
			result.put("startTimeType", organization.getStartTimeType());
			result.put("endTime", organization.getEndTime());
			result.put("endTimeType", organization.getEndTimeType());
			result.put("place", organization.getPlace());
			result.put("creator", organization.getCreator());
			result.put("member", organization.getMember());
			result.put("leader", organization.getLeader());
			result.put("secretariat", organization.getSecretariat());
			result.put("logoUrl", organization.getLogoUrl());
			result.put("clickNumber", organization.getClickNumber());
			result.put("state", organization.getState());
			result.put("ctime", organization.getCtime());
			result.put("cname", organization.getName());
			result.put("mtime", organization.getMtime());
			result.put("mname", organization.getMname());
//			result.put("officePlace", organization.getOfficePlace());
			result.put("relatedPeopleList", relatedPeopleList);
			j.setSuccess(true);
			j.setMsg("获取成功");
			j.setObj(result);
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("获取失败: " + e.getMessage());
			j.setObj(null);
			e.printStackTrace();
		}
		return j;
	}

	@Override
	public boolean addOrganization(Organization organization, String username) {
		// TODO Auto-generated method stub
		try {
			organization.setClickNumber(0);
			organization.setState(true);
			organization.setCname(username);
			organization.setCtime(new Timestamp(System.currentTimeMillis()));
			organizationService.save(organization);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	@Override
	public Json add(@RequestBody WrapOrganization wrapOrganization, HttpSession session) {
		// TODO Auto-generated method stub
		Json j = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			Organization organization = wrapOrganization.getOrganization();
			List<PeopleRelation> relatedPeopleList = wrapOrganization.getRelatedPeopleList();
			List<OfficePlace> officePlaceList = wrapOrganization.getOfficePlaceList();
			List<WrapActivity> wrapActivityList = wrapOrganization.getWrapActivityList();
			List<WrapJournal> wrapJournalList = wrapOrganization.getWrapJournalList();
			List<WrapLiterature> wrapLiteratureList = wrapOrganization.getWrapLiteratureList();
			List<WrapRealObject> wrapRealObjectList = wrapOrganization.getWrapRealObjectList();
			List<WrapConstitution> wrapConstitutionList = wrapOrganization.getWrapConstitutionList();
			List<WrapHistoricalDataIndex> wrapHistoricalDataIndexList = wrapOrganization
					.getWrapHistoricalDataIndexList();
			// 加社团
			Organization newOrganization = new Organization();
			newOrganization.setName(organization.getName());
			newOrganization.setDescription(organization.getDescription());
			newOrganization.setNameHistory(organization.getNameHistory());
			newOrganization.setStartTime(organization.getStartTime());
			newOrganization.setStartTimeType(1);
			newOrganization.setEndTime(organization.getEndTime());
			newOrganization.setEndTimeType(1);
			newOrganization.setPlace(organization.getPlace());
			newOrganization.setCreator(organization.getCreator());
			newOrganization.setMember(organization.getMember());
			newOrganization.setLeader(organization.getLeader());
			newOrganization.setSecretariat(organization.getSecretariat());
			newOrganization.setLogoUrl(organization.getLogoUrl());
			newOrganization.setClickNumber(0);
			newOrganization.setState(true);
			newOrganization.setCname(username);
			newOrganization.setCtime(new Timestamp(System.currentTimeMillis()));
			organizationService.save(newOrganization);
			Long organizationId = newOrganization.getId();
			// 加社团的相关人物
			if (!relatedPeopleList.isEmpty()) {
				for (PeopleRelation peopleRelation : relatedPeopleList) {
					PeopleRelation newPeopleRelationUnit = new PeopleRelation();
					newPeopleRelationUnit.setPeopleId(peopleRelation.getPeopleId());
					newPeopleRelationUnit.setEntityId(organizationId);
					newPeopleRelationUnit.setType(1);
					newPeopleRelationUnit.setRelationDescription(peopleRelation.getRelationDescription());
					;
					peopleRelationService.save(newPeopleRelationUnit);
				}
			}
//			// 加社团的图片
//			for (Image image : wrapOrganization.getImageList()) {
//				image.setEntityId(organizationId);
//				image.setType(1);
//				image.setState(true);
//				image.setCname(username);
//				image.setCtime(new Timestamp(System.currentTimeMillis()));
//				imageService.save(image);
//				Long imageId = image.getId();
//				if(image.getRelatedPeople()!=null) {
//					for (PeopleRelation peopleRelation : image.getRelatedPeople()) {
//						peopleRelation.setEntityId(imageId);
//						peopleRelation.setType(6);
//						peopleRelationService.save(peopleRelation);
//					}
//				}
//			}
//			// 加社团的音频
//			for (Audio audio : wrapOrganization.getAudioList()) {
//				audio.setEntityId(organizationId);
//				audio.setType(1);
//				audio.setState(true);
//				audio.setCname(username);
//				audio.setCtime(new Timestamp(System.currentTimeMillis()));
//				audioService.save(audio);
//				Long audioId = audio.getId();
//				if(audio.getRelatedPeople()!=null) {
//					for (PeopleRelation peopleRelation : audio.getRelatedPeople()) {
//						peopleRelation.setEntityId(audioId);
//						peopleRelation.setType(7);
//						peopleRelationService.save(peopleRelation);
//					}
//				}
//			}
//			// 社团的视频
//			for (Video video : wrapOrganization.getVideoList()) {
//				video.setEntityId(organizationId);
//				video.setType(1);
//				video.setState(true);
//				video.setCname(username);
//				video.setCtime(new Timestamp(System.currentTimeMillis()));
//				videoService.save(video);
//				Long videoId = video.getId();
//				if(video.getRelatedPeople()!=null) {
//					for (PeopleRelation peopleRelation : video.getRelatedPeople()) {
//						peopleRelation.setEntityId(videoId);
//						peopleRelation.setType(8);
//						peopleRelationService.save(peopleRelation);
//					}
//				}
//			}

			// 加办公地点
			if (!officePlaceList.isEmpty()) {
				for (OfficePlace officePlace : officePlaceList) {
					OfficePlace newOfficePlaceUnit = new OfficePlace();
					newOfficePlaceUnit.setOrganizationId(organizationId);
					newOfficePlaceUnit.setOfficePlace(officePlace.getOfficePlace());
					newOfficePlaceUnit.setStartTime(officePlace.getStartTime());
					newOfficePlaceUnit.setStartTimeType(1);
					newOfficePlaceUnit.setEndTime(officePlace.getEndTime());
					newOfficePlaceUnit.setEndTimeType(1);
					newOfficePlaceUnit.setState(true);
					newOfficePlaceUnit.setCname(username);
					newOfficePlaceUnit.setCtime(new Timestamp(System.currentTimeMillis()));
					officePlaceService.save(newOfficePlaceUnit);
				}
			}

			// 加社团的活动
			if (!wrapActivityList.isEmpty()) {
				for (WrapActivity wrapActivity : wrapActivityList) {
					Activity newActivityUnit = new Activity();
					newActivityUnit.setOrganizationId(organizationId);
					newActivityUnit.setType(wrapActivity.getActivity().getType());
					newActivityUnit.setName(wrapActivity.getActivity().getName());
					newActivityUnit.setPlace(wrapActivity.getActivity().getPlace());
					newActivityUnit.setContent(wrapActivity.getActivity().getContent());
					newActivityUnit.setStartTime(wrapActivity.getActivity().getStartTime());
					newActivityUnit.setStartTimeType(1);
					newActivityUnit.setEndTime(wrapActivity.getActivity().getEndTime());
					newActivityUnit.setEndTimeType(1);
					newActivityUnit.setState(true);
					newActivityUnit.setCname(username);
					newActivityUnit.setCtime(new Timestamp(System.currentTimeMillis()));
					activityService.save(newActivityUnit);
					Long activityId = newActivityUnit.getId();
					// 加活动的相关人物
					if (wrapActivity.getRelatedPeopleList() != null) {
//						for (PeopleRelation peopleRelation : wrapActivity.getRelatedPeopleList()) {
//							PeopleRelation newPeopleRelationUnit = new PeopleRelation();
//							newPeopleRelationUnit.setPeopleId(peopleRelation.getPeopleId());
//							newPeopleRelationUnit.setEntityId(activityId);
//							newPeopleRelationUnit.setType(2);
//							peopleRelationService.save(newPeopleRelationUnit);
//						}
						for (Integer i : wrapActivity.getRelatedPeopleList()) {
							PeopleRelation newPeopleRelationUnit = new PeopleRelation();
							newPeopleRelationUnit.setPeopleId(i.longValue());
							newPeopleRelationUnit.setEntityId(activityId);
							newPeopleRelationUnit.setType(2);
							peopleRelationService.save(newPeopleRelationUnit);
						}
					}
					List<Image> imageList = wrapActivity.getImageList();
					List<Video> videoList = wrapActivity.getVideoList();
					List<Audio> audioList = wrapActivity.getAudioList();
					// 活动的图片
					for (Image image : imageList) {
						Image newImageUnit = new Image();
						newImageUnit.setEntityId(activityId);
						newImageUnit.setType(2);
						newImageUnit.setName(image.getName());
						newImageUnit.setUrl(image.getUrl());
						newImageUnit.setDescription(image.getDescription());
						newImageUnit.setPhotographer(image.getPhotographer());
						newImageUnit.setCreateTime(image.getCreateTime());
						newImageUnit.setCreateTimeType(1);
						newImageUnit.setState(true);
						newImageUnit.setCname(username);
						newImageUnit.setCtime(new Timestamp(System.currentTimeMillis()));
						imageService.save(newImageUnit);
						Long imageId = newImageUnit.getId();
						// 加活动的图片的相关人物
						if (image.getRelatedPeopleList() != null) {
//							for (PeopleRelation peopleRelation : image.getRelatedPeople()) {
//								PeopleRelation newPeopleRelationUnit = new PeopleRelation();
//								newPeopleRelationUnit.setPeopleId(peopleRelation.getPeopleId());
//								newPeopleRelationUnit.setEntityId(imageId);
//								newPeopleRelationUnit.setType(6);
//								peopleRelationService.save(newPeopleRelationUnit);
//							}
							for (Integer i : image.getRelatedPeopleList()) {
								PeopleRelation newPeopleRelationUnit = new PeopleRelation();
								newPeopleRelationUnit.setPeopleId(i.longValue());
								newPeopleRelationUnit.setEntityId(imageId);
								newPeopleRelationUnit.setType(6);
								peopleRelationService.save(newPeopleRelationUnit);
							}
						}

					}
					// 活动的音频
					for (Audio audio : audioList) {
						Audio newAudioUnit = new Audio();
						newAudioUnit.setEntityId(activityId);
						newAudioUnit.setType(2);
						newAudioUnit.setName(audio.getName());
						newAudioUnit.setUrl(audio.getUrl());
						newAudioUnit.setDescription(audio.getDescription());
						newAudioUnit.setCreateTime(audio.getCreateTime());
						newAudioUnit.setCreateTimeType(1);
						newAudioUnit.setState(true);
						newAudioUnit.setCname(username);
						newAudioUnit.setCtime(new Timestamp(System.currentTimeMillis()));
						audioService.save(newAudioUnit);
						Long audioId = newAudioUnit.getId();
						// 加活动的音频的相关人物
						if (audio.getRelatedPeopleList() != null) {
							for (Integer i : audio.getRelatedPeopleList()) {
								PeopleRelation newPeopleRelationUnit = new PeopleRelation();
								newPeopleRelationUnit.setPeopleId(i.longValue());
								newPeopleRelationUnit.setEntityId(audioId);
								newPeopleRelationUnit.setType(7);
								peopleRelationService.save(newPeopleRelationUnit);
							}
						}
					}
					// 活动的视频
					for (Video video : videoList) {
						Video newVideoUnit = new Video();
						newVideoUnit.setEntityId(activityId);
						newVideoUnit.setType(2);
						newVideoUnit.setName(video.getName());
						newVideoUnit.setUrl(video.getUrl());
						newVideoUnit.setDescription(video.getDescription());
						newVideoUnit.setCreateTime(video.getCreateTime());
						newVideoUnit.setCreateTimeType(1);
						newVideoUnit.setState(true);
						newVideoUnit.setCname(username);
						newVideoUnit.setCtime(new Timestamp(System.currentTimeMillis()));
						videoService.save(newVideoUnit);
						Long videoId = newVideoUnit.getId();
						// 加活动的视频的相关人物
						if (video.getRelatedPeopleList() != null) {
							for (Integer i : video.getRelatedPeopleList()) {
								PeopleRelation newPeopleRelationUnit = new PeopleRelation();
								newPeopleRelationUnit.setPeopleId(i.longValue());
								newPeopleRelationUnit.setEntityId(videoId);
								newPeopleRelationUnit.setType(8);
								peopleRelationService.save(newPeopleRelationUnit);
							}
						}
					}
				}
			}

			// 加社团的刊物
			if (!wrapJournalList.isEmpty()) {
				for (WrapJournal wrapJournal : wrapJournalList) {
					Journal newJournalUnit = new Journal();
					newJournalUnit.setOrganizationId(organizationId);
					newJournalUnit.setName(wrapJournal.getJournal().getName());
					newJournalUnit.setNameHistory(wrapJournal.getJournal().getNameHistory());
					newJournalUnit.setDescription(wrapJournal.getJournal().getDescription());
					newJournalUnit.setStartTime(wrapJournal.getJournal().getStartTime());
					newJournalUnit.setStartTimeType(1);
					newJournalUnit.setEndTime(wrapJournal.getJournal().getEndTime());
					newJournalUnit.setEndTimeType(1);
					newJournalUnit.setState(true);
					newJournalUnit.setCname(username);
					newJournalUnit.setCtime(new Timestamp(System.currentTimeMillis()));
					journalService.save(newJournalUnit);
					Long journalId = newJournalUnit.getId();
					// 刊物的相关人物
					if (wrapJournal.getRelatedPeopleList() != null) {
						for (Integer i : wrapJournal.getRelatedPeopleList()) {
							PeopleRelation newPeopleRelationUnit = new PeopleRelation();
							newPeopleRelationUnit.setPeopleId(i.longValue());
							newPeopleRelationUnit.setEntityId(journalId);
							newPeopleRelationUnit.setType(3);
							peopleRelationService.save(newPeopleRelationUnit);
						}
					}
					// 加刊物的图片
					List<Image> imageList = wrapJournal.getImageList();
					for (Image image : imageList) {
						Image newImageUnit = new Image();
						newImageUnit.setEntityId(journalId);
						newImageUnit.setType(3);
						newImageUnit.setName(image.getName());
						newImageUnit.setUrl(image.getUrl());
						newImageUnit.setDescription(image.getDescription());
						newImageUnit.setPhotographer(image.getPhotographer());
						newImageUnit.setCreateTime(image.getCreateTime());
						newImageUnit.setCreateTimeType(1);
						newImageUnit.setState(true);
						newImageUnit.setCname(username);
						newImageUnit.setCtime(new Timestamp(System.currentTimeMillis()));
						imageService.save(newImageUnit);
						Long imageId = newImageUnit.getId();
						// 加刊物的图片的相关人物
						if (image.getRelatedPeopleList() != null) {
							for (Integer i : image.getRelatedPeopleList()) {
								PeopleRelation newPeopleRelationUnit = new PeopleRelation();
								newPeopleRelationUnit.setPeopleId(i.longValue());
								newPeopleRelationUnit.setEntityId(imageId);
								newPeopleRelationUnit.setType(6);
								peopleRelationService.save(newPeopleRelationUnit);
							}
						}
					}
				}
			}

			// 加社团的文献
			if (!wrapLiteratureList.isEmpty()) {
				for (WrapLiterature wrapLiterature : wrapLiteratureList) {
					Literature newLiteratureUnit = new Literature();
					newLiteratureUnit.setOrganizationId(organizationId);
					newLiteratureUnit.setType(wrapLiterature.getLiterature().getType());
					newLiteratureUnit.setName(wrapLiterature.getLiterature().getName());
					newLiteratureUnit.setDescription(wrapLiterature.getLiterature().getDescription());
					newLiteratureUnit.setCreateTime(wrapLiterature.getLiterature().getCreateTime());
					newLiteratureUnit.setCreateTimeType(1);
					newLiteratureUnit.setSource(wrapLiterature.getLiterature().getSource());
					newLiteratureUnit.setState(true);
					newLiteratureUnit.setCname(username);
					newLiteratureUnit.setCtime(new Timestamp(System.currentTimeMillis()));
					literatureService.save(newLiteratureUnit);
					Long literatureId = newLiteratureUnit.getId();
					// 文献的相关人物
					if (wrapLiterature.getRelatedPeopleList() != null) {
						for (Integer i : wrapLiterature.getRelatedPeopleList()) {
							PeopleRelation newPeopleRelationUnit = new PeopleRelation();
							newPeopleRelationUnit.setPeopleId(i.longValue());
							newPeopleRelationUnit.setEntityId(literatureId);
							newPeopleRelationUnit.setType(4);
							peopleRelationService.save(newPeopleRelationUnit);
						}
					}
					// 加文献的图片
					List<Image> imageList = wrapLiterature.getImageList();
					for (Image image : imageList) {
						Image newImageUnit = new Image();
						newImageUnit.setEntityId(literatureId);
						newImageUnit.setType(4);
						newImageUnit.setName(image.getName());
						newImageUnit.setUrl(image.getUrl());
						newImageUnit.setDescription(image.getDescription());
						newImageUnit.setPhotographer(image.getPhotographer());
						newImageUnit.setCreateTime(image.getCreateTime());
						newImageUnit.setCreateTimeType(1);
						newImageUnit.setState(true);
						newImageUnit.setCname(username);
						newImageUnit.setCtime(new Timestamp(System.currentTimeMillis()));
						imageService.save(newImageUnit);
						Long imageId = newImageUnit.getId();
						// 加文献的图片的相关人物
						if (image.getRelatedPeopleList() != null) {
							for (Integer i : image.getRelatedPeopleList()) {
								PeopleRelation newPeopleRelationUnit = new PeopleRelation();
								newPeopleRelationUnit.setPeopleId(i.longValue());
								newPeopleRelationUnit.setEntityId(imageId);
								newPeopleRelationUnit.setType(6);
								peopleRelationService.save(newPeopleRelationUnit);
							}
						}

					}
				}
			}

			// 加社团的实物
			if (!wrapRealObjectList.isEmpty()) {
				for (WrapRealObject wrapRealObject : wrapRealObjectList) {
					RealObject newRealObjectUnit = new RealObject();
					newRealObjectUnit.setOrganizationId(organizationId);
					newRealObjectUnit.setName(wrapRealObject.getRealObject().getName());
					newRealObjectUnit.setDescription(wrapRealObject.getRealObject().getDescription());
					newRealObjectUnit.setSource(wrapRealObject.getRealObject().getSource());
					newRealObjectUnit.setCreateTime(wrapRealObject.getRealObject().getCreateTime());
					newRealObjectUnit.setCreateTimeType(1);
					newRealObjectUnit.setState(true);
					newRealObjectUnit.setCname(username);
					newRealObjectUnit.setCtime(new Timestamp(System.currentTimeMillis()));
					realObjectService.save(newRealObjectUnit);
					Long realObjectId = newRealObjectUnit.getId();
					// 加实物的相关人物
					if (wrapRealObject.getRelatedPeopleList() != null) {
						for (Integer i : wrapRealObject.getRelatedPeopleList()) {
							PeopleRelation newPeopleRelationUnit = new PeopleRelation();
							newPeopleRelationUnit.setPeopleId(i.longValue());
							newPeopleRelationUnit.setEntityId(realObjectId);
							newPeopleRelationUnit.setType(5);
							peopleRelationService.save(newPeopleRelationUnit);
						}
					}
					// 加实物的图片
					List<Image> imageList = wrapRealObject.getImageList();
					List<Audio> audioList = wrapRealObject.getAudioList();
					List<Video> videoList = wrapRealObject.getVideoList();
					for (Image image : imageList) {
						Image newImageUnit = new Image();
						newImageUnit.setEntityId(realObjectId);
						newImageUnit.setType(5);
						newImageUnit.setName(image.getName());
						newImageUnit.setUrl(image.getUrl());
						newImageUnit.setDescription(image.getDescription());
						newImageUnit.setPhotographer(image.getPhotographer());
						newImageUnit.setCreateTime(image.getCreateTime());
						newImageUnit.setCreateTimeType(1);
						newImageUnit.setState(true);
						newImageUnit.setCname(username);
						newImageUnit.setCtime(new Timestamp(System.currentTimeMillis()));
						imageService.save(newImageUnit);
						Long imageId = newImageUnit.getId();
						// 加实物的图片的相关人物
						if (image.getRelatedPeopleList() != null) {
							for (Integer i : image.getRelatedPeopleList()) {
								PeopleRelation newPeopleRelationUnit = new PeopleRelation();
								newPeopleRelationUnit.setPeopleId(i.longValue());
								newPeopleRelationUnit.setEntityId(imageId);
								newPeopleRelationUnit.setType(6);
								peopleRelationService.save(newPeopleRelationUnit);
							}
						}
					}
					// 加实物的音频
					for (Audio audio : audioList) {
						Audio newAudioUnit = new Audio();
						newAudioUnit.setEntityId(realObjectId);
						newAudioUnit.setType(5);
						newAudioUnit.setName(audio.getName());
						newAudioUnit.setUrl(audio.getUrl());
						newAudioUnit.setDescription(audio.getDescription());
						newAudioUnit.setCreateTime(audio.getCreateTime());
						newAudioUnit.setCreateTimeType(1);
						newAudioUnit.setState(true);
						newAudioUnit.setCname(username);
						newAudioUnit.setCtime(new Timestamp(System.currentTimeMillis()));
						audioService.save(newAudioUnit);
						Long audioId = newAudioUnit.getId();
						// 加实物的音频的相关人物
						if (audio.getRelatedPeopleList() != null) {
							for (Integer i : audio.getRelatedPeopleList()) {
								PeopleRelation newPeopleRelationUnit = new PeopleRelation();
								newPeopleRelationUnit.setPeopleId(i.longValue());
								newPeopleRelationUnit.setEntityId(audioId);
								newPeopleRelationUnit.setType(7);
								peopleRelationService.save(newPeopleRelationUnit);
							}
						}
					}
					// 加实物的视频
					for (Video video : videoList) {
						Video newVideoUnit = new Video();
						newVideoUnit.setEntityId(realObjectId);
						newVideoUnit.setType(5);
						newVideoUnit.setName(video.getName());
						newVideoUnit.setUrl(video.getUrl());
						newVideoUnit.setDescription(video.getDescription());
						newVideoUnit.setCreateTime(video.getCreateTime());
						newVideoUnit.setCreateTimeType(1);
						newVideoUnit.setState(true);
						newVideoUnit.setCname(username);
						newVideoUnit.setCtime(new Timestamp(System.currentTimeMillis()));
						videoService.save(newVideoUnit);
						Long videoId = newVideoUnit.getId();
						// 加实物的视频的相关人物
						if (video.getRelatedPeopleList() != null) {
							for (Integer i : video.getRelatedPeopleList()) {
								PeopleRelation newPeopleRelationUnit = new PeopleRelation();
								newPeopleRelationUnit.setPeopleId(i.longValue());
								newPeopleRelationUnit.setEntityId(videoId);
								newPeopleRelationUnit.setType(8);
								peopleRelationService.save(newPeopleRelationUnit);
							}
						}
					}
				}
			}

			// 加社团的章程
			if (!wrapConstitutionList.isEmpty()) {
				for (WrapConstitution wrapConstitution : wrapConstitutionList) {
					Constitution newConstitutionUnit = new Constitution();
					newConstitutionUnit.setOrganizationId(organizationId);
					newConstitutionUnit.setConstitutionName(wrapConstitution.getConstitution().getConstitutionName());
					newConstitutionUnit.setCreateTime(wrapConstitution.getConstitution().getCreateTime());
					newConstitutionUnit.setCreateTimeType(1);
					newConstitutionUnit.setState(true);
					newConstitutionUnit.setCname(username);
					newConstitutionUnit.setCtime(new Timestamp(System.currentTimeMillis()));
					constitutionService.save(newConstitutionUnit);
					Long constitutionId = newConstitutionUnit.getId();
					List<Image> imageList = wrapConstitution.getImageList();
					// 加章程的图片
					for (Image image : imageList) {
						Image newImageUnit = new Image();
						newImageUnit.setEntityId(constitutionId);
						newImageUnit.setType(6);
						newImageUnit.setName(image.getName());
						newImageUnit.setUrl(image.getUrl());
						newImageUnit.setDescription(image.getDescription());
						newImageUnit.setPhotographer(image.getPhotographer());
						newImageUnit.setCreateTime(image.getCreateTime());
						newImageUnit.setCreateTimeType(1);
						newImageUnit.setState(true);
						newImageUnit.setCname(username);
						newImageUnit.setCtime(new Timestamp(System.currentTimeMillis()));
						imageService.save(newImageUnit);
						Long imageId = newImageUnit.getId();
						// 加章程的图片的相关人物
						if (image.getRelatedPeopleList() != null) {
							for (Integer i : image.getRelatedPeopleList()) {
								PeopleRelation newPeopleRelationUnit = new PeopleRelation();
								newPeopleRelationUnit.setPeopleId(i.longValue());
								newPeopleRelationUnit.setEntityId(imageId);
								newPeopleRelationUnit.setType(6);
								peopleRelationService.save(newPeopleRelationUnit);
							}
						}
					}
				}
			}

			// 加社团的历史索引
			if (!wrapHistoricalDataIndexList.isEmpty()) {
				for (WrapHistoricalDataIndex wrapHistoricalDataIndex : wrapHistoricalDataIndexList) {
					HistoricalDataIndex newHistoricalDataIndexUnit = new HistoricalDataIndex();
					newHistoricalDataIndexUnit.setOrganizationId(organizationId);
					newHistoricalDataIndexUnit.setName(wrapHistoricalDataIndex.getHistoricalDataIndex().getName());
					newHistoricalDataIndexUnit
							.setDescription(wrapHistoricalDataIndex.getHistoricalDataIndex().getDescription());
					newHistoricalDataIndexUnit
							.setCreateTime(wrapHistoricalDataIndex.getHistoricalDataIndex().getCreateTime());
					newHistoricalDataIndexUnit.setCreateTimeType(1);
					newHistoricalDataIndexUnit.setState(true);
					newHistoricalDataIndexUnit.setCname(username);
					newHistoricalDataIndexUnit.setCtime(new Timestamp(System.currentTimeMillis()));
					historicalDataIndexService.save(newHistoricalDataIndexUnit);
					Long historicalDataIndexId = newHistoricalDataIndexUnit.getId();
					List<Image> imageList = wrapHistoricalDataIndex.getImageList();
					// 加历史索引的图片
					for (Image image : imageList) {
						Image newImageUnit = new Image();
						newImageUnit.setEntityId(historicalDataIndexId);
						newImageUnit.setType(7);
						newImageUnit.setName(image.getName());
						newImageUnit.setUrl(image.getUrl());
						newImageUnit.setDescription(image.getDescription());
						newImageUnit.setPhotographer(image.getPhotographer());
						newImageUnit.setCreateTime(image.getCreateTime());
						newImageUnit.setCreateTimeType(1);
						newImageUnit.setState(true);
						newImageUnit.setCname(username);
						newImageUnit.setCtime(new Timestamp(System.currentTimeMillis()));
						imageService.save(newImageUnit);
						Long imageId = newImageUnit.getId();
						// 加历史索引的图片的相关人物
						if (image.getRelatedPeopleList() != null) {
							for (Integer i : image.getRelatedPeopleList()) {
								PeopleRelation newPeopleRelationUnit = new PeopleRelation();
								newPeopleRelationUnit.setPeopleId(i.longValue());
								newPeopleRelationUnit.setEntityId(imageId);
								newPeopleRelationUnit.setType(6);
								peopleRelationService.save(newPeopleRelationUnit);
							}
						}
					}
				}
			}

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
	public Json edit(WrapOrganization wrapOrganization, HttpSession session) {
		// TODO Auto-generated method stub
		Json j = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			Organization oldOrganization = organizationService.find(wrapOrganization.getOrganization().getId());
			List<OfficePlace> officePlaceList = wrapOrganization.getOfficePlaceList();
			List<WrapActivity> wrapActivityList = wrapOrganization.getWrapActivityList();
			List<WrapJournal> wrapJournalList = wrapOrganization.getWrapJournalList();
			List<WrapLiterature> wrapLiteratureList = wrapOrganization.getWrapLiteratureList();
			List<WrapRealObject> wrapRealObjectList = wrapOrganization.getWrapRealObjectList();
			List<WrapConstitution> wrapConstitutionList = wrapOrganization.getWrapConstitutionList();
			List<WrapHistoricalDataIndex> wrapHistoricalDataIndexList = wrapOrganization
					.getWrapHistoricalDataIndexList();
			// 修改旧的社团
			oldOrganization.setName(wrapOrganization.getOrganization().getName());
			oldOrganization.setDescription(wrapOrganization.getOrganization().getDescription());
			oldOrganization.setNameHistory(wrapOrganization.getOrganization().getNameHistory());
			oldOrganization.setStartTime(wrapOrganization.getOrganization().getStartTime());
			oldOrganization.setStartTimeType(wrapOrganization.getOrganization().getStartTimeType());
			oldOrganization.setEndTime(wrapOrganization.getOrganization().getEndTime());
			oldOrganization.setEndTimeType(wrapOrganization.getOrganization().getEndTimeType());
			oldOrganization.setPlace(wrapOrganization.getOrganization().getPlace());
			oldOrganization.setCreator(wrapOrganization.getOrganization().getCreator());
			oldOrganization.setMember(wrapOrganization.getOrganization().getMember());
			oldOrganization.setLeader(wrapOrganization.getOrganization().getLeader());
			oldOrganization.setSecretariat(wrapOrganization.getOrganization().getSecretariat());
			oldOrganization.setLogoUrl(wrapOrganization.getOrganization().getLogoUrl());
			oldOrganization.setClickNumber(wrapOrganization.getOrganization().getClickNumber());
			oldOrganization.setState(true);
			oldOrganization.setMname(username);
			oldOrganization.setMtime(new Timestamp(System.currentTimeMillis()));
			Long organizationId = oldOrganization.getId();
			organizationService.update(oldOrganization);
			// 删除旧的社团的相关人物
			String sqlOne = "delete from sk_people_relation where entity_id=" + organizationId + " and type=1";
			peopleRelationService.deleteBySql(sqlOne);
			// 新增新的社团的相关人物
			if (!wrapOrganization.getRelatedPeopleList().isEmpty()) {
				for (PeopleRelation peopleRelation : wrapOrganization.getRelatedPeopleList()) {
					PeopleRelation newPeopleRelationUnit = new PeopleRelation();
					newPeopleRelationUnit.setPeopleId(peopleRelation.getPeopleId());
					newPeopleRelationUnit.setEntityId(organizationId);
					newPeopleRelationUnit.setType(1);
					newPeopleRelationUnit.setRelationDescription(peopleRelation.getRelationDescription());
					;
					peopleRelationService.save(newPeopleRelationUnit);
				}
			}
			// 删除旧的社团的办公地点
			String sql = "delete from sk_office_place where organization_id=" + organizationId;
			officePlaceService.deleteBySql(sql);
			// 新增新的办公地点
			if (!officePlaceList.isEmpty()) {
				for (OfficePlace officePlace : officePlaceList) {
					OfficePlace newOfficePlaceUnit = new OfficePlace();
					newOfficePlaceUnit.setOrganizationId(organizationId);
					newOfficePlaceUnit.setOfficePlace(officePlace.getOfficePlace());
					newOfficePlaceUnit.setStartTime(officePlace.getStartTime());
					newOfficePlaceUnit.setStartTimeType(1);
					newOfficePlaceUnit.setEndTime(officePlace.getEndTime());
					newOfficePlaceUnit.setEndTimeType(1);
					newOfficePlaceUnit.setState(true);
					newOfficePlaceUnit.setMname(username);
					newOfficePlaceUnit.setMtime(new Timestamp(System.currentTimeMillis()));
					officePlaceService.save(newOfficePlaceUnit);
				}
			}

			List<Filter> filter = new ArrayList<>();
			filter.add(Filter.eq("organizationId", organizationId));
			List<Activity> oldActivityList = activityService.findList(null, filter, null);
			List<Journal> oldJournalList = journalService.findList(null, filter, null);
			List<Literature> oldLiteratureList = literatureService.findList(null, filter, null);
			List<RealObject> oldRealObjectList = realObjectService.findList(null, filter, null);
			List<Constitution> oldConstitutionList = constitutionService.findList(null, filter, null);
			List<HistoricalDataIndex> oldHistoricalDataIndexList = historicalDataIndexService.findList(null, filter,
					null);
			// 操作旧的社团的活动
			if (!oldActivityList.isEmpty()) {
				for (Activity activity : oldActivityList) {
					Long activityId = activity.getId();// 拿到每个活动的旧ID
					filter.clear();
					filter.add(Filter.eq("type", 2));
					filter.add(Filter.eq("entityId", activityId));
					List<Image> oldImageList = imageService.findList(null, filter, null);// 拿到每个活动的所有图片
					List<Audio> oldAudioList = audioService.findList(null, filter, null);// 拿到每个活动的所有音频
					List<Video> oldVideoList = videoService.findList(null, filter, null);// 拿到每个活动的所有视频
					List<PeopleRelation> oldMainPeopleRelationList = peopleRelationService.findList(null, filter, null);
					if (!oldImageList.isEmpty()) {
						for (Image image : oldImageList) {
							Long imageId = image.getId();
							filter.clear();
							filter.add(Filter.eq("type", 6));
							filter.add(Filter.eq("entityId", imageId));
							List<PeopleRelation> oldPeopleRelationList = peopleRelationService.findList(null, filter,
									null);// 拿到这个图片的所有相关人物
							if (!oldPeopleRelationList.isEmpty()) {
								for (PeopleRelation peopleRelation : oldPeopleRelationList) {
									peopleRelationService.delete(peopleRelation);// 删除旧的这个图片的相关人物
								}
							}
							imageService.delete(image);// 删除这个图片
						}
					}
					if (!oldAudioList.isEmpty()) {
						for (Audio audio : oldAudioList) {
							Long audioId = audio.getId();
							filter.clear();
							filter.add(Filter.eq("type", 7));
							filter.add(Filter.eq("entityId", audioId));
							List<PeopleRelation> oldPeopleRelationList = peopleRelationService.findList(null, filter,
									null);// 拿到这个音频的所有相关人物
							if (!oldPeopleRelationList.isEmpty()) {
								for (PeopleRelation peopleRelation : oldPeopleRelationList) {
									peopleRelationService.delete(peopleRelation);// 删除旧的这个音频的相关人物
								}
							}
							audioService.delete(audio);// 删除这个音频
						}
					}
					if (!oldVideoList.isEmpty()) {
						for (Video video : oldVideoList) {
							Long videoId = video.getId();
							filter.clear();
							filter.add(Filter.eq("type", 8));
							filter.add(Filter.eq("entityId", videoId));
							List<PeopleRelation> oldPeopleRelationList = peopleRelationService.findList(null, filter,
									null);// 拿到这个视频的所有相关人物
							if (!oldPeopleRelationList.isEmpty()) {
								for (PeopleRelation peopleRelation : oldPeopleRelationList) {
									peopleRelationService.delete(peopleRelation);// 删除旧的这个视频的相关人物
								}
							}
							videoService.delete(video);// 删除这个视频
						}
					}
					// 删除旧的活动的相关人物
					if (!oldMainPeopleRelationList.isEmpty()) {
						for (PeopleRelation peopleRelation : oldMainPeopleRelationList) {
							peopleRelationService.delete(peopleRelation);
						}
					}
					activityService.delete(activityId);// 当已经删除了这个活动底下的图片音频视频才可以删除该活动
				}
			}
			// 加社团的活动
			if (!wrapActivityList.isEmpty()) {
				for (WrapActivity wrapActivity : wrapActivityList) {
					Activity newActivityUnit = new Activity();
					newActivityUnit.setOrganizationId(organizationId);
					newActivityUnit.setType(wrapActivity.getActivity().getType());
					newActivityUnit.setName(wrapActivity.getActivity().getName());
					newActivityUnit.setPlace(wrapActivity.getActivity().getPlace());
					newActivityUnit.setContent(wrapActivity.getActivity().getContent());
					newActivityUnit.setStartTime(wrapActivity.getActivity().getStartTime());
					newActivityUnit.setStartTimeType(1);
					newActivityUnit.setEndTime(wrapActivity.getActivity().getEndTime());
					newActivityUnit.setEndTimeType(1);
					newActivityUnit.setState(true);
					newActivityUnit.setMname(username);
					newActivityUnit.setMtime(new Timestamp(System.currentTimeMillis()));
					activityService.save(newActivityUnit);
					Long activityId = newActivityUnit.getId();
					// 加活动的相关人物
					if (wrapActivity.getRelatedPeopleList() != null) {
//						for (PeopleRelation peopleRelation : wrapActivity.getRelatedPeopleList()) {
//							PeopleRelation newPeopleRelationUnit = new PeopleRelation();
//							newPeopleRelationUnit.setPeopleId(i.longValue());
//							newPeopleRelationUnit.setEntityId(activityId);
//							newPeopleRelationUnit.setType(2);
//							peopleRelationService.save(newPeopleRelationUnit);
//						}
						for (Integer i : wrapActivity.getRelatedPeopleList()) {
							PeopleRelation newPeopleRelationUnit = new PeopleRelation();
							newPeopleRelationUnit.setPeopleId(i.longValue());
							newPeopleRelationUnit.setEntityId(activityId);
							newPeopleRelationUnit.setType(2);
							peopleRelationService.save(newPeopleRelationUnit);
						}
					}
					List<Image> imageList = wrapActivity.getImageList();
					List<Video> videoList = wrapActivity.getVideoList();
					List<Audio> audioList = wrapActivity.getAudioList();
					// 活动的图片
					for (Image image : imageList) {
						Image newImageUnit = new Image();
						newImageUnit.setEntityId(activityId);
						newImageUnit.setType(2);
						newImageUnit.setName(image.getName());
						newImageUnit.setUrl(image.getUrl());
						newImageUnit.setDescription(image.getDescription());
						newImageUnit.setPhotographer(image.getPhotographer());
						newImageUnit.setCreateTime(image.getCreateTime());
						newImageUnit.setCreateTimeType(1);
						newImageUnit.setState(true);
						newImageUnit.setMname(username);
						newImageUnit.setMtime(new Timestamp(System.currentTimeMillis()));
						imageService.save(newImageUnit);
						Long imageId = newImageUnit.getId();
						// 加活动的图片的相关人物
						if (image.getRelatedPeopleList() != null) {
							for (Integer i : image.getRelatedPeopleList()) {
								PeopleRelation newPeopleRelationUnit = new PeopleRelation();
								newPeopleRelationUnit.setPeopleId(i.longValue());
								newPeopleRelationUnit.setEntityId(imageId);
								newPeopleRelationUnit.setType(6);
								peopleRelationService.save(newPeopleRelationUnit);
							}
						}

					}
					// 活动的音频
					for (Audio audio : audioList) {
						Audio newAudioUnit = new Audio();
						newAudioUnit.setEntityId(activityId);
						newAudioUnit.setType(2);
						newAudioUnit.setName(audio.getName());
						newAudioUnit.setUrl(audio.getUrl());
						newAudioUnit.setDescription(audio.getDescription());
						newAudioUnit.setCreateTime(audio.getCreateTime());
						newAudioUnit.setCreateTimeType(1);
						newAudioUnit.setState(true);
						newAudioUnit.setMname(username);
						newAudioUnit.setMtime(new Timestamp(System.currentTimeMillis()));
						audioService.save(newAudioUnit);
						Long audioId = newAudioUnit.getId();
						// 加活动的音频的相关人物
						if (audio.getRelatedPeopleList() != null) {
							for (Integer i : audio.getRelatedPeopleList()) {
								PeopleRelation newPeopleRelationUnit = new PeopleRelation();
								newPeopleRelationUnit.setPeopleId(i.longValue());
								newPeopleRelationUnit.setEntityId(audioId);
								newPeopleRelationUnit.setType(7);
								peopleRelationService.save(newPeopleRelationUnit);
							}
						}
					}
					// 活动的视频
					for (Video video : videoList) {
						Video newVideoUnit = new Video();
						newVideoUnit.setEntityId(activityId);
						newVideoUnit.setType(2);
						newVideoUnit.setName(video.getName());
						newVideoUnit.setUrl(video.getUrl());
						newVideoUnit.setDescription(video.getDescription());
						newVideoUnit.setCreateTime(video.getCreateTime());
						newVideoUnit.setCreateTimeType(1);
						newVideoUnit.setState(true);
						newVideoUnit.setMname(username);
						newVideoUnit.setMtime(new Timestamp(System.currentTimeMillis()));
						videoService.save(newVideoUnit);
						Long videoId = newVideoUnit.getId();
						// 加活动的视频的相关人物
						if (video.getRelatedPeopleList() != null) {
							for (Integer i : video.getRelatedPeopleList()) {
								PeopleRelation newPeopleRelationUnit = new PeopleRelation();
								newPeopleRelationUnit.setPeopleId(i.longValue());
								newPeopleRelationUnit.setEntityId(videoId);
								newPeopleRelationUnit.setType(8);
								peopleRelationService.save(newPeopleRelationUnit);
							}
						}
					}
				}
			}

			// 操作旧的社团的刊物
			if (!oldJournalList.isEmpty()) {
				for (Journal journal : oldJournalList) {
					Long journalId = journal.getId();
					filter.clear();
					filter.add(Filter.eq("type", 3));
					filter.add(Filter.eq("entityId", journalId));
					List<Image> oldImageList = imageService.findList(null, filter, null);// 拿到每个刊物的所有图片
					List<PeopleRelation> oldMainPeopleRelationList = peopleRelationService.findList(null, filter, null);
					if (!oldImageList.isEmpty()) {
						for (Image image : oldImageList) {
							Long imageId = image.getId();
							filter.clear();
							filter.add(Filter.eq("type", 6));
							filter.add(Filter.eq("entityId", imageId));
							List<PeopleRelation> oldPeopleRelationList = peopleRelationService.findList(null, filter,
									null);// 拿到这个图片的所有相关人物
							if (!oldPeopleRelationList.isEmpty()) {
								for (PeopleRelation peopleRelation : oldPeopleRelationList) {
									peopleRelationService.delete(peopleRelation);// 删除旧的这个图片的相关人物
								}
							}
							imageService.delete(image);// 删除这个图片
						}
					}
					// 删除旧的刊物的相关人物
					if (!oldMainPeopleRelationList.isEmpty()) {
						for (PeopleRelation peopleRelation : oldMainPeopleRelationList) {
							peopleRelationService.delete(peopleRelation);
						}
					}
					journalService.delete(journalId);
				}
			}

			// 加社团的刊物
			if (!wrapJournalList.isEmpty()) {
				for (WrapJournal wrapJournal : wrapJournalList) {
					Journal newJournalUnit = new Journal();
					newJournalUnit.setOrganizationId(organizationId);
					newJournalUnit.setName(wrapJournal.getJournal().getName());
					newJournalUnit.setNameHistory(wrapJournal.getJournal().getNameHistory());
					newJournalUnit.setDescription(wrapJournal.getJournal().getDescription());
					newJournalUnit.setStartTime(wrapJournal.getJournal().getStartTime());
					newJournalUnit.setStartTimeType(1);
					newJournalUnit.setEndTime(wrapJournal.getJournal().getEndTime());
					newJournalUnit.setEndTimeType(1);
					newJournalUnit.setState(true);
					newJournalUnit.setMname(username);
					newJournalUnit.setMtime(new Timestamp(System.currentTimeMillis()));
					journalService.save(newJournalUnit);
					Long journalId = newJournalUnit.getId();
					// 刊物的相关人物
					if (wrapJournal.getRelatedPeopleList() != null) {
						for (Integer i : wrapJournal.getRelatedPeopleList()) {
							PeopleRelation newPeopleRelationUnit = new PeopleRelation();
							newPeopleRelationUnit.setPeopleId(i.longValue());
							newPeopleRelationUnit.setEntityId(journalId);
							newPeopleRelationUnit.setType(3);
							peopleRelationService.save(newPeopleRelationUnit);
						}
					}
					// 加刊物的图片
					List<Image> imageList = wrapJournal.getImageList();
					for (Image image : imageList) {
						image.setEntityId(journalId);
						image.setType(3);
						image.setState(true);
						image.setMname(username);
						image.setMtime(new Timestamp(System.currentTimeMillis()));
						imageService.save(image);
						Long imageId = image.getId();
						// 加刊物的图片的相关人物
						if (image.getRelatedPeopleList() != null) {
							for (Integer i : image.getRelatedPeopleList()) {
								PeopleRelation newPeopleRelationUnit = new PeopleRelation();
								newPeopleRelationUnit.setPeopleId(i.longValue());
								newPeopleRelationUnit.setEntityId(imageId);
								newPeopleRelationUnit.setType(6);
								peopleRelationService.save(newPeopleRelationUnit);
							}
						}
					}
				}
			}

			// 操作旧的社团的文献
			if (!oldLiteratureList.isEmpty()) {
				for (Literature literature : oldLiteratureList) {
					Long literatureId = literature.getId();
					filter.clear();
					filter.add(Filter.eq("type", 4));
					filter.add(Filter.eq("entityId", literatureId));
					List<Image> oldImageList = imageService.findList(null, filter, null);// 拿到每个文献的所有图片
					List<PeopleRelation> oldMainPeopleRelationList = peopleRelationService.findList(null, filter, null);
					if (!oldImageList.isEmpty()) {
						for (Image image : oldImageList) {
							Long imageId = image.getId();
							filter.clear();
							filter.add(Filter.eq("type", 6));
							filter.add(Filter.eq("entityId", imageId));
							List<PeopleRelation> oldPeopleRelationList = peopleRelationService.findList(null, filter,
									null);// 拿到这个图片的所有相关人物
							if (!oldPeopleRelationList.isEmpty()) {
								for (PeopleRelation peopleRelation : oldPeopleRelationList) {
									peopleRelationService.delete(peopleRelation);// 删除旧的这个图片的相关人物
								}
							}
							imageService.delete(image);// 删除这个图片
						}
					}
					// 删除旧的文献的相关人物
					if (!oldMainPeopleRelationList.isEmpty()) {
						for (PeopleRelation peopleRelation : oldMainPeopleRelationList) {
							peopleRelationService.delete(peopleRelation);
						}
					}
					literatureService.delete(literatureId);
				}
			}

			// 加社团的文献
			if (!wrapLiteratureList.isEmpty()) {
				for (WrapLiterature wrapLiterature : wrapLiteratureList) {
					Literature newLiteratureUnit = new Literature();
					newLiteratureUnit.setOrganizationId(organizationId);
					newLiteratureUnit.setType(wrapLiterature.getLiterature().getType());
					newLiteratureUnit.setName(wrapLiterature.getLiterature().getName());
					newLiteratureUnit.setDescription(wrapLiterature.getLiterature().getDescription());
					newLiteratureUnit.setCreateTime(wrapLiterature.getLiterature().getCreateTime());
					newLiteratureUnit.setCreateTimeType(1);
					newLiteratureUnit.setSource(wrapLiterature.getLiterature().getSource());
					newLiteratureUnit.setState(true);
					newLiteratureUnit.setMname(username);
					newLiteratureUnit.setMtime(new Timestamp(System.currentTimeMillis()));
					literatureService.save(newLiteratureUnit);
					Long literatureId = newLiteratureUnit.getId();
					// 文献的相关人物
					if (wrapLiterature.getRelatedPeopleList() != null) {
						for (Integer i : wrapLiterature.getRelatedPeopleList()) {
							PeopleRelation newPeopleRelationUnit = new PeopleRelation();
							newPeopleRelationUnit.setPeopleId(i.longValue());
							newPeopleRelationUnit.setEntityId(literatureId);
							newPeopleRelationUnit.setType(4);
							peopleRelationService.save(newPeopleRelationUnit);
						}
					}
					// 加文献的图片
					List<Image> imageList = wrapLiterature.getImageList();
					for (Image image : imageList) {
						Image newImageUnit = new Image();
						newImageUnit.setEntityId(literatureId);
						newImageUnit.setType(4);
						newImageUnit.setName(image.getName());
						newImageUnit.setUrl(image.getUrl());
						newImageUnit.setDescription(image.getDescription());
						newImageUnit.setPhotographer(image.getPhotographer());
						newImageUnit.setCreateTime(image.getCreateTime());
						newImageUnit.setCreateTimeType(1);
						newImageUnit.setState(true);
						newImageUnit.setMname(username);
						newImageUnit.setMtime(new Timestamp(System.currentTimeMillis()));
						imageService.save(newImageUnit);
						Long imageId = newImageUnit.getId();
						// 加文献的图片的相关人物
						if (image.getRelatedPeopleList() != null) {
							for (Integer i : image.getRelatedPeopleList()) {
								PeopleRelation newPeopleRelationUnit = new PeopleRelation();
								newPeopleRelationUnit.setPeopleId(i.longValue());
								newPeopleRelationUnit.setEntityId(imageId);
								newPeopleRelationUnit.setType(6);
								peopleRelationService.save(newPeopleRelationUnit);
							}
						}

					}
				}
			}

			// 操作旧的社团的实物
			if (!oldRealObjectList.isEmpty()) {
				for (RealObject realObject : oldRealObjectList) {
					Long realObjectId = realObject.getId();
					filter.clear();
					filter.add(Filter.eq("type", 5));
					filter.add(Filter.eq("entityId", realObjectId));
					List<Image> oldImageList = imageService.findList(null, filter, null);// 拿到每个实物的所有图片
					List<Audio> oldAudioList = audioService.findList(null, filter, null);// 拿到每个实物的所有音频
					List<Video> oldVideoList = videoService.findList(null, filter, null);// 拿到每个实物的所有视频
					List<PeopleRelation> oldMainPeopleRelationList = peopleRelationService.findList(null, filter, null);
					if (!oldImageList.isEmpty()) {
						for (Image image : oldImageList) {
							Long imageId = image.getId();
							filter.clear();
							filter.add(Filter.eq("type", 6));
							filter.add(Filter.eq("entityId", imageId));
							List<PeopleRelation> oldPeopleRelationList = peopleRelationService.findList(null, filter,
									null);// 拿到这个图片的所有相关人物
							if (!oldPeopleRelationList.isEmpty()) {
								for (PeopleRelation peopleRelation : oldPeopleRelationList) {
									peopleRelationService.delete(peopleRelation);// 删除旧的这个图片的相关人物
								}
							}
							imageService.delete(image);// 删除这个图片
						}
					}
					if (!oldAudioList.isEmpty()) {
						for (Audio audio : oldAudioList) {
							Long audioId = audio.getId();
							filter.clear();
							filter.add(Filter.eq("type", 7));
							filter.add(Filter.eq("entityId", audioId));
							List<PeopleRelation> oldPeopleRelationList = peopleRelationService.findList(null, filter,
									null);// 拿到这个音频的所有相关人物
							if (!oldPeopleRelationList.isEmpty()) {
								for (PeopleRelation peopleRelation : oldPeopleRelationList) {
									peopleRelationService.delete(peopleRelation);// 删除旧的这个音频的相关人物
								}
							}
							audioService.delete(audio);// 删除这个音频
						}
					}
					if (!oldVideoList.isEmpty()) {
						for (Video video : oldVideoList) {
							Long videoId = video.getId();
							filter.clear();
							filter.add(Filter.eq("type", 8));
							filter.add(Filter.eq("entityId", videoId));
							List<PeopleRelation> oldPeopleRelationList = peopleRelationService.findList(null, filter,
									null);// 拿到这个视频的所有相关人物
							if (!oldPeopleRelationList.isEmpty()) {
								for (PeopleRelation peopleRelation : oldPeopleRelationList) {
									peopleRelationService.delete(peopleRelation);// 删除旧的这个视频的相关人物
								}
							}
							videoService.delete(video);// 删除这个视频
						}
					}
					// 删除旧的实物的相关人物
					if (!oldMainPeopleRelationList.isEmpty()) {
						for (PeopleRelation peopleRelation : oldMainPeopleRelationList) {
							peopleRelationService.delete(peopleRelation);
						}
					}
					realObjectService.delete(realObjectId);
				}
			}

			// 加社团的实物
			if (!wrapRealObjectList.isEmpty()) {
				for (WrapRealObject wrapRealObject : wrapRealObjectList) {
					RealObject newRealObjectUnit = new RealObject();
					newRealObjectUnit.setOrganizationId(organizationId);
					newRealObjectUnit.setName(wrapRealObject.getRealObject().getName());
					newRealObjectUnit.setDescription(wrapRealObject.getRealObject().getDescription());
					newRealObjectUnit.setSource(wrapRealObject.getRealObject().getSource());
					newRealObjectUnit.setCreateTime(wrapRealObject.getRealObject().getCreateTime());
					newRealObjectUnit.setCreateTimeType(1);
					newRealObjectUnit.setState(true);
					newRealObjectUnit.setMname(username);
					newRealObjectUnit.setMtime(new Timestamp(System.currentTimeMillis()));
					realObjectService.save(newRealObjectUnit);
					Long realObjectId = newRealObjectUnit.getId();
					// 加实物的相关人物
					if (wrapRealObject.getRelatedPeopleList() != null) {
						for (Integer i : wrapRealObject.getRelatedPeopleList()) {
							PeopleRelation newPeopleRelationUnit = new PeopleRelation();
							newPeopleRelationUnit.setPeopleId(i.longValue());
							newPeopleRelationUnit.setEntityId(realObjectId);
							newPeopleRelationUnit.setType(5);
							peopleRelationService.save(newPeopleRelationUnit);
						}
					}
					// 加实物的图片
					List<Image> imageList = wrapRealObject.getImageList();
					for (Image image : imageList) {
						Image newImageUnit = new Image();
						newImageUnit.setEntityId(realObjectId);
						newImageUnit.setType(5);
						newImageUnit.setName(image.getName());
						newImageUnit.setUrl(image.getUrl());
						newImageUnit.setDescription(image.getDescription());
						newImageUnit.setPhotographer(image.getPhotographer());
						newImageUnit.setCreateTime(image.getCreateTime());
						newImageUnit.setCreateTimeType(1);
						newImageUnit.setState(true);
						newImageUnit.setMname(username);
						newImageUnit.setMtime(new Timestamp(System.currentTimeMillis()));
						imageService.save(newImageUnit);
						Long imageId = newImageUnit.getId();
						// 加实物的图片的相关人物
						if (image.getRelatedPeopleList() != null) {
							for (Integer i : image.getRelatedPeopleList()) {
								PeopleRelation newPeopleRelationUnit = new PeopleRelation();
								newPeopleRelationUnit.setPeopleId(i.longValue());
								newPeopleRelationUnit.setEntityId(imageId);
								newPeopleRelationUnit.setType(6);
								peopleRelationService.save(newPeopleRelationUnit);
							}
						}
					}
					// 加实物的音频
					List<Audio> audioList = wrapRealObject.getAudioList();
					for (Audio audio : audioList) {
						Audio newAudioUnit = new Audio();
						newAudioUnit.setEntityId(realObjectId);
						newAudioUnit.setType(5);
						newAudioUnit.setName(audio.getName());
						newAudioUnit.setUrl(audio.getUrl());
						newAudioUnit.setDescription(audio.getDescription());
						newAudioUnit.setCreateTime(audio.getCreateTime());
						newAudioUnit.setCreateTimeType(1);
						newAudioUnit.setState(true);
						newAudioUnit.setMname(username);
						newAudioUnit.setMtime(new Timestamp(System.currentTimeMillis()));
						audioService.save(newAudioUnit);
						Long audioId = newAudioUnit.getId();
						// 加实物的音频的相关人物
						if (audio.getRelatedPeopleList() != null) {
							for (Integer i : audio.getRelatedPeopleList()) {
								PeopleRelation newPeopleRelationUnit = new PeopleRelation();
								newPeopleRelationUnit.setPeopleId(i.longValue());
								newPeopleRelationUnit.setEntityId(audioId);
								newPeopleRelationUnit.setType(7);
								peopleRelationService.save(newPeopleRelationUnit);
							}
						}
					}
					// 加实物的视频
					List<Video> videoList = wrapRealObject.getVideoList();
					for (Video video : videoList) {
						Video newVideoUnit = new Video();
						newVideoUnit.setEntityId(realObjectId);
						newVideoUnit.setType(5);
						newVideoUnit.setName(video.getName());
						newVideoUnit.setUrl(video.getUrl());
						newVideoUnit.setDescription(video.getDescription());
						newVideoUnit.setCreateTime(video.getCreateTime());
						newVideoUnit.setCreateTimeType(1);
						newVideoUnit.setState(true);
						newVideoUnit.setMname(username);
						newVideoUnit.setMtime(new Timestamp(System.currentTimeMillis()));
						videoService.save(newVideoUnit);
						Long videoId = newVideoUnit.getId();
						// 加实物的视频的相关人物
						if (video.getRelatedPeopleList() != null) {
							for (Integer i : video.getRelatedPeopleList()) {
								PeopleRelation newPeopleRelationUnit = new PeopleRelation();
								newPeopleRelationUnit.setPeopleId(i.longValue());
								newPeopleRelationUnit.setEntityId(videoId);
								newPeopleRelationUnit.setType(8);
								peopleRelationService.save(newPeopleRelationUnit);
							}
						}
					}
				}
			}

			// 操作旧的社团的章程
			if (!oldConstitutionList.isEmpty()) {
				for (Constitution constitution : oldConstitutionList) {
					Long constitutionId = constitution.getId();
					filter.clear();
					filter.add(Filter.eq("type", 6));
					filter.add(Filter.eq("entityId", constitutionId));
					List<Image> oldImageList = imageService.findList(null, filter, null);// 拿到每个章程的所有图片
					if (!oldImageList.isEmpty()) {
						for (Image image : oldImageList) {
							Long imageId = image.getId();
							filter.clear();
							filter.add(Filter.eq("type", 6));
							filter.add(Filter.eq("entityId", imageId));
							List<PeopleRelation> oldPeopleRelationList = peopleRelationService.findList(null, filter,
									null);// 拿到这个图片的所有相关人物
							if (!oldPeopleRelationList.isEmpty()) {
								for (PeopleRelation peopleRelation : oldPeopleRelationList) {
									peopleRelationService.delete(peopleRelation);// 删除旧的这个图片的相关人物
								}
							}
							imageService.delete(image);// 删除这个图片
						}
					}
					constitutionService.delete(constitutionId);
				}
			}

			// 加社团的章程
			if (!wrapConstitutionList.isEmpty()) {
				for (WrapConstitution wrapConstitution : wrapConstitutionList) {
					Constitution newConstitutionUnit = new Constitution();
					newConstitutionUnit.setOrganizationId(organizationId);
					newConstitutionUnit.setConstitutionName(wrapConstitution.getConstitution().getConstitutionName());
					newConstitutionUnit.setCreateTime(wrapConstitution.getConstitution().getCreateTime());
					newConstitutionUnit.setCreateTimeType(1);
					newConstitutionUnit.setState(true);
					newConstitutionUnit.setMname(username);
					newConstitutionUnit.setMtime(new Timestamp(System.currentTimeMillis()));
					constitutionService.save(newConstitutionUnit);
					Long constitutionId = newConstitutionUnit.getId();
					List<Image> imageList = wrapConstitution.getImageList();
					// 加章程的图片
					for (Image image : imageList) {
						Image newImageUnit = new Image();
						newImageUnit.setEntityId(constitutionId);
						newImageUnit.setType(6);
						newImageUnit.setName(image.getName());
						newImageUnit.setUrl(image.getUrl());
						newImageUnit.setDescription(image.getDescription());
						newImageUnit.setPhotographer(image.getPhotographer());
						newImageUnit.setCreateTime(image.getCreateTime());
						newImageUnit.setCreateTimeType(1);
						newImageUnit.setState(true);
						newImageUnit.setMname(username);
						newImageUnit.setMtime(new Timestamp(System.currentTimeMillis()));
						imageService.save(newImageUnit);
						Long imageId = newImageUnit.getId();
						// 加章程的图片的相关人物
						if (image.getRelatedPeopleList() != null) {
							for (Integer i : image.getRelatedPeopleList()) {
								PeopleRelation newPeopleRelationUnit = new PeopleRelation();
								newPeopleRelationUnit.setPeopleId(i.longValue());
								newPeopleRelationUnit.setEntityId(imageId);
								newPeopleRelationUnit.setType(6);
								peopleRelationService.save(newPeopleRelationUnit);
							}
						}
					}
				}
			}

			// 操作旧的社团的历史索引
			if (!oldHistoricalDataIndexList.isEmpty()) {
				for (HistoricalDataIndex historicalDataIndex : oldHistoricalDataIndexList) {
					Long historicalDataIndexId = historicalDataIndex.getId();
					filter.clear();
					filter.add(Filter.eq("type", 7));
					filter.add(Filter.eq("entityId", historicalDataIndexId));
					List<Image> oldImageList = imageService.findList(null, filter, null);// 拿到每个历史索引的所有图片
					if (!oldImageList.isEmpty()) {
						for (Image image : oldImageList) {
							Long imageId = image.getId();
							filter.clear();
							filter.add(Filter.eq("type", 6));
							filter.add(Filter.eq("entityId", imageId));
							List<PeopleRelation> oldPeopleRelationList = peopleRelationService.findList(null, filter,
									null);// 拿到这个图片的所有相关人物
							if (!oldPeopleRelationList.isEmpty()) {
								for (PeopleRelation peopleRelation : oldPeopleRelationList) {
									peopleRelationService.delete(peopleRelation);// 删除旧的这个图片的相关人物
								}
							}
							imageService.delete(image);// 删除这个图片
						}
					}
					historicalDataIndexService.delete(historicalDataIndexId);
				}
			}

			// 加社团的历史索引
			if (!wrapHistoricalDataIndexList.isEmpty()) {
				for (WrapHistoricalDataIndex wrapHistoricalDataIndex : wrapHistoricalDataIndexList) {
					HistoricalDataIndex newHistoricalDataIndexUnit = new HistoricalDataIndex();
					newHistoricalDataIndexUnit.setOrganizationId(organizationId);
					newHistoricalDataIndexUnit.setName(wrapHistoricalDataIndex.getHistoricalDataIndex().getName());
					newHistoricalDataIndexUnit
							.setDescription(wrapHistoricalDataIndex.getHistoricalDataIndex().getDescription());
					newHistoricalDataIndexUnit
							.setCreateTime(wrapHistoricalDataIndex.getHistoricalDataIndex().getCreateTime());
					newHistoricalDataIndexUnit.setCreateTimeType(1);
					newHistoricalDataIndexUnit.setState(true);
					newHistoricalDataIndexUnit.setMname(username);
					newHistoricalDataIndexUnit.setMtime(new Timestamp(System.currentTimeMillis()));
					historicalDataIndexService.save(newHistoricalDataIndexUnit);
					Long historicalDataIndexId = newHistoricalDataIndexUnit.getId();
					List<Image> imageList = wrapHistoricalDataIndex.getImageList();
					// 加历史索引的图片
					for (Image image : imageList) {
						Image newImageUnit = new Image();
						newImageUnit.setEntityId(historicalDataIndexId);
						newImageUnit.setType(7);
						newImageUnit.setName(image.getName());
						newImageUnit.setUrl(image.getUrl());
						newImageUnit.setDescription(image.getDescription());
						newImageUnit.setPhotographer(image.getPhotographer());
						newImageUnit.setCreateTime(image.getCreateTime());
						newImageUnit.setCreateTimeType(1);
						newImageUnit.setState(true);
						newImageUnit.setMname(username);
						newImageUnit.setMtime(new Timestamp(System.currentTimeMillis()));
						imageService.save(newImageUnit);
						Long imageId = newImageUnit.getId();
						// 加历史索引的图片的相关人物
						if (image.getRelatedPeopleList() != null) {
							for (Integer i : image.getRelatedPeopleList()) {
								PeopleRelation newPeopleRelationUnit = new PeopleRelation();
								newPeopleRelationUnit.setPeopleId(i.longValue());
								newPeopleRelationUnit.setEntityId(imageId);
								newPeopleRelationUnit.setType(6);
								peopleRelationService.save(newPeopleRelationUnit);
							}
						}
					}
				}
			}
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
	public Json deleteById(Long organizationId) {
		// TODO Auto-generated method stub
		Json j = new Json();
		try {
			Organization oldOrganization = organizationService.find(organizationId);
			List<Filter> filter = new ArrayList<>();
			filter.add(Filter.eq("organizationId", organizationId));
			List<Activity> oldActivityList = activityService.findList(null, filter, null);
			List<Journal> oldJournalList = journalService.findList(null, filter, null);
			List<Literature> oldLiteratureList = literatureService.findList(null, filter, null);
			List<RealObject> oldRealObjectList = realObjectService.findList(null, filter, null);
			List<Constitution> oldConstitutionList = constitutionService.findList(null, filter, null);
			List<HistoricalDataIndex> oldHistoricalDataIndexList = historicalDataIndexService.findList(null, filter,
					null);
			// 删除社团的办公地点
			String sqlOne = "delete from sk_office_place where organization_id=" + organizationId;
			officePlaceService.deleteBySql(sqlOne);
			// 删除社团的相关人物
			String sqlTwo = "delete from sk_people_relation where entity_id=" + organizationId + " and type=1";
			peopleRelationService.deleteBySql(sqlTwo);
			// 删除社团的活动
			if (!oldActivityList.isEmpty()) {
				for (Activity activity : oldActivityList) {
					Long activityId = activity.getId();
					filter.clear();
					filter.add(Filter.eq("type", 2));
					filter.add(Filter.eq("entityId", activityId));
					List<Image> oldImageList = imageService.findList(null, filter, null);
					List<Audio> oldAudioList = audioService.findList(null, filter, null);
					List<Video> oldVideoList = videoService.findList(null, filter, null);
					List<PeopleRelation> oldPeopleRelationList = peopleRelationService.findList(null, filter, null);
					// 删除图片
					if (!oldImageList.isEmpty()) {
						for (Image image : oldImageList) {
							Long imageId = image.getId();
							filter.clear();
							filter.add(Filter.eq("type", 6));
							filter.add(Filter.eq("entityId", imageId));
							// 删除图片相关人物
							List<PeopleRelation> oldImagePeopleRelationList = peopleRelationService.findList(null,
									filter, null);
							for (PeopleRelation peopleRelation : oldImagePeopleRelationList) {
								peopleRelationService.delete(peopleRelation);
							}
							imageService.delete(image);
						}
					}
					// 删除音频
					if (!oldAudioList.isEmpty()) {
						for (Audio audio : oldAudioList) {
							Long audioId = audio.getId();
							filter.clear();
							filter.add(Filter.eq("type", 7));
							filter.add(Filter.eq("entityId", audioId));
							// 删除音频相关人物
							List<PeopleRelation> oldAudioPeopleRelationList = peopleRelationService.findList(null,
									filter, null);
							for (PeopleRelation peopleRelation : oldAudioPeopleRelationList) {
								peopleRelationService.delete(peopleRelation);
							}
							audioService.delete(audio);
						}
					}
					// 删除视频
					if (!oldVideoList.isEmpty()) {
						for (Video video : oldVideoList) {
							Long videoId = video.getId();
							filter.clear();
							filter.add(Filter.eq("type", 8));
							filter.add(Filter.eq("entityId", videoId));
							// 删除音频相关人物
							List<PeopleRelation> oldVideoPeopleRelationList = peopleRelationService.findList(null,
									filter, null);
							for (PeopleRelation peopleRelation : oldVideoPeopleRelationList) {
								peopleRelationService.delete(peopleRelation);
							}
							videoService.delete(video);
						}
					}
					// 删除关联人物
					if (!oldPeopleRelationList.isEmpty()) {
						for (PeopleRelation peopleRelation : oldPeopleRelationList) {
							peopleRelationService.delete(peopleRelation);
						}
					}
					activityService.delete(activityId);
				}
			}
			// 删除刊物
			if (!oldJournalList.isEmpty()) {
				for (Journal journal : oldJournalList) {
					Long journalId = journal.getId();
					filter.clear();
					filter.add(Filter.eq("type", 3));
					filter.add(Filter.eq("entityId", journalId));
					List<Image> oldImageList = imageService.findList(null, filter, null);
					List<PeopleRelation> oldPeopleRelationList = peopleRelationService.findList(null, filter, null);
					// 删除图片
					if (!oldImageList.isEmpty()) {
						for (Image image : oldImageList) {
							Long imageId = image.getId();
							filter.clear();
							filter.add(Filter.eq("type", 6));
							filter.add(Filter.eq("entityId", imageId));
							// 删除图片相关人物
							List<PeopleRelation> oldImagePeopleRelationList = peopleRelationService.findList(null,
									filter, null);
							for (PeopleRelation peopleRelation : oldImagePeopleRelationList) {
								peopleRelationService.delete(peopleRelation);
							}
							imageService.delete(image);
						}
					}
					// 删除关联人物
					if (!oldPeopleRelationList.isEmpty()) {
						for (PeopleRelation peopleRelation : oldPeopleRelationList) {
							peopleRelationService.delete(peopleRelation);
						}
					}
					journalService.delete(journalId);
				}
			}
			// 删除文献
			if (!oldLiteratureList.isEmpty()) {
				for (Literature literature : oldLiteratureList) {
					Long literatureId = literature.getId();
					filter.clear();
					filter.add(Filter.eq("type", 4));
					filter.add(Filter.eq("entityId", literatureId));
					List<Image> oldImageList = imageService.findList(null, filter, null);
					List<PeopleRelation> oldPeopleRelationList = peopleRelationService.findList(null, filter, null);
					// 删除图片
					if (!oldImageList.isEmpty()) {
						for (Image image : oldImageList) {
							Long imageId = image.getId();
							filter.clear();
							filter.add(Filter.eq("type", 6));
							filter.add(Filter.eq("entityId", imageId));
							// 删除图片相关人物
							List<PeopleRelation> oldImagePeopleRelationList = peopleRelationService.findList(null,
									filter, null);
							for (PeopleRelation peopleRelation : oldImagePeopleRelationList) {
								peopleRelationService.delete(peopleRelation);
							}
							imageService.delete(image);
						}
					}
					// 删除关联人物
					if (!oldPeopleRelationList.isEmpty()) {
						for (PeopleRelation peopleRelation : oldPeopleRelationList) {
							peopleRelationService.delete(peopleRelation);
						}
					}
					literatureService.delete(literatureId);
				}
			}
			// 删除实物
			if (!oldRealObjectList.isEmpty()) {
				for (RealObject realObject : oldRealObjectList) {
					Long realObjectId = realObject.getId();
					filter.clear();
					filter.add(Filter.eq("type", 5));
					filter.add(Filter.eq("entityId", realObjectId));
					List<Image> oldImageList = imageService.findList(null, filter, null);
					List<Audio> oldAudioList = audioService.findList(null, filter, null);
					List<Video> oldVideoList = videoService.findList(null, filter, null);
					List<PeopleRelation> oldPeopleRelationList = peopleRelationService.findList(null, filter, null);
					// 删除图片
					if (!oldImageList.isEmpty()) {
						for (Image image : oldImageList) {
							Long imageId = image.getId();
							filter.clear();
							filter.add(Filter.eq("type", 6));
							filter.add(Filter.eq("entityId", imageId));
							// 删除图片相关人物
							List<PeopleRelation> oldImagePeopleRelationList = peopleRelationService.findList(null,
									filter, null);
							for (PeopleRelation peopleRelation : oldImagePeopleRelationList) {
								peopleRelationService.delete(peopleRelation);
							}
							imageService.delete(image);
						}
					}
					// 删除音频
					if (!oldAudioList.isEmpty()) {
						for (Audio audio : oldAudioList) {
							Long audioId = audio.getId();
							filter.clear();
							filter.add(Filter.eq("type", 7));
							filter.add(Filter.eq("entityId", audioId));
							// 删除音频相关人物
							List<PeopleRelation> oldAudioPeopleRelationList = peopleRelationService.findList(null,
									filter, null);
							for (PeopleRelation peopleRelation : oldAudioPeopleRelationList) {
								peopleRelationService.delete(peopleRelation);
							}
							audioService.delete(audio);
						}
					}
					// 删除视频
					if (!oldVideoList.isEmpty()) {
						for (Video video : oldVideoList) {
							Long videoId = video.getId();
							filter.clear();
							filter.add(Filter.eq("type", 8));
							filter.add(Filter.eq("entityId", videoId));
							// 删除音频相关人物
							List<PeopleRelation> oldVideoPeopleRelationList = peopleRelationService.findList(null,
									filter, null);
							for (PeopleRelation peopleRelation : oldVideoPeopleRelationList) {
								peopleRelationService.delete(peopleRelation);
							}
							videoService.delete(video);
						}
					}
					// 删除关联人物
					if (!oldPeopleRelationList.isEmpty()) {
						for (PeopleRelation peopleRelation : oldPeopleRelationList) {
							peopleRelationService.delete(peopleRelation);
						}
					}
					realObjectService.delete(realObjectId);
				}
			}
			// 删除章程
			if (!oldConstitutionList.isEmpty()) {
				for (Constitution constitution : oldConstitutionList) {
					Long constitutionId = constitution.getId();
					filter.clear();
					filter.add(Filter.eq("type", 6));
					filter.add(Filter.eq("entityId", constitutionId));
					List<Image> oldImageList = imageService.findList(null, filter, null);
					// 删除图片
					if (!oldImageList.isEmpty()) {
						for (Image image : oldImageList) {
							Long imageId = image.getId();
							filter.clear();
							filter.add(Filter.eq("type", 6));
							filter.add(Filter.eq("entityId", imageId));
							// 删除图片相关人物
							List<PeopleRelation> oldImagePeopleRelationList = peopleRelationService.findList(null,
									filter, null);
							for (PeopleRelation peopleRelation : oldImagePeopleRelationList) {
								peopleRelationService.delete(peopleRelation);
							}
							imageService.delete(image);
						}
					}
					constitutionService.delete(constitutionId);
				}
			}
			// 删除历史索引
			if (!oldHistoricalDataIndexList.isEmpty()) {
				for (HistoricalDataIndex historicalDataIndex : oldHistoricalDataIndexList) {
					Long historicalDataIndexId = historicalDataIndex.getId();
					filter.clear();
					filter.add(Filter.eq("type", 7));
					filter.add(Filter.eq("entityId", historicalDataIndexId));
					List<Image> oldImageList = imageService.findList(null, filter, null);
					// 删除图片
					if (!oldImageList.isEmpty()) {
						for (Image image : oldImageList) {
							Long imageId = image.getId();
							filter.clear();
							filter.add(Filter.eq("type", 6));
							filter.add(Filter.eq("entityId", imageId));
							// 删除图片相关人物
							List<PeopleRelation> oldImagePeopleRelationList = peopleRelationService.findList(null,
									filter, null);
							for (PeopleRelation peopleRelation : oldImagePeopleRelationList) {
								peopleRelationService.delete(peopleRelation);
							}
							imageService.delete(image);
						}
					}
					historicalDataIndexService.delete(historicalDataIndexId);
				}
			}
			organizationService.delete(oldOrganization);
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

}
