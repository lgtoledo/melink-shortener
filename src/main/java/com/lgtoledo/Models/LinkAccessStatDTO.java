package com.lgtoledo.Models;

public class LinkAccessStatDTO {

    private String id;
    private String creationDateUTC;
    private String firstAccessedDateUTC;
    private String lastAccessedDateUTC;
    private long accessCount;

    public LinkAccessStatDTO() {
    }

    public LinkAccessStatDTO(String linkId, String creationDate, String firstAccessedDate,
            String lastAccessedDate, long accessCount) {
        this.id = linkId;
        this.creationDateUTC = creationDate;
        this.firstAccessedDateUTC = firstAccessedDate;
        this.lastAccessedDateUTC = lastAccessedDate;
        this.accessCount = accessCount;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreationDate() {
        return this.creationDateUTC;
    }

    public void setCreationDate(String creationDate) {
        this.creationDateUTC = creationDate;
    }

    public String getFirstAccessedDate() {
        return this.firstAccessedDateUTC;
    }

    public void setFirstAccessedDate(String firstAccessedDate) {
        this.firstAccessedDateUTC = firstAccessedDate;
    }

    public String getLastAccessedDate() {
        return this.lastAccessedDateUTC;
    }

    public void setLastAccessedDate(String lastAccessedDate) {
        this.lastAccessedDateUTC = lastAccessedDate;
    }

    public long getAccessCount() {
        return this.accessCount;
    }

    public void setAccessCount(long accessCount) {
        this.accessCount = accessCount;
    }
}

