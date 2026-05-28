package com.example.socialnetwork.model;

public class Group {
    private int id;
    private String name;
    private Integer headmanId;

    public Group() {}

    public Group(String name, Integer headmanId) {
        this.name = name;
        this.headmanId = headmanId;
    }

    public Group(int id, String name, Integer headmanId) {
        this.id = id;
        this.name = name;
        this.headmanId = headmanId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getHeadmanId() { return headmanId; }
    public void setHeadmanId(Integer headmanId) { this.headmanId = headmanId; }

    @Override
    public String toString() {
        return name;
    }
}