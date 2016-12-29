package com.swqs.schooltrade.util;

import java.io.IOException;
import java.io.Serializable;

import android.graphics.Bitmap;

public class ImageItem implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public String imageId;
	public String thumbnailPath;
	public String imagePath;
	private Bitmap bitmap;
	public boolean isSelected = false;

	public String getImageId() {
		return imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}

	public String getThumbnailPath() {
		return thumbnailPath;
	}

	public void setThumbnailPath(String thumbnailPath) {
		this.thumbnailPath = thumbnailPath;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public Bitmap getBitmap() {
		if (bitmap == null) {
			try {
				bitmap = FileUtils.revitionImageSize(imagePath);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof ImageItem)) {
			return false;
		}
		ImageItem other = (ImageItem) o;
		if(other==this){
			return true;
		}
		if (this.imagePath!=null&&this.imagePath.equals(other.imagePath)) {
			return true;
		}
		if(this.thumbnailPath!=null&&this.thumbnailPath.equals(other.thumbnailPath)){
			return true;
		}
		return super.equals(o);
	}

}
