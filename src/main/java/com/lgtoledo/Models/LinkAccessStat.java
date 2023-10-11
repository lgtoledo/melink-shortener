package com.lgtoledo.Models;

import java.time.LocalDateTime;

public class LinkAccessStat {

    private String id;
    private LocalDateTime creationDateUTC;
    private LocalDateTime firstAccessedDateUTC;
    private LocalDateTime lastAccessedDateUTC;
    private long accessCount;

    public LinkAccessStat() {
    }

    public LinkAccessStat(String linkId, LocalDateTime creationDate, LocalDateTime firstAccessedDate,
            LocalDateTime lastAccessedDate, long accessCount) {
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

    public LocalDateTime getCreationDateUTC() {
        return this.creationDateUTC;
    }

    public void setCreationDateUTC(LocalDateTime creationDate) {
        this.creationDateUTC = creationDate;
    }

    public LocalDateTime getFirstAccessedDateUTC() {
        return this.firstAccessedDateUTC;
    }

    public void setFirstAccessedDateUTC(LocalDateTime firstAccessedDate) {
        this.firstAccessedDateUTC = firstAccessedDate;
    }

    public LocalDateTime getLastAccessedDateUTC() {
        return this.lastAccessedDateUTC;
    }

    public void setLastAccessedDateUTC(LocalDateTime lastAccessedDate) {
        this.lastAccessedDateUTC = lastAccessedDate;
    }

    public long getAccessCount() {
        return this.accessCount;
    }

    public void setAccessCount(long accessCount) {
        this.accessCount = accessCount;
    }

    @Override
    public String toString() {
        return "{" + " linkId='" + getId() + "'" + ", creationDate='" + getCreationDateUTC() + "'"
                + ", firstAccessedDate='" + getFirstAccessedDateUTC() + "'" + ", lastAccessedDate='" + getLastAccessedDateUTC()
                + "'" + ", accessCount='" + getAccessCount() + "'" + "}";
    }

    public String toJson() {
        return String.format(
                "{\"id\": \"%s\", \"creationDate\": \"%s\", \"firstAccessedDate\": \"%s\", \"lastAccessedDate\": \"%s\", \"accessCount\": \"%s\"}",
                id, creationDateUTC, firstAccessedDateUTC, lastAccessedDateUTC, accessCount);
    }

    public LinkAccessStatDTO toDTO() {
        String creationDateString = (creationDateUTC != null) ? creationDateUTC.toString() : "";
        String firstAccessedDateString = (firstAccessedDateUTC != null) ? firstAccessedDateUTC.toString() : "";
        String lastAccessedDateString = (lastAccessedDateUTC != null) ? lastAccessedDateUTC.toString() : "";
    
        return new LinkAccessStatDTO(id, creationDateString, firstAccessedDateString, lastAccessedDateString, accessCount);
    }
    

    
}

