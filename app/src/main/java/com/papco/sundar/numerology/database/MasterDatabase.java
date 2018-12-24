package com.papco.sundar.numerology.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.papco.sundar.numerology.database.dao.AlphabatValueDao;
import com.papco.sundar.numerology.database.dao.FavouriteDao;
import com.papco.sundar.numerology.database.entity.AlphabatValue;
import com.papco.sundar.numerology.database.entity.Favourite;

@Database(entities = {AlphabatValue.class,Favourite.class},version = 1)
public abstract class MasterDatabase extends RoomDatabase {

    static MasterDatabase db;
    public static MasterDatabase getInstance(Context context){

        if(db==null)
            db= Room.databaseBuilder(context,MasterDatabase.class,"master_database").build();

        return db;
    }

    public abstract AlphabatValueDao getAlphabatValueDao();
    public abstract FavouriteDao getFavouriteDao();
}
