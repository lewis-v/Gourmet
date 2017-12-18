package com.yw.gourmet.ui.share;

/**
 * Created by Lewis-v on 2017/12/18.
 */

/**
 * 字体格式状态存储
 */
public class ToolType {
    public static final int LEFT = 0;//左对齐
    public static final int CENTER = 1;//居中
    public static final int RIGHT = 2;//右对齐

    private boolean isBold = false;
    private boolean isItalic = false;
    private int textType = 0;

    public ToolType(){}

    public boolean isBold() {
        return isBold;
    }

    public ToolType setBold(boolean bold) {
        isBold = bold;
        return this;
    }

    public boolean isItalic() {
        return isItalic;
    }

    public ToolType setItalic(boolean italic) {
        isItalic = italic;
        return this;
    }

    public int getTextType() {
        return textType;
    }

    public ToolType setTextType(int textType) {
        this.textType = textType;
        return this;
    }

    @Override
    public String toString() {
        return "ToolType{" +
                "isBold=" + isBold +
                ", isItalic=" + isItalic +
                ", textType=" + textType +
                '}';
    }
}
