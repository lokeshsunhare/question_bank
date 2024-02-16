package com.igkvmis.questionbank.activities.BlurImageAndRelatedFile;

public class Images {
    private String id;
    private String name;
    private String IMG_URL;

    public Images(String id, String name, String IMG_URL) {
        this.id = id;
        this.name = name;
        this.IMG_URL = IMG_URL;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIMG_URL() {
        return IMG_URL;
    }

    public void setIMG_URL(String IMG_URL) {
        this.IMG_URL = IMG_URL;
    }
}
