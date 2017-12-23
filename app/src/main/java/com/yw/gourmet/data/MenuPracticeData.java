package com.yw.gourmet.data;

/**
 * Created by Lewis-v on 2017/12/22.
 */

public class MenuPracticeData<T> {
    private String content;//操作内容
    private T img_practiceData;//操作对应的图片

    public MenuPracticeData() {
    }

    public String getContent() {
        return content;
    }

    public MenuPracticeData setContent(String content) {
        this.content = content;
        return this;
    }

    public T getImg_practiceData() {
        return img_practiceData;
    }

    public MenuPracticeData setImg_practiceData(T img_practiceData) {
        this.img_practiceData = img_practiceData;
        return this;
    }

    @Override
    public String toString() {
        return "MenuPracticeData{" +
                "content='" + content + '\'' +
                ", img_practiceData=" + img_practiceData +
                '}';
    }
}
