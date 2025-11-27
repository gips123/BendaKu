package com.example.bendaku.model;

import com.google.gson.annotations.SerializedName;

public class StrapiResponse<T> {
    @SerializedName("data")
    private T data;

    @SerializedName("meta")
    private Meta meta;

    @SerializedName("error")
    private StrapiError error;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public StrapiError getError() {
        return error;
    }

    public void setError(StrapiError error) {
        this.error = error;
    }

    public boolean isSuccess() {
        return error == null;
    }

    public static class Meta {
    }

    public static class StrapiError {
        @SerializedName("status")
        private int status;

        @SerializedName("message")
        private String message;

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}

