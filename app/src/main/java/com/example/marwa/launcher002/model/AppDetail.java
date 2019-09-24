package com.example.marwa.launcher002.model;

import android.graphics.drawable.Drawable;

public class AppDetail {
    int id;
    CharSequence label; //name
    CharSequence name; //package
    Drawable icon;
    int id_taget;
    String packg;
    String nom;
    int state;
    String type;
    String img;
    String date;


    public AppDetail() {
    }

    public AppDetail(CharSequence label, CharSequence name, Drawable icon) {
        this.label = label;
        this.name = name;
        this.icon = icon;
    }

    public AppDetail(int id, int id_taget, String packg, String nom, int state, String type, String img, String date) {
        this.id = id;
        this.id_taget = id_taget;
        this.packg = packg;
        this.nom = nom;
        this.state = state;
        this.type = type;
        this.img = img;
        this.date = date;
    }

    public CharSequence getLabel() {
        return label;
    }

    public void setLabel(CharSequence label) {
        this.label = label;
    }

    public CharSequence getName() {
        return name;
    }

    public void setName(CharSequence name) {
        this.name = name;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public int getId_taget() {
        return id_taget;
    }

    public void setId_taget(int id_taget) {
        this.id_taget = id_taget;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPackg() {
        return packg;
    }

    public void setPackg(String packg) {
        this.packg = packg;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
