package com.sf.plugins.template.extractor.enums;

/**
 *
 * @author Uche
 *
 */
public enum BiometricType {
    FINGER("FINGER"),
    FACE("FACE"),
    IRIS("IRIS"),
    PALM("PALM"),
    VOICE("VOICE");

    private String type;

    BiometricType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    public static BiometricType from(String type) {
        if (type != null) {
            for (BiometricType b : BiometricType.values()) {
                if (type.equalsIgnoreCase(b.getType())) {
                    return b;
                }
            }
        }
        return null;
    }
}
