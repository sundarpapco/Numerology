package com.papco.sundar.numerology.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;

import com.papco.sundar.numerology.database.dao.AlphabatValueDao;
import com.papco.sundar.numerology.database.dao.FavouriteDao;
import com.papco.sundar.numerology.database.entity.AlphabatValue;
import com.papco.sundar.numerology.database.entity.Favourite;

@Database(entities = {AlphabatValue.class,Favourite.class},version = 2)
public abstract class MasterDatabase extends RoomDatabase {

    static MasterDatabase db;
    private static final Migration MIGRATION_1_2=new Migration(1,2) {

        //prev V1 database dont have default values in the table. We need this to write to new table in V2
        private String[] val = {"1","2","3","4","5","8","3","5","1","1","2","3","4","5","7","8","1","2","3","4","6","6","6","5","1","7"};

        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

            //migrating the alphabat values table
            database.execSQL("ALTER TABLE alphavalues RENAME TO alpha_backup");
            database.execSQL("CREATE TABLE IF NOT EXISTS `alphavalues` (`_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `val` INTEGER NOT NULL, `defaultValue` INTEGER NOT NULL)");
            database.execSQL("INSERT INTO alphavalues(val,defaultValue) SELECT val,3 from alpha_backup");
            String temp;
            for(int i=0;i<26;++i) {
                temp=val[i];
                database.execSQL("UPDATE alphavalues SET defaultValue=" + temp + " WHERE _id=" + Integer.toString(i + 1));
            }
            database.execSQL("DROP TABLE alpha_backup");

            //migrating the favourites table
            database.execSQL("ALTER TABLE favourites RENAME TO fav_backup");
            database.execSQL("CREATE TABLE IF NOT EXISTS `favourites` (`_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT, `priority` INTEGER NOT NULL)");
            database.execSQL("INSERT INTO favourites(name,priority) select name,1 from fav_backup");
            database.execSQL("DROP TABLE fav_backup");
            database.execSQL("UPDATE favourites set priority=_id");
        }
    };


    public static MasterDatabase getInstance(Context context){

        if(db==null)
            db= Room.databaseBuilder(context,MasterDatabase.class,"numerologyDB")
                    .addMigrations(MIGRATION_1_2).build();

        return db;
    }

    public abstract AlphabatValueDao getAlphabatValueDao();
    public abstract FavouriteDao getFavouriteDao();
}
