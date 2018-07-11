/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.plugins.template.extractor;

import com.neurotec.biometrics.NFinger;
import com.neurotec.biometrics.NSubject;
import com.neurotec.biometrics.client.NBiometricClient;
import com.neurotec.images.NImage;
import com.neurotec.images.NImageFormat;
import com.neurotec.images.WSQInfo;
import com.neurotec.io.NBuffer;
import com.sf.plugins.template.extractor.enums.BiometricType;
import com.sf.plugins.template.extractor.enums.ResponseCodeEnum;
import com.sf.plugins.template.extractor.pojos.ExtractResponse;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Uchechukwu Onuoha <yoursuche@gmail.com>
 */
public class Extractor implements IExtractor {

    private static final Logger log = Logger.getLogger(Extractor.class.getName());

    @Override
    public ExtractResponse extract(String biometricType, String imageExtension, String base64ImageString, NBiometricClient client) {
        //validate inputs
        ExtractResponse response = new ExtractResponse(ResponseCodeEnum.ERROR);
        if (base64ImageString == null || biometricType == null) {
            log.log(Level.SEVERE, ResponseCodeEnum.INVALID_INPUT.getDescription()+" : Invalid image or Biomtric Type ");
            return new ExtractResponse(ResponseCodeEnum.INVALID_INPUT);
        }
        if (BiometricType.from(biometricType) == BiometricType.FINGER) {
             log.info("Beginning Finger Image Template Extraction");
            byte[] templateByte = extractFingerTemplate(biometricType, null, base64ImageString, client);
            if (templateByte != null) {
                response = new ExtractResponse(ResponseCodeEnum.SUCCESS);
                response.setTemplate(templateByte);
            }
        } else {
            return new ExtractResponse(ResponseCodeEnum.INVALID_INPUT);
        }
        return response;
    }

    public byte[] extractFingerTemplate(String biometricType, String imageExtension, String base64ImageString, NBiometricClient client) {
        byte[] templateByte = null;
        String bmpStr = base64ImageString.replaceAll("\\s+", "");
        byte[] bmpBytes = Base64.getDecoder().decode(bmpStr);
        NImage image = null;
        WSQInfo info = null;
        image = NImage.fromMemory(new NBuffer(bmpBytes));
        info = (WSQInfo) NImageFormat.getWSQ().createInfo(image);
        float bitrate = WSQInfo.DEFAULT_BIT_RATE;
        info.setBitRate(bitrate);
        // Save image in WSQ format and bitrate to file
        image.save(info);
        final NSubject nSubject = new NSubject();
        NFinger nFinger = new NFinger();
        image.setHorzResolution(500);
        image.setVertResolution(500);
        nFinger.setImage(image);
        nSubject.getFingers().add(nFinger);
        client.createTemplate(nSubject);
        templateByte = nSubject.getTemplateBuffer().toByteArray();
        if (templateByte.length <= 10) {
            return null;
        }
        return templateByte;
    }
}
