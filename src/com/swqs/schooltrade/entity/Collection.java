package com.swqs.schooltrade.entity;

import java.util.Date;

public class Collection {
	public class ID {
		User user;
		Goods goods;

		public User getUser() {
			return user;
		}

		public void setUser(User user) {
			this.user = user;
		}

		public Goods getGoods() {
			return goods;
		}

		public void setGoods(Goods goods) {
			this.goods = goods;
		}
	}

	ID id;
	Date createDate;

	public void setId(ID id) {
		this.id = id;
	}
	

	public ID getId() {
		return id;
	}


	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
}
