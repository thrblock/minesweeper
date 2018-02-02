package com.thrblock.sweeper.entity;

import com.thrblock.cino.gltexture.GLTexture;

public class SweeperTexture {
    /**
     * 数字 0~9
     */
    private GLTexture[] digs;
    /**
     * 未揭示
     */
    private GLTexture unshow;
    /**
     * 地雷 未爆炸
     */
    private GLTexture mine;
    /**
     * 地雷 爆炸
     */
    private GLTexture mineExplode;
    /**
     * 红旗标志
     */
    private GLTexture flag;
    /**
     * 错误的红旗标志
     */
    private GLTexture wrongFlag;
    public GLTexture[] getDigs() {
        return digs;
    }
    public void setDigs(GLTexture[] digs) {
        this.digs = digs;
    }
    public GLTexture getUnshow() {
        return unshow;
    }
    public void setUnshow(GLTexture unshow) {
        this.unshow = unshow;
    }
    public GLTexture getMine() {
        return mine;
    }
    public void setMine(GLTexture mine) {
        this.mine = mine;
    }
    public GLTexture getMineExplode() {
        return mineExplode;
    }
    public void setMineExplode(GLTexture mineExplode) {
        this.mineExplode = mineExplode;
    }
    public GLTexture getFlag() {
        return flag;
    }
    public void setFlag(GLTexture flag) {
        this.flag = flag;
    }
    public GLTexture getWrongFlag() {
        return wrongFlag;
    }
    public void setWrongFlag(GLTexture wrongFlag) {
        this.wrongFlag = wrongFlag;
    }
}
