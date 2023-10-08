package com.lgtoledo.Models;

public class LinkModel {
    private String id;
    private String long_link;

    public LinkModel() {
    }

    public LinkModel(String id, String long_link) {
        this.id = id;
        this.long_link = long_link;
    }

    public String getId() {
        return id;
    }

    public void setId(String shortLinkId) {
        this.id = shortLinkId;
    }

    public String getLong_link() {
        return long_link;
    }

    public void setLong_link(String longLink) {
        this.long_link = longLink;
    }

    @Override
    public String toString() {
        return "Link{" +
                "shortLinkId='" + id + '\'' +
                ", longLink='" + long_link + '\'' +
                '}';
    }

    public String toJson() {
        return String.format("{\"id\": \"%s\", \"long_link\": \"%s\"}", id, long_link);
    }

    public static LinkModel fromJson(String json) {
        String[] parts = json.split(",");
        String shortLinkId = parts[0].split(":")[1].replace("\"", "").trim();
        String longLink = parts[1].split(":")[1].replace("\"", "").trim();
        return new LinkModel(shortLinkId, longLink);
    }

}
