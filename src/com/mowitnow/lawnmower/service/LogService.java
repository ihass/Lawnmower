/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mowitnow.lawnmower.service;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author ihassiko-a
 */
public final class LogService {

    public static final Integer ERR = 0;

    private static volatile LogService instance = null;
    private Integer code;
    private StringProperty msg = new SimpleStringProperty();

    private LogService() {
        super();
    }

    public final static LogService getInstance() {
        if (LogService.instance == null) {
            synchronized (LogService.class) {
                if (LogService.instance == null) {
                    LogService.instance = new LogService();
                }
            }
        }
        return LogService.instance;
    }

    /**
     * @return the code
     */
    public Integer getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(final Integer code) {
        this.code = code;
    }

    /**
     * @return the msg
     */
    public StringProperty msg() {
        return this.msg;
    }

    /**
     * @return the msg value
     */
    public final String getMsg() {
        return this.msg.get();
    }

    /**
     * @param msg the msg to set
     */
    public final void setMsg(final String msg) {
        this.msg.set(msg);
    }

    public void log(final String msg) {
        if (StringUtils.isNotBlank(msg)) {
            this.setCode(ERR);
            this.setMsg(msg);
        } else {
            this.setCode(null);
            this.setMsg(null);
        }
    }

    public void log(final int code, final String msg) {
        this.setCode(code);
        this.setMsg(msg);
    }
}
