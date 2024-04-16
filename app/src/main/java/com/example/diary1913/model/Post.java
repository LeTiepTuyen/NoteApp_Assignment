package com.example.diary1913.model;

public class Post {
    String id;
    private String title;
    private String content;


    public Post() {
    }

    public Post(String id, String title, String content) {
        this.title = title;
        this.content = content;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }











}
