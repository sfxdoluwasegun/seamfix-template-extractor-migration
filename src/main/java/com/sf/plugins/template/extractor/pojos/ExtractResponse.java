/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.plugins.template.extractor.pojos;

import com.sf.plugins.template.extractor.enums.ResponseCodeEnum;
import java.io.Serializable;

/**
 *
 * @author Uchechukwu Onuoha <yoursuche@gmail.com>
 */
public class ExtractResponse implements Serializable {

    private byte[] template;
    private Integer code;
    private String description;

    public ExtractResponse() {
    }

    public ExtractResponse(ResponseCodeEnum responseCodeEnum) {
        this.code = responseCodeEnum.getCode();
        this.description = responseCodeEnum.getDescription();
    }

    public byte[] getTemplate() {
        return template;
    }

    public void setTemplate(byte[] template) {
        this.template = template;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
