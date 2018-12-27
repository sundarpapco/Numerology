package com.papco.sundar.numerology.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.papco.sundar.numerology.database.entity.Favourite;

import java.util.List;

@Dao
public interface FavouriteDao {

    @Insert
    public long addFavourite(Favourite newvalue);

    @Query("select * from favourites order by priority")
    public LiveData<List<Favourite>> getAllFavourites();

    @Update
    public void updateFavourites(List<Favourite> updatedValues);

    @Delete
    public void deleteFavourite(Favourite favourite);

    @Query("delete  from favourites")
    public void deleteAllFavourites();
}
