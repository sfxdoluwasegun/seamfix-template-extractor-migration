/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.plugins.template.extractor;

import com.neurotec.biometrics.NFinger;
import com.neurotec.biometrics.NSubject;
import com.neurotec.biometrics.NTemplateSize;
import com.neurotec.biometrics.client.NBiometricClient;
import com.neurotec.images.NImage;
import com.neurotec.images.NImageFormat;
import com.neurotec.images.WSQInfo;
import com.neurotec.io.NBuffer;
import com.seamfix.util.CompressorWriter;
import com.sf.plugins.template.extractor.enums.BiometricType;
import com.sf.plugins.template.extractor.enums.ResponseCodeEnum;
import com.sf.plugins.template.extractor.pojos.ExtractResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.jnbis.api.Jnbis;

/**
 *
 * @author Uchechukwu Onuoha
 */
public class Extractor implements IExtractor {

    private static final Logger log = Logger.getLogger(Extractor.class.getName());

    @Override
    public ExtractResponse extract(String biometricType, String imageExtension, String base64ImageString, NBiometricClient client) {
        //validate inputs
        ExtractResponse response = new ExtractResponse(ResponseCodeEnum.ERROR);
        if (base64ImageString == null || biometricType == null) {
            log.log(Level.SEVERE, ResponseCodeEnum.INVALID_INPUT.getDescription() + " : Invalid image or Biomtric Type ");
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

    public String toWsq(String base64ImageString) {
        String wsqBase64String = null;
        Path path = null;
        try {
            String bmpStr = base64ImageString.replaceAll("\\s+", "");
            byte[] bmpBytes = Base64.getDecoder().decode(bmpStr);
            NImage image = null;
            WSQInfo info = null;
            image = NImage.fromMemory(new NBuffer(bmpBytes));
            info = (WSQInfo) NImageFormat.getWSQ().createInfo(image);
            float bitrate = WSQInfo.DEFAULT_BIT_RATE;
            info.setBitRate(bitrate);
            path = Files.createTempFile("fingerprint", ".wsq");
            String pat = path.toString();
            image.save(pat, info);

            byte[] wsqByte = Files.readAllBytes(path);
            wsqBase64String = Base64.getEncoder().encodeToString(wsqByte);
            if (wsqBase64String == null || wsqBase64String.trim().isEmpty()) {
                return null;
            }

        } catch (Exception ex) {
            log.log(Level.SEVERE, ex.getMessage());
            return null;
        } finally {
            try {
                Files.deleteIfExists(path);
            } catch (IOException ex) {
                log.log(Level.SEVERE, ex.getMessage());
            }

        }

        return wsqBase64String;
    }

    public String wsqToBmp(String base64WsqString) throws IOException {
        String base64BmpString = null;
        byte[] input = Jnbis.wsq()
                .decode(Base64.getDecoder().decode(base64WsqString)).toJpg().asByteArray();
        input = CompressorWriter.compress(input, 0.7f, 5, "jpeg");
        BufferedImage bi = ImageIO.read(new ByteArrayInputStream(input));
        Path path = Files.createTempFile("fingerprint", ".bmp");
        ImageIO.write(bi, "bmp", path.toFile());
        byte[] bmpByte = Files.readAllBytes(path);
        base64BmpString = Base64.getEncoder().encodeToString(bmpByte);
        try {
            Files.deleteIfExists(path);
        } catch (IOException ex) {
            log.log(Level.SEVERE, ex.getMessage());
        }
        return base64BmpString;

    }

    public byte[] wsqToNTemplate(String base64WsqImageString, NBiometricClient client) {
        byte[] templateByte = null;
        String bmpStr = base64WsqImageString.replaceAll("\\s+", "");
        byte[] bmpBytes = Base64.getDecoder().decode(bmpStr);
        NBuffer buffer = null;
        buffer = new NBuffer(bmpBytes);
        final NSubject nSubject = new NSubject();
        NFinger nFinger = new NFinger();
        nFinger.setSampleBuffer(buffer);
        nSubject.getFingers().add(nFinger);
        client.setFingersTemplateSize(NTemplateSize.LARGE);
        client.createTemplate(nSubject);
        templateByte = nSubject.getTemplateBuffer().toByteArray();
        if (templateByte.length <= 10) {
            return null;
        }
        return templateByte;
    }

}
