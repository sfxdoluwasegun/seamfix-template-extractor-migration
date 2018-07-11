/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.plugins.template.extractor;

import com.neurotec.biometrics.client.NBiometricClient;
import com.sf.plugins.template.extractor.pojos.ExtractResponse;

/**
 *
 * @author Uchechukwu Onuoha <yoursuche@gmail.com>
 */
public interface IExtractor {

    public ExtractResponse extract(String biometricType, String imageExtension, String base64ImageString, NBiometricClient client);

}
