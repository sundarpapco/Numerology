package com.papco.sundar.numerology.database.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.security.AlgorithmParameterGenerator;

@Entity( tableName = "alphavalues")
public class AlphabatValue {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    private int id;
    @ColumnInfo(name = "val")
    private int currentValue;
    private int defaultValue;

    public AlphabatValue(){
        currentValue=-1;
        defaultValue=-1;
    }

    public AlphabatValue(int value){
        this.currentValue=value;
        this.defaultValue=value;
    }

    public int getId() {
        return id;
    }

    public int getCurrentValue() {
        return currentValue;
    }

    public int getDefaultValue() {
        return defaultValue;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCurrentValue(int currentValue) {
        this.currentValue = currentValue;
    }

    public void setDefaultValue(int defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Ignore
    public String getCurrentValueString(){
        return Integer.toString(currentValue);
    }
}
