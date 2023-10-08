package com.lgtoledo.Models;

public class Link {
    private String id;
    private String longLink;

    public Link() {
    }

    public Link(String id, String longLink) {
        this.id = id;
        this.longLink = longLink;
    }

    public String getId() {
        return id;
    }

    public void setId(String shortLinkId) {
        this.id = shortLinkId;
    }

    public String getlongLink() {
        return longLink;
    }

    public void setlongLink(String longLink) {
        this.longLink = longLink;
    }

    @Override
    public String toString() {
        return "Link{" +
                "shortLinkId='" + id + '\'' +
                ", longLink='" + longLink + '\'' +
                '}';
    }

    public String toJson() {
        return String.format("{\"id\": \"%s\", \"longLink\": \"%s\"}", id, longLink);
    }

    public static Link fromJson(String json) {
        String[] parts = json.split(",");
        String shortLinkId = parts[0].split(":")[1].replace("\"", "").trim();
        String longLink = parts[1].split(":")[1].replace("\"", "").trim();
        return new Link(shortLinkId, longLink);
    }

}
