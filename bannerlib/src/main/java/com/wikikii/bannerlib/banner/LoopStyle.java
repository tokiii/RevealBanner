package com.wikikii.bannerlib.banner;

/**
 * Loop style
 * 默认empty
 * 深度depth
 * 缩小zoo
 */
public enum LoopStyle {
    Empty(-1),
    Depth(1),
    Zoom(2);
    private int value;

    LoopStyle(int idx) {
        this.value = idx;
    }

    public int getValue() {
        return value;
    }

}
