package com.example.mrt.ship.models;

import java.io.Serializable;

/**
 * Created by mrt on 16/10/2016.
 */

public class Note implements Serializable{
    private String header;
    private String content;
    private String date;
    private int id;

    public Note(){}

    public Note(String header, String content){
        this.header = header;
        this.content = content;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
