package com.papco.sundar.numerology;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.papco.sundar.numerology.database.MasterDatabase;
import com.papco.sundar.numerology.database.entity.AlphabatValue;
import com.papco.sundar.numerology.database.entity.Favourite;

import java.util.ArrayList;
import java.util.List;

public class MainActivityVM extends AndroidViewModel {

    private final String TAG="SUNDAR";
    
    private LiveData<List<AlphabatValue>> alphabatList;
    private LiveData<List<Favourite>> favourites;
    private MasterDatabase db;

    public MainActivityVM(@NonNull Application application) {
        super(application);
        db=MasterDatabase.getInstance(getApplication());
        alphabatList=db.getAlphabatValueDao().getAlphabatValues();
        favourites=db.getFavouriteDao().getAllFavourites();
    }

    public LiveData<List<AlphabatValue>> getAlphabatList() {
        return alphabatList;
    }

    public LiveData<List<Favourite>> getFavourites() {
        return favourites;
    }

    public void addDefaultAlphabets(){

        Log.d(TAG, "addDefaultAlphabets: ");
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Long> count;
                count=db.getAlphabatValueDao().addAllAlphabats(getDefaultValues());
                Log.d(TAG, "ADDED THE DEFAULT DATA: "+Integer.toString(count.size())+" Rows added");
            }
        }).start();

    }

    public void resetAlphabetValues(){

        final List<AlphabatValue> updatingList=alphabatList.getValue();
        for(AlphabatValue val:updatingList){
            val.setCurrentValue(val.getDefaultValue());
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                db.getAlphabatValueDao().resetAlphabatValues(updatingList);
            }
        }).start();


    }

    public void addFavourite(final Favourite fav){

        new Thread(new Runnable() {
            @Override
            public void run() {
                db.getFavouriteDao().addFavourite(fav);
            }
        }).start();

    }

    public  void clearFavourites(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                db.getFavouriteDao().deleteAllFavourites();
            }
        }).start();
    }

    private List<AlphabatValue> getDefaultValues(){

        List<AlphabatValue> list=new ArrayList<>();
        list.add(new AlphabatValue(1)); //A
        list.add(new AlphabatValue(2)); //B
        list.add(new AlphabatValue(3)); //C
        list.add(new AlphabatValue(4)); //D
        list.add(new AlphabatValue(5)); //E
        list.add(new AlphabatValue(8)); //F
        list.add(new AlphabatValue(3)); //G
        list.add(new AlphabatValue(5)); //H
        list.add(new AlphabatValue(1)); //I
        list.add(new AlphabatValue(1)); //J
        list.add(new AlphabatValue(2)); //K
        list.add(new AlphabatValue(3)); //L
        list.add(new AlphabatValue(4)); //M
        list.add(new AlphabatValue(5)); //N
        list.add(new AlphabatValue(7)); //O
        list.add(new AlphabatValue(8)); //P
        list.add(new AlphabatValue(1)); //Q
        list.add(new AlphabatValue(2)); //R
        list.add(new AlphabatValue(3)); //S
        list.add(new AlphabatValue(4)); //T
        list.add(new AlphabatValue(6)); //U
        list.add(new AlphabatValue(6)); //V
        list.add(new AlphabatValue(6)); //W
        list.add(new AlphabatValue(5)); //X
        list.add(new AlphabatValue(1)); //Y
        list.add(new AlphabatValue(7)); //Z

        return list;

    }
}
