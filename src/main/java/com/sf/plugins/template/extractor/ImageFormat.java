package com.sf.plugins.template.extractor;

/**
 *
 * @author Uche
 *
 */
public enum ImageFormat {
    BMP("BMP", ".bmp"),
    WSQ("WSQ", ".wsq"),
    JPG("JPG", ".jpeg"),
    PNG("PNG", ".png"),
    RAW("RAW", ".raw");

    private String ext; //file extension
    private String type;

    ImageFormat(String type, String ext) {
        this.ext = ext;
        this.type = type;
    }

    public static ImageFormat from(String type) {
        if (type != null) {
            for (ImageFormat b : ImageFormat.values()) {
                if (type.equalsIgnoreCase(b.getExt())) {
                    return b;
                }
            }
        }
        return null;
    }

    public String getExt() {
        return ext;
    }

    public String getType() {
        return type;
    }

}
