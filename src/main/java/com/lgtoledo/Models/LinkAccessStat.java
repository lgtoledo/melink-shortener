package com.lgtoledo.Models;

import java.time.LocalDateTime;

public class LinkAccessStat {

    private String id;
    private LocalDateTime creationDate;
    private LocalDateTime firstAccessedDate;
    private LocalDateTime lastAccessedDate;
    private long accessCount;

    public LinkAccessStat() {
    }

    public LinkAccessStat(String linkId, LocalDateTime creationDate, LocalDateTime firstAccessedDate,
            LocalDateTime lastAccessedDate, long accessCount) {
        this.id = linkId;
        this.creationDate = creationDate;
        this.firstAccessedDate = firstAccessedDate;
        this.lastAccessedDate = lastAccessedDate;
        this.accessCount = accessCount;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getCreationDate() {
        return this.creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getFirstAccessedDate() {
        return this.firstAccessedDate;
    }

    public void setFirstAccessedDate(LocalDateTime firstAccessedDate) {
        this.firstAccessedDate = firstAccessedDate;
    }

    public LocalDateTime getLastAccessedDate() {
        return this.lastAccessedDate;
    }

    public void setLastAccessedDate(LocalDateTime lastAccessedDate) {
        this.lastAccessedDate = lastAccessedDate;
    }

    public long getAccessCount() {
        return this.accessCount;
    }

    public void setAccessCount(long accessCount) {
        this.accessCount = accessCount;
    }

    @Override
    public String toString() {
        return "{" + " linkId='" + getId() + "'" + ", creationDate='" + getCreationDate() + "'"
                + ", firstAccessedDate='" + getFirstAccessedDate() + "'" + ", lastAccessedDate='" + getLastAccessedDate()
                + "'" + ", accessCount='" + getAccessCount() + "'" + "}";
    }

    public String toJson() {
        return String.format(
                "{\"id\": \"%s\", \"creationDate\": \"%s\", \"firstAccessedDate\": \"%s\", \"lastAccessedDate\": \"%s\", \"accessCount\": \"%s\"}",
                id, creationDate, firstAccessedDate, lastAccessedDate, accessCount);
    }
}

