package com.feelyou.vo;

import java.io.Serializable;

public class UserInfo implements Serializable {
	
	private String uid;
	private String utel;
	private String uname;
	private String usex;
	private String udistance;
	private String usignature;	
	private String photo;

	/**
	 * @param uid
	 * @param utel
	 * @param uname
	 * @param usex
	 * @param udistance
	 * @param usignature
	 */
	public UserInfo(String uid, String utel, String uname, String usex,
			String udistance, String usignature) {
		super();
		this.uid = uid;
		this.utel = utel;
		this.uname = uname;
		this.usex = usex;
		this.udistance = udistance;
		this.usignature = usignature;
	}
	
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getUtel() {
		return utel;
	}
	public void setUtel(String utel) {
		this.utel = utel;
	}
	public String getUname() {
		return uname;
	}
	public void setUname(String uname) {
		this.uname = uname;
	}
	public String getUsex() {
		return usex;
	}
	public void setUsex(String usex) {
		this.usex = usex;
	}
	public String getUdistance() {
		return udistance;
	}
	public void setUdistance(String udistance) {
		this.udistance = udistance;
	}
	public String getUsignature() {
		return usignature;
	}
	public void setUsignature(String usignature) {
		this.usignature = usignature;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	@Override
	public String toString() {
		return "UserInfo [uid=" + uid + ", utel=" + utel + ", uname=" + uname
				+ ", usex=" + usex + ", udistance=" + udistance
				+ ", usignature=" + usignature + ", photo=" + photo + "]";
	}
	


}
