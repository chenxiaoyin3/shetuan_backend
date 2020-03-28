package com.sn.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="sn_picture_book_resources")
public class PictureBookResources {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	private Long id;
	
	@Column(name = "picture_book_id")
	private Long pictureBookId;
	
	@Column(name = "audio_url")
	private String audioUrl;
	
	@Column(name = "photo_url")
	private String photoUrl;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "mtime", length = 19)
	private Date mtime;

	@Column(name = "mname")
	private String mname;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPictureBookId() {
		return pictureBookId;
	}

	public void setPictureBookId(Long pictureBookId) {
		this.pictureBookId = pictureBookId;
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

	public Date getMtime() {
		return mtime;
	}

	public void setMtime(Date mtime) {
		this.mtime = mtime;
	}

	public String getMname() {
		return mname;
	}

	public void setMname(String mname) {
		this.mname = mname;
	}

//	public PictureBook getPictureBook() {
//		return pictureBook;
//	}
//
//	public void setPictureBook(PictureBook pictureBook) {
//		this.pictureBook = pictureBook;
//	}


}
