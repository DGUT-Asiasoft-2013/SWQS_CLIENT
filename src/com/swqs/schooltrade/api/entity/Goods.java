package com.swqs.schooltrade.api.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.swqs.schooltrade.api.entity.Image;
import com.swqs.schooltrade.api.entity.User;

public class Goods implements Serializable{
	Integer id;
	
	String title;
	String content;
	Date createDate;
	Date editDate;
	User account;
	float originalPrice;
	float curPrice;
	List<Image> listImage;	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public Date getEditDate() {
		return editDate;
	}
	public void setEditDate(Date editDate) {
		this.editDate = editDate;
	}
	public User getAccount() {
		return account;
	}
	public void setAccount(User account) {
		this.account = account;
	}
	public float getOriginalPrice() {
		return originalPrice;
	}
	public void setOriginalPrice(float originalPrice) {
		this.originalPrice = originalPrice;
	}
	public float getCurPrice() {
		return curPrice;
	}
	public void setCurPrice(float curPrice) {
		this.curPrice = curPrice;
	}
	public List<Image> getListImage() {
		return listImage;
	}
	public void setListImage(List<Image> listImage) {
		this.listImage = listImage;
	}	
}
