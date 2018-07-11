/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.plugins.template.extractor.enums;

/**
 *
 * @author Uche
 *
 */
public enum ResponseCodeEnum {

    SUCCESS(0, "Success"),
    ERROR(-1, "Error"),
    INVALID_INPUT(-1, "Error");

    private ResponseCodeEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    private int code;
    private String description;

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

}
