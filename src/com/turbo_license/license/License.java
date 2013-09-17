package com.turbo_license.license;

import java.sql.Blob;

import com.turbo_license.dao.LicenseDAO;



public class License extends LicenseDAO {

	public int getId() {
		return this.getIntVal("id");
	}
	public String getKeycode () {
		return this.getStrVal("keycode");
	}
	public String getName () {
		return this.getStrVal("name");
	}
	public String getCreatedDate () {
		return this.getStrVal("created_date");
	}
	public String getStartDate () {
		return this.getStrVal("start_date");
	}
	public String getExpireDate () {
		return this.getStrVal("expire_date");
	}
	public String getUpdatedExpireDate() {
		return this.getStrVal("updated_expire_date");
	}
	public int getMaxNumber () {
		return this.getIntVal("max_number");
	}
	public Blob getPublicKey () {
		return (Blob) this.getValue("public_key");
	}
	public Blob getPrivateKey () {
		return (Blob) this.getValue("private_key");
	}
	public Blob getEncryptedLicense () {
		return (Blob) this.getValue("encrypted_license");
	}
	public String getFileName() {
		return this.getStrVal("file_name");
	}
	public String getAttr1() {
		return this.getStrVal("attr1");
	}
	public String getAttr2() {
		return this.getStrVal("attr2");
	}
	public String getAttr3() {
		return this.getStrVal("attr3");
	}
	public String getAttr4() {
		return this.getStrVal("attr4");
	}
	public String getAttr5() {
		return this.getStrVal("attr5");
	}
	public String getUserId() {
		return this.getStrVal("user_id");
	}
	public void setId(int val) {
		this.setField("id", val);
	}
	public void setKeycode(String val) {
		this.setField("keycode", val);
	}
	public void setName(String val) {
		this.setField("name", val);
	}
	public void setCreatedDate(String val) {
		this.setField("created_date", val);
	}
	public void setStartDate(String val) {
		this.setField("start_date", val);
	}
	public void setExpireDate(String val) {
		this.setField("expire_date", val);
	}
	public void setUpdatedExpireDate(String val) {
		this.setField("updated_expire_date", val);
	}
	public void setMaxNumber(int val) {
		this.setField("max_number", val);
	}
	public void setPublicKey(Blob val) {
		this.setField("public_key", val);
	}
	public void setPrivateKey(Blob val) {
		this.setField("private_key", val);
	}
	public void setEncryptedLicense(Blob val) {
		this.setField("encrypted_license", val);
	}
	public void setFileName(String val) {
		this.setField("file_name", val);
	}
	public void setAttr1(String val) {
		this.setField("attr1", val);
	}
	public void setAttr2(String val) {
		this.setField("attr2", val);
	}
	public void setAttr3(String val) {
		this.setField("attr3", val);
	}
	public void setAttr4(String val) {
		this.setField("attr4", val);
	}
	public void setAttr5(String val) {
		this.setField("attr5", val);
	}
	public void setUserId(String val) {
		this.setField("user_id", val);
	}
}
