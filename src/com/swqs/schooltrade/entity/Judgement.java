package com.swqs.schooltrade.entity;

import java.io.Serializable;
import java.sql.Date;

public class Judgement implements Serializable {
	private Date createDate;
	private Date editDate;
	Integer id;
	Goods goods;
	User judgeAcc;
	String text;
	
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
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Goods getGoods() {
		return goods;
	}
	public void setGoods(Goods goods) {
		this.goods = goods;
	}
	public User getJudgeAcc() {
		return judgeAcc;
	}
	public void setJudgeAcc(User judgeAcc) {
		this.judgeAcc = judgeAcc;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}

	
}
