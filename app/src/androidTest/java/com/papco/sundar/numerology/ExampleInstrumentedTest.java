package com.papco.sundar.numerology;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.db.framework.FrameworkSQLiteOpenHelperFactory;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.arch.persistence.room.testing.MigrationTestHelper;
import android.util.Log;

import com.papco.sundar.numerology.database.MasterDatabase;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    private static final Migration MIGRATION_1_2=new Migration(1,2) {

        private String[] val = {"1","2","3","4","5","8","3","5","1","1","2","3","4","5","7","8","1","2","3","4","6","6","6","5","1","7"};

        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

            database.execSQL("ALTER TABLE alphavalues RENAME TO alpha_backup");
            database.execSQL("CREATE TABLE IF NOT EXISTS `alphavalues` (`_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `val` INTEGER NOT NULL, `defaultValue` INTEGER NOT NULL)");
            database.execSQL("INSERT INTO alphavalues(val,defaultValue) SELECT val,3 from alpha_backup");
            String temp;
            for(int i=0;i<26;++i) {
                temp=val[i];
                database.execSQL("UPDATE alphavalues SET defaultValue=" + temp + " WHERE _id=" + Integer.toString(i + 1));
            }
            database.execSQL("DROP TABLE alpha_backup");

            database.execSQL("ALTER TABLE favourites RENAME TO fav_backup");
            database.execSQL("CREATE TABLE IF NOT EXISTS `favourites` (`_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT, `priority` INTEGER NOT NULL)");
            database.execSQL("INSERT INTO favourites(name,priority) select name,1 from fav_backup");
            database.execSQL("DROP TABLE fav_backup");
            database.execSQL("UPDATE favourites set priority=_id");


        }
    };
    @Rule
    public MigrationTestHelper migrationHelper;
    @Test
    public void migrate1To2() throws IOException {

        migrationHelper = new MigrationTestHelper(InstrumentationRegistry.getInstrumentation(),
                MasterDatabase.class.getCanonicalName(),
                new FrameworkSQLiteOpenHelperFactory());
        // Use TestDbOpenHelper
        // to create the database with the initial version 1
        NumerologyDataHandler sqliteDbHelper = new NumerologyDataHandler(
                InstrumentationRegistry.getTargetContext());

        SQLiteDatabase db = sqliteDbHelper.getWritableDatabase();
        //db.execSQL(CREATE_TABLE");
        db.close();
        // Re-open the database with version 2 (which Room generated)
        // and provide MIGRATION_1_2 as the migration process.
        migrationHelper.runMigrationsAndValidate(
                "numerologyDB", 2, true,
                MIGRATION_1_2);                                                       }
}
