package com.example.marwa.launcher002.model;

public class SmsData {

    private String date;
    private String type;
    private String number;
    private String body;

    public SmsData(String date, String type, String number, String body) {
        this.date = date;
        this.type = type;
        this.number = number;
        this.body = body;
    }

    public SmsData() {

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
