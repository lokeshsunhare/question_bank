package com.igkvmis.questionbank.model;

import java.io.Serializable;

public class FileDownload implements Serializable {
    private String fileURL;

    public FileDownload(String fileURL) {
        this.fileURL = fileURL;
    }

    public String getFileURL() {
        return fileURL;
    }

    public void setFileURL(String fileURL) {
        this.fileURL = fileURL;
    }
}
