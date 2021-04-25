package com.movie.app.Model;

public class MediaFileListModel {
    private String fileName, filePath, fileSize, fileCreatedTime,fileDate;

    public MediaFileListModel() {
    }

    public String getFileDate() {
        return fileDate;
    }

    public void setFileDate(String fileDate) {
        this.fileDate = fileDate;
    }

    public String getFileCreatedTime() {
        return fileCreatedTime;
    }

    public void setFileCreatedTime(String fileCreatedTime) {
        this.fileCreatedTime = fileCreatedTime;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}