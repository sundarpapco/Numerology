package com.papco.sundar.numerology.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.papco.sundar.numerology.database.entity.AlphabatValue;

import java.util.List;

@Dao
public interface AlphabatValueDao {

    @Insert
    public  List<Long> addAllAlphabats(List<AlphabatValue> values);

    @Query("select * from alphavalues")
    public LiveData<List<AlphabatValue>> getAlphabatValues();

    @Query("select * from alphavalues")
    public List<AlphabatValue> getAlphabatsNonLive();

    @Update
    public void updateAlphabatValue(AlphabatValue updatedValue);

    @Update
    public void resetAlphabatValues(List<AlphabatValue> updatedList);


}
