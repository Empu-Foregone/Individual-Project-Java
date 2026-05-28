package com.example.socialnetwork.model;

public class Friendship {
    private int id;
    private int student1Id;
    private int student2Id;

    public Friendship() {}

    public Friendship(int student1Id, int student2Id) {
        this.student1Id = Math.min(student1Id, student2Id);
        this.student2Id = Math.max(student1Id, student2Id);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getStudent1Id() { return student1Id; }
    public void setStudent1Id(int student1Id) { this.student1Id = student1Id; }
    public int getStudent2Id() { return student2Id; }
    public void setStudent2Id(int student2Id) { this.student2Id = student2Id; }
}