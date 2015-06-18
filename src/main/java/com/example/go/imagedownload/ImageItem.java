package com.example.go.imagedownload;

import android.graphics.Bitmap;

//이미지 정보 클래스
public class ImageItem {
    private Bitmap image; // 이미지 데이터
    private String title; // 이미지 주소

    public ImageItem(Bitmap image, String title) {
        super();
        this.image = image;
        this.title = title;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
