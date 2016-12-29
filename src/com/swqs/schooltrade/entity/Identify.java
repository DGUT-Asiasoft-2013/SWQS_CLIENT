package com.swqs.schooltrade.entity;

import java.io.Serializable;
import java.util.Date;

public class Identify implements Serializable {

	Integer id;
	Goods goods;
	User buyer;
	User seller;
	short tradeState;
	private Date createDate;
	private Date editDate;
	
	

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

	public User getBuyer() {
		return buyer;
	}

	public void setBuyer(User buyer) {
		this.buyer = buyer;
	}

	public User getSeller() {
		return seller;
	}

	public void setSeller(User seller) {
		this.seller = seller;
	}

	public short getTradeState() {
		return tradeState;
	}

	public void setTradeState(short tradeState) {
		this.tradeState = tradeState;
	}

}
