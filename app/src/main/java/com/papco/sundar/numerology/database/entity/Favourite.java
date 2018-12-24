package com.papco.sundar.numerology.database.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Favourite {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private int priority;

    public Favourite(){

    }

    public Favourite(String name,int priority){
        this.name=name;
        this.priority=priority;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPriority() {
        return priority;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
