/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mowitnow.lawnmower.model;

import java.util.List;

/**
 * La classe Lawn est un Singleton en aggrégation avec la classe Mower
 *
 * @author ihassiko-a
 */
public final class Lawn {

    private static volatile Lawn instance = null;
    private int width;
    private int height;
    private List<Mower> mowerList;

    private Lawn(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public final static Lawn getInstance(int width, int height) {
        if (Lawn.instance == null) {
            synchronized (Lawn.class) {
                if (Lawn.instance == null) {
                    Lawn.instance = new Lawn(width, height);
                }
            }
        } else {
            Lawn.instance.setWidth(width);
            Lawn.instance.setHeight(height);
        }
        return Lawn.instance;
    }

    /**
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * @return the height
     */
    public int getHeight() {
        return height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * @return the mowerList
     */
    public List<Mower> getMowerList() {
        return mowerList;
    }

    /**
     * @param mowerList the mowerList to set
     */
    public void setMowerList(List<Mower> mowerList) {
        this.mowerList = mowerList;
    }

}
