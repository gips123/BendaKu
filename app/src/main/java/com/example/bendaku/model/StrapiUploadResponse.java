package com.example.bendaku.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StrapiUploadResponse {
    @SerializedName("id")
    private Integer id;

    @SerializedName("name")
    private String name;

    @SerializedName("url")
    private String url;

    @SerializedName("mime")
    private String mime;

    @SerializedName("size")
    private Double size;

    private List<StrapiUploadResponse> uploads;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

    public Double getSize() {
        return size;
    }

    public void setSize(Double size) {
        this.size = size;
    }

    public List<StrapiUploadResponse> getUploads() {
        return uploads;
    }

    public void setUploads(List<StrapiUploadResponse> uploads) {
        this.uploads = uploads;
    }
}

