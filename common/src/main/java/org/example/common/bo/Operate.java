package org.example.common.bo;

import java.util.List;

public class Operate extends BaseBo {

    private int operate;

    private int sore;

    private List<Integer> allowOperates;

    public int getOperate() {
        return operate;
    }

    public void setOperate(int operate) {
        this.operate = operate;
    }

    public int getSore() {
        return sore;
    }

    public void setSore(int sore) {
        this.sore = sore;
    }

    public List<Integer> getAllowOperates() {
        return allowOperates;
    }

    public void setAllowOperates(List<Integer> allowOperates) {
        this.allowOperates = allowOperates;
    }
}
