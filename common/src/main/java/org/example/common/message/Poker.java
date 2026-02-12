package org.example.common.message;

public class Poker extends BaseBo {

    /**
     * 花色
     * {@link org.example.common.enume.PokerSuitsEnum}
     */
    private int suits;

    private int no;

    public int getSuits() {
        return suits;
    }

    public void setSuits(int suits) {
        this.suits = suits;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }
}
